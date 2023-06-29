package it.polimi.ingsw.distributed;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.distributed.rmi.ServerImpl;
import it.polimi.ingsw.distributed.socket.ServerSocketImpl;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.GameView;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.util.Warnings;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.text.SimpleDateFormat;
import java.util.*;
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
                System.out.println(getTime()+" ServerRMI is running");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });
        Thread socket = new Thread(() -> {
            try {
                System.out.println(getTime()+" ServerSocket is running");
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
            System.out.println(getTime()+ ANSI_GREEN_BACKGROUND+" ServerONE: timer has been stopped!"+ANSI_RESET);
            interruptedTimer = true;
            timerTask.cancel(true);
            timerTask = null;

            if (connectedClients == 2) {
                // this.model.newTurn();
                model.resumingTurn();
                model.printGame();
            }
        }
        System.out.println(getTime()+" SERVERONE: number of clients connected = " + connectedClients);
        return lastID-1;
    }

    @Override
    public void clientDisconnected(String nickname, int ID) {
        if (connectedClientsID.remove((Integer) ID)) {
            System.out.println(getTime()+ANSI_RED_BACKGROUND +" SERVERONE: " +  "client " + nickname + " has disconnected" + ANSI_RESET);
            this.connectedClients--;
            System.out.println(getTime()+" SERVERONE: number of clients connected = " + connectedClients);

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
            if (connectedClients <= 1) {    // one player left

                System.out.println(getTime()+ANSI_PURPLE_BACKGROUND+" SERVERONE: waiting for more players to continue..."+ANSI_RESET);
                interruptedTimer = false;
                startTimer();
            }
        }
    }


    private void startTimer() {

        // Schedule a timer task to be executed after a specified timeout
        timerExecutor = Executors.newSingleThreadScheduledExecutor();
        System.out.println(getTime()+ANSI_RED_BACKGROUND+" SERVERONE: Timer has started. TIK TAK TIK TAK"+ANSI_RESET);
        // Code to be executed when the timer expires

        timerTask = timerExecutor.schedule(this::handleTimeout, TIMEOUT_DURATION, TimeUnit.SECONDS);
        // Countdown loop
        int duration = TIMEOUT_DURATION;
        while (duration > 0 && !interruptedTimer) {
            try {
                System.out.print("\r");
                System.out.flush();
                TimeUnit.SECONDS.sleep(1);
                System.out.print(getTime()+ANSI_YELLOW_BACKGROUND + " Timer: " + duration + " seconds remaining" + ANSI_RESET);
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
        System.out.println(getTime()+ANSI_RED_BACKGROUND+" Timeout! No other players!"+ANSI_RESET+"\nClosing the game...");
        model.setErrorType(Warnings.NO_PLAYERS_LEFT);
        System.exit(1);
    }

    public int getConnectedClients() {
        return connectedClients;
    }

    public List<Integer> getConnectedClientsID() {
        return connectedClientsID;
    }
    public String getTime(){
        return new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(Calendar.getInstance().getTime());
    }
}
