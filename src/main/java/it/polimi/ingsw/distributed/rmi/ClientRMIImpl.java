package it.polimi.ingsw.distributed.rmi;

import it.polimi.ingsw.distributed.Client;
import it.polimi.ingsw.distributed.Server;
import it.polimi.ingsw.model.GameView;
import it.polimi.ingsw.util.Warnings;
import it.polimi.ingsw.util.ViewListener;
import it.polimi.ingsw.view.GraphicalUI.FXGraphicalUI;
import it.polimi.ingsw.view.TextualUI;
import it.polimi.ingsw.view.UI;

import java.rmi.RemoteException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;


public class ClientRMIImpl extends UnicastRemoteObject implements Client, ViewListener, Runnable {
    private UI view;
    private Server stub;
    private String nickname = null;
    private static final int PONG_PERIOD = 5000;  // milliseconds
    private int id = -1;

    /**
     * Constructs a new `ClientRMIImpl` object with the specified server and GUI flag.
     *
     * @param s   the `Server` object representing the server
     * @param gui a boolean flag indicating whether to use a graphical user interface (GUI)
     * @throws RemoteException if there is a remote communication error
     */
    public ClientRMIImpl(Server s, boolean gui) throws RemoteException {
        super();
        this.stub = s;
        checkServer();
        if(gui){
            this.view = new FXGraphicalUI();
        }else
            this.view = new TextualUI();
        this.view.addListener(this);
        try {
            if (view instanceof FXGraphicalUI) ((FXGraphicalUI) view).launchGUI();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Constructs a new `ClientRMIImpl` object with the specified server and port number.
     *
     * @param s    the `Server` object representing the server
     * @param port the port number for RMI communication
     * @throws RemoteException if there is a remote communication error
     */
    public ClientRMIImpl(Server s, int port) throws RemoteException {
        super(port);
        this.stub = s;
        this.view.addListener(this);
        try {
            if (view instanceof FXGraphicalUI) ((FXGraphicalUI) view).launchGUI();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Constructs a new `ClientRMIImpl` object with the specified server, port number, client socket factory, and server socket factory.
     *
     * @param s    the `Server` object representing the server
     * @param port the port number for RMI communication
     * @param csf  the client socket factory
     * @param ssf  the server socket factory
     * @throws RemoteException if there is a remote communication error
     */
    public ClientRMIImpl(Server s, int port, RMIClientSocketFactory csf, RMIServerSocketFactory ssf) throws RemoteException {
        super(port, csf, ssf);
        this.stub = s;
        this.view.addListener(this);
        try {
            if (view instanceof FXGraphicalUI) ((FXGraphicalUI) view).launchGUI();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Checks the server's connectivity periodically.
     *
     * This method creates a separate thread that periodically sends a "pong" signal to the server
     * to check its connectivity.
     * If a remote communication error occurs during the ping-pong process,
     * the client assumes that the server has crashed and exits the game.
     */
    private void checkServer(){
        Thread pongThread = new Thread(() -> {
            while (true){
                try {
                    this.stub.pong();
                    Thread.sleep(PONG_PERIOD);
                } catch (RemoteException e) {
                    System.err.println("Server has crushed!\nExiting the game...");
                    System.exit(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        pongThread.start();
    }


    // ***************** VIEW LISTENER METHODS   ************************************************

    /**
     * Sets the client's nickname by invoking the corresponding method on the server.
     *
     * @param nickName the nickname to be set for the client
     * @throws RemoteException if there is a remote communication error
     */
    @Override
    public void clientNickNameSetting(String nickName) throws RemoteException{
        this.stub.clientNickNameSetting(this, nickName);

    }


    /**
     * Checks the existing nickname.
     *
     * @param nickname the nickname to check
     * @throws RemoteException if there is a remote communication error
     */
    @Override
    public void checkingExistingNickname(String nickname) throws RemoteException{
        this.stub.checkingExistingNickname(this, nickname);
    }

    /**
     * Checks the coordinates.
     *
     * @param coordinates the coordinates to check
     * @throws RemoteException if there is a remote communication error
     */
    @Override
    public void checkingCoordinates(int[] coordinates) throws RemoteException{
        this.stub.checkingCoordinates(coordinates);

    }

    /**
     * Sends the tile position to drop.
     *
     * @param tilePosition the position of the tile to drop
     * @throws RemoteException if there is a remote communication error
     */
    @Override
    public void tileToDrop(int tilePosition) throws RemoteException{
        this.stub.tileToDrop(tilePosition);
    }

    /**
     * Sets the column for the tiles to be dropped.
     *
     * @param i the column index
     * @throws RemoteException if there is a remote communication error
     */
    @Override
    public void columnSetting(int i)   throws   RemoteException{
        this.stub.columnSetting(i);
    }

    /**
     * Sets the number of participants.
     *
     * @param n the number of participants
     * @throws RemoteException if there is a remote communication error
     */
    @Override
    public void numberPartecipantsSetting(int n) throws RemoteException{
        this.stub.numberOfParticipantsSetting(n);
    }

    /**
     * Signals the end of the selection phase.
     *
     * @throws RemoteException if there is a remote communication error
     */
    @Override
    public void endsSelection() throws  RemoteException{
        this.stub.endsSelection();
    }

    /**
     * Sends a new message to the server.
     *
     * @param message the message to send
     * @throws RemoteException if there is a remote communication error
     */
    @Override
    public void newMessage(String message) throws RemoteException {
        this.stub.newMessage(this, message);
    }


    //*********************************      CLIENT METHODS      *********************************************************

    /**
     * Notifies the client about a new turn.
     *
     * @param playing a boolean indicating if it's the client's turn to play
     */
    @Override
    public void newTurn(boolean playing) {
        try {
            this.view.newTurn(playing);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Notifies the client about the last turn of the game.
     *
     * @param playing a boolean indicating if the client is still playing
     */
    @Override
    public void lastTurn(boolean playing){
        this.view.lastTurn(playing);
    }

    /**
     * Prints the current game state to the client's view.
     *
     * @param gameView the GameView object representing the current game state
     */
    @Override
    public void printGame(GameView gameView)  {
        this.view.printGame(gameView);
    }

    /**
     * Notifies the client about the final points of the game.
     *
     * @param finalPoints a map containing the final points of each player
     * @throws RemoteException if there is a remote communication error
     */
    @Override
    public void finalPoints(Map<String, Integer> finalPoints) throws RemoteException {
        this.view.printFinalPoints(finalPoints);
    }

    /**
     * Notifies the client about a warning or error.
     *
     * @param e the Warnings object representing the warning or error
     * @throws RemoteException if there is a remote communication error
     */
    @Override
    public void warning(Warnings e) throws RemoteException {
        this.view.warning(e);
        if(e == Warnings.GAME_ALREADY_STARTED || e == Warnings.NO_PLAYERS_LEFT)
            System.exit(1);
    }


    /**
     * Asks the client to enter the number of participants for the game.
     *
     * @throws RemoteException if there is a remote communication error
     */
    @Override
    public void askNumberParticipants() throws RemoteException {
        this.view.askNumber();

    }

    /**
     * Asks the client to enter the order of the tiles to be dropped.
     */
    @Override
    public void askOrder(){
        this.view.askOrder();

    }

    /**
     * Notifies the client about the last turn reached by a player who completed a shelf.
     *
     * @param nickname the nickname of the player who reached the last turn
     */
    @Override
    public void lastTurnNotification(String nickname){
        this.view.lastTurnReached(nickname);
    }

    /**
     * Asks the client to choose an action to perform.
     *
     * @throws RemoteException if there is a remote communication error
     */
    @Override
    public void askAction() throws RemoteException {
        this.view.chooseAction();

    }

    /**
     * Asks the client to enter a nickname.
     *
     * @throws RemoteException if there is a remote communication error
     */
    @Override
    public void askNickname() throws RemoteException {
        this.view.askNickName();

    }

    /**
     * Asks the client to RE-enter a nickname to reconnect to the game.
     *
     * @throws RemoteException if there is a remote communication error
     */
    @Override
    public void askExistingNickname() throws RemoteException{
        this.view.askExistingNickname();

    }

    /**
     * Ping method to check the client's connectivity.
     *
     * @throws RemoteException if there is a remote communication error
     */
    @Override
    public void ping() throws RemoteException {
    }

    /**
     * Asks the client to enter a column for their chosen tiles to be dropped.
     *
     * @throws RemoteException if there is a remote communication error
     */
    @Override
    public void askColumn() throws RemoteException {
        this.view.askColumn();
    }

    /**
     * Runs the client's thread for handling server communication.
     */
    @Override
    public void run(){
        try {
            this.stub.clientConnection(this);
        } catch (RemoteException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the client's nickname.
     *
     * @return the client's nickname
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * Sets the nickname of the client.
     *
     * @param nickname the nickname to set
     * @throws RemoteException if there is a remote communication error
     */
    @Override
    public void setNickname(String nickname) throws RemoteException {
        this.nickname = nickname;
    }

    /**
     * Notifies the client that the game has started.
     *
     * @param yourTurn a boolean indicating if it's the client's turn to play
     */
    @Override
    public void gameStarted(boolean yourTurn) {
        this.view.gameStarted(yourTurn);
    }

    /**
     * Sets the ID of the client.
     *
     * @param id the ID to set
     * @throws RemoteException if there is a remote communication error
     */
    @Override
    public void setID(int id) throws RemoteException {
        this.id = id;
    }

    /**
     * Returns the ID of the client.
     *
     * @return the ID of the client
     * @throws RemoteException if there is a remote communication error
     */
    @Override
    public int getID() throws RemoteException {
        return this.id;
    }

    /**
     * Notifies the client about the resumption of their turn.
     *
     * @param playing a boolean indicating if it's the client's turn to play
     * @throws RemoteException if there is a remote communication error
     */
    @Override
    public void resumingTurn(boolean playing) throws RemoteException {
        this.view.resumingTurn(playing);
    }

    /**
     * Notifies the client that another client has reconnected to the server.
     *
     * @param nickname the nickname of the reconnected client
     * @throws RemoteException if there is a remote communication error
     */
    @Override
    public void clientReconnected(String nickname) throws RemoteException {
        view.clientReconnected(nickname);
    }

    /**
     * Notifies the client that another client has disconnected from the server.
     *
     * @param nickname the nickname of the disconnected client
     * @throws RemoteException if there is a remote communication error
     */
    @Override
    public void clientDisconnected(String nickname) throws RemoteException{
        this.view.clientDisconnected(nickname);
    }
}
