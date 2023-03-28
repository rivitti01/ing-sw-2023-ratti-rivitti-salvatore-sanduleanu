package it.polimi.ingsw.model;
import it.polimi.ingsw.model.Algorythms.CardStrategy;

import java.util.ArrayList;
//import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.List;

import static it.polimi.ingsw.Costants.*;

public class Player {
    private String nickname;
    private Shelf shelf;
    private boolean seat;
    private boolean[] goalsCompleted;
    private boolean playingTurn;
    private List<Tile> chosenTiles;
    private PersonalGoalCard personalGoalCardCard;
    private int points;

    public Player(String nickname,PersonalGoalCard personalGoalCard){
        this.nickname = nickname;
        shelf = new Shelf();
        chosenTiles = new ArrayList<>();
        this.goalsCompleted = new boolean[COMMON_CARDS_PER_GAME];
        points = 0;
    }

    public void setSeat(boolean seat) {
        this.seat = seat;
    }

    public void setPrivateCard(PersonalGoalCard personalGoalCard){
        this.personalGoalCardCard = personalGoalCard;
    }

    public void addPoints(int points){
        this.points += points;
    }
    public void addPoints(CommonGoalCard card){
        points += card.getPoint();
    }
    private int checkPersonalPoints(){
        return 0;
    }
    public Shelf getShelf(){return shelf;}

    private void getTile(Board board) {
        Scanner scanner = new Scanner(System.in);
        int[] coordinates = new int[2];
        if (scanner.hasNextInt()) {
            do {
                coordinates[0] = scanner.nextInt(); //throws InputMismatchException (managing NOT numeric input)
                coordinates[1] = scanner.nextInt(); //throws InputMismatchException (managing NOT numeric input)
            } while (!board.getAvailableTiles().contains(board.getTile(coordinates[0], coordinates[1]))); //manca l aggiornamento delle availableTiles in base alle coordinate precedentemente selezionate
                        //idea: fare un metodo filter che prende availableTiles e restituisce availableTiles in base a due coppie di coordinate come parametri 
        } else {
            //returna nulla
        }
        chosenTiles.add(board.popTile(coordinates[0], coordinates[1]));
    }

    private int selectColumn(){
        Scanner scanner = new Scanner(System.in);
        int column;
        do {
             column = scanner.nextInt();
        }while (column<0 || column>= SHELF_COLUMN || shelf.checkColumnEmptiness(column) < chosenTiles.size());
        return column;
    }


    public void play(Board board, CommonGoalCard[] cards) {
        System.out.println("Seleziona almeno una tessera, al massimo tre:");
        //for (int i = 0; i < board.getAvailableTiles2().size(); i++) {
        //    System.out.println(board.getAvailableTiles2().get(i)[0] + " " + board.getAvailableTiles2().get(i)[1]);
        //}
        for (int i = 0; i < MAX_TILES_PER_TURN; i++) {
            getTile(board);
        }
        System.out.println("Selezionare una colonna valida dove inserire la/e tessera/e scelta/e");
        int columnSelected = selectColumn();
        System.out.println("Selezionare l'ordine di inserimento,\ndalla posizione piu bassa alla piu alta:\n");

        //for (int i = 0; i < chosenTiles.size(); i++) {
        //    System.out.println("[" + i + "]" + " " + chosenTiles.get(i).getColor());
        //}
        List<Tile> tmp = new ArrayList<>();

        Scanner scanner = new Scanner(System.in);
        for (int i = 0; i < chosenTiles.size(); i++) {  // throws OutOfBoundException
            tmp.add(chosenTiles.remove(scanner.nextInt())); // stampare le rimanenze delle chosenTiles
        }
        shelf.dropTiles(tmp, columnSelected);

        // shelf.checkAdjacents();  ogni fine turno controlla le adiacenze

        for (int i = 0; i < cards.length; i++) {
            if (!goalsCompleted[i] && cards[i].algorythm(this.shelf)) {
                addPoints(cards[i]);
                goalsCompleted[i] = true;
            }
        } // controlla per ogni common se e stato fatto l obiettivo

    }


    public boolean getSeat(){       //non ho capito l utilita
        return this.seat;
    }



}
