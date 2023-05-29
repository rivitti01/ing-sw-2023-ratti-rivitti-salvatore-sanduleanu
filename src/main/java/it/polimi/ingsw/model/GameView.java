package it.polimi.ingsw.model;


import java.io.Serializable;
import java.util.*;

public class GameView  implements Serializable {

    static final long serialVersionUID = 1L;
    private final Board board;
    private final String nickName;
    private final Map<String, Shelf> playersShelves;
    private final PersonalGoalCard personal;
    private final String[] commons;
    private final List<Tile> chosenTiles;
    private final ChatView chat;


    public GameView(Game model, Player p){
        this.chat = new ChatView(model);
        this.chosenTiles = model.getCurrentPlayer().getChosenTiles();
        this.playersShelves = new HashMap<>();
        this.commons = new String[2];
        this.board = model.getBoard();
        this.nickName = model.getCurrentPlayer().getNickname();
        for (Player player: model.getPlayers()){
            this.playersShelves.put(player.getNickname(), player.getShelf());
        }
        this.personal = p.getPersonalGoalCard();
        for(int i = 0 ; i<2 ; i++){
            commons[i] = model.getCommonGoals()[i].getDescription();
        }
    }


    public String getNickName() {
        return this.nickName;
    }

    public Map<String, Shelf> getPlayersShelves() {
        return this.playersShelves;
    }

    public List<Tile> getChosenTiles() {
        return chosenTiles;
    }

    public PersonalGoalCard getPersonal() {
        return personal;
    }
    public Board getBoard(){
        return this.board;
    }
    public String[] getCommonGoals(){return this.commons;}
    public ChatView getChat(){
        return this.chat;
    }


}
