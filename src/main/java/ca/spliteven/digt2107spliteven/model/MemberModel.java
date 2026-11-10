package ca.spliteven.digt2107spliteven.model;

import java.time.Instant;
import java.util.UUID;

public class MemberModel {
    private final UUID id;
    private final long creationTime;
    private String name;

    public MemberModel(String name) {
        this(UUID.randomUUID(), name);
    }

    public MemberModel(UUID id, String name) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Member name cannot be blank");
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

    /** Shown wherever a member appears in a list or a drop-down. */
    @Override
    public String toString() {
        return name;
    }

    public void updateName(String name) {
        this.name = name;
    }

}
