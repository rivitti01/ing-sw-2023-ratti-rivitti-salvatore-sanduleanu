package it.polimi.ingsw.view.GraphicalUI;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.util.CurrentState;
import it.polimi.ingsw.util.ViewListener;
import it.polimi.ingsw.util.Warnings;
import it.polimi.ingsw.view.UI;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

public class FXGraphicalUI implements UI {
    
    private static ViewListener listener;
    private static FXGameController gameController = null;
    private CurrentState currentState = null;

    public void launchGUI() throws Exception {
        GUIRunnable gr = new GUIRunnable(listener);
        Thread GUIThread = new Thread(gr);
        GUIThread.start();
    }

    public static class GUIRunnable implements Runnable{

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
    public void warning(Warnings e) throws RemoteException {
        switch (e){
            case INVALID_TILE -> {
                gameController.invalidTile();
            }
            case INVALID_NICKNAME ->{
                synchronized (listener) {
                    while (gameController == null) {
                        try {
                            listener.wait();
                        } catch (InterruptedException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                    gameController.invalidNickname();
                    listener.notifyAll();
                }
            }

            case INVALID_COLUMN -> {
                synchronized (listener) {
                    while (gameController == null) {
                        try {
                            listener.wait();
                        } catch (InterruptedException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                    gameController.invalidColumn();
                    listener.notifyAll();
                }
            }
            case INVALID_ACTION -> {}
            case GAME_ALREADY_STARTED -> {
                    while (gameController == null) {
                        try {
                            listener.wait();
                        } catch (InterruptedException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                gameController.gameAlreadyStarted();
            }
            case MAX_TILES_CHOSEN -> {
                gameController.maxTiles();
            }
            case INVALID_ORDER -> {}
            case WAIT -> {
                    while (gameController == null) {
                        try {
                            Thread.sleep(150);
                        } catch (InterruptedException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                    gameController.waitForNumber();
            }
            case OK_JOINER -> {}
            case INVALID_CHAT_MESSAGE -> {
                gameController.invalidChatMessage(); // con un label sotto la chat
            }
            case IVALID_RECEIVER -> {
                gameController.invalidReceiver();
            }
            case YOUR_TURN -> newTurn(true);
            case NOT_YOUR_TURN -> newTurn(false);
            case CORRECT_CORD -> {}
            case CONTINUE_TO_CHOOSE -> gameController.chooseNext();
            case ASK_COLUMN -> askColumn();
            case ASK_ORDER -> gameController.askOrder();
            case SET_NUMBER_PLAYERS, INVALID_NUMBER_PLAYERS -> askNumber();
            case ASK_NICKNAME -> askNickName();
            case CLIENT_DISCONNECTED -> gameController.playerDisconnected();
            case WAITING_FOR_MORE_PLAYERS -> gameController.waitingToContinue();
            case NO_PLAYERS_LEFT -> gameController.noPlayersLeft();
            case RECONNECTION -> {
                // this.currentState = CurrentState.WAITING_TURN; potrebbe servire
                gameController.reconnectedMessage();
            }
            case INVALID_RECONNECTION_NICKNAME -> {
                synchronized (listener) {
                    while (gameController == null) {
                        try {
                            listener.wait();
                        } catch (InterruptedException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                    gameController.invalidReconnectionNickname();
                    askExistingNickname();
                    listener.notifyAll();
                }
            }
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
    }

    public void printBoard(Board b){

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
        listener=l;
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
    public void gameStarted(boolean nickname) {
        System.out.println("DDDDD");
        gameController.startGame();}

    public void waitingTurn() throws RemoteException{
        gameController.waitingTurn();
    }

    public void askNumber() throws RemoteException{
            while(gameController==null) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            System.out.println("d");
            gameController.setPlayerNumber(true);
    }

    public void chooseAction() throws RemoteException{
        //
    }
    public void printChat(ChatView chatView) throws RemoteException {
        gameController.printChat(chatView);
    }
    public void askNickName() throws RemoteException{
            while(gameController==null) {
                try {
                    Thread.sleep(150);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            System.out.println("nick");
            gameController.askNickname();
    }
    public void printShelves(Map <String, Shelf> playerShelves){}
    public void printPersonalGoalShelf(PersonalGoalCard personalGoalCard){}
    public void printChosenTiles(List<Tile> chosenTiles, String nickname){}

    public void askExistingNickname(){
        gameController.askReconnectingNickname();
    }
    public static void setController(FXGameController controller2){
        FXGraphicalUI.gameController=controller2;
    }
}