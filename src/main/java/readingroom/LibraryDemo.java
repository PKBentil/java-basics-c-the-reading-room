package readingroom;

import readingroom.enums.MemberType;
import readingroom.exceptions.LibraryException;

import java.io.PrintStream;


public class LibraryDemo {

    private final PrintStream out;
    private final LibraryReporter reporter;

    public LibraryDemo(PrintStream out) {
        this.out = out;
        this.reporter = new LibraryReporter(out);
    }

    public void run() {
        Library library = new Library("The Reading Room");

        LendingItem novel = new Book("Things Fall Apart");
        LendingItem audioCourse = new AudioKit("French Audio Course");
        library.addItem(novel);
        library.addItem(audioCourse);

        Member kofi = new Member("Kofi Mensah", MemberType.STANDARD);
        Member ama = new Member("Ama Owusu", MemberType.STUDENT);
        library.addMember(kofi);
        library.addMember(ama);

        // Rejected: borrower was never registered with the library
        Member outsider = new Member("Kwame Asante", MemberType.STANDARD);
        lend(library, outsider, novel);

        // Rejected: item was never added to the library's collection
        LendingItem externalBook = new Book("The Pragmatic Programmer");
        lend(library, kofi, externalBook);

        // A normal afternoon: borrow, return, then borrow the returned item
        lend(library, kofi, novel);
        lend(library, ama, audioCourse);
        giveBack(library, audioCourse);
        lend(library, kofi, audioCourse);

        // Rejected: item is already on loan to someone else
        lend(library, ama, novel);

        reporter.printReport(library);
        printHistory(library, kofi);
        printHistory(library, ama);
    }

    private void lend(Library library, Member member, LendingItem item) {
        try {
            Loan loan = library.borrow(member, item);
            out.println("Lent \"" + item.getTitle() + "\" to "
                    + member.getName() + " for " + loan.getLoanDays() + " days");
        } catch (LibraryException e) {
            out.println("Cannot lend \"" + item.getTitle() + "\": " + e.getMessage());
        }
    }

    private void giveBack(Library library, LendingItem item) {
        try {
            library.returnItem(item);
            out.println("Returned \"" + item.getTitle() + "\"");
        } catch (LibraryException e) {
            out.println("Cannot return \"" + item.getTitle() + "\": " + e.getMessage());
        }
    }

    private void printHistory(Library library, Member member) {
        try {
            reporter.printMemberHistory(library, member);
        } catch (LibraryException e) {
            out.println("Cannot display history: " + e.getMessage());
        }
    }
}