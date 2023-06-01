package it.polimi.ingsw;

import it.polimi.ingsw.distributed.ServerOne;

import java.io.IOException;
import java.rmi.RemoteException;

public class AppServerOne {
    public static void main( String[] args ) throws IOException {
        ServerOne server = new ServerOne();
        server.start();
    }
}
