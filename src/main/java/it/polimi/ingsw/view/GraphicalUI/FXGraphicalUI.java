package it.polimi.ingsw.view.GraphicalUI;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.util.ViewListener;
import it.polimi.ingsw.util.Warnings;
import it.polimi.ingsw.view.UI;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

public class FXGraphicalUI implements UI {

    public GameView model;
    private static ViewListener listener;
    private FXLoginController loginController;
    private FXGameController gameController;
    private int numberPlayers;
    private boolean started;

    public void launchGUI() throws Exception {

        FXMLLoader loginFXML = new FXMLLoader();
        FXMLLoader gameFXML = new FXMLLoader();
        loginFXML.setLocation(getClass().getResource("/Login.fxml"));
        gameFXML.setLocation(getClass().getResource("/Game.fxml"));

        gameController = new FXGameController(listener);
        gameFXML.setController(gameController);

        Parent gameRoot = gameFXML.load();
        Scene gameScene = new Scene(gameRoot, 1366, 768);

        loginController = new FXLoginController(listener, gameScene, gameController);
        loginFXML.setController(loginController);

        FXStageLauncher gameLauncher = new FXStageLauncher();
        Thread GUIThread = new Thread(){

        };


    }

    @Override
    public void newTurn(boolean b) throws RemoteException {
        gameController.newTurn(b);
    }

    @Override
    public void askOrder() {
    }

    @Override
    public void askColumn() {
    //
    }

    public void printBoard(Board b){

    }
    @Override
    public void warning(Warnings e) throws RemoteException {
        switch (e) {
            case SET_NUMBER_PLAYERS, INVALID_NUMBER_PLAYERS -> askNumber();
        }
    //
    }

    @Override
    public void lastTurnReached(String nickname) {
        gameController.lastTurnReached();
    }





    @Override
    public void printFinalPoints(Map<String, Integer> chart) {
        gameController.printFinalPoints(chart);
    }

    @Override
    public void addListener(ViewListener l) {
        this.listener=l;
    }

    @Override
    public void printGame(GameView gameView) {
        gameController.printGame(gameView);
    }

    @Override
    public void lastTurn(boolean playing) {
        gameController.lastTurn(playing);
    }

    @Override
    public void gameStarted(boolean nickname) { gameController.startGame();}


    public void waitingTurn() throws RemoteException{
        gameController.waitingTurn();
    }

    public void askNumber() throws RemoteException{
        System.out.println("d");
        loginController.setPlayerNumber(true);
    }
    public void chooseAction() throws RemoteException{
        //
    }
    public void printChat(ChatView chatView) throws RemoteException {
        gameController.printChat(chatView);
    }
    public void askNickName() throws RemoteException{
        //
    }
    public void askExistingNickname() {

    }

    public void printShelves(Map <String, Shelf> playerShelves){}

    public void printPersonalGoalShelf(PersonalGoalCard personalGoalCard){}

    public void printChosenTiles(List<Tile> chosenTiles, String nickname){}

}