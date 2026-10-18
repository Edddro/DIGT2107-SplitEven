package ca.spliteven.digt2107spliteven;

import ca.spliteven.digt2107spliteven.ui.MainController;
import ca.spliteven.digt2107spliteven.service.GroupService;
import ca.spliteven.digt2107spliteven.ui.MainView;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class SplitEven extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        final GroupService model = new GroupService();
        final MainView view = new MainView(stage);
        final MainController controller = new MainController(model, view);
    }
}
