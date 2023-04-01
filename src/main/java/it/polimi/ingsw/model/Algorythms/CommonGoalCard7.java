package it.polimi.ingsw.model.Algorythms;

import it.polimi.ingsw.model.Shelf;

import static it.polimi.ingsw.Costants.SHELF_COLUMN;
import static it.polimi.ingsw.Costants.SHELF_ROWS;

public class CommonGoalCard7 implements CardStrategy {

    public boolean algorythm(Shelf myShelf) {
        for (int r=0; r<SHELF_ROWS; r++)
            for(int c=0; c<SHELF_COLUMN; c++){   //scorro la shelf

                if( (c <= SHELF_COLUMN-5) && (r <= SHELF_ROWS-5) ){  //se posso checkare per una diagonale principale
                    for(int i=0; myShelf.getTile(i+r,i+c).getColor() == myShelf.getTile(i+r+1,i+c+1).getColor(); i++)
                        if(i==3) return true;
                }

                if( (c <= SHELF_COLUMN-5) && (r >= 5) )   {    //se posso checkare per una diagonale secondaria
                    for(int i=0; myShelf.getTile(i+r,i+c).getColor() == myShelf.getTile(i+r-1,i+c+1).getColor(); i++)
                        if(i==3) return true;
                }

            }
    return false;
    }


    public String toString() {
        return "Cinque tessere dello stesso tipo che formano una diagonale.";
    }


}