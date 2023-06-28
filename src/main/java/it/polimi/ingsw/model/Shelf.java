

package it.polimi.ingsw.model;

import static it.polimi.ingsw.util.Costants.*;

import java.io.Serializable;


/**
 * The Shelf class represents the shelf where tiles are placed in the game.
 */
public class Shelf implements Serializable {
    private Tile[][] shelf;

    /**
     * Constructs a new Shelf object.
     */
    public Shelf(){
        shelf = new Tile[SHELF_ROWS][SHELF_COLUMN];
    }

    /**
     * Returns the tile at the specified row and column.
     *
     * @param row the row index
     * @param col the column index
     * @return the tile at the specified row and column
     */
    public Tile getTile(int row, int col){
        return this.shelf[row][col];
    }

    /**
     * Puts a tile at the specified row and column.
     *
     * @param row the row index
     * @param column the column index
     * @param tile the tile to be placed
     */
    public void putTile(int row,int column, Tile tile){
        shelf[row][column] = tile;
    }

    /**
     * Checks the number of empty squares in a column.
     *
     * @param column the column index
     * @return the number of empty squares in the column
     */
    public int checkColumnEmptiness(int column){
        int emptySquareCounter = 0;
        for (int i = 0; i < SHELF_ROWS; i++){
            if (this.shelf[i][column]==null){
                emptySquareCounter++;
            }
        }
        return emptySquareCounter;
    }

    /**
     * Checks if a row is completely full of tiles (useful for CommonGoalCard8 algorithm).
     *
     * @param row the row index
     * @return true if the row is full of tiles, false otherwise
     */
    public boolean checkRowFullness(int row){
        for (int i = 0; i < SHELF_COLUMN; i++) {
            if (this.shelf[row][i]==null){
                return false;
            }
        }
        return true;
    }

    /**
     * Returns a copy of the column at the specified index (useful for CommonGoalCard5 algorithm).
     *
     * @param index the column index
     * @return a copy of the column
     */
    public Tile[] getColumn(int index){
        Tile[] column = new Tile[SHELF_ROWS];
        for(int i=0; i<column.length; i++){
            column[i] = this.shelf[i][index];
        }
        return column;
    }

    /**
     * Returns the row at the specified index.
     *
     * @param index the row index
     * @return the row at the specified index
     */
    public Tile[] getRow(int index){
        Tile[] row;
        row = this.shelf[index];
        return row;
    }

    /**
     * Returns the maximum number of empty spaces in a column.
     *
     * @return the maximum number of empty spaces in a column
     */
    public int getMaxColumnSpace(){
        int maxSpace = 0;
        for (int i=0; i<SHELF_COLUMN; i++){
            if(checkColumnEmptiness(i) > maxSpace)
                maxSpace = checkColumnEmptiness(i);
        }
        return maxSpace;
    }

    /**
     * Creates a copy of the shelf (useful for CommonGoalCards algorithms).
     *
     * @return a copy of the shelf
     */
    public Shelf copyShelf(){
        Shelf newShelf = new Shelf();
        for (int i = 0; i < SHELF_ROWS; i++){
            for (int j = 0; j < SHELF_COLUMN; j++){
                newShelf.putTile(i,j,this.shelf[i][j]);
            }
        }
        return newShelf;
    }

    /**
     * Checks the adjacency of tiles on the shelf and calculates the points based on the adjacency rules.
     *
     * @return the points earned from tile adjacency
     */
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

    /**
     * Drops a tile onto the specified column of the shelf.
     *
     * @param tile   the tile to be dropped onto the shelf
     * @param column the column index where the tile should be dropped
     */
    public void dropTile(Tile tile, int column){
        int j=0;
        while(j+1<SHELF_ROWS && this.getTile(j+1, column)==null) {
            j++;
        }
        putTile(j, column, tile);
    }

    /**
     * Checks if the shelf is full.
     *
     * @return {@code true} if the shelf is full, {@code false} otherwise
     */
    public boolean isFull(){
        for (int i = 0; i < SHELF_ROWS; i++){
            if (!checkRowFullness(i)){
                return false;
            }
        }
        return true;
    }

    /**
     * Sets a tile at the specified position on the shelf.
     *
     * @param r the row index of the tile's position
     * @param c the column index of the tile's position
     * @param t the tile to be set
     */
    public void setTile(int r, int c, Tile t){
        shelf[r][c]=t;
    }
}