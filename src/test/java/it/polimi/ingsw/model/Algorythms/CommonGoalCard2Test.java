package it.polimi.ingsw.model.Algorythms;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.CommonGoalCard;
import it.polimi.ingsw.model.Shelf;
import it.polimi.ingsw.model.Tile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommonGoalCard2Test {
    Shelf testShelf;
    Shelf testShelf2;
    Shelf testShelf3;
    Shelf getTestShelf4;
    CommonGoalCard commonGoalCard;
    @BeforeEach
    void setUp(){
        commonGoalCard = new CommonGoalCard(new CommonGoalCard2(),4);

        testShelf = new Shelf();
        testShelf.putTile(0,0,new Tile(Color.WHITE));
        testShelf.putTile(0,4,new Tile(Color.WHITE));
        testShelf.putTile(5,0,new Tile(Color.WHITE));
        testShelf.putTile(5,4,new Tile(Color.WHITE));
        testShelf2 = testShelf.copyShelf();
        testShelf2.putTile(0,0,new Tile(Color.CYAN));
        testShelf3 = testShelf.copyShelf();
        testShelf3.putTile(0,0,new Tile(null));
    }
    @Test
    void algorythm() {
        assertTrue(commonGoalCard.algorythm(testShelf));
        assertFalse(commonGoalCard.algorythm(testShelf2));
        assertFalse(commonGoalCard.algorythm(testShelf3));
        assertFalse(commonGoalCard.algorythm(getTestShelf4));
    }
}