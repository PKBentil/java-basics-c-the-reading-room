package readingroom.exceptions;

public class NoActiveLoanException extends LibraryException {

    public NoActiveLoanException (String title) {
        super("No active loan exists for: " + title);
    }
}
