package it.polimi.ingsw;

import it.polimi.ingsw.distributed.ServerOne;

import java.io.IOException;
import java.rmi.RemoteException;

public class AppServerOne {
    public static void main( String[] args ) throws IOException {
        if (args.length==3) {
            int socketPort = Integer.parseInt(args[1]);
            int rmiPort = Integer.parseInt(args[2]);
            ServerOne server = new ServerOne(socketPort,rmiPort);
            server.setIpAddress(args[0]);
            server.start();
        }else {
            System.out.println("Usage: java -jar ServerOne.jar [ipAddress] [socketPort] [rmiPort]");
            System.exit(69);
        }

    }
}
