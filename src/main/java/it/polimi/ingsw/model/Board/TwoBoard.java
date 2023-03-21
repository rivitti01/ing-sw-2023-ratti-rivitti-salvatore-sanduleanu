package it.polimi.ingsw.model.Board;

import com.google.gson.*;
import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.Tile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class TwoBoard extends Board{
    private Board twoBoard;

    public TwoBoard(int players){
        super(7);
    }
    @Override
    public void setupBoard() {
        //read from json file
        String filePath = "src/main/resources/BoardFactor.json";
        File input = new File(filePath);
        List<Tile> newBoard = new ArrayList<>();
        try {
            JsonElement fileElement = JsonParser.parseReader(new FileReader(input));
            JsonObject fileObject = fileElement.getAsJsonObject();
            JsonArray jsonArrayOfTiles = fileObject.get("twoBoard").getAsJsonArray();
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
            System.err.println("File non trovato");
            e.printStackTrace();
        } catch (Exception e){
            System.err.println("Errore di lettura del file FourBoard.json");
            e.printStackTrace();
        }
        super.makeBoard(newBoard);
    }

}
