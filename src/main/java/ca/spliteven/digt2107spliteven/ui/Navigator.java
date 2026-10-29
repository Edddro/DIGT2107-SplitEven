package ca.spliteven.digt2107spliteven.ui;

import ca.spliteven.digt2107spliteven.model.GroupModel;
import ca.spliteven.digt2107spliteven.service.ExpenseService;
import ca.spliteven.digt2107spliteven.service.GroupService;
import javafx.scene.layout.BorderPane;

public class Navigator {
    private final BorderPane shell;
    private final GroupService service;
    private final ExpenseService expenseService;
    private final MainView mainView;                  // built ONCE: it's an observer

    public Navigator(GroupService service, ExpenseService expenseService, BorderPane shell) {
        this.shell = shell;
        this.service = service;
        this.expenseService = expenseService;
        this.mainView = new MainView();
        service.addObserver(mainView);
        new MainController(service, mainView, this); // add this?
    }

    public void showMain() {
        mainView.update(service);
        shell.setCenter(mainView.getRoot());
    }

    public void showAddGroup() {
        CreateGroupView view = new CreateGroupView();
        CreateGroupController controller = new CreateGroupController(service, view, this);
        shell.setCenter(view.getRoot());
    }

    public void showAddExpense(GroupModel group) {    // built fresh each time: an empty form
        AddExpenseView view = new AddExpenseView(group);
        new AddExpenseController(expenseService, view, this, group);
        shell.setCenter(view.getRoot());
    }
}
