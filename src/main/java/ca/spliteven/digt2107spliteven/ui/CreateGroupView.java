package ca.spliteven.digt2107spliteven.ui;

import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import java.util.List;

public class CreateGroupView {

    private final BorderPane root;
    private final Button cancelButton = new Button("Cancel");
    private final Button saveButton = new Button("Save");
    private final TextField groupNameField = new TextField();
    private final GridPane groupForm = new GridPane();
    private final Button addMemberButton = new Button("Add Member");
    private final TextField memberNameField = new TextField();
    private final ListView<String> pendingMembers = new ListView<>();
    private final Label memberError = errorLabel();

    public CreateGroupView() {
        this.root = new BorderPane();
        cancelButton.setCancelButton(true); // accessibility + keyboard
        final ToolBar toolBar = new ToolBar(cancelButton, saveButton);
        final Label groupNameLabel = new Label("Group Name");
        final Label memberNameLabel = new Label("Member Name");

        groupNameLabel.setLabelFor(groupNameField);
        memberNameLabel.setLabelFor(memberNameField);
        groupForm.add(groupNameLabel, 0 , 0);
        groupForm.add(groupNameField, 1, 0);

        groupForm.add(memberNameLabel, 0 , 1);
        groupForm.add(memberNameField, 1, 1);
        groupForm.add(addMemberButton, 2, 1);
        groupForm.add(pendingMembers, 0, groupForm.getRowCount());
        root.setBottom(toolBar);
        root.setCenter(groupForm);
    }

    public void addPendingMember() {
        String name = memberNameField.getText().strip();
        if (name.isEmpty()) {                       // quick feedback only; the service is the real check
            memberError.setText("Enter a name");
            return;
        }
        pendingMembers.getItems().add(name);
        memberNameField.clear();
        memberError.setText("");
        memberNameField.requestFocus();             // ready for the next name (NFR-006)
    }

    public List<String> getPendingMemberNames() {
        return pendingMembers.getItems();
    }


    public BorderPane getRoot() {
        return root;
    }

    public Button getCancelButton() {
        return cancelButton;
    }

    public Button getSaveButton() {
        return saveButton;
    }

    public TextField getGroupNameField() {
        return groupNameField;
    }

    public Button getAddMemberButton() {
        return addMemberButton;
    }

    public TextField getMemberNameField() {
        return memberNameField;
    }

    private static Label errorLabel() {
        Label l = new Label();
        l.getStyleClass().add("field-error");
        return l;
    }

}
