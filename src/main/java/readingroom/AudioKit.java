package readingroom;

import readingroom.enums.MemberType;

public class AudioKit extends LendingItem{

    public AudioKit(String title){
        super(title);
    }

    @Override
    public int calculateLoanDays(Member member){
        return member.getType() == MemberType.STUDENT ? 14 : 7;
    }

    @Override
    public String getTypeName(){
        return "AUDIO_KIT";
    }
}
