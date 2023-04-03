package it.polimi.ingsw.model.Algorythms;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.CommonGoalCard;
import it.polimi.ingsw.model.Shelf;
import it.polimi.ingsw.model.Tile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CommonGoalCard6Test {
    Shelf testShelf;
    Shelf testShelf2;
    Shelf testShelf3;

    CommonGoalCard commonGoalCard;


    @BeforeEach
    void setUp() {
        commonGoalCard = new CommonGoalCard(new CommonGoalCard6(), 4);

        testShelf = new Shelf();
        testShelf2 = new Shelf();
        testShelf3 = new Shelf();

        testShelf.putTile(0,0, new Tile(Color.WHITE));
        testShelf.putTile(1,2, new Tile(Color.WHITE));
        testShelf.putTile(4,1, new Tile(Color.WHITE));
        testShelf.putTile(1,4, new Tile(Color.WHITE));
        testShelf.putTile(2,1, new Tile(Color.WHITE));
        testShelf.putTile(1,3, new Tile(Color.WHITE));
        testShelf.putTile(4,4, new Tile(Color.WHITE));
        testShelf.putTile(5,0, new Tile(Color.WHITE));

        testShelf2.putTile(0,0, new Tile(Color.WHITE));
        testShelf2.putTile(1,2, new Tile(Color.WHITE));
        testShelf2.putTile(4,1, new Tile(Color.WHITE));
        testShelf2.putTile(1,4, new Tile(Color.BLUE));
        testShelf2.putTile(2,1, new Tile(Color.WHITE));
        testShelf2.putTile(1,3, new Tile(Color.WHITE));
        testShelf2.putTile(4,4, new Tile(Color.WHITE));
        testShelf2.putTile(5,0, new Tile(Color.WHITE));
    }

    @Test
    void algorythm() {
        //8 tessere di colore WHITE
        assertTrue(commonGoalCard.algorythm(testShelf));

        // 7 WHITE 1 BLUE
        assertFalse(commonGoalCard.algorythm(testShelf2));

        //shelf vuota
        assertFalse(commonGoalCard.algorythm(testShelf3));


    }
}
