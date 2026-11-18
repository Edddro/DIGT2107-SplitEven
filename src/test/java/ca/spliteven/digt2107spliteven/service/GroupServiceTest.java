package ca.spliteven.digt2107spliteven.service;

import ca.spliteven.digt2107spliteven.model.GroupModel;
import ca.spliteven.digt2107spliteven.model.MemberModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GroupServiceTest {
    private GroupService groupService;
    private GroupModel group;

    @BeforeEach
    void setUp() {
        group = new GroupModel("Apartment 4B");
        groupService = new GroupService();
    }

    @Test
    @DisplayName("TC-001 Create a group")
    void tc001_createGroup() {
        groupService.addGroup(group);

        List<GroupModel> groups = groupService.getGroups();

        assertEquals(1, groups.size());
        assertEquals(group, groups.get(0));
        assertNotNull(group.getId());
        assertEquals(0, group.getMembers().size());
    }

    @Test
    @DisplayName("TC-003 Add a member")
    void tc003_addMember() {
        groupService.addGroup(group);
        groupService.addMember(group, new MemberModel("Ana"));

        List<MemberModel> members = groupService.getGroups().get(0).getMembers();

        assertEquals(1, members.size());
        assertEquals("Ana", members.get(0).getName());
        assertNotNull(members.get(0).getId());
    }

    @Test
    @DisplayName("TC-004 Two members with the same name")
    void tc004_twoMembersWithTheSameName() {
        groupService.addGroup(group);
        groupService.addMember(group, new MemberModel("Ana"));
        groupService.addMember(group, new MemberModel("Ana"));

        List<MemberModel> members = groupService.getGroups().get(0).getMembers();

        assertEquals(2, members.size());
        assertEquals("Ana", members.get(0).getName());
        assertEquals("Ana", members.get(1).getName());
        assertNotEquals(members.get(0).getId(), members.get(1).getId());
    }
}
