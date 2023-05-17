package it.polimi.ingsw.distributed.socket;

import it.polimi.ingsw.distributed.Client;
import it.polimi.ingsw.distributed.Server;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.util.ModelListener;
import it.polimi.ingsw.util.Warnings;

import java.rmi.RemoteException;

public class ServerHandler implements Server,Runnable, ModelListener {
    private String nickname;

    @Override
    public void clientConnection(Client c, String nickName) throws RemoteException {

    }

    @Override
    public void tileToDrop(int tilePosition) throws RemoteException {

    }

    @Override
    public void checkingCoordinates(int[] coordinates) throws RemoteException {

    }

    @Override
    public void columnSetting(int i) throws RemoteException {

    }

    @Override
    public void endsSelection() throws RemoteException {

    }

    @Override
    public void numberOfParticipantsSetting(int n) throws RemoteException {

    }

    @Override
    public void printGame() {

    }

    @Override
    public void error(Warnings e, Player currentPlayer) {

    }

    @Override
    public void newTurn(Player currentPlayer) {

    }

    @Override
    public void askOrder() {

    }

    @Override
    public void isLastTurn() {

    }

    @Override
    public void askColumn() {

    }

    @Override
    public void askAction() {

    }

    @Override
    public void run() {

    }
}
