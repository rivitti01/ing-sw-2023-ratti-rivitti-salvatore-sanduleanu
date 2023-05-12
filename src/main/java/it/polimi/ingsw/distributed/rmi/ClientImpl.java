package it.polimi.ingsw.distributed.rmi;

import it.polimi.ingsw.distributed.Client;
import it.polimi.ingsw.distributed.Server;
import it.polimi.ingsw.model.GameView;
import it.polimi.ingsw.model.Tile;
import it.polimi.ingsw.util.Warnings;
import it.polimi.ingsw.util.ViewListener;
import it.polimi.ingsw.util.Warnings;
import it.polimi.ingsw.view.TextualUI;

import java.rmi.RemoteException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;


public class ClientImpl extends UnicastRemoteObject implements Client, ViewListener, Runnable {
    private TextualUI view = new TextualUI();
    private Server stub;


    public ClientImpl(Server s) throws RemoteException {
        super();
        this.stub = s;
        this.view.addListener(this);
    }
    public ClientImpl(Server s, int port) throws RemoteException {
        super(port);
        this.stub = s;
        this.view.addListener(this);
    }
    public ClientImpl(Server s, int port, RMIClientSocketFactory csf, RMIServerSocketFactory ssf) throws RemoteException {
        super(port, csf, ssf);
        this.stub = s;
        this.view.addListener(this);
    }


    // ***************** VIEW LISTENER METHODS
    @Override
    public void clientConnection(String nickName) {
        try {
            this.stub.clientConnection(this, nickName);
        } catch (RemoteException exception){
            System.err.println("Unable to connect the player to the game:" +
                    exception.getMessage() + ". Skipping the update...");
        }

    }
    @Override
    public void checkingCoordinates(int[] coordinates) {
        try {
            this.stub.checkingCoordinates(coordinates);
        } catch (RemoteException exception){
            System.err.println("Unable to send the tile coordinates to the server:" +
                    exception.getMessage() + ". Skipping the update...");
        }

    }
    @Override
    public void tileToDrop(int tilePosition) {
        try {
            this.stub.tileToDrop(tilePosition);
        } catch (RemoteException exception){
            System.err.println("Unable to send the tile order to the server:" +
                    exception.getMessage() + ". Skipping the update...");
        }
    }
    @Override
    public void columnSetting(int i) {
        try {
            this.stub.columnSetting(i);
        } catch (RemoteException exception){
            System.err.println("Unable to send the tile order to the server:" +
                    exception.getMessage() + ". Skipping the update...");
        }
    }
    @Override
    public void numberPartecipantsSetting(int n) {
        try {
            this.stub.numberOfParticipantsSetting(n);
        } catch (RemoteException exception){
            System.err.println("Unable to send the number of participants to the server:" +
                    exception.getMessage() + ". Skipping the update...");
        }
    }
    @Override
    public void endsSelection() {
        try {
            this.stub.endsSelection();
        } catch (RemoteException exception){
            System.err.println("Unable to stop the tile choice:" +
                    exception.getMessage() + ". Skipping the update...");
        }
    }


    //*************** CLIENT METHODS
    @Override
    public void newTurn() {
        this.view.newTurn();
    }
    @Override
    public void lastTurn(){
        this.view.lastTurn();
    }
    @Override
    public void printGame(GameView gameView) {
        this.view.printGame(gameView);
    }
    @Override
    public void error(Warnings e) {
        this.view.error(e);
    }
    @Override
    public void askNumberParticipants() {
        this.view.askNumber();
    }
    @Override
    public void askOrder(){
        this.view.askOrder();
    }
    @Override
    public void lastTurnNotification(String nickname){
        this.view.lastTurnReached(nickname);
    }

    @Override
    public void askColumn() throws RemoteException {
        this.view.askColumn();
    }

    @Override
    public void run() {
        this.view.askNickName();
    }
}
