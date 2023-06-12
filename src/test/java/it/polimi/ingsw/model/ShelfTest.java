package it.polimi.ingsw.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
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
        s.putTile(r, c, t);
        assertEquals(t, s.getTile(r, c));
    }

    @Test
    void checkColumnEmptiness() {
        int c = random.nextInt(5);
        int r = random.nextInt(6);
        Tile t = b.getTile();
        s.putTile(r, c, t);
        assertEquals(5, s.checkColumnEmptiness(c));
    }

    @Test
    void checkRowFullness() {
        int c = random.nextInt(5);
        int r = random.nextInt(6);
        Tile t = b.getTile();
        s.putTile(r, c, t);
        assertFalse(s.checkRowFulness(r));
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
            s.putTile(r, i, t);
            row[i] = t;
        }
        assertArrayEquals(row, s.getRow(r));
    }


    @Test
    void getShelf() {
        int c = random.nextInt(5);
        int r = random.nextInt(6);
        Tile[][] shelf = new Tile[6][5];
        Tile t = b.getTile();
        s.putTile(r, c, t);
        shelf[r][c] = t;
        assertArrayEquals(shelf, s.getShelf());
    }


    @Test
    void checkAdjacents() {
        s.putTile(5,0, new Tile(Color.GREEN));
        s.putTile(5,1, new Tile(Color.GREEN));
        s.putTile(4,1, new Tile(Color.GREEN));
        s.putTile(4,0, new Tile(Color.BLUE));
        s.putTile(3,0, new Tile(Color.BLUE));
        s.putTile(3,1, new Tile(Color.BLUE));
        s.putTile(3,2, new Tile(Color.BLUE));
        s.putTile(4,2, new Tile(Color.BLUE));
        s.putTile(5,2, new Tile(Color.BLUE));
        s.putTile(5,3, new Tile(Color.YELLOW));
        s.putTile(4,3, new Tile(Color.GREEN));
        s.putTile(3,3, new Tile(Color.GREEN));
        s.putTile(2,3, new Tile(Color.GREEN));
        s.putTile(5,4, new Tile(Color.YELLOW));
        s.putTile(4,4, new Tile(Color.YELLOW));
        s.putTile(3,4, new Tile(Color.YELLOW));
        s.putTile(2,4, new Tile(Color.YELLOW));
        s.putTile(1,4, new Tile(Color.YELLOW));
        s.putTile(1,3, new Tile(Color.YELLOW));
        s.putTile(1,2, new Tile(Color.YELLOW));
        s.putTile(2,2, new Tile(Color.YELLOW));

        int expectedPoints = 20;
        int actualPoints = s.checkAdjacents();
        assertEquals(expectedPoints, actualPoints);
    }

    @Test
    void dropTiles(){
        s.putTile(5,1, new Tile(Color.WHITE));
        s.putTile(5,2, new Tile(Color.WHITE));
        s.putTile(5,3, new Tile(Color.WHITE));
        s.putTile(5,4, new Tile(Color.WHITE));
        s.putTile(4,4, new Tile(Color.WHITE));
        List<Tile> chosenTiles = new ArrayList<>();
        Tile tile1 = new Tile(Color.YELLOW, 2);
        Tile tile2 = new Tile(Color.WHITE, 0);
        Tile tile3 = new Tile(Color.BLUE, 1);
        chosenTiles.add(tile1);
        chosenTiles.add(tile2);
        chosenTiles.add(tile3);
        //s.dropTiles(chosenTiles, 4);
    }

    @Test
    void checkAdjacents_1() {
        s.putTile(5, 0, new Tile(Color.WHITE));
        s.putTile(4, 0, new Tile(Color.WHITE));
        s.putTile(3, 0, new Tile(Color.WHITE));


        assertEquals(2, s.checkAdjacents());
    }

    @Test
    void copyShelf(){
        int c = random.nextInt(5);
        int r = random.nextInt(6);
        Tile t = b.getTile();
        s.putTile(r, c, t);
        Shelf s1 = s.copyShelf();
        s1.putTile(random.nextInt(6), random.nextInt(5), b.getTile());
        assertNotSame(s, s1);
    }

    @Test
    void isFull(){
        int c = random.nextInt(5);
        int r = random.nextInt(6);
        Tile[][] shelf = new Tile[6][5];
        Tile t = b.getTile();
        s.putTile(r, c, t);
        assertFalse(s.isFull());
    }
}