package it.polimi.ingsw.util;

import it.polimi.ingsw.distributed.Client;
import it.polimi.ingsw.model.Player;

public interface ModelListener extends java.util.EventListener {


        void printGame();

        void error(ErrorType e, Player currentPlayer);

        void newTurn(Player currentPlayer);
        void askNumberPartecipants();

}
