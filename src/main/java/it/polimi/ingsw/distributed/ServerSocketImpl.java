package it.polimi.ingsw.distributed;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.*;


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ServerSocketImpl implements PropertyChangeListener {
    private final int port;
    ServerSocket serverSocket;
    private Game model;
    private GameController controller;
    private int playerNumber;
    private Object lock0;


    Map<Integer,ServerHandler> clients;

    public ServerSocketImpl(int port) throws IOException {
        this.port = port;
        model = new Game();
        model.addPropertyChangeListener(this);
        controller = new GameController(model);
        lock0 = new Object();
    }
    public void StartServer() throws IOException, ClassNotFoundException{

        clients = new HashMap<>();

        System.out.println("Open server socket on "+ port+ " port.");

        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println(e.getMessage()); // porta non disponibile
            return;
        }
        System.out.println("Server ready");
        int i = 0;

        while (true){
            System.out.println("Aspetto connessione");
            Socket socket = serverSocket.accept();
            System.out.println("["+i+"]"+" connessione accettata");
            ServerHandler serverHandler;
            if (clients.size() == 0){
                serverHandler = new ServerHandler(socket,model, lock0,true);
            }else {
                serverHandler = new ServerHandler(socket,model, lock0,false);
            }
            clients.put(i,serverHandler);
            serverHandler.addPropertyChangeListener(this);
            Thread thread = new Thread(serverHandler);
            thread.start();

            if (i > 3){
                System.out.println("Raggiunto numero massimo di client");
                while (true){
                    //stay open
                }
            }else
                i++;
        }

    }
    public void sendModelView(Game model) throws InterruptedException {
        GameView modelView = new GameView(model);
        //wait(1500);
        for (int i = 0; i < clients.size(); i++){
            try {
                clients.get(i).sendModelView(modelView); //invio il nuovo gameView ai client
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }
    public void setPlayerNumber(int number) throws IOException, InterruptedException {
        controller.setPlayerNumber(number);
        playerNumber = number;
        System.out.println("Preparo il gioco per "+ number + " giocatori");
        boolean start = true;
        if (clients.size() >= playerNumber ){
            for (int i = 0; i < clients.size(); i++){
                if (i < playerNumber && clients.get(i).getNickname()==null){
                    start = false;
                    break;
                }
                if (i >= playerNumber && clients.get(i).getNickname()==null){
                    clients.get(i).sendString("cantPlay");
                    clients.remove(i);
                }
            }
            if (start){
                controller.initializeModel();
                System.out.println("inizializzo");
                //wait(1500);
                //sendModelView(model);
            }
        }


        /*
        if (clients.size() >= playerNumber ){
            for (int i = 0; i < clients.size(); i++){
                if (i < playerNumber) {
                    controller.setPlayerNickname(clients.get(i).getNickname());
                    clients.get(i).setStarted(true);
                }else {
                    try {
                        clients.get(i).setCanPlay(false);
                    } catch (IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            for (int i = 0; i < clients.size(); i++){
                if (!clients.get(i).getCanPlay()) clients.remove(i);
            }
            controller.initializeModel();
            System.out.println("inizializzo");
            sendModelView(model);
        }*/
    }
    private void setNickname(PropertyChangeEvent evt) throws IOException, ClassNotFoundException, InterruptedException {
        ServerHandler serverHandler = (ServerHandler) evt.getOldValue();
        String nickname = (String) evt.getNewValue();
        if (controller.setPlayerNickname(nickname)){
            serverHandler.setNickname(nickname);
            serverHandler.sendString("ok");
            System.out.println("C'è un giocatore di nome "+ evt.getNewValue());
            if (clients.size() == playerNumber){
                //wait(1500);
                setPlayerNumber(playerNumber);
            }
        }else {
            serverHandler.sendString("ko");
            for (int i = 0; i < clients.size(); i++) {
                if (clients.get(i).equals(serverHandler)){
                    clients.get(i).waitAndSetNickname();
                }
            }
        }
    }
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("start")){
            System.out.println("qui il model dovrebbe essere fatto e dovrebbe essere propagato ai client");
            model = (Game) evt.getSource();
            try {
                sendModelView(model);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        if (Objects.equals(evt.getPropertyName(), "playerNumber")){
            int number = (int) evt.getNewValue();
            try {
                setPlayerNumber(number);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
        if (Objects.equals(evt.getPropertyName(), "nickname")){
            try {
                setNickname(evt);
            } catch (IOException | ClassNotFoundException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
