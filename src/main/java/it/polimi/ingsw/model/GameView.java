package it.polimi.ingsw.model;


import java.io.Serializable;
import java.util.*;

/**
 * Represents a view of the game state.
 */
public class GameView  implements Serializable {

    static final long serialVersionUID = 1L;
    private final Board board;
    private final String nickName;
    private final Map<String, Shelf> playersShelves;
    private final PersonalGoalCard personal;
    private final String[] commons;
    private final String[] commonName;
    private final List<Tile> chosenTiles;
    private final ChatView chatView;
    private final int points;
    private final List<Integer> commonGoal1;
    private final List<Integer> commonGoal2;
    private  boolean yourTurn = false;
    private List<String> players;



    /**
     * Constructs a GameView object based on the given game model and player.
     *
     * @param model The game model
     * @param p     The player
     */
    public GameView(Game model, Player p){
        this.chosenTiles = model.getCurrentPlayer().getChosenTiles();
        this.playersShelves = new LinkedHashMap<>();
        this.commons = new String[2];
        this.commonName = new String[2];
        this.board = model.getBoard();
        this.nickName = model.getCurrentPlayer().getNickname();
        players = new ArrayList<>();
        for (Player player: model.getPlayers()){
            this.playersShelves.put(player.getNickname(), player.getShelf());
            this.players.add(player.getNickname());
        }
        this.personal = p.getPersonalGoalCard();
        for(int i = 0 ; i<2 ; i++){
            commons[i] = model.getCommonGoals()[i].getDescription();
            commonName[i] = model.getCommonGoals()[i].getName();
        }
        chatView = new ChatView(model, p);
        points = p.getPoints() + p.getAdjacencyPoints() + p.getPersonalGoalPoints();
        commonGoal1 = new ArrayList<>();
        commonGoal2 =  new ArrayList<>();
        commonGoal1.addAll(model.getCommonGoals()[0].getScores());
        commonGoal2.addAll(model.getCommonGoals()[1].getScores());
        if(p.getNickname().equals(model.getCurrentPlayer().getNickname()))
            yourTurn = true;
    }


    /**
     * Gets the total points of the player.
     *
     * @return The total points
     */
    public int getPoints() {
        return points;
    }


    /**
     * Gets the nickname of the player.
     *
     * @return The nickname
     */
    public String getNickName() {
        return this.nickName;
    }

    /**
     * Gets the shelves of all players.
     *
     * @return The map of player nicknames and their shelves
     */
    public Map<String, Shelf> getPlayersShelves() {
        return this.playersShelves;
    }

    public List<String> getPlayers() {
        return players;
    }

    /**
     * Gets the list of chosen tiles by the player.
     *
     * @return The list of chosen tiles
     */
    public List<Tile> getChosenTiles() {
        return chosenTiles;
    }


    /**
     * Gets the personal goal card of the player.
     *
     * @return The personal goal card
     */
    public PersonalGoalCard getPersonal() {
        return personal;
    }

    /**
     * Gets the game board.
     *
     * @return The game board
     */
    public Board getBoard(){
        return this.board;
    }

    /**
     * Gets the descriptions of the common goals.
     *
     * @return The array of common goal descriptions
     */
    public String[] getCommonGoals(){return this.commons;}

    /**
     * Gets the chat view associated with the game.
     *
     * @return The chat view
     */
    public ChatView getChatView(){
        return this.chatView;
    }

    /**
     * Gets the names of the common goals (useful for GUI).
     *
     * @return The array of common goal names
     */
    public String[] getNameGoals(){return this.commonName;}

    /**
     * Gets the scores for the first common goal.
     *
     * @return The list of scores for the first common goal
     */
    public List<Integer> getCommonGoal1() {
        return commonGoal1;
    }

    /**
     * Gets the scores for the second common goal.
     *
     * @return The list of scores for the second common goal
     */
    public List<Integer> getCommonGoal2() {
        return commonGoal2;
    }

    /**
     * Checks if it is currently the player's turn.
     *
     * @return {@code true} if it is the player's turn, {@code false} otherwise
     */
    public boolean isYourTurn() {
        return yourTurn;
    }
}

