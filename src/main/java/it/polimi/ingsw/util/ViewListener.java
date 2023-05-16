package it.polimi.ingsw.util;

import it.polimi.ingsw.model.Tile;

import java.rmi.RemoteException;
import java.util.List;

public interface ViewListener {
    void clientConnection(String nickName) throws RemoteException;
    void checkingCoordinates(int[] coordinates) throws RemoteException;
    void tileToDrop(int tilePosition) throws RemoteException;
    void columnSetting(int i) throws RemoteException;
    void numberPartecipantsSetting(int n) throws RemoteException;
    void endsSelection() throws RemoteException;

}
