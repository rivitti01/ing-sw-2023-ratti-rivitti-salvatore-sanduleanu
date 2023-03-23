package it.polimi.ingsw.model;

public class Shelf {
    private Tile[][] shelf;
    static final int rows = 6;
    static final int columns = 5;

    public Shelf(){
        shelf = new Tile[rows][columns];
    }
    public Shelf(Shelf shelf){
        this.shelf = shelf.shelf;
    }
    public Tile getTile(int row, int col){
        return this.shelf[row][col];
    }

    public void putTile(int row,int column, Tile tile){
        shelf[row][column] = tile;
    }
    public int checkColumnEmptiness(int column){
        int emptySquareCounter = 0;
        for (int i = 0; i < 5; i++){
            if (this.shelf[i][column]==null){
                emptySquareCounter++;
            }
        }
        return emptySquareCounter;
    }
    public boolean checkRowEmptiness(int row){
        for (int i = 0; i < 5; i++) {
            if (this.shelf[row][i]!=null){
                return false;
            }
        }
        return true;
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

    public Tile[][] getShelf() {
        return this.shelf.clone();
    }
    public Shelf copyShelf(){
        Shelf newShelf = new Shelf();
        for (int i = 0; i < rows; i++){
            for (int j = 0; j < columns; j++){
                newShelf.putTile(i,j,this.shelf[i][j]);
            }
        }
        return newShelf;
    }

    public int checkAdjacents(){
        Shelf copy = this.copyShelf();
        int points = 0;
        int count;
        for(int i=0; i<rows; i++){
            for (int j=0; j<columns; j++){
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
        if(x+1<rows && copy.getTile(x+1, y)!=null && copy.getTile(x+1, y).getColor().equals(tmp))
            count = countAdjacents(copy, x+1, y, count);
        if(x-1>=0 && copy.getTile(x-1, y)!=null && copy.getTile(x-1, y).getColor().equals(tmp))
            count = countAdjacents(copy, x-1, y, count);
        if(y+1<columns && copy.getTile(x, y+1)!=null && copy.getTile(x, y+1).getColor().equals(tmp))
            count = countAdjacents(copy, x, y+1, count);
        if(y-1>=0 && copy.getTile(x, y-1)!=null && copy.getTile(x, y-1).getColor().equals(tmp))
            count = countAdjacents(copy, x, y-1, count);

        return count;
    }



}
