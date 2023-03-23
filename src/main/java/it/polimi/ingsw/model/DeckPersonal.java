package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.shuffle;

public class DeckPersonal {
    List<String> deck;
    private static final int deckSize = 12;

    public DeckPersonal(){
        deck = new ArrayList<>();
        for(int i=1; i<=deckSize; i++){
            deck.add("goalStrategy" + i);
        }
        shuffle(deck);
    }

    public String popPersonalCard(){
       return deck.remove(0);
    }
}

