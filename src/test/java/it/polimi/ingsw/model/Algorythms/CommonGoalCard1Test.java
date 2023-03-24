package it.polimi.ingsw.model.Algorythms;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.CommonGoalCard;
import it.polimi.ingsw.model.Shelf;
import it.polimi.ingsw.model.Tile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommonGoalCard1Test {
    Shelf testShelf;
    Shelf testShelf2;
    CommonGoalCard commonGoalCard;
    @BeforeEach
    void setUp(){
        commonGoalCard = new CommonGoalCard(new CommonGoalCard1(), 4);
        testShelf = new Shelf();
        testShelf.putTile(0,0,new Tile(Color.WHITE));
        testShelf.putTile(0,1,new Tile(Color.WHITE));
        testShelf.putTile(1,0,new Tile(Color.WHITE));
        testShelf.putTile(1,1,new Tile(Color.WHITE));
        testShelf.putTile(2,0,new Tile(Color.WHITE));
        testShelf.putTile(2,1,new Tile(Color.WHITE));
        testShelf.putTile(3,0,new Tile(Color.WHITE));
        testShelf.putTile(3,1,new Tile(Color.WHITE));
        testShelf.putTile(4,0,new Tile(Color.WHITE));
        testShelf.putTile(4,1,new Tile(Color.WHITE));
        testShelf.putTile(5,0,new Tile(Color.WHITE));
        testShelf.putTile(5,1,new Tile(Color.WHITE));
        testShelf2 = testShelf.copyShelf();
        testShelf2.putTile(0,0,new Tile(Color.CYAN));
    }
    @Test
    void algorythm() {
        assertTrue(commonGoalCard.algorythm(testShelf));
        assertFalse(commonGoalCard.algorythm(testShelf2));
    }
}