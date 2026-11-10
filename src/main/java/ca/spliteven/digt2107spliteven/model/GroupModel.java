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

    public GroupModel(String name) {
        this(UUID.randomUUID(), name);
    }

    public GroupModel(UUID id, String name) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Group name cannot be blank");
        }
        this.id = id;
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

    public void addMember(MemberModel member) {
        this.members.add(member);
    }

}
