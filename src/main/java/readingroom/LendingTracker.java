
package readingroom;

import readingroom.enums.MemberType;

public class LendingTracker {

    public static void main(String[] args) {

        Library lib = new Library("The Reading Room");

        LendingItem novel = new Book("Things Fall Apart");
        LendingItem audioCourse = new AudioKit("French Audio Course");

        lib.addItem(novel);
        lib.addItem(audioCourse);

        Member kofi = new Member("Kofi Mensah", MemberType.STANDARD);
        Member ama = new Member("Ama Owusu", MemberType.STUDENT);

        lib.addMember(kofi);
        lib.addMember(ama);

        // A normal afternoon
        lib.borrow(kofi, novel);
        lib.borrow(ama, audioCourse);
        lib.returnItem(audioCourse);

        // Attempt to borrow an item that is already on loan
        lib.borrow(ama, novel);

        lib.printReport();
    }
}
