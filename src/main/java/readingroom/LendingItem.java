package readingroom;

public abstract class LendingItem {

    private final int id;
    private final String title;

    protected LendingItem(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Item title cannot be empty");
        }

        this.id = IdGenerator.nextItemId();
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public abstract int calculateLoanDays(Member member);

    public abstract String getTypeName();
}