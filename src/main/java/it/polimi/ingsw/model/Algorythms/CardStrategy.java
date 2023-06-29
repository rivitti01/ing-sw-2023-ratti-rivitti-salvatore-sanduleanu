package it.polimi.ingsw.model.Algorythms;

import it.polimi.ingsw.model.Shelf;

public interface CardStrategy {
    /**
     * The algorithm for evaluating the card strategy.
     *
     * @param myShelf the shelf to evaluate
     * @return true if the algorithm condition is met, false otherwise
     */
     boolean algorithm(Shelf myShelf);

    /**
     * Returns a string representation of the card strategy.
     *
     * @return a string describing the card strategy
     */
     String toString();

    /**
     * Retrieves the name of the card.
     *
     * @return the name of the card
     */
     String getCardName();
}
