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
                    bag.add(new Tile(Color.CYAN,j%3));
                else if (i == 1)
                    bag.add(new Tile(Color.BLUE,j%3));
                else if (i == 2)
                    bag.add(new Tile(Color.GREEN,j%3));
                else if (i == 3)
                    bag.add(new Tile(Color.PINK,j%3));
                else if (i == 4)
                    bag.add(new Tile(Color.WHITE,j%3));
                else
                    bag.add(new Tile(Color.YELLOW,j%3));
                }
            }
        shuffle(bag);
    }




    public Tile getTile(){
        return bag.remove(0);
    }
}
