package it.polimi.ingsw.distributed;

import it.polimi.ingsw.model.ChatView;
import it.polimi.ingsw.model.GameView;
import it.polimi.ingsw.util.Warnings;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

public interface Client extends Remote {
    void printGame(GameView gameView) throws RemoteException;
    void finalPoints(Map<String, Integer> finalPoints, String winnerNickname) throws RemoteException;
    void warning(Warnings e) throws RemoteException;
    void askNumberParticipants() throws RemoteException;
    void newTurn(boolean playing) throws RemoteException;

    void lastTurn(boolean currentPlayer) throws RemoteException;
    void askOrder() throws RemoteException;
    void lastTurnNotification(String nickname) throws RemoteException;
    void askColumn() throws RemoteException;
    void askAction() throws RemoteException;
    void askNickname() throws RemoteException;
    void askExistingNickname() throws RemoteException;
    void ping() throws RemoteException;
    void setNickname(String nickname) throws RemoteException;
    void gameStarted(boolean youTurn) throws RemoteException;
    void setID(int id) throws RemoteException;
    int getID() throws RemoteException;
    void resumingTurn(boolean playing) throws RemoteException;
    void clientReconnected(String nickname) throws RemoteException;
    void clientDisconnected(String nickname) throws RemoteException;

}
