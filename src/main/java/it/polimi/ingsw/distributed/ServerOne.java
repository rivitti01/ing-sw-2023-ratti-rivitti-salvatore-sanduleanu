package it.polimi.ingsw.distributed;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.distributed.rmi.ServerImpl;
import it.polimi.ingsw.distributed.socket.ServerSocketImpl;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.util.Warnings;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static it.polimi.ingsw.util.Costants.TIMEOUT_DURATION;

public class ServerOne implements ServerListener {
    private ServerSocketImpl serverSocket;
    private ServerImpl serverRMI;
    private Game model;
    private GameController controller;
    private int connectedClients = 0;
    private Registry registry;
    private First first;
    private ScheduledExecutorService timerExecutor;
    private ScheduledFuture<?> timerTask;

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
        if(timerTask != null) {
            System.out.println("ServerONE: timer has been stopped!");
            timerTask.cancel(true);
            timerTask = null;
        }
    }

    @Override
    public void clientDisconnected(String nickname) {
        System.err.println("SERVERONE: client " + nickname + " has disconnected");
        this.connectedClients--;
        Player disconnectedPlayer = null;
        for(Player player : model.getPlayers()){
            if(player.getNickname().equals(nickname)) {
                disconnectedPlayer = player;
                break;
            }

        }
        controller.disconnectedPlayer(disconnectedPlayer);

        if(this.connectedClients > 1) {   // multiple players left
            if (nickname.equals(model.getCurrentPlayer().getNickname())) {    // currentPlayer Disconnected
                disconnectedPlayer.reset(model.getCommonGoals()); //TODO put the tiles back on the board
                try {
                    this.controller.nextPlayer();
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }

            }
        }else{    // one player left
            System.out.println("SERVERONE: waiting for more players to continue...");
            startTimer();
        }
    }


    private void startTimer() {

        // Schedule a timer task to be executed after a specified timeout
        timerExecutor = Executors.newSingleThreadScheduledExecutor();
        System.out.println("timer has started. TIK TOK TIK TOK");
        // Code to be executed when the timer expires

        timerTask = timerExecutor.schedule(this::handleTimeout, TIMEOUT_DURATION, TimeUnit.SECONDS);
    }

    private void handleTimeout() {
        // Code to be executed when the timeout occurs
        System.out.println("Timeout! No other players!\nClosing the game...");
        model.setErrorType(Warnings.NO_PLAYERS_LEFT);
        System.exit(1);
    }

    public int getConnectedClients() {
        return connectedClients;
    }
}
