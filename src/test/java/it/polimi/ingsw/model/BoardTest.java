package it.polimi.ingsw.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
public class BoardTest {
    Board board1;
    Board board2;
    Board board3;
    Board empty;
    Bag bag1;
    Bag bag2;
    Bag bag3;

    Board board4;
    Bag bag4;

    Tile tmp;

    @BeforeEach
    void setup(){
        this.bag1 = new Bag();
        this.bag2 = new Bag();
        this.bag3 = new Bag();
        board1 = new Board(4);
        board2 = new Board(4);
        board3 = new Board(2);
        this.board1.fillBoard(bag1);
        this.board2.fillBoard(bag2);
        this.board3.fillBoard(bag3);

        this.board4 = new Board(4);
        this.bag4 = new Bag();
        this.board4.fillBoard(bag4);

        empty = new Board(4);

    }

    @Test
    void availableCoordinates(){
        List<int[]> available1 = this.board1.getAvailableTiles();
        List<int[]> available2 = this.board2.getAvailableTiles();
        List<int[]> available3 = this.board3.getAvailableTiles();
    }

    @Test
    void checkRefill(){
        for(int i=0; i<9; i++){
            for(int j=0; j<9; j++){
                if(i==3 && j==3) continue;
                else if(i==4 && j==7) continue;
                else if(i==5 && j==5) continue;
                else if(i==7 && j==4) continue;
                else tmp = this.board1.popTile(i, j);
            }
        }
        for(int i=0; i<9; i++) {
            for (int j = 0; j < 9; j++) {
                if (i == 3 && j == 3) continue;
                else if (i == 3 && j == 4) continue;
                else tmp = this.board2.popTile(i, j);
            }
        }

        for(int i=0; i<9; i++){
            for(int j=0; j<9; j++){
                if(i==0 && j==3) continue;
                else if(i==2 && j==6) continue;
                else if(i==4 && j==8) continue;
                else if(i==6 && j==6) continue;
                else if(i==4 && j==0) continue;
                else if(i==6 && j==2) continue;
                else if(i==8 && j==4) continue;
                else if(i==8 && j==5) continue;
                else tmp = this.board4.popTile(i, j);
            }
        }


        //ok
        assertFalse(this.board2.checkRefill());
        //ok
        assertTrue(this.board1.checkRefill());
        //ok
        assertTrue(this.empty.checkRefill());
        //
        assertFalse(this.board4.checkRefill());
    }
}
