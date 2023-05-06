package it.polimi.ingsw.model.Algorythms;

import it.polimi.ingsw.model.Shelf;

import java.io.Serializable;

public interface CardStrategy extends Serializable {
    public boolean algorythm(Shelf myShelf);
    public String toString();
}
