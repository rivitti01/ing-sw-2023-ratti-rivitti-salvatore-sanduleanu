package it.polimi.ingsw.model.Algorythms;
import static it.polimi.ingsw.util.Costants.*;
import it.polimi.ingsw.model.Shelf;
import it.polimi.ingsw.model.Tile;

import java.util.ArrayList;

public class CommonGoalCard8 implements CardStrategy {

    public boolean algorythm(Shelf myShelf) {

        int i = 0, j = 0, k = 0, colorCounter=1, rowCounter =0;
        boolean colorFlag = false;
        ArrayList<Tile[]> fullRows = new ArrayList<>();

        for (i=0; i<SHELF_ROWS-1; i++){
            if(myShelf.checkRowFulness(i)) {
                fullRows.add(myShelf.getRow(i));
            }
        }

        if (fullRows.size()<4){
            return false;
        } else {
            for (Tile[] tiles: fullRows) {
                colorCounter=1;
                for (j = 1; j < SHELF_COLUMN-1; j++) {
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
                if (colorCounter <= 3) {
                    rowCounter++;
                }
            }
            return rowCounter >= 3;
        }
    }

    @Override
    public String toString() {
        return """
                Four lines each formed by 5 tiles of
                maximum three different types. One
                line can show the same or a different
                combination of another line.""";
    }

    public String getCardName(){return "8common";}
}

