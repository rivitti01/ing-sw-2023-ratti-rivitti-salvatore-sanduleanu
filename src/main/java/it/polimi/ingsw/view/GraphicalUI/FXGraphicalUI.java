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


    /**

     Launches the GUI by creating a new thread and running a GUIRunnable object.
     The GUIRunnable is responsible for initializing and displaying the graphical user interface.
     @throws Exception if an exception occurs during the execution.
     */
    public void launchGUI() throws Exception {
        GUIRunnable gr = new GUIRunnable(listener);
        Thread GUIThread = new Thread(gr);
        GUIThread.start();
    }

    public static class GUIRunnable implements Runnable{

        private final ViewListener clientListener;

        /**
         Constructs a new GUIRunnable object with the specified ViewListener.
         @param l The ViewListener to associate with the GUIRunnable.
         */
        public GUIRunnable(ViewListener l){
            clientListener = l;
        }

        /**

         The run method of the GUIRunnable.
         It creates an instance of FXStageLauncher, adds the clientListener as a listener,
         and launches the stage using the FXStageLauncher.
         This method is executed when the GUIRunnable thread is started.
         */
        public void run(){
            FXStageLauncher gameLauncher = new FXStageLauncher();
            gameLauncher.addListener(clientListener);
            gameLauncher.launchStage();
        }
    }



    /**

     Handles the different messages received from the server and performs corresponding actions.
     @param e The Warnings enum representing the type of warning received.
     @throws RemoteException if a remote exception occurs during the execution.
     */

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
            case INVALID_ACTION -> {
                gameController.columnWithoutTiles();
            }
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
            case INVALID_ORDER -> {
                gameController.invalidOrder();
            }
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
            case OK_JOINER -> gameController.setJoiningPane();
            case INVALID_CHAT_MESSAGE -> {
                gameController.invalidChatMessage(); // con un label sotto la chat
            }
            case IVALID_RECEIVER -> {
                gameController.invalidReceiver();
            }
            case YOUR_TURN -> newTurn(true);
            case NOT_YOUR_TURN -> newTurn(false);
            case CORRECT_CORD -> {}
            case CONTINUE_TO_CHOOSE -> gameController.chooseNext(true);
            case ASK_COLUMN -> askColumn();
            case ASK_ORDER -> gameController.askOrder();
            case SET_NUMBER_PLAYERS, INVALID_NUMBER_PLAYERS -> askNumber();
            case ASK_NICKNAME -> askNickName();
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


    /**

     Notifies the game controller about a new turn.
     @param b a boolean indicating if it's the player's turn (true) or not (false).
     @throws RemoteException if a remote exception occurs during the execution.
     */
    @Override
    public void newTurn(boolean b) throws RemoteException {
        gameController.newTurn(b);
    }

    /**

     Notifies the game controller that a turn is resuming.
     @param playing a boolean indicating if the player is resuming their turn (true) or not (false).
     @throws RemoteException if a remote exception occurs during the execution.
     */
    @Override
    public void resumingTurn(boolean playing) throws RemoteException {
        gameController.resuming(playing);
    }


    /**

     Notifies the game controller to initiate the order selection process.
     This method is called when the game needs to ask the player to choose their order.
     */
    @Override
    public void askOrder() {
        gameController.choosingOrder();
    }


    /**

     Notifies the game controller to initiate the column selection process.
     This method is called when the game needs to ask the player to choose a column.
     */
    @Override

    public void askColumn() {
        gameController.choosingColumn();
    }

    /**
     Notifies the game controller that the last turn has been reached by a player with the specified nickname.
     @param nickname The nickname of the player who reached the last turn.
     */
    @Override
    public void lastTurnReached(String nickname) {
            gameController.lastTurnReached(nickname);
        }

    /**

     Notifies the game controller to print the final points chart and the winner.
     @param chart A Map representing the final points chart with player nicknames as keys and their corresponding points as values.
     @param winner The nickname of the winner.
     */
    @Override
    public void printFinalPoints(Map<String, Integer> chart, String winner) {
        gameController.printFinalPoints(chart, winner);
    }

    /**

     Sets the ViewListener for the current object.
     @param l The ViewListener to be set as the listener.
     */
    @Override
    public void addListener(ViewListener l) {
        listener=l;
    }


    /**

     Notifies the game controller to print the game view.
     @param gameView The GameView object representing the current state of the game.
     */
    @Override
    public void printGame(GameView gameView) {
        gameController.printGame(gameView);
    }



    /**

     Notifies the game controller that it is the last turn.
     @param playing a boolean indicating if it is the player's last turn (true) or not (false).
     */
    @Override
    public void lastTurn(boolean playing) {
        gameController.lastTurn(playing);
    }

    /**

     Notifies the GUI that the game has started.
     @param yourTurn a boolean indicating if it is the player's turn (true) or not (false) at the beginning of the game.
     */
    @Override
    public void gameStarted(boolean yourTurn) {
        try {
            newTurn(yourTurn);
        } catch (RemoteException e) {
            System.err.println("Error in gameStarted GUI side");
            throw new RuntimeException(e);
        }
    }

    /**

     Initiates the process of asking the player to enter the number of players.
     @throws RemoteException if a remote exception occurs during the execution.
     */
    public void askNumber() throws RemoteException{
            while(gameController==null) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            gameController.setPlayerNumber(true);
    }
    /**

     Notifies the game controller to initiate the process of choosing an action.
     @throws RemoteException if a remote exception occurs during the execution.
     */
    public void chooseAction() throws RemoteException{
        gameController.chooseNext(false);
    }

    /**

     Notifies the game controller to print the chat view.
     @param chatView The ChatView object representing the current state of the chat.
     @throws RemoteException if a remote exception occurs during the execution.
     */
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

    /**

     Initiates the process of asking the player to enter their nickname.
     @throws RemoteException if a remote exception occurs during the execution.
     */
    public void askExistingNickname(){
        gameController.askReconnectingNickname();
    }

    /**

     Notifies the game controller that a client has reconnected with the specified nickname.
     @param nickname The nickname of the reconnected player.
     */
    @Override
    public void clientReconnected(String nickname) {
        gameController.playerReconnected(nickname);
    }


    /**

     Notifies the game controller that a client has disconnected with the specified nickname.
     @param nickname The nickname of the disconnected player.
     */
    @Override
    public void clientDisconnected(String nickname) {
        gameController.playerDisconnected(nickname);
    }

    /**

     Sets the FXGameController for the FXGraphicalUI class.
     @param controller2 The FXGameController to be set as the controller.
     */
    public static void setController(FXGameController controller2){
        FXGraphicalUI.gameController=controller2;
    }
}