package it.polimi.ingsw.model.Algorythms;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.CommonGoalCard;
import it.polimi.ingsw.model.Shelf;
import it.polimi.ingsw.model.Tile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CommonGoalCard9Test {
    Shelf testShelf;
    Shelf testShelf2;
    Shelf testShelf3;
    Shelf testShelf4;
    Shelf testShelf5;
    Shelf testShelf6;

    CommonGoalCard commonGoalCard;


    @BeforeEach
    void setUp() {
        commonGoalCard = new CommonGoalCard(new CommonGoalCard9(), 4);

        testShelf = new Shelf();
        testShelf2 = new Shelf();
        testShelf4 = new Shelf();
        testShelf5 = new Shelf();
        testShelf6 = new Shelf();

        testShelf.putTile(0, 0, new Tile(Color.WHITE));
        testShelf.putTile(1, 0, new Tile(Color.BLUE));
        testShelf.putTile(2, 0, new Tile(Color.PINK));
        testShelf.putTile(3, 0, new Tile(Color.YELLOW));
        testShelf.putTile(4, 0, new Tile(Color.GREEN));
        testShelf.putTile(5, 0, new Tile(Color.CYAN));
        testShelf.putTile(0, 3, new Tile(Color.WHITE));
        testShelf.putTile(1, 3, new Tile(Color.BLUE));
        testShelf.putTile(2, 3, new Tile(Color.PINK));
        testShelf.putTile(3, 3, new Tile(Color.YELLOW));
        testShelf.putTile(4, 3, new Tile(Color.GREEN));
        testShelf.putTile(5, 3, new Tile(Color.CYAN));

        testShelf2.putTile(0, 2, new Tile(Color.WHITE));
        testShelf2.putTile(1, 2, new Tile(Color.CYAN));
        testShelf2.putTile(2, 2, new Tile(Color.YELLOW));
        testShelf2.putTile(3, 2, new Tile(Color.BLUE));
        testShelf2.putTile(4, 2, new Tile(Color.PINK));
        testShelf2.putTile(5, 2, new Tile(Color.GREEN));
        testShelf2.putTile(0, 3, new Tile(Color.WHITE));
        testShelf2.putTile(1, 3, new Tile(Color.CYAN));
        testShelf2.putTile(2, 3, new Tile(Color.YELLOW));
        testShelf2.putTile(3, 3, new Tile(Color.BLUE));
        testShelf2.putTile(4, 3, new Tile(Color.PINK));
        testShelf2.putTile(5, 3, new Tile(Color.WHITE));

        testShelf4.putTile(0, 4, new Tile(Color.WHITE));
        testShelf4.putTile(1, 4, new Tile(Color.CYAN));
        testShelf4.putTile(2, 4, new Tile(Color.YELLOW));
        testShelf4.putTile(3, 4, new Tile(Color.BLUE));
        testShelf4.putTile(4, 4, new Tile(Color.PINK));
        testShelf4.putTile(5, 4, new Tile(Color.GREEN));

        testShelf5.putTile(0, 3, new Tile(Color.WHITE));
        testShelf5.putTile(1, 3, new Tile(Color.CYAN));
        testShelf5.putTile(2, 3, new Tile(Color.YELLOW));
        testShelf5.putTile(3, 3, new Tile(Color.BLUE));
        testShelf5.putTile(4, 3, new Tile(Color.PINK));
        testShelf5.putTile(5, 3, new Tile(Color.WHITE));
        testShelf5.putTile(0, 2, new Tile(Color.WHITE));
        testShelf5.putTile(1, 2, new Tile(Color.CYAN));
        testShelf5.putTile(2, 2, new Tile(Color.YELLOW));
        testShelf5.putTile(3, 2, new Tile(Color.BLUE));
        testShelf5.putTile(4, 2, new Tile(Color.PINK));
        testShelf5.putTile(5, 2, new Tile(Color.WHITE));
        testShelf5.putTile(0, 1, new Tile(Color.WHITE));
        testShelf5.putTile(1, 1, new Tile(Color.CYAN));
        testShelf5.putTile(2, 1, new Tile(Color.YELLOW));
        testShelf5.putTile(3, 1, new Tile(Color.BLUE));
        testShelf5.putTile(4, 1, new Tile(Color.PINK));
        testShelf5.putTile(5, 1, new Tile(Color.WHITE));
    }

    @Test
    void algorythm() {
        // 2 colonne distanziate corrette
        assertTrue(commonGoalCard.algorythm(testShelf));

        // 1 colonna giusta, 1 errata
        assertFalse(commonGoalCard.algorythm(testShelf2));

        //shelf non costruita
        assertFalse(commonGoalCard.algorythm(testShelf3));

        // 1 colonna corretta
        assertFalse(commonGoalCard.algorythm(testShelf4));

        // 3 colonne errate
        assertFalse(commonGoalCard.algorythm(testShelf5));

        // shelf vuota
        assertFalse(commonGoalCard.algorythm(testShelf6));
    }
}