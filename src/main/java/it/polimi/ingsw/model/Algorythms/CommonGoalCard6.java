package it.polimi.ingsw.model.Algorythms;

import it.polimi.ingsw.model.Shelf;

public class CommonGoalCard6 implements CardStrategy{
    public String toString(){
        return "Otto tessere dello stesso tipo. " +
                "Non ci sono restrizioni sulla posizione di queste tessere.";
    }

    public String getCardName(){return "6common";}


    public boolean algorythm(Shelf myShelf) {
        for (int r1=0; r1<6; r1++){
            for (int c1=0; c1<5; c1++) {
                int count = 0;
                for (int r2 = 0; r2 < 6; r2++) {
                    for (int c2 = 0; c2 < 5; c2++) {
                        if (myShelf.getTile(r1, c1)!=null && myShelf.getTile(r2, c2)!=null &&
                                myShelf.getTile(r1, c1).getColor()==myShelf.getTile(r2, c2).getColor())
                            count++;
                    }
                }
                if (count == 8)
                    return true;
            }
        }
        return false;
    }
}
