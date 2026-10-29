module ca.spliteven.digt2107spliteven {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires java.desktop;

    opens ca.spliteven.digt2107spliteven to javafx.fxml;
    exports ca.spliteven.digt2107spliteven;
}