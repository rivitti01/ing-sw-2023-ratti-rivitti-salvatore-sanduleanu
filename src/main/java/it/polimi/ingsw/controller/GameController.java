package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.util.Warnings;


import java.util.*;

import static it.polimi.ingsw.util.Costants.END_GAME_POINT;
import static it.polimi.ingsw.util.Costants.SHELF_COLUMN;

public class GameController  {
    //attributo a Model per poterlo modificare
    private Game model;

    public GameController (Game model){
        this.model = model;
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
        List<int[]> temp = this.model.getAvailableTilesForCurrentPlayer();
        for (int[] availableCoordinate : temp) {
            if (Arrays.equals(availableCoordinate, inputCoordinates)) {
                this.model.getCurrentPlayer().addChosenCoordinate(inputCoordinates);
                this.model.getCurrentPlayer().addChosenTile(this.model.popTileFromBoard(inputCoordinates));
                this.model.checkMaxNumberOfTilesChosen();
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
