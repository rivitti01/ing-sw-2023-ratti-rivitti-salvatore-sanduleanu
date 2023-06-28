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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static it.polimi.ingsw.util.Costants.*;
import static it.polimi.ingsw.view.Colors.*;

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
    private boolean interruptedTimer = false;
    private List<Integer> connectedClientsID;
    private int lastID = 0;

    public ServerOne() throws RemoteException {
        first = new First();
        model = new Game();
        controller = new GameController(model);
        serverSocket = new ServerSocketImpl(SOCKET_PORT,model,controller,first);
        serverSocket.addServerListener(this);
        serverRMI = new ServerImpl(model,controller,first);
        serverRMI.addServerListener(this);
        connectedClientsID = new ArrayList<>();
    }
    public void start() throws IOException {
        Thread rmi = new Thread(() -> {
            try {
                registry = LocateRegistry.createRegistry(RMI_PORT);
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
    public int clientConnected() {
        this.connectedClients++;
        connectedClientsID.add(lastID++);
        if(timerTask != null) {
            System.out.println("ServerONE: timer has been stopped!");
            interruptedTimer = true;
            timerTask.cancel(true);
            timerTask = null;
        }
        System.out.println("SERVERONE: number of clients connected = " + connectedClients);
        return lastID-1;
    }

    @Override
    public void clientDisconnected(String nickname, int ID) {
        if (connectedClientsID.remove((Integer) ID)) {
            System.out.println("SERVERONE: " + ANSI_RED_BACKGROUND + "client " + nickname + " has disconnected" + ANSI_RESET);
            this.connectedClients--;
            System.out.println("SERVERONE: number of clients connected = " + connectedClients);

            Player disconnectedPlayer = null;
            if (nickname != null && model != null && model.getPlayers() != null) {
                for (Player player : model.getPlayers()) {
                    if (player.getNickname().equals(nickname)) {
                        disconnectedPlayer = player;
                        break;
                    }

                }
            }
            if (disconnectedPlayer != null) {
                controller.disconnectedPlayer(disconnectedPlayer);
            }
            if (nickname != null && !nickname.equals(model.getCurrentPlayer().getNickname())) {    // one player left
                System.out.println("SERVERONE: waiting for more players to continue...");
                interruptedTimer = false;
                startTimer();
            }
        }
    }


    private void startTimer() {

        // Schedule a timer task to be executed after a specified timeout
        timerExecutor = Executors.newSingleThreadScheduledExecutor();
        System.out.println("timer has started. TIK TOK TIK TOK");
        // Code to be executed when the timer expires

        timerTask = timerExecutor.schedule(this::handleTimeout, TIMEOUT_DURATION, TimeUnit.SECONDS);
        // Countdown loop
        int duration = TIMEOUT_DURATION;
        while (duration > 0 && !interruptedTimer) {
            try {
                System.out.print("\r");
                System.out.flush();
                TimeUnit.SECONDS.sleep(1);
                System.out.print(ANSI_YELLOW_BACKGROUND + "Timer: " + duration + " seconds remaining" + ANSI_RESET);
                System.out.flush();
                duration--;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        System.out.println();
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

    public List<Integer> getConnectedClientsID() {
        return connectedClientsID;
    }
}
