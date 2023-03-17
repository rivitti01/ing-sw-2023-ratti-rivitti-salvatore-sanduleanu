package it.polimi.ingsw.model;

public class Shelf {
    private Tile[][] shelf;

    public Shelf(){
        shelf = new Tile[6][5];
    }
    public Shelf(Shelf shelf){
        this.shelf = shelf.shelf;
    }
    public Tile getTile(int row, int col){
        return shelf[row][col];
    }

    public void putTile(int row,int column, Tile tile){
        shelf[row][column] = tile;
    }
    public int checkEmptyColumn(int column){

        return column;
    }
    public int checkAdjacents(){

        return 0;
    }
}
