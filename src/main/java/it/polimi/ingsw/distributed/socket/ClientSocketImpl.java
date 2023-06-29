package it.polimi.ingsw.distributed.socket;

import it.polimi.ingsw.distributed.Client;
import it.polimi.ingsw.model.GameView;
import it.polimi.ingsw.util.ViewListener;
import it.polimi.ingsw.util.Warnings;
import it.polimi.ingsw.view.GraphicalUI.FXGraphicalUI;
import it.polimi.ingsw.view.TextualUI;
import it.polimi.ingsw.view.UI;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.Map;

public class ClientSocketImpl implements ViewListener {
    private UI view;
    private Socket socket;
    private int port;
    private String ip;
    private String nickname;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    boolean lastTurn = false;
    boolean gameStarted = false;
    boolean canPlay = true;

    /**
     * Constructs a new instance of the ClientSocketImpl class with the specified IP address, port number,
     * and GUI option.
     *
     * @param ip   the IP address of the server
     * @param port the port number of the server
     * @param gui  true if a graphical user interface (GUI) should be used, false for a textual user interface (TUI)
     */
    public ClientSocketImpl(String ip, int port,boolean gui){
        this.ip = ip;
        this.port = port;
        if (gui) this.view = new FXGraphicalUI();
        else this.view = new TextualUI();
        this.view.addListener(this);
        try {
            if (view instanceof FXGraphicalUI) ((FXGraphicalUI) view).launchGUI();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Starts the client socket communication with the server.
     * It establishes a socket connection with the specified IP address and port number.
     * It sets up input and output streams for communication with the server.
     * It continuously reads objects from the input stream (Server) and analyzes them.
     *
     * @throws IOException if an I/O error occurs while establishing the socket connection
     * @throws ClassNotFoundException if the class of a serialized object received from the server
     *                                  cannot be found or loaded
     */
    public void start() throws IOException {
        socket = new Socket(ip,port);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());

        while (true) {
            try {
                Object object = new Object();
                object = in.readObject();
                analyzeMessage(object);
            }catch (IOException | ClassNotFoundException e){
                if(canPlay) System.err.println("Something went wrong with the connection to the server!");
                System.exit(1);
            }
        }
    }
    private void analyzeMessage(Object object) throws RemoteException {
        switch (object.getClass().getSimpleName()) {
            case "GameView" -> {
                GameView gameView = (GameView) object;
                view.printGame(gameView);
            }
            case "Warnings"-> {
                Warnings warnings = (Warnings) object;
                switch (warnings){
                    case YOUR_TURN -> {
                        if (!gameStarted) {
                            view.gameStarted(true);
                            gameStarted = true;
                            return;
                        }
                    }
                    case NOT_YOUR_TURN, RECONNECTION -> {
                        if (!gameStarted){
                            view.gameStarted(false);
                            gameStarted = true;
                            return;
                        }
                    }
                    case LAST_TURN_NOTIFICATION -> lastTurn = true;
                    case ASK_RECONNECTION_NICKNAME -> {
                        view.askExistingNickname();
                        return;
                    }
                    case OK_CREATOR -> {
                        view.askNumber();
                        return;
                    }
                    case INVALID_NICKNAME -> {
                        this.nickname = null;
                        view.warning(Warnings.INVALID_NICKNAME);
                        return;
                    }
                    case GAME_ALREADY_STARTED -> {
                        canPlay = false;
                    }
                    case RESUMING_TURN -> view.resumingTurn(true);
                }
                view.warning(warnings);
            }
            case "String" -> {
                String string = (String) object;
                if (string.contains("_RECONNECTED")){
                    String[] parts = string.split("_");
                    view.clientReconnected(parts[0]);
                }
                if (string.contains("_DISCONNECTED")){
                    String[] parts = string.split("_");
                    view.clientDisconnected(parts[0]);
                }
                if (lastTurn){
                    view.lastTurnReached(string);
                }
            }
            case "HashMap" -> {
                if(lastTurn) {
                    Map<String, Integer> map = (Map<String, Integer>) object;
                    view.printFinalPoints(map);
                }
            }
            default -> throw new IllegalStateException("Unexpected value: " + object.getClass().getSimpleName());
        }


    }


    /**
     * Sends the provided nickname to the server for checking its existence.
     *
     * @param nickname the nickname to be checked for existence
     * @throws RemoteException if a remote communication error occurs during the method execution
     */
    @Override
    public void checkingExistingNickname(String nickname) throws RemoteException {
        try {
            out.writeObject(nickname);
            out.reset();
            out.flush();
            this.nickname = nickname;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sets the nickname for the client and sends it to the server for further processing.
     *
     * @param nickName the nickname to be set for the client
     * @throws RemoteException if a remote communication error occurs during the method execution
     */
    @Override
    public void clientNickNameSetting(String nickName) throws RemoteException {
        try {
            this.nickname = nickName;
            out.writeObject(nickName);
            out.reset();
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException("Cannot create client connection",e);
        }
    }


    /**
     * Sends the provided coordinates to the server for checking.
     *
     * @param coordinates the array of coordinates to be checked
     * @throws RemoteException if a remote communication error occurs during the method execution
     */
    @Override
    public void checkingCoordinates(int[] coordinates) throws RemoteException {
        try {
            out.writeObject(coordinates);
            out.reset();
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Sends the position of a tile to drop in the player's shelf.
     *
     * @param tilePosition the position of the tile to be dropped
     * @throws RemoteException if a remote communication error occurs during the method execution
     */
    @Override
    public void tileToDrop(int tilePosition) throws RemoteException {
        try{
            out.writeObject(tilePosition);
            out.reset();
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sends the selected column number to the server for column setting.
     *
     * @param i the selected column number for setting
     * @throws RemoteException if a remote communication error occurs during the method execution
     */
    @Override
    public void columnSetting(int i) throws RemoteException {
        try {
            out.writeObject(i);
            out.reset();
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Sends the number of participants to the server for setting.
     *
     * @param n the number of participants to be set
     * @throws RemoteException if a remote communication error occurs during the method execution
     */
    @Override
    public void numberPartecipantsSetting(int n) throws RemoteException {
        try {
            out.writeObject(n);
            out.reset();
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Notifies the server that the selection process has ended.
     *
     * @throws RemoteException if a remote communication error occurs during the method execution
     */
    @Override
    public void endsSelection() throws RemoteException {
        try {
            out.writeObject(Warnings.END_SELECTION);
            out.reset();
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    /**
     * Sends a new message to the server.
     *
     * @param message the message to be sent
     * @throws RemoteException if a remote communication error occurs during the method execution
     */
    @Override
    public void newMessage(String message) throws RemoteException {
        try {
            out.writeObject(message);
            out.reset();
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    /**
     * Prints the provided game view.
     *
     * @param gameView the game view to be printed
     * @throws RemoteException if a remote communication error occurs during the method execution
     */
    public void printGame(GameView gameView) throws RemoteException {
        view.printGame(gameView);
    }

}
