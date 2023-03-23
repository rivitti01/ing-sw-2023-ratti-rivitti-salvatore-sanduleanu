package it.polimi.ingsw.model;

import it.polimi.ingsw.model.Board.Board;
import static it.polimi.ingsw.Costants.*;
import java.util.List;

public class Player {
    private String nickname;
    private Shelf shelf;
    private Boolean seat;
    private Boolean playingTurn;
    private List<Tile> chosenTiles;
    private PersonalGoalCard privateCard;
    private List<Integer> points;

    public void player(String nickname){
        this.nickname = nickname;
    }
    public void setSeat(){
        seat = true;
    }
    public void choosePersonal(){

        //fra rivitti maestro di json pensaci tu

    }
    public void getTiles(Board board){
        //board.getAvailableTiles();
    }
    public boolean checkFullShelf(){
        int tmp;
        for(int i=0; i<SHELF_COLUMN; i++){
            tmp = this.shelf.checkColumnEmptiness(i);
            if(tmp > 0)
                return false;
        }
        return true;
    }

    public void addPoints(){

    }
    public void addPoints(CommonGoalCard card){
        //points.add(card.getPoint());
    }
    private int checkPersonalPoints(){
        return 0;
    }

    public Shelf getShelf(){return shelf;}



}
