package it.polimi.ingsw.model;


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.List;

import static it.polimi.ingsw.Costants.MAX_TILES_PER_TURN;

public class GameView implements Serializable {
    static final long serialVersionUID = 1L;
    private final Game model;
    //PropertyChangeSupport propertyChangeSupport;

    public GameView(Game model){
        this.model = model;
        //propertyChangeSupport = new PropertyChangeSupport(this);
        //this.model.addPropertyChangeListener(this);
    }
    public boolean isLastTurn(){
        return this.model.isLastTurn();
    }
    public List<Player> getPlayers(){
        return this.model.getPlayers();
    }
    public Board getBoard(){
        return this.model.getBoard();
    }
    public CommonGoalCard[] getCommonGoalCards(){
        return this.model.getCommonGoals();
    }
    public Player getCurrentPlayer(){ return this.model.getCurrentPlayer();}
    public List<Tile> getCurrentPlayerChosenTiles(){return this.model.getCurrentPlayer().getChosenTiles();}
    public String getCurrentPlayerNickname(){
        return this.model.getCurrentPlayer().getNickname();
    }
    public Shelf getCurrentPlayerShelf(){return this.model.getCurrentPlayer().getShelf();}
    public List<int[]> getCurrentPlayerChosenCoordinates(){return this.model.getCurrentPlayer().getChosenCoordinates();}
    public List<int[]> getAvailableTilesForCurrentPlayer(){return this.model.getAvailableTilesForCurrentPlayer();}
    public PersonalGoalCard getCurrentPlayerPersonalCard(){return this.model.getCurrentPlayer().getPersonalGoalCard();}
    public boolean getCurrentPlayerSeat(){return this.model.getCurrentPlayer().getSeat();}
    public int getMaxColumnSpace(){
        int flag = this.model.getCurrentPlayer().getShelf().getMaxColumnSpace();
        return Math.min(flag, MAX_TILES_PER_TURN);
    }
    public CommonGoalCard[] getCommonGoals(){return this.model.getCommonGoals();}
    /*
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
           propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this, evt.getPropertyName(),evt.getOldValue(),evt.getNewValue()));
    }
     */
}
