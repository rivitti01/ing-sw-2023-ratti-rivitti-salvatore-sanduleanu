package it.polimi.ingsw.util;

import it.polimi.ingsw.distributed.Client;
import it.polimi.ingsw.model.Player;

import java.rmi.RemoteException;

public interface ModelListener extends java.util.EventListener {

        void printGame();
        void error(Warnings e, Player currentPlayer);
        void newTurn(Player currentPlayer);
        void askOrder();
        void isLastTurn();
        void askColumn();




}
