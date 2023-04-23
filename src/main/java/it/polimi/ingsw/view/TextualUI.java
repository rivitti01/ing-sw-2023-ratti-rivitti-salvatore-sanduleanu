package it.polimi.ingsw.view;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.*;


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import static it.polimi.ingsw.Costants.*;
import static it.polimi.ingsw.view.Colors.*;



public class TextualUI  implements  Runnable, PropertyChangeListener {
    private GameController controller;
    private Game model;



    public TextualUI(GameController gc, Game model){
        this.controller = gc;
        this.model = model;
        this.model.addPropertyChangeListener(this);
    }

    @Override
    public void run() {
        //noinspection InfiniteLoopStatement

        System.out.println("--- WELCOME TO A NEW GAME OF 'MY_SHELFIE' :) ---");
        /* Player chooses */
        //Damiani nel suo codice non ha dipendenze dal controller in textualUI (con Observable<Choice> indica che Controller è un suo listener il quale cambia in base alle notifiche)
        int n = askNumber();
        controller.setPlayerNumber(n);
        for (int i = 0; i < n; i++) {
            controller.setPlayerNickname(askNickName(i));
        }
        //dopo la scelta del numero giocatori e i nomi dei Players inizializza il GameModel
        this.controller.initializeModel();
        this.model.getCurrentPlayer().addPropertyChangeListener(this);
        for(int i=0; i<COMMON_CARDS_PER_GAME; i++){
            this.model.getCommonGoals()[i].addPropertyChangeListener(this);
        }


        System.out.println("inizio gioco...");

        while(!model.isLastTurn()){
            System.out.println("È il turno di " + this.model.getCurrentPlayer().getNickname());
            printShelf(controller.getCurrentPlayer().getShelf());
            askCoordinates();
            int column = askColumn();
            askOrder();
            controller.getCurrentPlayer().getShelf().dropTiles(controller.getCurrentPlayer().getChosenTiles(),column);
            printShelf(controller.getCurrentPlayer().getShelf());
            this.controller.nextPlayer();
        }
        while(!this.controller.getCurrentPlayer().getSeat()){
            System.out.println("È l'ULTIMO turno di " + this.model.getCurrentPlayer().getNickname());
            printShelf(controller.getCurrentPlayer().getShelf());
            askCoordinates();
            int column = askColumn();
            askOrder();
            controller.getCurrentPlayer().getShelf().dropTiles(controller.getCurrentPlayer().getChosenTiles(),column);
            printShelf(controller.getCurrentPlayer().getShelf());
            this.controller.nextPlayer();
        }
        System.out.println("LA PARTITA È FINITA");
        System.out.println("------------------\n\nPUNTEGGI FINALI\n\n------------------");
        String winner = controller.calculateWinner();
        System.out.println("Ha vinto " + winner);
    }

    private void askOrder() {
        System.out.println("Selezionare l'ordine di inserimento,\ndalla posizione PIU BASSA alla PIU ALTA:");
        List<Tile> tmp = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        do{
            for (int i = 0; i < controller.getCurrentPlayer().getChosenTiles().size(); i++) {
                System.out.println("[" + i + "]" + " " + controller.getCurrentPlayer().getChosenTiles().get(i).getColor());
            }
            try {
                int pos = Integer.parseInt(scanner.nextLine());
                if (pos < 0 || pos >= controller.getCurrentPlayer().getChosenTiles().size())
                    System.out.println("posizione non valida!\nRiprovare");
                else tmp.add(controller.getCurrentPlayer().getChosenTiles().remove(pos));
            }catch(NumberFormatException e){
                System.out.println("ERRORE! Non hai inserito un numero.\nRiprova");
            }
        }while (controller.getCurrentPlayer().getChosenTiles().size()!=0);
        controller.getCurrentPlayer().setChosenTiles(tmp);
    }

    private int askColumn() {
        Scanner scanner = new Scanner(System.in);
        int column;
        System.out.print("Selezionare una colonna valida dove inserire la/e tessera/e scelta/e\nColonna: ");
        while (true) {
            try {
                column = Integer.parseInt(scanner.nextLine());
                if (column < 0 || column >= SHELF_COLUMN)
                    System.out.println("posizione invalida! riprovare\n");
                else if (model.getCurrentPlayer().getShelf().checkColumnEmptiness(column) < model.getCurrentPlayer().getChosenCoordinates().size())
                    System.out.println("troppe tessere! Riprovare\n");
                else {
                    return column;
                }
            }catch (NumberFormatException e){
                System.out.println("ERRORE! non hai inserito un numero.\nRiprova");
            }
        }

    }

    public void askCoordinates(){
        //inizializzo le border tiles e le scelte che fa il player sono in base alle tiles disponibili all inizio del turno
        this.controller.setBorderTiles();
        List<int[]> borderTiles = this.model.getBoard().getBorderTiles();

        Scanner scanner = new Scanner(System.in);
        System.out.println("\nQuesta è la tua Carta Obiettivo Personale");
        printPersonalGoalShelf(this.model.getCurrentPlayer().getPersonalGoalCard());
        System.out.println("\nQuesti sono gli Obiettivi Comuni:");
        for (int i=0; i<COMMON_CARDS_PER_GAME; i++){
            System.out.println(i+1 + ") " + this.model.getCommonGoals()[i].getDescription() + "\n");
        }
        for (int[] cord : model.getCurrentPlayer().getChosenCoordinates()){
            System.out.println(model.getCurrentPlayer().getNickname()+" "+cord[0] + " " + cord[1]);
        }

        for(int i=0; i<controller.getMaxColumnSpace(); i++) {
            if(!this.model.getAvailableTilesForCurrentPlayer().isEmpty()) {
                boolean chosen = false;
                while (!chosen) {
                    printBoard(this.model.getBoard());
                    System.out.println("[S] : seleziona una tessera disponibile\n[Q] : passa alla selezione della colonna");

                    String s = scanner.nextLine();

                    switch (s.toUpperCase()) {
                        case "Q" -> {
                            if (i == 0) {
                                System.err.println("Selezionare almeno una tessera!");
                            } else
                                return;
                        }
                        case "S" -> {
                            System.out.println("seleziona tessera tra le seguenti disponibili");
                            printAvailableTiles(this.model.getAvailableTilesForCurrentPlayer());
                            //while(true)... scelta x scelta y this.controller."addChosenCoordinates" i++ nel for che seleziona la tessere dopo...
                            while (!chosen) {
                                try {
                                    int[] coordinates = new int[2];
                                    System.out.print("x: ");
                                    coordinates[0] = scanner.nextInt();
                                    scanner.nextLine();
                                    System.out.print("y: ");
                                    coordinates[1] = scanner.nextInt();
                                    scanner.nextLine();
                                    if (this.controller.checkCorrectCoordinates(coordinates, borderTiles)) {
                                        this.controller.addChosenCoordinate(coordinates);
                                        this.controller.addChosenTile(coordinates);
                                        chosen = true;
                                    } else {
                                        System.err.println("Coordinate non valide. Riprova\n");
                                    }
                                } catch (InputMismatchException e1) {
                                    System.err.println("Inserire un numero");
                                    scanner.nextLine();
                                    System.out.println();
                                }
                            }
                        }
                        default -> System.err.println("Non conosco questo comando.\nRiprova");
                    }
                }
            }else{
                System.err.println("Non ci sono piu tessere disponibili");
                return;
            }

        }
        System.err.println("Non ci sono piu tessere disponibili oppure sono gia state scelte NUM_MAX");
    }
    public int askNumber() {
        Scanner s = new Scanner(System.in);
        while (true) {
            System.out.println("Type in the number of players that will take part in this game:");
            int input;
            while (true) {
                if (s.hasNextInt()) {
                    input = s.nextInt();
                    break;
                } else {
                    System.err.println("Enter a valid value");
                    s.nextLine(); // Consuma il valore non intero inserito
                }
            }
            if (input < 2 || input > 4){
                System.err.println("Sorry you cannot play with this much players :(");
                System.err.println("Please enter a number in between 2 and 4:");
            } else {
                return input;
            }
        }
    }

    private String askNickName(int index) {
        while (true) {
            Scanner s = new Scanner (System.in);
            System.out.println("Player " + index + ", choose your nickname for the game:");
            String nickName = s.next();
            if (nickName.length() == 0){
                System.err.println("Sorry there was something wrong with your nickname :(");
                System.err.println("Please try again:");
            } else {
                return nickName;
            }
        }
    }


    void printBoard(Board b) {
        System.out.print("   ");
        System.out.print("   ");
        for (int i = 0; i < b.getSize(); i++)
            System.out.print("  " + i + "  ");

        for (int i = 0; i < b.getSize(); i++) {
            System.out.print("\n");
            for (int j = 0; j < b.getSize(); j++) {
                if (j == 0)
                    System.out.print(" " + i + " ");
                if (b.getTile(i, j) == null || b.getTile(i, j).getColor() == null)
                    System.out.print("     ");
                else {
                    if (b.getTile(i, j).getColor() == Color.WHITE)
                        System.out.print(ANSI_WHITE_BACKGROUND + " " + i + ";" + j + " " + ANSI_RESET);
                    else if (b.getTile(i, j).getColor() == Color.YELLOW)
                        System.out.print(ANSI_YELLOW_BACKGROUND + " " + i + ";" + j + " " + ANSI_RESET);
                    else if (b.getTile(i, j).getColor() == Color.TRANSPARENT)
                        System.out.print("     ");
                    else if (b.getTile(i, j).getColor() == Color.BLUE)
                        System.out.print(ANSI_BLUE_BACKGROUND + " " + i + ";" + j + " " + ANSI_RESET);
                    else if (b.getTile(i, j).getColor() == Color.GREEN)
                        System.out.print(ANSI_GREEN_BACKGROUND + " " + i + ";" + j + " " + ANSI_RESET);
                    else if (b.getTile(i, j).getColor() == Color.PINK)
                        System.out.print(ANSI_PURPLE_BACKGROUND + " " + i + ";" + j + " " + ANSI_RESET);
                    else if (b.getTile(i, j).getColor() == Color.CYAN)
                        System.out.print(ANSI_CYAN_BACKGROUND + " " + i + ";" + j + " " + ANSI_RESET);
                }
            }

        }
        System.out.println();
        System.out.println();
        System.out.println();
    }

    void printAvailableTiles(List<int[]> availableTiles) {
        for (int[] availableTile : availableTiles) System.out.print(availableTile[0] + ";" + availableTile[1] + " ");
        System.out.println();
    }

    void printShelf(Shelf s) {
        for (int i = 0; i < SHELF_ROWS; i++) {
            System.out.println("  ");
            for (int j = 0; j < SHELF_COLUMN; j++) {
                if (j == 0)
                    System.out.print("|");
                if (s.getTile(i, j) == null)
                    System.out.print("  " + "|");
                else {
                    if (s.getTile(i, j).getColor() == Color.WHITE)
                        System.out.print(ANSI_WHITE_BACKGROUND + "  " + ANSI_RESET + "|");
                    else if (s.getTile(i, j).getColor() == Color.YELLOW)
                        System.out.print(ANSI_YELLOW_BACKGROUND + "  " + ANSI_RESET + "|");
                    else if (s.getTile(i, j).getColor() == Color.BLUE)
                        System.out.print(ANSI_BLUE_BACKGROUND + "  " + ANSI_RESET + "|");
                    else if (s.getTile(i, j).getColor() == Color.GREEN)
                        System.out.print(ANSI_GREEN_BACKGROUND + "  " + ANSI_RESET + "|");
                    else if (s.getTile(i, j).getColor() == Color.PINK)
                        System.out.print(ANSI_PURPLE_BACKGROUND + "  " + ANSI_RESET + "|");
                    else if (s.getTile(i, j).getColor() == Color.CYAN)
                        System.out.print(ANSI_CYAN_BACKGROUND + "  " + ANSI_RESET + "|");
                }
            }
        }
        System.out.println();
        System.out.println();
    }

    void printPersonalGoalShelf(PersonalGoalCard personalGoalCard){
        for (int i = 0; i < SHELF_ROWS; i++) {
            System.out.println("  ");
            for (int j = 0; j < SHELF_COLUMN; j++) {
                if (j == 0)
                    System.out.print("|");
                if (personalGoalCard.getGoalsShelf()[i][j] == null)
                    System.out.print("  " + "|");
                else {
                    if (personalGoalCard.getGoalsShelf()[i][j].getColor() == Color.WHITE)
                        System.out.print(ANSI_WHITE_BACKGROUND + "  " + ANSI_RESET + "|");
                    else if (personalGoalCard.getGoalsShelf()[i][j].getColor() == Color.YELLOW)
                        System.out.print(ANSI_YELLOW_BACKGROUND + "  " + ANSI_RESET + "|");
                    else if (personalGoalCard.getGoalsShelf()[i][j].getColor() == Color.BLUE)
                        System.out.print(ANSI_BLUE_BACKGROUND + "  " + ANSI_RESET + "|");
                    else if (personalGoalCard.getGoalsShelf()[i][j].getColor() == Color.GREEN)
                        System.out.print(ANSI_GREEN_BACKGROUND + "  " + ANSI_RESET + "|");
                    else if (personalGoalCard.getGoalsShelf()[i][j].getColor() == Color.PINK)
                        System.out.print(ANSI_PURPLE_BACKGROUND + "  " + ANSI_RESET + "|");
                    else if (personalGoalCard.getGoalsShelf()[i][j].getColor() == Color.CYAN)
                        System.out.print(ANSI_CYAN_BACKGROUND + "  " + ANSI_RESET + "|");
                }
            }
        }
        System.out.println();
        System.out.println();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("board".equals(evt.getPropertyName())) {
            Board b = (Board) evt.getSource();
            printBoard(b);
        } else if ("shelf".equals(evt.getPropertyName())) {
            Shelf s = (Shelf) evt.getSource();
            printShelf(s);
        }else if ("seat".equals(evt.getPropertyName())){
            System.out.println("The first player is: " + evt.getNewValue());
        } else if ("commonPoint".equals(evt.getPropertyName())) {
            System.out.print("************ "+ evt.getNewValue() + " punti ");
        } else if ("playerTakesCommonPoint".equals(evt.getPropertyName())) {
            System.out.println("presi da " + evt.getNewValue() + " ************");
        } else if ("playerTakesEndPoint".equals(evt.getPropertyName())) {
            System.out.println("************ " + evt.getNewValue() + "prende il punto Fine-Partita [1] ************");
        } else if ("playerName".equals(evt.getPropertyName())) {
            System.out.print("------------------\n" + evt.getNewValue() + "  :  ");
        } else if ("playerPoints".equals(evt.getPropertyName())) {
            System.out.println(evt.getNewValue() + "\n------------------");
        }
    }


}