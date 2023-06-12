package it.polimi.ingsw.model.Algorythms;

import it.polimi.ingsw.model.Shelf;

public class CommonGoalCard1 implements CardStrategy {

    public boolean algorythm(Shelf myShelf) { //potrebbe essere più efficiente
        if (myShelf == null){
            return false;
        }
        Shelf tmpShelf = myShelf.copyShelf();
        int counter = 0;
        for (int r = 0; r < 5; r++){
            for (int c = 0; c < 5; c++){
                if (tmpShelf.getTile(r,c) != null && tmpShelf.getTile(r+1,c) != null &&
                        tmpShelf.getTile(r,c).getColor() == tmpShelf.getTile(r+1,c).getColor()){
                    tmpShelf.putTile(r,c,null);
                    tmpShelf.putTile(r+1,c,null);
                    counter++;
                }
            }
        }
        for (int r = 0; r < 6; r++) {
            for (int c = 0; c < 4; c++) {
                if (tmpShelf.getTile(r,c) != null && tmpShelf.getTile(r,c+1) != null &&
                        tmpShelf.getTile(r,c).getColor() == tmpShelf.getTile(r,c+1).getColor()){
                    tmpShelf.putTile(r,c,null);
                    tmpShelf.putTile(r,c+1,null);
                    counter++;
                }
            }
        }
        return counter >= 6; // non deve essere uguale a 6, si possono fare fino a 15 corrispondenze di questo tipo
    }

    public int getId(){
        return 1;
    }

    @Override
    public String toString() {
        return "Sei gruppi separati formati ciascuno da due tessere adiacenti " +
                "dello stesso tipo (non necessariamente come mostrato in figura). " +
                "Le tessere di un gruppo possono essere diverse da quelle di un altro gruppo.";
    }

    public String getCardName(){return "1common";}
}
