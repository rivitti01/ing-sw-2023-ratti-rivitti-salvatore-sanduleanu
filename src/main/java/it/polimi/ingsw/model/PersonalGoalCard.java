package it.polimi.ingsw.model;
import static it.polimi.ingsw.util.Costants.*;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Serializable;

public class PersonalGoalCard implements Serializable {
    final Tile[][] goalsShelf;
    final String cardName;

    public PersonalGoalCard(String personalGoalCard){
        cardName = personalGoalCard;
        goalsShelf = new Tile[SHELF_ROWS][SHELF_COLUMN];
        String filePath = "src/main/resources/PersonalGoalStrategy.json";
        File input = new File(filePath);
        try {
            JsonElement fileElement = JsonParser.parseReader(new FileReader(input));
            JsonObject fileObject = fileElement.getAsJsonObject();
            JsonArray jsonArrayOfPersonalGoal = fileObject.get(personalGoalCard).getAsJsonArray();
            for (JsonElement personalElement : jsonArrayOfPersonalGoal){
                JsonObject personalObject = personalElement.getAsJsonObject();
                String color = personalObject.get("color").getAsString();
                int x = personalObject.get("x").getAsInt();
                int y = personalObject.get("y").getAsInt();
                goalsShelf[x][y] = new Tile(Color.valueOf(color));
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found!");
            e.printStackTrace();
        } catch (Exception e){
            System.err.println("File read warning!");
            e.printStackTrace();
        }
    }

    public Tile[][] getGoalsShelf() {
        return goalsShelf;
    }
}
