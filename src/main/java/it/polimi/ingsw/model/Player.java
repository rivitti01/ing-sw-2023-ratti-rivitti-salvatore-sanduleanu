package it.polimi.ingsw.model;



import java.util.ArrayList;
import java.util.List;
import static it.polimi.ingsw.util.Costants.*;

public class Player {
    final String nickname;
    private Shelf shelf;
    private boolean seat;
    private boolean[] goalsCompleted;
    private List<int[]> chosenCoordinates;
    private List<Tile> chosenTiles;
    private int chosenColumn;
    private PersonalGoalCard personalGoalCard;
    private int points;
    private int personalGoalPoints;
    private int adjacencyPoints;
    private boolean connected;

    /**
     * Constructs a new player with the specified nickname.
     *
     * @param nickname The player's nickname
     */
    public Player(String nickname){
        this.nickname = nickname;
        shelf = new Shelf();
        chosenCoordinates = new ArrayList<>();
        chosenTiles = new ArrayList<>();
        this.goalsCompleted = new boolean[COMMON_CARDS_PER_GAME];
        points = 0;
        personalGoalPoints = 0;
        adjacencyPoints = 0;
        chosenColumn = -1;
        this.connected = true;
    }

    /**
     * Gets the player's nickname.
     *
     * @return The player's nickname
     */
    public String getNickname(){return this.nickname;}
    public void setSeat(boolean seat) {
        this.seat = seat;
    }

    /**
     * Sets the connected status of the player.
     *
     * @param connected The connected status
     */
    public void setConnected(boolean connected){
        this.connected = connected;
    }

    /**
     * Checks if the player is connected.
     *
     * @return {@code true} if the player is connected, {@code false} otherwise
     */
    public boolean isConnected(){
        return this.connected;
    }

    /**
     * Sets the private goal card for the player.
     *
     * @param personalGoalCard the personal goal card to be set
     */
    public void setPrivateCard(PersonalGoalCard personalGoalCard){
        this.personalGoalCard = personalGoalCard;
    }


    /**
     * Adds points to the player's total points.
     *
     * @param points the points to be added
     */
    public void addPoints(int points){
        this.points += points;
    }

    /**
     * Returns the personal goal points earned by the player.
     *
     * @return the personal goal points earned
     */
    public int getPersonalGoalPoints(){
        return this.personalGoalPoints;
    }

    /**
     * Checks and calculates the personal goal points earned by the player.
     *
     * @return the personal goal points earned
     */
    public int checkPersonalPoints(){
        int count = 0;
        for (int i=0; i<SHELF_ROWS; i++){
            for(int j=0; j<SHELF_COLUMN; j++){
                if(personalGoalCard.goalsShelf[i][j]!=null && this.shelf.getTile(i, j)!=null &&
                        personalGoalCard.goalsShelf[i][j].getColor().equals(this.shelf.getTile(i, j).getColor()))
                    count++;
            }
        }
        return switch (count) {
            case 1 -> personalGoalPoints = 1;
            case 2 -> personalGoalPoints = 2;
            case 3 -> personalGoalPoints = 4;
            case 4 -> personalGoalPoints = 6;
            case 5 -> personalGoalPoints = 9;
            case 6 -> personalGoalPoints = 12;
            default -> personalGoalPoints = 0;
        };
    }

    /**
     * Returns the shelf of the player.
     *
     * @return the shelf of the player
     */
    public Shelf getShelf(){return this.shelf;}

    /**
     * Returns the total points earned by the player.
     *
     * @return the total points earned by the player
     */
    public int getPoints(){return this.points;}

    /**
     * Returns the list of chosen coordinates by the player.
     *
     * @return the list of chosen coordinates by the player
     */
    public List<int[]> getChosenCoordinates(){return this.chosenCoordinates;}

    /**
     * Adds the specified coordinates to the list of chosen coordinates.
     *
     * @param coordinates the coordinates to be added
     */
    public void addChosenCoordinate(int[] coordinates){
        this.chosenCoordinates.add(coordinates);
    }

    /**
     * Adds the specified tile to the list of chosen tiles.
     *
     * @param tile the tile to be added
     */
    public void addChosenTile(Tile tile){
        this.chosenTiles.add(tile);
    }

    /**
     * Returns the personal goal card of the player.
     *
     * @return the personal goal card of the player
     */
    public PersonalGoalCard getPersonalGoalCard(){
        return this.personalGoalCard;
    }

    /**
     * Resets the player's state and calculates points based on the completed goals.
     *
     * @param cards the array of common goal cards in the game
     */
    public void reset(CommonGoalCard[] cards){
        chosenTiles = new ArrayList<>();
        chosenCoordinates = new ArrayList<>(2);
        for (int i = 0; i < COMMON_CARDS_PER_GAME; i++) {
            if (!this.goalsCompleted[i] && cards[i].algorythm(this.shelf)) { // controlla per ogni common se e stato fatto l obiettivo
                addPoints(cards[i].getPoint());
                this.goalsCompleted[i] = true;
            }
        }
        this.adjacencyPoints = this.shelf.checkAdjacents();
        this.personalGoalPoints = checkPersonalPoints();
    }

    /**
     * Returns the list of tiles chosen by the player.
     *
     * @return the list of chosen tiles
     */
    public List<Tile> getChosenTiles() {
        return chosenTiles;
    }

    /**
     * Returns the column chosen by the player.
     *
     * @return the column chosen
     */
    public int getChosenColumn() {
        return chosenColumn;
    }

    /**
     * Sets the chosen column for the player.
     *
     * @param chosenColumn the chosen column index
     */
    public void setChosenColumn(int chosenColumn) {
        this.chosenColumn = chosenColumn;
    }

    /**
     * Returns the adjacency points of the player.
     *
     * @return the adjacency points
     */
    public int getAdjacencyPoints() {
        return adjacencyPoints;
    }

}