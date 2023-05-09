package it.polimi.ingsw.distributed;

import it.polimi.ingsw.model.GameView;
import it.polimi.ingsw.util.ErrorType;

import java.rmi.Remote;

public interface Client extends Remote {
    void printGame(GameView gameView);
    void error(ErrorType e);
    void askNumberPartecipants();
    void run();

}
