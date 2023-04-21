package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.*;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        currentPlayer = model.getCurrentPlayer();
    }
    public void nextPlayer(){
       currentPlayer.reset(model.getCommonGoals());
       int indexCurrentPlayer = this.model.getPlayers().indexOf(this.model.getCurrentPlayer());
       if(indexCurrentPlayer == this.model.getPlayers().size()-1)
           this.model.setCurrentPlayer(this.model.getPlayers().get(0));
       else
           this.model.setCurrentPlayer(this.model.getPlayers().get(indexCurrentPlayer + 1));
       currentPlayer = model.getCurrentPlayer();
    }
    // inizializza le tiles ai bordi prima di ogni scelta e fino alla fine delle selezioni come filtro di partenza si usano le border
    public void setBorderTiles(){
         this.model.getBoard().setBorderTiles(model.getBoard().getAvailableTiles());
    }

    public boolean checkCorrectCoordinates(int[] inputCoordinates, List<int[]> borderTiles){
        for (int[] availableCoordinate : borderTiles) {
            if (Arrays.equals(availableCoordinate, inputCoordinates))
                return true;
        }
        return false;
    }
    public void addChosenCoordinate(int[] coordinates){
        this.model.getCurrentPlayer().addChosenCoordinate(coordinates);
    }
    public void addChosenTile(int[] coordinates){
        this.model.getCurrentPlayer().addChosenTile(this.model.getBoard().popTile(coordinates[0], coordinates[1]));
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
