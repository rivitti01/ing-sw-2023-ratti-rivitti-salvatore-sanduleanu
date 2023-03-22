package it.polimi.ingsw.model.Board;

import it.polimi.ingsw.model.Bag;
import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.Tile;

import java.util.ArrayList;
import java.util.List;

public abstract class Board {
    private Tile[][] board;
    private final int size;

    public Board(int size){
        this.size = size;
        this.board = new Tile[size][size];
    }
    public Tile[][] getBoard(){
        return board;
    }
    public abstract void setupBoard();
    public Tile getTile(int x, int y){
        return board[x][y];
    }
    public boolean checkRefill(){
        for(int i=0; i<size-1; i++){
            for(int j=0; j<size-1; j++) {
                if(getTile(i, j).getColor() != null && !getTile(i, j).getColor().equals(Color.TRANSPARENT)){
                    if(getTile(i+1,j).getColor() != null && !getTile(i+1, j).getColor().equals(Color.TRANSPARENT))
                        return false;
                    else if(getTile(i,j+1).getColor() != null && !getTile(i, j+1).getColor().equals(Color.TRANSPARENT))
                        return false;
                }
            }
        }
        for(int i=0; i<size-1; i++){
            if(getTile(i, size-1).getColor() != null && !getTile(i, size-1).getColor().equals(Color.TRANSPARENT)){
                if(getTile(i+1, size-1).getColor() != null && !getTile(i+1, size-1).getColor().equals(Color.TRANSPARENT))
                    return false;
            }
        }
        return true;
    }
    public List<Tile> getAvailableTiles() {
        List<Tile> availableTiles = new ArrayList<Tile>();
        for(int i=0; i<size; i++){
            if(getTile(i, 0) != null && !getTile(i, 0).getColor().equals(Color.TRANSPARENT))
                availableTiles.add(getTile(i, 0));
            if(getTile(0, i) != null && !getTile(0, i).getColor().equals(Color.TRANSPARENT))
                availableTiles.add(getTile(0, i));
            if(getTile(i, size-1) != null && !getTile(i, size-1).getColor().equals(Color.TRANSPARENT))
                availableTiles.add(getTile(i, size-1));
            if(getTile(size-1, i) != null && !getTile(size-1, i).getColor().equals(Color.TRANSPARENT))
                availableTiles.add(getTile(size-1, i));
        }
        for (int i=1; i<size-1; i++){
            for(int j=1; j<size-1; j++){
                if(getTile(i, j) != null && !getTile(i, j).getColor().equals(Color.TRANSPARENT)){
                    if (getTile(i+1, j) == null || getTile(i, j+1) == null || getTile(i-1, j) == null || getTile(i, j-1) == null ||
                            getTile(i+1, j).getColor().equals(Color.TRANSPARENT) || getTile(i, j+1).getColor().equals(Color.TRANSPARENT) || getTile(i-1, j).getColor().equals(Color.TRANSPARENT) || getTile(i, j-1).getColor().equals(Color.TRANSPARENT)) {
                        availableTiles.add(getTile(i, j));
                    }
                }
            }
        }
        return availableTiles;
    }
    public Tile popTile(int x, int y){
        Tile temp = getTile(x, y);
        board[x][y] = null;
        return temp;
    }
    public void fillBoard(Bag bag){
        for(int i=0; i<size; i++){
            for(int j=0; j<size; j++){
                if(getTile(i, j).getColor() == null)
                    board[i][j] = bag.getTile();
            }
        }
    }
    public void makeBoard(List<Tile> newBoard){
        for (int i = 0; i< size; i++){
            for (int j = 0; j < size; j++){
                board[i][j] = newBoard.remove(0);
            }
        }

    }
}
