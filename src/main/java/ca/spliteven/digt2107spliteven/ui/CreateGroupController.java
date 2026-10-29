package ca.spliteven.digt2107spliteven.ui;

import ca.spliteven.digt2107spliteven.model.GroupModel;
import ca.spliteven.digt2107spliteven.service.GroupService;

public class CreateGroupController {

    private final GroupService groupService;

    public CreateGroupController(GroupService groupService, CreateGroupView view, Navigator navigator) {
        this.groupService = groupService;
        view.getCancelButton().setOnAction(event -> {
            navigator.showMain();
        });
    }

    void onGroupAdded(GroupModel group) {
        groupService.addGroup(group);
    }

}
