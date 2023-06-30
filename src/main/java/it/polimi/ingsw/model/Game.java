package it.polimi.ingsw.model;


import it.polimi.ingsw.distributed.socket.ServerHandler;
import it.polimi.ingsw.util.Costants;
import it.polimi.ingsw.util.Warnings;
import it.polimi.ingsw.util.ModelListener;



import java.util.ArrayList;
import java.util.List;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Game {
    private int numberParticipants;
    private List<Player> players;
    private CommonGoalCard[] commonGoals;
    private Board board;
    private Bag bag;
    private Player currentPlayer;
    private Chat chat;
    //per capire se si è completata una shelf o meno (l'ho messo come attributo perchè veniva usato in startGame()
    private boolean lastTurn;
    private boolean start = false;
    private boolean end=false;
    private List<ModelListener> listener;

    private Warnings errorType = null;

    /**
     * Starts the game with the specified number of participants and players.
     *
     * @param numberParticipants the number of participants in the game
     * @param players            the list of players in the game
     */
    public void startGame(int numberParticipants, List<Player> players){
        this.chat = new Chat();
        this.numberParticipants = numberParticipants;
        this.players = new ArrayList<>();
        this.bag = new Bag();
        this.board = new Board(this.numberParticipants);
        this.board.fillBoard(this.bag);
        this.board.setBorderTiles();
        this.players = new ArrayList<>();
        setFirstPlayer(players);
        this.currentPlayer = this.players.get(0);
        this.commonGoals = new CommonGoalCard[2];

        // building a list of random Strings made like ""CommonGoalCard" + randomNumber"
        List<String> randomCommons = new ArrayList<>();
        while (randomCommons.size() < Costants.COMMON_CARDS_PER_GAME) {
            int randomNumber = ThreadLocalRandom.current().nextInt(1, 13);
            if (!randomCommons.contains("CommonGoalCard" + randomNumber)) {
                randomCommons.add("CommonGoalCard" + randomNumber);
            }
        }
        // building a list of random Strings made like ""goalStrategy" + randomNumber"
        List<String> randomPersonals = new ArrayList<>();
        while (randomPersonals.size() < this.players.size()) {
            int randomNumber = ThreadLocalRandom.current().nextInt(1, 13);
            if (!randomPersonals.contains("goalStrategy" + randomNumber)) {
                randomPersonals.add("goalStrategy" + randomNumber);
            }
        }
        for(int i=0; i< commonGoals.length; i++)
            commonGoals[i] = new CommonGoalCard(this.numberParticipants, randomCommons.get(i));
        for(int i=0; i<this.players.size(); i++)
            this.players.get(i).setPrivateCard(new PersonalGoalCard(randomPersonals.get(i)));

        listener.forEach(x->x.gameStarted(currentPlayer));
        listener.forEach(ModelListener::printGame);
    }

    /**
     * Returns the list of ModelListeners registered with the game.
     *
     * @return the list of ModelListeners
     */
    public List<ModelListener> getListener() {
        return listener;
    }

    /**
     * Returns the chat associated with the game.
     *
     * @return the chat object
     */
    public Chat getChat() {
        return chat;
    }

    /**
     * Sets the first player and updates the player order based on random selection.
     * Each player has as next player the one that has connected after them (clockwise order)
     *
     * @param players the list of players participating in the game
     */
    public void setFirstPlayer(List<Player> players){
        Random random = new Random();
        int tmp = random.nextInt(this.numberParticipants);
        List<Player> tempList = new ArrayList<>();
        tempList.add(players.get(tmp));
        for(int i = tmp+1; i<players.size(); i++)
            tempList.add(players.get(i));
        for(int i=0; i<tmp; i++){
            tempList.add(players.get(i));
        }
        this.players = tempList;
        this.players.get(0).setSeat(true);
        setCurrentPlayer(this.players.get(0));
    }

    /**
     * Checks if it is the last turn of the game.
     *
     * @return true if it is the last turn, false otherwise
     */
    public boolean isLastTurn() {
        return this.lastTurn;
    }

    /**
     * Sets the flag indicating whether it is the last turn of the game.
     *
     * @param lastTurn true if it is the last turn, false otherwise
     */
    public void setLastTurn(boolean lastTurn) {
        this.lastTurn = lastTurn;
        listener.forEach(ModelListener::isLastTurn);//listener.isLastTurn();
    }

    /**
     * Returns the list of players participating in the game.
     *
     * @return the list of players
     */
    public List<Player> getPlayers() {
        return this.players;
    }

    /**
     * Returns the board of the game.
     *
     * @return the board of the game
     */

    public Board getBoard() {
        return board;
    }

    /**
     * Returns the Bag of the game.
     *
     * @return the Bag of the game
     */
    public Bag getBag() {
        return bag;
    }

    /**
     * Returns the common goals of the game.
     *
     * @return the common goals of the game
     */
    public CommonGoalCard[] getCommonGoals() {
        return commonGoals;
    }

    /**
     * Returns the current player in the game.
     *
     * @return the current player
     */
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Returns a list of available tiles for the current player based
     * on their choices in that turn.
     *
     * @return a list of available tiles for the current player
     */
    public List<int[]> getAvailableTilesForCurrentPlayer(){
        int[]  chosenCoordinates1;
        int[]  chosenCoordinates2;
        try {
            chosenCoordinates1 = this.currentPlayer.getChosenCoordinates().get(0);
        }catch (IndexOutOfBoundsException e1){
            chosenCoordinates1 = null;
        }
        try{
            chosenCoordinates2 = this.currentPlayer.getChosenCoordinates().get(1);
        }catch (IndexOutOfBoundsException e2){
            chosenCoordinates2 = null;
        }
        return this.board.filterAvailableTiles(chosenCoordinates1, chosenCoordinates2, this.board.getBorderTiles());
    }

    /**
     * Sets the current player in the game.
     *
     * @param currentPlayer the player to set as the current player
     */
    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    /**
     * Sets the start flag of the game.
     *
     * @param s the value to set for the start flag
     */
    public void setStart(boolean s){
        this.start = s;
    }

    /**
     * Sets the end flag of the game.
     *
     * @param e the value to set for the end flag
     */
    public void setEnd(boolean e){
        this.end = e;
    }

    /**
     * Removes and returns the tile at the specified coordinates from the board.
     *
     * @param coordinates the coordinates of the tile to be popped from the board
     * @return the popped tile
     */
    public Tile popTileFromBoard(int[] coordinates){
        Tile poppedTile = this.board.popTile(coordinates[0], coordinates[1]);
        listener.forEach(ModelListener::printGame);//listener.printGame();
        return poppedTile;
    }

    /**
     * Sets the chosen column for the current player.
     *
     * @param c the chosen column index
     */
    public void setChosenColumnByPlayer(int c){
        this.currentPlayer.setChosenColumn(c);
    }


    public void askOrder(){
        listener.forEach(ModelListener::askOrder);
    }

    /**
     * Sets the error type for the game.
     * The errorType is used to notify the listener
     * that an invalid action has occurred.
     *
     * @param errorType the error type to be set
     */
    public void setErrorType(Warnings errorType){
        this.errorType = errorType;
        listener.forEach(x->x.warning(this.errorType, this.currentPlayer));//listener.warning(errorType, this.currentPlayer);
    }

    /**
     * Retrieves the error type associated with this instance of the Warnings class.
     *
     * @return The error type represented by this instance of Warnings.
     */
    public Warnings getErrorType() {
        return errorType;
    }

    /**
     * Sets the error type for the game with a specific nickname.
     *
     * @param errorType the error type to be set
     * @param nickname  the nickname of the player associated with the error
     */
    public void setErrorType(Warnings errorType, String nickname){
        listener.forEach(x->x.warning(errorType, nickname));//listener.warning(errorType, this.currentPlayer);
    }

    /**
     * Drops a tile into the player's shelf at the specified column.
     *
     * @param tile   the tile to be dropped
     * @param column the column in which the tile is dropped
     */
    public void droppedTile(Tile tile, int column){
        this.currentPlayer.getShelf().dropTile(tile, column);
        listener.forEach(ModelListener::printGame);//listener.printGame();
        if (this.currentPlayer.getChosenTiles().size()>1){
            return;
        }
        if(!this.currentPlayer.getChosenTiles().isEmpty())
            listener.forEach(ModelListener::askOrder);//listener.askOrder();
    }


    /**
     * Initiates a new turn in the game.
     * Notifies the registered listeners about the new turn and updates the game state.
     */
    public void newTurn(){
        listener.forEach(x->x.newTurn(this.currentPlayer));//listener.newTurn(this.currentPlayer);
        listener.forEach(ModelListener::printGame);//listener.printGame();
    }

    public void resumingTurn(){
        listener.forEach(ModelListener::resumingTurn);
        listener.forEach(ModelListener::printGame);
    }


    /**
     * Ends the game and calculates the final points for each player.
     * Notifies the registered listeners to print the final game state and display the final points.
     */
    public void endGame() {
        this.listener.forEach(ModelListener::printGame);
        setEnd(true);
        for(Player p : this.players) {
            p.addPoints(p.getShelf().checkAdjacents());
            p.addPoints(p.checkPersonalPoints());
        }
        this.listener.forEach(ModelListener::finalPoints);//finalPoints();
    }

    /**
     * Adds a ModelListener to the game.
     *
     * @param l the ModelListener to be added
     */
    public void addModelListener(ModelListener l){
        if (listener == null)
            listener = new ArrayList<>();
        listener.add(l);
    }

    /**
     * Removes a ModelListener from the game.
     *
     * @param l the ModelListener to be removed
     */
    public void removeModelListener(ModelListener l){
        listener.remove(l);
        //checkOnlinePlayers();
    }

    /**
     * Controls the selection of tiles by the current player.
     * If the player has not chosen any tiles, it sends a warning to the listeners.
     * Otherwise, it prompts the listeners to ask for the column selection.
     */
    public void selectionControl() {
        if (this.currentPlayer.getChosenTiles().size()==0) {
            listener.forEach(x->x.warning(Warnings.INVALID_ACTION, this.getCurrentPlayer()));//listener.warning(Warnings.INVALID_ACTION, this.getCurrentPlayer());
        } else {
            listener.forEach(ModelListener::askColumn);//listener.askColumn();
        }
    }

    /**
     * Checks if the current player has chosen the maximum number of tiles allowed.
     * If the maximum number of tiles is reached, no more actions can be performed, and
     * they will be asked to choose a column where to drop the tiles.
     * It sends a warning to the listeners if any of the following conditions are met:
     *   - The current player has chosen their shelf's maximum column space tiles.
     *   - There are no more available tiles for the current player.
     *   - The current player has already chosen the maximum number of tiles (3).
     * Otherwise, it prompts the listeners to ask for the next action.
     */
    public void checkMaxNumberOfTilesChosen() {
        if (this.currentPlayer.getShelf().getMaxColumnSpace() == this.currentPlayer.getChosenTiles().size() ||
                getAvailableTilesForCurrentPlayer().isEmpty() ||
                this.currentPlayer.getChosenTiles().size() == 3){
            listener.forEach(x->x.warning(Warnings.MAX_TILES_CHOSEN, this.getCurrentPlayer()));//this.listener.warning(Warnings.MAX_TILES_CHOSEN, this.getCurrentPlayer());
        }
        else listener.forEach(x->x.askAction());
    }

    /**
     * Checks if the game has ended.
     *
     * @return true if the game has ended, false otherwise.
     */
    public boolean isEnd() {
        return end;
    }

    /**
     * Checks if the game has started.
     *
     * @return true if the game has started, false otherwise.
     */
    public boolean isStart() {
        return start;
    }

    /**
     * Sends a new message between players.
     *
     * @param sender   the sender of the message
     * @param receiver the receiver of the message
     * @param message  the content of the message
     */
    public void newMessage(String sender, String receiver, String message)  {
        this.chat.newMessage(sender, receiver, message);
        listener.forEach(ModelListener::printGame);
    }

    /**
     * Prints the current state of the game.
     * This method notifies all registered listeners to print the game.
     */
    public void printGame(){
        listener.forEach(ModelListener::printGame);
    }
}
