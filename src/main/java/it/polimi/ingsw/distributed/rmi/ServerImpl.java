package it.polimi.ingsw.distributed.rmi;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.distributed.Client;
import it.polimi.ingsw.distributed.Server;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.util.Warnings;
import it.polimi.ingsw.util.ModelListener;

import java.rmi.RemoteException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

public class ServerImpl extends UnicastRemoteObject implements Server, ModelListener {
    private final Game model;
    private final GameController controller;
    private final LinkedHashMap<Client, String> connectedClients;
    private int numParticipants;
    private boolean gameAlreadyStarted;
    private final ReentrantLock connectionLock;


    public ServerImpl() throws RemoteException {
        super();
        connectedClients = new LinkedHashMap<>();
        this.gameAlreadyStarted = false;
        this.model = new Game();
        this.model.addModelListener(this);
        this.controller = new GameController(this.model);
        this.numParticipants = 0;
        this.connectionLock = new ReentrantLock();
    }
    public ServerImpl(int port) throws RemoteException {
        super(port);
        connectedClients = new LinkedHashMap<>();
        this.gameAlreadyStarted = false;
        this.model = new Game();
        this.model.addModelListener(this);
        this.controller = new GameController(this.model);
        this.numParticipants = 0;
        this.connectionLock = new ReentrantLock();
    }
    public ServerImpl(int port, RMIClientSocketFactory csf, RMIServerSocketFactory ssf) throws RemoteException {
        super(port, csf, ssf);
        connectedClients = new LinkedHashMap<>();
        this.gameAlreadyStarted = false;
        this.model = new Game();
        this.model.addModelListener(this);
        this.controller = new GameController(this.model);
        this.numParticipants = 0;
        this.connectionLock = new ReentrantLock();
    }
    private Client getKeyByValue(Player p) {
        for (Client c : this.connectedClients.keySet()) {
            if (this.connectedClients.get(c).equals(p.getNickname())) {
                return c;
            }
        }
        return null; // valore non trovato nella mappa
    }


    //********** SERVER METHODS
    @Override
    public void clientConnection(Client c) throws RemoteException {
        boolean canPlay = false;
        try {
            c.warning(Warnings.WAIT);
        } catch (RemoteException e){
            System.err.println("Unable to advice the client about the loading:" +
                    e.getMessage() + ". Skipping the update...");
        }
        if(!this.gameAlreadyStarted) {
            connectionLock.lock();
            try {
                if (!this.gameAlreadyStarted){
                    canPlay = true;
                    connectedClients.put(c, null);
                    if (connectedClients.size() == 1) {
                        try {
                            c.askNumberParticipants();
                        } catch (RemoteException e) {
                            System.err.println("Unable to ask the number of participants the client: "
                                    + e.getMessage() + ". Skipping the update...");
                        }
                    } else if (this.connectedClients.size() == this.numParticipants) {
                        this.gameAlreadyStarted = true;
                    }
                } else {
                    try {
                        c.warning(Warnings.GAME_ALREADY_STARTED);
                    } catch (RemoteException e){
                        System.err.println("Unable to advice the client about the game being already full:" +
                                e.getMessage() + ". Skipping the update...");
                    }
                }
            } finally {
                connectionLock.unlock();
            }
            if (canPlay){
                c.askNickname();
                try {
                    c.warning(Warnings.WAIT);
                } catch (RemoteException e){
                    System.err.println("Unable to advice the client about the loading:" +
                            e.getMessage() + ". Skipping the update...");
                }
                this.controller.checkGameInitialization();
            }
        } else {
            try {
                c.warning(Warnings.GAME_ALREADY_STARTED);
            } catch (RemoteException e){
                System.err.println("Unable to advice the client about the game being already full:" +
                        e.getMessage() + ". Skipping the update...");
            }
        }
    }
    @Override
    public void clientNickNameSetting(Client c, String nickName) throws RemoteException {
        if (this.controller.setPlayerNickname(nickName)) {
            connectedClients.put(c, nickName);
        } else {
            try {
                c.warning(Warnings.INVALID_NICKNAME);
            } catch (RemoteException e){
                System.err.println("Unable to advise the client about the invalidation of the chosen nick name:" +
                        e.getMessage() + ". Skipping the update...");
            }
        }
    }
    @Override
    public void tileToDrop(int tilePosition) throws RemoteException{
        this.controller.dropTile(tilePosition);
    }
    @Override
    public void checkingCoordinates(int[] coordinates) throws RemoteException{
        this.controller.checkCorrectCoordinates(coordinates);
    }
    @Override
    public void columnSetting(int c) throws RemoteException{
        controller.setChosenColumn(c);
    }
    @Override
    public void endsSelection() throws RemoteException{
        this.model.selectionControl();
    }

    @Override
    public synchronized void numberOfParticipantsSetting(int n) throws RemoteException {
        this.numParticipants = n;
        this.controller.setNumberPlayers(n);
    }

    @Override
    public void chatTyped(Client client) throws RemoteException {
        this.controller.playerTypedChat(connectedClients.get(client));
    }

    @Override
    public void newMessage(String message, Client sender) throws RemoteException {
        this.controller.newMessage(connectedClients.get(sender), message);
    }

    @Override
    public void chatAvailable() {
        for(Client c:  connectedClients.keySet()){
            for (Player p: this.model.getPlayers()){
                if (connectedClients.get(c).equals(p.getNickname())){
                    try {
                        c.chatAvailable();
                    } catch (RemoteException e){
                        System.err.println("Unable to make the chat available:" +
                                e.getMessage() + ". Skipping the update...");
                    }
                }
            }
        }
    }

    @Override
    public void printChat() {
        for (Player p: this.model.getPlayers()){
            for(Client c:  connectedClients.keySet()){
                if (connectedClients.get(c).equals(p.getNickname()) && p.isChatting()){
                    try {
                        c.printChat(new ChatView(this.model));
                    } catch (RemoteException e){
                        System.err.println("Unable to print the chat:" +
                                e.getMessage() + ". Skipping the update...");
                    }
                }
            }
        }
    }


    //************ MODEL LISTENER METHODS
    @Override
    public void printGame() {
        for(Client c:  connectedClients.keySet()){
            for (Player p: this.model.getPlayers()){
                if (connectedClients.get(c).equals(p.getNickname()) && !p.isChatting()){
                    try {
                        c.printGame(new GameView(this.model, p));
                    } catch (RemoteException e){
                        System.err.println("Unable to print the game:" +
                                e.getMessage() + ". Skipping the update...");
                    }
                }
            }
        }
    }
    @Override
    public void warning(Warnings e, Player currentPlayer) {
        try {
            Objects.requireNonNull(getKeyByValue(currentPlayer)).warning(e);
        } catch (RemoteException exception ){
            System.err.println("Unable to advise the client about a game warning:" +
                    exception.getMessage() + ". Skipping the update...");
        }
    }
    @Override
    public void newTurn(Player currentPlayer) {
        if(!this.model.isLastTurn()) {
            try {
                Objects.requireNonNull(getKeyByValue(currentPlayer)).newTurn();
            } catch (RemoteException exception) {
                System.err.println("Unable to start a new turn:" +
                        exception.getMessage() + ". Skipping the update...");
            }
        }
        else {
            try {
                Objects.requireNonNull(getKeyByValue(currentPlayer)).lastTurn();
            } catch (RemoteException exception) {
                System.err.println("Unable to start the last turn:" +
                        exception.getMessage() + ". Skipping the update...");
            }
        }
    }
    @Override
    public void askOrder(){
        Player currentPlayer = this.model.getCurrentPlayer();
        try {
            if(currentPlayer.getChosenTiles().size() > 1)
                Objects.requireNonNull(getKeyByValue(currentPlayer)).askOrder();
            else{
                this.controller.dropTile(1);
            }
        } catch (RemoteException e){
                System.err.println("Unable to ask the current player the order:" +
                        e.getMessage() + ". Skipping the update...");
        }
    }
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
    @Override
    public void askColumn() {
        try {
            Player currentPlayer = this.model.getCurrentPlayer();
            Objects.requireNonNull(getKeyByValue(currentPlayer)).askColumn();
        } catch (RemoteException e){
            System.err.println("Unable to ask the current player the column:" +
                    e.getMessage() + ". Skipping the update...");
        }
    }
    @Override
    public void askAction() {
        try {
            Player currentPlayer = this.model.getCurrentPlayer();
            Objects.requireNonNull(getKeyByValue(currentPlayer)).askAction();
        } catch (RemoteException e){
            System.err.println("Unable to ask the current player the action:" +
                    e.getMessage() + ". Skipping the update...");
        }
    }
    @Override
    public void finalPoints(){
        Map<String, Integer> finalPoints = new HashMap<>();
        for(Player p: this.model.getPlayers()) {
            finalPoints.put(p.getNickname(), p.getPoints());
            try {
                Objects.requireNonNull(getKeyByValue(p)).finalPoints(finalPoints);
            } catch (RemoteException e) {
                System.err.println("Unable to advice the client about the final points:" +
                        e.getMessage() + ". Skipping the update...");
            }
        }
    }
}
