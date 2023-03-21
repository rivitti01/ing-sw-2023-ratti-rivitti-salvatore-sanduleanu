package it.polimi.ingsw.model;

import it.polimi.ingsw.model.Board.Board;

import java.util.List;

public class Player {
    private String nickname;
    private Shelf shelf;
    private Boolean seat;
    private Boolean playingTurn;
    private Tile[] choosenTile;
    private PersonalGoalCard privateCard;
    private List<Integer> points;

    public void player(String nickname){

    }
    public void setSeat(){

    }
    public void choosePersonal(){

    }
    public void getTiles(Board board){
        //board.getAvailableTiles();
    }
    public boolean checkFullShelf(){

        return false;
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
