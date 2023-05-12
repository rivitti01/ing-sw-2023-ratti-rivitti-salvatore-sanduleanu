package it.polimi.ingsw.distributed;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.GameView;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ServerHandler implements Runnable { //, PropertyChangeListener
    private final Socket socket;
    private final Game model;
    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    private String nickname;
    private Boolean started;
    private final Object lock0;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private boolean creator;
    private boolean canPlay;
    public ServerHandler(Socket socket,Game model,Object lock0, Boolean creator){
        this.socket = socket;
        this.model = model;
        this.lock0 = lock0;
        started = false;
        this.creator = creator;
        canPlay = true;
    }

    @Override
    public void run() {
        System.out.println("ServerClientHandler of "+ socket.getPort());
        try {
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            System.out.println("aspetto messaggio "+ socket.getPort());
            waitAndSetNickname(); // il primo messaggio che voglio ricevere e il nome utente del client a cui è collegato questo ServerHandler
            setUp();
        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        while (true){

        }


    }
    public void setUp() throws IOException, ClassNotFoundException, InterruptedException {
        if (creator){
            sendString("playerNumber");
            Message message = (Message) objectInputStream.readObject();
            readMessageOnConsole(message);
            fireMessage(message);
        }else {
            sendString("NoPlayerNumber");
        }
    }
    public void waitAndSetNickname() throws IOException, ClassNotFoundException {
        Message message = (Message) objectInputStream.readObject();
        if (message.getNickName()!=null){
            fireMessage(message);
        }
        System.out.println("Client"+socket.getPort()+ ": nome assegnato -> "+nickname);
    }
    public Message waitMessage() throws IOException, ClassNotFoundException {
        return (Message) objectInputStream.readObject();
    }
    public void fireMessage(Message message){
        if (message.getNumberPlayers()!=null){
            propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(socket,"playerNumber",this,message.getNumberPlayers()));
        }
        if (message.getNickName()!=null){
            propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(socket,"nickname",this,message.getNickName()));
        }
        if (message.getColumn()!=null){
            propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(socket,"column",this,message.getColumn()));
        }
        if (message.getCoordinates()!=null){
            propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(socket,"coordinates",this,message.getCoordinates()));
        }
    }
    public void readMessageOnConsole(Message message){
        System.out.print("Message from: "+ socket.getPort()+ " -> ");
        if (message.getNickName()!=null) System.out.println(message.getNickName());
        if (message.getColumn()!=null) System.out.println(message.getColumn());
        if (message.getCoordinates()!=null) message.getCoordinates().stream().forEach(x -> System.out.print(x[0]+" "+x[1]));
        if (message.getNumberPlayers()!=null) System.out.println(message.getNumberPlayers());
    }
    public void sendModelView(GameView gameView) throws IOException {
        gameView.getBoard().setBorderTiles();
        objectOutputStream.writeObject(gameView);
        objectOutputStream.flush();
    }
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.propertyChangeSupport.removePropertyChangeListener(listener);
    }
    public Socket getSocket(){
        return socket;
    }
    /*
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            try {
                System.out.println("lo mando?");
                sendModelView(new GameView((Game) evt.getSource()));
                System.out.println("mandato");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }*/
    public String getNickname(){
        return nickname;
    }
    public void setNickname(String nickname){
        this.nickname = nickname;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }
    public void setCanPlay(boolean canPlay) throws IOException, InterruptedException {
        this.canPlay = canPlay;
        objectOutputStream.writeObject("cantPlay");
    }
    public boolean getCanPlay(){
        return canPlay;
    }
    public void sendString(String string) throws IOException {
        objectOutputStream.writeObject(string);
        objectOutputStream.flush();
    }
}
