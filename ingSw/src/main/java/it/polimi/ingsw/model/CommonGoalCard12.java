package it.polimi.ingsw.model;

public class CommonGoalCard12 {
    public boolean algorythm(Shelf myShelf) { // da fare usando parallelismo
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
