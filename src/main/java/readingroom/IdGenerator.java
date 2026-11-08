package readingroom;

import java.util.concurrent.atomic.AtomicInteger;

final class IdGenerator {

    private static final AtomicInteger ITEM_IDS = new AtomicInteger();
    private static final AtomicInteger LOAN_IDS = new AtomicInteger();
    private static final AtomicInteger MEMBER_IDS = new AtomicInteger();

    private IdGenerator() {
    }

    static int nextItemId() {
        return ITEM_IDS.incrementAndGet();
    }

    static int nextLoanId() {
        return LOAN_IDS.incrementAndGet();
    }

    static int nextMemberId() {
        return MEMBER_IDS.incrementAndGet();
    }
}