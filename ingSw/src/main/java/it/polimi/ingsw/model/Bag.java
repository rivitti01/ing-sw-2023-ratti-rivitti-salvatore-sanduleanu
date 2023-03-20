package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.shuffle;

public class Bag {
    final List<Tile> bag;


    public Bag(){
        bag = new ArrayList<>();

        for (int i = 0; i < 6; i++){
            for (int j = 0; j < 22; j++){
                if (i == 0)
                    bag.add(new Tile(Color.CYAN));
                else if (i == 1)
                    bag.add(new Tile(Color.BLUE));
                else if (i == 2)
                    bag.add(new Tile(Color.GREEN));
                else if (i == 3)
                    bag.add(new Tile(Color.PINK));
                else if (i == 4)
                    bag.add(new Tile(Color.WHITE));
                else
                    bag.add(new Tile(Color.YELLOW));
                }
            }
        shuffle(bag);
    }




    public Tile getTile(){
        return bag.remove(0);
    }
}
