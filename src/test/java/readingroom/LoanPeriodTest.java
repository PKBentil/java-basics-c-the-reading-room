package readingroom;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import readingroom.enums.MemberType;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LoanPeriodTest {

    static Stream<Arguments> loanPeriods() {
        return Stream.of(
                Arguments.of(new Book("Things Fall Apart"), MemberType.STANDARD, 14),
                Arguments.of(new Book("Things Fall Apart"), MemberType.STUDENT, 28),
                Arguments.of(new AudioKit("French Audio Course"), MemberType.STANDARD, 7),
                Arguments.of(new AudioKit("French Audio Course"), MemberType.STUDENT, 14)
        );
    }

    @ParameterizedTest(name = "{1} member gets {2} days")
    @MethodSource("loanPeriods")
    void loanPeriodDependsOnItemTypeAndMemberType(LendingItem item, MemberType type, int expectedDays) {
        Member member = new Member("Test Member", type);

        assertEquals(expectedDays, item.calculateLoanDays(member));
    }
}
