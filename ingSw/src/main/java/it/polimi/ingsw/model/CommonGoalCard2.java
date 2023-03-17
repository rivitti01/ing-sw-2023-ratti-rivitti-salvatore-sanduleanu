package it.polimi.ingsw.model;

public class CommonGoalCard2 implements CardStrategy{
    public boolean algorythm(Shelf myShelf) {
        return myShelf.getTile(0, 0).getColor() ==
                        myShelf.getTile(0, 5).getColor() &&
                myShelf.getTile(0, 5).getColor() ==
                        myShelf.getTile(6, 5).getColor() &&
                myShelf.getTile(6, 5).getColor() ==
                        myShelf.getTile(6, 0).getColor();
    }
}
