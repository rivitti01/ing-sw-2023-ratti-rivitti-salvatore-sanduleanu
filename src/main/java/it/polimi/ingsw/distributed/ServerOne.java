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
        if(timerTask != null)
            timerTask.cancel(true);
    }

    @Override
    public void clientDisconnected(String nickname) {
        System.out.println("SERVEROOOOOOOOONE");
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
                //TODO put the tiles back on the board
                disconnectedPlayer.reset(model.getCommonGoals());
                try {
                    this.controller.nextPlayer();
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }

            }
        }else{    // one player left
            System.out.println("waiting for more players to continue...");
            if(serverRMI.getRMIClients().size() == 1) {  // one RMI client remaining
                try {
                    getLastRMIClient().warning(Warnings.WAITING_FOR_MORE_PLAYERS);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            } else {    // one SOCKET client remaining
                //TODO notify SOCKET client that he needs to wait for clients
            }
            startTimer();
        }
    }

    private Client getLastRMIClient() {
        Iterator<Map.Entry<Client, String>> iterator = serverRMI.getRMIClients().entrySet().iterator();
        if (iterator.hasNext()) {
            Map.Entry<Client, String> firstEntry = iterator.next();
            return firstEntry.getKey();
        }
        return null;
    }

    // TODO getLastSOCKETClient

    private void startTimer() {
        if (timerExecutor != null && !timerExecutor.isShutdown()) {
            // Timer is already running, no need to start a new one
            return;
        }

        // Schedule a timer task to be executed after a specified timeout
        timerExecutor = Executors.newSingleThreadScheduledExecutor();
        // Code to be executed when the timer expires
        timerTask = timerExecutor.schedule(this::handleTimeout, TIMEOUT_DURATION, TimeUnit.SECONDS);
    }

    private void handleTimeout() {
        // Code to be executed when the timeout occurs
        System.out.println("Timeout! No other players!\nClosing the game...");
        if(serverRMI.getRMIClients().size() == 1){   // one rmi client remained
            try {
                getLastRMIClient().warning(Warnings.NO_PLAYERS_LEFT);
            } catch (RemoteException e) {

            }

        } else{   // one socket client remained

        }
        System.exit(2);
    }

    public int getConnectedClients() {
        return connectedClients;
    }
}
