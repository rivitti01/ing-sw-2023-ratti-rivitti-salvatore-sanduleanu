package it.polimi.ingsw;

import it.polimi.ingsw.distributed.Server;
import it.polimi.ingsw.distributed.rmi.ClientImpl;
import it.polimi.ingsw.distributed.socket.ClientSocketImpl;

import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class AppClientOne {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        String serverType = null;
        String uiType = null;
        boolean ok = false;
        Scanner scanner = new Scanner(System.in);
/*
        while (!ok) {
            System.out.println("choose:\n[R] RMI Server\n[S] Socket Server");
            serverType = scanner.nextLine();
            if (serverType.equals("R")) {
                ok = true;
                try {
                    // Obtain a reference to the remote object
                    Registry registry = LocateRegistry.getRegistry();
                    Server server = (Server) registry.lookup("server");
                    ClientImpl client = new ClientImpl(server);
                    client.run();
                } catch (Exception e) {
                    System.err.println("Client exception: " + e.toString());
                    e.printStackTrace();
                }

            } else if (serverType.equals("S")) {
                ok = true;
                ClientSocketImpl client = new ClientSocketImpl("127.0.0.1",2000);
                client.start();

            } else
                System.out.println("ERRORE: comando sconosciuto!");
        }
*/
        while (!ok) {
            System.out.println("choose:\n[R] RMI Server\n[S] Socket Server");
            serverType = scanner.nextLine();
            if (!serverType.equals("R") && !serverType.equals("S"))
                System.out.println("ERRORE: comando sconosciuto!");
            else
                ok = true;
        }

        ok = false;

        while (!ok) {
            System.out.println("choose:\n[G] GUI\n[T] TUI");
            uiType = scanner.nextLine();
            if (!uiType.equals("G") && !uiType.equals("T"))
                System.out.println("ERRORE: comando sconosciuto!");
            else
                ok = true;
        }

        if (serverType.equals("R")) {
            if (uiType.equals("G")) {     // RMI GUI
                try {
                    // Obtain a reference to the remote object
                    Registry registry = LocateRegistry.getRegistry();
                    Server server = (Server) registry.lookup("server");
                    ClientImpl client = new ClientImpl(server, true);
                    client.run();
                } catch (Exception e) {
                    System.err.println("Client exception: " + e.toString());
                    e.printStackTrace();
                }
            } else {      // RMI TUI
                try {
                    // Obtain a reference to the remote object
                    Registry registry = LocateRegistry.getRegistry();
                    Server server = (Server) registry.lookup("server");
                    ClientImpl client = new ClientImpl(server, false);
                    client.run();
                } catch (Exception e) {
                    System.err.println("Client exception: " + e.toString());
                    e.printStackTrace();
                }
            }
        } else {
            if (uiType.equals("G")) {   // SOCKET GUI


            } else {                    // SOCKET TUI
                ClientSocketImpl client = new ClientSocketImpl("127.0.0.1", 2000);
                client.start();
            }
        }
    }
}
