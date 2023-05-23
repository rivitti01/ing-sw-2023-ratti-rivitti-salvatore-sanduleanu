package it.polimi.ingsw;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


import it.polimi.ingsw.distributed.Server;
import it.polimi.ingsw.distributed.rmi.ServerImpl;

public class AppServerRMI {
    public static void main( String[] args )  {
        try {
            Server server = new ServerImpl();
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("server", server);
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
        System.out.println("Il server sta runnando daje");
    }
}
