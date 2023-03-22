package it.polimi.ingsw.model;

import it.polimi.ingsw.model.Algorythms.CardStrategy;

import java.util.Stack;

public class CommonGoalCard {
    private CardStrategy cardStrategy;

    private int Id;
    private Stack<ScoringToken> scores;
    private String description;
    public CommonGoalCard(){
    }
    public CommonGoalCard(CardStrategy cardStrategy, int numberPartecipants){
        setCardStrategy(cardStrategy);
        fillStack(numberPartecipants);
        Id = cardStrategy.getId();
    }

    public void setCardStrategy(CardStrategy cardStrategy){
        this.cardStrategy = cardStrategy;
        description = cardStrategy.toString();
    }

    public void fillStack(int numberPartecipants){ //pro-tip: creare uno strategy pattern/factory method anche per scores
        scores = new Stack<ScoringToken>();
        if(numberPartecipants == 4){
            scores.push(new ScoringToken(2));
            scores.push(new ScoringToken(4));
            scores.push(new ScoringToken(6));
            scores.push(new ScoringToken(8));
        }
        if(numberPartecipants == 3){
            scores.push(new ScoringToken(4));
            scores.push(new ScoringToken(6));
            scores.push(new ScoringToken(8));
        }
        if(numberPartecipants == 2){
            scores.push(new ScoringToken(4));
            scores.push(new ScoringToken(8));
        }
    }
    public int getPoint(){
        return scores.pop().score;
    }

    public boolean algorythm(Shelf myShelf){
        return cardStrategy.algorythm(myShelf);
    }

    public String getDescritpion(){
        return description;
    }
    public int getId() {
        return Id;
    }
}
