package it.polimi.ingsw.controller;

import it.polimi.ingsw.distributed.Client;
import it.polimi.ingsw.model.*;


import java.util.*;

import static it.polimi.ingsw.util.Costants.END_GAME_POINT;

public class GameController  {
    //attributo a Model per poterlo modificare
    private Game model;
    // due attributi che derivano da input dell'utente
    private final int numberPlayers;
    private Map<Player, Client> players;

    //dovremmmo spostare la logica di startGame() di GameModel nel Controller

    public GameController (Game model, Map<String, Client> playersNames, int numberPartecipants){
        this.model = model;
        this.players = new HashMap<>();
        this.numberPlayers = numberPartecipants;
        for(String s: playersNames.keySet()){
            this.players.put(new Player(s), playersNames.get(s) );
        }
        this.model.startGame(this.numberPlayers, this.players);
    }

    public Map<Player, Client> getPlayers() {
        return players;
    }
//crea il Model in base a numberPlayers e alla List di players

    public void nextPlayer(){
        model.getCurrentPlayer().reset(model.getCommonGoals());
        //controlla se c'è bisogno di riempire la board per il player dopo
        if (model.getBoard().checkRefill())
            model.getBoard().fillBoard(model.getBag());
        int indexCurrentPlayer = this.model.getPlayers().indexOf(this.model.getCurrentPlayer());
        // controlla se ha finito la shelf
        if (model.isLastTurn() && indexCurrentPlayer == this.model.getPlayers().size() - 1){
            calculateWinner();
        } else {
            if(this.model.getCurrentPlayer().getShelf().isFull()){
                this.model.getCurrentPlayer().addPoints(END_GAME_POINT);
                this.model.setLastTurn(true);
            }
            if (indexCurrentPlayer == this.model.getPlayers().size() - 1)
                this.model.setCurrentPlayer(this.model.getPlayers().get(0));
            else
                this.model.setCurrentPlayer(this.model.getPlayers().get(indexCurrentPlayer + 1));
        }
    }

    public void setChosenColumn(int c){
        //controllo sulla colonna
        //settaggio della colonna
    }
    // inizializza le tiles ai bordi prima di ogni scelta e fino alla fine delle selezioni come filtro di partenza si usano le border
    public void setBorderTiles(){
         this.model.getBoard().setBorderTiles(model.getBoard().getAvailableTiles());
    }
    // restituisce la colonna con massimo spazio (utile quando si chiedono le coordinate nela TUI per esempio nel caso di shelf con solo l ultima riga vuota in modo da far scegliere solo una tile)


    public void checkCorrectCoordinates(int[] inputCoordinates){
        for (int[] availableCoordinate : this.model.getBoard().getBorderTiles()) {
            if (Arrays.equals(availableCoordinate, inputCoordinates)) {
                this.model.getCurrentPlayer().addChosenCoordinate(inputCoordinates);
                this.model.getCurrentPlayer().addChosenTile(this.model.getBoard().popTile(inputCoordinates[0], inputCoordinates[1]));
                this.model.checkMaxNumberOfTilesChosen();
            }
        }
    }
    public void dropTiles(List<Tile> chosenTiles){
        model.getCurrentPlayer().getShelf().dropTiles(chosenTiles);
        nextPlayer();
    }

    public void calculateWinner(){
        this.model.endGame();
        this.model.findWinner();
    }

    public void setChosenTiles(List<Tile> tmp) {
        model.getCurrentPlayer().setChosenTiles(tmp);
    }



}
