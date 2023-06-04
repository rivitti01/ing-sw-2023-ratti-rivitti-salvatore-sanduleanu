package it.polimi.ingsw.view;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.util.ViewListener;
import it.polimi.ingsw.util.Warnings;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

public class FXGraphicalUI extends Application implements UI{

    private ViewListener listener;
    private FXLoginController loginController;
    private int numberPlayers;

    @Override
    public void start(Stage primaryStage) throws Exception {

        loginController = new FXLoginController(/*numberPlayers*/);
        setControllerListener(listener);

        FXMLLoader loader = new FXMLLoader();
        loader.setController(loginController);
        loader.setLocation(getClass().getResource("/Login.fxml"));


        Parent root = loader.load();
        primaryStage.setTitle("My Shelfie");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();

    }

    @Override
    public void newTurn(boolean b) throws RemoteException {

    }

    @Override
    public void askOrder() {

    }

    @Override
    public void askColumn() {

    }

    @Override
    public void chooseAction() throws RemoteException {

    }

    public void askNumber() throws RemoteException {
    }

    @Override
    public void askNickName() throws RemoteException {
    }

    @Override
    public void warning(Warnings e) throws RemoteException {

    }

    @Override
    public void lastTurnReached(String nickname) {

    }

    @Override
    public void printBoard(Board b) {

    }

    @Override
    public void printShelves(Map<String, Shelf> playerShelves) {

    }

    @Override
    public void printPersonalGoalShelf(PersonalGoalCard personalGoalCard) {

    }

    @Override
    public void printChosenTiles(List<Tile> chosenTiles, String nickname) {

    }

    @Override
    public void printFinalPoints(Map<String, Integer> chart) {

    }

    @Override
    public void addListener(ViewListener l) {
        this.listener=l;
    }

    @Override
    public void printGame(GameView gameView) {

    }

    @Override
    public void printChat(ChatView chatView) throws RemoteException {

    }

    @Override
    public void lastTurn(boolean playing) {

    }
    @Override
    public void waitingTurn() throws RemoteException{

    }

    @Override
    public void gameStarted(boolean nickname) {

    }


    public void setControllerListener(ViewListener l){
        this.loginController.addListener(l);
    }


    public void launchGUI(){Application.launch();}

}