package it.polimi.ingsw.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Board implements Serializable {
    private Tile[][] board;
    private int size;
    private List<int[]> borderTiles;


    public Board(int numberParticipants){
        String name = "Board" + numberParticipants;
        setupBoard(name);
        this.borderTiles = new ArrayList<>();
    }

    public Tile[][] getBoard(){
        return board;
    }

    private void setupBoard(String boardName){
        String filePath = "src/main/resources/BoardFactor.json";
        File input = new File(filePath);
        List<Tile> newBoard = new ArrayList<>();
        try {
            JsonElement fileElement = JsonParser.parseReader(new FileReader(input));
            JsonObject fileObject = fileElement.getAsJsonObject();
            JsonArray jsonArrayOfTiles = fileObject.get(boardName).getAsJsonArray();
            for (JsonElement tileElement : jsonArrayOfTiles){
                JsonObject tileObject = tileElement.getAsJsonObject();
                String color = tileObject.get("color").getAsString();
                if (!color.equals("TRANSPARENT")){
                    newBoard.add(new Tile(null));
                }else {
                    newBoard.add(new Tile(Color.TRANSPARENT));
                }
            }
        } catch (FileNotFoundException | NullPointerException e) {
            System.err.println("File BoardFactor.json not found, or invalid number of participant ");
            e.printStackTrace();
        } catch (Exception e){
            System.err.println("File read warning: BoardFactor.json!!");
            e.printStackTrace();
        }
        size = (int) Math.sqrt(newBoard.size());
        board = new Tile[size][size];
        makeBoard(newBoard);
    }

    public int getSize() {
        return size;
    }

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
        for(int i=0; i<size-1; i++){
            if(getTile(size-1, i).getColor() != null && !getTile(size-1, i).getColor().equals(Color.TRANSPARENT)){
                if(getTile(size-1, i+1 ).getColor() != null && !getTile(size-1,i+1).getColor().equals(Color.TRANSPARENT))
                    return false;
            }
        }
        return true;
    }

    // serve all inizio della scelta del giocatore un riferimento alle Tiles disponibili prima delle scelte del giocatore per far funzionare bene il filtro
    public void setBorderTiles() {
        this.borderTiles = getAvailableTiles();
    }

    public List<int[]> getBorderTiles() {
        return borderTiles;
    }

    public List<int[]> getAvailableTiles(){
        List<int[]> availableTiles = new ArrayList<>();
        int[] pos;

        for(int i=0; i<size; i++){
            if(getTile(i, 0).getColor() != null && !getTile(i,0).getColor().equals(Color.TRANSPARENT)){
                pos = new int[2];
                pos[0] = i;
                pos[1] = 0;
                availableTiles.add(pos);
            }
            if(getTile(0, i).getColor() != null && !getTile(0,i).getColor().equals(Color.TRANSPARENT)){
                pos = new int[2];
                pos[0] = 0;
                pos[1] = i;
                availableTiles.add(pos);
            }
            if(getTile(i, size-1).getColor() != null && !getTile(i,size-1).getColor().equals(Color.TRANSPARENT)){
                pos = new int[2];
                pos[0] = i;
                pos[1] = size-1;
                availableTiles.add(pos);
            }
            if(getTile(size-1, i).getColor() != null && !getTile(size-1,i).getColor().equals(Color.TRANSPARENT)){
                pos = new int[2];
                pos[0] = size-1;
                pos[1] = i;
                availableTiles.add(pos);
            }
        }
        for (int i=1; i<size-1; i++){
            for(int j=1; j<size-1; j++){
                if(getTile(i, j).getColor() != null && !getTile(i, j).getColor().equals(Color.TRANSPARENT)){
                    if (getTile(i+1, j).getColor() == null || getTile(i, j+1).getColor() == null || getTile(i-1, j).getColor() == null || getTile(i, j-1).getColor() == null ||
                            getTile(i+1, j).getColor().equals(Color.TRANSPARENT) || getTile(i, j+1).getColor().equals(Color.TRANSPARENT) ||
                            getTile(i-1, j).getColor().equals(Color.TRANSPARENT) || getTile(i, j-1).getColor().equals(Color.TRANSPARENT)) {
                        pos = new int[2];
                        pos[0] = i;
                        pos[1] = j;
                        availableTiles.add(pos);
                    }
                }
            }
        }
        return availableTiles;

    }

    public List<int[]> filterAvailableTiles(int[] t1, int[] t2, List<int[]> borderTiles){
        //seleziona dalle available quelle con stesse coordinate di una tile o quelle che hanno una coordinata in comune con le altre due selezionate
        List<int[]> goodTiles = new ArrayList<>();
        if(t1==null && t2==null)
            return borderTiles;
        else if(t1!=null && t2==null){
            for (int[] i: borderTiles) {
                if (t1[0] == i[0] && (t1[1] == i[1] - 1 || t1[1] == i[1] + 1)) {

                    if (!Arrays.equals(t1, i)){
                        goodTiles.add(i);
                    }
                } else if ((t1[0] == i[0] - 1 || t1[0] == i[0] + 1) && t1[1] == i[1]) {
                    if (!Arrays.equals(t1, i)){
                        goodTiles.add(i);
                    }
                }
            }
            return goodTiles;
        }else if(t1!=null && t2!=null){
            for (int[] i: borderTiles) {
                if (t1[0] == t2[0]) {
                    if (i[0]==t1[0] && (i[1]==t1[1]+1 ||  i[1] == t1[1]-1 || i[1]==t2[1]+1 || i[1] == t2[1]-1)) {
                        if (!Arrays.equals(t1, i) && !Arrays.equals(t2, i)){
                            goodTiles.add(i);
                        }
                    }
                } else if (t1[1] == t2[1]) {
                    if (i[1] == t1[1] && (i[0]==t1[0]+1 ||  i[0] == t1[0]-1 || i[0]==t2[0]+1 || i[0] == t2[0]-1)) {
                        if (!Arrays.equals(t1, i) && !Arrays.equals(t2, i) ){
                            goodTiles.add(i);
                        }
                    }
                }
            }
        }
        return goodTiles;
    }

    private Tile[][] copyBoard(){
        Tile[][] boardCopy = new Tile[this.board.length][this.board.length];
        for (int i=0; i<this.board.length; i++){
            for (int j=0; j<this.board.length; j++){
                boardCopy[i][j] = this.getTile(i, j);
            }
        }
        return boardCopy;
    }

    public Tile popTile(int x, int y){
        Tile[][] bCopy = copyBoard();
        Tile temp = getTile(x, y);
        board[x][y] = new Tile(null); //prima era board[x][y] = null;
        return temp;
    }

    public void fillBoard(Bag bag){
        for(int i=0; i<size; i++){
            for(int j=0; j<size; j++){
                if(getTile(i, j)==null || getTile(i, j).getColor()==null)
                    board[i][j] = bag.getTile();
            }
        }
    }

    private void makeBoard(List<Tile> newBoard){
        for (int i = 0; i< size; i++){
            for (int j = 0; j < size; j++){
                board[i][j] = newBoard.remove(0);
            }
        }

    }
}
