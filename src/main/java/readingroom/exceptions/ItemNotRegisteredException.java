package readingroom.exceptions;

public class ItemNotRegisteredException extends LibraryException{
    public ItemNotRegisteredException(String title) {
        super("Item does not belong to this library: " + title);
    }
}
