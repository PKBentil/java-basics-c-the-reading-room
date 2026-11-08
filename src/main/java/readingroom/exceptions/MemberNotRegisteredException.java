package readingroom.exceptions;

public class MemberNotRegisteredException extends LibraryException {
    public MemberNotRegisteredException(String memberName) {
        super("Member is not registered: " + memberName);
    }
}
