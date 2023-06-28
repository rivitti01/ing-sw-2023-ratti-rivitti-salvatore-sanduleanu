package it.polimi.ingsw.distributed;

public interface ServerListener extends java.util.EventListener {
    int clientConnected();
    void clientDisconnected(String s, int ID);
}
