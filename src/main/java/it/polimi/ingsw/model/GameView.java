package it.polimi.ingsw.model;


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;

public class GameView  implements PropertyChangeListener {
    private Game model;
    PropertyChangeSupport propertyChangeSupport;

    public GameView(Game model){
        this.model = model;
        propertyChangeSupport = new PropertyChangeSupport(this);
        model.addPropertyChangeListener(this);
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
    public String getCurrentPlayer(){
        return this.model.getCurrentPlayer().getNickname();
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
       if(evt.getPropertyName().equals("seat"))
           propertyChangeSupport.firePropertyChange(evt.getPropertyName(),evt.getOldValue(),evt.getNewValue());

    }
}
