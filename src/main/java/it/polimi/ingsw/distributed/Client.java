package it.polimi.ingsw.distributed;

import it.polimi.ingsw.model.GameView;
import it.polimi.ingsw.util.Warnings;
import it.polimi.ingsw.util.Warnings;

import java.rmi.Remote;

public interface Client extends Remote {
    void printGame(GameView gameView);
    void error(Warnings e);
    void askNumberPartecipants();
    void newTurn();
    void lastTurn();
    void askOrder();
    void lastTurnNotification(String nickname);



}
