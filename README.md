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
- Reject borrowing attempts involving unregistered members or items outside the library.
- Display individual member borrowing histories.

## Technology

- Java 21
- Maven
- Object-oriented programming

No external application dependencies are required.

## Project Structure

```text
src/main/java/readingroom/
├── AudioKit.java
├── Book.java
├── LendingItem.java
├── LendingTracker.java
├── Library.java
├── Loan.java
├── Member.java
└── enums/
    ├── LoanStatus.java
    └── MemberType.java
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
- **Unique item identity:** Each loan references a specific item rather than identifying it by title.
- **Defensive collection access:** Internal collections are not directly exposed for modification.

## Running the Application

Requirements:
- JDK 21
- Maven

Compile:

```bash
mvn clean compile
```

Run:

```bash
mvn exec:java "-Dexec.mainClass=readingroom.LendingTracker"
```

## Demonstrated Behaviour and Checks

The application demonstrates:

1. Rejection of an unregistered member.
2. Rejection of an item not registered with the library.
3. Standard and student borrowing periods.
4. Successful borrowing and returning.
5. Re-borrowing an item after it has been returned.
6. Rejection of an attempt to borrow an item already on loan.
7. End-of-day reporting.
8. Member borrowing-history reporting.

## Applied Lab Deliverables

- `DIAGNOSIS.md` — analysis of the original implementation and its business risks.
- `DESIGN_NOTES.md` — architectural decisions, reasoning and trade-offs.
- Rebuilt Java application — implementation of the proposed design.

