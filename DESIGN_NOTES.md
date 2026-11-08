# The Reading Room — Design Notes

## 1. Introduction

The rebuilt Reading Room application focuses on protecting business rules, preventing inconsistent records, and making future changes easier to implement.

The design uses object-oriented programming principles, including encapsulation, abstraction, inheritance, and polymorphism. It also separates lending rules from presentation so that behaviour can be verified by automated tests.

This document explains the major architectural decisions, the alternatives considered, and the trade-offs accepted.

## 2. Abstract Class for LendingItem

### Decision

`LendingItem` is an abstract class inherited by `Book` and `AudioKit`.

It contains shared properties:

- Unique item ID.
- Item title.

It also defines abstract methods:

- `calculateLoanDays(Member member)`
- `getTypeName()`

Each concrete item type implements these methods.

### Why this approach?

Books and audio kits share common item properties, but their loan-period rules differ.

An abstract class allows shared state and behaviour to be defined once while requiring each concrete item type to implement its specific rules.

### Alternative considered

An interface could define a contract for calculating loan periods. However, interfaces are not the best fit here because the item types also share instance state, such as their ID and title.

### Trade-off

The approach creates an inheritance relationship between lending items. This is appropriate for the current domain because books and audio kits are both types of lending items.

## 3. Enums for MemberType and LoanStatus

### Decision

The application uses two enums:

- `MemberType`: `STANDARD`, `STUDENT`.
- `LoanStatus`: `ACTIVE`, `RETURNED`.

### Why this approach?

Using unrestricted strings would allow invalid values such as `"STUDNET"` or `"BOROWED"`.

Enums restrict these values to a defined set of constants and make comparisons safer and clearer.

### Alternative considered

String constants could reduce spelling mistakes, but they would not prevent arbitrary strings from being passed into methods.

### Trade-off

When new member categories or loan states are introduced, the enums must be updated. This is acceptable because these categories are controlled parts of the business domain.

## 4. Object References in Loan

### Decision

A `Loan` stores references to a `Member` and a `LendingItem` rather than storing only their names and titles.

### Why this approach?

Names and titles are not necessarily unique. Two members may share a name, and the library may own multiple copies of a book.

Object references allow each loan to identify the exact member and item involved in the transaction.

### Alternative considered

The application could store unique IDs and look up the corresponding objects whenever necessary.

This is useful in database-backed systems, where IDs and foreign keys are commonly used to represent relationships.

### Trade-off

Object references are suitable for this small in-memory application. A persistent database implementation would normally use identifiers and foreign-key relationships instead.

## 5. Loan Records as the Single Source of Truth

### Decision

The application uses loan records to determine whether an item is available.

An item is considered unavailable if an active loan exists for its unique item ID.

The `LendingItem` class does not store separate `status` or `currentBorrower` fields.

### Why this approach?

The original application stored lending information in both the item and the loan record. This created two representations of the same business state, allowing them to disagree.

Using loan records as the single source of truth eliminates this duplication.

The library derives availability and current borrower information from active loans when generating reports.

### Implementation note

`Library` keeps every `Loan` in a list, which is the full lending history. It also keeps a map from item ID to the item's active loan. The map holds references to the same `Loan` objects as the list; it is a lookup index, not a second copy of the state. It is only changed inside `borrow` and `returnItem`, so the two cannot drift apart.

### Alternative considered

The application could maintain item status and borrower information directly on each item and update those fields whenever a loan is created or returned.

However, that would require careful synchronisation between item state and loan state.

A second alternative is to scan the loan list for an active loan every time. That needs no index but costs a full scan on every borrow and report line.

### Trade-off

The index must be maintained alongside the list. This is a small, localised cost in exchange for constant-time availability checks and an inexpensive guard against double borrowing.

## 6. Encapsulation and Controlled State Changes

### Decision

Important object fields are private, and controlled methods are used to access or change state.

Collections are also private. Methods such as `getHistory()`, `getLoans()`, `getItems()` and `getActiveLoans()` return unmodifiable or newly built views rather than exposing the internal collections for direct modification.

Methods such as `recordBorrowing()` and `markReturned()` have package-level access rather than being publicly available.

### Why this approach?

Encapsulation prevents unrelated parts of the application from directly modifying internal state and bypassing business rules.

For example, callers cannot directly change a loan's status or add arbitrary entries to a member's borrowing history. They must go through `Library`, which enforces the rules.

### Alternative considered

Public fields and mutable collection getters are simpler to write, but they expose internal implementation details and make the application harder to protect.

### Trade-off

More controlled methods are required, and package-level access means tests of those methods live in the same package. The resulting application is easier to maintain and reason about.

## 7. Validation and Failure Signalling

### Decision

The application validates important constructor inputs and borrowing preconditions.

The library checks that:

- The member is registered.
- The item belongs to the library.
- The item does not already have an active loan.

Only after these checks does it create a new loan and update member history.

When a rule is broken, `Library` throws a dedicated exception rather than printing a message and returning:

- `MemberNotRegisteredException`
- `ItemNotRegisteredException`
- `ItemAlreadyOnLoanException`
- `NoActiveLoanException`

They share a base class, `LibraryException`, so a caller can handle all lending failures in one place. Returning an already returned loan throws `IllegalStateException`, because that is a misuse of `Loan` rather than a library business rule.

### Why this approach?

Rejecting invalid operations before changing state prevents partial or inconsistent updates.

Throwing exceptions makes failure visible to the caller. The original approach, printing and returning, gave callers no way to know a request had failed and made failures impossible to test.

### Alternative considered

A result type, such as a `LendingResult` with a success flag and message, would also make failure explicit without exceptions. Checked exceptions would force every caller to handle failures.

### Trade-off

The exceptions are unchecked, so the compiler does not force callers to handle them. This is accepted because these are expected business outcomes that the entry point handles in one place, and unchecked exceptions keep method signatures simple. The cost is several small exception classes.

## 8. Polymorphism and Future Extension

### Decision

The library does not need to know the loan-period rules for each concrete item type.

It delegates the calculation to:

```java
item.calculateLoanDays(member);
```

The actual implementation depends on whether the item is a `Book`, `AudioKit`, or another concrete subtype.

### Why this approach?

If the library introduces an electronic loan type, an `ELoan` class can extend `LendingItem` and implement its own loan-period calculation.

The central borrowing workflow can remain unchanged.

### Trade-off

Each new item type must implement the abstract methods required by `LendingItem`. This provides consistency across all lending item types. The item also has to know about `Member`; if rules grew more complex, a separate loan-policy object would be a better home for them.

## 9. Separating Rules from Presentation

### Decision

`Library` contains lending rules only and never prints. Output is handled by two classes that write to a `PrintStream`:

- `LibraryReporter` prints the end-of-day report and a member's borrowing history.
- `LibraryDemo` runs a scripted walkthrough and prints the outcome of each scenario, catching `LibraryException` where a rule is broken.

`LendingTracker.main` only starts the demo.

### Why this approach?

Mixing validation, business rules and console output in one method makes the rules hard to test and hard to reuse. Because the reporter accepts any `PrintStream`, tests can capture its output in memory, and the same rules could later be used with a different interface.

### Additional improvement

The member borrowing-history report is an addition beyond the core rebuild. The original application kept history but gave no convenient way to inspect an individual member's records.

### Trade-off

There are more classes than a single-file script. The history itself is still stored as strings; a more advanced system could use dedicated transaction records with dates and return information.

## 10. Identity and Lookup

### Decision

Items and members are registered in maps keyed by ID, and all lookups use the ID rather than object identity. IDs come from a single `IdGenerator` class that uses thread-safe counters, one each for items, members and loans.

### Why this approach?

The original design used separate static counters in three classes, which duplicated the idea and was not thread-safe. Centralising generation removes the duplication, and atomic counters prevent duplicate IDs under concurrent use. ID-based lookup avoids relying on `equals`, and maps replace linear `contains` searches over lists.

### Alternative considered

Implementing `equals` and `hashCode` on each class, or injecting an ID generator into each object.

### Trade-off

`IdGenerator` is still global static state, which is acceptable for a small in-memory application. Injection would be preferable if IDs needed to be controlled in tests or stored in a database. `Library` itself is not synchronised, so concurrent borrowing would need further work.

## 11. Testing Strategy

### Decision

Behaviour is verified with JUnit 5 tests in `src/test/java/readingroom`:

- `LibraryTest` covers borrowing, returning, rejection cases and the guarantee that a rejected borrow leaves existing loans untouched.
- `LoanTest` covers loan validation and double returns.
- `LoanPeriodTest` is parameterized across every item type and member type.
- `LibraryReporterTest` and `LibraryDemoTest` check output captured from a `PrintStream`.

### Why this approach?

The critical rule, that an item can only be on one active loan at a time, needs a test that fails if the rule is broken. A scripted `main` only shows output and asserts nothing.

### Trade-off

The tests are small and fast but cover the in-memory rules only. There is no persistence layer to test.

## 12. Conclusion

The design prioritises data integrity, encapsulation, clear responsibilities, and extensibility without introducing unnecessary complexity.

The use of an abstract `LendingItem` class, enums, object references, controlled state changes, loan records as the single source of truth, exceptions for failures, and a separate presentation layer addresses the major risks identified in the original application.

The result preserves the original legitimate lending behaviour while making invalid operations harder to perform, behaviour verifiable by tests, and future extensions easier to implement.