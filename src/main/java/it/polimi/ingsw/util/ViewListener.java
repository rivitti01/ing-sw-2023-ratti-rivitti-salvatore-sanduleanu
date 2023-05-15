package it.polimi.ingsw.util;

public interface ViewListener {
    void clientConnection(String nickName);
    void checkingCoordinates(int[] coordinates);
    void tileToDrop(int tilePosition);
    void columnSetting(int i);
    void numberParticipantsSetting(int n);
    void endsSelection();

}
