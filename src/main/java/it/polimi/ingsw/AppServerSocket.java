package it.polimi.ingsw;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.distributed.First;
import it.polimi.ingsw.distributed.socket.ServerSocketImpl;
import it.polimi.ingsw.model.Game;

import java.io.IOException;

public class AppServerSocket {
    public static void main( String[] args ) throws IOException, ClassNotFoundException {
        First first = new First();
        Game model = new Game();
        GameController controller = new GameController(model);
        ServerSocketImpl server = new ServerSocketImpl(2000,model,controller,first);
        server.start();
    }
}