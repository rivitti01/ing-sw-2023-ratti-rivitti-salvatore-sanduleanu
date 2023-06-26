package it.polimi.ingsw.distributed;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.distributed.rmi.ServerImpl;
import it.polimi.ingsw.distributed.socket.ServerSocketImpl;
import it.polimi.ingsw.model.Game;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerOne implements ServerListener {
    private ServerSocketImpl serverSocket;
    private ServerImpl serverRMI;
    private Game model;
    private GameController controller;
    private int connectedClients = 0;
    private Registry registry;
    private First first;

    public ServerOne() throws RemoteException {
        first = new First();
        model = new Game();
        controller = new GameController(model);
        serverSocket = new ServerSocketImpl(2000,model,controller,first);
        serverSocket.addServerListener(this);
        serverRMI = new ServerImpl(model,controller,first);
        serverRMI.addServerListener(this);
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
                System.out.println("ServerSocket is running");
                serverSocket.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        rmi.start();
        socket.start();
    }

    @Override
    public void clientConnected() {
        this.connectedClients++;
    }

    @Override
    public void clientDisconnected() {
        this.connectedClients--;
    }

    public int getConnectedClients() {
        return connectedClients;
    }
}
