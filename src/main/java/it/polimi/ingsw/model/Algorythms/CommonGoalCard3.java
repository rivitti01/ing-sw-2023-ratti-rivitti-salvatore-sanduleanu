package it.polimi.ingsw.model.Algorythms;

import it.polimi.ingsw.model.Shelf;

public class CommonGoalCard3 implements CardStrategy {
    private Shelf tmpShelf;
    public boolean algorithm(Shelf myShelf) {
        if (myShelf == null) {
            return false;
        }
        tmpShelf = myShelf.copyShelf();
        int counter = 0;
        for (int r = 0; r < 6; r++ ){ // 0,1,2,3,4,5 righe; 0,1,2,3,4 colonne
            for (int c = 0; c < 4; c++){
                if (tmpShelf.getTile(r,c)!= null && tmpShelf.getTile(r,c+1) != null &&
                        tmpShelf.getTile(r,c).getColor() == tmpShelf.getTile(r,c+1).getColor()){
                    if (c == 2){
                        counter++;
                        removeRow(r);
                    }
                }else
                    break;
            }
        }
        for (int c = 0; c < 5; c++ ){
            for (int r = 0; r < 5; r++){
                if (tmpShelf.getTile(r,c)!= null && tmpShelf.getTile(r+1,c) != null &&
                        tmpShelf.getTile(r,c).getColor() == tmpShelf.getTile(r+1,c).getColor()){
                    if (r == 4){
                        counter++;
                        removeColumn(c);
                    }
                }else
                    break;
            }
        }
        return counter >= 4;
    }

    public int getId(){
        return 3;
    }
    public String toString(){
        return "Four groups each containing at least\n" +
                "4 tiles of the same type (not necessarily in the depicted shape).\n" +
                "The tiles of one group can be different from those of another group.";
    }

    public String getCardName(){return "3common";}

    private void removeRow(int row){
        for (int c = 0; c < 5; c++) tmpShelf.putTile(row, c, null);
    }
    private void removeColumn(int col){
        for (int r = 0; r < 6; r++) tmpShelf.putTile(r, col, null);
    }
}
