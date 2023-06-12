module softeng.gc30 {

    requires javafx.controls;
    requires javafx.fxml;
    requires java.rmi;
    requires javafx.swing;
    requires com.google.gson;

    opens it.polimi.ingsw.view.GraphicalUI to javafx.fxml;
    opens it.polimi.ingsw.distributed to java.rmi;
    exports it.polimi.ingsw.view.GraphicalUI;
}