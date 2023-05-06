package it.polimi.ingsw.util;

import it.polimi.ingsw.distributed.Client;
import it.polimi.ingsw.model.Player;

public interface ModelListener extends java.util.EventListener {

        /**
         * This method gets called when a bound property is changed.
         *
         * @param evt A PropertyChangeEvent object describing the event source
         *            and the property that has changed.
         */

        void printGame();

        void chosenTileError(Client c);

        void chosenColumnError(Client c);

        void newTurn(Player currentPlayer);



}
