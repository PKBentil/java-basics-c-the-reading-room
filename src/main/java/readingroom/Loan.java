
package readingroom;

import readingroom.enums.LoanStatus;

public class Loan {

    private static int nextId = 1;

    private final int id;
    private final Member member;
    private final LendingItem item;
    private final int loanDays;
    private LoanStatus status;

    public Loan(Member member, LendingItem item, int loanDays) {

        if (member == null) {
            throw new IllegalArgumentException("Loan member cannot be null.");
        }

        if (item == null) {
            throw new IllegalArgumentException("Loan item cannot be null");
        }

        if (loanDays <= 0) {
            throw new IllegalArgumentException("Loan duration must be positive");
        }

        this.id = nextId++;
        this.member = member;
        this.item = item;
        this.loanDays = loanDays;
        this.status = LoanStatus.ACTIVE;
    }

    public int getId() {
        return id;
    }

    public Member getMember() {
        return member;
    }

    public LendingItem getItem() {
        return item;
    }

    public int getLoanDays() {
        return loanDays;
    }

    public LoanStatus getStatus() {
        return status;
    }

     void markReturned() {
        if (status == LoanStatus.ACTIVE) {
            status = LoanStatus.RETURNED;
        }
    }
}
