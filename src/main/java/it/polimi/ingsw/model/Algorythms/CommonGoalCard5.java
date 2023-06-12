package it.polimi.ingsw.model.Algorythms;
import static it.polimi.ingsw.util.Costants.*;
import it.polimi.ingsw.model.Shelf;
import it.polimi.ingsw.model.Tile;

import java.util.ArrayList;

public class CommonGoalCard5 implements CardStrategy {

    public boolean algorythm(Shelf myShelf) {

        int i = 0, j = 0, k = 0, colorCounter = 1, columnCounter = 0;
        boolean colorFlag = false;
        ArrayList<Tile[]> fullColumns = new ArrayList<>();

        for (i = 0; i < SHELF_COLUMN-1; i++) {
            if (myShelf.checkColumnEmptiness(i) == 0) {
                fullColumns.add(myShelf.getColumn(i));
            }
        }

        if (fullColumns.size() < 3) {
            return false;
        } else {
            for (Tile[] tiles : fullColumns) {
                colorCounter = 1;
                for (j = 1; j < SHELF_ROWS-1; j++) {
                    for (k = 0; k < j; k++) {
                        if (tiles[j].getColor().equals(tiles[k].getColor())) {
                            colorFlag = false;
                            k = j;
                        } else {
                            colorFlag = true;
                        }
                    }
                    if (colorFlag) {
                        colorCounter++;
                    }
                    colorFlag = false;
                }
                if (colorCounter <= 3) {
                    columnCounter++;
                }
            }
            return columnCounter >= 3;
        }
    }

    @Override
    public String toString() {
        return """
                Three columns each formed by 6 tiles
                of maximum three different types. One
                column can show the same or a different
                combination of another column.""";
    }

    public String getCardName(){return "5common";}
}

