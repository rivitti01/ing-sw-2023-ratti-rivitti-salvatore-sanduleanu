package it.polimi.ingsw.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.polimi.ingsw.model.Algorythms.CardStrategy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Stack;

import static it.polimi.ingsw.Costants.SHELF_COLUMN;
import static it.polimi.ingsw.Costants.SHELF_ROWS;

public class CommonGoalCard {
    private CardStrategy cardStrategy;
    private Stack<Integer> scores;
    private String description;

    public CommonGoalCard(CardStrategy cardStrategy, int numberParticipants){
        setCardStrategy(cardStrategy);
        fillStack(numberParticipants);
    }

    public void setCardStrategy(CardStrategy cardStrategy){
        this.cardStrategy = cardStrategy;
        description = cardStrategy.toString();
    }

    public int getPoint(){
        return scores.pop();
    }

    public void fillStack(int numberParticipants){
        scores = new Stack<>();
        String filePath = "src/main/resources/PlayersPoints.json";
        File input = new File(filePath);
        try {
            JsonElement fileElement = JsonParser.parseReader(new FileReader(input));
            JsonObject fileObject = fileElement.getAsJsonObject();
            JsonArray jsonArrayOfPoints = fileObject.get(String.valueOf(numberParticipants)).getAsJsonArray();
            for (JsonElement personalElement : jsonArrayOfPoints){
                JsonObject personalObject = personalElement.getAsJsonObject();
                int point = personalObject.get("point").getAsInt();
                scores.push(point);
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found!");
            e.printStackTrace();
        } catch (Exception e){
            System.err.println("File read error!");
            e.printStackTrace();
        }
    }

    public boolean algorythm(Shelf myShelf){
        return cardStrategy.algorythm(myShelf);
    }

    public String getDescritpion(){
        return description;
    }

}
