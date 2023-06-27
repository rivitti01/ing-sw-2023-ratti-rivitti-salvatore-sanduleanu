package it.polimi.ingsw.distributed.rmi;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.distributed.*;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.util.Warnings;
import it.polimi.ingsw.util.ModelListener;

import java.rmi.RemoteException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import static it.polimi.ingsw.util.Costants.PING_PERIOD;
import static it.polimi.ingsw.util.Costants.TIMEOUT_DURATION;

public class ServerImpl extends UnicastRemoteObject implements Server, ModelListener {
    private final Game model;
    private final GameController controller;
    private final LinkedHashMap<Client, String> connectedClients;
    private final ReentrantLock connectionLock;
    private final ReentrantLock lock = new ReentrantLock();
    private First first;
    private ScheduledExecutorService timerExecutor;
    private ScheduledFuture<?> timerTask;
    private ServerListener serverONE;


    public ServerImpl(Game model, GameController controller, First first) throws RemoteException {
        super();
        this.first = first;
        connectedClients = new LinkedHashMap<>();
        this.model = model;
        this.model.addModelListener(this);
        this.controller = controller;
        this.connectionLock = new ReentrantLock();
    }

    public ServerImpl(int port) throws RemoteException {
        super(port);
        connectedClients = new LinkedHashMap<>();
        this.model = new Game();
        this.model.addModelListener(this);
        this.controller = new GameController(this.model);
        this.connectionLock = new ReentrantLock();
    }

    public ServerImpl(int port, RMIClientSocketFactory csf, RMIServerSocketFactory ssf) throws RemoteException {
        super(port, csf, ssf);
        connectedClients = new LinkedHashMap<>();
        this.model = new Game();
        this.model.addModelListener(this);
        this.controller = new GameController(this.model);
        this.connectionLock = new ReentrantLock();
    }

    public void addServerListener(ServerListener serverListener){
        this.serverONE = serverListener;
    }
    private Client getKeyByValue(Player p) {
        for (Client c : this.connectedClients.keySet()) {
            if (this.connectedClients.get(c).equals(p.getNickname())) {
                return c;
            }
        }
        return null; // valore non trovato nella mappa
    }

    public LinkedHashMap<Client, String> getRMIClients(){
        return this.connectedClients;
    }

    //**********************        SERVER METHODS         ************************************************

    private void addClientToGame(Client c) {
        connectedClients.put(c, null);
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(() -> {
            try {
                c.ping();
            } catch (RemoteException e) {
                System.err.println("A client has exited the game");
                serverONE.clientDisconnected(this.connectedClients.remove(c));
                executorService.shutdown();
            }
        }, 0, PING_PERIOD, TimeUnit.MILLISECONDS);
    }
    @Override
    public void clientConnection(Client c) throws RemoteException {
        boolean canPlay = false;
        try {
            c.warning(Warnings.WAIT);
        } catch (RemoteException e) {
            System.err.println("Unable to advice the client about the loading:" +
                    e.getMessage() + ". Skipping the update...");
        }
        if (!controller.isGameAlreadystarted()) {
            this.serverONE.clientConnected();
            canPlay = true;
            synchronized (first) {
                if (first.getFirst()) {
                    first.setFirst(false);
                    try {
                        c.askNumberParticipants();
                    } catch (RemoteException e) {
                        System.err.println("Unable to ask the number of participants the client: "
                                + e.getMessage() + ". Skipping the update...");
                    }
                } else if (((ServerOne) serverONE).getConnectedClients() > controller.getNumberPlayers())
                    canPlay = false;
                first.notifyAll();

            }
                if (canPlay) {
                    addClientToGame(c);
                    c.askNickname();
                    try {
                        c.warning(Warnings.WAIT);
                    } catch (RemoteException e) {
                        System.err.println("Unable to advice the client about the loading:" +
                                e.getMessage() + ". Skipping the update...");
                    }
                    synchronized (first) {
                        this.controller.checkGameInitialization();
                        first.notifyAll();
                    }
                } else
                    c.warning(Warnings.GAME_ALREADY_STARTED);
        } else {
            if(((ServerOne) serverONE).getConnectedClients() < this.controller.getNumberPlayers()){
                addClientToGame(c);
                System.out.println("RMI: A client has RE-connected.");
                c.askExistingNickname();
            } else {
                try {
                    c.warning(Warnings.GAME_ALREADY_STARTED);
                } catch (RemoteException e) {
                    System.err.println("Unable to advice the client about the game being already full:" +
                            e.getMessage() + ". Skipping the update...");
                }
            }
        }
    }

    @Override
    public void checkingExistingNickname(Client c, String nickName) throws RemoteException {
        if (controller.checkingExistingNickname(nickName)) {
            serverONE.clientConnected();
            // if client is reconnecting I need to open the scanner thread again in the TUI
            System.out.println(" RECONNECTION ");
            c.warning(Warnings.RECONNECTION);

            for (Player player : this.model.getPlayers()) {
                if (player.getNickname().equals(nickName)) {
                    player.setConnected(true);
                }
            }
            connectedClients.put(c, nickName);
            c.setNickname(nickName);
            if (((ServerOne) serverONE).getConnectedClients() == 2) {
                this.model.newTurn();
            } else if (((ServerOne) serverONE).getConnectedClients() > 2) {
                for(Player player : this.model.getPlayers()){
                    if(player.getNickname().equals(nickName)) {
                        c.printGame(new GameView(this.model, player));
                        return;
                    }
                }
            }
        } else
            c.warning(Warnings.INVALID_RECONNECTION_NICKNAME);
    }

    @Override
    public void clientNickNameSetting(Client c, String nickName) throws RemoteException {
        if(model.getPlayers()==null || !this.controller.checkReconnection(nickName)) {
            // if there is no reconnection
            if (this.controller.setPlayerNickname(nickName)) {
                System.out.println("client: " + nickName + " connected");
                connectedClients.put(c, nickName);
                for (Client client : connectedClients.keySet())
                    System.out.print(connectedClients.get(client) + " - ");
                System.out.println();
                c.setNickname(nickName);
            } else {
                try {
                    c.warning(Warnings.INVALID_NICKNAME);
                } catch (RemoteException e) {
                    System.err.println("Unable to advise the client about the invalidation of the chosen nick name:" +
                            e.getMessage() + ". Skipping the update...");
                }
            }
        }
    }

    @Override
    public void tileToDrop(int tilePosition) throws RemoteException {
        this.controller.dropTile(tilePosition);
    }

    @Override
    public void checkingCoordinates(int[] coordinates) throws RemoteException {
        this.controller.checkCorrectCoordinates(coordinates);
    }

    @Override
    public void columnSetting(int c) throws RemoteException {
        controller.setChosenColumn(c);
    }

    @Override
    public void endsSelection() throws RemoteException {
        this.model.selectionControl();
    }

    @Override
    public synchronized void numberOfParticipantsSetting(int n) throws RemoteException {
        this.controller.setNumberPlayers(n);
    }

    @Override
    public void newMessage(Client client, String message) throws RemoteException {
        this.controller.addChatMessage(this.connectedClients.get(client), message);
    }


    @Override
    public void pong() throws RemoteException {

    }






    //************************      MODEL LISTENER METHODS      ************************************************
    @Override
    public void printGame() {
        for (Client c : connectedClients.keySet()) {
            try {
                Player p = null;
                for (int i = 0; i < model.getPlayers().size(); i++) {
                    if (model.getPlayers().get(i).getNickname().equals(connectedClients.get(c))) {
                        p = model.getPlayers().get(i);
                        break;
                    }
                }
                GameView gameView = new GameView(this.model, p);
                c.printGame(gameView);
            } catch (RemoteException e) {
                System.err.println("Unable to print the game:" +
                        e.getMessage() + ". Skipping the update...");
            }
        }

    }

    @Override
    public void warning(Warnings e, Player currentPlayer) {
        if (isMine()) {
            try {
                Objects.requireNonNull(getKeyByValue(currentPlayer)).warning(e);
            } catch (RemoteException exception) {
                System.err.println("Unable to advise the client about a game warning:" +
                        exception.getMessage() + ". Skipping the update...");
            }
        }

    }


    @Override
    public void newTurn(Player currentPlayer) {
        if (!this.model.isLastTurn()) {
            try {
                for (Client c : this.connectedClients.keySet()) {
                    c.newTurn(this.connectedClients.get(c).equals(model.getCurrentPlayer().getNickname()));
                }
            } catch (RemoteException exception) {
                System.err.println("Unable to start a new turn:" +
                        exception.getMessage() + ". Skipping the update...");
            }
        } else {
            try {
                for (Client c : this.connectedClients.keySet())
                    c.lastTurn(this.connectedClients.get(c).equals(model.getCurrentPlayer().getNickname()));
            } catch (RemoteException exception) {
                System.err.println("Unable to start the last turn:" +
                        exception.getMessage() + ". Skipping the update...");
            }
        }
    }
    @Override
    public void askOrder() {
        if (isMine()) {
            try {
                Player currentPlayer = this.model.getCurrentPlayer();

                Objects.requireNonNull(getKeyByValue(currentPlayer)).askOrder();
            } catch (RemoteException e) {
                System.err.println("Unable to ask the current player the order:" +
                        e.getMessage() + ". Skipping the update...");
            }
        }
    }

    @Override
    public void isLastTurn(){
        for(Client c : this.connectedClients.keySet()){
            try {
                c.lastTurnNotification(this.model.getCurrentPlayer().getNickname());
            } catch (RemoteException exception) {
                System.err.println("Unable to advice the clients about the last round beginning:" +
                        exception.getMessage() + ". Skipping the update...");
            }
        }
    }
    @Override
    public void askColumn() {
        if (isMine()) {
            try {
                Player currentPlayer = this.model.getCurrentPlayer();
                Objects.requireNonNull(getKeyByValue(currentPlayer)).askColumn();
            } catch (RemoteException e) {
                System.err.println("Unable to ask the current player the column:" +
                        e.getMessage() + ". Skipping the update...");
            }
        }
    }
    @Override
    public void askAction() {
        if (isMine()) {
            try {
                Player currentPlayer = this.model.getCurrentPlayer();
                Objects.requireNonNull(getKeyByValue(currentPlayer)).askAction();
            } catch (RemoteException e) {
                System.err.println("Unable to ask the current player the action:" +
                        e.getMessage() + ". Skipping the update...");
            }
        }
    }
    @Override
    public void finalPoints(){
        Map<String, Integer> finalPoints = new HashMap<>();
        for(Player p: this.model.getPlayers()) {
            finalPoints.put(p.getNickname(), p.getPoints());
            try {
                Objects.requireNonNull(getKeyByValue(p)).finalPoints(finalPoints);//TODO: crasherà qui perchè non tutti i player p sono di rmi
            } catch (RemoteException e) {
                System.err.println("Unable to advice the client about the final points:" +
                        e.getMessage() + ". Skipping the update...");
            }
        }
    }
    private boolean isMine(){
        for(Client c : connectedClients.keySet()){
            if(model.getCurrentPlayer().getNickname().equals(connectedClients.get(c))){
                return true;
            }
        }
        return false;
    }

    @Override
    public void gameStarted(Player currentPlayer) {
    for(Client client : this.connectedClients.keySet()){
        if(this.connectedClients.get(client).equals(currentPlayer.getNickname())) {
            try {
                client.gameStarted(true);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            try {
                client.gameStarted((false));
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
    }

    }

    @Override
    public void warning(Warnings errorType, String nickname) {
        for(Client client : connectedClients.keySet()){
            if(connectedClients.get(client).equals(nickname)) {
                try {
                    client.warning(errorType);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Override
    public void onePlayerLeft(Player theOnlyPlayerLeft, int countdownToEnd) {

    }

    @Override
    public void playerDisconnected(String nickname) {
        if(connectedClients.size() > 0) {
            for (Client client : this.connectedClients.keySet()) {
                try {
                    client.warning(Warnings.CLIENT_DISCONNECTED);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
            int counter = 0;
            for (Player p : model.getPlayers()) {
                if (p.isConnected()) {
                    counter++;
                }
            }
            if (counter == 1) {
                try {
                    getLastRMIClient().warning(Warnings.WAITING_FOR_MORE_PLAYERS);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }



    private Client getLastRMIClient() {
        Iterator<Map.Entry<Client, String>> iterator = this.connectedClients.entrySet().iterator();
        if (iterator.hasNext()) {
            Map.Entry<Client, String> firstEntry = iterator.next();
            return firstEntry.getKey();
        }
        return null;
    }

    @Override
    public void playerReconnected(String nickname) {
        for(Client client : this.connectedClients.keySet()){
            if(model.getCurrentPlayer().getNickname().equals(connectedClients.get(client))) {
                try {
                    client.newTurn(true);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            } else {
                try {
                    client.newTurn(false);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }

}
