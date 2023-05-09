package it.polimi.ingsw.model;
import static it.polimi.ingsw.util.Costants.*;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.shuffle;

public class Bag {
    private final List<Tile> bag;


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

    public List<Tile> getBag(){
        return this.bag;
    }

    public Tile getTile(){
        return bag.remove(0);
    }
}
