package ca.spliteven.digt2107spliteven.model;

import ca.spliteven.digt2107spliteven.service.GroupService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GroupModelTest {

    private GroupService groupService;

    @BeforeEach
    void setUp() {
        groupService = new GroupService();
    }

    @Test
    @DisplayName("TC-001 Create a group")
    void tc001_createGroup() {
        GroupModel group = new GroupModel("Apartment 4B");
        groupService.addGroup(group);

        List<GroupModel> groups = groupService.getGroups();

        assertEquals(1, groups.size());
        assertEquals(group, groups.get(0));
        assertNotNull(group.getId());
        assertEquals(0, group.getMembers().size());
    }

    @Test
    @DisplayName("TC-002 Blank group name is refused")
    void tc002_blankGroupNameIsRefused() {
        for (String name : new String[] {"", "   "}) {
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class, () -> new GroupModel(name));

            assertEquals("Group name cannot be blank", exception.getMessage());
            assertEquals(0, groupService.getGroups().size());
        }
    }

    @Test
    @DisplayName("TC-003 Add a member")
    void tc003_addMember() {
        GroupModel group = new GroupModel("Apartment 4B");
        groupService.addGroup(group);
        group.addMember(new MemberModel("Ana"));

        List<MemberModel> members = groupService.getGroups().get(0).getMembers();

        assertEquals(1, members.size());
        assertEquals("Ana", members.get(0).getName());
        assertNotNull(members.get(0).getId());
    }

    @Test
    @DisplayName("TC-004 Two members with the same name")
    void tc004_twoMembersWithTheSameName() {
        GroupModel group = new GroupModel("Apartment 4B");
        group.addMember(new MemberModel("Ana"));
        groupService.addGroup(group);
        group.addMember(new MemberModel("Ana"));

        List<MemberModel> members = groupService.getGroups().get(0).getMembers();

        assertEquals(2, members.size());
        assertEquals("Ana", members.get(0).getName());
        assertEquals("Ana", members.get(1).getName());
        assertNotEquals(members.get(0).getId(), members.get(1).getId());
    }

    @Test
    @DisplayName("TC-005 Blank member name is refused")
    void tc005_blankMemberNameIsRefused() {
        GroupModel group = new GroupModel("Apartment 4B");
        groupService.addGroup(group);

        for (String name : new String[] {"", "   "}) {
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class, () -> new MemberModel(name));

            assertEquals("Member name cannot be blank", exception.getMessage());
            assertEquals(0, group.getMembers().size());
        }
    }
}
