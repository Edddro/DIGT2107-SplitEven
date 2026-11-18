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
