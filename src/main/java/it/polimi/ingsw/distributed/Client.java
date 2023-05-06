package it.polimi.ingsw.distributed;

import it.polimi.ingsw.model.GameView;

import java.rmi.Remote;

public interface Client extends Remote {
    void printGame(GameView gameView);
    void nameError();
    void chosenTileError();
    int askNumberPartecipants();
    void chosenColumnError();
    void run();
}
