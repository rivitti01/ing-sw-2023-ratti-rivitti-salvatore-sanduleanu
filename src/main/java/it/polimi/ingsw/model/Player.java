package it.polimi.ingsw.model;


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
//import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.List;

import static it.polimi.ingsw.Costants.*;

public class Player  {
    final String nickname;
    private Shelf shelf;
    private boolean seat;
    private boolean[] goalsCompleted;
    private List<int[]> chosenCoordinates;
    private List<Tile> chosenTiles;
    private PersonalGoalCard personalGoalCard;
    private int points;
    PropertyChangeSupport propertyChangeSupport;

    public Player(String nickname){
        this.nickname = nickname;
        shelf = new Shelf();
        chosenCoordinates = new ArrayList<>();
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
        if(points == END_GAME_POINT)
            propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this, "playerTakesEndPoint", null, this.getNickname()));
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
    public List<int[]> getChosenCoordinates(){return this.chosenCoordinates;}
    public void addChosenCoordinate(int[] coordinates){
        this.chosenCoordinates.add(coordinates);
    }
    public void addChosenTile(Tile tile){
        this.chosenTiles.add(tile);
    }
    public PersonalGoalCard getPersonalGoalCard(){
        return this.personalGoalCard;
    }
    public void reset(CommonGoalCard[] cards){
        chosenTiles = new ArrayList<>();
        chosenCoordinates = new ArrayList<>(2);
        for (int i = 0; i < COMMON_CARDS_PER_GAME; i++) {
            if (!this.goalsCompleted[i] && cards[i].algorythm(this.shelf)) {
                int point = cards[i].getPoint();
                addPoints(point);
                this.goalsCompleted[i] = true;
                propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this, "playerTakesCommonPoint", null, this.getNickname()));
            }
        } // controlla per ogni common se e stato fatto l obiettivo
    }

    public List<Tile> getChosenTiles() {
        return chosenTiles;
    }

    public void setChosenTiles(List<Tile> chosenTiles) {
        this.chosenTiles = chosenTiles;
    }

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

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }



}
