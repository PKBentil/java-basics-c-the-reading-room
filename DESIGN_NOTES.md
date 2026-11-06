# The Reading Room — Design Notes

## 1. Introduction

The rebuilt Reading Room application focuses on protecting business rules, preventing inconsistent records, and making future changes easier to implement.

The design uses object-oriented programming principles, including encapsulation, abstraction, inheritance, and polymorphism.

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

### Alternative considered

The application could maintain item status and borrower information directly on each item and update those fields whenever a loan is created or returned.

However, that would require careful synchronisation between item state and loan state.

### Trade-off

The library performs active-loan lookups when determining availability. This is a reasonable trade-off for the small in-memory application and improves consistency.

## 6. Encapsulation and Controlled State Changes

### Decision

Important object fields are private, and controlled methods are used to access or change state.

Collections are also private. Methods such as `getHistory()` and `getLoans()` return unmodifiable views rather than exposing the internal collections for direct modification.

Methods such as `recordBorrowing()` and `markReturned()` have package-level access rather than being publicly available.

### Why this approach?

Encapsulation prevents unrelated parts of the application from directly modifying internal state and bypassing business rules.

For example, callers cannot directly change a loan's status or add arbitrary entries to a member's borrowing history.

### Alternative considered

Public fields and mutable collection getters are simpler to write, but they expose internal implementation details and make the application harder to protect.

### Trade-off

More controlled methods are required, but the resulting application is easier to maintain and reason about.

## 7. Validation Before State Changes

### Decision

The application validates important constructor inputs and borrowing preconditions.

The library checks that:

- The member is registered.
- The item belongs to the library.
- The item does not already have an active loan.

Only after these checks does it create a new loan and update member history.

### Why this approach?

Rejecting invalid operations before changing state prevents partial or inconsistent updates.

For example, an unregistered member must not receive an item, and an item outside the library must not create a loan record.

### Trade-off

Validation introduces additional checks, but these checks protect the application's business rules and improve reliability.

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

Each new item type must implement the abstract methods required by `LendingItem`. This provides consistency across all lending item types.

## 9. Additional Improvement — Member Borrowing History Report

### Decision

A member borrowing-history report was added as an improvement beyond the core rebuild.

The library provides `printMemberHistory(Member member)` to display a registered member's borrowing history.

### Why this improvement?

The original application maintained borrowing history but did not provide a convenient way to inspect an individual member's records.

The additional report makes the existing history feature more useful and demonstrates how the rebuilt design supports additional functionality.

### Trade-off

The history currently records borrowing events as strings. A more advanced system could represent history using dedicated transaction records, including dates and return information.

## 10. Conclusion

The design prioritises data integrity, encapsulation, clear responsibilities, and extensibility without introducing unnecessary complexity.

The use of an abstract `LendingItem` class, enums, object references, controlled state changes, and loan records as the single source of truth addresses the major risks identified in the original application.

The result preserves the original legitimate lending behaviour while making invalid operations harder to perform and future extensions easier to implement.