package it.polimi.ingsw.model.Algorythms;

import it.polimi.ingsw.model.Shelf;

public class CommonGoalCard2 implements CardStrategy {
    public boolean algorythm(Shelf myShelf) {
        return myShelf.getTile(0, 0).getColor() ==
                        myShelf.getTile(0, 4).getColor() &&
                myShelf.getTile(0, 0).getColor()!= null && myShelf.getTile(0, 4).getColor() != null &&
                myShelf.getTile(0, 4).getColor() ==
                        myShelf.getTile(5, 4).getColor() &&
                myShelf.getTile(5, 0).getColor()!= null && myShelf.getTile(5, 4).getColor() != null &&
                myShelf.getTile(5, 4).getColor() ==
                        myShelf.getTile(5, 0).getColor();
    }
    public int getId(){
        return 2;
    }

    @Override
    public String toString() {
        return "Quattro tessere dello stesso tipo" +
                " ai quattro angoli della Libreria.";
    }
}
