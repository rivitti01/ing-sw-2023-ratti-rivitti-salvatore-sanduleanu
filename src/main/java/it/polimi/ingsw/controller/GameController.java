package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.*;


import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static it.polimi.ingsw.Costants.END_GAME_POINT;
import static it.polimi.ingsw.Costants.MAX_TILES_PER_TURN;

public class GameController  {
    //attributo a Model per poterlo modificare
    private Game model;
    // due attributi che derivano da input dell'utente
    int numberPlayers;
    private List<Player> players;
    //dovremmmo spostare la logica di startGame() di GameModel nel Controller

    public GameController (Game model){
        this.model = model;
        this.players = new ArrayList<>();
        this.numberPlayers = 0;
    }

    public void setPlayerNumber(int n){
        this.numberPlayers = n;
    }
    public boolean setPlayerNickname(String s){
        for (Player p:players) {
            if (p.getNickname().equals(s)) return false;
        }
        this.players.add(new Player(s));
        return true;
    }

    //crea il Model in base a numberPlayers e alla List di players
    public void initializeModel(){
        model.setGame(this.numberPlayers, this.players);
        model.setStart(true);
    }
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
    // inizializza le tiles ai bordi prima di ogni scelta e fino alla fine delle selezioni come filtro di partenza si usano le border
    public void setBorderTiles(){
         this.model.getBoard().setBorderTiles(model.getBoard().getAvailableTiles());
    }
    // restituisce la colonna con massimo spazio (utile quando si chiedono le coordinate nela TUI per esempio nel caso di shelf con solo l ultima riga vuota in modo da far scegliere solo una tile)


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
    public void dropTiles(List<Tile> choosenTiles, int column){
        model.getCurrentPlayer().getShelf().dropTiles(choosenTiles,column);
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
