package it.polimi.ingsw.model;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CommonGoalCardTest {
    CommonGoalCard card1;
    CommonGoalCard card2;
    CommonGoalCard card3;


    @BeforeEach
    void setup(){
        this.card1 = new CommonGoalCard(4,"CommonGoalCard2");
        this.card2 = new CommonGoalCard(3, "CommonGoalCard2");
        this.card3 = new CommonGoalCard(2, "CommonGoalCard2");
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
    @Test
    void testGetPoint() {
        // Test if getPoint() returns the correct point from the scores stack
        int point = card1.getPoint();
        assertEquals(8, point);
        assertEquals(3, card1.getScores().size()); // Ensure the stack size is decreased
    }

    @Test
    void testFillStack() {
        // Test if fillStack() populates the scores stack correctly
        card1.fillStack(4);
        assertEquals(4, card1.getScores().size());
    }

    @Test
    void testAlgorithm() {
        // Test if algorithm() applies the card strategy correctly
        Shelf myShelf = new Shelf();
        boolean result = card1.algorythm(myShelf);
        assertFalse(result);
    }

    @Test
    void testGetDescription() {
        // Test if getDescription() returns the correct description
        String description = card1.getDescription();
        assertEquals("Four tiles of the same type in the four corners of the bookshelf.", description);
    }

    @Test
    void testGetName() {
        // Test if getName() returns the correct name
        String name = card1.getName();
        assertEquals("2common", name);
    }
}
