package it.polimi.ingsw.view.GraphicalUI;

import it.polimi.ingsw.util.ViewListener;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FXStageLauncher extends Application {

    public static ViewListener listenerClient;
    public FXLoginController loginController;
    public  FXGameController gameController;

    public FXStageLauncher(ViewListener listener) {
        listenerClient = listener;
    };

    public FXStageLauncher (){}

    public void start(Stage primaryStage) throws Exception {


        FXMLLoader loader = new FXMLLoader();
        FXMLLoader loader2 = new FXMLLoader();
        loader.setLocation(getClass().getResource("/Login.fxml"));
        loader2.setLocation(getClass().getResource("/Game.fxml"));

        gameController = new FXGameController(listenerClient);
        loader2.setController(gameController);
        Parent root2 = loader2.load();
        Scene gameScene = new Scene(root2, 1366, 768);

        loginController = new FXLoginController(listenerClient, gameScene, gameController);
        loader.setController(loginController);
        Parent root = loader.load();
        Scene loginScene = new Scene(root, 600, 400);

        primaryStage.setTitle("My Shelfie");
        primaryStage.setScene(loginScene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public void launchStage(){
        Application.launch();
    }
}
