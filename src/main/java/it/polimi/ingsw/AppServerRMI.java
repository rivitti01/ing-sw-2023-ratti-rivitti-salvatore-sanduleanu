package it.polimi.ingsw;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import it.polimi.ingsw.distributed.Server;
import it.polimi.ingsw.distributed.rmi.ServerImpl;

public class AppServerRMI {

    public static void main( String[] args )  {

        try {
            Server server = new ServerImpl();
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind("server", server);
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }

    }
}
