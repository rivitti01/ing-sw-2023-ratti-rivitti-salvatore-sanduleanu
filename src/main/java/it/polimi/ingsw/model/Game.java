package it.polimi.ingsw.model;
import it.polimi.ingsw.model.Board.Board;

import java.util.LinkedList;
import java.util.List;
import static it.polimi.ingsw.Costants.*;

public class Game {
    private int numberPartecipants;
    private LinkedList<Player> players = new LinkedList();
    private CommonGoalCard[] commonGoals;
    private Board board;
    private Bag bag;
    private boolean hasStarted;
    private boolean endPointGiven;
    private Player currentPlayer;
    private Player nextPlayer;

    public Game(int partecipants){
        commonGoals = new CommonGoalCard[COMMON_CARDS_PER_GAME];
        numberPartecipants = partecipants;




    }



    public void fillBoard(){}
    public void addPartecipant(){}
    private void setFirstPlayer(){}
    public Player getplayer(){return currentPlayer;}
    public void start(){
        players.get(0).getTiles(board); // e tutta la logica di gioco
    }
    public void endGame(){}
    public Player findWinner(){
        return null;
    }//cose sul main
}
