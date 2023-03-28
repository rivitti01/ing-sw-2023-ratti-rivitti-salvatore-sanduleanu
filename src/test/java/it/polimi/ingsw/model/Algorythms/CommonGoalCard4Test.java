package it.polimi.ingsw.model.Algorythms;

import it.polimi.ingsw.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;



import static org.junit.jupiter.api.Assertions.*;

class CommonGoalCard4Test {

    private Shelf s;
    private CardStrategy card;

    @BeforeEach
    void setUp() {
        card = new CommonGoalCard4();
        s = new Shelf();
    }

    @Test
    void emptyShelf() {
        assertFalse(card.algorythm(s));
    }

    @Test
    void noSquares(){
        s.putTile(5,0, new Tile(Color.YELLOW));
        s.putTile(5,1, new Tile(Color.GREEN));
        s.putTile(4,1, new Tile(Color.GREEN));
        s.putTile(3,1, new Tile(Color.BLUE));
        s.putTile(2,1, new Tile(Color.BLUE));
        s.putTile(1,1, new Tile(Color.BLUE));
        s.putTile(0,1, new Tile(Color.BLUE));
        s.putTile(5,2, new Tile(Color.BLUE));
        s.putTile(4,2, new Tile(Color.BLUE));
        s.putTile(0,4, new Tile(Color.YELLOW));
        s.putTile(1,4, new Tile(Color.GREEN));
        s.putTile(2,4, new Tile(Color.GREEN));
        s.putTile(3,4, new Tile(Color.GREEN));
        s.putTile(4,4, new Tile(Color.YELLOW));
        s.putTile(5,4, new Tile(Color.YELLOW));
        assertFalse(card.algorythm(s));
    }

    @Test
    void lessThanTwo(){
        s.putTile(5,0, new Tile(Color.GREEN));
        s.putTile(4,0, new Tile(Color.BLUE));
        s.putTile(3,1, new Tile(Color.YELLOW));
        s.putTile(2,2, new Tile(Color.PINK));
        s.putTile(1,2, new Tile(Color.PINK));
        s.putTile(0,3, new Tile(Color.PINK));
        s.putTile(5,3, new Tile(Color.PINK));
        assertFalse(card.algorythm(s));

    }

    @Test
    void meetingCriteria(){
        s.putTile(5,0, new Tile(Color.BLUE));
        s.putTile(4,0, new Tile(Color.GREEN));
        s.putTile(3,0, new Tile(Color.GREEN));
        s.putTile(2,0, new Tile(Color.YELLOW));
        s.putTile(5,1, new Tile(Color.PINK));
        s.putTile(4,1, new Tile(Color.GREEN));
        s.putTile(3,1, new Tile(Color.GREEN));
        s.putTile(2,1, new Tile(Color.BLUE));
        s.putTile(0,2, new Tile(Color.BLUE));
        s.putTile(0,3, new Tile(Color.BLUE));
        s.putTile(1,3, new Tile(Color.BLUE));
        s.putTile(2,3, new Tile(Color.BLUE));
        s.putTile(0,4, new Tile(Color.YELLOW));
        s.putTile(1,4, new Tile(Color.BLUE));
        s.putTile(2,4, new Tile(Color.BLUE));
        assertTrue(card.algorythm(s));

    }
}