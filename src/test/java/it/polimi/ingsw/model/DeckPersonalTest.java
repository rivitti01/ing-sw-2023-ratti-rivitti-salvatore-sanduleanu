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
        PersonalGoalCard card_A = deck.popPersonalCard();
        PersonalGoalCard card_B = deck.popPersonalCard();
        PersonalGoalCard card_C = deck.popPersonalCard();
        PersonalGoalCard card_D = deck.popPersonalCard();
    }
}
