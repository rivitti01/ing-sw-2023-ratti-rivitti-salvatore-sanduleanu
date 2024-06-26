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
        void finalPoints(String winnerNickname);
        void gameStarted(Player currentPlayer);
        void warning(Warnings errorType, String nickname);
        void playerDisconnected(String nickname);
        void playerReconnected(String nickname);
        void resumingTurn();
}
