package readingroom;

import readingroom.enums.MemberType;

public class Book extends LendingItem{

    public Book(String title){
        super(title);
    }
    @Override
    public int calculateLoanDays(Member member){
        return member.getType() == MemberType.STUDENT ? 28 : 14;
    }

    @Override
    public String getTypeName() {
        return "Book";
    }


}
