package readingroom;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import readingroom.enums.LoanStatus;
import readingroom.enums.MemberType;
import readingroom.exceptions.ItemAlreadyOnLoanException;
import readingroom.exceptions.ItemNotRegisteredException;
import readingroom.exceptions.MemberNotRegisteredException;
import readingroom.exceptions.NoActiveLoanException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LibraryTest {

    private Library library;
    private LendingItem novel;
    private Member kofi;
    private Member ama;

    @BeforeEach
    void setUp() {
        library = new Library("Test Library");
        novel = new Book("Things Fall Apart");
        kofi = new Member("Kofi Mensah", MemberType.STANDARD);
        ama = new Member("Ama Owusu", MemberType.STUDENT);

        library.addItem(novel);
        library.addMember(kofi);
        library.addMember(ama);
    }

    @Test
    void borrowCreatesActiveLoanAndRecordsHistory() {
        Loan loan = library.borrow(kofi, novel);

        assertEquals(LoanStatus.ACTIVE, loan.getStatus());
        assertEquals(kofi, loan.getMember());
        assertEquals(novel, loan.getItem());
        assertEquals(1, library.getLoans().size());
        assertEquals(1, kofi.getHistory().size());
    }

    @Test
    void borrowingAnItemAlreadyOnLoanIsRejected() {
        library.borrow(kofi, novel);

        assertThrows(ItemAlreadyOnLoanException.class, () -> library.borrow(ama, novel));
    }

    @Test
    void rejectedDoubleBorrowLeavesExistingLoanUntouched() {
        Loan original = library.borrow(kofi, novel);

        assertThrows(ItemAlreadyOnLoanException.class, () -> library.borrow(ama, novel));

        assertEquals(1, library.getLoans().size());
        assertEquals(kofi, library.findActiveLoan(novel).orElseThrow().getMember());
        assertEquals(LoanStatus.ACTIVE, original.getStatus());
        assertTrue(ama.getHistory().isEmpty());
    }

    @Test
    void borrowingByAnUnregisteredMemberIsRejected() {
        Member outsider = new Member("Kwame Asante", MemberType.STANDARD);

        assertThrows(MemberNotRegisteredException.class, () -> library.borrow(outsider, novel));
        assertTrue(library.getLoans().isEmpty());
    }

    @Test
    void borrowingWithANullMemberIsRejected() {
        assertThrows(MemberNotRegisteredException.class, () -> library.borrow(null, novel));
    }

    @Test
    void borrowingAnItemOutsideTheLibraryIsRejected() {
        LendingItem external = new Book("The Pragmatic Programmer");

        assertThrows(ItemNotRegisteredException.class, () -> library.borrow(kofi, external));
        assertTrue(library.getLoans().isEmpty());
        assertTrue(kofi.getHistory().isEmpty());
    }

    @Test
    void returnedItemCanBeBorrowedAgain() {
        library.borrow(kofi, novel);
        library.returnItem(novel);

        Loan second = library.borrow(ama, novel);

        assertEquals(ama, second.getMember());
        assertEquals(2, library.getLoans().size());
        assertEquals(1, library.getActiveLoans().size());
    }

    @Test
    void returnMarksTheLoanReturned() {
        Loan loan = library.borrow(kofi, novel);

        Loan returned = library.returnItem(novel);

        assertEquals(loan, returned);
        assertEquals(LoanStatus.RETURNED, loan.getStatus());
        assertFalse(library.findActiveLoan(novel).isPresent());
    }

    @Test
    void returningAnItemWithNoActiveLoanIsRejected() {
        assertThrows(NoActiveLoanException.class, () -> library.returnItem(novel));
    }

    @Test
    void returningTheSameItemTwiceIsRejected() {
        library.borrow(kofi, novel);
        library.returnItem(novel);

        assertThrows(NoActiveLoanException.class, () -> library.returnItem(novel));
    }

    @Test
    void returningAnItemOutsideTheLibraryIsRejected() {
        LendingItem external = new Book("The Pragmatic Programmer");

        assertThrows(ItemNotRegisteredException.class, () -> library.returnItem(external));
    }

    @Test
    void loanListCannotBeModifiedFromOutside() {
        library.borrow(kofi, novel);

        assertThrows(UnsupportedOperationException.class, () -> library.getLoans().clear());
    }
}
