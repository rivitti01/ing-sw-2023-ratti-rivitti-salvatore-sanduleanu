package it.polimi.ingsw;

import it.polimi.ingsw.distributed.Server;
import it.polimi.ingsw.distributed.rmi.ClientRMIImpl;
import it.polimi.ingsw.distributed.socket.ClientSocketImpl;

import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


import static java.lang.System.exit;

public class AppClientOne {
    public static void main(String[] args) throws IOException{
        String serverType = null;
        String uiType = null;
        String ipAddress = null;
        int socketPort = 0;
        int rmiPort = 0;
        if (args.length == 5){
            uiType = args[0];
            serverType = args[1];
            ipAddress = args[2];
            socketPort = Integer.parseInt(args[3]);
            rmiPort = Integer.parseInt(args[4]);
        }else {
            System.out.println("Usage: java -jar ClientOne.jar [G/T] [R/S] [ipAddress] [socketPort] [rmiPort]");
            System.exit(1);
        }


        if (serverType.equalsIgnoreCase("R")) {
            if (uiType.equalsIgnoreCase("G")) {     // RMI GUI
                try {
                    // Obtain a reference to the remote object
                    Registry registry = LocateRegistry.getRegistry(ipAddress,rmiPort);
                    Server server = (Server) registry.lookup("server");
                    ClientRMIImpl client = new ClientRMIImpl(server, true);
                    client.run();
                } catch (Exception e) {
                    System.err.println("Client exception: " + e);
                    e.printStackTrace();
                }
            } else {      // RMI TUI
                try {
                    // Obtain a reference to the remote object
                    Registry registry = LocateRegistry.getRegistry(ipAddress,rmiPort);
                    Server server = (Server) registry.lookup("server");
                    ClientRMIImpl client = new ClientRMIImpl(server, false);
                    client.run();
                } catch (Exception e) {
                    System.err.println("Client exception: " + e);
                    e.printStackTrace();
                }
            }
        }else {
            ClientSocketImpl client;
            if (uiType.equalsIgnoreCase("G")) {   // SOCKET GUI
                client = new ClientSocketImpl(ipAddress, socketPort, true);
            } else {                    // SOCKET TUI
                client = new ClientSocketImpl(ipAddress, socketPort, false);
            }
            client.start();
        }
    }
}
