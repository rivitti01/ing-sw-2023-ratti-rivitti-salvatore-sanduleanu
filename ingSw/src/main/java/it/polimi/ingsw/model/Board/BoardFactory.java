package it.polimi.ingsw.model.Board;

public class BoardFactory {

    public static Board createBoard(int players){
        switch (players){
            case 2:
                return new TwoBoard(2);
            case 3:
                return new ThreeBoard(3);
            case 4:
                return new FourBoard(4);
            default:
                throw new IllegalArgumentException("Invalid number of boards: " + players);
        }

    }
}
