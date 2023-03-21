package it.polimi.ingsw.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class ShelfTest {

    private Shelf s;
    private Bag b;
    private final Random random = new Random();

    @BeforeEach
    void setUp() {
        s = new Shelf();
        b = new Bag();
    }

    @Test
    void getTile() {
        int c = random.nextInt(5);
        int r = random.nextInt(6);
        Tile t = b.getTile();
        s.putTile(r,c, t);
        assertEquals(t, s.getTile(r,c));
    }

    @Test
    void checkColumnEmptiness() {
        int c = random.nextInt(5);
        int r = random.nextInt(6);
        Tile t = b.getTile();
        s.putTile(r,c, t);
        assertEquals(4, s.checkColumnEmptiness(c));
    }

    @Test
    void checkEmptyRow() {
        int c = random.nextInt(5);
        int r = random.nextInt(6);
        Tile t = b.getTile();
        s.putTile(r,c, t);
        assertFalse(s.checkRowEmptiness(r));
    }

    @Test
    void getColumn() {
        int c = random.nextInt(5);
        Tile t;
        Tile[] column = new Tile[6];
        for (int i = 0; i < 6; i++) {
            t = b.getTile();
            s.putTile(i, c, t);
            column[i] = t;
        }
        assertArrayEquals(column, s.getColumn(c));
    }

    @Test
    void getRow() {
        int r = random.nextInt(6);
        Tile t;
        Tile[] row = new Tile[5];
        for (int i = 0; i < 5; i++) {
            t = b.getTile();
            s.putTile(r,i,t);
            row[i] = t;
        }
        assertArrayEquals(row, s.getRow(r));
    }

    @Test
    void checkAdjacents() {
    }

    @Test
    void getShelf() {
        int c = random.nextInt(5);
        int r = random.nextInt(6);
        Tile[][] shelf = new Tile[6][5];
        Tile t = b.getTile();
        s.putTile(r, c, t);
        shelf[r][c]=t;
        assertArrayEquals(shelf, s.getShelf());
    }
}