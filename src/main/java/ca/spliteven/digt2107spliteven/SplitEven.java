package ca.spliteven.digt2107spliteven;

import ca.spliteven.digt2107spliteven.model.GroupModel;
import ca.spliteven.digt2107spliteven.model.MemberModel;
import ca.spliteven.digt2107spliteven.service.ExpenseService;
import ca.spliteven.digt2107spliteven.service.GroupService;
import ca.spliteven.digt2107spliteven.ui.Navigator;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class SplitEven extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        final GroupService groupService = new GroupService();
        final ExpenseService expenseService = new ExpenseService(groupService);
        seedDemoGroup(groupService);
        final BorderPane shell =  new BorderPane();
        final Navigator navigator = new Navigator(groupService, expenseService, shell);
        navigator.showMain();
        final Scene scene = new Scene(shell, 860, 720);
        stage.setTitle("SplitEven");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Gives the app one group to work with so the Add Expense form can be opened
     * and demonstrated.
     *
     * <p>Temporary scaffolding: GE-001 adds the Create Group form and GE-006 loads
     * real groups from storage at startup. Delete this method once either lands.</p>
     */
    private void seedDemoGroup(GroupService groupService) {
        if (!groupService.getGroups().isEmpty()) {
            return;
        }
        final GroupModel group = new GroupModel("Apartment 4B");
        group.addMember(new MemberModel("Adam"));
        group.addMember(new MemberModel("Edward"));
        group.addMember(new MemberModel("Francis"));
        group.addMember(new MemberModel("Claire"));
        groupService.addGroup(group);
    }
}
