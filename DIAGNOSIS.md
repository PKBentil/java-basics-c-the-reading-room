# The Reading Room — Diagnosis and Proposed Design

## 1. Current System Overview

The Reading Room is a small library lending tracker. It currently supports:

- Physical books and audio kits.
- Standard and student members.
- Different loan periods based on item and member type.
- Borrowing and returning items.
- Tracking member borrowing history.
- An end-of-day report showing items and active loans.

The application runs successfully, but its current structure makes changes risky and allows inconsistent lending records.

## 2. Diagnosis of Existing Problems

### 2.1 Critical — The same item can be borrowed more than once

**Root cause:** `Library.borrow()` does not check whether an item is already on loan before creating another loan. It simply changes the item's status and current borrower.

**Evidence:** In the original run, Kofi borrows *Things Fall Apart*. Ama then borrows the same item. The report shows Ama as the current borrower, but the active-loan list still contains loans for both Kofi and Ama.

**Business risk:** The library may promise an item to multiple members, lose track of who has it, and be unable to manage returns reliably.

**Required decision:** Reject a borrowing request if the item already has an active loan. The existing loan and item state must remain unchanged.

### 2.2 High — Returns identify items by title instead of unique identity

**Root cause:** `Loan` stores the item's title, and `Library.returnItem()` uses that title to find active loans.

**Business risk:** If the library has multiple copies of a book with the same title, returning one copy could incorrectly mark loans for other copies as returned.

**Required decision:** Associate every loan with the actual item identity, preferably through its unique ID or object reference.

### 2.3 High — Important object fields are publicly mutable

**Root cause:** Classes such as `LendingItem`, `Member`, and `Loan` expose fields publicly. Other classes can directly change item status, borrower details, member information, or loan status.

**Business risk:** A caller could mark an item as available while it is still on loan, change a borrower's details, or alter a loan's status without following the library's rules.

**Required decision:** Make state private and expose controlled methods or read-only accessors. State changes should happen through operations that enforce business rules.

### 2.4 High — Internal collections can be modified from outside

**Root cause:** `Library.items`, `Library.members`, and `Member.history` are public lists. `Library.getLoans()` also returns the actual internal loan list.

**Business risk:** Other parts of the program can add, remove, or alter records without the library controlling those changes. This can make reports and lending operations unreliable.

**Required decision:** Keep collections private and expose read-only views or safe access methods.

### 2.5 Medium — Loan rules are tightly coupled to the borrowing method

**Root cause:** `Library.borrow()` contains conditional logic for every supported item type and member type.

**Business risk:** Adding another item type, such as an e-loan, could require changing an already working borrowing method. Repeated changes to this method increase the chance of breaking existing rules.

**Required decision:** Move item-specific loan-period behaviour out of the central borrowing workflow. Use polymorphism or another small, well-defined policy abstraction so new item types can provide their own rules.

### 2.6 Medium — Item and loan state can disagree

**Root cause:** The application stores lending information in two places: the item's `status` and `currentBorrower`, and the loan's `status`, `memberName`, and `itemTitle`.

**Business risk:** These separate values can become inconsistent. The original run already demonstrates this: the item says Ama has it, while the active-loan records say both Kofi and Ama have it.

**Required decision:** Use loan records as the single source of truth. An item is considered on loan if an active loan exists for its unique item ID. Availability and current borrower information should be derived from the active loan rather than stored separately on the item.

### 2.7 Medium — Invalid operations and unsupported values are not rejected

**Root cause:** `returnItem()` does not verify that an item has an active loan before returning it. The borrowing method also does not explicitly reject unsupported item types or invalid member/item inputs.

**Business risk:** The application can report successful operations that do not represent valid lending activity. Unsupported item types may even produce a loan with zero days.

**Required decision:** Validate inputs and operation preconditions before changing records. Invalid operations should be rejected clearly without partially changing state.

## 3. Proposed Class Structure

The proposed design separates responsibilities:

- `Library`: manages registered items, members, and loan records; coordinates borrowing and returns; checks for existing active loans.
- `LendingItem`: abstract parent providing shared item identity and title.
- `Book`: provides book-specific loan-period rules.
- `AudioKit`: provides audio-kit-specific loan-period rules.
- `Member`: represents a member and maintains borrowing history.
- `MemberType`: enum for controlled member categories such as `STANDARD` and `STUDENT`.
- `Loan`: represents one borrowing transaction and associates a specific member with a specific item.
- `LoanStatus`: enum for controlled loan states, such as `ACTIVE` and `RETURNED`.
- `LendingTracker`: application entry point and demonstration.

### Proposed relationships

```text
                    LendingItem (abstract)
                     /               \
                  Book             AudioKit
                     \               /
                      registered in
                           |
                        Library
                     /     |     \
                  Items  Members  Loans
                                  |
                           +------+------+
                           |             |
                         Member      LendingItem
                           |
                       MemberType

                     LendingTracker
                           |
                        Library
```

**Source of truth:** `Library` maintains loan records. Each `Loan` identifies its specific item and member. To determine whether an item is available, the library checks for an active loan associated with that item's unique ID.

The item itself does not maintain a separate `status` or `currentBorrower` field. The end-of-day report derives those details from the loan records.

The design should allow a future `ELoan` class to provide its own borrowing rules without requiring the central borrowing workflow to be rewritten.

## 4. Behaviour That Must Be Preserved

The rebuilt application must retain the original legitimate behaviour:

1. Standard members can borrow books for 14 days.
2. Students can borrow books for 28 days.
3. Standard members can borrow audio kits for 7 days.
4. Students can borrow audio kits for 14 days.
5. Items can be returned.
6. Member borrowing history is maintained.
7. The end-of-day report displays item availability and active loans.

The intentional correction is that an item already on loan must not be lent to another member.

## 5. Priority Order

| Priority | Problem | Reason |
|---|---|---|
| 1 — Critical | Duplicate active loans | Directly compromises item ownership and lending integrity |
| 2 — High | Public mutable state and exposed collections | Allows business rules to be bypassed |
| 3 — High | Returns based on titles | Can affect the wrong physical copy |
| 4 — High | Duplicated item and loan state | Allows contradictory records |
| 5 — Medium | Tightly coupled loan-period rules | Makes future item types risky to introduce |
| 6 — Medium | Missing operation validation | Allows invalid lending and return operations |

## 6. Conclusion

The main problem is not that the application fails to run. It is that its current design cannot reliably protect the library's records or safely accommodate change.

The rebuild should preserve the existing legitimate behaviour while enforcing exclusive borrowing, encapsulating state, separating responsibilities, and making future item types easier to introduce.