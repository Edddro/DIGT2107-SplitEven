package ca.spliteven.digt2107spliteven.ui;

import ca.spliteven.digt2107spliteven.model.GroupModel;
import ca.spliteven.digt2107spliteven.service.GroupService;
import javafx.scene.control.Alert;

import java.io.UncheckedIOException;

public class CreateGroupController {

    private final GroupService groupService;

    public CreateGroupController(GroupService groupService, CreateGroupView view, Navigator navigator) {
        this.groupService = groupService;
        view.getCancelButton().setOnAction(event -> {
            navigator.showMain();
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
