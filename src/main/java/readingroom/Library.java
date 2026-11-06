
package readingroom;

import readingroom.enums.LoanStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Library {

    private final String name;
    private final List<LendingItem> items = new ArrayList<>();
    private final List<Member> members = new ArrayList<>();
    private final List<Loan> loans = new ArrayList<>();

    public Library(String name) {
        this.name = name;
    }

    public void addItem(LendingItem item) {
        items.add(item);
    }

    public void addMember(Member member) {
        members.add(member);
    }

    public void borrow(Member member, LendingItem item) {

        boolean alreadyOnLoan = loans.stream()
                .anyMatch(loan ->
                        loan.getItem().getId() == item.getId()
                                && loan.getStatus() == LoanStatus.ACTIVE);

        if (alreadyOnLoan) {
            System.out.println(
                    "Cannot lend \"" + item.getTitle()
                            + "\": item is already on loan."
            );
            return;
        }

        int loanDays = item.calculateLoanDays(member);

        Loan loan = new Loan(member, item, loanDays);
        loans.add(loan);

        member.recordBorrowing(item.getTitle());

        System.out.println(
                "Lent \"" + item.getTitle() + "\" to "
                        + member.getName() + " for " + loanDays + " days"
        );
    }

    public void returnItem(LendingItem item) {

        for (Loan loan : loans) {
            if (loan.getItem().getId() == item.getId()
                    && loan.getStatus() == LoanStatus.ACTIVE) {

                loan.markReturned();

                System.out.println(
                        "Returned \"" + item.getTitle() + "\""
                );
                return;
            }
        }

        System.out.println(
                "Cannot return \"" + item.getTitle()
                        + "\": no active loan exists."
        );
    }

    public List<Loan> getLoans() {
        return Collections.unmodifiableList(loans);
    }

    public void printReport() {

        System.out.println();
        System.out.println("=== " + name + " - end of day ===");
        System.out.println("Items:");

        for (LendingItem item : items) {

            Loan activeLoan = loans.stream()
                    .filter(loan ->
                            loan.getItem().getId() == item.getId()
                                    && loan.getStatus() == LoanStatus.ACTIVE)
                    .findFirst()
                    .orElse(null);

            String status = activeLoan == null
                    ? "AVAILABLE"
                    : "ON_LOAN";

            System.out.println(
                    "  \"" + item.getTitle() + "\" ("
                            + item.getTypeName() + "): " + status
                            + (activeLoan != null
                            ? " - with " + activeLoan.getMember().getName()
                            : "")
            );
        }

        System.out.println("Active loans:");

        for (Loan loan : loans) {
            if (loan.getStatus() == LoanStatus.ACTIVE) {
                System.out.println(
                        "  Loan #" + loan.getId() + ": \""
                                + loan.getItem().getTitle()
                                + "\" -> " + loan.getMember().getName()
                );
            }
        }
    }
}
