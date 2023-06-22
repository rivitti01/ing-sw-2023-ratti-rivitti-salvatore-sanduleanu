package it.polimi.ingsw.view.GraphicalUI;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FXStageLauncher extends Application {

    public void start(Stage primaryStage) throws Exception {

        FXMLLoader loginLoader = new FXMLLoader();
        loginLoader.setLocation(getClass().getResource("/Login.fxml"));

        Parent loginParent = loginLoader.load();
        Scene loginScene = new Scene(loginParent, 600, 400);

        primaryStage.setTitle("My Shelfie");
        primaryStage.setScene(loginScene);
        primaryStage.setResizable(false);
        Platform.setImplicitExit(true);
        primaryStage.show();
    }

    public void launchStage(){
        Application.launch();
    }
}
