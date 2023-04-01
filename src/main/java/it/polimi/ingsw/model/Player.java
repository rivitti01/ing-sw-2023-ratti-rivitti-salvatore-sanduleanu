package it.polimi.ingsw.model;


import java.util.ArrayList;
//import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.List;

import static it.polimi.ingsw.Costants.*;

public class Player {
    final String nickname;
    private Shelf shelf;
    private boolean seat;
    private boolean[] goalsCompleted;
    private List<Tile> chosenTiles;
    private PersonalGoalCard personalGoalCard;
    private int points;

    public Player(String nickname,PersonalGoalCard chosenCard){
        this.nickname = nickname;
        shelf = new Shelf();
        chosenTiles = new ArrayList<>();
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
        points += card.getPoint();
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

    public Shelf getShelf(){return shelf;}

    public void getTile(Board board, List<int[]> chosenCoordinates) {
        Scanner scanner = new Scanner(System.in);
        int[] coordinates = new int[2];
        int flag = 0;
        int[] t1 = null;
        int[] t2 = null;
        if(chosenCoordinates.size()>0)
            t1 = chosenCoordinates.get(0);
        if(chosenCoordinates.size()>1)
            t2 = chosenCoordinates.get(1);
        if (scanner.hasNextInt()) {
            do {
                if(flag > 0)
                    System.out.println("Posizione errata!\nReinserire coordinate: ");
                coordinates[0] = scanner.nextInt(); //throws InputMismatchException (managing NOT numeric input)
                coordinates[1] = scanner.nextInt(); //throws InputMismatchException (managing NOT numeric input)
                flag++;
            } while (!board.getAvailableTiles2(t1, t2).contains(coordinates));
            chosenCoordinates.add(coordinates);
            this.chosenTiles.add(board.popTile(coordinates[0], coordinates[1]));
        }
        //altrimenti non fa nulla (non so se si possa non fare nulla da input pero si vedra)
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
        //}  (ci manca il colore da far vedere sullo schermo in quella posizione)

        List<int[]> chosenCoordinates = new ArrayList<>(2);
        for (int i = 0; i < MAX_TILES_PER_TURN; i++) {
            //modificare MAX_TILES_PER_TURN con un metodo che trova la capacita massima della shelf
            getTile(board, chosenCoordinates);
        }

        System.out.println("Selezionare una colonna valida dove inserire la/e tessera/e scelta/e");
        int columnSelected = selectColumn();


        for (int i = 0; i < chosenTiles.size(); i++) {
            System.out.println("[" + i + "]" + " " + chosenTiles.get(i).getColor());
        }

        List<Tile> tmp = new ArrayList<>();

        System.out.println("Selezionare l'ordine di inserimento,\ndalla posizione piu bassa alla piu alta:\n");

        Scanner scanner = new Scanner(System.in);
        for (int i = 0; i < chosenTiles.size(); i++) {  // throws OutOfBoundException
            tmp.add(chosenTiles.remove(scanner.nextInt())); // stampare le rimanenze delle chosenTiles
            // problema su come il giocatore sceglie l'ordine: all inizio deve mettere un numero tra 0 e il (numero di Tiles scelte)-1 mentre dopo la lunghezza diminuisce
        }
        shelf.dropTiles(tmp, columnSelected);
        chosenTiles = new ArrayList<Tile>();

        for (int i = 0; i < COMMON_CARDS_PER_GAME; i++) {
            if (!goalsCompleted[i] && cards[i].algorythm(this.shelf)) {
                addPoints(cards[i]);
                goalsCompleted[i] = true;
            }
        } // controlla per ogni common se e stato fatto l obiettivo

    }   // finisce il turno


    public boolean getSeat(){
        return this.seat;
    }

    public int getPoints(){return this.points;}
    public List<Tile> getChosenTiles(){return this.chosenTiles;}

}
