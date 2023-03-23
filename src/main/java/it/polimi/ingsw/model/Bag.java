package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.shuffle;

public class Bag {
    static final int tilesPerColor = 22;
    final List<Tile> bag;


    public Bag(){
        bag = new ArrayList<>();

        for(Color c : Color.values()) {
            for (int j = 0; j < tilesPerColor; j++) {
                if (c.equals(Color.TRANSPARENT))
                    break;
                else
                    bag.add(new Tile(c, j % 3));
            }


            shuffle(bag);
        }
    }


    public Tile getTile(){
        return bag.remove(0);
    }
}
