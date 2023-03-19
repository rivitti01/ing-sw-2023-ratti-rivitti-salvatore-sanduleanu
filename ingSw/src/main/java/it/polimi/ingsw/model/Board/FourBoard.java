package it.polimi.ingsw.model.Board;

public class FourBoard extends Board{
    FourBoard fourBoard;
    public FourBoard(int players){
        super(9);
    }

    @Override
    public void setupBoard() {
        //read from json file
    }
}
