package it.polimi.ingsw.model;


import java.util.ArrayList;
//import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.List;

import static it.polimi.ingsw.Costants.*;
import static it.polimi.ingsw.model.Colors.*;
import static it.polimi.ingsw.model.Colors.ANSI_RESET;

public class Player {
    final String nickname;
    private Shelf shelf;
    private boolean seat;
    private boolean[] goalsCompleted;
    private List<Tile> chosenTiles;
    private PersonalGoalCard personalGoalCard;
    private int points;
    private List<int[]> borderTiles;
    private boolean isChoosing;

    public Player(String nickname){
        this.nickname = nickname;
        shelf = new Shelf();
        chosenTiles = new ArrayList<>();
        this.goalsCompleted = new boolean[COMMON_CARDS_PER_GAME];
        points = 0;
    }
    public Player(String nickname,PersonalGoalCard chosenCard){
        this.nickname = nickname;
        shelf = new Shelf();
        this.goalsCompleted = new boolean[COMMON_CARDS_PER_GAME];
        points = 0;
        setPrivateCard(chosenCard);
    }
    public String getNickname(){return this.nickname;}
    public void setSeat(boolean seat) {
        this.seat = seat;
    }
    public void setPrivateCard(PersonalGoalCard personalGoalCard){
        this.personalGoalCard = personalGoalCard;
    }
    public void addPoints(int points){
        this.points += points;
    }
    public void addPoints(CommonGoalCard card){
        this.points += card.getPoint();
    }
    public int checkPersonalPoints(){
        int count = 0;
        for (int i=0; i<SHELF_ROWS; i++){
            for(int j=0; j<SHELF_COLUMN; j++){
                if(personalGoalCard.goalsShelf[i][j]!=null && this.shelf.getTile(i, j)!=null &&
                        personalGoalCard.goalsShelf[i][j].getColor().equals(this.shelf.getTile(i, j).getColor()))
                    count++;
            }
        }
        return switch (count) {
            case 1 -> 1;
            case 2 -> 2;
            case 3 -> 4;
            case 4 -> 6;
            case 5 -> 9;
            case 6 -> 12;
            default -> 0;
        };
    }
    public Shelf getShelf(){return this.shelf;}
    public boolean getSeat(){
        return this.seat;
    }
    public int getPoints(){return this.points;}
    public List<Tile> getChosenTiles(){return this.chosenTiles;}
    public void printShelf() {
        for (int i = 0; i < SHELF_ROWS; i++) {
            System.out.println("  ");
            for (int j = 0; j < SHELF_COLUMN; j++) {
                if(j==0)
                    System.out.print("|");
                if (this.shelf.getTile(i, j) == null)
                    System.out.print("  " + "|");
                else {
                    if (this.shelf.getTile(i, j).getColor() == Color.WHITE)
                        System.out.print(ANSI_WHITE_BACKGROUND + "  " + ANSI_RESET + "|");
                    else if (this.shelf.getTile(i, j).getColor() == Color.YELLOW)
                        System.out.print(ANSI_YELLOW_BACKGROUND + "  " + ANSI_RESET + "|");
                    else if (this.shelf.getTile(i, j).getColor() == Color.BLUE)
                        System.out.print(ANSI_BLUE_BACKGROUND + "  " + ANSI_RESET + "|");
                    else if (this.shelf.getTile(i, j).getColor() == Color.GREEN)
                        System.out.print(ANSI_GREEN_BACKGROUND + "  " + ANSI_RESET + "|");
                    else if (this.shelf.getTile(i, j).getColor() == Color.PINK)
                        System.out.print(ANSI_PURPLE_BACKGROUND + "  " + ANSI_RESET + "|");
                    else if (this.shelf.getTile(i, j).getColor() == Color.CYAN)
                        System.out.print(ANSI_CYAN_BACKGROUND + "  " + ANSI_RESET + "|");
                }

            }
        }
        System.out.println("");
    }

    public void getTile(Board board, List<int[]> chosenCoordinates) {
        Scanner scanner = new Scanner(System.in);
        int[] coordinates = new int[2];
        boolean flag = true;
        int[] t1 = null;
        int[] t2 = null;
        if(chosenCoordinates.size()>0)
            t1 = chosenCoordinates.get(0);
        if(chosenCoordinates.size()>1)
            t2 = chosenCoordinates.get(1);
        List<int[]> availableTiles = board.filterAvailableTiles(t1,t2,this.borderTiles);
        if (availableTiles.size()!= 0){
            System.out.println("Seleziona una delle seguenti tessere\n");
        }
        if (chosenTiles.size()!= 0 && availableTiles.size()!= 0){
            System.out.println("Scrivere 'exit' o qualsiasi altra parola se non si vogliono scegliere altre tiles\n");
        }
        if (availableTiles.size()== 0){
            System.out.println("Non sono disponibili altre tiles da poter prendere\n");
            isChoosing = false;
        }
        if (availableTiles.size()!= 0){
            for (int[] availableTile : availableTiles) {
                System.out.print(availableTile[0] + ";" + availableTile[1] + "   ");
            }
            System.out.println("");
        }
        if (isChoosing) {
            if (scanner.hasNextInt()) { //&& isChoosing
                do {
                    coordinates[0] = scanner.nextInt(); //throws InputMismatchException (managing NOT numeric input)
                    coordinates[1] = scanner.nextInt(); //throws InputMismatchException (managing NOT numeric input)
                    for (int i = 0; i < availableTiles.size(); i++) {
                        if (availableTiles.get(i)[0] == coordinates[0] && availableTiles.get(i)[1] == coordinates[1]) {
                            flag = false;
                            break;
                        }
                    }
                    if (flag) {
                        System.out.println("Posizione errata!\nReinserire coordinate: ");
                    }
                } while (flag); //&& board.getAvailableTiles2(t1, t2).contains(coordinates)
                chosenCoordinates.add(coordinates);
                this.chosenTiles.add(board.popTile(coordinates[0], coordinates[1]));
            } else {
                isChoosing = false;
            }
        }
    }

    private int selectColumn() {
        Scanner scanner = new Scanner(System.in);
        int column;

        while (true) {
            column = scanner.nextInt();
            if(column < 0 || column >= SHELF_COLUMN)
                System.out.println("posizione invalida! riprovare\n");
            else if(shelf.checkColumnEmptiness(column) < chosenTiles.size())
                System.out.println("troppe tessere! Riprovare\n");
            else return column;
        }
    }


    public void play(Board board, CommonGoalCard[] cards) {
        chosenTiles = new ArrayList<>();
        borderTiles = new ArrayList<>();
        borderTiles = board.getAvailableTiles();
        isChoosing = true;
        List<int[]> chosenCoordinates = new ArrayList<>(2);
        for (int i = 0; i < MAX_TILES_PER_TURN; i++) {//modificare MAX_TILES_PER_TURN con un metodo che trova la capacita massima della shelf
            if (isChoosing){
                getTile(board, chosenCoordinates);
            }else
                break;
        }

        System.out.println("Selezionare una colonna valida dove inserire la/e tessera/e scelta/e");
        this.printShelf();
        for(int i=0; i<SHELF_COLUMN; i++)
            System.out.print(" " + i + " ");
        System.out.println("");
        int columnSelected = selectColumn();

        chosenTiles = chooseOrder(chosenTiles);
        shelf.dropTiles(chosenTiles, columnSelected);


        for (int i = 0; i < COMMON_CARDS_PER_GAME; i++) {
            if (!this.goalsCompleted[i] && cards[i].algorythm(this.shelf)) {
                addPoints(cards[i]);
                this.goalsCompleted[i] = true;
            }
        } // controlla per ogni common se e stato fatto l obiettivo

    }   // finisce il turno


    private List<Tile> chooseOrder(List<Tile> chosenTiles){
        System.out.println("Selezionare l'ordine di inserimento,\ndalla posizione piu bassa alla piu alta:\n");
        List<Tile> tmp = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        do{
            for (int i = 0; i < chosenTiles.size(); i++) {
                System.out.println("[" + i + "]" + " " + chosenTiles.get(i).getColor());
            }
            tmp.add(chosenTiles.remove(scanner.nextInt()));
        }while (chosenTiles.size()!=0);
        return tmp;
    }
}
