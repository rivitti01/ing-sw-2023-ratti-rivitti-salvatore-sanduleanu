package it.polimi.ingsw.model;
import it.polimi.ingsw.model.Board;

import java.util.List;
import static it.polimi.ingsw.Costants.*;

public class Game {
    private int numberPartecipants;
    private List<Player> players;
    private CommonGoalCard[] commonGoals;
    private Board board;
    private Bag bag;
    private boolean hasStarted;
    private boolean endPointGiven;

    public Game(int partecipants){
        commonGoals = new CommonGoalCard[COMMON_CARDS_PER_GAME];
        numberPartecipants = partecipants;
        //--------------------------------
        //estrarre due numeri casuali, da questi creare un ciclo per definire le strategie (as esempio):
        //if (x = 1)
        //commonGoals[0].setCardStrategy(new CommonGoalCard1());
        //if (y = 2)
        //commonGoals[1].setCardStrategy(new CommonGoalCard2());
        //--------------------------------



    }



    public void fillBoard(){}
    public void addPartecipant(){}
    private void setFirstPlayer(){}

    public void startGame(){
        players.get(0).getTiles(board); // e tutta la logica di gioco
    }
    public void endGame(){}
    public Player findWinner(){
        return null;
    }//cose sul main
}
