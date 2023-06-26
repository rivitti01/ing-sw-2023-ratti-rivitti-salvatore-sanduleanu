module softeng.gc30 {

    requires javafx.controls;
    requires javafx.fxml;
    requires java.rmi;
    requires com.google.gson;
    requires java.desktop;
    requires javafx.swing;

    opens it.polimi.ingsw.view.GraphicalUI to javafx.fxml;
    opens it.polimi.ingsw.distributed to java.rmi;
    exports it.polimi.ingsw.view.GraphicalUI to javafx.graphics;
    exports it.polimi.ingsw.util;
    exports it.polimi.ingsw.model;
}