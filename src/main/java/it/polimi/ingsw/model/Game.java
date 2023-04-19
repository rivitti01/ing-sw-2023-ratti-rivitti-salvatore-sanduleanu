package it.polimi.ingsw.model;


import it.polimi.ingsw.view.TextualUI;

import javax.swing.event.ChangeListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static it.polimi.ingsw.Costants.*;

public class Game  {
    private int numberPartecipants;
    private List<Player> players;
    private CommonGoalCard[] commonGoals;
    private Board board;
    private Bag bag;
    private Player currentPlayer;
    //per capire se si è completata una shelf o meno (l'ho messo come attributo perchè veniva usato in startGame()
    private boolean lastTurn;

    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);



    public void setGame(int numberParticipants, List<Player> players){
        this.numberPartecipants = numberParticipants;
        this.bag = new Bag();
        this.board = new Board(this.numberPartecipants);
        this.board.fillBoard(this.bag);
        this.players = players;
        setFirstPlayer();
        this.currentPlayer = this.players.get(0);
        this.commonGoals = new CommonGoalCard[2];
        DeckCommon deckCommon = new DeckCommon();
        for(int i=0; i<COMMON_CARDS_PER_GAME; i++)
            commonGoals[i] = new CommonGoalCard(this.numberPartecipants, deckCommon);
    }



    //Getters and Setters
    public void setFirstPlayer(){
        Random random = new Random();
        int tmp = random.nextInt(this.numberPartecipants);
        List<Player> tempList = new ArrayList<>();
        while(players.size() != 0){
            if (tmp < players.size()){
                tempList.add(players.remove(tmp));
            }else {
                tempList.add(players.remove(0));
            }
        }
        players = tempList;
        players.get(0).setSeat(true);
        propertyChangeSupport.firePropertyChange("seat", null, this.players.get(0).getNickname());

    }
    public boolean isLastTurn() {
        return this.lastTurn;
    }
    public List<Player> getPlayers() {
        return players;
    }
    public Board getBoard() {
        return board;
    }
    public Bag getBag() {
        return bag;
    }
    public CommonGoalCard[] getCommonGoals() {
        return commonGoals;
    }
    public Player getCurrentPlayer() {
        return currentPlayer;
    }
    public List<int[]> getAvailableTilesForCurrentPlayer(){
        int[]  chosenCoordinates1;
        int[]  chosenCoordinates2;
        try {
            chosenCoordinates1 = this.currentPlayer.getChosenTiles().get(0);
        }catch (IndexOutOfBoundsException e1){
            chosenCoordinates1 = null;
        }
        try{
            chosenCoordinates2 = this.currentPlayer.getChosenTiles().get(1);
        }catch (IndexOutOfBoundsException e2){
            chosenCoordinates2 = null;
        }
        return this.board.filterAvailableTiles(chosenCoordinates1, chosenCoordinates2, this.board.getAvailableTiles());
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

 /*   public void startGame(){
        int index = 0;
        currentPlayer = players.get(index);

        while(!this.lastTurn){  //turni finché nessuno ha riempito una shelf
            System.out.println("Gioca: " + currentPlayer.nickname);
            // scelta delle Tiles di ogni player da passare a getTile
            // scelta della colonna
            // scelta ordine delle tiles
            currentPlayer.play(this.board, this.commonGoals);

            if(this.board.checkRefill())
                this.board.fillBoard(this.bag);

            if (currentPlayer.getShelf().isFull()){
                    currentPlayer.addPoints(END_GAME_POINT);
                    lastTurn = true;
                }
            index = ( index + 1 ) % players.size();
            currentPlayer = players.get(index);
        }

        while(!players.iterator().next().getSeat()){  // ultimi turni finche il giocatore dopo e quello con la sedia
            currentPlayer.play(board, commonGoals);
            currentPlayer = players.iterator().next();
        }
    }

  */

    public void endGame(){
        for(Player p : this.players) {
            p.addPoints(p.getShelf().checkAdjacents());
            p.addPoints(p.checkPersonalPoints());
            //punti dai gruppi sulla shelf aggiunti qui
            //punti personalGoalCard aggiunti qui
            //punti delle commonGoals gia eventualmente aggiunti ai punteggi in player.play()
            //punto della fine della partita e stato gia assegnato in game.startGame()
        }

    }
    public Player findWinner(){
        Player tempWinner = players.get(0);
        for(int i=1; i<players.size(); i++){
            if(players.get(i).getPoints() >= players.get(i-1).getPoints())
                tempWinner = players.get(i);
        }
         return tempWinner;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.propertyChangeSupport.addPropertyChangeListener(listener);
    }
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.propertyChangeSupport.removePropertyChangeListener(listener);
    }

}
