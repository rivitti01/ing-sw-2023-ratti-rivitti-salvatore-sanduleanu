package it.polimi.ingsw.distributed.rmi;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.distributed.Client;
import it.polimi.ingsw.distributed.Server;
import it.polimi.ingsw.model.*;
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

    protected ServerImpl() throws RemoteException {
        connectedClients = new HashMap<>();
    }

    protected ServerImpl(int port) throws RemoteException {
        super(port);
        connectedClients = new HashMap<>();
    }

    protected ServerImpl(int port, RMIClientSocketFactory csf, RMIServerSocketFactory ssf) throws RemoteException {
        super(port, csf, ssf);
        connectedClients = new HashMap<>();
    }

    public boolean nameControl(String newName){
        for (String s: connectedClients.keySet()){
            if (s.equals(newName)) return false;
        }
        return true;
    }

    @Override
    public void clientConnection(Client c, String nickName) {
        if (nameControl(nickName)){
            if (connectedClients.size()==0){
                this.numPartecipants = c.askNumberPartecipants();
                connectedClients.put(nickName, c);
            } else {
                connectedClients.put(nickName, c);
            }
            if (connectedClients.size()==this.numPartecipants){
                this.model = new Game();
                this.controller = new GameController(model, this.connectedClients, this.numPartecipants);
                this.model.addModelListener(this);
            }
        } else {
            c.nameError();
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
    public void chosenTileError(Client c) {
        System.err.println("The selected tile cannot be taken. Choose another one:");
        c.chosenTileError();
    }

    @Override
    public void chosenColumnError(Client c) {
        System.err.println("The selected column cannot be chosen for room problems. Choose another one:");
        c.chosenColumnError();
    }

    @Override
    public void newTurn(Player currentPlayer) {
        String nickName = currentPlayer.getNickname();
        connectedClients.get(nickName).run();
    }


}
