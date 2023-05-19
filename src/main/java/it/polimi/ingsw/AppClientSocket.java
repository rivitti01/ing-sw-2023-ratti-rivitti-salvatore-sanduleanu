package it.polimi.ingsw;

import it.polimi.ingsw.distributed.socket.ClientSocketImpl;

import java.io.IOException;

public class AppClientSocket {
    public static void main( String[] args ) throws IOException, ClassNotFoundException, InterruptedException {
        ClientSocketImpl client = new ClientSocketImpl("127.0.0.1",2000);
        client.start();
    }
}