package it.polimi.ingsw;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.Algorythms.CommonGoalCard1;

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

        Bag bag = new Bag();
        Board board = new Board(4);
        DeckPersonal deckPersonal = new DeckPersonal();
        CommonGoalCard[] commonGoalCards = new CommonGoalCard[2];
        commonGoalCards[0] = new CommonGoalCard(new CommonGoalCard1(),4);
        commonGoalCards[1] = new CommonGoalCard(new CommonGoalCard1(),4);
        Player player = new Player("0", deckPersonal.popPersonalCard());
        Player player1 = new Player("1", deckPersonal.popPersonalCard());
        Player player2 = new Player("2", deckPersonal.popPersonalCard());
        Player player3 = new Player("3", deckPersonal.popPersonalCard());
        List<Player> lista = new ArrayList<>();
        lista.add(player);
        lista.add(player1);
        lista.add(player2);
        lista.add(player3);
        Game game = new Game(4,lista);
        board.fillBoard(bag);
        game.startGame();







    }
}
