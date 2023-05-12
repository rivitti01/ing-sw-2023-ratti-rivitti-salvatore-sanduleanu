package it.polimi.ingsw.distributed;

import it.polimi.ingsw.model.Tile;

import java.rmi.Remote;
import java.util.List;

public interface Server extends Remote {
    void clientConnection(Client c, String nickName);
    void tileToDrop(int tilePosition);
    void checkingCoordinates(int[] coordinates);
    void columnSetting(int i);
    void endsSelection();

}
