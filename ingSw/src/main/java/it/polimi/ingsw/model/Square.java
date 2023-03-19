package it.polimi.ingsw.model;

public class Square implements CardStrategy{

    public boolean algorythm(Shelf myShelf) {
        Tile[][] copiedShelf = myShelf.getShelf();
        int squareCounter = 0;
        int r = 0 ,c = 0;
        for (r=0; r<4; r++){
            for(c= 0; c<3; c++){
                if(copiedShelf[r][c].getColor()!=null
                        && copiedShelf[r][c].getColor().equals(copiedShelf[r+1][c].getColor())
                        && copiedShelf[r+1][c].getColor().equals(copiedShelf[r][c+1].getColor())
                        && copiedShelf[r][c+1].getColor().equals(copiedShelf[r+1][c+1].getColor())) {
                    copiedShelf[r][c] = null;
                    copiedShelf[r+1][c] = null;
                    copiedShelf[r][c+1] = null;
                    copiedShelf[r+1][c+1] = null;
                    squareCounter++;
                    c++;
                }
                if (squareCounter>=2){
                    return true;
                }
            }
        }
        return false;
    }
}
