package it.polimi.ingsw.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TileTest {
    Tile tile;

    @BeforeEach
    void Tile(){
        tile = new Tile(Color.YELLOW, 2);
    }

    @Test
    void getColor(){
        Color expectedColor = Color.YELLOW;
        Color actualColor = tile.getColor();
        assertEquals(expectedColor, actualColor);
    }

    @Test
    void getType(){
        int expectedType = 2;
        int actualType = tile.getType();
        assertEquals(actualType, expectedType);
    }
}

