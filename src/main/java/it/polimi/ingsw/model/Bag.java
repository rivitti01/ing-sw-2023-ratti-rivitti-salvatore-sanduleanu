package it.polimi.ingsw.model;
import static it.polimi.ingsw.util.Costants.*;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.shuffle;

/**
 * The Bag class represents a bag of tiles in the game.
 */
public class Bag {
    private final List<Tile> bag;

    /**
     * Constructs a new Bag instance.
     * Initializes the bag with tiles of different colors and values.
     */
    public Bag(){
        bag = new ArrayList<>();

        for(Color c : Color.values()) {
            for (int j = 0; j < TILES_PER_COLOR; j++) {
                if (c.equals(Color.TRANSPARENT))
                    break;
                else
                    bag.add(new Tile(c, j % 3));
            }
            shuffle(bag);
        }
    }

    /**
     * Removes and returns the first tile from the bag.
     *
     * @return the first tile from the bag
     */
    public Tile getTile(){
        return bag.remove(0);
    }
}
