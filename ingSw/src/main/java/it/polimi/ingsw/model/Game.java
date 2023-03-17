package it.polimi.ingsw.model;
import java.util.List;

public class Game {
    private int numberPartecipants;
    private List<Player> players;
    private CommonGoalCard[] commonGoals;
    private Board board;
    private Bag bag;
    private boolean hasStarted;
    private boolean endPointGiven;

    public Game(int partecipants){
        numberPartecipants = partecipants;


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
