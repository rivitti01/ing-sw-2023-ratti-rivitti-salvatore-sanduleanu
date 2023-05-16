package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.util.Warnings;


import java.util.*;

import static it.polimi.ingsw.util.Costants.END_GAME_POINT;
import static it.polimi.ingsw.util.Costants.SHELF_COLUMN;

public class GameController  {
    //attributo a Model per poterlo modificare
    private Game model;
    private int numberPlayers;
    private List<Player> players;

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
    public void nextPlayer(){
        model.getCurrentPlayer().reset(model.getCommonGoals());
        //controlla se c'è bisogno di riempire la board per il player dopo
        if (model.getBoard().checkRefill())
            model.getBoard().fillBoard(model.getBag());

        int indexCurrentPlayer = this.model.getPlayers().indexOf(this.model.getCurrentPlayer());

        // controlla se e finito il game
        if (model.isLastTurn() && indexCurrentPlayer == this.model.getPlayers().size() - 1){
            calculateWinner();
        } else { //altrimenti partita e ancora in gioco
            if(this.model.getCurrentPlayer().getShelf().isFull()){  //controlla se shelf e full
                this.model.getCurrentPlayer().addPoints(END_GAME_POINT);  //prende ultimo punto
                this.model.setLastTurn(true);       //setto gioco in endgame
            }

            //passaggio al prossimo player
            if (indexCurrentPlayer == this.model.getPlayers().size() - 1)
                this.model.setCurrentPlayer(this.model.getPlayers().get(0));
            else
                this.model.setCurrentPlayer(this.model.getPlayers().get(indexCurrentPlayer + 1));
            this.model.getBoard().setBorderTiles();

            this.model.newTurn();
        }
    }
    public void setChosenColumn(int c){
        //controllo sulla colonna
        if(c<0 || c>SHELF_COLUMN)
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
    public void dropTile(int tilePosition){
        if( tilePosition-1 < 0  ||  tilePosition-1 > model.getCurrentPlayer().getChosenTiles().size() )
            this.model.setErrorType(Warnings.INVALID_ORDER);
        else {
            Tile chosenTile = model.getCurrentPlayer().getChosenTiles().get(tilePosition - 1);
            int column = model.getCurrentPlayer().getChosenColumn();
            model.droppedTile(chosenTile, column);
            if(this.model.getCurrentPlayer().getChosenTiles().isEmpty())
                nextPlayer();
        }
    }
    public void calculateWinner(){
        this.model.endGame();
        this.model.findWinner();
    }
    public void setChosenTiles(List<Tile> tmp) {
        model.getCurrentPlayer().setChosenTiles(tmp);
    }
    

}
