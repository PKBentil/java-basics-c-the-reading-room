package readingroom.exceptions;

public class ItemAlreadyOnLoanException extends LibraryException{
    public ItemAlreadyOnLoanException(String title){
        super("Item is already on loan: " + title);
    }
}
