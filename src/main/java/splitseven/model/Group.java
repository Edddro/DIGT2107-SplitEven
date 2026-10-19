package splitseven.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Group {
    private final String id;
    private final String name;
    private final List<Member> members = new ArrayList<>();

    public Group(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public static Group create(String name) {
        return new Group(UUID.randomUUID().toString(), name);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Member> getMembers() {
        return members;
    }

    public void addMember(Member member) {
        members.add(member);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Group group
                && id.equals(group.id)
                && name.equals(group.name)
                && members.equals(group.members);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, members);
    }
}
