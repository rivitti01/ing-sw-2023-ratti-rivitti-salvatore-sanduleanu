package it.polimi.ingsw.model.Algorythms;

import it.polimi.ingsw.model.Shelf;
import it.polimi.ingsw.model.Tile;

import java.util.ArrayList;

public class CommonGoalCard8 implements CardStrategy {

    public boolean algorythm(Shelf myShelf) {

        int i = 0, j = 0, k = 0, colorCounter=1;
        boolean colorFlag = false;
        ArrayList<Tile[]> fullRows = new ArrayList<Tile[]>();

        for (i=0; i<5; i++){
            if(!myShelf.checkEmptyRow(i)) {
                fullRows.add(myShelf.getRow(i));
            }
        }

        if (fullRows.size()<4){
            return false;
        } else {
            for (Tile[] tiles: fullRows) {
                colorCounter=1;
                for (j = 1; j < 4; j++) {
                    for (k = 0; k < j; k++) {
                        if (tiles[j].getColor().equals(tiles[k].getColor())){
                            colorFlag = false;
                            k=j;
                        } else {
                            colorFlag = true;
                        }
                    }
                    if (colorFlag){
                        colorCounter++;
                    }
                    colorFlag = false;
                }
                if (colorCounter > 3){
                    fullRows.remove(tiles);
                }
            }
            return fullRows.size() >= 4;
        }
    }

    @Override
    public String toString() {
        return "Quattro righe formate ciascuna da 5 tessere di uno, due o tre tipi differenti." +
                " Righe diverse possono avere combinazioni diverse di tipi di tessere.";
    }
}

