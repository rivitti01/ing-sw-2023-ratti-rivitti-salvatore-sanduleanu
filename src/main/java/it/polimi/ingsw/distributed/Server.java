package it.polimi.ingsw.distributed;

import it.polimi.ingsw.model.Tile;

import java.util.List;

public interface Server{
    void clientConnection(Client c, String nickName);
    void orederSetting(List<Tile> chosenTiles);
    void checkingCoordinates(int[] coordinates);
    void columnSetting(int i);
}
