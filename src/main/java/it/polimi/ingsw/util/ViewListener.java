package it.polimi.ingsw.util;

import java.rmi.RemoteException;

public interface ViewListener {
    void clientNickNameSetting(String nickName) throws RemoteException;
    void checkingCoordinates(int[] coordinates) throws RemoteException;
    void tileToDrop(int tilePosition)   throws RemoteException  ;
    void columnSetting(int i) throws RemoteException;
    void numberPartecipantsSetting(int n) throws RemoteException;
    void endsSelection() throws RemoteException;

    void newMessage(String message) throws RemoteException;


}
