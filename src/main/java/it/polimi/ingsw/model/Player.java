package it.polimi.ingsw.model;
import java.util.Scanner;
import java.util.List;

public class Player {
    private String nickname;
    private Shelf shelf;
    private boolean seat;
    private boolean playingTurn;
    private List<Integer> chosenTiles;
    private PersonalGoalCard privateCard;
    private List<Integer> points;

    public void player(String nickname){
        this.nickname = nickname;
    }
    public void setSeat(){
        seat = true;
    }
    public void setPrivateCard(PersonalGoalCard personalGoalCard){
        privateCard = personalGoalCard;
        //fra rivitti maestro di json pensaci tu
    }
    public void getTiles(Board board){
        //board.getAvailableTiles();
    }
    public boolean checkFullShelf(){
        return this.shelf.isFull();
    } //returns true iff the player's shelf is completely full of tiles

    public void addPoints(){

    }
    public void addPoints(CommonGoalCard card){
        //points.add(card.getPoint());
    }
    private int checkPersonalPoints(){
        return 0;
    }

    public Shelf getShelf(){return shelf;}
    public void play(Board board) {
        int[] coordnates = new int[2];
        int i = 0;
        Scanner s = new Scanner(System.in);
        board.getAvailableTiles();

            System.out.println("Write the coordinates of the chosen tiles and press 's' when you are finished.");
            chosenTiles.add(i, s.nextInt());
            i++;
            chosenTiles.add(i, s.nextInt());


        System.out.println("l'utente inserisce il numero d ");
        //qua succederà qualcosa che traforma le icone cliccate dall'utente in coordinate delle tiles scelte
        chosenTiles.add(0, s.nextInt());


    }
    public boolean getSeat(){
        return this.seat;
    }



}
