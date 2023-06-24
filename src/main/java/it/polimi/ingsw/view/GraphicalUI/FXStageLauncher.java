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
            FXMLLoader loginLoader = new FXMLLoader();
            FXMLLoader gameLoader = new FXMLLoader();
            loginLoader.setLocation(getClass().getResource("/Login.fxml"));
            gameLoader.setLocation(getClass().getResource("/Game.fxml"));

            FXGameController gameController = new FXGameController(listenerClient);
            gameLoader.setController(gameController);
            Parent gameRoot = gameLoader.load();
            Scene gameScene = new Scene(gameRoot, 1366, 768);

            FXLoginController loginController = new FXLoginController(listenerClient, gameScene, gameController);
            loginLoader.setController(loginController);
            Parent loginRoot = loginLoader.load();
            Scene loginScene = new Scene(loginRoot, 600, 400);

            primaryStage.setTitle("My Shelfie");
            primaryStage.setScene(loginScene);
            primaryStage.setResizable(false);
            Platform.setImplicitExit(true);

            FXGraphicalUI.setControllers(loginController, gameController);
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
