package ca.spliteven.digt2107spliteven.ui;

import ca.spliteven.digt2107spliteven.model.GroupModel;
import ca.spliteven.digt2107spliteven.model.MemberModel;
import ca.spliteven.digt2107spliteven.service.GroupService;
import javafx.scene.control.Alert;

import java.io.UncheckedIOException;

public class CreateGroupController {

    private final GroupService groupService;
    private GroupModel groupModel = null;

    public CreateGroupController(GroupService groupService, CreateGroupView view, Navigator navigator) {
        this.groupService = groupService;
        view.getCancelButton().setOnAction(event -> {
            navigator.showMain();
        });
        view.getSaveButton().setOnAction(event -> {
            System.out.printf("event: %s \n", event);
            groupModel = groupModel == null
                    ? new GroupModel(view.getGroupNameField().getText())
                    : groupModel;
            for (String member: view.getPendingMemberNames()) {
                groupModel.addMember(new MemberModel(member));
            }
            onGroupAdded(groupModel);
            navigator.showMain();
        });
        view.getAddMemberButton().setOnAction(event -> {
            // Add's a member when Enter key is pressed. Good for accessibility.
            view.addPendingMember();
        });
        view.getMemberNameField().setOnAction(event -> {
            String t = event.getTarget().toString();
            System.out.printf("event name: %s \n", t);
            view.addPendingMember();
        });
    }

    void onGroupAdded(GroupModel group) {
        try {
            groupService.addGroup(group);
        } catch (UncheckedIOException error) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Could not save group");
            alert.setHeaderText("The group was not saved");
            alert.setContentText(error.getCause().getMessage());
            alert.showAndWait();
        }
    }

}
