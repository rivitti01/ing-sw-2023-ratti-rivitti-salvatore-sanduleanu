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

public class ServerHandler implements Server,Runnable, ModelListener {
    private String nickname;
    private Socket socket;
    private Game model;
    private GameController controller;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private boolean creator;
    public ServerHandler(Socket socket, Game model, GameController controller,boolean creator){
        this.socket = socket;
        this.model = model;
        this.controller = controller;
        this.model.addModelListener(this);
        this.creator = creator;
    }
    @Override
    public void run() {
        System.out.println("ServerClientHandler of "+ socket.getPort());
        try {
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
            waitAndSetNickname();
            /*while (model.getCurrentPlayer().equals(nickname)){
                Object response = in.readObject();
                if (response instanceof GameView){
                    GameView gameView = (GameView) response;
                    controller.updateModel(gameView);
                }
                if (response instanceof int[]){
                    int[] coordinates = (int[]) response;
                    controller.checkingCoordinates(coordinates);
                }
                if (response instanceof Integer){
                    int column = (int) response;
                    controller.columnSetting(column);
                }
                if (response instanceof String && response.equals("endTurn")){
                    controller.endsSelection();
                }
                if (response instanceof String && response.equals("endGame")){
                    controller.endGame();
                }
            }*/


        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }


    }
    private void waitAndSetNickname() throws IOException, ClassNotFoundException {
        String nickname = (String) in.readObject();
        System.out.println(socket.getPort()+": Nickname = "+nickname);
        if (nickname !=null){
            if(controller.setPlayerNickname(nickname)){
                this.nickname = nickname;
                if (creator){
                    out.writeObject("okCreator");
                    out.flush();
                    waitAndSetNumberPlayers();
                }else {
                    out.writeObject("ok");
                    out.flush();
                }
                if (controller.getNumberPlayers()>1 && controller.getNumberPlayers()<5 && controller.getNumberPlayers()==controller.getPlayers().size() && !model.isStart()){//TODO: correggere il controllo da parte del controller e poi cancellare il superfluo in questo if
                    controller.initializeModel();
                }
            } else {
                out.writeObject("ko");
                out.flush();
                waitAndSetNickname();
            }
        }
        System.out.println("Client"+socket.getPort()+ ": nome assegnato -> "+nickname);
    }
    private void waitAndSetNumberPlayers() throws IOException, ClassNotFoundException {
        int numberPlayers = (int) in.readObject();
        System.out.println(socket.getPort()+": Number of players = "+numberPlayers);
        if (numberPlayers > 1 && numberPlayers < 5){
            controller.setNumberPlayers(numberPlayers);
            if (controller.getNumberPlayers()==controller.getPlayers().size() && !model.isStart()){
                controller.initializeModel();
            }
        }
        System.out.println("Client"+socket.getPort()+ ": numero giocatori assegnato -> "+numberPlayers);
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

    }

    @Override
    public void printGame() { //ModelListener ->
        for (Player p : model.getPlayers()){
            if (p.getNickname().equals(nickname)){
                GameView gameView = new GameView(model, p);
                try {
                    out.writeObject(gameView);
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

    }

    @Override
    public void newTurn(Player currentPlayer) {
        if(currentPlayer.getNickname().equals(nickname)){
            try {
                out.writeObject("yourTurn");
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            try {
                out.writeObject("notYourTurn");
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void askOrder() {

    }

    @Override
    public void isLastTurn() {

    }

    @Override
    public void askColumn() {

    }

    @Override
    public void askAction() {

    }




}
