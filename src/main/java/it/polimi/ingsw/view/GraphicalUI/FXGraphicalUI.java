package it.polimi.ingsw.view.GraphicalUI;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.util.ViewListener;
import it.polimi.ingsw.util.Warnings;
import it.polimi.ingsw.view.UI;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

public class FXGraphicalUI implements UI {

    public GameView model;
    private static ViewListener listener;
    private int numberPlayers;
    private boolean started;
    private FXStageLauncher stageLauncher;

    public void launchGUI(){
        stageLauncher = new FXStageLauncher(listener);
        stageLauncher.launchStage();
    }

    @Override
    public void newTurn(boolean b) throws RemoteException {
        stageLauncher.gameController.newTurn(b);
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
        stageLauncher.gameController.lastTurnReached();
    }





    @Override
    public void printFinalPoints(Map<String, Integer> chart) {
        stageLauncher.gameController.printFinalPoints(chart);
    }

    @Override
    public void addListener(ViewListener l) {
        this.listener=l;
    }

    @Override
    public void printGame(GameView gameView) {
        stageLauncher.gameController.printGame(gameView);
    }

    @Override
    public void lastTurn(boolean playing) {
        stageLauncher.gameController.lastTurn(playing);
    }

    @Override
    public void gameStarted(boolean nickname) { stageLauncher.gameController.startGame();}


    public void waitingTurn() throws RemoteException{
        stageLauncher.gameController.waitingTurn();
    }

    public void askNumber() throws RemoteException{
        System.out.println("d");
        stageLauncher.loginController.setPlayerNumber(true);
    }
    public void chooseAction() throws RemoteException{
        //
    }
    public void printChat(ChatView chatView) throws RemoteException {
        stageLauncher.gameController.printChat(chatView);
    }
    public void askNickName() throws RemoteException{
        //
    }

    public void printShelves(Map <String, Shelf> playerShelves){}

    public void printPersonalGoalShelf(PersonalGoalCard personalGoalCard){}

    public void printChosenTiles(List<Tile> chosenTiles, String nickname){}

}