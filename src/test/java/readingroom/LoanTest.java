package readingroom;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import readingroom.enums.LoanStatus;
import readingroom.enums.MemberType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LoanTest {

    private Member member;
    private LendingItem item;

    @BeforeEach
    void setUp() {
        member = new Member("Kofi Mensah", MemberType.STANDARD);
        item = new Book("Things Fall Apart");
    }

    @Test
    void newLoanIsActive() {
        Loan loan = new Loan(member, item, 14);

        assertEquals(LoanStatus.ACTIVE, loan.getStatus());
        assertEquals(14, loan.getLoanDays());
    }

    @Test
    void markReturnedChangesStatus() {
        Loan loan = new Loan(member, item, 14);

        loan.markReturned();

        assertEquals(LoanStatus.RETURNED, loan.getStatus());
    }

    @Test
    void returningALoanTwiceIsRejected() {
        Loan loan = new Loan(member, item, 14);
        loan.markReturned();

        assertThrows(IllegalStateException.class, loan::markReturned);
    }

    @Test
    void loanWithoutAMemberIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Loan(null, item, 14));
    }

    @Test
    void loanWithoutAnItemIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Loan(member, null, 14));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -14})
    void loanWithANonPositiveDurationIsRejected(int days) {
        assertThrows(IllegalArgumentException.class, () -> new Loan(member, item, days));
    }
}
