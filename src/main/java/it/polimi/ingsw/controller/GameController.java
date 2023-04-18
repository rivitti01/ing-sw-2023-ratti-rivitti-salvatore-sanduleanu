package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.view.TextualUI;

import java.util.ArrayList;
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


    public void turnHandler(){  //sarebbe un pezzo di startGame() in model.Game
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

    }

}
