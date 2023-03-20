package it.polimi.ingsw.model.Algorythms;

import it.polimi.ingsw.model.Shelf;

public class CommonGoalCard12 {
    public boolean algorythm(Shelf myShelf) { // 5 columns, each has 1 more Tile than the previous colomn or 1 less
        boolean tmp = true;
        for (int c = 0; c < 4; c++) {
            if(myShelf.checkColumnEmptiness(c) <= myShelf.checkColumnEmptiness(c+1))
                tmp = false;
        }
        if(tmp == true)
            return tmp;

        for (int c = 0; c < 4; c++) {
            if(myShelf.checkColumnEmptiness(c) >= myShelf.checkColumnEmptiness(c+1))
                tmp = false;
        }
        return tmp;
    }
}
