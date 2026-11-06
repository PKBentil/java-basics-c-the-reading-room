
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

        //test 1
        Member outsider = new Member("Kwame Asante", MemberType.STANDARD);
        lib.borrow(outsider, novel);

        //test 2
        LendingItem externalBook = new Book("The pragmatic programmer");
        lib.borrow(kofi, externalBook);


        // A normal afternoon
        lib.borrow(kofi, novel);
        lib.borrow(ama, audioCourse);
        lib.returnItem(audioCourse);

        lib.borrow(kofi, audioCourse);

        // Attempt to borrow an item that is already on loan
        lib.borrow(ama, novel);

        lib.printReport();
        lib.printMemberHistory(kofi);
        lib.printMemberHistory(ama);
    }
}
