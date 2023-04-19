package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.view.TextualUI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static it.polimi.ingsw.Costants.END_GAME_POINT;

public class GameController  {
    //attributo a Model per poterlo modificare
    private Game model;
    // due attributi che derivano dall'input dell'utente
    int numberPlayers;
    List<Player> players;
    //dovremmmo spostare la logica di startGame() di GameModel nel Controller
    Player currentPlayer;

    public GameController (Game model){
        this.model = model;
        this.players = new ArrayList<>();
        this.numberPlayers = 0;
    }
    public Player getCurrentPlayer() {
        return currentPlayer;
    }
    public void setPlayerNumber(int n){
        this.numberPlayers = n;
    }
    public void setPlayerNickname(String s){
        this.players.add(new Player(s));
    }

    //crea il Model in base a numberPlayers e alla List di players
    public void initializeModel(){
        model.setGame(this.numberPlayers, this.players);
    }
    public void nextPlayer(){
       int indexCurrentPlayer = this.model.getPlayers().indexOf(this.model.getCurrentPlayer());
       if(indexCurrentPlayer == this.model.getPlayers().size()-1)
           this.model.setCurrentPlayer(this.model.getPlayers().get(0));
       else
           this.model.setCurrentPlayer(this.model.getPlayers().get(indexCurrentPlayer + 1));
    }
    public boolean checkCorrectCoordinates(int[] inputCoordinates){
        List<int[]> availableCoordinates = model.getAvailableTilesForCurrentPlayer();
        for(int i=0; i<availableCoordinates.size(); i++){
            if(Arrays.equals(availableCoordinates.get(i), inputCoordinates))
                return true;
        }
        return false;
    }
    public void addChosenTile(int[] coordinates){
        this.model.getCurrentPlayer().addChosenTile(coordinates);
    }

 /*   public void turnHandler(){  //sarebbe un pezzo di startGame() in model.Game
        int index = 0;
        this.currentPlayer = players.get(index);

        while(!this.model.isLastTurn()){
            // scelta delle Tiles di currentPlayer
            // scelta della colonna
            // scelta ordine delle tiles
            // inserimento

            if(this.model.getBoard().checkRefill())
                this.model.getBoard().fillBoard(this.model.getBag());

            if (this.currentPlayer.getShelf().isFull()){
                this.currentPlayer.addPoints(END_GAME_POINT);
                break;
            }
            index = (index + 1) % players.size();
            this.currentPlayer = players.get(index);
        }

        while(!players.iterator().next().getSeat()){  // ultimi turni finche il giocatore dopo e quello con la sedia
            //currentPlayer.play(board, commonGoals);
            // scelta delle Tiles di currentPlayer
            // scelta della colonna
            // scelta ordine delle tiles
            // inserimento
            currentPlayer = players.iterator().next();
        }

    } */

}
