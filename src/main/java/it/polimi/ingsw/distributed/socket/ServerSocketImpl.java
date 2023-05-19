package it.polimi.ingsw.distributed.socket;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.Game;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ServerSocketImpl {
    private Game model;
    private GameController controller;
    private ServerSocket serverSocket;
    private Map<Integer,ServerHandler> clients;
    private int port;

    public ServerSocketImpl(int port){
        this.port = port;
        this.model = new Game();
        this.controller = new GameController(this.model);

    }
    public void start() throws IOException {
        clients = new HashMap<>();
        serverSocket = new ServerSocket(port);
        int i = 0;


        while (true){
            System.out.println("Aspetto connessione");
            Socket socket = serverSocket.accept();
            ServerHandler serverHandler;
            if (clients.size() == 0){
                serverHandler = new ServerHandler(socket,model,controller,true);
            }else {
                serverHandler = new ServerHandler(socket, model, controller, false);
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
