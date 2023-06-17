package it.polimi.ingsw.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.polimi.ingsw.model.Algorythms.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Stack;

public class CommonGoalCard {
    private CardStrategy cardStrategy;
    private Stack<Integer> scores;
    private String description;
    private String name;


    public CommonGoalCard(int numberParticipants, DeckCommon deckCommon){
        setCardStrategy(deckCommon);
        fillStack(numberParticipants);
        description = cardStrategy.toString();
        name = cardStrategy.getCardName();
    }
    public CommonGoalCard(CardStrategy cardStrategy, int numberParticipants){
        this.cardStrategy = cardStrategy;
        fillStack(numberParticipants);
        description = cardStrategy.toString();
        name = cardStrategy.getCardName();
    }

    private void setCardStrategy(DeckCommon deckCommon){
        String common = deckCommon.popCommonGoalCard();
        switch (common){
            case "CommonGoalCard1" -> {
                cardStrategy = new CommonGoalCard1();
            }
            case "CommonGoalCard2" -> {
                cardStrategy = new CommonGoalCard2();
            }
            case "CommonGoalCard3" -> {
                cardStrategy = new CommonGoalCard3();
            }
            case "CommonGoalCard4" -> {
                cardStrategy = new CommonGoalCard4();
            }
            case "CommonGoalCard5" -> {
                cardStrategy = new CommonGoalCard5();
            }
            case "CommonGoalCard6" -> {
                cardStrategy = new CommonGoalCard6();
            }
            case "CommonGoalCard7" -> {
                cardStrategy = new CommonGoalCard7();
            }
            case "CommonGoalCard8" -> {
                cardStrategy = new CommonGoalCard8();
            }
            case "CommonGoalCard9" -> {
                cardStrategy = new CommonGoalCard9();
            }
            case "CommonGoalCard10" -> {
                cardStrategy = new CommonGoalCard10();
            }
            case "CommonGoalCard11" -> {
                cardStrategy = new CommonGoalCard11();
            }
            case "CommonGoalCard12" -> {
                cardStrategy = new CommonGoalCard12();
            }
        }
    }

    public int getPoint(){
        int point = scores.pop();
        return point;
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
            System.err.println("File read warning!");
            e.printStackTrace();
        }
    }

    public boolean algorythm(Shelf myShelf){
        return cardStrategy.algorythm(myShelf);
    }

    public String getDescription(){
        return description;
    }

    public String getName() {return name;}

    public Stack<Integer> getScores() {
        return scores;
    }
}
