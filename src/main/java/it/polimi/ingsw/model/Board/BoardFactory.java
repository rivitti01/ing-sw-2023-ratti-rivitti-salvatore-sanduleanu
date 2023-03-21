package it.polimi.ingsw.model.Board;

public class BoardFactory {

    public Board createBoard(int players){
        return switch (players) {
            case 2 -> new TwoBoard(2);
            case 3 -> new ThreeBoard(3);
            case 4 -> new FourBoard(4);
            default -> throw new IllegalArgumentException("Invalid number of boards: " + players);
        };

    }
}
