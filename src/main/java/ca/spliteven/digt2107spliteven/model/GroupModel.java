package ca.spliteven.digt2107spliteven.model;

import java.time.Instant;
import java.util.List;

/**
 * Used to create a group that holds Member Expenses.*/
public class GroupModel {
    final long id;
    final long creationTime;
    String name;
    List<MemberModel> members;

    GroupModel(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        this.id = Instant.now().toEpochMilli();
        this.creationTime = Instant.now().toEpochMilli();
        this.name = name;
    }

}
