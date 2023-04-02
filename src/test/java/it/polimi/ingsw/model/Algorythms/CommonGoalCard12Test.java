package it.polimi.ingsw.model.Algorythms;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.CommonGoalCard;
import it.polimi.ingsw.model.Shelf;
import it.polimi.ingsw.model.Tile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CommonGoalCard12Test {
    Shelf testShelf;
    Shelf testShelf2;
    Shelf testShelf3;
    CommonGoalCard commonGoalCard;
    @BeforeEach
    void setUp(){
        commonGoalCard = new CommonGoalCard(new CommonGoalCard12(),4);

        testShelf = new Shelf();
        testShelf2 = new Shelf();
        testShelf3 = new Shelf();

        testShelf.putTile(5,0, new Tile(Color.WHITE));
        testShelf.putTile(4,0, new Tile(Color.WHITE));
        testShelf.putTile(3,0, new Tile(Color.WHITE));
        testShelf.putTile(2,0, new Tile(Color.WHITE));
        testShelf.putTile(1,0, new Tile(Color.WHITE));
        testShelf.putTile(0,0, new Tile(Color.WHITE));
        testShelf.putTile(5,1, new Tile(Color.WHITE));
        testShelf.putTile(4,1, new Tile(Color.WHITE));
        testShelf.putTile(3,1, new Tile(Color.WHITE));
        testShelf.putTile(2,1, new Tile(Color.WHITE));
        testShelf.putTile(1,1, new Tile(Color.WHITE));
        testShelf.putTile(5,2, new Tile(Color.WHITE));
        testShelf.putTile(4,2, new Tile(Color.WHITE));
        testShelf.putTile(3,2, new Tile(Color.WHITE));
        testShelf.putTile(2,2, new Tile(Color.WHITE));
        testShelf.putTile(5,3, new Tile(Color.WHITE));
        testShelf.putTile(4,3, new Tile(Color.WHITE));
        testShelf.putTile(3,3, new Tile(Color.WHITE));
        testShelf.putTile(5,4, new Tile(Color.WHITE));
        testShelf.putTile(4,4, new Tile(Color.WHITE));


        testShelf2.putTile(5,0, new Tile(Color.WHITE));
        testShelf2.putTile(5,1, new Tile(Color.WHITE));
        testShelf2.putTile(4,1, new Tile(Color.WHITE));
        testShelf2.putTile(5,2, new Tile(Color.WHITE));
        testShelf2.putTile(4,2, new Tile(Color.WHITE));
        testShelf2.putTile(3,2, new Tile(Color.WHITE));
        testShelf2.putTile(5,3, new Tile(Color.WHITE));
        testShelf2.putTile(4,3, new Tile(Color.WHITE));
        testShelf2.putTile(3,3, new Tile(Color.WHITE));
        testShelf2.putTile(2,3, new Tile(Color.WHITE));
        testShelf2.putTile(5,4, new Tile(Color.WHITE));
        testShelf2.putTile(4,4, new Tile(Color.WHITE));
        testShelf2.putTile(3,4, new Tile(Color.WHITE));
        testShelf2.putTile(2,4, new Tile(Color.WHITE));
        testShelf2.putTile(1,4, new Tile(Color.WHITE));

    }
    @Test
    void algorythm() {
        //caso in cui ho la scala a partire da una riga piu in alto decrescente da sinistra
        assertTrue(commonGoalCard.algorythm(testShelf));

        //caso scala crescente da sinistra partendo dalla riga piu in basso
        assertTrue(commonGoalCard.algorythm(testShelf2));

        //shelf vuota
        assertFalse(commonGoalCard.algorythm(testShelf3));
    }
}
