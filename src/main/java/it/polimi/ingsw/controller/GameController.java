package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.view.TextualUI;

public class GameController  {
    private Game model;

    public GameController (Game g){
        this.model = g;
    }

    public void setPlayerNumber(int n){
        this.model.setNumberPartecipants(n);
    }

    public void setPlayerNickname(String s){
        this.model.setPlayers(s);
    }

    public void setFirst(){
        model.setFirstPlayer();
    }


}
