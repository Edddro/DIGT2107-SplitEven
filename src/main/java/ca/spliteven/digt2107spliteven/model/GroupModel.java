package ca.spliteven.digt2107spliteven.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Used to create a group that holds Member Expenses.*/
public class GroupModel {

    private final long id;
    private final long creationTime;
    private String name;
    private final List<MemberModel> members = new ArrayList<>();

    public GroupModel(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        this.id = Instant.now().toEpochMilli();
        this.creationTime = Instant.now().toEpochMilli();
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<MemberModel> getMembers() {
        return members;
    }

    void addMember(MemberModel member) {
        this.members.add(member);
    }

}
