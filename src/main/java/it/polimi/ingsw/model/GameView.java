package it.polimi.ingsw.model;


import it.polimi.ingsw.distributed.Client;

import java.io.Serializable;
import java.util.List;

import static it.polimi.ingsw.Costants.MAX_TILES_PER_TURN;

public class GameView  implements Serializable {

    static final long serialVersionUID = 1L;
    private final Board board;
    private final String nickName;
    private final Shelf playerShelf;
    private final PersonalGoalCard personal;
    private final String[] commons;

    public GameView(Game model, Player p){
        this.commons = new String[2];
        this.board = model.getBoard();
        this.nickName = p.getNickname();
        this.playerShelf = p.getShelf();
        this.personal = p.getPersonalGoalCard();
        for(int i = 0 ; i<2 ; i++){
            commons[i] = model.getCommonGoals()[i].getDescription();
        }
    }

    public String getNickName() {
        return nickName;
    }

    public Shelf getPlayerShelf() {
        return playerShelf;
    }

    public PersonalGoalCard getPersonal() {
        return personal;
    }

    public Board getBoard(){
        return this.board;
    }
    public String[] getCommonGoals(){return this.commons;}


}
