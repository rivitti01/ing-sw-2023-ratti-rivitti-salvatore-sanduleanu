package it.polimi.ingsw.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BagTest {
    private Bag bag;

    @BeforeEach
    void setBag(){
        bag = new Bag();
    }

    @Test
    void getTile(){
        bag.getTile();
        bag.getTile();
        bag.getTile();
        bag.getTile();
        bag.getTile();
        bag = new Bag();
        bag.getTile();
        bag.getTile();


    }
}
