package ca.spliteven.digt2107spliteven;

import ca.spliteven.digt2107spliteven.service.ExpenseService;
import ca.spliteven.digt2107spliteven.service.GroupService;
import ca.spliteven.digt2107spliteven.storage.CsvStorage;
import ca.spliteven.digt2107spliteven.ui.Navigator;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SplitEven extends Application {
    private static final String DATA_DIRECTORY_PROPERTY = "spliteven.dataDir";

    @Override
    public void start(Stage stage) throws IOException {
        final CsvStorage storage = new CsvStorage(dataDirectory());
        final GroupService groupService = new GroupService(storage);
        final ExpenseService expenseService = new ExpenseService(groupService);
        final BorderPane shell =  new BorderPane();
        final Navigator navigator = new Navigator(groupService, expenseService, shell);
        navigator.showMain();
        final Scene scene = new Scene(shell, 860, 720);
        stage.setTitle("SplitEven");
        stage.setScene(scene);
        stage.show();
    }

    private Path dataDirectory() {
        final String configuredDirectory = System.getProperty(DATA_DIRECTORY_PROPERTY);
        if (configuredDirectory != null && !configuredDirectory.trim().isEmpty()) {
            return Paths.get(configuredDirectory);
        }
        return Paths.get(System.getProperty("user.home"), ".spliteven");
    }

}
