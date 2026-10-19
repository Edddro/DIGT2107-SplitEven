package ca.spliteven.digt2107spliteven.ui;

import ca.spliteven.digt2107spliteven.model.GroupModel;
import ca.spliteven.digt2107spliteven.model.MemberModel;
import ca.spliteven.digt2107spliteven.service.BalanceService;
import ca.spliteven.digt2107spliteven.service.GroupService;
import ca.spliteven.digt2107spliteven.service.Money;
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

import java.util.Map;

public class MainView implements Observer {

    private final Button addGroup = new Button("Add Group");
    private final Button addExpense = new Button("Add Expense");
    private final ObservableList<GroupModel> groups = FXCollections.observableArrayList();
    private final ListView<GroupModel> listView = createListView(groups);
    private final VBox balances = new VBox(12);
    private final BalanceService balanceService;
    private final BorderPane root;

    public MainView(BalanceService balanceService) {
        this.balanceService = balanceService;
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

    /** Lists each member of the selected group as owed, owing or settled. */
    private void showBalances() {
        balances.getChildren().clear();
        final GroupModel selected = getSelectedGroup();
        if (selected == null) {
            balances.getChildren().add(new Label("Everyone is settled."));
            return;
        }

        boolean allSettled = true;
        final Map<MemberModel, Long> balancesByMember = balanceService.balancesFor(selected);
        for (Map.Entry<MemberModel, Long> entry : balancesByMember.entrySet()) {
            final String name = entry.getKey().getName();
            final long cents = entry.getValue();
            final Label line;
            if (cents > 0) {
                line = new Label(name + " is owed $" + Money.format(cents));
                allSettled = false;
            } else if (cents < 0) {
                line = new Label(name + " owes $" + Money.format(-cents));
                allSettled = false;
            } else {
                line = new Label(name + " is settled up");
            }
            balances.getChildren().add(line);
        }
        if (allSettled) {
            balances.getChildren().add(0, new Label("Everyone is settled."));
        }
    }
}
