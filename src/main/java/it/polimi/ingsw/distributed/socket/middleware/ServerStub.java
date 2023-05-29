package it.polimi.ingsw.distributed.socket.middleware;

import it.polimi.ingsw.distributed.Client;
import it.polimi.ingsw.distributed.Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.rmi.RemoteException;

public class ServerStub implements Server {
    private final String ip;
    private final int port;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private Socket socket;


    public ServerStub(String host, int port){
        this.ip = host;
        this.port = port;
    }

    /*public void connect(){
        // TODO
    }

    public void disconnect(){
        // TODO
    }

    public void send(String message){
        // TODO
    }

    public String receive(){
        // TODO
    }*/

    @Override
    public void clientConnection(Client c) throws RemoteException {

    }

    @Override
    public void clientNickNameSetting(Client c, String nickName) throws RemoteException {
        try {
            this.socket = new Socket(ip, port);
            try {
                this.oos = new ObjectOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                throw new RemoteException("Cannot create output stream", e);
            }
            try {
                this.ois = new ObjectInputStream(socket.getInputStream());
            } catch (IOException e) {
                throw new RemoteException("Cannot create input stream", e);
            }
        } catch (IOException e) {
            throw new RemoteException("Unable to connect to the server", e);
        }
    }

    @Override
    public void tileToDrop(int tilePosition) throws RemoteException {

    }

    @Override
    public void checkingCoordinates(int[] coordinates) throws RemoteException {

    }

    @Override
    public void columnSetting(int i) throws RemoteException {

    }

    @Override
    public void endsSelection() throws RemoteException {

    }

    @Override
    public void numberOfParticipantsSetting(int n) throws RemoteException {

    }

    @Override
    public void newMessage(Client client, String message) {

    }

    @Override
    public void pong() throws RemoteException {

    }


}
