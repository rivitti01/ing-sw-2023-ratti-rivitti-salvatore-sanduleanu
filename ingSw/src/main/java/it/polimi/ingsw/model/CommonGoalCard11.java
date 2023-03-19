package it.polimi.ingsw.model;

public class CommonGoalCard11 {
    public boolean algorythm(Shelf myShelf) {
        for (int r=1; r<5; r++){
            for (int c=1; c<4; c++) {
                if(myShelf.getTile(r, c).getColor() == myShelf.getTile(r-1, c+1).getColor() &&
                   myShelf.getTile(r, c).getColor() == myShelf.getTile(r+1, c+1).getColor() &&
                   myShelf.getTile(r, c).getColor() == myShelf.getTile(r+1, c-1).getColor() &&
                   myShelf.getTile(r, c).getColor() == myShelf.getTile(r-1, c-1).getColor())
                    return true;
            }
        }
        return false;
    }
}
