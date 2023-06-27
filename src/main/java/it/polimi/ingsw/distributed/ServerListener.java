package it.polimi.ingsw.distributed;

public interface ServerListener extends java.util.EventListener {
    void clientConnected();
    void clientDisconnected(String s);
}
