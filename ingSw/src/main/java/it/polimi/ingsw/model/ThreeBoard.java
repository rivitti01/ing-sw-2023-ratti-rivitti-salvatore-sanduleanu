package it.polimi.ingsw.model;

public class ThreeBoard extends Board{
    private Tile[][] board;

    public Board(3){
        board = new Tile[9][9];
    }

    public void print(){
        System.out.println("3");
    }
}
