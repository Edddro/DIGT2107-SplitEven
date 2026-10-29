package ca.spliteven.digt2107spliteven.ui;

import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;

public class CreateGroupView {

    private final BorderPane root;
    private final Button cancelButton = new Button("Cancel");

    public CreateGroupView() {
        this.root = new BorderPane();
        ToolBar toolBar = new ToolBar(cancelButton);
        root.setBottom(toolBar);
    }

    public BorderPane getRoot() {
        return root;
    }

    public Button getCancelButton() {
        return cancelButton;
    }
}
