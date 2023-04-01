package it.polimi.ingsw.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class Board {
    private Tile[][] board;
    private int size;

    public Board(int numberParticipants){
        String name = "Board" + numberParticipants;
        setupBoard(name);
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
            System.err.println("File read error: BoardFactor.json!!");
            e.printStackTrace();
        }
        size = (int) Math.sqrt(newBoard.size());
        board = new Tile[size][size];
        makeBoard(newBoard);
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
        return true;
    }

    public List<int[]> getAvailableTiles2(){
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


    public List<int[]> getAvailableTiles2(int[] t1, int[] t2){
        //seleziona dalle available quelle con stesse coordinate di una tile o quelle che hanno una coordinata in comune con le altre due selezionate
        List<int[]> borderTiles = getAvailableTiles2();
        if(t1==null && t2==null)
            return borderTiles;
        else if(t1!=null && t2==null){
            borderTiles.removeIf(i -> i[0] != t1[0] || i[1] != t1[1]);
            return borderTiles;
        }else if(t1!=null && t2!=null){
            borderTiles.removeIf(i -> (t1[0] == t2[0] && i[0] != t1[0]) || (t1[1] == t2[1] && i[1] != t1[1]));
            return borderTiles;
        }
        return borderTiles;
    }
    public List<Tile> getAvailableTiles() {
        List<Tile> availableTiles = new ArrayList<>();
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
