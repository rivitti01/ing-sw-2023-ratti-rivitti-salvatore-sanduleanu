package it.polimi.ingsw.distributed.rmi;

import it.polimi.ingsw.distributed.Client;
import it.polimi.ingsw.distributed.Server;
import it.polimi.ingsw.model.ChatView;
import it.polimi.ingsw.model.GameView;
import it.polimi.ingsw.util.Warnings;
import it.polimi.ingsw.util.ViewListener;
import it.polimi.ingsw.view.TextualUI;

import java.rmi.RemoteException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;


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
    public void clientNickNameSetting(String nickName) throws RemoteException{
        this.stub.clientNickNameSetting(this, nickName);

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
        this.stub.newMessage(message, this);
    }

    @Override
    public void chatTyped() throws RemoteException {
        this.stub.chatTyped(this);
    }


    //*************** CLIENT METHODS
    @Override
    public void newTurn() {
        try {
            this.view.newTurn();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void lastTurn(){
        this.view.lastTurn();
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
        try {
            this.view.chooseAction();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void printChat(ChatView chatView) throws RemoteException {
        this.view.printChat(chatView);
    }

    @Override
    public void chatAvailable() throws RemoteException {
        this.view.chatAvailable();
    }

    @Override
    public void askNickname() throws RemoteException {
        this.view.askNickName();
    }

    @Override
    public void newChatMessage(String message, String sender) throws RemoteException {

    }

    @Override
    public void askColumn() throws RemoteException {
        this.view.askColumn();
    }

    @Override
    public void run()  {
        try {
            this.stub.clientConnection(this);
        } catch (RemoteException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
