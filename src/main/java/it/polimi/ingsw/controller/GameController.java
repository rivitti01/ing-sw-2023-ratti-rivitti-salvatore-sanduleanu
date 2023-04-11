package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.util.Observable_1;
import it.polimi.ingsw.util.Observer_1;
import it.polimi.ingsw.view.TextualUI;

import java.util.List;

public class GameController implements Observer_1 {
    private Game model;

    public GameController (Game g){
        this.model = g;
    }


    @Override
    public void update(Observable_1 o, Object arg) {

    }
}
