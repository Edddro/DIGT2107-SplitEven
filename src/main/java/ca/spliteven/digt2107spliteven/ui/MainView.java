package ca.spliteven.digt2107spliteven.ui;

import ca.spliteven.digt2107spliteven.mock.MockOpeningData;
import ca.spliteven.digt2107spliteven.mock.MockOpeningData.Person;
import ca.spliteven.digt2107spliteven.model.GroupModel;
import ca.spliteven.digt2107spliteven.service.GroupService;
import ca.spliteven.digt2107spliteven.service.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.util.List;

public class MainView implements Observer {

    private final Button addGroup = new Button("Add Group");
    private final Button addExpense = new Button("Add Expense");
    private final ObservableList<GroupModel> groups = FXCollections.observableArrayList();
    private final ListView<GroupModel> listView = createListView(groups);
    private final VBox balances = new VBox(12);
    private final BorderPane root;

    public MainView() {
        final ToolBar toolBar = new ToolBar(
                addGroup,
                addExpense,
                new Button("Settle Up"),
                new Button("Expense History"));

        addExpense.disableProperty().bind(
                listView.getSelectionModel().selectedItemProperty().isNull());
        listView.setPlaceholder(new Label("No groups yet."));
        listView.getSelectionModel().selectedItemProperty().addListener(
                (obs, previous, current) -> showBalances());

        balances.setPadding(new Insets(12));

        final BorderPane root = new BorderPane();
        this.root = root;
        root.setTop(toolBar);
        root.setCenter(listView);
        root.setBottom(balances);
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

    public GroupModel getSelectedGroup() {
        return listView.getSelectionModel().getSelectedItem();
    }

    @Override
    public void update(GroupService service) {
        final GroupModel selected = getSelectedGroup();
        groups.setAll(service.getGroups());
        if (selected != null && groups.contains(selected)) {
            listView.getSelectionModel().select(selected);
        } else if (!groups.isEmpty()) {
            listView.getSelectionModel().selectFirst();
        }
        showBalances();
    }

    public BorderPane getRoot() {
        return root;
    }

    private void showBalances() {
        balances.getChildren().clear();
        GroupModel selected = getSelectedGroup();
        List<Person> people = selected == null || selected.getMembers().isEmpty()
                ? List.of()
                : MockOpeningData.people();
        if (people.isEmpty()) {
            balances.getChildren().add(new Label("Everyone is settled."));
            return;
        }
        for (Person person : people) {
            Label heading = new Label(person.getName() + "  " + person.getTotal());
            heading.setStyle("-fx-font-weight: bold");
            
            VBox block = new VBox(2, heading);
            for (String line : person.getLines()) {
                block.getChildren().add(new Label(line));
            }
            balances.getChildren().add(block);
        }
    }
}
