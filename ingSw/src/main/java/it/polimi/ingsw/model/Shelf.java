package it.polimi.ingsw.model;

public class Shelf {
    private Tile[][] shelf;

    public Shelf(){
        shelf = new Tile[6][5];
    }
    public Tile getTile(int row, int col){
        return shelf[row][col];
    }

    public void putTile(int column){

    }
    public int checkEmptyColumn(int column){

        return column;
    }
    public int checkAdjacents(){

        return 0;
    }
}
