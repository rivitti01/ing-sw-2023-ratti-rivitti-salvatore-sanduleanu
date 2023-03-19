package it.polimi.ingsw.model;

import java.util.ArrayList;

public class ThreeColumns implements CardStrategy{

    public boolean algorythm(Shelf myShelf) {

        int i = 0, j = 0, k = 0, colorCounter=1;
        boolean colorFlag = false;
        ArrayList<Tile[]> fullColumns = new ArrayList<Tile[]>();

        for (i=0; i<4; i++){
            if(myShelf.checkColumnEmptiness(i)==0) {
                fullColumns.add(myShelf.getColumn(i));
            }
        }

        if (fullColumns.size()<3){
            return false;
        } else {
            for (Tile[] tiles: fullColumns) {
                colorCounter=1;
                for (j = 1; j < 5; j++) {
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
                    fullColumns.remove(tiles);
                }
            }
            return fullColumns.size() >= 3;
        }
    }

}
