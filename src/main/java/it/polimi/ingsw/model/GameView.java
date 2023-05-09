package it.polimi.ingsw.model;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GameView  implements Serializable {

    static final long serialVersionUID = 1L;
    private final Board board;
    private final String nickName;
    private final List<Shelf> playersShelves; //TODO: fare una mappa <String, Shelf> per stampare le shelf associate al loro nome
    private final PersonalGoalCard personal;
    private final String[] commons;

    public GameView(Game model, Player p){
        this.playersShelves = new ArrayList<>();
        this.commons = new String[2];
        this.board = model.getBoard();
        this.nickName = p.getNickname();
        for (Player player: model.getPlayers()){
            this.playersShelves.add(player.getShelf());
        }
        this.personal = p.getPersonalGoalCard();
        for(int i = 0 ; i<2 ; i++){
            commons[i] = model.getCommonGoals()[i].getDescription();
        }
    }

    public String getNickName() {
        return nickName;
    }

    public List<Shelf> getPlayersShelves() {
        return this.playersShelves;
    }

    public PersonalGoalCard getPersonal() {
        return personal;
    }

    public Board getBoard(){
        return this.board;
    }
    public String[] getCommonGoals(){return this.commons;}


}
