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
import java.util.List;
import java.util.Map;

public class ServerImpl extends UnicastRemoteObject implements Server, ModelListener {
    private Game model;
    private GameController controller;
    private Map<String, Client> connectedClients;
    private int numPartecipants;
    private boolean gameAlreadyStarted;

    public ServerImpl() throws RemoteException {
        connectedClients = new HashMap<>();
    }
    public ServerImpl(int port) throws RemoteException {
        super(port);
        connectedClients = new HashMap<>();
        this.gameAlreadyStarted = false;
    }
    public ServerImpl(int port, RMIClientSocketFactory csf, RMIServerSocketFactory ssf) throws RemoteException {
        super(port, csf, ssf);
        connectedClients = new HashMap<>();
        this.gameAlreadyStarted = false;
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
                if (connectedClients.size() == 0) {
                    c.askNumberPartecipants();
                    connectedClients.put(nickName, c);
                } else {
                    connectedClients.put(nickName, c);
                }
                if (connectedClients.size() == this.numPartecipants) {
                    this.model = new Game();
                    this.model.addModelListener(this);
                    this.gameAlreadyStarted = true;
                    this.controller = new GameController(this.model, this.connectedClients, this.numPartecipants);
                }
            } else {
                c.error(Warnings.INVALID_NICKNAME);
            }
        } else {
            c.error(Warnings.GAME_ALREADY_STARTED);
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


    //************ MODEL LISTENER METHODS
    @Override
    public void printGame() {
        for(String s:  connectedClients.keySet()){
            for (Player p: this.model.getPlayers()){
                if (s.equals(p.getNickname())){
                    connectedClients.get(s).printGame(new GameView(this.model, p));
                }
            }
        }
    }
    @Override
    public void error(Warnings e, Player currentPlayer) {
        String nickName = currentPlayer.getNickname();
        this.connectedClients.get(nickName).error(e);
    }
    @Override
    public void newTurn(Player currentPlayer) {
        String nickName = currentPlayer.getNickname();
        if(!this.model.isLastTurn())
            connectedClients.get(nickName).newTurn();
        else
            connectedClients.get(nickName).lastTurn();
    }
    @Override
    public void askNumberPartecipants() {
        connectedClients.get(this.model.getCurrentPlayer().getNickname()).askNumberPartecipants();
    }
    @Override
    public void askOrder(){
        this.connectedClients.get(this.model.getCurrentPlayer().getNickname());
    }
    @Override
    public void isLastTurn(){
        for(String nickname : this.connectedClients.keySet()){
            this.connectedClients.get(nickname).lastTurnNotification(this.model.getCurrentPlayer().getNickname());
        }

    }


}
