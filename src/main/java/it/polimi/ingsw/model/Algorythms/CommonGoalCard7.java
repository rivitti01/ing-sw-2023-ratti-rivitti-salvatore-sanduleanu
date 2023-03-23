package it.polimi.ingsw.model.Algorythms;

import it.polimi.ingsw.model.Shelf;

public class CommonGoalCard7 implements CardStrategy {

    public boolean algorythm(Shelf myShelf) {
        //le diagonali possibili sono 4, le controllo una per volta
        for(int i=0; i<2; i++) {
            for(int j=0; myShelf.getTile(i+j,i+j).getColor() == myShelf.getTile(i+j+1,i+j+1).getColor(); j++)
                if(j==3) return true;
        }

        for(int i=4; i<6; i++) {
            for(int j=0; myShelf.getTile(i+j,i+j).getColor() == myShelf.getTile(i+j-1,i+j+1).getColor(); j++)
                if(j==3) return true;
        }

        return false;
    }


    public String toString() {
        return "Cinque tessere dello stesso tipo che formano una diagonale.";
    }


}