package it.polimi.ingsw.distributed.socket;

import it.polimi.ingsw.distributed.Client;
import it.polimi.ingsw.model.GameView;
import it.polimi.ingsw.util.ViewListener;
import it.polimi.ingsw.util.Warnings;
import it.polimi.ingsw.view.GraphicalUI.FXGraphicalUI;
import it.polimi.ingsw.view.TextualUI;
import it.polimi.ingsw.view.UI;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.Map;

public class ClientSocketImpl implements Client, ViewListener {
    private UI view;
    private Socket socket;
    private int port;
    private String ip;
    private String nickname;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    boolean lastTurn = false;
    boolean gameStarted = false;

    public ClientSocketImpl(String ip, int port,boolean gui){
        this.ip = ip;
        this.port = port;
        if (gui) this.view = new FXGraphicalUI();
        else this.view = new TextualUI();
        this.view.addListener(this);
        try {
            if (view instanceof FXGraphicalUI) ((FXGraphicalUI) view).launchGUI();
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    public void start() throws IOException {
        socket = new Socket(ip,port);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());

        while (true) {
            try {
                Object object = new Object();
                object = in.readObject();
                analyzeMessage(object);
            }catch (IOException | ClassNotFoundException e){
                System.err.println("Something went wrong with the connection to the server!");
                System.exit(1);
            }
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
                    case NOT_YOUR_TURN, RECONNECTION -> {
                        if (!gameStarted){
                            view.gameStarted(false);
                            gameStarted = true;
                            return;
                        }
                    }
                    case LAST_TURN_NOTIFICATION -> lastTurn = true;
                    case ASK_RECONNECTION_NICKNAME -> {
                        view.askExistingNickname();
                        return;
                    }
                    case OK_JOINER -> {

                       // return;
                    }
                    case OK_CREATOR -> {
                        view.askNumber();
                        return;
                    }
                    case INVALID_NICKNAME -> {
                        this.nickname = null;
                        view.warning(Warnings.INVALID_NICKNAME);
                        return;
                    }
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
    public void askExistingNickname() throws RemoteException {

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
    public void setID(int id) throws RemoteException {

    }

    @Override
    public int getID() throws RemoteException {
        return 0;
    }


    @Override
    public void checkingExistingNickname(String nickname) throws RemoteException {

        try {
            out.writeObject(nickname);
            out.reset();
            out.flush();
            this.nickname = nickname;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    @Override
    public void clientNickNameSetting(String nickName) throws RemoteException {
        try {
            this.nickname = nickName;
            out.writeObject(nickName);
            out.reset();
            out.flush();
            /*Object object = in.readObject();
            Warnings response = null;
            if (object instanceof Warnings){
                response = (Warnings) object;
            }
            //Warnings response = (Warnings) in.readObject();

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
            }*/


        } catch (IOException /*| ClassNotFoundException*/ e) {
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
