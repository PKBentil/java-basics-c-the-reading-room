package readingroom;

import readingroom.enums.MemberType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Member {
    private static int nextId = 1;

    private final int id;
    private final String name;
    private final MemberType type;
    private final List<String> history;

    public Member(String name, MemberType type) {
        this.id = nextId++;
        this.name = name;
        this.type = type;
        this.history = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public MemberType getType() {
        return type;
    }

    public List<String> getHistory() {
        return Collections.unmodifiableList(history);
    }

    public void recordBorrowing(String itemTitle){
        history.add("Borrowed " + itemTitle);
    }
}
