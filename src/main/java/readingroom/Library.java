package readingroom;

import readingroom.exceptions.ItemAlreadyOnLoanException;
import readingroom.exceptions.ItemNotRegisteredException;
import readingroom.exceptions.MemberNotRegisteredException;
import readingroom.exceptions.NoActiveLoanException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


public class Library {

    private final String name;
    private final Map<Integer, LendingItem> items = new LinkedHashMap<>();
    private final Map<Integer, Member> members = new LinkedHashMap<>();
    private final Map<Integer, Loan> activeLoansByItemId = new HashMap<>();
    private final List<Loan> loans = new ArrayList<>();

    public Library(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addItem(LendingItem item) {
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null");
        }
        items.put(item.getId(), item);
    }

    public void addMember(Member member) {
        if (member == null) {
            throw new IllegalArgumentException("Member cannot be null");
        }
        members.put(member.getId(), member);
    }

    public boolean isRegistered(Member member) {
        return member != null && members.containsKey(member.getId());
    }

    public boolean isRegistered(LendingItem item) {
        return item != null && items.containsKey(item.getId());
    }

    public Loan borrow(Member member, LendingItem item) {
        if (!isRegistered(member)) {
            throw new MemberNotRegisteredException(member == null ? "null" : member.getName());
        }

        if (!isRegistered(item)) {
            throw new ItemNotRegisteredException(item == null ? "null" : item.getTitle());
        }

        if (activeLoansByItemId.containsKey(item.getId())) {
            throw new ItemAlreadyOnLoanException(item.getTitle());
        }

        Loan loan = new Loan(member, item, item.calculateLoanDays(member));
        loans.add(loan);
        activeLoansByItemId.put(item.getId(), loan);
        member.recordBorrowing(item.getTitle());

        return loan;
    }

    public Loan returnItem(LendingItem item) {
        if (!isRegistered(item)) {
            throw new ItemNotRegisteredException(item == null ? "null" : item.getTitle());
        }

        Loan loan = activeLoansByItemId.remove(item.getId());
        if (loan == null) {
            throw new NoActiveLoanException(item.getTitle());
        }

        loan.markReturned();
        return loan;
    }

    public Optional<Loan> findActiveLoan(LendingItem item) {
        return Optional.ofNullable(activeLoansByItemId.get(item.getId()));
    }

    public Collection<LendingItem> getItems() {
        return Collections.unmodifiableCollection(items.values());
    }

    public List<Loan> getLoans() {
        return Collections.unmodifiableList(loans);
    }

    public List<Loan> getActiveLoans() {
        return loans.stream()
                .filter(loan -> activeLoansByItemId.get(loan.getItem().getId()) == loan)
                .toList();
    }
}