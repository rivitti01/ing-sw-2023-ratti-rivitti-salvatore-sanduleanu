package it.polimi.ingsw.distributed;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Server extends Remote {
    void clientConnection(Client c, String nickName) throws RemoteException;
    void tileToDrop(int tilePosition) throws RemoteException;
    void checkingCoordinates(int[] coordinates) throws RemoteException;
    void columnSetting(int i) throws RemoteException;
    void endsSelection() throws RemoteException;
    void numberOfParticipantsSetting(int n) throws RemoteException;

}
