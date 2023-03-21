package it.polimi.ingsw;

import it.polimi.ingsw.model.Bag;
import it.polimi.ingsw.model.Board.Board;
import it.polimi.ingsw.model.Board.BoardFactory;
import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.Tile;


import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) {
        DeckPersonal deckPersonal = new DeckPersonal();
        deckPersonal.popPersonalCard();
        deckPersonal.popPersonalCard();
        deckPersonal.popPersonalCard();




        Bag oneBag = new Bag(); // prove xD lol
        BoardFactory board = new BoardFactory();
        Board myBoard = board.createBoard(4);
        myBoard.setupBoard();
        myBoard.fillBoard(oneBag);
        List<Tile> tiles = new ArrayList<>();
        int i = 0;
        while (i != 3){
            tiles.add(oneBag.getTile());
            i++;
        }
        /*ListIterator<Tile> tmp = tiles.listIterator();
        Color c = tmp.next().getColor();
        c = tmp.next().getColor();
        c = tmp.next().getColor();
        c = tmp.next().getColor();// non va oltrw
        c = tmp.next().getColor();
        c = tmp.next().getColor();
        c = tmp.next().getColor();
        c = tmp.next().getColor();*/






    }
}
