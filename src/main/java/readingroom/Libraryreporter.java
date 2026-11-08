package readingroom;

import java.io.PrintStream;
import java.util.Optional;

public class LibraryReporter {

    private final PrintStream out;

    public LibraryReporter(PrintStream out) {
        this.out = out;
    }

    public void printReport(Library library) {
        out.println();
        out.println("=== " + library.getName() + " - end of day ===");
        out.println("Items:");

        for (LendingItem item : library.getItems()) {
            Optional<Loan> activeLoan = library.findActiveLoan(item);
            String status = activeLoan
                    .map(loan -> "ON_LOAN - with " + loan.getMember().getName())
                    .orElse("AVAILABLE");

            out.println("  \"" + item.getTitle() + "\" (" + item.getTypeName() + "): " + status);
        }

        out.println("Active loans:");
        for (Loan loan : library.getActiveLoans()) {
            out.println("  Loan #" + loan.getId() + ": \"" + loan.getItem().getTitle()
                    + "\" -> " + loan.getMember().getName());
        }
    }

    public void printMemberHistory(Library library, Member member) {
        if (!library.isRegistered(member)) {
            throw new MemberNotRegisteredException(member == null ? "null" : member.getName());
        }

        out.println();
        out.println("=== Borrowing history: " + member.getName() + " ===");

        if (member.getHistory().isEmpty()) {
            out.println("No borrowing history");
            return;
        }

        for (String entry : member.getHistory()) {
            out.println("  " + entry);
        }
    }
}