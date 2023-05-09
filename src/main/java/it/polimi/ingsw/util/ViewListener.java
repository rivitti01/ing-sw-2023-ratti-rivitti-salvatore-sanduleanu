package it.polimi.ingsw.util;

import it.polimi.ingsw.model.Tile;

import java.util.List;

public interface ViewListener {
    void clientConnection(String nickName);
    void checkingCoordinates(int[] coordinates);
    void orederSetting(List<Tile> orderedTiles);
    void columnSetting(int i);
    void numberPartecipantsSetting(int n);
    void endsSelection();
}
