package it.polimi.ingsw.view.GraphicalUI;

import it.polimi.ingsw.util.ViewListener;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FXStageLauncher extends Application {

    private static ViewListener listenerClient;

    public void start(Stage primaryStage) throws Exception {
        synchronized (listenerClient) {
            FXMLLoader gameLoader = new FXMLLoader();
            gameLoader.setLocation(getClass().getResource("/Game.fxml"));
            FXGameController gameController = new FXGameController(listenerClient);
            gameLoader.setController(gameController);
            Parent gameRoot = gameLoader.load();
            Scene gameScene = new Scene(gameRoot, 1366, 768);

            primaryStage.setTitle("My Shelfie");
            primaryStage.setScene(gameScene);
            primaryStage.setResizable(false);
            Platform.setImplicitExit(false);

            FXGraphicalUI.setController(gameController);

            listenerClient.notifyAll();
        }

        primaryStage.show();
    }

    public void launchStage(){
        Application.launch();
    }

    public void addListener(ViewListener l) {
        FXStageLauncher.listenerClient=l;
    }
}
