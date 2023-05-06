package it.polimi.ingsw.model;
import static it.polimi.ingsw.Costants.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.shuffle;

public class DeckPersonal implements Serializable {
    private List<String> deck;

    public DeckPersonal(){
        deck = new ArrayList<>();
        for(int i=1; i<=DECK_SIZE; i++){
            deck.add("goalStrategy" + i);
        }
        shuffle(deck);
    }

    public PersonalGoalCard popPersonalCard(){
        return new PersonalGoalCard(deck.remove(0));
    }
}

