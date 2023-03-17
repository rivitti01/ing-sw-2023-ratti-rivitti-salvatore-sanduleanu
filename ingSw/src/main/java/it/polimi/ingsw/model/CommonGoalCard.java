package it.polimi.ingsw.model;

import java.util.Stack;

public class CommonGoalCard {
    private CardStrategy cardStrategy;

    private String ID;
    private Stack<ScoringToken> scores;
    private String description;

    public void setCardStrategy(CardStrategy cardStrategy){
        this.cardStrategy = cardStrategy;
    }

    public boolean algorythm(Shelf myShelf){
        return cardStrategy.algorythm(myShelf);
    }

    public String getDescritpion(){
        return description;
    }

    public void fillStack(){}
}
