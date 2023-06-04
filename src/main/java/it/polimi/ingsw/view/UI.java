package it.polimi.ingsw.view;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.util.ViewListener;
import it.polimi.ingsw.util.Warnings;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

public interface UI {


    public abstract void newTurn(boolean b) throws RemoteException;


    public abstract void askOrder();


    public abstract void askColumn();


    public abstract void chooseAction() throws RemoteException;

    public abstract void askNumber() throws RemoteException;

    public abstract void askNickName() throws RemoteException;

    public abstract void warning(Warnings e) throws RemoteException;


    public abstract void lastTurnReached(String nickname);


    public abstract void printBoard(Board b);

    public abstract void printShelves(Map<String, Shelf> playerShelves);


    public abstract void printPersonalGoalShelf(PersonalGoalCard personalGoalCard);


    public abstract void printChosenTiles(List<Tile> chosenTiles, String nickname);


    public abstract void printFinalPoints(Map<String, Integer> chart);

    public abstract void addListener(ViewListener l);


    public abstract void printGame(GameView gameView);

    public abstract void printChat(ChatView chatView) throws RemoteException;

    public abstract void lastTurn(boolean currentPlayer);

    public abstract void waitingTurn() throws RemoteException;

    void gameStarted(boolean nickname);
}

