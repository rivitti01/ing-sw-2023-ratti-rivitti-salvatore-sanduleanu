package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.shuffle;

public class Bag {
     static final int tileTypes = 6;
     static final int tilesPerColor = 22;
    final List<Tile> bag;


    public Bag(){
        bag = new ArrayList<>();

        for (int i = 0; i < tileTypes; i++){
            for (int j = 0; j < tilesPerColor; j++){
                switch (i) {
                    case 0 -> bag.add(new Tile(Color.CYAN, j % 3));
                    case 1 -> bag.add(new Tile(Color.BLUE, j % 3));
                    case 2 -> bag.add(new Tile(Color.GREEN, j % 3));
                    case 3 -> bag.add(new Tile(Color.PINK, j % 3));
                    case 4 -> bag.add(new Tile(Color.WHITE, j % 3));
                    default -> bag.add(new Tile(Color.YELLOW, j % 3));
                }
                }
            }
        shuffle(bag);
    }




    public Tile getTile(){
        return bag.remove(0);
    }
}
