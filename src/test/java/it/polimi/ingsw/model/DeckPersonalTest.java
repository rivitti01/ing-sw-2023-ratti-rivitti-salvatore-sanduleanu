package it.polimi.ingsw.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DeckPersonalTest {
    DeckPersonal deck;

    @BeforeEach
    void DeckPersonal(){
        deck = new DeckPersonal();
    }

    @Test
    void popPersonalCard(){
        deck.popPersonalCard();
        deck.popPersonalCard();
        deck = new DeckPersonal();
        deck.popPersonalCard();
        deck.popPersonalCard();
        deck = new DeckPersonal();

    }
}
