package it.polimi.ingsw.model.Algorythms;

import it.polimi.ingsw.model.Shelf;

public class CommonGoalCard11 implements CardStrategy{
    public String toString(){
        return "Five tiles of the same type forming an X.";
    }
    public String getCardName(){return "11common";}


    public boolean algorithm(Shelf myShelf) {  // 5 Tiles of same color placed like a cross
        for (int r=1; r<5; r++){
            for (int c=1; c<4; c++) {
                if(myShelf.getTile(r, c)!=null && myShelf.getTile(r-1, c+1)!=null &&
                        myShelf.getTile(r+1, c+1)!=null && myShelf.getTile(r-1, c-1)!=null &&
                            myShelf.getTile(r, c).getColor() == myShelf.getTile(r-1, c+1).getColor() &&
                            myShelf.getTile(r, c).getColor() == myShelf.getTile(r+1, c+1).getColor() &&
                            myShelf.getTile(r, c).getColor() == myShelf.getTile(r+1, c-1).getColor() &&
                            myShelf.getTile(r, c).getColor() == myShelf.getTile(r-1, c-1).getColor()
                )
                        return true;
            }
        }
        return false;
    }
}
