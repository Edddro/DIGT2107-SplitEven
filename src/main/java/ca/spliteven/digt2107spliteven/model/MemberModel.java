package ca.spliteven.digt2107spliteven.model;

import java.time.Instant;

public class MemberModel {
    private final long id;
    private final long creationTime;
    private String name;

    public MemberModel(String name) {
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

    /** Shown wherever a member appears in a list or a drop-down. */
    @Override
    public String toString() {
        return name;
    }
}
