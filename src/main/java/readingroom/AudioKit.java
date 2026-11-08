package readingroom;

import readingroom.enums.MemberType;

public class AudioKit extends LendingItem {

    private static final int STUDENT_LOAN_DAYS = 14;
    private static final int STANDARD_LOAN_DAYS = 7;

    public AudioKit(String title) {
        super(title);
    }

    @Override
    public int calculateLoanDays(Member member) {
        return member.getType() == MemberType.STUDENT ? STUDENT_LOAN_DAYS : STANDARD_LOAN_DAYS;
    }

    @Override
    public String getTypeName() {
        return "AUDIO_KIT";
    }
}