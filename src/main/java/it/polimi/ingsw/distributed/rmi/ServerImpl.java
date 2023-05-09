package it.polimi.ingsw.distributed.rmi;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.distributed.Client;
import it.polimi.ingsw.distributed.Server;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.util.ErrorType;
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

    protected ServerImpl() throws RemoteException {
        connectedClients = new HashMap<>();
    }

    protected ServerImpl(int port) throws RemoteException {
        super(port);
        connectedClients = new HashMap<>();
        this.gameAlreadyStarted = false;
    }

    protected ServerImpl(int port, RMIClientSocketFactory csf, RMIServerSocketFactory ssf) throws RemoteException {
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
                    this.controller = new GameController(model, this.connectedClients, this.numPartecipants);
                }
            } else {
                c.error(ErrorType.INVALID_NICKNAME);
            }
        } else {
            c.error(ErrorType.GAME_ALREADY_STARTED);
        }
    }
    @Override
    public void orederSetting(List<Tile> chosenTiles) {
        this.controller.dropTiles(chosenTiles);
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
    public void numberPartecipantsSetting(int n) {
        this.numPartecipants = n;
    }

    @Override
    public void printGame() {
        for(String s:  connectedClients.keySet()){
            for (Player p: this.controller.getPlayers().keySet()){
                if (s.equals(p.getNickname())){
                    connectedClients.get(s).printGame(new GameView(this.model, p));
                }
            }
        }
    }

    @Override
    public void error(ErrorType e, Player currentPlayer) {
        String nickName = currentPlayer.getNickname();
        this.connectedClients.get(nickName).error(e);
    }

    @Override
    public void newTurn(Player currentPlayer) {
        String nickName = currentPlayer.getNickname();
        connectedClients.get(nickName).run();
    }

    @Override
    public void askNumberPartecipants() {
        connectedClients.get(this.model.getCurrentPlayer().getNickname()).askNumberPartecipants();
    }



}
