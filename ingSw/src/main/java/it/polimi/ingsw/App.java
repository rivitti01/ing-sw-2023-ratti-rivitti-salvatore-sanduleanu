package it.polimi.ingsw;

import it.polimi.ingsw.model.Bag;
import it.polimi.ingsw.model.Board.Board;
import it.polimi.ingsw.model.Board.BoardFactory;
import it.polimi.ingsw.model.Tile;

import java.util.ArrayList;
import java.util.List;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) {
        Bag oneBag = new Bag(); // prove xD lol
        BoardFactory board = new BoardFactory();
        Board myBoard = board.createBoard(4);
        myBoard.setupBoard();
        myBoard.fillBoard(oneBag);
        List<Tile> tiles = new ArrayList<>();
        int i = 0;
        while (i != 10){
            tiles.add(oneBag.getTile());
            i++;
        }





    }
}
