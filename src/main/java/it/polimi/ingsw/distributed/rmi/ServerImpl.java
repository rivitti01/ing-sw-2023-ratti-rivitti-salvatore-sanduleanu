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
import java.util.Map;

public class ServerImpl extends UnicastRemoteObject implements Server, ModelListener {
    private final Game model;
    private GameController controller;
    private final Map<String, Client> connectedClients;
    private int numPartecipants;
    private boolean gameAlreadyStarted;

    public ServerImpl() throws RemoteException {
        super();
        connectedClients = new HashMap<>();
        this.gameAlreadyStarted = false;
        this.model = new Game();
        this.model.addModelListener(this);
    }
    public ServerImpl(int port) throws RemoteException {
        super(port);
        connectedClients = new HashMap<>();
        this.gameAlreadyStarted = false;
        this.model = new Game();
        this.model.addModelListener(this);
    }
    public ServerImpl(int port, RMIClientSocketFactory csf, RMIServerSocketFactory ssf) throws RemoteException {
        super(port, csf, ssf);
        connectedClients = new HashMap<>();
        this.gameAlreadyStarted = false;
        this.model = new Game();
        this.model.addModelListener(this);
    }


    public boolean nameControl(String newName){
        for (String s: connectedClients.keySet()){
            if (s.equals(newName)) return false;
        }
        return true;
    }


    //********** SERVER METHODS
    @Override
    public void clientConnection(Client c, String nickName) {
        if (!this.gameAlreadyStarted) {
            if (nameControl(nickName)) {
                connectedClients.put(nickName, c);
                if (connectedClients.size()==1) {
                    try {
                        c.askNumberParticipants();
                    } catch (RemoteException e){
                        System.err.println("Unable to ask the number of participants the client: "
                                + e.getMessage() + ". Skipping the update...");
                    }
                }
                if (connectedClients.size() == this.numPartecipants) {
                    this.gameAlreadyStarted = true;
                    this.controller = new GameController(this.model, this.connectedClients, this.numPartecipants);
                }
            } else {
                try {
                    c.error(Warnings.INVALID_NICKNAME);
                } catch (RemoteException e){
                    System.err.println("Unable to advise the client about the invalidation of the chosen nick name:" +
                            e.getMessage() + ". Skipping the update...");
                }
            }
        } else {
            try {
                c.error(Warnings.GAME_ALREADY_STARTED);
            } catch (RemoteException e){
                System.err.println("Unable to advice the client about the game being already full:" +
                        e.getMessage() + ". Skipping the update...");
            }
        }
    }
    @Override
    public void tileToDrop(int tilePosition) {
        this.controller.dropTile(tilePosition);
    }
    @Override
    public void checkingCoordinates(int[] coordinates) {
        this.controller.checkCorrectCoordinates(coordinates);
    }
    @Override
    public void columnSetting(int c) {
        controller.setChosenColumn(c);
    }
    @Override
    public void endsSelection() {
        this.model.selectionControl();
    }

    @Override
    public void numberOfParticipantsSetting(int n) {
        this.numPartecipants = n;
    }
    //************ MODEL LISTENER METHODS
    @Override
    public void printGame() {
        for(String s:  connectedClients.keySet()){
            for (Player p: this.model.getPlayers()){
                if (s.equals(p.getNickname())){
                    try {
                        connectedClients.get(s).printGame(new GameView(this.model, p));
                    } catch (RemoteException e){
                        System.err.println("Unable to print the game:" +
                                e.getMessage() + ". Skipping the update...");
                    }
                }
            }
        }
    }
    @Override
    public void error(Warnings e, Player currentPlayer) {
        String nickName = currentPlayer.getNickname();
        try {
            this.connectedClients.get(nickName).error(e);
        } catch (RemoteException exception){
            System.err.println("Unable to advise the client about a game error:" +
                    exception.getMessage() + ". Skipping the update...");
        }
    }
    @Override
    public void newTurn(Player currentPlayer) {
        String nickName = currentPlayer.getNickname();
        if(!this.model.isLastTurn()) {
            try {
                connectedClients.get(nickName).newTurn();
            } catch (RemoteException exception) {
                System.err.println("Unable to start a new turn:" +
                        exception.getMessage() + ". Skipping the update...");
            }
        }
        else {
            try {
                connectedClients.get(nickName).lastTurn();
            } catch (RemoteException exception) {
                System.err.println("Unable to start the last turn:" +
                        exception.getMessage() + ". Skipping the update...");
            }
        }
    }
    @Override
    public void askOrder(){
        try {
            this.connectedClients.get(this.model.getCurrentPlayer().getNickname()).askOrder();
        } catch (RemoteException e){
            System.err.println("Unable to ask the current player the order:" +
                    e.getMessage() + ". Skipping the update...");
        }
    }
    @Override
    public void isLastTurn(){
        for(String nickname : this.connectedClients.keySet()){
            try {
                this.connectedClients.get(nickname).lastTurnNotification(this.model.getCurrentPlayer().getNickname());
            } catch (RemoteException exception) {
                System.err.println("Unable to advice the clients about the last round beginning:" +
                        exception.getMessage() + ". Skipping the update...");
            }
        }
    }

    @Override
    public void askColumn() {
        try {
            this.connectedClients.get(this.model.getCurrentPlayer().getNickname()).askColumn();
        } catch (RemoteException e){
            System.err.println("Unable to ask the current player the column:" +
                    e.getMessage() + ". Skipping the update...");
        }
    }
}
