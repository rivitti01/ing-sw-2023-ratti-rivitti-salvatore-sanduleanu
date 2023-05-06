package it.polimi.ingsw;

import it.polimi.ingsw.distributed.ClientSocketImpl;
import it.polimi.ingsw.distributed.ServerSocketImpl;

import java.io.IOException;

public class AppClientSocket {
    public static void main( String[] args ) throws IOException, ClassNotFoundException, InterruptedException {
        ClientSocketImpl client = new ClientSocketImpl(2000,"127.0.0.1");
        client.startClient();
    }
}
