package it.polimi.ingsw.model;

import java.util.Stack;

public class CommonGoalCard {
    private String ID;
    private Stack<ScoringToken> scores;
    private String description;

    public String getDescritpion(){
        return description;
    }

    public void fillStack(){}
}
