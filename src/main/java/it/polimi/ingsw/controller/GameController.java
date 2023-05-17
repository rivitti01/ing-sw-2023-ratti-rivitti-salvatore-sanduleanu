package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.util.Warnings;


import java.rmi.RemoteException;
import java.util.*;

import static it.polimi.ingsw.util.Costants.END_GAME_POINT;
import static it.polimi.ingsw.util.Costants.SHELF_COLUMN;

public class GameController  {
    //attributo a Model per poterlo modificare
    private Game model;
    private int numberPlayers;
    private List<Player> players;
    private boolean endPointGiven = false;

    public void setNumberPlayers(int numberPlayers) {
        this.numberPlayers = numberPlayers;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public int getNumberPlayers() {
        return numberPlayers;
    }

    public List<Player> getPlayers() {
        return players;
    }

    //crea il Model in base a numberPlayers e alla List di players
    public void initializeModel(){
        model.startGame(this.numberPlayers, this.players);
        model.setStart(true);
    }

    public boolean setPlayerNickname(String s){
        for (int i = 0; i < this.players.size(); i++) {
            if (this.players.get(i).getNickname().equals(s)) return false;
        }
        this.players.add(new Player(s));
        return true;
    }

    public GameController (Game model){
        this.model = model;
        this.players = new ArrayList<>();
        this.numberPlayers = 0;
    }
    public void nextPlayer() throws RemoteException {
        model.getCurrentPlayer().reset(model.getCommonGoals());
        //checks if board is empty or tiles are "alone" on board
        if (model.getBoard().checkRefill())
            model.getBoard().fillBoard(model.getBag());

        if(!this.endPointGiven && this.model.getCurrentPlayer().getShelf().isFull()) {  // checks if shelf of currentPlayer us full
            this.model.getCurrentPlayer().addPoints(END_GAME_POINT);                    // takes END_GAME_POINT
            this.endPointGiven = true;
            this.model.setLastTurn(true);                                                //last turn for each player...
        }

        int indexCurrentPlayer = this.model.getPlayers().indexOf(this.model.getCurrentPlayer());
        // checks if game is over
        if(!this.model.isEnd()) {       // check if the game has ended
            if (model.isLastTurn() && indexCurrentPlayer == this.model.getPlayers().size() - 1) {
                this.model.setEnd(true);
                calculateWinner();
            } else { // going to next Player
                if (indexCurrentPlayer == this.model.getPlayers().size() - 1)
                    this.model.setCurrentPlayer(this.model.getPlayers().get(0));
                else
                    this.model.setCurrentPlayer(this.model.getPlayers().get(indexCurrentPlayer + 1));
                this.model.getBoard().setBorderTiles();
                this.model.newTurn();
            }
        }
    }
    public void setChosenColumn(int c){
        //controllo sulla colonna
        int emptyCells = this.model.getCurrentPlayer().getShelf().checkColumnEmptiness(c);
        if(c<0 || c>=SHELF_COLUMN || this.model.getCurrentPlayer().getChosenTiles().size() > emptyCells)
            this.model.setErrorType(Warnings.INVALID_COLUMN);
        else{   //settaggio della colonna
            this.model.setChosenColumnByPlayer(c);
        }

    }
    public void checkCorrectCoordinates(int[] inputCoordinates){
        for (int[] availableCoordinate : this.model.getAvailableTilesForCurrentPlayer()) {
            if (Arrays.equals(availableCoordinate, inputCoordinates)) {
                this.model.getCurrentPlayer().addChosenCoordinate(inputCoordinates);
                this.model.getCurrentPlayer().addChosenTile(this.model.getBoard().getTile(inputCoordinates[0], inputCoordinates[1]));
                Tile tmp = this.model.popTileFromBoard(inputCoordinates);
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
            Tile chosenTile = model.getCurrentPlayer().getChosenTiles().get(tilePosition - 1);
            int column = model.getCurrentPlayer().getChosenColumn();
            model.droppedTile(chosenTile, column);
            if(this.model.getCurrentPlayer().getChosenTiles().isEmpty() && !this.model.isEnd())
                nextPlayer();
        }
    }
    public void calculateWinner() throws RemoteException {
        this.model.endGame();
        //this.model.findWinner();  reasoning's in the gameModel
    }
    public void setChosenTiles(List<Tile> tmp) {
        model.getCurrentPlayer().setChosenTiles(tmp);
    }
    

}
