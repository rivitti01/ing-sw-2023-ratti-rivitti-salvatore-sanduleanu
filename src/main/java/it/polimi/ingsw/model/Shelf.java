package it.polimi.ingsw.model;

import static it.polimi.ingsw.util.Costants.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.List;


public class Shelf implements Serializable {
    private Tile[][] shelf;
    public Shelf(){
        shelf = new Tile[SHELF_ROWS][SHELF_COLUMN];
    }
    public Tile getTile(int row, int col){

        return this.shelf[row][col];
    }
    public void putTile(int row,int column, Tile tile){
        shelf[row][column] = tile;
    }
    public int checkColumnEmptiness(int column){ // return 0 if there is no space in the column. Return the number of space available
        int emptySquareCounter = 0;
        for (int i = 0; i < SHELF_ROWS; i++){
            if (this.shelf[i][column]==null){
                emptySquareCounter++;
            }
        }
        return emptySquareCounter;
    }
    public boolean checkRowFulness(int row){
        for (int i = 0; i < SHELF_COLUMN; i++) {
            if (this.shelf[row][i]==null){
                return false;
            }
        }
        return true;
    }  //returns true iff "row" is completely full of tiles
    public Tile[] getColumn(int index){ //returns a copy of the column, not the reference to that column
        Tile[] column = new Tile[SHELF_ROWS];
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
    public Tile[][] getShelf() {
        return this.shelf;
    }
    public int getMaxColumnSpace(){
        int maxSpace = 0;
        for (int i=0; i<SHELF_COLUMN; i++){
            if(checkColumnEmptiness(i) > maxSpace)
                maxSpace = checkColumnEmptiness(i);
        }
        return maxSpace;
    }
    public Shelf copyShelf(){
        Shelf newShelf = new Shelf();
        for (int i = 0; i < SHELF_ROWS; i++){
            for (int j = 0; j < SHELF_COLUMN; j++){
                newShelf.putTile(i,j,this.shelf[i][j]);
            }
        }
        return newShelf;
    }
    public int checkAdjacents(){
        Shelf copy = this.copyShelf();
        int points = 0;
        int count;
        for(int i=0; i<SHELF_ROWS; i++){
            for (int j=0; j<SHELF_COLUMN; j++){
                count = 0;
                if(copy.getTile(i, j) != null && copy.getTile(i, j).getColor()!=null){
                    count = countAdjacents(copy, i, j, count);
                    if (count == 3)
                        points += 2;
                    else if (count == 4)
                        points += 3;
                    else if(count == 5)
                        points += 5;
                    else if(count >= 6)
                        points += 8;
                }
            }
        }
        return points;
    }
    private int countAdjacents(Shelf copy, int x, int y, int count){
        Color tmp = copy.getTile(x, y).getColor();
        copy.putTile(x, y, null);
        count++;
        if(x+1<SHELF_ROWS && copy.getTile(x+1, y)!=null && copy.getTile(x+1, y).getColor().equals(tmp))
            count = countAdjacents(copy, x+1, y, count);
        if(x-1>=0 && copy.getTile(x-1, y)!=null && copy.getTile(x-1, y).getColor().equals(tmp))
            count = countAdjacents(copy, x-1, y, count);
        if(y+1<SHELF_COLUMN && copy.getTile(x, y+1)!=null && copy.getTile(x, y+1).getColor().equals(tmp))
            count = countAdjacents(copy, x, y+1, count);
        if(y-1>=0 && copy.getTile(x, y-1)!=null && copy.getTile(x, y-1).getColor().equals(tmp))
            count = countAdjacents(copy, x, y-1, count);

        return count;
    }
    public void dropTile(Tile tile, int column){
        int j=0;
        while(j+1<SHELF_ROWS && this.getTile(j+1, column)==null) {
            j++;
        }
        putTile(j, column, tile);
    }
    public boolean isFull(){
        for (int i = 0; i < SHELF_ROWS; i++){
            if (!checkRowFulness(i)){
                return false;
            }
        }
        return true;
    }
    public void setTile(int r, int c, Tile t){
        shelf[r][c]=t;
    }
}

