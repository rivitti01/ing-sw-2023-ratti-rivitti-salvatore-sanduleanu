package it.polimi.ingsw.model;

import it.polimi.ingsw.model.Algorythms.CardStrategy;

import java.util.Stack;

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

    public void fillStack(int numberPartecipants){ //pro-tip: creare uno strategy pattern/factory method anche per scores
        scores = new Stack<>();
        switch (numberPartecipants) {
            case 4 -> {
                scores.push(2);
                scores.push(4);
                scores.push(6);
                scores.push(8);
            }
            case 3 -> {
                scores.push(4);
                scores.push(6);
                scores.push(8);
            }
            case 2 -> {
                scores.push(4);
                scores.push(8);
            }
        }
    }

    public boolean algorythm(Shelf myShelf){
        return cardStrategy.algorythm(myShelf);
    }

    public String getDescritpion(){
        return description;
    }

}
