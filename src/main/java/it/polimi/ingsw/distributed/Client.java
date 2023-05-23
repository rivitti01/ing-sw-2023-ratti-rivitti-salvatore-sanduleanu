package it.polimi.ingsw.distributed;

import it.polimi.ingsw.model.ChatView;
import it.polimi.ingsw.model.GameView;
import it.polimi.ingsw.util.Warnings;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

public interface Client extends Remote {
    void printGame(GameView gameView) throws RemoteException;
    void finalPoints(Map<String, Integer> finalPoints) throws RemoteException;
    void warning(Warnings e) throws RemoteException;
    void askNumberParticipants() throws RemoteException;
    void newTurn() throws RemoteException;
    void lastTurn() throws RemoteException;
    void askOrder() throws RemoteException;
    void lastTurnNotification(String nickname) throws RemoteException;
    void askColumn() throws RemoteException;
    void askAction() throws RemoteException;
    void printChat(ChatView chatView) throws RemoteException;
    void chatAvailable() throws RemoteException;
    void askNickname() throws RemoteException;
}
