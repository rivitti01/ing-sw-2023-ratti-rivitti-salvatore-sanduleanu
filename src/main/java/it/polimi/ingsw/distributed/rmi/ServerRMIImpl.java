package it.polimi.ingsw.distributed.rmi;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.distributed.*;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.util.Warnings;
import it.polimi.ingsw.util.ModelListener;

import java.rmi.RemoteException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import static it.polimi.ingsw.util.Costants.PING_PERIOD;
import static it.polimi.ingsw.view.Colors.ANSI_GREEN_BACKGROUND;
import static it.polimi.ingsw.view.Colors.ANSI_RESET;


/**
 * The `ServerImpl` class represents the server-side implementation for the RMI communication protocol.
 * It implements the `Server` interface and extends the `UnicastRemoteObject` class.
 */
public class ServerRMIImpl extends UnicastRemoteObject implements Server, ModelListener {
    private final Game model;
    private final GameController controller;
    private final LinkedHashMap<Client, String> connectedClients;
    private Map<Client,Integer> connectedClientsID;
    private final ReentrantLock connectionLock;
    private First first;
    private ServerListener serverONE;

    /**
     * Constructs a new ServerImpl object.
     *
     * @param model      The Game object representing the game model.
     * @param controller The GameController object controlling the game.
     * @param first      The First object is used to ask the first player connected
     *                   the number of players in the game.
     * @throws RemoteException if there is a communication-related issue.
     */
    public ServerRMIImpl(Game model, GameController controller, First first) throws RemoteException {
        super();
        this.first = first;
        connectedClients = new LinkedHashMap<>();
        connectedClientsID = new HashMap<>();
        this.model = model;
        this.model.addModelListener(this);
        this.controller = controller;
        this.connectionLock = new ReentrantLock();
    }

    /**
     * Constructs a new ServerImpl object with the specified port.
     *
     * @param port The port number for the server.
     * @throws RemoteException if there is a communication-related issue.
     */
    public ServerRMIImpl(int port) throws RemoteException {
        super(port);
        connectedClients = new LinkedHashMap<>();
        this.model = new Game();
        this.model.addModelListener(this);
        this.controller = new GameController(this.model);
        this.connectionLock = new ReentrantLock();
    }

    /**
     * Constructs a new ServerImpl object with the specified port, RMIClientSocketFactory, and RMIServerSocketFactory.
     *
     * @param port The port number for the server.
     * @param csf The RMIClientSocketFactory to be used for creating client sockets.
     * @param ssf The RMIServerSocketFactory to be used for creating server sockets.
     * @throws RemoteException if there is a communication-related issue.
     */
    public ServerRMIImpl(int port, RMIClientSocketFactory csf, RMIServerSocketFactory ssf) throws RemoteException {
        super(port, csf, ssf);
        connectedClients = new LinkedHashMap<>();
        this.model = new Game();
        this.model.addModelListener(this);
        this.controller = new GameController(this.model);
        this.connectionLock = new ReentrantLock();
    }

    /**
     * Adds a ServerListener (ServerONE) to this ServerImpl instance.
     *
     * @param serverListener The ServerListener to be added.
     */
    public void addServerListener(ServerListener serverListener){
        this.serverONE = serverListener;
    }

    /**
     * Returns the Client associated with the given Player.
     *
     * @param p The Player for which to retrieve the associated Client.
     * @return The Client associated with the given Player, or null if not found in the map.
     */
    private Client getKeyByValue(Player p) {
        for (Client c : this.connectedClients.keySet()) {
            if (this.connectedClients.get(c).equals(p.getNickname())) {
                return c;
            }
        }
        return null;
    }


    //**********************        SERVER METHODS         ************************************************

    /**
     * Adds a client to the game and starts a periodic ping check for the client's connectivity.
     *
     * @param c The client to be added to the game.
     */
    private void addClientToGame(Client c) {
        connectedClients.put(c, null);
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(() -> {
            try {
                c.ping();
            } catch (RemoteException e) {
                System.err.println(getTime()+" RMI: A client has exited the game");
                serverONE.clientDisconnected(this.connectedClients.remove(c), connectedClientsID.remove(c));
                executorService.shutdown();
            }
        }, 0, PING_PERIOD, TimeUnit.MILLISECONDS);
    }

    /**
     * Handles the client connection process.
     *
     * @param c The client being connected.
     * @throws RemoteException If a remote exception occurs.
     */
    @Override
    public void clientConnection(Client c) throws RemoteException {
        synchronized (connectionLock) {
            int ID = this.serverONE.clientConnected();
            c.setID(ID);
            connectedClientsID.put(c, ID);
            addClientToGame(c);
            connectionLock.notifyAll();
        }
        boolean canPlay;
        try {
            c.warning(Warnings.WAIT);
        } catch (RemoteException e) {
            System.err.println("Unable to advice the client about the loading:" +
                    e.getMessage() + ". Skipping the update...");
        }

        if (!controller.isGameAlreadystarted()) {
                canPlay = true;
                synchronized (first) {
                    if (first.getFirst()) {
                        first.setFirst(false);
                        try {
                            c.askNumberParticipants();
                            while (this.controller.getNumberPlayers() == 0) {
                                Thread.sleep(100);
                            }
                        } catch (RemoteException e) {
                            System.err.println("Unable to ask the number of participants the client: "
                                    + e.getMessage() + ". Skipping the update...");
                            first.setFirst(true);
                            serverONE.clientDisconnected(null,connectedClientsID.get(c));
                            return;
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    if (((ServerOne)serverONE).getConnectedClientsID().indexOf(connectedClientsID.get(c))+1 > controller.getNumberPlayers()) { //((ServerOne) serverONE).getConnectedClients() > controller.getNumberPlayers()
                        canPlay = false;
                    }
                    first.notifyAll();
                }
                if (canPlay) {
                    c.askNickname();
                    while (this.connectedClients.get(c) == null) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    try {
                        c.warning(Warnings.OK_JOINER);
                    } catch (RemoteException e) {
                        System.err.println("Unable to advice the client about the loading:" +
                                e.getMessage() + ". Skipping the update...");
                    }
                    synchronized (first) {
                        this.controller.checkGameInitialization();
                        first.notifyAll();
                    }
                } else
                    c.warning(Warnings.GAME_ALREADY_STARTED);
        } else {
            if(((ServerOne)serverONE).getConnectedClientsID().indexOf(connectedClientsID.get(c))+1 <= controller.getNumberPlayers()){ //((ServerOne) serverONE).getConnectedClients() < this.controller.getNumberPlayers()
                addClientToGame(c);
                c.askExistingNickname();
            } else {
                try {
                    c.warning(Warnings.GAME_ALREADY_STARTED);
                } catch (RemoteException e) {
                    System.err.println("Unable to advice the client about the game being already full:" +
                            e.getMessage() + ". Skipping the update...");
                }
            }
        }
    }

    /**
     * Checks the existing nickname for a client during reconnection to the already
     * existing game.
     * If the nickname is valid and matches an existing player, the client is considered reconnected.
     * The necessary updates and notifications are sent to the client and other players.
     * If the nickname is invalid or does not match any existing player, an appropriate warning is sent to the client.
     * @param c the client requesting reconnection
     * @param nickName the nickname provided by the client for reconnection
     * @throws RemoteException if a remote communication error occurs
     */
    @Override
    public void checkingExistingNickname(Client c, String nickName) throws RemoteException {
        if (controller.checkingExistingNickname(nickName)) {
            // if client is reconnecting I need to open the scanner thread again in the TUI
            System.out.println(getTime()+ANSI_GREEN_BACKGROUND +" RMI: " +  nickName + " RE-connected" + ANSI_RESET);
            c.warning(Warnings.RECONNECTION);

            controller.reconnectedPlayer(nickName);
            connectedClients.put(c, nickName);
            c.setNickname(nickName);

            for(Player player : model.getPlayers())
                if(player.getNickname().equals(nickName)) {
                    c.printGame(new GameView(model, model.getPlayers().get(model.getPlayers().indexOf(player))));
                    break;
                }
        } else
            c.warning(Warnings.INVALID_RECONNECTION_NICKNAME);
    }

    /**
     * Sets the nickname for a client during the initial connection.
     * If the player's nickname is valid and successfully set, the client is considered connected.
     * The necessary updates and notifications are sent to the client and other players.
     * If the player's nickname is invalid or cannot be set, an appropriate warning is sent to the client.
     * @param c the client requesting nickname setting
     * @param nickName the nickname chosen by the client
     * @throws RemoteException if a remote communication error occurs
     */
    @Override
    public void clientNickNameSetting(Client c, String nickName) throws RemoteException {
        if(model.getPlayers()==null) {
            if (this.controller.setPlayerNickname(nickName)) {
                System.out.println(getTime()+ANSI_GREEN_BACKGROUND  +" RMI: " +  nickName + " connected" + ANSI_RESET);
                connectedClients.put(c, nickName);
                c.setNickname(nickName);
            } else {
                try {
                    c.warning(Warnings.INVALID_NICKNAME);
                } catch (RemoteException e) {
                    System.err.println("Unable to advise the client about the invalidation of the chosen nick name:" +
                            e.getMessage() + ". Skipping the update...");
                }
            }
        }
    }

    /**
     *
     * Forwards the tile position to the game controller to handle the tile dropping logic.
     *
     * @param tilePosition the position of the tile to be dropped
     * @throws RemoteException if a remote communication error occurs
     */
    @Override
    public void tileToDrop(int tilePosition) throws RemoteException {
        this.controller.dropTile(tilePosition);
    }

    /**
     *
     * The server forwards the coordinates to the game controller to perform the necessary checks.
     *
     * @param coordinates the coordinates to be checked
     * @throws RemoteException if a remote communication error occurs
     */
    @Override
    public void checkingCoordinates(int[] coordinates) throws RemoteException {
        this.controller.checkCorrectCoordinates(coordinates);
    }

    /**
     * Sets the chosen column by the current player.
     * The server invokes the `setChosenColumn` method on the game controller to set the chosen column for the current player.
     * @param c the column to be set
     * @throws RemoteException if a remote communication error occurs
     */
    @Override
    public void columnSetting(int c) throws RemoteException {
        controller.setChosenColumn(c);
    }

    /**
     * Signals the end of the tile selection phase.
     * The server invokes the `selectionControl` method on the game model to indicate that the tile selection phase has ended.
     * @throws RemoteException if a remote communication error occurs
     */
    @Override
    public void endsSelection() throws RemoteException {
        this.model.selectionControl();
    }

    /**
     * Sets the number of participants for the game.
     * This method is used to set the number of players participating in the game. It synchronizes access to ensure thread safety.
     *
     * @param n the number of participants to set
     * @throws RemoteException if a remote communication error occurs
     */
    @Override
    public synchronized void numberOfParticipantsSetting(int n) throws RemoteException {
        this.controller.setNumberPlayers(n);
    }

    /**
     * Adds a new chat message to the game.
     * This method is used to add a new chat message to the game's chat log.
     * The message is associated with the specified client.
     * It delegates the task to the controller to handle the addition of the chat message.
     *
     * @param client  the client associated with the message
     * @param message the chat message to add
     * @throws RemoteException if a remote communication error occurs
     */
    @Override
    public void newMessage(Client client, String message) throws RemoteException {
        this.controller.addChatMessage(this.connectedClients.get(client), message);
    }

    /**
     * Pong method used for checking server connectivity.
     * This method is invoked by the client to send a "pong" signal to the server,
     * indicating that the client is still connected.
     * It is used for checking the server's connectivity status.
     * The server implementation of this method does not perform any specific action.
     *
     * @throws RemoteException if a remote communication error occurs
     */
    @Override
    public void pong() throws RemoteException {

    }


    //************************      MODEL LISTENER METHODS      ************************************************



    /**
     * Prints the game view for each connected client.
     * This method iterates over the connected clients and sends the game view to each client to be printed.
     * The game view contains the current state of the game for the corresponding player.
     * If a remote communication error occurs while printing the game view for a client,
     * an error message is displayed and the update is skipped for that client.
     */
    @Override
    public void printGame() {
        for (Client c : connectedClients.keySet()) {
            try {
                Player p = null;
                for (int i = 0; i < model.getPlayers().size(); i++) {
                    if (model.getPlayers().get(i).getNickname().equals(connectedClients.get(c))) {
                        p = model.getPlayers().get(i);
                        break;
                    }
                }
                GameView gameView = new GameView(this.model, p);
                c.printGame(gameView);
            } catch (RemoteException e) {
                System.err.println("Unable to print the game:" +
                        e.getMessage() + ". Skipping the update...");
            }
        }

    }

    /**
     * Sends a game warning to the corresponding client.
     * This method sends a game warning to the client associated with the specified player.
     * If the current server instance is responsible for the player,
     * the warning message is sent to the client using the associated client object.
     * If a remote communication error occurs while sending the warning message,
     * an error message is displayed and the update is skipped.
     *
     * @param e             The game warning to send.
     * @param currentPlayer The player for whom the warning is intended.
     */
    @Override
    public void warning(Warnings e, Player currentPlayer) {
        if (isMine()) {
            try {
                Objects.requireNonNull(getKeyByValue(currentPlayer)).warning(e);
            } catch (RemoteException exception) {
                System.err.println("Unable to advise the client about a game warning:" +
                        exception.getMessage() + ". Skipping the update...");
            }
        }
    }

    /**
     * Notifies the clients about a new turn in the game.
     * This method notifies all connected clients about a new turn in the game.
     * If it is not the last turn, each client's `newTurn` method is called with a boolean parameter
     * indicating whether it is their turn.
     * If it is the last turn, each client's `lastTurn` method is called instead.
     * If a remote communication error occurs while notifying the clients,
     * an error message is displayed and the update is skipped.
     *
     * @param currentPlayer The player who is currently taking the turn.
     */
    @Override
    public void newTurn(Player currentPlayer) {
        if (!this.model.isLastTurn()) {
            try {
                for (Client c : this.connectedClients.keySet()) {
                    c.newTurn(this.connectedClients.get(c).equals(model.getCurrentPlayer().getNickname()));
                }
            } catch (RemoteException exception) {
                System.err.println("Unable to start a new turn:" +
                        exception.getMessage() + ". Skipping the update...");
            }
        } else {
            try {
                for (Client c : this.connectedClients.keySet())
                    c.lastTurn(this.connectedClients.get(c).equals(model.getCurrentPlayer().getNickname()));
            } catch (RemoteException exception) {
                System.err.println("Unable to start the last turn:" +
                        exception.getMessage() + ". Skipping the update...");
            }
        }
    }

    /**
     * Asks the current player for their order.
     * This method is used to ask the current player for the order of the chosen tiles in the game.
     * If the client is RMI (determined by the `isMine` method),
     * the `askOrder` method of the corresponding client object is called to ask the player in the UI for the order.
     * If a remote communication error occurs while asking the current player for the order,
     * an error message is displayed and the update is skipped.
     */
    @Override
    public void askOrder() {
        if (isMine()) {
            try {
                Player currentPlayer = this.model.getCurrentPlayer();
                Objects.requireNonNull(getKeyByValue(currentPlayer)).askOrder();
            } catch (RemoteException e) {
                System.err.println("Unable to ask the current player the order:" +
                        e.getMessage() + ". Skipping the update...");
            }
        }
    }

    /**
     * Notifies the clients about the last turn in the game.
     * This method is used to notify all connected clients that it is the last turn in the game. It sends a notification to each client using the `lastTurnNotification` method, passing the nickname of the current player. If a remote communication error occurs while notifying the clients about the last turn, an error message is displayed and the update is skipped.
     */
    @Override
    public void isLastTurn(){
        for(Client c : this.connectedClients.keySet()){
            try {
                c.lastTurnNotification(this.model.getCurrentPlayer().getNickname());
            } catch (RemoteException exception) {
                System.err.println("Unable to advice the clients about the last round beginning:" +
                        exception.getMessage() + ". Skipping the update...");
            }
        }
    }

    /**
     * Asks the current player to choose a column.
     * This method is used to ask the current player to choose a column where to drop their chosen tiles.
     * It first checks if the current player is the local player using the `isMine` method.
     * If the current player is the local player, it retrieves the current player from the model and uses the `askColumn`
     * method of the corresponding client to request the column choice.
     * If a remote communication error occurs while asking the current player for the column,
     * an error message is displayed and the update is skipped.
     */
    @Override
    public void askColumn() {
        if (isMine()) {
            try {
                Player currentPlayer = this.model.getCurrentPlayer();
                Objects.requireNonNull(getKeyByValue(currentPlayer)).askColumn();
            } catch (RemoteException e) {
                System.err.println("Unable to ask the current player the column:" +
                        e.getMessage() + ". Skipping the update...");
            }
        }
    }

    /**
     * Asks the current player to perform an action.
     * This method checks if the current player is the local player by invoking the {@link #isMine()} method.
     * If the current player is the local player,
     * it retrieves the corresponding client from the connected clients map using the current player's nickname.
     * Then, it calls the client's {@code askAction()} method to prompt the current player to perform an action.
     *
     */
    @Override
    public void askAction() {
        if (isMine()) {
            try {
                Player currentPlayer = this.model.getCurrentPlayer();
                Objects.requireNonNull(getKeyByValue(currentPlayer)).askAction();
            } catch (RemoteException e) {
                System.err.println("Unable to ask the current player the action:" +
                        e.getMessage() + ". Skipping the update...");
            }
        }
    }

    /**
     * Sends the final points to all connected clients.
     * This method retrieves the final points for each player from the game model
     * and stores them in a map where the player's nickname is mapped to their points.
     * Then, it iterates over all connected clients and calls their {@code finalPoints} method,
     * passing the map of final points as an argument.
     *
     */
    @Override
    public void finalPoints() {
        Map<String, Integer> finalPoints = new HashMap<>();
        for (Player p : this.model.getPlayers()) {
            finalPoints.put(p.getNickname(), p.getPoints());
        }
        try {
            for (Client client : this.connectedClients.keySet()) {
                client.finalPoints(finalPoints);
            }
        } catch (RemoteException e) {
            System.err.println("Unable to advice the client about the final points:" +
                    e.getMessage() + ". Skipping the update...");
        }


    }

    /**
     * Checks if the current player is the local player.
     * This method checks if the current player, retrieved from the model,
     * matches the nickname of any client in the connected clients map. If a match is found,
     * it means that the current player is the local player, and the method returns true.
     * Otherwise, it returns false.
     *
     * @return true if the current player is the local player, false otherwise.
     */
    private boolean isMine(){
        for(Client c : connectedClients.keySet()){
            if(model.getCurrentPlayer().getNickname().equals(connectedClients.get(c))){
                return true;
            }
        }
        return false;
    }

    /**
     * Notifies all connected clients that the game has started.
     * This method sends a notification to each connected client indicating whether the game has started or not.
     * It iterates over all connected clients and checks if the client corresponds to the current player.
     * If it does, the client's {@code gameStarted} method is called with a parameter value of {@code true};
     * otherwise, it is called with a parameter value of {@code false}.
     * It is used to set the state in the UI if it's the player's turn or not.
     *
     * @param currentPlayer the player who is the current player in the game.
     * @throws RuntimeException if a remote communication error occurs.
     */
    @Override
    public void gameStarted(Player currentPlayer) {
        for (Client client : this.connectedClients.keySet()) {
            if (this.connectedClients.get(client).equals(currentPlayer.getNickname())) {
                try {
                    client.gameStarted(true);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            } else {
                try {
                    client.gameStarted((false));
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * Sends a warning message to a specific client.
     * This method sends a warning message to a client with the specified nickname.
     * It iterates over all connected clients and checks if the nickname matches the client's nickname.
     * If there is a match, the client's {@code warning} method is called
     * with the specified error type as the parameter.
     *
     * @param errorType the type of warning/error to send.
     * @param nickname the nickname of the client to send the warning to.
     * @throws RuntimeException if a remote communication error occurs.
     */
    @Override
    public void warning(Warnings errorType, String nickname) {
        for(Client client : connectedClients.keySet()){
            if(connectedClients.get(client).equals(nickname)) {
                try {
                    client.warning(errorType);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * Notifies all clients about a player disconnection and checks if there are enough remaining players.
     * This method notifies all connected clients about a player disconnection with the specified nickname.
     * It iterates over all connected clients and calls their {@code clientDisconnected} method with the nickname as the parameter.
     * After notifying the clients, it checks the number of remaining connected players.
     * If there is only one player remaining, it sends a warning message to the last remaining client to wait for more players to join.
     *
     * @param nickname the nickname of the disconnected player.
     * @throws RuntimeException if a remote communication error occurs.
     */
    @Override
    public void playerDisconnected(String nickname) {
        if(connectedClients.size() > 0) {
            for (Client client : this.connectedClients.keySet()) {
                try {
                    client.clientDisconnected(nickname);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
            int counter = 0;
            for (Player p : model.getPlayers()) {
                if (p.isConnected()) {
                    counter++;
                }
            }
            if (counter == 1) {
                try {
                    getLastRMIClient().warning(Warnings.WAITING_FOR_MORE_PLAYERS);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * Retrieves the last connected RMI client from the connectedClients map.
     * It uses an iterator to iterate over the entries in the map and returns the key (RMI client)
     * of the first entry if it exists. If the map is empty or there are no entries, it returns null.
     *
     * @return the last connected RMI client, or null if the map is empty or there are no entries.
     */
    private Client getLastRMIClient() {
        Iterator<Map.Entry<Client, String>> iterator = this.connectedClients.entrySet().iterator();
        if (iterator.hasNext()) {
            Map.Entry<Client, String> firstEntry = iterator.next();
            return firstEntry.getKey();
        }
        return null;
    }


    /**
     * Notifies all connected clients that a player has reconnected.
     * This method sends a notification to all connected clients that a player with the specified nickname has reconnected.
     * It iterates over the connectedClients map and calls the client's `clientReconnected` method,
     * passing the nickname as a parameter.
     * If a RemoteException occurs during the process, it is rethrown as a RuntimeException.
     *
     * @param nickname the nickname of the reconnected player
     * @throws RuntimeException if a RemoteException occurs while notifying the clients
     */
    @Override
    public void playerReconnected(String nickname) {
        for(Client client : this.connectedClients.keySet()) {
            try {
                client.clientReconnected(nickname);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Notifies all connected clients about the resumption of a turn.
     * This method sends a notification to all connected clients about the resumption of a turn.
     * It iterates over the `connectedClients` map and calls the client's `resumingTurn` method,
     * passing a boolean value indicating whether it is the turn of the current player or not.
     * If the client's nickname matches the nickname of the current player, the method is called with `true`,
     * otherwise with `false`. If a `RemoteException` occurs during the process,
     * it is rethrown as a `RuntimeException`.
     *
     * @throws RuntimeException if a `RemoteException` occurs while notifying the clients
     */
    @Override
    public void resumingTurn() {
        for (Client client : this.connectedClients.keySet()){
            if(this.connectedClients.get(client).equals(model.getCurrentPlayer().getNickname())) {
                try {
                    client.resumingTurn(true);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
            else {
                try {
                    client.resumingTurn(false);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    private String getTime(){
        return new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(Calendar.getInstance().getTime());
    }
}
