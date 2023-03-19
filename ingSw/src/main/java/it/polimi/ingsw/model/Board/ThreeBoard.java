package it.polimi.ingsw.model.Board;

public class ThreeBoard extends Board{
    ThreeBoard threeBoard;
    public ThreeBoard(int players){
        super(9);
    }

    @Override
    public void setupBoard() {
        //read from json file
    }
}
