package it.polimi.ingsw.distributed.socket;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.distributed.Client;
import it.polimi.ingsw.distributed.Server;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.GameView;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.util.ModelListener;
import it.polimi.ingsw.util.Warnings;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.Objects;

public class ServerHandler implements Server,Runnable, ModelListener {
    private String nickname;
    private Socket socket;
    private Game model;
    private GameController controller;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private boolean creator;
    private boolean wait;
    private state currentState;
    public enum state{
        COLUMN, ORDER
    }
    public ServerHandler(Socket socket, Game model, GameController controller,boolean creator){
        this.socket = socket;
        this.model = model;
        this.controller = controller;
        this.model.addModelListener(this);
        this.creator = creator;
        currentState = state.COLUMN;
    }
    @Override
    public void run() {
        System.out.println("ServerClientHandler of "+ socket.getPort());
        try {
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
            waitAndSetNickname();
            while (true) {
                analyzeMessage(in.readObject());
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }


    }
    private void analyzeMessage(Object response) throws IOException {
        switch (response.getClass().getSimpleName()){
            case "int[]" -> {
                int[] coordinates = (int[]) response;
                controller.checkCorrectCoordinates(coordinates);
            }
            case "Warnings" -> {
                Warnings warning = (Warnings) response;
                if (warning.equals(Warnings.END_SELECTION)){
                    model.selectionControl();
                }

            }
            case "Integer" -> {
                if(currentState == state.COLUMN) {
                    int column = (int) response;
                    controller.setChosenColumn(column);
                }else if(currentState == state.ORDER) {
                    int order = (int) response;
                    controller.dropTile(order);;
                }
            }
        }


    }
    private void waitAndSetNickname() throws IOException, ClassNotFoundException {
        String nickname = (String) in.readObject();
        System.out.println(socket.getPort()+": Nickname = "+nickname);
        if (nickname !=null){
            if(controller.setPlayerNickname(nickname)){
                this.nickname = nickname;
                if (creator){
                    out.writeObject(Warnings.OK_CREATOR);
                    out.reset();
                    out.flush();
                    waitAndSetNumberPlayers();
                }else {
                    out.writeObject(Warnings.OK_JOINER);
                    out.reset();
                    out.flush();
                }
                if (controller.getNumberPlayers()>1 && controller.getNumberPlayers()<5 && controller.getNumberPlayers()==controller.getPlayers().size() && !model.isStart()){//TODO: correggere il controllo da parte del controller e poi cancellare il superfluo in questo if
                    controller.initializeModel();
                }
            } else {
                out.writeObject(Warnings.INVALID_NICKNAME);
                out.reset();
                out.flush();
                waitAndSetNickname();
            }
        }
        System.out.println("Client"+socket.getPort()+ ": nome assegnato -> "+nickname);
    }
    private void waitAndSetNumberPlayers() throws IOException, ClassNotFoundException {
        int numberPlayers = (int) in.readObject();
        numberOfParticipantsSetting(numberPlayers);
    }
    public String getNickname(){
        return nickname;
    }


    //ModelListener ->
    @Override
    public void tileToDrop(int tilePosition) throws RemoteException {

    }

    @Override
    public void finalPoints() {

    }

    @Override
    public void checkingCoordinates(int[] coordinates) throws RemoteException {

    }

    @Override
    public void columnSetting(int i) throws RemoteException {

    }

    @Override
    public void endsSelection() throws RemoteException {

    }

    @Override
    public void numberOfParticipantsSetting(int n) throws RemoteException {
        System.out.println(socket.getPort()+": Number of players = "+n);
        if (n > 1 && n < 5){
            controller.setNumberPlayers(n);
            if (controller.getNumberPlayers()==controller.getPlayers().size() && !model.isStart()){
                controller.initializeModel();
            }
        }
        System.out.println("Client"+socket.getPort()+ ": numero giocatori assegnato -> "+n);

    }

    @Override
    public void printGame() {
        for (Player p : model.getPlayers()){
            if (p.getNickname().equals(nickname)){
                GameView gameView = new GameView(model, p);
                try {
                    out.writeObject(gameView);
                    out.reset();
                    out.flush();
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void warning(Warnings e, Player currentPlayer) {
        if (currentPlayer.getNickname().equals(nickname)){
            try {
                out.writeObject(e);
                out.reset();
                out.flush();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    @Override
    public void newTurn(Player currentPlayer) {
        if(currentPlayer.getNickname().equals(nickname)){
            try {
                out.writeObject(Warnings.YOUR_TURN);
                out.reset();
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            try {
                out.writeObject(Warnings.NOT_YOUR_TURN);
                out.reset();
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void askOrder() {
        if (model.getCurrentPlayer().getNickname().equals(nickname)){
            try {
                if(model.getCurrentPlayer().getChosenTiles().size() > 1){
                    out.writeObject(Warnings.ASK_ORDER);
                    out.reset();
                    out.flush();
                    currentState = state.ORDER;
                } else{
                    this.controller.dropTile(1);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    @Override
    public void isLastTurn() {

    }

    @Override
    public void askColumn() {
        if (model.getCurrentPlayer().getNickname().equals(nickname)) {
            try {
                out.writeObject(Warnings.ASK_COLUMN);
                out.reset();
                out.flush();
                currentState = state.COLUMN;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    @Override
    public void askAction() {
        if (model.getCurrentPlayer().getNickname().equals(nickname)){
            try {
                currentState = state.COLUMN;
                out.writeObject(Warnings.CONTINUE_TO_CHOOSE);
                out.reset();
                out.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    @Override
    public void clientConnection(Client c) throws RemoteException {

    }

    @Override
    public void clientNickNameSetting(Client c, String nickName) throws RemoteException {//Server ->

    }

    @Override
    public void chatTyped(Client client) throws RemoteException {

    }

    @Override
    public void newMessage(String message, Client sender) throws RemoteException {

    }

    @Override
    public void chatAvailable() {

    }

    @Override
    public void printChat() {

    }




}
