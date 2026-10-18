package ca.spliteven.digt2107spliteven.ui;

import ca.spliteven.digt2107spliteven.service.GroupService;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Navigator {
    private final BorderPane shell;
    private final GroupService service;
    private final MainView mainView;                  // built ONCE: it's an observer

    public Navigator(GroupService service, BorderPane shell) {
        this.shell = shell;
        this.service = service;
        this.mainView = new MainView();
        service.addObserver(mainView);
        new MainController(service, mainView, this); // add this?
    }

    public void showMain() {
        shell.setCenter(mainView.getRoot());
    }

    public void showAddGroup() {
        CreateGroupView view = new CreateGroupView();
        CreateGroupController controller = new CreateGroupController(service, view, this);
        shell.setCenter(view.getRoot());
    }

//    public void showAddExpense(String groupId) {      // built fresh each time: an empty form
//        AddExpenseView view = new AddExpenseView();
//        new AddExpenseController(view, service, this, groupId);
//        shell.setCenter(view.getRoot());
//    }
}
