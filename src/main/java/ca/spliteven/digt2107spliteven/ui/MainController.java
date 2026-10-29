package ca.spliteven.digt2107spliteven.ui;

import ca.spliteven.digt2107spliteven.model.GroupModel;
import ca.spliteven.digt2107spliteven.service.GroupService;

public class MainController {

    private final GroupService groupService;
    private final Navigator navigator;

    public MainController(GroupService groupService, MainView view, Navigator navigator) {
        groupService.addObserver(view);
        this.groupService = groupService;
        this.navigator = navigator;

        view.getAddGroup().setOnAction(event -> {
            // TODO open CreateGroupView to create a group.
            navigator.showAddGroup();
        });

        view.getAddExpense().setOnAction(event -> {
            final GroupModel group = view.getSelectedGroup();
            if (group != null) {
                navigator.showAddExpense(group);
            }
        });
    }



}
