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
    public int checkColumnEmptiness(int column){

        return 0;
    }
    public boolean checkEmptyRow(int column){

        return false;
    }
    public Tile[] getColumn(int index){ //returns a copy of the column, not the reference to that column
        Tile[] column = new Tile[6];
        for(int i=0; i<column.length; i++){
            column[i] = this.shelf[i][index];
        }
        return column;
    }

    public Tile[] getRow(int index){
        Tile[] row;
        row = this.shelf[index];
        return row;
    }

    public int checkAdjacents(){

        return 0;
    }
}
