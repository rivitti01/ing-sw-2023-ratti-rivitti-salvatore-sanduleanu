package it.polimi.ingsw.model.Algorythms;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.CommonGoalCard;
import it.polimi.ingsw.model.Shelf;
import it.polimi.ingsw.model.Tile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CommonGoalCard7Test {
    Shelf testShelf;
    Shelf testShelf2;
    Shelf testShelf3;
    Shelf testShelf4;
    Shelf testShelf5;
    Shelf testShelf6;
    Shelf testShelf7;

    CommonGoalCard commonGoalCard;

    @BeforeEach
    void setUp() {
        commonGoalCard = new CommonGoalCard(new CommonGoalCard7(), 4);

        testShelf = new Shelf();
        testShelf2 = new Shelf();
        testShelf3 = new Shelf();
        testShelf5 = new Shelf();
        testShelf6 = new Shelf();
        testShelf7 = new Shelf();

        testShelf.putTile(1,0, new Tile(Color.WHITE));
        testShelf.putTile(2,1, new Tile(Color.WHITE));
        testShelf.putTile(3,2, new Tile(Color.WHITE));
        testShelf.putTile(4,3, new Tile(Color.WHITE));
        testShelf.putTile(5,4, new Tile(Color.WHITE));


        testShelf2.putTile(5,0, new Tile(Color.WHITE));
        testShelf2.putTile(4,1, new Tile(Color.WHITE));
        testShelf2.putTile(3,2, new Tile(Color.WHITE));
        testShelf2.putTile(2,3, new Tile(Color.BLUE));
        testShelf2.putTile(1,4, new Tile(Color.WHITE));

        testShelf5.putTile(1,0, new Tile(Color.WHITE));
        testShelf5.putTile(2,1, new Tile(Color.WHITE));
        testShelf5.putTile(3,2, new Tile(Color.WHITE));
        testShelf5.putTile(4,3, new Tile(Color.WHITE));
        testShelf5.putTile(5,4, new Tile(Color.WHITE));

        testShelf6.putTile(4,0, new Tile(Color.WHITE));
        testShelf6.putTile(3,1, new Tile(Color.WHITE));
        testShelf6.putTile(2,2, new Tile(Color.WHITE));
        testShelf6.putTile(1,3, new Tile(Color.WHITE));
        testShelf6.putTile(0,4, new Tile(Color.WHITE));

        testShelf7.putTile(5,0, new Tile(Color.WHITE));
        testShelf7.putTile(4,1, new Tile(Color.WHITE));
        testShelf7.putTile(3,2, new Tile(Color.WHITE));
        testShelf7.putTile(2,3, new Tile(Color.WHITE));
        testShelf7.putTile(1,4, new Tile(Color.WHITE));


    }

    @Test
    void algorythm() {
        // checko prima diagonale principale
        assertTrue(commonGoalCard.algorythm(testShelf));

        // diagonale errata
        assertFalse(commonGoalCard.algorythm(testShelf2));

        // shelf vuota
        assertFalse(commonGoalCard.algorythm(testShelf3));

        // shelf non costruita
        assertFalse(commonGoalCard.algorythm(testShelf4));

        // checko seconda diagonale principale
        assertTrue(commonGoalCard.algorythm(testShelf5));

        //checko prima diagonale secondaria
        assertTrue(commonGoalCard.algorythm(testShelf6));

        //checko seconda diagonale secondaria
        assertTrue(commonGoalCard.algorythm(testShelf7));
    }
}