package it.polimi.ingsw.view;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.util.ViewListener;
import it.polimi.ingsw.util.Warnings;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

public interface UI {


    void newTurn(boolean b) throws RemoteException;

    void resumingTurn(boolean playing) throws RemoteException;

    void askOrder();


    void askColumn();


    void chooseAction() throws RemoteException;

    void askNumber() throws RemoteException;

    void askNickName() throws RemoteException;

    void warning(Warnings e) throws RemoteException;


    void lastTurnReached(String nickname);


    void printBoard(Board b);

    void printShelves(Map<String, Shelf> playerShelves);


    void printPersonalGoalShelf(PersonalGoalCard personalGoalCard);


    void printChosenTiles(List<Tile> chosenTiles, String nickname);


    void printFinalPoints(Map<String, Integer> chart, String winnerNickname);

    void addListener(ViewListener l);


    void printGame(GameView gameView);

    void printChat(ChatView chatView) throws RemoteException;

    void lastTurn(boolean currentPlayer);

    void gameStarted(boolean nickname);

    void askExistingNickname();

    public void clientReconnected(String nickname);

    public void clientDisconnected(String nickname);
}

