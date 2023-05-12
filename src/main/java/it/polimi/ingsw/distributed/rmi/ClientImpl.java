package it.polimi.ingsw.distributed.rmi;

import it.polimi.ingsw.distributed.Client;
import it.polimi.ingsw.distributed.Server;
import it.polimi.ingsw.model.GameView;
import it.polimi.ingsw.model.Tile;
import it.polimi.ingsw.util.Warnings;
import it.polimi.ingsw.util.ViewListener;
import it.polimi.ingsw.view.TextualUI;

import java.rmi.RemoteException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;


public class ClientImpl extends UnicastRemoteObject implements Client, Runnable, ViewListener {
    private TextualUI view = new TextualUI();
    private Server stub;


    protected ClientImpl(Server s) throws RemoteException {
        super();
        this.stub = s;
        this.view.addListener(this);
    }

    protected ClientImpl(Server s, int port) throws RemoteException {
        super(port);
        this.stub = s;
        this.view.addListener(this);
    }

    protected ClientImpl(Server s, int port, RMIClientSocketFactory csf, RMIServerSocketFactory ssf) throws RemoteException {
        super(port, csf, ssf);
        this.stub = s;
        this.view.addListener(this);
    }

    @Override
    public void run() {
        this.view.run();
    }

    @Override
    public void askColumn() {
        this.view.askColumn();
    }


    @Override
    public void clientConnection(String nickName) {
        this.stub.clientConnection(this, nickName);
    }


    @Override
    public void checkingCoordinates(int[] coordinates) {
        this.stub.checkingCoordinates(coordinates);
    }

    @Override
    public void orederSetting(List<Tile> orderedTiles) {
        this.stub.orederSetting(orderedTiles);
    }

    @Override
    public void columnSetting(int i) {
        this.stub.columnSetting(i);
    }

    @Override
    public void numberPartecipantsSetting(int n) {

    }

    @Override
    public void endsSelection() {
        this.stub.endsSelection();
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
    public void askNumberPartecipants() {
        this.view.askNumber();
    }
}
