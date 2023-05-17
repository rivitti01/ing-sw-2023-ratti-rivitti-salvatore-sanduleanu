package it.polimi.ingsw.distributed.socket;

import it.polimi.ingsw.distributed.Client;
import it.polimi.ingsw.model.GameView;
import it.polimi.ingsw.util.ViewListener;
import it.polimi.ingsw.util.Warnings;

import java.rmi.RemoteException;

public class ClientSocketImpl implements Client, ViewListener {

    @Override
    public void printGame(GameView gameView) throws RemoteException {

    }

    @Override
    public void error(Warnings e) throws RemoteException {

    }

    @Override
    public void askNumberParticipants() throws RemoteException {

    }

    @Override
    public void newTurn() throws RemoteException {

    }

    @Override
    public void lastTurn() throws RemoteException {

    }

    @Override
    public void askOrder() throws RemoteException {

    }

    @Override
    public void lastTurnNotification(String nickname) throws RemoteException {

    }

    @Override
    public void askColumn() throws RemoteException {

    }

    @Override
    public void askAction() throws RemoteException {

    }

    @Override
    public void clientConnection(String nickName) throws RemoteException {

    }

    @Override
    public void checkingCoordinates(int[] coordinates) throws RemoteException {

    }

    @Override
    public void tileToDrop(int tilePosition) throws RemoteException {

    }

    @Override
    public void columnSetting(int i) throws RemoteException {

    }

    @Override
    public void numberPartecipantsSetting(int n) throws RemoteException {

    }

    @Override
    public void endsSelection() throws RemoteException {

    }
}
