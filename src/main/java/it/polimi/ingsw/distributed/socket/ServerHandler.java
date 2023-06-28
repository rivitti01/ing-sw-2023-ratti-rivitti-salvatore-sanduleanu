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
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import static it.polimi.ingsw.view.Colors.*;

public class ServerHandler implements Server,Runnable, ModelListener {
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
    public enum state{
        COORD, COLUMN, ORDER
    }
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
    @Override
    public void run() {
        //System.out.println("SOCKET: "+"accepted connection: "+ socket.getPort());
        try {
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());

            try {
                setUp();//TODO: sconnettere il client prima di chiedergli il nickname
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
        switch (response.getClass().getSimpleName()){
            case "int[]" -> {
                if (currentState == state.COORD) {
                    int[] coordinates = (int[]) response;
                    controller.checkCorrectCoordinates(coordinates);
                }
            }
            case "Warnings" -> {
                Warnings warning = (Warnings) response;
                if (warning.equals(Warnings.END_SELECTION)){
                    model.selectionControl();
                }

            }
            case "Integer" -> {
                if(currentState == state.COLUMN) {
                    int column = (int) response;
                    controller.setChosenColumn(column);
                }else if(currentState == state.ORDER) {
                    int order = (int) response;
                    controller.dropTile(order);
                }
            }
            case "String" ->{
                controller.addChatMessage(nickname,(String) response);
            }
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
                //System.out.println("SOCKET: "+Thread.currentThread().getName() + " is waiting for the creator to set the number of players");
                synchronized (this.lock) {
                    //System.out.println("SOCKET: "+Thread.currentThread().getName() + " is not waiting anymore");
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
            //se tutti i giocatori sono connessi e la partita è iniziata allora saluto il giocatore e chiudo la connessione
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
        //System.out.println("SOCKET: "+socket.getPort() + ": Nickname = " + nickname);
        if (nickname != null) {
            if (model.isStart()) {
                if (controller.playerOffline()) {
                    if (controller.checkingExistingNickname(nickname)) { //playerIsBack(nickname)

                        controller.reconnectedPlayer(nickname); //setta a true il connected del player e comunica a tutti i thread che un player si è riconesso
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
                    /*out.writeObject(Warnings.OK_JOINER);
                    out.reset();
                    out.flush();*/
                    if (controller.getPlayers().size() == controller.getNumberPlayers()) {
                        synchronized (this.lock) {
                            controller.checkGameInitialization();
                            this.lock.notifyAll();
                        }
                        System.out.println(getTime()+ANSI_GREEN_BACKGROUND +" SOCKET: "+  this.nickname+" connected"+ ANSI_RESET);
                        return;
                    } else {
                        out.writeObject(Warnings.OK_JOINER);//prima era Warnings.OK_WAIT
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
        int numberPlayers = (int) in.readObject();
        numberOfParticipantsSetting(numberPlayers);
    }
    public String getNickname(){
        return nickname;
    }

    @Override
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

    @Override
    public void newMessage(Client client, String message) {


    }

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

    @Override
    public void newTurn(Player currentPlayer) {
        if(model.getCurrentPlayer().getNickname().equals(nickname)){// == currentPlayer.getNickname().equals(nickname)
            try {
                //System.out.println("Client "+socket.getPort()+" YOUR TURN "+ "Thread nick : "+nickname+" current player: "+currentPlayer.getNickname());
                out.writeObject(Warnings.YOUR_TURN);
                out.reset();
                out.flush();
                currentState = state.COORD;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            try {
                //System.out.println("Client "+socket.getPort()+" NOT YOUR TURN "+ "Thread nick: "+nickname+" current player: "+currentPlayer.getNickname());
                out.writeObject(Warnings.NOT_YOUR_TURN);
                out.reset();
                out.flush();
                currentState = state.COORD;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

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
    @Override
    public void clientConnection(Client c) throws RemoteException {

    }

    @Override
    public void clientNickNameSetting(Client c, String nickName) throws RemoteException {//Server ->

    }



    @Override
    public void pong() throws RemoteException {

    }





    @Override
    public void gameStarted(Player currentPlayer) {
        newTurn(currentPlayer);
    }

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

    @Override
    public void onePlayerLeft(Player theOnlyPlayerLeft, int countdownToEnd) {
        if (theOnlyPlayerLeft.getNickname().equals(nickname)){
            if (countdownToEnd != 0){
                try {
                    System.out.println("One player left");
                    out.writeObject(Warnings.WAITING_FOR_MORE_PLAYERS);
                    out.reset();
                    out.flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        }


    }

    @Override
    public void playerDisconnected(String nickname) {
        try {
            out.writeObject(Warnings.CLIENT_DISCONNECTED);
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

    @Override
    public void playerReconnected(String nickname) {
        try {
            if (model.getCurrentPlayer().getNickname().equals(this.nickname)){
                out.writeObject(Warnings.YOUR_TURN);
                out.reset();
                out.flush();
            }else {
                out.writeObject(Warnings.NOT_YOUR_TURN);
                out.reset();
                out.flush();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void tileToDrop(int tilePosition) throws RemoteException {

    }

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

    public int getClientID() {
        return clientID;
    }

    public void setClientID(int clientID) {
        this.clientID = clientID;
    }

    @Override
    public void checkingCoordinates(int[] coordinates) throws RemoteException {

    }
    @Override
    public void checkingExistingNickname(Client c, String nickName) throws RemoteException {

    }

    @Override
    public void columnSetting(int i) throws RemoteException {

    }

    @Override
    public void endsSelection() throws RemoteException {

    }

    public String getTime(){
        return new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(Calendar.getInstance().getTime());
    }




}
