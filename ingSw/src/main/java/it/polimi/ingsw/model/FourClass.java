package it.polimi.ingsw.model;

public class FourClass extends Board {
    private Tile[][] board;

    public Board(4){
        board = new Tile[9][9];
    }

    public void print(){
        System.out.println("4");
    }
}
