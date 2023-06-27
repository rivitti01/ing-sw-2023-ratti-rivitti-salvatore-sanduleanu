package it.polimi.ingsw.controller;

import it.polimi.ingsw.distributed.socket.ServerHandler;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.util.ModelListener;
import it.polimi.ingsw.util.Warnings;


import java.rmi.RemoteException;
import java.util.*;

import static it.polimi.ingsw.util.Costants.END_GAME_POINT;
import static it.polimi.ingsw.util.Costants.SHELF_COLUMN;

public class GameController  {
    //attributo a Model per poterlo modificare
    private Game model;
    private boolean gameAlreadystarted = false;
    private int numberPlayers;
    private List<Player> players;
    private boolean endPointGiven = false;


    public GameController (Game model){
        this.model = model;
        this.players = new ArrayList<>();
        this.numberPlayers = 0;
    }

    public void setNumberPlayers(int numberPlayers) {
        this.numberPlayers = numberPlayers;
    }

    public int getNumberPlayers() {
        return numberPlayers;
    }

    public List<Player> getPlayers() {
        return players;
    }
    public void checkGameInitialization(){
        if(!gameAlreadystarted) {
            if (this.players.size() == this.numberPlayers) {
                initializeModel();
                gameAlreadystarted = true;
            }
        }
    }

    //crea il Model in base a numberPlayers e alla List di players
    public void initializeModel(){
        model.startGame(this.numberPlayers, this.players);
        model.setStart(true);
        this.gameAlreadystarted = true;
    }

    public boolean setPlayerNickname(String s){
        for (Player player : this.players) {
            if (player.getNickname().equals(s)) return false;
        }
        this.players.add(new Player(s));
        return true;
    }
    public boolean checkingExistingNickname(String nickname){
        for (Player player : this.model.getPlayers()) {
            if (player.getNickname().equals(nickname) && !player.isConnected()) return true;
        }
        return false;
    }
    public boolean playerOffline(){//controlla se c'è almeno un player offline
        for (Player player : this.model.getPlayers()) {
            if (!player.isConnected()) return true;
        }
        return false;
    }


    /*
     * I need this method to check if a client is reconnecting to the game
     * or is connecting for the first time.
     * ( setPlayerNickname tells the server only if the nickname was set correctly )
     */
    public boolean checkReconnection(String nickname) {
        for (Player player : this.model.getPlayers()) {
            if (player.getNickname().equals(nickname))
                return true;
        }
        return false;
    }



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
    public void setChosenColumn(int c){
        //controllo sulla colonna
        if(c<0 || c>=SHELF_COLUMN)
            this.model.setErrorType(Warnings.INVALID_COLUMN);
        else{   //settaggio della colonna
            int emptyCells = this.model.getCurrentPlayer().getShelf().checkColumnEmptiness(c);
            if(this.model.getCurrentPlayer().getChosenTiles().size() > emptyCells){
                this.model.setErrorType(Warnings.INVALID_COLUMN);
            }else
                this.model.setChosenColumnByPlayer(c);
        }

    }
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
    public void dropTile(int tilePosition) throws RemoteException {
        if( tilePosition-1 < 0  ||  tilePosition > model.getCurrentPlayer().getChosenTiles().size() )
            this.model.setErrorType(Warnings.INVALID_ORDER);
        else {
            Tile chosenTile = model.getCurrentPlayer().getChosenTiles().remove(tilePosition - 1);
            int column = model.getCurrentPlayer().getChosenColumn();
            model.droppedTile(chosenTile, column);
            if(this.model.getCurrentPlayer().getChosenTiles().isEmpty() && !this.model.isEnd())
                nextPlayer();
        }
    }
    public void calculateWinner() {
        this.model.endGame();
        //this.model.findWinner();
    }
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
        /*if(model.getListener().size() == 1){
            //Player lastPlayer = model.getPlayers().stream().filter(Player::isConnected).findFirst().get();
            for (Player p : model.getPlayers()){
                if (p.isConnected()){
                    model.getListener().get(model.getPlayers().indexOf(p)).onePlayerLeft(p,10000);
                    break;
                }
            }
        }*/
    }

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
                //this.model.newMessage(sender, receiver, content);
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

    public boolean isGameAlreadystarted() {
        return gameAlreadystarted;
    }

    public void setGameAlreadystarted(boolean gameAlreadystarted) {
        this.gameAlreadystarted = gameAlreadystarted;
    }
}
