package it.polimi.ingsw.model;


import it.polimi.ingsw.view.TextualUI.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import java.beans.PropertyChangeSupport;
import java.text.*;
import java.util.ArrayList;
//import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.List;

import static it.polimi.ingsw.Costants.*;
import static it.polimi.ingsw.model.Colors.*;
import static it.polimi.ingsw.model.Colors.ANSI_RESET;
import static javax.swing.UIManager.addPropertyChangeListener;

public class Player  {
    final String nickname;
    private Shelf shelf;
    private boolean seat;
    private boolean[] goalsCompleted;
    private List<int[]> chosenTiles;
    private PersonalGoalCard personalGoalCard;
    private int points;
    private List<int[]> borderTiles;
    private boolean isChoosing;
    PropertyChangeSupport propertyChangeSupport;

    public Player(String nickname){
        this.nickname = nickname;
        shelf = new Shelf();
        chosenTiles = new ArrayList<>();
        this.goalsCompleted = new boolean[COMMON_CARDS_PER_GAME];
        points = 0;
        propertyChangeSupport = new PropertyChangeSupport(this);

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
        boolean oldValue = this.seat;
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
    public List<int[]> getChosenTiles(){return this.chosenTiles;}
    public void addChosenTile(int[] tile){
        this.chosenTiles.add(tile);
    }
  /*  public void getTile(Board board, List<int[]> chosenCoordinates) {
        Scanner scanner = new Scanner(System.in);
        int[] coordinates = new int[2];
        boolean flag = true;
        int[] t1 = null;
        int[] t2 = null;
        if (chosenCoordinates.size() > 0)
            t1 = chosenCoordinates.get(0);
        if (chosenCoordinates.size() > 1)
            t2 = chosenCoordinates.get(1);
        List<int[]> availableTiles = board.filterAvailableTiles(t1, t2, this.borderTiles);
        if (availableTiles.size() != 0) {
            System.out.println("Seleziona una delle seguenti tessere\n");
        }
        if (chosenTiles.size() != 0 && availableTiles.size() != 0) {
            System.out.println("Scrivere 'ok' per terminare la selezione\n");
        }
        if (availableTiles.size() == 0) {
            System.out.println("Non sono disponibili altre tiles da poter prendere\n");
            isChoosing = false;
            return;
        }
        if (availableTiles.size() != 0) {
            for (int[] availableTile : availableTiles) {
                System.out.print(availableTile[0] + ";" + availableTile[1] + "   ");
            }
            System.out.println("");
        }
        while (true) {
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
                    return;
                } else {
                    if(scanner.next().equals("ok")){
                        if(this.chosenTiles.size()==0) {
                            System.out.println("scegliere almeno una tessera");
                            scanner.nextLine();
                        }
                        else {
                            isChoosing = false;
                            return;
                        }
                    }else System.out.println("comando non valido. riprova");
                }
            }
        }
    }

   */
  /*  private int selectColumn() {
        Scanner scanner = new Scanner(System.in);
        int column;

        while (true) {
            try {
                column = Integer.parseInt(scanner.nextLine());
                if (column < 0 || column >= SHELF_COLUMN)
                    System.out.println("posizione invalida! riprovare\n");
                else if (shelf.checkColumnEmptiness(column) < chosenTiles.size())
                    System.out.println("troppe tessere! Riprovare\n");
                else return column;
            }catch (NumberFormatException e){
                System.out.println("ERRORE! non hai inserito un numero.\nRiprova");
            }
        }
    }

   */
  /*   public void play(Board board, CommonGoalCard[] cards) {
        chosenTiles = new ArrayList<>();
        borderTiles = new ArrayList<>();
        borderTiles = board.getAvailableTiles();
        isChoosing = true;

        System.out.println("Questa e la tua carta obiettivo personale:");
        this.personalGoalCard.printPersonal();

        List<int[]> chosenCoordinates = new ArrayList<>(2);
        for (int i = 0; i < MAX_TILES_PER_TURN; i++) {//modificare MAX_TILES_PER_TURN con un metodo che trova la capacita massima della shelf
            if (isChoosing){
                getTile(board, chosenCoordinates);
            }else
                break;
        }

        System.out.println("Selezionare una colonna valida dove inserire la/e tessera/e scelta/e");

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

  */

    private List<Tile> chooseOrder(List<Tile> chosenTiles){
        System.out.println("Selezionare l'ordine di inserimento,\ndalla posizione PIU BASSA alla PIU ALTA:\n");
        List<Tile> tmp = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        do{
            for (int i = 0; i < chosenTiles.size(); i++) {
                System.out.println("[" + i + "]" + " " + chosenTiles.get(i).getColor());
            }
            try {
                int pos = Integer.parseInt(scanner.nextLine());
                if (pos < 0 || pos >= chosenTiles.size())
                    System.out.println("posizione non valida!\nRiprovare");
                else tmp.add(chosenTiles.remove(pos));
            }catch(NumberFormatException e){
                System.out.println("ERRORE! Non hai inserito un numero.\nRiprova");
            }
        }while (chosenTiles.size()!=0);
        return tmp;
    }



}
