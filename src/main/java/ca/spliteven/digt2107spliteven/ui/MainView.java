package ca.spliteven.digt2107spliteven.ui;

import ca.spliteven.digt2107spliteven.model.GroupModel;
import ca.spliteven.digt2107spliteven.service.GroupService;
import ca.spliteven.digt2107spliteven.service.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;

public class MainView implements Observer {

    private final Button addGroup =  new Button("Add Group");
    private final Button addExpense = new Button("Add Expense");
    private final ObservableList<GroupModel> groups = FXCollections.observableArrayList();
    private final ListView<GroupModel> listView = createListView(groups);
    private final BorderPane root;

    public MainView() {
        final ToolBar toolBar = new ToolBar(
                addGroup,
                addExpense
        );

        // Nothing to add an expense to until a group is picked.
        addExpense.disableProperty().bind(
                listView.getSelectionModel().selectedItemProperty().isNull());
        listView.setPlaceholder(new Label("No groups yet."));

        final BorderPane root = new BorderPane();
        this.root = root;
        root.setTop(toolBar);
        root.setCenter(listView);
    }

    private static ListView<GroupModel> createListView(ObservableList<GroupModel> groups) {
        ListView<GroupModel> listView = new ListView<GroupModel>(groups);
        listView.setCellFactory(p -> new ListCell<GroupModel>() {

                @Override
                protected void updateItem(GroupModel groupModel, boolean empty) {
                    super.updateItem(groupModel, empty);

                    if (groupModel == null || empty) {
                        setText(null);
                    } else {
                        setText(groupModel.getName()
                                + "  (" + groupModel.getMembers().size() + " members)");
                    }
                }
        });
        return listView;
    }

    public Button getAddGroup() {
        return addGroup;
    }

    public Button getAddExpense() {
        return addExpense;
    }

    /** The group the user has picked, or null when none is selected. */
    public GroupModel getSelectedGroup() {
        return listView.getSelectionModel().getSelectedItem();
    }

    @Override
    public void update(GroupService service) {
        final GroupModel selected = getSelectedGroup();
        groups.setAll(service.getGroups());
        if (selected != null && groups.contains(selected)) {
            listView.getSelectionModel().select(selected);
        }
    }

    public BorderPane getRoot() {
        return root;
    }
}
