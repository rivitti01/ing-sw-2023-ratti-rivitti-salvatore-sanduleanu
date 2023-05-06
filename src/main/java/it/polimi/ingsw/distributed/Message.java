package it.polimi.ingsw.distributed;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.List;

public class Message implements Serializable {
    private final Integer numberPlayers;
    private final Integer column;
    private final String nickName;
    private final List<int[]> coordinates;

    public Message(int numberPlayers, String nickName) {
        this.numberPlayers = numberPlayers;
        this.nickName = nickName;
        column = null;
        coordinates = null;
    }
    public Message(int numberPlayers) {
        this.numberPlayers = numberPlayers;
        this.nickName = null;
        column = null;
        coordinates = null;
    }
    public Message(int column, List<int[]> coordinatesAlreadySorted){
        this.column = column;
        this.coordinates = coordinatesAlreadySorted;
        nickName = null;
        numberPlayers = null;
    }
    public Message(String nickName){
        this.nickName = nickName;
        numberPlayers = null;
        coordinates = null;
        column = null;
    }

    public Integer getNumberPlayers() {
        return numberPlayers;
    }

    public Integer getColumn() {
        return column;
    }

    public String getNickName() {
        return nickName;
    }

    public List<int[]> getCoordinates() {
        return coordinates;
    }


}
