package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.List;

import static it.polimi.ingsw.util.Costants.DECK_SIZE;
import static java.util.Collections.shuffle;

public class DeckCommon {
    private List<String> deck;

    public DeckCommon(){
        deck = new ArrayList<>();
        for(int i = 1; i <= DECK_SIZE;i++){
            deck.add("CommonGoalCard"+i);
        }
        shuffle(deck);
    }
    public String popCommonGoalCard(){
        return deck.remove(0);
    }
}
