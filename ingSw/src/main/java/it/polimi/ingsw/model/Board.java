package it.polimi.ingsw.model;

import java.util.List;

public class Board {
    private Tile[][] board;

    public Board(){
        board = new Tile[9][9];
    }

    public Tile getTile(int[] position){
        return board[position[0]][position[1]];
    }
    public boolean checkRefill(){

        return false;
    }

    public List<Tile> getAvailableTiles() {
        return null;
    }

    public void fillBoard(){}

}


