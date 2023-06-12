package it.polimi.ingsw.model.Algorythms;

import it.polimi.ingsw.model.Shelf;

public interface CardStrategy {
    public boolean algorythm(Shelf myShelf);
    public String toString();
    public String getCardName();
}
