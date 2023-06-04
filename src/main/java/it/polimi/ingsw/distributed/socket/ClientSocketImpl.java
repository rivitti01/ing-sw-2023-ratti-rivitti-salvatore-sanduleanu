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
import java.util.HashMap;
import java.util.Map;

public class ClientSocketImpl implements Client, ViewListener {
    private TextualUI view = new TextualUI();
    private Socket socket;
    private int port;
    private String ip;
    private String nickname;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    boolean lastTurn = false;
    boolean gameStarted = false;

    public ClientSocketImpl(String ip, int port){
        this.ip = ip;
        this.port = port;
        this.view.addListener(this);
    }
    public void start() throws IOException, ClassNotFoundException {
        socket = new Socket(ip,port);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());

        while (true) {
            Object object = new Object();
            object = in.readObject();
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
                switch (warnings){
                    case YOUR_TURN -> {
                        if (!gameStarted) {
                            view.gameStarted(true);
                            gameStarted = true;
                            return;
                        }
                    }
                    case NOT_YOUR_TURN -> {
                        if (!gameStarted){
                            view.gameStarted(false);
                            gameStarted = true;
                            return;
                        }
                    }
                    case LAST_TURN_NOTIFICATION -> lastTurn = true;
                }

                view.warning(warnings);
            }
            case "String" -> {
                if (lastTurn){
                    String string = (String) object;
                    view.lastTurnReached(string);
                }
            }
            case "HashMap" -> {
                if(lastTurn) {
                    Map<String, Integer> map = (Map<String, Integer>) object;
                    view.printFinalPoints(map);
                }
            }
            default -> throw new IllegalStateException("Unexpected value: " + object.getClass().getSimpleName());
        }


    }



    @Override
    public void askNickname() throws RemoteException {

    }

    @Override
    public void ping() throws RemoteException {

    }

    @Override
    public void setNickname(String nickname) throws RemoteException {
        this.nickname = nickname;
    }

    @Override
    public void gameStarted(boolean youTurn) {

    }

    @Override
    public void clientNickNameSetting(String nickName) throws RemoteException {
        try {
            out.writeObject(nickName);
            out.reset();
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
            out.reset();
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void tileToDrop(int tilePosition) throws RemoteException {
        try{
            out.writeObject(tilePosition);
            out.reset();
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void columnSetting(int i) throws RemoteException {
        try {
            out.writeObject(i);
            out.reset();
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void numberPartecipantsSetting(int n) throws RemoteException {
        try {
            out.writeObject(n);
            out.reset();
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void endsSelection() throws RemoteException {
        try {
            out.writeObject(Warnings.END_SELECTION);
            out.reset();
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public void newMessage(String message) throws RemoteException {
        try {
            out.writeObject(message);
            out.reset();
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
    public void newTurn(boolean playing) throws RemoteException { //CLIENT
    }


    @Override
    public void lastTurn(boolean currentPlayer) throws RemoteException { //CLIENT

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
