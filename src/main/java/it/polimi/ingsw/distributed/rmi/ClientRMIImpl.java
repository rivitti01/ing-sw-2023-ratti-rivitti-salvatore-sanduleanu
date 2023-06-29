package it.polimi.ingsw.distributed.rmi;

import it.polimi.ingsw.distributed.Client;
import it.polimi.ingsw.distributed.Server;
import it.polimi.ingsw.model.GameView;
import it.polimi.ingsw.util.Warnings;
import it.polimi.ingsw.util.ViewListener;
import it.polimi.ingsw.view.GraphicalUI.FXGraphicalUI;
import it.polimi.ingsw.view.TextualUI;
import it.polimi.ingsw.view.UI;

import java.rmi.RemoteException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;


public class ClientRMIImpl extends UnicastRemoteObject implements Client, ViewListener, Runnable {
    private UI view;
    private Server stub;
    private String nickname = null;
    private static final int PONG_PERIOD = 1000;  // milliseconds
    private int id = -1;


    public ClientRMIImpl(Server s, boolean gui) throws RemoteException {
        super();
        this.stub = s;
        checkServer();
        if(gui){
            this.view = new FXGraphicalUI();
        }else
            this.view = new TextualUI();
        this.view.addListener(this);
        try {
            if (view instanceof FXGraphicalUI) ((FXGraphicalUI) view).launchGUI();
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    public ClientRMIImpl(Server s, int port) throws RemoteException {
        super(port);
        this.stub = s;
        this.view.addListener(this);
        try {
            if (view instanceof FXGraphicalUI) ((FXGraphicalUI) view).launchGUI();
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    public ClientRMIImpl(Server s, int port, RMIClientSocketFactory csf, RMIServerSocketFactory ssf) throws RemoteException {
        super(port, csf, ssf);
        this.stub = s;
        this.view.addListener(this);
        try {
            if (view instanceof FXGraphicalUI) ((FXGraphicalUI) view).launchGUI();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Checks the server's connectivity periodically.
     *
     * This method creates a separate thread that periodically sends a "pong" signal to the server
     * to check its connectivity.
     * If a remote communication error occurs during the ping-pong process,
     * the client assumes that the server has crashed and exits the game.
     */
    private void checkServer(){
        Thread pongThread = new Thread(() -> {
            while (true){
                try {
                    this.stub.pong();
                    Thread.sleep(PONG_PERIOD);
                } catch (RemoteException e) {
                    System.err.println("Server has crushed!\nExiting the game...");
                    System.exit(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        pongThread.start();
    }


    // ***************** VIEW LISTENER METHODS   ************************************************
    @Override
    public void clientNickNameSetting(String nickName) throws RemoteException{
        this.stub.clientNickNameSetting(this, nickName);

    }
    @Override
    public void checkingExistingNickname(String nickname) throws RemoteException{
        this.stub.checkingExistingNickname(this, nickname);
    }
    @Override
    public void checkingCoordinates(int[] coordinates) throws RemoteException{
        this.stub.checkingCoordinates(coordinates);

    }
    @Override
    public void tileToDrop(int tilePosition) throws RemoteException{
        this.stub.tileToDrop(tilePosition);
    }
    @Override
    public void columnSetting(int i)   throws   RemoteException{
        this.stub.columnSetting(i);
    }
    @Override
    public void numberPartecipantsSetting(int n) throws RemoteException{
        this.stub.numberOfParticipantsSetting(n);
    }
    @Override
    public void endsSelection() throws  RemoteException{
        this.stub.endsSelection();
    }
    @Override
    public void newMessage(String message) throws RemoteException {
        this.stub.newMessage(this, message);
    }


    //*********************************      CLIENT METHODS      *********************************************************
    @Override
    public void newTurn(boolean playing) {
        try {
            this.view.newTurn(playing);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }



    @Override
    public void lastTurn(boolean playing){
        this.view.lastTurn(playing);
    }
    @Override
    public void printGame(GameView gameView)  {
        this.view.printGame(gameView);
    }

    @Override
    public void finalPoints(Map<String, Integer> finalPoints) throws RemoteException {
        this.view.printFinalPoints(finalPoints);
    }

    @Override
    public void warning(Warnings e) throws RemoteException {
        this.view.warning(e);
        if(e == Warnings.GAME_ALREADY_STARTED || e == Warnings.NO_PLAYERS_LEFT)
            System.exit(1);
    }
    @Override
    public void askNumberParticipants() throws RemoteException {
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
    public void askAction() throws RemoteException {
        this.view.chooseAction();

    }

    @Override
    public void askNickname() throws RemoteException {
        this.view.askNickName();

    }
    @Override
    public void askExistingNickname() throws RemoteException{
        this.view.askExistingNickname();

    }

    @Override
    public void ping() throws RemoteException {
    }

    @Override
    public void askColumn() throws RemoteException {
        this.view.askColumn();
    }

    @Override
    public void run(){
        try {
            this.stub.clientConnection(this);
        } catch (RemoteException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public String getNickname() {
        return nickname;
    }
    @Override
    public void setNickname(String nickname) throws RemoteException {
        this.nickname = nickname;
    }

    @Override
    public void gameStarted(boolean yourTurn) {
        this.view.gameStarted(yourTurn);
    }

    @Override
    public void setID(int id) throws RemoteException {
        this.id = id;
    }

    @Override
    public int getID() throws RemoteException {
        return this.id;
    }

    @Override
    public void resumingTurn(boolean playing) throws RemoteException {
        this.view.resumingTurn(playing);
    }

    @Override
    public void clientReconnected(String nickname) throws RemoteException {
        view.clientReconnected(nickname);
    }

    @Override
    public void clientDisconnected(String nickname) throws RemoteException{
        this.view.clientDisconnected(nickname);
    }
}
