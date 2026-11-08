# The Reading Room — Community Library Lending Tracker

A Java 21 application for managing a community library's lending operations.

This project is a rebuild of the original Java Basics applied lab starter application. The goal was to preserve its legitimate behaviour while improving its structure, enforcing business rules, and making future changes safer.

## Features

- Register library items and members.
- Support physical books and audio kits.
- Apply different loan periods based on item type and member type.
- Prevent an item from being borrowed while it already has an active loan.
- Return borrowed items using their unique identities.
- Maintain member borrowing history.
- Generate an end-of-day report showing item availability and active loans.
- Reject invalid operations with domain exceptions instead of printing and continuing.
- Display individual member borrowing histories.

## Technology

- Java 21
- Maven
- JUnit 5 (test scope only)

## Project Structure

```text
src/main/java/readingroom/
├── AudioKit.java           # audio kit loan rules (7 / 14 days)
├── Book.java               # book loan rules (14 / 28 days)
├── IdGenerator.java        # thread-safe id sequences for items, members and loans
├── LendingItem.java        # abstract parent for lendable items
├── LendingTracker.java     # thin entry point; delegates to LibraryDemo
├── Library.java            # lending rules; throws exceptions, never prints
├── LibraryDemo.java        # console walkthrough of the lending scenarios
├── LibraryReporter.java    # presentation: end-of-day report and member history
├── Loan.java               # one borrowing transaction
├── Member.java
├── enums/
│   ├── LoanStatus.java
│   └── MemberType.java
└── exceptions/
    ├── LibraryException.java             # base type for business-rule failures
    ├── ItemAlreadyOnLoanException.java
    ├── ItemNotRegisteredException.java
    ├── MemberNotRegisteredException.java
    └── NoActiveLoanException.java

src/test/java/readingroom/
├── LibraryTest.java          # borrowing and returning rules
├── LoanTest.java             # loan validation and double-return
├── LoanPeriodTest.java       # parameterized item type x member type loan days
├── LibraryReporterTest.java  # report and history output
└── LibraryDemoTest.java      # demo scenario outcomes
```

## Loan Periods

| Item | Standard Member | Student |
|---|---:|---:|
| Book | 14 days | 28 days |
| Audio Kit | 7 days | 14 days |

## Design Overview

- **Encapsulation:** Object state is private, with controlled access through methods.
- **Abstraction and polymorphism:** `LendingItem` defines shared item behaviour, while `Book` and `AudioKit` implement their own loan-period rules.
- **Enums:** `MemberType` and `LoanStatus` restrict values to defined categories.
- **Single source of truth:** Loan records determine item availability. The application derives availability and current borrower information from active loans.
- **Unique item identity:** Each loan references a specific item, and the library looks items and members up by id.
- **Failures are signalled, not printed:** `Library` throws a `LibraryException` subtype when a rule is broken; callers decide how to present it.
- **Presentation is separate:** `LibraryReporter` and `LibraryDemo` write to a `PrintStream`, so output can be redirected or tested.
- **Defensive collection access:** Internal collections are not directly exposed for modification.

## Running the Application

Requirements:
- JDK 21
- Maven

Build and run the tests:

```bash
mvn clean test
```

Run the demo:

```bash
mvn compile exec:java
```

### Expected output

```text
Cannot lend "Things Fall Apart": Member is not registered: Kwame Asante
Cannot lend "The Pragmatic Programmer": Item does not belong to this library: The Pragmatic Programmer
Lent "Things Fall Apart" to Kofi Mensah for 14 days
Lent "French Audio Course" to Ama Owusu for 14 days
Returned "French Audio Course"
Lent "French Audio Course" to Kofi Mensah for 7 days
Cannot lend "Things Fall Apart": Item is already on loan: Things Fall Apart

=== The Reading Room - end of day ===
Items:
  "Things Fall Apart" (BOOK): ON_LOAN - with Kofi Mensah
  "French Audio Course" (AUDIO_KIT): ON_LOAN - with Kofi Mensah
Active loans:
  Loan #1: "Things Fall Apart" -> Kofi Mensah
  Loan #3: "French Audio Course" -> Kofi Mensah

=== Borrowing history: Kofi Mensah ===
  Borrowed Things Fall Apart
  Borrowed French Audio Course

=== Borrowing history: Ama Owusu ===
  Borrowed French Audio Course
```

## Tests

The test suite covers:

1. Rejection of an unregistered or null member.
2. Rejection of an item not registered with the library.
3. Rejection of a second loan for an item already on loan, with the original loan left untouched.
4. Re-borrowing an item after it has been returned.
5. Rejection of returns with no active loan, and of double returns.
6. Loan periods for every item type and member type combination.
7. End-of-day report and member-history output.

## Applied Lab Deliverables

- `DIAGNOSIS.md` — analysis of the original implementation and its business risks.
- `DESIGN_NOTES.md` — architectural decisions, reasoning and trade-offs.
- Rebuilt Java application and test suite — implementation of the proposed design.