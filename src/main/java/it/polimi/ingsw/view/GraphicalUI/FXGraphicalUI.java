package it.polimi.ingsw.view.GraphicalUI;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.util.ViewListener;
import it.polimi.ingsw.util.Warnings;
import it.polimi.ingsw.view.UI;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

public class FXGraphicalUI implements UI {
    private static ViewListener listener;
    private static FXLoginController loginController = null;
    private static FXGameController gameController = null;
    //private int numberPlayers;
    private boolean started;

    public void launchGUI() throws Exception {
        GUIRunnable gr = new GUIRunnable(listener);
        Thread GUIThread = new Thread(gr);
        GUIThread.start();
    }

    private class GUIRunnable implements Runnable{

        private final ViewListener clientListener;
        public GUIRunnable(ViewListener l){
            clientListener = l;
        }
        public void run(){
            FXStageLauncher gameLauncher = new FXStageLauncher();
            gameLauncher.addListener(clientListener);
            gameLauncher.launchStage();
        }
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
        FXGraphicalUI.listener=l;
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
        while(loginController==null)
        {
            try
            {
                Thread.sleep(500);
            }
            catch(InterruptedException ex)
            {
                Thread.currentThread().interrupt();
            }
        }
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

        while(loginController==null)
        {
            try
            {
                Thread.sleep(500);
            }
            catch(InterruptedException ex)
            {
                Thread.currentThread().interrupt();
            }
        }

    }
    public void askExistingNickname() {

    }

    public void printShelves(Map <String, Shelf> playerShelves){}

    public void printPersonalGoalShelf(PersonalGoalCard personalGoalCard){}

    public void printChosenTiles(List<Tile> chosenTiles, String nickname){}

    public static void setControllers(FXLoginController controller1, FXGameController controller2){
        FXGraphicalUI.loginController=controller1;
        FXGraphicalUI.gameController=controller2;
    }

}