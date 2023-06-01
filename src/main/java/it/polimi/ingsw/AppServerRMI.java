package it.polimi.ingsw;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.distributed.First;
import it.polimi.ingsw.distributed.Server;
import it.polimi.ingsw.distributed.rmi.ServerImpl;
import it.polimi.ingsw.model.Game;

public class AppServerRMI {
    public static void main( String[] args )  {
        Game model = new Game();
        GameController controller = new GameController(model);
        try {
            First first = new First();
            Server server = new ServerImpl(model,controller,first);
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("server", server);
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
        System.out.println("Il server sta runnando daje");
    }
}
