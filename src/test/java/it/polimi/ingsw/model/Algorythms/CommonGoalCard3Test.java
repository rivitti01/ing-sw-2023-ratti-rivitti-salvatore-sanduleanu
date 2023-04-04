package it.polimi.ingsw.model.Algorythms;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.Shelf;
import it.polimi.ingsw.model.Tile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommonGoalCard3Test {
    Shelf test1;
    Shelf test2;
    Shelf test3;
    Shelf test4;
    Shelf test5;
    CommonGoalCard3 commonGoalCard3;



    @BeforeEach
    void setUp() {
        commonGoalCard3 = new CommonGoalCard3();
        test1 = new Shelf();
        test2 = new Shelf();
        test3 = new Shelf();
        test1.putTile(0,0,new Tile(Color.WHITE));
        test1.putTile(0,1,new Tile(Color.WHITE));
        test1.putTile(0,2,new Tile(Color.WHITE));
        test1.putTile(0,3,new Tile(Color.WHITE));
        test1.putTile(1,0,new Tile(Color.BLUE));
        test1.putTile(1,1,new Tile(Color.BLUE));
        test1.putTile(1,2,new Tile(Color.BLUE));
        test1.putTile(1,3,new Tile(Color.BLUE));
        test1.putTile(2,0,new Tile(Color.GREEN));
        test1.putTile(2,1,new Tile(Color.GREEN));
        test1.putTile(2,2,new Tile(Color.GREEN));
        test1.putTile(2,3,new Tile(Color.GREEN));
        test1.putTile(3,0,new Tile(Color.YELLOW));
        test1.putTile(3,1,new Tile(Color.YELLOW));
        test1.putTile(3,2,new Tile(Color.YELLOW));
        test1.putTile(3,3,new Tile(Color.YELLOW));

        test2.putTile(0,0,new Tile(Color.WHITE));
        test2.putTile(1,0,new Tile(Color.WHITE));
        test2.putTile(2,0,new Tile(Color.WHITE));
        test2.putTile(3,0,new Tile(Color.WHITE));
        test2.putTile(0,1,new Tile(Color.WHITE));
        test2.putTile(1,1,new Tile(Color.WHITE));
        test2.putTile(2,1,new Tile(Color.WHITE));
        test2.putTile(3,1,new Tile(Color.WHITE));
        test2.putTile(0,2,new Tile(Color.WHITE));
        test2.putTile(1,2,new Tile(Color.WHITE));
        test2.putTile(2,2,new Tile(Color.WHITE));
        test2.putTile(3,2,new Tile(Color.WHITE));
        test2.putTile(0,3,new Tile(Color.WHITE));
        test2.putTile(1,3,new Tile(Color.WHITE));
        test2.putTile(2,3,new Tile(Color.WHITE));
        test2.putTile(3,3,new Tile(Color.WHITE));

        test4 = test2.copyShelf();
        test4.putTile(0,0,new Tile(Color.PINK));




    }

    @Test
    void algorythm() {
        assertTrue(commonGoalCard3.algorythm(test1));
        assertTrue(commonGoalCard3.algorythm(test2));
        assertFalse(commonGoalCard3.algorythm(test3));
        assertFalse(commonGoalCard3.algorythm(test4));
        assertFalse(commonGoalCard3.algorythm(test5));
    }
}