package it.polimi.ingsw.model;
import static it.polimi.ingsw.util.Costants.*;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.util.Objects;

/**
 * Represents a personal goal card which
 * is a private Goal different for each player.
 */
public class PersonalGoalCard implements Serializable {
    final Tile[][] goalsShelf;
    final String cardName;

    /**
     * Constructs a PersonalGoalCard object with the specified card name.
     *
     * @param personalGoalCard the name of the personal goal card
     */
    public PersonalGoalCard(String personalGoalCard){
        cardName = personalGoalCard;
        goalsShelf = new Tile[SHELF_ROWS][SHELF_COLUMN];
        Reader readerConfig = new InputStreamReader(Objects.requireNonNull(this.getClass().getResourceAsStream("/PersonalGoalStrategy.json")));
        try {
            JsonElement fileElement = JsonParser.parseReader(readerConfig);
            JsonObject fileObject = fileElement.getAsJsonObject();
            JsonArray jsonArrayOfPersonalGoal = fileObject.get(personalGoalCard).getAsJsonArray();
            for (JsonElement personalElement : jsonArrayOfPersonalGoal){
                JsonObject personalObject = personalElement.getAsJsonObject();
                String color = personalObject.get("color").getAsString();
                int x = personalObject.get("x").getAsInt();
                int y = personalObject.get("y").getAsInt();
                goalsShelf[x][y] = new Tile(Color.valueOf(color));
            }
        } catch (Exception e){
            System.err.println("File read warning!");
            e.printStackTrace();
        }
    }

    /**
     * Retrieves the goal shelf.
     *
     * @return the goal shelf as a 2D array of Tile objects
     */
    public Tile[][] getGoalsShelf() {
        return goalsShelf;
    }

    /**
     * Retrieves the card name.
     *
     * @return the card name as a String
     */
    public String getCardName() {
        return cardName;
    }
}