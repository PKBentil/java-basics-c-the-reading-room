package readingroom;

public abstract class   LendingItem {

    private static int nextId = 1;

    private final int id;
    private final String title;

    protected LendingItem(String title) {
        this.id = nextId++;
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