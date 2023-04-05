package it.polimi.ingsw.model.Algorythms;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.CommonGoalCard;
import it.polimi.ingsw.model.Shelf;
import it.polimi.ingsw.model.Tile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CommonGoalCard10Test {
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
        commonGoalCard = new CommonGoalCard(new CommonGoalCard10(), 4);

        testShelf = new Shelf();
        testShelf2 = new Shelf();
        testShelf4 = new Shelf();
        testShelf5 = new Shelf();
        testShelf6 = new Shelf();
        testShelf7 = new Shelf();


        testShelf.putTile(0, 4, new Tile(Color.BLUE));
        testShelf.putTile(0, 3, new Tile(Color.PINK));
        testShelf.putTile(0, 2, new Tile(Color.YELLOW));
        testShelf.putTile(0, 1, new Tile(Color.GREEN));
        testShelf.putTile(0, 0, new Tile(Color.CYAN));
        testShelf.putTile(1, 4, new Tile(Color.BLUE));
        testShelf.putTile(1, 3, new Tile(Color.PINK));
        testShelf.putTile(1, 2, new Tile(Color.YELLOW));
        testShelf.putTile(1, 1, new Tile(Color.GREEN));
        testShelf.putTile(1, 0, new Tile(Color.CYAN));

        testShelf2.putTile(0, 4, new Tile(Color.BLUE));
        testShelf2.putTile(0, 3, new Tile(Color.PINK));
        testShelf2.putTile(0, 2, new Tile(Color.YELLOW));
        testShelf2.putTile(0, 1, new Tile(Color.GREEN));
        testShelf2.putTile(0, 0, new Tile(Color.CYAN));
        testShelf2.putTile(0, 4, new Tile(Color.BLUE));
        testShelf2.putTile(0, 3, new Tile(Color.PINK));
        testShelf2.putTile(0, 2, new Tile(Color.YELLOW));
        testShelf2.putTile(0, 1, new Tile(Color.GREEN));
        testShelf2.putTile(0, 0, new Tile(Color.YELLOW));

        testShelf5.putTile(0, 4, new Tile(Color.BLUE));
        testShelf5.putTile(0, 3, new Tile(Color.PINK));
        testShelf5.putTile(0, 2, new Tile(Color.YELLOW));
        testShelf5.putTile(0, 1, new Tile(Color.GREEN));
        testShelf5.putTile(0, 0, new Tile(Color.CYAN));

        testShelf6.putTile(0, 4, new Tile(Color.YELLOW));
        testShelf6.putTile(0, 3, new Tile(Color.PINK));
        testShelf6.putTile(0, 2, new Tile(Color.YELLOW));
        testShelf6.putTile(0, 1, new Tile(Color.GREEN));
        testShelf6.putTile(0, 0, new Tile(Color.CYAN));
        testShelf6.putTile(1, 4, new Tile(Color.YELLOW));
        testShelf6.putTile(1, 3, new Tile(Color.PINK));
        testShelf6.putTile(1, 2, new Tile(Color.YELLOW));
        testShelf6.putTile(1, 1, new Tile(Color.GREEN));
        testShelf6.putTile(1, 0, new Tile(Color.YELLOW));
        testShelf6.putTile(2, 4, new Tile(Color.BLUE));
        testShelf6.putTile(2, 3, new Tile(Color.PINK));
        testShelf6.putTile(2, 2, new Tile(Color.YELLOW));
        testShelf6.putTile(2, 1, new Tile(Color.YELLOW));
        testShelf6.putTile(2, 0, new Tile(Color.CYAN));

        testShelf7.putTile(0, 4, new Tile(Color.YELLOW));
        testShelf7.putTile(0, 3, new Tile(Color.PINK));
        testShelf7.putTile(0, 2, new Tile(Color.WHITE));
        testShelf7.putTile(0, 1, new Tile(Color.GREEN));
        testShelf7.putTile(0, 0, new Tile(Color.CYAN));
        testShelf7.putTile(1, 4, new Tile(Color.WHITE));
        testShelf7.putTile(1, 3, new Tile(Color.PINK));
        testShelf7.putTile(1, 2, new Tile(Color.BLUE));
        testShelf7.putTile(1, 1, new Tile(Color.GREEN));
        testShelf7.putTile(1, 0, new Tile(Color.YELLOW));

    }

    @Test
    void algorythm() {
        // 2 righe corrette
        assertTrue(commonGoalCard.algorythm(testShelf));

        // 1 riga giusta, 1 sbagliata
        assertFalse(commonGoalCard.algorythm(testShelf2));

        //shelf non costruita
        assertFalse(commonGoalCard.algorythm(testShelf3));

        //shelf vuota
        assertFalse(commonGoalCard.algorythm(testShelf4));

        // 1 riga corretta
        assertFalse(commonGoalCard.algorythm(testShelf5));

        //3 righe errate
        assertFalse(commonGoalCard.algorythm(testShelf6));

        // 2 righe corrette ( righe diverse da test1 )
        assertTrue(commonGoalCard.algorythm(testShelf7));
    }
}