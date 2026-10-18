package ca.spliteven.digt2107spliteven;

import ca.spliteven.digt2107spliteven.ui.MainController;
import ca.spliteven.digt2107spliteven.service.GroupService;
import ca.spliteven.digt2107spliteven.ui.MainView;
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
        final BorderPane shell =  new BorderPane();
        final Navigator navigator = new Navigator(groupService, shell);
        navigator.showMain();
        final Scene scene = new Scene(shell, 720, 720);
        stage.setTitle("SplitEven");
        stage.setScene(scene);
        stage.show();
    }
}
