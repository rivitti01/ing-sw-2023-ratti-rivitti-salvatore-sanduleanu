package it.polimi.ingsw.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class BoardTest {
    Board board1;
    Board board2;
    Board board3;
    Bag bag1;
    Bag bag2;
    Bag bag3;

    @BeforeEach
    void setup(){
        this.bag1 = new Bag();
        this.bag2 = new Bag();
        this.bag3 = new Bag();
        board1 = new Board(4);
        board2 = new Board(3);
        board3 = new Board(2);
        this.board1.fillBoard(bag1);
        this.board2.fillBoard(bag2);
        this.board3.fillBoard(bag3);
    }

    @Test
    void availableCoordinates(){
        List<int[]> available1 = this.board1.getAvailableTiles2();
        List<int[]> available2 = this.board2.getAvailableTiles2();
        List<int[]> available3 = this.board3.getAvailableTiles2();
    }
}
