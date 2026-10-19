package splitseven.model;

import java.util.UUID;

public record Member(String id, String name) {
    public static Member create(String name) {
        return new Member(UUID.randomUUID().toString(), name);
    }
}
