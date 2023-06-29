package it.polimi.ingsw.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.polimi.ingsw.model.Algorythms.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Stack;

/**
 * Represents a common goal card used in the game.
 */
public class CommonGoalCard {
    private CardStrategy cardStrategy;
    private Stack<Integer> scores;
    private String description;
    private String name;

    /**
     * Constructs a CommonGoalCard object with the specified number of participants and a String.
     * It sets the card strategy, fills the stack with scores, and initializes the description and name.
     * Name is used to show the correct image associated to that CommonGoalCard object
     *
     * @param numberParticipants the number of participants
     * @param commonCard         a String made like: ""CommonGoalCard" + x" where x is a
     *                           random integer between 1 and 12
     */
    public CommonGoalCard(int numberParticipants, String commonCard){
        setCardStrategy(commonCard);
        fillStack(numberParticipants);
        description = cardStrategy.toString();
        name = cardStrategy.getCardName();
    }

    /**
     * Constructor method used for tests
     * @param cardStrategy   the card strategy
     * @param numberParticipants the number of participants of the game.
     */
    public CommonGoalCard(CardStrategy cardStrategy, int numberParticipants){
        this.cardStrategy = cardStrategy;
        fillStack(numberParticipants);
        description = cardStrategy.toString();
        name = cardStrategy.getCardName();
    }


    private void setCardStrategy(String commonCard){
        switch (commonCard){
            case "CommonGoalCard1"  -> cardStrategy = new CommonGoalCard1();
            case "CommonGoalCard2"  -> cardStrategy = new CommonGoalCard2();
            case "CommonGoalCard3"  -> cardStrategy = new CommonGoalCard3();
            case "CommonGoalCard4"  -> cardStrategy = new CommonGoalCard4();
            case "CommonGoalCard5"  -> cardStrategy = new CommonGoalCard5();
            case "CommonGoalCard6"  -> cardStrategy = new CommonGoalCard6();
            case "CommonGoalCard7"  -> cardStrategy = new CommonGoalCard7();
            case "CommonGoalCard8"  -> cardStrategy = new CommonGoalCard8();
            case "CommonGoalCard9"  -> cardStrategy = new CommonGoalCard9();
            case "CommonGoalCard10" -> cardStrategy = new CommonGoalCard10();
            case "CommonGoalCard11" -> cardStrategy = new CommonGoalCard11();
            case "CommonGoalCard12" -> cardStrategy = new CommonGoalCard12();
        }
    }

    /**
     * Retrieves and removes the top element from the scores stack.
     *
     * @return the top element from the scores stack
     */
    public int getPoint(){
        return scores.pop();
    }

    /**
     * Fills the scores stack with points obtained from
     * a JSON file based on the number of participants.
     *
     * @param numberParticipants the number of participants used to retrieve points from the JSON file
     */
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

    /**
     * Applies the algorithm defined by the card strategy to the given Shelf object.
     *
     * @param myShelf the Shelf object to which the algorithm will be applied
     * @return true if the algorithm succeeds on the given shelf, false otherwise
     */
    public boolean algorythm(Shelf myShelf){
        return cardStrategy.algorithm(myShelf);
    }

    /**
     * Returns the description of the CommonGoalCard.
     *
     * @return the description of the CommonGoalCard
     */
    public String getDescription(){
        return description;
    }

    /**
     * Returns the name of the CommonGoalCard.
     * It is used to show in the GUI the correct image associated to
     * the CommonGoalCard object.
     *
     * @return the name of the CommonGoalCard
     */
    public String getName() {return name;}

    /**
     * Returns the Stack of points of the CommonGoalCard.
     *
     * @return the Stack of points of the CommonGoalCard
     */
    public Stack<Integer> getScores() {
        return scores;
    }
}