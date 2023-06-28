package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.util.ModelListener;
import it.polimi.ingsw.util.Warnings;


import java.rmi.RemoteException;
import java.util.*;

import static it.polimi.ingsw.util.Costants.END_GAME_POINT;
import static it.polimi.ingsw.util.Costants.SHELF_COLUMN;

/**
 * The GameController class handles the game logic and interacts with the Game model.
 */

public class GameController  {
    private Game model;
    private boolean gameAlreadystarted = false;
    private int numberPlayers;
    private List<Player> players;
    private boolean endPointGiven = false;

    /**
     * Constructs a GameController object with the specified Game model.
     *
     * @param model the Game model
     */
    public GameController (Game model){
        this.model = model;
        this.players = new ArrayList<>();
        this.numberPlayers = 0;
    }

    /**
     * Sets the number of players for the game.
     *
     * @param numberPlayers the number of players
     */

    public void setNumberPlayers(int numberPlayers) {
        this.numberPlayers = numberPlayers;
    }

    /**
     * Returns the number of players for the game.
     *
     * @return the number of players
     */
    public int getNumberPlayers() {
        return numberPlayers;
    }


    /**
     * Returns the list of players in the game.
     *
     * @return the list of players
     */
    public List<Player> getPlayers() {
        return players;
    }

    /**
     * Checks if the game has been initialized and starts the game if the conditions are met.
     * <p>
     * If the game has not already started and the number of players is equal to the expected number of players,
     * the model is initialized and the game is marked as started.
     * </p>
     */
    public void checkGameInitialization(){
        if(!gameAlreadystarted) {
            if (this.players.size() == this.numberPlayers) {
                initializeModel();
                gameAlreadystarted = true;
            }
        }
    }

    /**
     * Initializes the game model by starting the game with the specified number of players and player list.
     * It also sets the 'start' flag to true and updates the 'gameAlreadyStarted' flag.
     *
     * @see Game#startGame(int, List)
     */
    public void initializeModel(){
        model.startGame(this.numberPlayers, this.players);
        model.setStart(true);
        this.gameAlreadystarted = true;
    }

    /**
     * Sets the nickname for a player and adds them to the list of players.
     *
     * @param nickname the nickname to set for the player
     * @return {@code true} if the nickname is unique and added successfully, {@code false} otherwise
     */
    public boolean setPlayerNickname(String nickname){
        for (Player player : this.players) {
            if (player.getNickname().equals(nickname)) return false;
        }
        this.players.add(new Player(nickname));
        return true;
    }

    /**
     * Checks if a nickname already exists among the disconnected players in the model.
     *
     * @param nickname the nickname to check
     * @return {@code true} if the nickname already exists among disconnected players, {@code false} otherwise
     */
    public boolean checkingExistingNickname(String nickname){
        for (Player player : this.model.getPlayers()) {
            if (player.getNickname().equals(nickname) && !player.isConnected()) return true;
        }
        return false;
    }

    /**
     * Checks if there is at least one player who is offline (not connected).
     *
     * @return {@code true} if there is at least one player who is offline, {@code false} otherwise
     */
    public boolean playerOffline(){//controlla se c'è almeno un player offline
        for (Player player : this.model.getPlayers()) {
            if (!player.isConnected()) return true;
        }
        return false;
    }






    /**
     * Proceeds to the next player's turn in the game.
     */
    public void nextPlayer() throws RemoteException {
        model.getCurrentPlayer().reset(model.getCommonGoals());
        //checks if board is empty or tiles are "alone" on board
        if (model.getBoard().checkRefill())
            model.getBoard().fillBoard(model.getBag());

        //checks if current shelf is full
        if(!this.endPointGiven && this.model.getCurrentPlayer().getShelf().isFull()) {
            this.model.getCurrentPlayer().addPoints(END_GAME_POINT);
            this.endPointGiven = true;
            this.model.setLastTurn(true);
        }

        int indexCurrentPlayer = this.model.getPlayers().indexOf(this.model.getCurrentPlayer());

        // checks if game is over
        if(!this.model.isEnd()) {   // ending...
            if (model.isLastTurn() && indexCurrentPlayer == this.model.getPlayers().size() - 1) {
                this.model.setEnd(true);
                calculateWinner();
            } else {            // going to next Player
                switchCurrentPlayer(indexCurrentPlayer);
                // checking if the player is associated to a connected client

                this.model.getBoard().setBorderTiles();
                this.model.newTurn();
            }
        }
    }
    private void switchCurrentPlayer(int indexCurrentPlayer){
        int connectedPlayers = 0;
        for (int i = 0; i < this.model.getPlayers().size(); i++) {
            if (this.model.getPlayers().get(i).isConnected()) {
                connectedPlayers++;
            }
        }
        if(connectedPlayers == 1){
            //gestire il caso in cui rimane un solo giocatore connesso
        }
        if (indexCurrentPlayer == this.model.getPlayers().size() - 1) {
            for (int i = 0; i < this.model.getPlayers().size(); i++) {
                if (this.model.getPlayers().get(i).isConnected()) {
                    this.model.setCurrentPlayer(this.model.getPlayers().get(i));
                    return;
                }
            }
            //this.model.setCurrentPlayer(this.model.getPlayers().get(0));
        }else{
            for (int i = indexCurrentPlayer + 1; i < this.model.getPlayers().size(); i++) {
                if (this.model.getPlayers().get(i).isConnected()) {
                    this.model.setCurrentPlayer(this.model.getPlayers().get(i));
                    return;
                }
            }
            for (int i = 0; i < this.model.getPlayers().size(); i++) {
                if (this.model.getPlayers().get(i).isConnected() && i != indexCurrentPlayer) {
                    this.model.setCurrentPlayer(this.model.getPlayers().get(i));
                    return;
                }
            }
        }
    }

    /**
     * Sets the chosen column for the current player's turn.
     *
     * @param c the chosen column
     */
    public void setChosenColumn(int c) {
        //controllo sulla colonna
        if(c<0 || c>=SHELF_COLUMN)
            this.model.setErrorType(Warnings.INVALID_COLUMN);
        else{   //settaggio della colonna
            int emptyCells = this.model.getCurrentPlayer().getShelf().checkColumnEmptiness(c);
            if(this.model.getCurrentPlayer().getChosenTiles().size() > emptyCells){
                this.model.setErrorType(Warnings.INVALID_COLUMN);
            }else{
                this.model.setChosenColumnByPlayer(c);
                if (this.model.getCurrentPlayer().getChosenTiles().size()==1){
                    try {
                        dropTile(1);
                    } catch (RemoteException e) {
                        System.err.println("Error in dropTile(1)");
                        throw new RuntimeException(e);
                    }
                } else {
                    this.model.askOrder();
                }
            }

        }

    }

    /**
     * Checks if the input coordinates are correct and valid for the current player's turn.
     *
     * @param inputCoordinates the input coordinates to check
     */
    public void checkCorrectCoordinates(int[] inputCoordinates){
        for (int[] availableCoordinate : this.model.getAvailableTilesForCurrentPlayer()) {
            if (Arrays.equals(availableCoordinate, inputCoordinates)) {
                this.model.getCurrentPlayer().addChosenCoordinate(inputCoordinates);
                this.model.getCurrentPlayer().addChosenTile(this.model.getBoard().getTile(inputCoordinates[0], inputCoordinates[1]));
                this.model.popTileFromBoard(inputCoordinates);
                this.model.checkMaxNumberOfTilesChosen();
                return;
            }
        }
        this.model.setErrorType(Warnings.INVALID_TILE);
    }

    /**
     * Drops the tile at the specified position from the current player's chosen tiles and places it on the shelf.
     *
     * @param tilePosition the position of the tile to drop
     */
    public void dropTile(int tilePosition) throws RemoteException {
        if( tilePosition-1 < 0  ||  tilePosition > model.getCurrentPlayer().getChosenTiles().size() )
            this.model.setErrorType(Warnings.INVALID_ORDER);
        else {
            Tile chosenTile = model.getCurrentPlayer().getChosenTiles().remove(tilePosition - 1);
            int column = model.getCurrentPlayer().getChosenColumn();
            model.droppedTile(chosenTile, column);
            if (this.model.getCurrentPlayer().getChosenTiles().size()==1){
                Tile lastTile = this.model.getCurrentPlayer().getChosenTiles().remove(0);
                model.droppedTile(lastTile, column);
            }
            if(this.model.getCurrentPlayer().getChosenTiles().isEmpty() && !this.model.isEnd())
                nextPlayer();
        }
    }

    /**
     * Calculates the winner of the game and ends the game.
     */
    public void calculateWinner() {
        this.model.endGame();
    }


    /**
     * Reconnects a player with the specified nickname.
     *
     * @param nickname The nickname of the player to be reconnected.
     */
    public void reconnectedPlayer(String nickname){
        for (Player player : model.getPlayers()) {
            if (player.getNickname().equals(nickname)) {
                player.setConnected(true);
            }
        }
        for (ModelListener listener: model.getListener()){
            listener.playerReconnected(nickname);
        }
    }

    /**
     * Puts back the chosen tiles of the specified player on the board.
     * Used if the player disconnected while choosing tiles.
     *
     * @param player The player whose chosen tiles are to be put back.
     */

    private void putBackTiles(Player player){
        if(!player.getChosenTiles().isEmpty()){
            List<Tile> tiles = player.getChosenTiles();
            List<int[]> coordinates = player.getChosenCoordinates();
            Board board = model.getBoard();
            for(int i=0; i<tiles.size(); i++){
                board.putTile(tiles.get(i), coordinates.get(i));
            }
        }
    }

    /**
     * Handles the disconnection of a player.
     *
     * @param player The player who got disconnected.
     */

    public void disconnectedPlayer(Player player){
        player.setConnected(false);
        putBackTiles(player);
        if(player.equals(model.getCurrentPlayer())){
            try {
                nextPlayer();
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
        for (ModelListener listener: model.getListener()){
            listener.playerDisconnected(player.getNickname());
        }
    }

    /**
     * Adds a chat message to the game.
     *
     * @param sender  The nickname of the sender.
     * @param message The content of the message.
     */
    public void addChatMessage(String sender, String message){
        String receiver;
        String content;

        if (message.startsWith("@")) {
            int spaceIndex = -1;
            spaceIndex = message.indexOf(" ");
            if (spaceIndex != -1) {
                receiver = message.substring(1, spaceIndex);
                content = message.substring(spaceIndex + 1);
                for (Player player : this.model.getPlayers()) {
                    if (player.getNickname().equals(receiver)) {
                        this.model.newMessage(sender, receiver, content);
                        return;
                    }
                }
                this.model.setErrorType(Warnings.IVALID_RECEIVER, sender);
            } else {
               this.model.setErrorType(Warnings.INVALID_CHAT_MESSAGE, sender);   //@leo
            }
        } else {
            receiver = "all";
            content = message;
            this.model.newMessage(sender, receiver, content);
        }
    }

    /**
     * Checks if the game has already started.
     *
     * @return {@code true} if the game has already started, {@code false} otherwise.
     *
     */
    public boolean isGameAlreadystarted() {
            return gameAlreadystarted;
    }

}
