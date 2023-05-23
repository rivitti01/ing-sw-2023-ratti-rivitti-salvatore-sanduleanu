package it.polimi.ingsw;

import it.polimi.ingsw.distributed.socket.ServerSocketImpl;

import java.io.IOException;

public class AppServerSocket {
    public static void main( String[] args ) throws IOException, ClassNotFoundException {
        ServerSocketImpl server = new ServerSocketImpl(2000);
        server.start();
    }
}