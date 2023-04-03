package it.polimi.ingsw.model;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CommonGoalCardTest {
    CommonGoalCard card1;
    CommonGoalCard card2;
    CommonGoalCard card3;
    DeckCommon deck;

    @BeforeEach
    void setup(){
        this.deck = new DeckCommon();
        this.card1 = new CommonGoalCard(4, this.deck);
        this.card2 = new CommonGoalCard(3, this.deck);
        this.card3 = new CommonGoalCard(2, this.deck);
    }
    @Test
    void fillStack(){
        assertEquals(8, this.card1.getPoint());
        assertEquals(6, this.card1.getPoint());
        assertEquals(4, this.card1.getPoint());
        assertEquals(2, this.card1.getPoint());

        assertEquals(8, this.card2.getPoint());
        assertEquals(6, this.card2.getPoint());
        assertEquals(4, this.card2.getPoint());

        assertEquals(8, this.card3.getPoint());
        assertEquals(4, this.card3.getPoint());

    }
}
