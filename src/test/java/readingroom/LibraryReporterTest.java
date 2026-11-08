package readingroom;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import readingroom.enums.MemberType;
import readingroom.exceptions.MemberNotRegisteredException;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LibraryReporterTest {

    private ByteArrayOutputStream captured;
    private LibraryReporter reporter;
    private Library library;
    private LendingItem novel;
    private Member kofi;

    @BeforeEach
    void setUp() {
        captured = new ByteArrayOutputStream();
        reporter = new LibraryReporter(new PrintStream(captured, true, StandardCharsets.UTF_8));

        library = new Library("Test Library");
        novel = new Book("Things Fall Apart");
        kofi = new Member("Kofi Mensah", MemberType.STANDARD);
        library.addItem(novel);
        library.addMember(kofi);
    }

    private String output() {
        return captured.toString(StandardCharsets.UTF_8);
    }

    @Test
    void reportShowsAvailableItem() {
        reporter.printReport(library);

        assertTrue(output().contains("\"Things Fall Apart\" (BOOK): AVAILABLE"));
    }

    @Test
    void reportShowsBorrowerForAnItemOnLoan() {
        library.borrow(kofi, novel);

        reporter.printReport(library);

        assertTrue(output().contains("ON_LOAN - with Kofi Mensah"));
        assertTrue(output().contains("\"Things Fall Apart\" -> Kofi Mensah"));
    }

    @Test
    void reportOmitsReturnedLoansFromActiveLoans() {
        library.borrow(kofi, novel);
        library.returnItem(novel);

        reporter.printReport(library);

        assertFalse(output().contains("->"));
    }

    @Test
    void historyShowsBorrowedItems() {
        library.borrow(kofi, novel);

        reporter.printMemberHistory(library, kofi);

        assertTrue(output().contains("Borrowed Things Fall Apart"));
    }

    @Test
    void historyForAMemberWithNoBorrowingSaysSo() {
        reporter.printMemberHistory(library, kofi);

        assertTrue(output().contains("No borrowing history"));
    }

    @Test
    void historyForAnUnregisteredMemberIsRejected() {
        Member outsider = new Member("Kwame Asante", MemberType.STANDARD);

        assertThrows(MemberNotRegisteredException.class,
                () -> reporter.printMemberHistory(library, outsider));
    }
}
