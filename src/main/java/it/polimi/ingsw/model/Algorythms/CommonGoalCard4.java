package it.polimi.ingsw.model.Algorythms;

import it.polimi.ingsw.model.Shelf;


import static it.polimi.ingsw.util.Costants.SHELF_COLUMN;
import static it.polimi.ingsw.util.Costants.SHELF_ROWS;


public class CommonGoalCard4 implements CardStrategy {

    public boolean algorythm(Shelf myShelf) {
        Shelf copiedShelf = myShelf.copyShelf();
        int squareCounter = 0;
        int r = 0, c = 0;
        for (r = 0; r < SHELF_ROWS-1; r++) {
            for (c = 0; c < SHELF_COLUMN-1; c++) {
                try{
                    if (copiedShelf.getTile(r,c).getColor().equals(copiedShelf.getTile(r+1,c).getColor())
                            && copiedShelf.getTile(r+1,c).getColor().equals(copiedShelf.getTile(r,c+1).getColor())
                            && copiedShelf.getTile(r,c+1).getColor().equals(copiedShelf.getTile(r+1,c+1).getColor())) {
                        copiedShelf.setTile(r,c, null);
                        copiedShelf.setTile(r+1,c, null);
                        copiedShelf.setTile(r,c+1, null);
                        copiedShelf.setTile(r+1,c, null);
                        squareCounter++;
                        c++;
                    }
                } catch (NullPointerException ignored){
                }



                    if (squareCounter >= 2) {
                        return true;
                    }
                }
            }
            return false;
        }




    @Override
    public String toString() {
        return """
                Two groups each containing 4 tiles of
                the same type in a 2x2 square. The tiles
                of one square can be different from
                those of the other square.""";
    }

    public String getCardName(){return "4common";}
}


