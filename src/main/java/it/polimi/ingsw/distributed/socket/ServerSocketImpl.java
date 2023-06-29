package it.polimi.ingsw.distributed.socket;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.distributed.First;
import it.polimi.ingsw.distributed.ServerListener;
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
    private final int port;
    private final Object lock;
    private First first;
    private ServerListener serverONE;


    /**
     * Constructs a new ServerSocketImpl with the specified port, game model, game controller, and first player indicator.
     *
     * @param port       the port number on which the server socket will listen for incoming connections
     * @param model      the game model representing the state of the game
     * @param controller the game controller responsible for handling game logic
     * @param first      the indicator for the first player in the game
     */
    public ServerSocketImpl(int port, Game model, GameController controller, First first) {
        this.port = port;
        this.model = model;
        this.controller = controller;
        this.lock = new Object();
        this.first = first;
    }

    /**
     * Adds a server listener to receive server-related events and updates.
     *
     * @param serverListener the server listener to be added
     */
    public void addServerListener(ServerListener serverListener){
        this.serverONE = serverListener;
    }

    /**
     * Starts the server and listens for incoming client connections.
     *
     * @throws IOException if an I/O error occurs when accepting client connections
     */
    public void start() throws IOException {
        clients = new HashMap<>();
        serverSocket = new ServerSocket(port);
        int lastID;
        while (true){
            Socket socket = serverSocket.accept();

            lastID = this.serverONE.clientConnected();
            ServerHandler serverHandler;

            if (clients.size() == 0 && first.getFirst()){
                first.setFirst(false);
                serverHandler = new ServerHandler(socket,model,controller,true,first,serverONE);
                serverHandler.setClientID(lastID);
            }else {
                serverHandler = new ServerHandler(socket, model, controller, false, first,serverONE);
                serverHandler.setClientID(lastID);
            }
            clients.put(clients.size(), serverHandler);
            Thread thread = new Thread(serverHandler);
            thread.start();
        }
    }
}
