package it.polimi.ingsw.model;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.List;

import static it.polimi.ingsw.Costants.SHELF_COLUMN;

public class Player {
    private String nickname;
    private Shelf shelf;
    private boolean seat;
    private boolean playingTurn;
    private List<Tile> chosenTiles;
    private PersonalGoalCard privateCard;
    private List<Integer> points;

    public Player(String nickname,PersonalGoalCard personalGoalCard){
        this.nickname = nickname;
        setPrivateCard(personalGoalCard);
        shelf = new Shelf();
        chosenTiles = new ArrayList<>();

    }
    public void setSeat(){
        seat = true;
    }
    private void setPrivateCard(PersonalGoalCard personalGoalCard){
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
        System.out.println("Choose up to 3 Tiles:");
        for (int i = 0; i < board.getAvailableTiles().size(); i++){
            System.out.println(board.getAvailableTiles2().get(i)[0] + " " + board.getAvailableTiles2().get(i)[1]);
        }
        Scanner scanner = new Scanner(System.in);
        int[] coordinates = new int[2];
        int counter = 0;
        while (counter < 3){
            if(scanner.hasNextInt()){
                coordinates[0] = scanner.nextInt();
                coordinates[1] = scanner.nextInt();
                chosenTiles.add(board.popTile(coordinates[0],coordinates[1]));
                counter++;
                //if (!board.getAvailableTiles2().contains(coordinates)){
                    //System.out.println("Please write valid coordinates");
                //}else {
                    //chosenTiles.add(board.popTile(coordinates[0],coordinates[1]));
                    //counter++;
                //}
            }else{
                if (counter == 0){
                    System.out.println("Write at least one coordinate");
                    continue;
                }
                break;
            }
        }
        System.out.println("Please choose a valid column to insert the tiles");
        Boolean correct = false;
        while (!correct){
            int column = scanner.nextInt();
            if (column >= 0 && column < SHELF_COLUMN){
                if(shelf.checkColumnEmptiness(column) >= chosenTiles.size()){
                    System.out.println("In which order do you want to insert the tiles?\n Please choose from lowest to highest:");
                    for (int i = 0; i< chosenTiles.size(); i++){
                        System.out.println("["+i+"]" + " " + chosenTiles.get(i).getColor());
                    }
                    List<Tile> tmp = new ArrayList<>();
                    for (int i = 0; i< chosenTiles.size(); i++){
                        tmp.add(chosenTiles.get(scanner.nextInt()));
                    }
                    shelf.dropTiles(tmp,column);
                    correct = true;
                }else {
                    System.out.println("Column "+column+ "can't contain "+ chosenTiles.size() + "tiles. Please repeat");
                }
            }else {
                System.out.println("Invalid column, please repeat");
            }
        }
    }
    public boolean getSeat(){
        return this.seat;
    }



}
