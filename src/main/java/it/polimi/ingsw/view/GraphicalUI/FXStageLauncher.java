package it.polimi.ingsw.view.GraphicalUI;

import it.polimi.ingsw.util.ViewListener;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Callback;

public class FXStageLauncher extends Application {

    private static ViewListener listenerClient;

    /**

     The start method is the entry point for the JavaFX application.

     It initializes and configures the primary stage, loads the game UI from an FXML file,

     sets up the game controller, and displays the game scene.

     @param primaryStage The primary stage of the JavaFX application.

     @throws Exception if an exception occurs during the execution.
     */
    public void start(Stage primaryStage) throws Exception {
        synchronized (listenerClient) {
            FXMLLoader gameLoader = new FXMLLoader();
            gameLoader.setLocation(getClass().getResource("/Game.fxml"));
            FXGameController gameController = new FXGameController(listenerClient);
            Callback<Class<?>, Object> controllerFactory = type ->{
                if(type == FXGameController.class)
                    return gameController;
                else{
                        try {
                            return type.newInstance();
                        } catch (InstantiationException | IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                }
            };

            gameLoader.setControllerFactory(controllerFactory);

            Parent gameRoot = gameLoader.load();
            Scene gameScene = new Scene(gameRoot, 1366, 768);

            primaryStage.setTitle("My Shelfie");
            primaryStage.setScene(gameScene);
            primaryStage.setResizable(false);
            Platform.setImplicitExit(true);

            FXGraphicalUI.setController(gameController);


            listenerClient.notifyAll();
        }

        primaryStage.setOnCloseRequest(e -> {
            System.exit(-1);
        });
        primaryStage.show();
    }


    /**
     Launches the JavaFX application by invoking the Application.launch() method.
     This method should be called to start the JavaFX application and display the primary stage.
     Note: This method assumes that the class containing this method extends the Application class.
     @see Application#launch(String...)
     */
    public void launchStage(){
        Application.launch();
    }


    /**

     Adds a listener to the FXStageLauncher.
     @param l The ViewListener to be added as a listener.
     */
    public void addListener(ViewListener l) {
        FXStageLauncher.listenerClient=l;
    }
}
