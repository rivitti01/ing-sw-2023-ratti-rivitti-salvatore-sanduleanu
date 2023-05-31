package it.polimi.ingsw.distributed;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.distributed.rmi.ServerImpl;
import it.polimi.ingsw.distributed.socket.ServerSocketImpl;
import it.polimi.ingsw.model.Game;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerOne {
    private ServerSocketImpl serverSocket;
    private ServerImpl serverRMI;
    private Game model;
    private GameController controller;
    private Registry registry;

    public ServerOne() throws RemoteException {
        model = new Game();
        controller = new GameController(model);
        serverSocket = new ServerSocketImpl(2000,model,controller);
        serverRMI = new ServerImpl(model,controller);
    }
    public void start() throws IOException {
        Thread rmi = new Thread(() -> {
            try {
                registry = LocateRegistry.createRegistry(1099);
                registry.rebind("server", serverRMI);
                System.out.println("ServerRMI is running");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });
        Thread socket = new Thread(() -> {
            try {
                serverSocket.start();
                System.out.println("ServerSocket is running");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        rmi.start();
        socket.start();
    }

}
