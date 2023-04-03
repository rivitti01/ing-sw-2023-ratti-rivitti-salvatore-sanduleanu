package it.polimi.ingsw.model.Algorythms;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.CommonGoalCard;
import it.polimi.ingsw.model.Shelf;
import it.polimi.ingsw.model.Tile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CommonGoalCard11Test {
    Shelf testShelf;
    Shelf testShelf2;
    Shelf testShelf3;
    Shelf testShelf4;
    CommonGoalCard commonGoalCard;

    //Cinque tessere dello stesso tipo che formano una X."
    @BeforeEach
    void setUp() {
        commonGoalCard = new CommonGoalCard(new CommonGoalCard11(), 4);

        testShelf = new Shelf();
        testShelf2 = new Shelf();
        testShelf3 = new Shelf();
        testShelf4 = new Shelf();

        testShelf.putTile(1,1, new Tile(Color.WHITE));
        testShelf.putTile(0,2, new Tile(Color.WHITE));
        testShelf.putTile(2,2, new Tile(Color.WHITE));
        testShelf.putTile(2,0, new Tile(Color.WHITE));
        testShelf.putTile(0,0, new Tile(Color.WHITE));

        testShelf2.putTile(3,3, new Tile(Color.WHITE));
        testShelf2.putTile(2,4, new Tile(Color.WHITE));
        testShelf2.putTile(4,4, new Tile(Color.WHITE));
        testShelf2.putTile(4,2, new Tile(Color.YELLOW));
        testShelf2.putTile(2,2, new Tile(Color.WHITE));

        testShelf4.putTile(0,0, new Tile(Color.WHITE));
        testShelf4.putTile(1,1, new Tile(Color.WHITE));
        testShelf4.putTile(3,3, new Tile(Color.WHITE));

    }

    @Test
    void algorythm() {
        //caso base a x
        assertTrue(commonGoalCard.algorythm(testShelf));

        //x ma con uno spigolo di colore diverso
        assertFalse(commonGoalCard.algorythm(testShelf2));

        //shelf vuota
        assertFalse(commonGoalCard.algorythm(testShelf3));

        //mancano due spigoli
        assertFalse(commonGoalCard.algorythm(testShelf4));

    }
}
