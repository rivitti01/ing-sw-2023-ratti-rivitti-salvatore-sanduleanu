package it.polimi.ingsw.model;


import it.polimi.ingsw.view.TextualUI;

import javax.swing.event.ChangeListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static it.polimi.ingsw.Costants.*;

public class Game implements PropertyChangeListener, Serializable {
    private int numberPartecipants;
    private List<Player> players;
    private CommonGoalCard[] commonGoals;
    private Board board;
    private Bag bag;
    private Player currentPlayer;
    //per capire se si è completata una shelf o meno (l'ho messo come attributo perchè veniva usato in startGame()
    private boolean lastTurn;
    private boolean start = false;
    private boolean end=false;
    //private PropertyChangeListener listener;
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
        DeckPersonal deckPersonal = new DeckPersonal();
        for (int i=0; i<this.players.size(); i++)
            this.players.get(i).setPrivateCard(deckPersonal.popPersonalCard());
        board.addPropertyChangeListener(this);
        for (Player p: this.players) {
            p.addPropertyChangeListener(this);
        }
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
        propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this, "seat", null, this.players.get(0)));
        //PropertyChangeEvent evt = new PropertyChangeEvent(this, "seat", null, this.players.get(0));
        //this.listener.propertyChange(evt);
    }

    public boolean isLastTurn() {
        return this.lastTurn;
    }

    public void setLastTurn(boolean lastTurn) {
        this.lastTurn = lastTurn;
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
            chosenCoordinates1 = this.currentPlayer.getChosenCoordinates().get(0);
        }catch (IndexOutOfBoundsException e1){
            chosenCoordinates1 = null;
        }
        try{
            chosenCoordinates2 = this.currentPlayer.getChosenCoordinates().get(1);
        }catch (IndexOutOfBoundsException e2){
            chosenCoordinates2 = null;
        }
        return this.board.filterAvailableTiles(chosenCoordinates1, chosenCoordinates2, this.board.getBorderTiles());
    }

    public void setCurrentPlayer(Player currentPlayer) {
        Player oldPlayer = this.currentPlayer;
        this.currentPlayer = currentPlayer;
        if (!lastTurn) {
            propertyChangeSupport.firePropertyChange("nextPlayer", oldPlayer, this.currentPlayer);
        } else {
            propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this, "Last Turn", oldPlayer, this.currentPlayer));
        }
    }

    public void setStart(boolean s){
        boolean old = this.start;
        this.start = s;
        propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this, "start", old, this.start));
    }

    public void setEnd(boolean e){
        boolean old = this.end;
        this.end = e;
        propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this, "end", old, this.end));
    }

    public void endGame(){
        setEnd(true);
        for(Player p : this.players) {
            p.addPoints(p.getShelf().checkAdjacents());
            p.addPoints(p.checkPersonalPoints());
            propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this, "playerName", null, p.getNickname()));
            propertyChangeSupport.firePropertyChange((new PropertyChangeEvent(this, "playerPoints", null, p.getPoints())));
            //punti dai gruppi sulla shelf aggiunti qui
            //punti personalGoalCard aggiunti qui
            //punti delle commonGoals gia eventualmente aggiunti
            //punto della fine della partita gia assegnato
        }

    }
    public int getNumberPartecipants(){
        return numberPartecipants;
    }

    public void findWinner(){
        Player tempWinner = players.get(0);
        for(int i=1; i<players.size(); i++){
            if(players.get(i).getPoints() >= players.get(i-1).getPoints())
                tempWinner = players.get(i);
        }
        propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this, "winner", null, tempWinner));
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.propertyChangeSupport.removePropertyChangeListener(listener);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this, evt.getPropertyName(),evt.getOldValue(),evt.getNewValue()));
    }
}
