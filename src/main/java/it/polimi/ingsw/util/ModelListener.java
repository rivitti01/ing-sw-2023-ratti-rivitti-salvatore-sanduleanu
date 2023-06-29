package it.polimi.ingsw.util;

import it.polimi.ingsw.model.Player;

public interface ModelListener extends java.util.EventListener {

        void printGame();
        void warning(Warnings e, Player currentPlayer);
        void newTurn(Player currentPlayer);
        void askOrder();
        void isLastTurn();
        void askColumn();
        void askAction();
        void finalPoints();
        void gameStarted(Player currentPlayer);
        void warning(Warnings errorType, String nickname);
        void onePlayerLeft(Player theOnlyPlayerLeft, int countdownToEnd);
        void playerDisconnected(String nickname);
        void playerReconnected(String nickname);
        void resumingTurn();
}
