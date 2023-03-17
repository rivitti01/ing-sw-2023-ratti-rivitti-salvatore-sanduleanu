package it.polimi.ingsw.model;

import java.util.Stack;

public class CommonGoalCard {
    private CardStrategy cardStrategy;

    private String ID;
    private Stack<ScoringToken> scores;
    private String description;

    public void setCardStrategy(CardStrategy cardStrategy){
        this.cardStrategy = cardStrategy;
        description = cardStrategy.toString();
    }

    public void setPoints(int numberPartecipants){ //pro-tip: creare uno strategy pattern/factory method anche per scores
        scores = new Stack<ScoringToken>();
        if(numberPartecipants == 4){
            scores.push(new ScoringToken(8));
            scores.push(new ScoringToken(6));
            scores.push(new ScoringToken(4));
            scores.push(new ScoringToken(2));
        }
        if(numberPartecipants == 3){
            scores.push(new ScoringToken(8));
            scores.push(new ScoringToken(6));
            scores.push(new ScoringToken(4));
        }
        if(numberPartecipants == 2){
            scores.push(new ScoringToken(8));
            scores.push(new ScoringToken(4));
        }
    }

    public boolean algorythm(Shelf myShelf){
        return cardStrategy.algorythm(myShelf);
    }

    public String getDescritpion(){
        return description;
    }

    public void fillStack(){}
}
