package model;

import java.time.Instant;

public class Member {
    final long id;
    final long creationTime;
    String name;

    Member(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        this.id = Instant.now().toEpochMilli();
        this.creationTime = Instant.now().toEpochMilli();
        this.name = name;
    }
}
