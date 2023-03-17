package it.polimi.ingsw.model;

public class TwoBoard extends Board{
    private Tile[][] board;

    public Board(2){
        board = new Tile[9][9];
    }

    public void print(){
        System.out.println("2");
    }
}
