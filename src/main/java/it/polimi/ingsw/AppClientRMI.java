package it.polimi.ingsw;

import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import it.polimi.ingsw.distributed.Server;
import it.polimi.ingsw.distributed.rmi.ClientImpl;

public class AppClientRMI {
    public static void main( String[] args ) throws RemoteException, NotBoundException {
        /*Registry registry = LocateRegistry.getRegistry();
        Server server = (Server) registry.lookup("server");

        ClientImpl client = new ClientImpl(server);
        client.run();
        */
        try {
            // Obtain a reference to the remote object
            Server remoteObj = (Server) Naming.lookup("RemoteInterface");
            ClientImpl client = new ClientImpl(remoteObj);
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}

