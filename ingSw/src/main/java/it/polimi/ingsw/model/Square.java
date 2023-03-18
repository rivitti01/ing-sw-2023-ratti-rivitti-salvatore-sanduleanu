package it.polimi.ingsw.model;

public class Square implements CardStrategy{

    public boolean algorythm(Shelf myShelf) {
        int squareCounter = 0;
        boolean squareFlag = false;
        int squareColumn=0;
        int r,c = 0;
        for (r=0; r<4; r++){
            if (squareFlag==true){
                c=squareColumn;
                squareFlag=false;
            }
            for(c=0; c<3; c++){
                if(myShelf.getTile(r, c).getColor().equals(myShelf.getTile(r+1, c+1).getColor().equals(myShelf.getTile(r+1, c).getColor().equals(myShelf.getTile(r, c+1).getColor())))){
                    squareCounter++;
                    squareFlag=true;
                    c=c+2;
                    squareColumn=c;
                }
                if (squareCounter==2){
                    return true;
                }
            }
        }
        return false;
    }
}
