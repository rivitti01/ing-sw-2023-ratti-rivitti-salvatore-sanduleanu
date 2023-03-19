package it.polimi.ingsw.model.Board;

public class TwoBoard extends Board{
    private Board twoBoard;

    public TwoBoard(int players){
        super(7);
    }
    @Override
    public void setupBoard() {
        //read from json file
    }

}
