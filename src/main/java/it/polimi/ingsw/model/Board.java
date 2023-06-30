package it.polimi.ingsw.model;

import com.google.gson.*;
import it.polimi.ingsw.Main;


import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.util.Objects;

/**
 * The Board class represents the living room of the game.
 */
public class Board implements Serializable {
    private Tile[][] board;
    private int size;
    private List<int[]> borderTiles;


    /**
     * Constructs a new Board instance.
     *
     * @param numberParticipants the number of participants in the game
     */
    public Board(int numberParticipants){
        String name = "Board" + numberParticipants;
        setupBoard(name);
        this.borderTiles = new ArrayList<>();
    }

    /**
     * Returns the game board.
     *
     * @return the game board
     */
    public Tile[][] getBoard(){
        return board;
    }

    /**
     * Places a tile on the board at the specified coordinates.
     *
     * @param tile       the tile to be placed
     * @param coordinates the coordinates where the tile should be placed
     */
    public void putTile(Tile tile, int[] coordinates){
        board[coordinates[0]] [coordinates[1]] = tile;
    }

    /**
     * Returns the size of the game board.
     *
     * @return the size of the game board
     */

    private void setupBoard(String boardName){
        Reader readerConfig = new InputStreamReader(Objects.requireNonNull(this.getClass().getResourceAsStream("/BoardFactor.json")));
        List<Tile> newBoard = new ArrayList<>();
        try {
            JsonElement fileElement = JsonParser.parseReader(readerConfig);
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
        } catch (NullPointerException e) {
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

    /**
     * Retrieves the tile at the specified coordinates on the game board.
     *
     * @param x the x-coordinate of the tile
     * @param y the y-coordinate of the tile
     * @return the tile at the specified coordinates
     */
    public Tile getTile(int x, int y){
        return board[x][y];
    }

    /**
     * Checks the tiles on the board and determines if there are any empty spaces or gaps that need to be filled.
     * It returns true if the board needs to be refilled,
     * indicating that there are no adjacent tiles of non-transparent color, and false otherwise.
     *
     * @return true if the board needs to be refilled, false otherwise
     */
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

    /**
     * Sets the border tiles of the game board.
     * The border tiles are the available tiles that can be popped during a turn by
     * a player from the board during gameplay.
     * They are filtered based on the player's choices during their turn
     */
    public void setBorderTiles() {
        this.borderTiles = getAvailableTiles();
    }

    /**
     * Returns the list of border tiles.
     *
     * @return The list of border tiles, which are the available tiles on the board during gameplay.
     */
    public List<int[]> getBorderTiles() {
        return borderTiles;
    }

    /**
     * Returns the list of available tiles.
     * They are tiles that have at least one empty adjacent empty cell or transparent tile.
     *
     * @return The list of available tiles.
     */
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

    /**
     * Filters the available tiles based on the tiles chosen by the player
     * and the border tiles of that turn.
     *
     * @param chosenTile1 The first chosen tile.
     * @param chosenTile2 The second chosen tile.
     * @param borderTiles The list of border tiles.
     * @return The list of filtered available tiles.
     */
    public List<int[]> filterAvailableTiles(int[] chosenTile1, int[] chosenTile2, List<int[]> borderTiles){
        //seleziona dalle available quelle con stesse coordinate di una tile o quelle che hanno una coordinata in comune con le altre due selezionate
        List<int[]> goodTiles = new ArrayList<>();
        if(chosenTile1==null && chosenTile2==null)
            return borderTiles;
        else if(chosenTile1!=null && chosenTile2==null){
            for (int[] i: borderTiles) {
                if (chosenTile1[0] == i[0] && (chosenTile1[1] == i[1] - 1 || chosenTile1[1] == i[1] + 1)) {

                    if (!Arrays.equals(chosenTile1, i)){
                        goodTiles.add(i);
                    }
                } else if ((chosenTile1[0] == i[0] - 1 || chosenTile1[0] == i[0] + 1) && chosenTile1[1] == i[1]) {
                    if (!Arrays.equals(chosenTile1, i)){
                        goodTiles.add(i);
                    }
                }
            }
            return goodTiles;
        }else if(chosenTile1!=null && chosenTile2!=null){
            for (int[] i: borderTiles) {
                if (chosenTile1[0] == chosenTile2[0]) {
                    if (i[0]==chosenTile1[0] && (i[1]==chosenTile1[1]+1 ||  i[1] == chosenTile1[1]-1 || i[1]==chosenTile2[1]+1 || i[1] == chosenTile2[1]-1)) {
                        if (!Arrays.equals(chosenTile1, i) && !Arrays.equals(chosenTile2, i)){
                            goodTiles.add(i);
                        }
                    }
                } else if (chosenTile1[1] == chosenTile2[1]) {
                    if (i[1] == chosenTile1[1] && (i[0]==chosenTile1[0]+1 ||  i[0] == chosenTile1[0]-1 || i[0]==chosenTile2[0]+1 || i[0] == chosenTile2[0]-1)) {
                        if (!Arrays.equals(chosenTile1, i) && !Arrays.equals(chosenTile2, i) ){
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

    /**
     * Pops the tile at the specified position (x, y) from the board.
     * Assigns a new Tile object with null color to the corresponding position in the board
     *
     * @param x The x-coordinate of the tile.
     * @param y The y-coordinate of the tile.
     * @return The tile that was popped from the board.
     */
    public Tile popTile(int x, int y){
        Tile[][] bCopy = copyBoard();
        Tile temp = getTile(x, y);
        board[x][y] = new Tile(null); //prima era board[x][y] = null;
        return temp;
    }

    /**
     * Fills the empty tiles on the board with tiles from the provided bag.
     *
     * @param bag The bag from which to draw tiles.
     */
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
