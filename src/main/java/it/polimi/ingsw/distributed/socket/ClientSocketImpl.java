package it.polimi.ingsw.distributed.socket;

import it.polimi.ingsw.distributed.Client;
import it.polimi.ingsw.model.ChatView;
import it.polimi.ingsw.model.GameView;
import it.polimi.ingsw.util.ViewListener;
import it.polimi.ingsw.util.Warnings;
import it.polimi.ingsw.view.TextualUI;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.Map;

public class ClientSocketImpl implements Client, ViewListener {
    private TextualUI view = new TextualUI();
    private Socket socket;
    private int port;
    private String ip;
    private String nickname;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public ClientSocketImpl(String ip, int port){
        this.ip = ip;
        this.port = port;
        this.view.addListener(this);
    }
    public void start() throws IOException, ClassNotFoundException {
        socket = new Socket(ip,port);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        view.askNickName();
        System.out.println("I'm waiting for the server");
        /*while(true) {
            waitModelView();
        }*/
        while (true){
            Object object = in.readObject();
            analyzeMessage(object);
        }


    }
    private void analyzeMessage(Object object) throws RemoteException {
        switch (object.getClass().getSimpleName()) {
            case "GameView" -> {
                GameView gameView = (GameView) object;
                view.printGame(gameView);
                }
            case "Warnings"-> {
                Warnings warnings = (Warnings) object;
                view.warning(warnings);
            }
        }


    }
    @Override
    public void printChat(ChatView chatView) throws RemoteException {

    }

    @Override
    public void chatAvailable() throws RemoteException {

    }

    @Override
    public void askNickname() throws RemoteException {

    }

    @Override
    public void clientNickNameSetting(String nickName) throws RemoteException {
        try {
            out.writeObject(nickName);
            out.flush();
            Warnings response = (Warnings) in.readObject();

            if (response != null && response.equals(Warnings.INVALID_NICKNAME)) {
                //view.askNickName();
                view.warning(response);
            }
            else if(response != null && response.equals(Warnings.OK_CREATOR)){
                nickname = nickName;
                view.askNumber();
            }
            else if( response != null && response.equals(Warnings.OK_JOINER)){
                nickname = nickName;
                //view.askNumber(); deve aspettare che la partita sia completa
            }


        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Cannot create client connection",e);
        }
    }


    @Override
    public void checkingCoordinates(int[] coordinates) throws RemoteException {
        try {
            out.writeObject(coordinates);
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void tileToDrop(int tilePosition) throws RemoteException {

    }

    @Override
    public void columnSetting(int i) throws RemoteException {
        try {
            out.writeObject(i);
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void numberPartecipantsSetting(int n) throws RemoteException {
        try {
            out.writeObject(n);
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void endsSelection() throws RemoteException {
        try {
            out.writeObject(Warnings.END_SELECTION);
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public void newMessage(String message) throws RemoteException {

    }

    @Override
    public void chatTyped() throws RemoteException {

    }


    @Override
    public void printGame(GameView gameView) throws RemoteException { //CLIENT
        view.printGame(gameView);
    }

    @Override
    public void finalPoints(Map<String, Integer> finalPoints) throws RemoteException { //CLIENT

    }

    @Override
    public void warning(Warnings e) throws RemoteException { //CLIENT

    }

    @Override
    public void askNumberParticipants() throws RemoteException { //CLIENT

    }

    @Override
    public void newTurn() throws RemoteException { //CLIENT
    }

    @Override
    public void lastTurn() throws RemoteException { //CLIENT

    }

    @Override
    public void askOrder() throws RemoteException { //CLIENT

    }

    @Override
    public void lastTurnNotification(String nickname) throws RemoteException { //CLIENT

    }

    @Override
    public void askColumn() throws RemoteException { //CLIENT

    }

    @Override
    public void askAction() throws RemoteException { //CLIENT

    }

}
