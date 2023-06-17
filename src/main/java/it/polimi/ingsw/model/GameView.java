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
    private final String[] commonName;
    private final List<Tile> chosenTiles;
    private final ChatView chatView;
    private final int points;
    private final List<Integer> commonGoal1;
    private final List<Integer> commonGoal2;



    public GameView(Game model, Player p){
        this.chosenTiles = model.getCurrentPlayer().getChosenTiles();
        this.playersShelves = new LinkedHashMap<>();
        this.commons = new String[2];
        this.commonName = new String[2];
        this.board = model.getBoard();
        this.nickName = model.getCurrentPlayer().getNickname();
        for (Player player: model.getPlayers()){
            this.playersShelves.put(player.getNickname(), player.getShelf());
        }
        this.personal = p.getPersonalGoalCard();
        for(int i = 0 ; i<2 ; i++){
            commons[i] = model.getCommonGoals()[i].getDescription();
            commonName[i] = model.getCommonGoals()[i].getName();
        }
        chatView = new ChatView(model, p);
        points = p.getPoints() + p.getAdjacencyPoints() + p.getPersonalGoalPoints();
        commonGoal1 = new ArrayList<>();
        commonGoal2 =  new ArrayList<>();
        commonGoal1.addAll(model.getCommonGoals()[0].getScores());
        commonGoal2.addAll(model.getCommonGoals()[1].getScores());
    }

    public int getPoints() {
        return points;
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
    public ChatView getChatView(){
        return this.chatView;
    }

    public String[] getNameGoals(){return this.commonName;}

    public List<Integer> getCommonGoal1() {
        return commonGoal1;
    }


    public List<Integer> getCommonGoal2() {
        return commonGoal2;
    }
}
