package it.polimi.ingsw.model;
import java.util.List;

public class Game {
    private int numberPartecipants;
    private List<Player> players;
    private CommonGoalCard[] commonGoals;
    private Board LivingRoom;
    private Bag bag;
    private boolean hasStarted;
    private boolean endPointGiven;



    public void fillBoard(){}
    public void addPartecipant(){}
    private void setFirstPlayer(){}

    public void startGame(){}
    public void endGame(){}
    public Player findWinner(){}
}
