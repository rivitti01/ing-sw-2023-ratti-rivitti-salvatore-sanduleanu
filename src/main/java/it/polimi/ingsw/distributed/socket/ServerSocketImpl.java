package it.polimi.ingsw.distributed.socket;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.distributed.First;
import it.polimi.ingsw.model.Game;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ServerSocketImpl {
    private Game model;
    private GameController controller;
    private ServerSocket serverSocket;
    private Map<Integer,ServerHandler> clients;
    private final int port;
    private final Object lock;
    private Boolean isOperationComplete;
    private First first;

    public ServerSocketImpl(int port, Game model, GameController controller, First first) {
        this.port = port;
        this.model = model;
        this.controller = controller;
        this.lock = new Object();
        this.first = first;
    }
    public void start() throws IOException {
        clients = new HashMap<>();
        serverSocket = new ServerSocket(port);
        int i = 0;


        while (true){
            System.out.println("Aspetto connessione");
            Socket socket = serverSocket.accept();
            ServerHandler serverHandler;
            if (clients.size() == 0 && first.getFirst()){
                first.setFirst(false);
                serverHandler = new ServerHandler(socket,model,controller,true,lock);
            }else {
                serverHandler = new ServerHandler(socket, model, controller, false, lock);
            }
            clients.put(clients.size(), serverHandler);
            Thread thread = new Thread(serverHandler);
            thread.start();
            if (clients.size() == 4){
                while (true){

                }
            }

        }
    }
}
