package it.polimi.ingsw.model.Algorythms;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.Shelf;
import it.polimi.ingsw.model.Tile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;



import static org.junit.jupiter.api.Assertions.*;

class CommonGoalCard5Test {

    private Shelf s;
    private CardStrategy card;

    @BeforeEach
    void setUp() {
        card = new CommonGoalCard5();
        s = new Shelf();
    }

    @Test
    void emptyShelf() {
        assertFalse(card.algorythm(s));
    }

    @Test
    void lessThanThree(){
        s.putTile(5,0, new Tile(Color.YELLOW));
        s.putTile(5,1, new Tile(Color.GREEN));
        s.putTile(4,1, new Tile(Color.GREEN));
        s.putTile(3,1, new Tile(Color.BLUE));
        s.putTile(2,1, new Tile(Color.BLUE));
        s.putTile(1,1, new Tile(Color.BLUE));
        s.putTile(0,1, new Tile(Color.BLUE));
        s.putTile(5,2, new Tile(Color.BLUE));
        s.putTile(4,2, new Tile(Color.BLUE));
        s.putTile(1,4, new Tile(Color.YELLOW));
        s.putTile(1,4, new Tile(Color.GREEN));
        s.putTile(1,4, new Tile(Color.GREEN));
        s.putTile(1,4, new Tile(Color.GREEN));
        s.putTile(1,4, new Tile(Color.YELLOW));
        s.putTile(0,4, new Tile(Color.YELLOW));
        assertFalse(card.algorythm(s));

    }

    @Test
    void notMeetingCriteria(){
        s.putTile(5,1, new Tile(Color.GREEN));
        s.putTile(4,1, new Tile(Color.BLUE));
        s.putTile(3,1, new Tile(Color.YELLOW));
        s.putTile(2,1, new Tile(Color.PINK));
        s.putTile(1,1, new Tile(Color.GREEN));
        s.putTile(0,1, new Tile(Color.GREEN));
        s.putTile(5,2, new Tile(Color.BLUE));
        s.putTile(4,2, new Tile(Color.YELLOW));
        s.putTile(3,2, new Tile(Color.GREEN));
        s.putTile(2,2, new Tile(Color.PINK));
        s.putTile(1,2, new Tile(Color.GREEN));
        s.putTile(0,2, new Tile(Color.GREEN));
        s.putTile(5,3, new Tile(Color.BLUE));
        s.putTile(4,3, new Tile(Color.YELLOW));
        s.putTile(3,3, new Tile(Color.GREEN));
        s.putTile(2,3, new Tile(Color.PINK));
        s.putTile(1,3, new Tile(Color.YELLOW));
        s.putTile(0,3, new Tile(Color.YELLOW));
        s.putTile(5,4, new Tile(Color.YELLOW));
        s.putTile(4,4, new Tile(Color.YELLOW));
        s.putTile(3,4, new Tile(Color.YELLOW));
        assertFalse(card.algorythm(s));

    }

    @Test
    void meetingCriteria(){
        s.putTile(5,1, new Tile(Color.GREEN));
        s.putTile(4,1, new Tile(Color.GREEN));
        s.putTile(3,1, new Tile(Color.GREEN));
        s.putTile(2,1, new Tile(Color.GREEN));
        s.putTile(1,1, new Tile(Color.GREEN));
        s.putTile(0,1, new Tile(Color.GREEN));
        s.putTile(5,2, new Tile(Color.BLUE));
        s.putTile(4,2, new Tile(Color.BLUE));
        s.putTile(3,2, new Tile(Color.BLUE));
        s.putTile(2,2, new Tile(Color.BLUE));
        s.putTile(1,2, new Tile(Color.BLUE));
        s.putTile(0,2, new Tile(Color.GREEN));
        s.putTile(5,3, new Tile(Color.BLUE));
        s.putTile(4,3, new Tile(Color.YELLOW));
        s.putTile(3,3, new Tile(Color.YELLOW));
        s.putTile(2,3, new Tile(Color.YELLOW));
        s.putTile(1,3, new Tile(Color.YELLOW));
        s.putTile(0,3, new Tile(Color.PINK));
        s.putTile(5,4, new Tile(Color.YELLOW));
        s.putTile(4,4, new Tile(Color.YELLOW));
        s.putTile(3,4, new Tile(Color.YELLOW));
        assertTrue(card.algorythm(s));

    }
}