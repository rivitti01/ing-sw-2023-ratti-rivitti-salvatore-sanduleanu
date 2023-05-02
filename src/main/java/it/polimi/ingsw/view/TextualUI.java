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
    private GameView modelView;



    public TextualUI(GameController gc, GameView modelView){
        this.controller = gc;
        this.modelView = modelView;
        this.modelView.addPropertyChangeListener(this);
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
            askNickName(i);
        }
        System.out.println("The game has started!");
        //dopo la scelta del numero giocatori e i nomi dei Players inizializza il GameModel
        this.controller.initializeModel();
        /*this.modelView.getCurrentPlayer().addPropertyChangeListener(this);
        for(int i=0; i<COMMON_CARDS_PER_GAME; i++){
            this.modelView.getCommonGoals()[i].addPropertyChangeListener(this);
        }
        while(!modelView.isLastTurn()){
            System.out.println("È il turno di " + this.modelView.getCurrentPlayerNickname() + ".");
            printShelf(modelView.getCurrentPlayerShelf());
            int tilesNum = askCoordinates();
            int column = askColumn();
            if (tilesNum > 1 ) askOrder();
            controller.dropTiles(modelView.getCurrentPlayerChosenTiles(),column);
        }
        while(!this.modelView.getCurrentPlayerSeat()){
            System.out.println("È L'ULTIMO TURNO PER: " + this.modelView.getCurrentPlayerNickname());
            printShelf(modelView.getCurrentPlayerShelf());
            int tilesNum = askCoordinates();
            int column = askColumn();
            if (tilesNum > 1) askOrder();
            modelView.getCurrentPlayerShelf().dropTiles(modelView.getCurrentPlayerChosenTiles(),column);
        }
        System.out.println("LA PARTITA È FINITA");
        System.out.println("------------------\n\nPUNTEGGI FINALI\n\n------------------");*/
    }

    public void newTurn(GameView modelview){
        System.out.println("È il turno di " + modelview.getCurrentPlayerNickname() + ".");
        System.out.println("\nQuesta è la tua Carta Obiettivo Personale");
        printPersonalGoalShelf(modelview.getCurrentPlayerPersonalCard());
        System.out.println("\nQuesti sono gli Obiettivi Comuni:");
        for (int i=0; i<COMMON_CARDS_PER_GAME; i++){
            System.out.println(i+1 + ") " + modelview.getCommonGoals()[i].getDescription() + "\n");
        }
        printShelf(modelview.getCurrentPlayerShelf());
        printBoard(modelview.getBoard());
        int tilesNum = askCoordinates();
        int column = askColumn();
        if (tilesNum > 1 ) askOrder();
        controller.dropTiles(modelview.getCurrentPlayerChosenTiles(),column);
    }

    public void lastTurn(GameView modelview){
        System.out.println("È L'ULTIMO TURNO PER: " + modelview.getCurrentPlayerNickname());
        printShelf(modelView.getCurrentPlayerShelf());
        int tilesNum = askCoordinates();
        int column = askColumn();
        if (tilesNum > 1) askOrder();
        modelview.getCurrentPlayerShelf().dropTiles(modelview.getCurrentPlayerChosenTiles(),column);
    }
    private void askOrder() {
        System.out.println("Selezionare l'ordine di inserimento,\ndalla posizione PIU BASSA alla PIU ALTA:");
        List<Tile> tmp = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        do{
            for (int i = 0; i < modelView.getCurrentPlayerChosenTiles().size(); i++) {
                System.out.println("[" + i + "]" + " " + modelView.getCurrentPlayerChosenTiles().get(i).getColor());
            }
            try {
                int pos = Integer.parseInt(scanner.nextLine());
                if (pos < 0 || pos >= modelView.getCurrentPlayerChosenTiles().size())
                    System.out.println("posizione non valida!\nRiprovare");
                else tmp.add(modelView.getCurrentPlayerChosenTiles().remove(pos));
            }catch(NumberFormatException e){
                System.out.println("ERRORE! Non hai inserito un numero.\nRiprova");
            }
        }while (modelView.getCurrentPlayerChosenTiles().size()!=0);
        controller.setChosenTiles(tmp);
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
                else if (modelView.getCurrentPlayerShelf().checkColumnEmptiness(column) < modelView.getCurrentPlayerChosenCoordinates().size())
                    System.out.println("troppe tessere! Riprovare\n");
                else {
                    return column;
                }
            }catch (NumberFormatException e){
                System.out.println("ERRORE! non hai inserito un numero.\nRiprova");
            }
        }

    }

    public int askCoordinates(){
        //ritorna il numero di tiles scelte. utile cosi che viene scelta una sola tiles non avviene la chiamata a askOrder
        //inizializzo le border tiles e le scelte che fa il player sono in base alle tiles disponibili all inizio del turno
        int cont = 0;

        this.controller.setBorderTiles();
        List<int[]> borderTiles = this.modelView.getBoard().getBorderTiles();

        Scanner scanner = new Scanner(System.in);

        for (int[] cord : modelView.getCurrentPlayerChosenCoordinates()){
            System.out.println(modelView.getCurrentPlayerNickname()+" "+cord[0] + " " + cord[1]);
        }
        int maxEmptySpace = modelView.getMaxColumnSpace();
        for(int i=0; i<maxEmptySpace; i++) {
            if(!this.modelView.getAvailableTilesForCurrentPlayer().isEmpty()) {
                boolean chosen = false;
                while (!chosen) {
                    System.out.println("[S] : seleziona una tessera disponibile\n[Q] : passa alla selezione della colonna");

                    String s = scanner.nextLine();

                    switch (s.toUpperCase()) {
                        case "Q" -> {
                            if (i == 0) {
                                System.err.println("Selezionare almeno una tessera!");
                            } else
                                return i;
                        }
                        case "S" -> {
                            System.out.println("seleziona tessera tra le seguenti disponibili");
                            printAvailableTiles(this.modelView.getAvailableTilesForCurrentPlayer());
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
                System.out.println("Non ci sono più tessere disponibili\n");
                return i;
            }
            cont = i;
        }
        System.err.println("Non ci sono piu tessere disponibili oppure sono gia state scelte NUM_MAX");
        return cont;
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

    private void askNickName(int index) {
        boolean validName = false;
        while (!validName) {
            Scanner s = new Scanner (System.in);
            System.out.println("Player " + index + ", choose your nickname for the game:");
            String nickName = s.next();
            if (nickName.length() == 0){
                System.err.println("Sorry there was something wrong with your nickname :(");
                System.err.println("Please try again:");
            } else {
                validName = controller.setPlayerNickname(nickName);
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
        System.out.println("Ecco la tua attuale shelf...");
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
        if (evt.getSource() instanceof GameView){
            switch (evt.getPropertyName()){
                case "board" -> printBoard((Board) evt.getNewValue());
                case "shelf" -> printShelf((Shelf)evt.getNewValue());
                case "seat"  ->System.out.println("The first player is: " + ((Player) evt.getNewValue()).getNickname());
                case "commonPoint" -> System.out.print("************ "+ evt.getNewValue() + " punti ");
                case "playerTakesCommonPoint" -> System.out.println("presi da " + evt.getNewValue() + " ************");
                case "playerTakesEndPoint" -> System.out.println("************ " + evt.getNewValue() + " prende il punto Fine-Partita [1] ************");
                case "playerName" -> System.out.print("------------------\n" + evt.getNewValue() + "  :  ");
                case "playerPoints" -> System.out.println(evt.getNewValue() + "\n------------------");
                case "nextPlayer", "start" -> newTurn((GameView) evt.getSource());
                case "Last Turn" -> lastTurn((GameView) evt.getSource());
                case "winner" -> System.out.println("THE WINNER IS: " + evt.getNewValue());
                case "end" -> System.out.println("LA PARTITA È FINITA\n------------------\n\nPUNTEGGI FINALI\n\n------------------");
                default -> System.err.println("Ignoring event from " + evt.getPropertyName());
            }
        } else {
            System.err.println("Ignoring event from "+ evt.getSource().toString());
        }

    }


}