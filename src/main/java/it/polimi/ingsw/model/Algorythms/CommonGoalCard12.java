package it.polimi.ingsw.model.Algorythms;

import it.polimi.ingsw.model.Shelf;

public class CommonGoalCard12 implements CardStrategy{
    public String toString(){
        return "Five columns of increasing or decreasing" +
                " height. Starting from the first column on" +
                " the left or on the right, each next column" +
                " must be made of exactly one more tile. " +
                "Tiles can be of any type.";
    }



    public boolean algorythm(Shelf myShelf) { // 5 columns, each has 1 more Tile than the previous colomn or 1 less
        boolean tmp = true;
        for (int c = 0; c < 4; c++) {
            if(myShelf.checkColumnEmptiness(c) <= myShelf.checkColumnEmptiness(c+1))
                tmp = false;
        }
        if(tmp == true)
            return tmp;

        tmp = true;

        for (int c = 0; c < 4; c++) {
            if(myShelf.checkColumnEmptiness(c) >= myShelf.checkColumnEmptiness(c+1))
                tmp = false;
        }
        return tmp;
    }

    public String getCardName(){return "12common";}
}
