package ca.spliteven.digt2107spliteven.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Used to create a group that holds Member Expenses.*/
public class GroupModel {

    private final UUID id;
    private final long creationTime;
    private String name;
    private final List<MemberModel> members = new ArrayList<>();
    // TODO To be linked from ExpenseService:
    private final List<ExpenseModel> expenses = new ArrayList<>();

    public GroupModel(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        this.id = UUID.randomUUID();
        this.creationTime = Instant.now().toEpochMilli();
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<MemberModel> getMembers() {
        return members;
    }

    public List<ExpenseModel> getExpenses() {
        return expenses;
    }

    public void addMember(MemberModel member) {
        this.members.add(member);
    }

    public void addExpenses(ExpenseModel expenses) {
        this.expenses.add(expenses);
    }

}
