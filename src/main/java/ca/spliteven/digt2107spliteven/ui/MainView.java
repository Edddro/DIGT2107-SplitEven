package ca.spliteven.digt2107spliteven.ui;

import ca.spliteven.digt2107spliteven.model.GroupModel;
import ca.spliteven.digt2107spliteven.service.GroupService;
import ca.spliteven.digt2107spliteven.service.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;

public class MainView implements Observer {

//    private final Stage stage;
    private final Button addGroup =  new Button("Add Group");
    private final BorderPane root;

    public MainView() {
//        this.stage = stage;
        final ToolBar toolBar = new ToolBar(
                addGroup
        );
        // Just testing this out:
        ObservableList<GroupModel> names = FXCollections.observableArrayList(
                new GroupModel("Test")
        );
        ListView<GroupModel> listView = createListView(names);

        final BorderPane root = new BorderPane();
        this.root = root;
        root.setTop(toolBar);
        root.setCenter(listView);

    }

    // Just testing this out:
    private static ListView<GroupModel> createListView(ObservableList<GroupModel> names) {
        ListView<GroupModel> listView = new ListView<GroupModel>(names);
        listView.setCellFactory(p -> new ListCell() {

                @Override
                protected void updateItem(Object item, boolean empty) {
                    GroupModel groupModel = (GroupModel) item;
                    super.updateItem(item, empty);

                    if (item == null || empty) {
                        // ...
                    } else {
                        setText(groupModel.getName());
                    }
                }
        });
        return listView;
    }

    public Button getAddGroup() {
        return addGroup;
    }

    @Override
    public void update(GroupService group) {
        // TODO
    }

    public BorderPane getRoot() {
        return root;
    }
}
