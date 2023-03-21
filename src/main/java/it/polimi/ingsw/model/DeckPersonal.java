package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.shuffle;

public class DeckPersonal {
    List<String> deck;

    public DeckPersonal(){
        deck = new ArrayList<>();
        for(int i=1; i<=12; i++){
            deck.add("goalStrategy" + i);
        }
        shuffle(deck);
    }

    public String popPersonalCard(){
       return deck.remove(0);
    }
}

