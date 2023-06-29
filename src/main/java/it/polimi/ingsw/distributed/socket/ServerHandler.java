package it.polimi.ingsw.distributed.socket;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.distributed.*;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.GameView;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.util.ModelListener;
import it.polimi.ingsw.util.Warnings;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static it.polimi.ingsw.util.Costants.TIMEOUT_DURATION;
import static it.polimi.ingsw.view.Colors.*;

public class ServerHandler implements Runnable, ModelListener {
    private String nickname;
    private Socket socket;
    private Game model;
    private GameController controller;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private boolean creator;
    private state currentState;
    private final First lock;
    private ServerListener serverOne;
    private int clientID;
    private ScheduledFuture<?> timerTask;
    private ScheduledExecutorService timerExecutor;
    private boolean interruptedTimer = false;
    private Timer timer;
    public enum state{
        COORD, COLUMN, ORDER
    }

    /**
     * Constructs a new ServerHandler object with the provided parameters.
     *
     * @param socket the socket representing the connection with the client
     * @param model the game model
     * @param controller the game controller
     * @param creator indicates whether the ServerHandler is for the creator of the game
     * @param lock the lock object used for synchronization
     * @param serverOne the server listener for communication with the server
     */
    public ServerHandler(Socket socket, Game model, GameController controller, boolean creator, First lock, ServerListener serverOne){
        this.socket = socket;
        this.model = model;
        this.controller = controller;
        this.model.addModelListener(this);
        this.creator = creator;
        currentState = state.COORD;
        this.lock = lock;
        this.serverOne = serverOne;
    }

    /**
     * Starts the execution of the ServerHandler in a separate thread.
     * This method is called when the thread is started.
     * It reads incoming objects from the client and analyzes them.
     * If a communication or class not found error occurs, appropriate actions are taken.
     */
    @Override
    public void run() {
        try {
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());

            try {
                setUp();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            while (true) {
                analyzeMessage(in.readObject());
            }
        } catch (IOException e) {
            System.err.println(getTime()+" SOCKET: "+ nickname+" disconnected");
            disconnectedClient();
            Thread.currentThread().interrupt();
        }
        catch (ClassNotFoundException  e) {
            System.err.println("SOCKET: "+"Class not found");
        }
    }
    private void analyzeMessage(Object response) throws IOException {
        if (response != null) {
            switch (response.getClass().getSimpleName()) {
                case "int[]" -> {
                    if (currentState == state.COORD) {
                        int[] coordinates = (int[]) response;
                        controller.checkCorrectCoordinates(coordinates);
                    }
                }
                case "Warnings" -> {
                    Warnings warning = (Warnings) response;
                    if (warning.equals(Warnings.END_SELECTION)) {
                        model.selectionControl();
                    }

                }
                case "Integer" -> {
                    if (currentState == state.COLUMN) {
                        int column = (int) response;
                        controller.setChosenColumn(column);
                    } else if (currentState == state.ORDER) {
                        int order = (int) response;
                        controller.dropTile(order);
                    }
                }
                case "String" -> {
                    String message = (String) response;
                    if (message.equals("PONG")) {
                        //System.out.println(getTime() + " SOCKET: " + nickname + " PONG");
                        receivedPong();
                        break;
                    }
                    controller.addChatMessage(nickname, (String) response);
                }
            }
        }
    }

    /**
     * Starts the ping timer.
     * If a timer is already running, it will be canceled and a new timer will be created.
     */
    public void start() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        timer = new Timer();
        //System.out.println("TImer started "+ nickname);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                disconnectedClient();
                interrupt();
                Thread.currentThread().interrupt();
            }
        }, 5000); // 5-seconds timeout
    }

    /**
     * Receives a ping from the client and resets the timer.
     * If a timer is running, it will be canceled and a new timer will be started.
     */
    public void receivedPong() {
        if (timer != null) {
            timer.cancel();
            //System.out.println("TImer cancel "+ nickname);
        }
        timer = null;
        start();
    }

    /**
     * Interrupts the timer.
     * If a timer is running, it will be canceled.
     */
    public void interrupt() {
        if (timer != null) {
            timer.cancel();
        }
    }

    private void setUp() throws IOException, ClassNotFoundException, InterruptedException {

        if (creator) {
            out.writeObject(Warnings.SET_NUMBER_PLAYERS);
            out.reset();
            out.flush();
            synchronized (this.lock) {
                waitAndSetNumberPlayers();
                this.lock.notifyAll();
            }
        } else {
            out.writeObject(Warnings.WAIT);
            out.reset();
            out.flush();
            if (controller.getNumberPlayers() == 0) {
                synchronized (this.lock) {
                    if (((ServerOne)serverOne).getConnectedClientsID().indexOf(clientID)+1 > controller.getNumberPlayers()){
                        disconnectedClient();
                        socket.close();
                        Thread.currentThread().interrupt();
                    }
                    this.lock.notifyAll();
                }
            }
        }
        if (model.isStart()){
            boolean playerOffline = false;
            for(Player player : model.getPlayers()){
                if(!player.isConnected()){
                    playerOffline = true;
                    out.writeObject(Warnings.ASK_RECONNECTION_NICKNAME);
                    out.reset();
                    out.flush();
                    break;
                }
            }
            if (!playerOffline){
                out.writeObject(Warnings.GAME_ALREADY_STARTED);
                out.reset();
                out.flush();
                model.removeModelListener(this);
                socket.close();
            }
        }else{
            out.writeObject(Warnings.ASK_NICKNAME);
            out.reset();
            out.flush();
        }
        waitAndSetNickname();
    }
    private void waitAndSetNickname() throws IOException, ClassNotFoundException, InterruptedException {

        String nickname = (String) in.readObject();
        if (nickname != null) {
            if (model.isStart()) {
                if (controller.playerOffline()) {
                    if (controller.checkingExistingNickname(nickname)) {

                        controller.reconnectedPlayer(nickname);
                        System.out.println(getTime()+ANSI_GREEN_BACKGROUND +" SOCKET: "+  nickname + " RE-connected" + ANSI_RESET);
                        this.nickname = nickname;
                        out.writeObject(Warnings.RECONNECTION);
                        out.reset();
                        out.flush();
                        printGame();
                        return;
                    } else {
                        out.writeObject(Warnings.INVALID_RECONNECTION_NICKNAME);
                        out.reset();
                        out.flush();
                        waitAndSetNickname();
                        return;
                    }
                } else {
                    serverOne.clientDisconnected(this.nickname, this.clientID);
                    out.writeObject(Warnings.GAME_ALREADY_STARTED);
                    out.reset();
                    out.flush();
                    model.removeModelListener(this);
                    socket.close();
                    Thread.currentThread().interrupt();
                }
            } else {
                if (controller.setPlayerNickname(nickname)) {
                    this.nickname = nickname;

                    if (controller.getPlayers().size() == controller.getNumberPlayers()) {
                        synchronized (this.lock) {
                            controller.checkGameInitialization();
                            this.lock.notifyAll();
                        }
                        System.out.println(getTime()+ANSI_GREEN_BACKGROUND +" SOCKET: "+  this.nickname+" connected"+ ANSI_RESET);
                        return;
                    } else {
                        out.writeObject(Warnings.OK_JOINER);
                        out.reset();
                        out.flush();
                        while (true) {
                            synchronized (this.lock) {
                                if (controller.getPlayers().size() < controller.getNumberPlayers())
                                    this.lock.wait();
                                else {
                                    lock.notifyAll();
                                    break;
                                }
                            }
                        }
                    }
                } else {
                    out.writeObject(Warnings.INVALID_NICKNAME);
                    out.reset();
                    out.flush();
                    waitAndSetNickname();
                }
            }
        }
        System.out.println(getTime()+" SOCKET: "+ANSI_GREEN_BACKGROUND+this.nickname+ " connected" + ANSI_RESET);
    }
    private void waitAndSetNumberPlayers() throws IOException, ClassNotFoundException {
        Object response = in.readObject();
        if (response instanceof Integer) {
            int numberPlayers = (int) response;
            numberOfParticipantsSetting(numberPlayers);
        }
    }

    /**
     * Returns the nickname associated with this ServerHandler.
     *
     * @return the nickname associated with this ServerHandler
     */
    public String getNickname(){
        return nickname;
    }


    /**
     * Sets the number of participants for the game and performs the necessary actions based on the number of players.
     *
     * @param n the number of participants to be set
     * @throws RemoteException if a remote communication error occurs during the method execution
     */
    public void numberOfParticipantsSetting(int n) throws RemoteException {
        System.out.println(getTime()+" SOCKET:"+socket.getPort()+": Number of players = "+n);
        if (n > 1 && n < 5){
            controller.setNumberPlayers(n);
            if (controller.getNumberPlayers()==controller.getPlayers().size() && !model.isStart()){
                controller.initializeModel();
            }
        }else {
            try {
                out.writeObject(Warnings.INVALID_NUMBER_PLAYERS);
                out.reset();
                out.flush();
                waitAndSetNumberPlayers();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("Client"+socket.getPort()+ ": numero giocatori assegnato -> "+n);
    }
    private void disconnectedClient() {
        try {
            out.writeObject(Warnings.GAME_ALREADY_STARTED);
            out.reset();
            out.flush();
        } catch (IOException e) {
        }
        model.removeModelListener(this);
        serverOne.clientDisconnected(this.nickname, this.clientID);
    }

    /**
     * Sends the game view of the current player to the client.
     * The game view contains the current state of the game for the player.
     * The game view is sent through the output stream to the client.
     * If an IOException occurs during the communication, the stack trace is printed.
     */
    @Override
    public void printGame() {
        for (Player p : model.getPlayers()){
            if (p.getNickname().equals(nickname)){
                GameView gameView = new GameView(model, p);
                try {
                    out.writeObject(gameView);
                    out.reset();
                    out.flush();
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * Sends a warning message to the client player.
     *
     * @param e             the warning type to be sent
     * @param currentPlayer the current player object associated with the warning
     */
    @Override
    public void warning(Warnings e, Player currentPlayer) {
        if (model.getCurrentPlayer().getNickname().equals(nickname)){
            if (e.equals(Warnings.MAX_TILES_CHOSEN)){
                currentState = state.COLUMN;
            }
            try {
                out.writeObject(e);
                out.reset();
                out.flush();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    /**
     * Notifies the client player about a new turn.
     *
     * @param currentPlayer the current player object associated with the new turn
     */
    @Override
    public void newTurn(Player currentPlayer) {
        if(model.getCurrentPlayer().getNickname().equals(nickname)){// == currentPlayer.getNickname().equals(nickname)
            try {
                out.writeObject(Warnings.YOUR_TURN);
                out.reset();
                out.flush();
                currentState = state.COORD;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            try {
                out.writeObject(Warnings.NOT_YOUR_TURN);
                out.reset();
                out.flush();
                currentState = state.COORD;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Asks the client player to choose the order tiles to drop in the shelf.
     * This method is called when it is the client player's turn.
     * It sends a request to the client player to choose the order of play.
     * The client player should respond with the chosen order.
     *
     * @throws RuntimeException if an IOException occurs during the communication
     */
    @Override
    public void askOrder() {
        if (model.getCurrentPlayer().getNickname().equals(nickname)){
            try {
                out.writeObject(Warnings.ASK_ORDER);
                out.reset();
                out.flush();
                currentState = state.ORDER;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    /**
     * Notifies the client player that it is the last turn of the game.
     * This method sends a notification to the client player indicating that the current turn is the last turn of the game.
     * The client player is also provided with the nickname of the current player.
     */
    @Override
    public void isLastTurn() {
        try {
            out.writeObject(Warnings.LAST_TURN_NOTIFICATION);
            out.reset();
            out.flush();
            out.writeObject(model.getCurrentPlayer().getNickname());
            out.reset();
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Asks the client player to choose a column for placing a tile.
     * This method is called when it is the client player's turn.
     * It sends a request to the client player to choose a column for placing a tile.
     * The client player should respond with the chosen column.
     */
    @Override
    public void askColumn() {
        if (model.getCurrentPlayer().getNickname().equals(nickname)) {
            try {
                out.writeObject(Warnings.ASK_COLUMN);
                out.reset();
                out.flush();
                currentState = state.COLUMN;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    /**
     * Asks the client player to choose their next action.
     * This method is called when it is the client player's turn.
     * It sends a request to the client player to choose their next action.
     * The client player should respond with their chosen action.
     *
     * @throws RuntimeException if an IOException occurs during the communication
     */
    @Override
    public void askAction() {
        if (model.getCurrentPlayer().getNickname().equals(nickname)){
            try {
                out.writeObject(Warnings.CONTINUE_TO_CHOOSE);
                out.reset();
                out.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Notifies the client player that the game has started.
     * This method is called when the game starts. It sends a notification to the client player indicating that the game has started.
     * It invokes the `newTurn()` method to inform the client player that it is their turn.
     *
     * @param currentPlayer the player whose turn it is
     */
    @Override
    public void gameStarted(Player currentPlayer) {
        newTurn(currentPlayer);
    }

    /**
     * Sends a warning message to the client player.
     * This method is called to notify the client player about a specific warning.
     *
     * @param errorType the type of warning to send
     * @param nickname  the nickname of the client player
     * @throws RuntimeException if an IOException occurs during the communication
     */
    @Override
    public void warning(Warnings errorType, String nickname) {
        if (nickname.equals(this.nickname)){
            try {
                out.writeObject(errorType);
                out.reset();
                out.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    /**
     * Notifies the client players that a player has disconnected from the game.
     * This method is called when a player disconnects, and it sends a message to all client players
     * to inform them about the disconnection. If there is only one player remaining in the game,
     * it sends a message to the remaining player to wait for more players to join.
     *
     * @param nickname the nickname of the disconnected player
     * @throws RuntimeException if an IOException occurs during the communication
     */
    @Override
    public void playerDisconnected(String nickname) {
        try {
            out.writeObject(nickname+"_DISCONNECTED");
            out.reset();
            out.flush();
            int counter = 0;
            for (Player p : model.getPlayers()){
                if (p.isConnected()){
                    counter++;
                }
            }
            if (counter == 1){
                out.writeObject(Warnings.WAITING_FOR_MORE_PLAYERS);
                out.reset();
                out.flush();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Notifies the client players that a player has reconnected to the game.
     * This method is called when a player reconnects, and it sends a message to all client players
     * to inform them about the reconnection.
     *
     * @param nickname the nickname of the reconnected player
     * @throws RuntimeException if an IOException occurs during the communication
     */
    @Override
    public void playerReconnected(String nickname) {
        try {
            out.writeObject(nickname+"_RECONNECTED");
            out.reset();
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Notifies the client player that their turn is being resumed.
     * This method is called when a player's turn is being resumed after a reconnection,
     * and it sends a message to the client player to inform them about the resumption of their turn.
     *
     * @throws RuntimeException if an IOException occurs during the communication
     */
    @Override
    public void resumingTurn() {
        try {
            if (model.getCurrentPlayer().getNickname().equals(this.nickname)){
                out.writeObject(Warnings.RESUMING_TURN);
                out.reset();
                out.flush();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    /**
     * Sends the final points to the client player.
     * This method is called at the end of the game to send the final points of all players to the client player.
     * The final points are represented as a map with player nicknames as keys and their corresponding points as values.
     *
     * @throws RuntimeException if an IOException occurs during the communication
     */
    @Override
    public void finalPoints() throws RuntimeException {
        Map<String, Integer> finalPoints = new HashMap<>();
        for(Player p: this.model.getPlayers()) {
            finalPoints.put(p.getNickname(), p.getPoints());
        }
        try {
            out.writeObject(finalPoints);
            out.reset();
            out.flush();
        } catch (RemoteException e) {
            System.err.println("Unable to advice the client about the final points:" +
                    e.getMessage() + ". Skipping the update...");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    /**
     * Sets the ID of the client.
     *
     * @param clientID the ID of the client to be set
     */
    public void setClientID(int clientID) {
        this.clientID = clientID;
    }

    private String getTime(){
        return new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(Calendar.getInstance().getTime());
    }




}
