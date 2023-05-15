package it.polimi.ingsw.view;


import it.polimi.ingsw.model.*;
import it.polimi.ingsw.util.Warnings;
import it.polimi.ingsw.util.ViewListener;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import static it.polimi.ingsw.util.Costants.*;
import static it.polimi.ingsw.view.Colors.*;



public class TextualUI {

    private ViewListener listener;

    public void newTurn(){
        System.out.println("È IL TUO TURNO");
        chooseAction();
    }
    public void lastTurn(){
        System.out.println("È IL TUO ULTIMO TURNO");
        chooseAction();
    }
    public void askOrder() {
        System.out.println("seleziona la tile da inserire prima: ");
        Scanner scanner = new Scanner(System.in);
        int tilePosition = -1;
        while(true) {
            try {
                tilePosition = Integer.parseInt(scanner.nextLine());
                listener.tileToDrop(tilePosition);
                return;
            } catch (NumberFormatException e) {
                System.out.println("ERRORE! Non hai inserito un numero.\nRiprova");
            }
        }
    }
    public void askColumn() {
        Scanner scanner = new Scanner(System.in);
        int column;
        System.out.print("Selezionare una colonna valida dove inserire la/e tessera/e scelta/e\nColonna: ");
        while (true) {
            try {
                column = Integer.parseInt(scanner.nextLine());
                //if (column < 0 || column >= SHELF_COLUMN)
                //System.out.println("posizione invalida! riprovare\n");
                //else if (modelView.getCurrentPlayerShelf().checkColumnEmptiness(column) < modelView.getCurrentPlayerChosenCoordinates().size())
                //System.out.println("troppe tessere! Riprovare\n");     TODO: farlo fare al controller
                // else {
                this.listener.columnSetting(column);
                return;
                //}
            }catch (NumberFormatException e){
                System.out.println("ERRORE! non hai inserito un numero.\nRiprova");
            }
        }

    }
    public void chooseAction() {
        Scanner scanner = new Scanner(System.in);
        while(true) {
            System.out.println("[S] : seleziona una tessera disponibile\n[Q] : passa alla selezione della colonna");
            String s = scanner.nextLine();
            switch (s.toUpperCase()) {
                case "Q" -> {
                    listener.endsSelection();
                }
                case "S" -> {
                    System.out.println("seleziona una tessera:");
                    try {
                        int[] coordinates = new int[2];
                        System.out.print("x: ");
                        coordinates[0] = scanner.nextInt();
                        scanner.nextLine();
                        System.out.print("y: ");
                        coordinates[1] = scanner.nextInt();
                        scanner.nextLine();
                        this.listener.checkingCoordinates(coordinates);
                    } catch (InputMismatchException e1) {
                        System.err.println("Inserire un numero");
                        scanner.nextLine();
                        System.out.println();
                    }
                }
                default -> System.err.println("Non conosco questo comando.\nRiprova");
            }
        }
    }
    public void askNumber() {
        Scanner s = new Scanner(System.in);
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
        }
        this.listener.numberParticipantsSetting(input);
    }
    public void askNickName() {
        boolean validName = false;
        String nickName = null;
        while (!validName) {
            Scanner s = new Scanner (System.in);
            System.out.println("Choose your nickname for the game:");
            nickName = s.next();
            if (nickName.length() == 0){
                System.err.println("Sorry there was something wrong with your nickname :(");
                System.err.println("Please try again:");
            } else {
                validName=true;
            }
        }
        listener.clientConnection(nickName);
    }
    public void error(Warnings e){
        switch (e){
            case INVALID_TILE -> {
                System.err.println("La tile selezionata non può essere scelta. Sceglierne un'altra:");
                chooseAction();
            }
            case INVALID_NICKNAME -> {
                System.err.println("Il nome scelto è già in uso, sceglierne un altro:");
                askNickName();
            }
            case INVALID_COLUMN -> {
                System.err.println("Errore nella scelta della colonna, scegline un'altra:");
                askColumn();
            }
            case INVALID_ACTION -> {
                System.err.println("Scegliere almeno una tile prima di procedere:");
                chooseAction();
            }
            case GAME_ALREADY_STARTED -> System.err.println("Ci dispiace c'è già una partita in atto. Non puoi giocare.");
            case MAX_TILES_CHOSEN -> {
                System.err.println("Hai raggiunto il numero massimo di tessere prendibili.");
                askColumn();
            }
            case INVALID_ORDER -> {
                System.err.println("AOO metti una posizione sensata.");
                askOrder();
            }
        }
    }



    public void lastTurnReached(String nickname){
        System.out.println(nickname + "ha riempito la shelf\nInizia l'ultimo giro");
    }
    public void printBoard(Board b) {
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
    public void printAvailableTiles(List<int[]> availableTiles) {
        for (int[] availableTile : availableTiles) System.out.print(availableTile[0] + ";" + availableTile[1] + " ");
        System.out.println();
    }
    public void printShelf(Shelf s, String nickname) {
        System.out.println("shelf di: " + nickname);
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
    public void printPersonalGoalShelf(PersonalGoalCard personalGoalCard){
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
    public void printChosenTiles(List<Tile> chosenTiles, String nickname) {
        if (!chosenTiles.isEmpty()) {
            System.out.println("tessere scelte da: " + nickname);
            for (int i = 0; i < chosenTiles.size(); i++)
                if (chosenTiles.get(i).getColor().equals(Color.BLUE))
                    System.out.println(i + 1 + ") " + ANSI_BLUE_BACKGROUND + "  " + ANSI_RESET);
                else if (chosenTiles.get(i).getColor().equals(Color.WHITE))
                    System.out.println(i + 1 + ") " + ANSI_WHITE_BACKGROUND + "  " + ANSI_RESET);
                else if (chosenTiles.get(i).getColor().equals(Color.YELLOW))
                    System.out.println(i + 1 + ") " + ANSI_YELLOW_BACKGROUND + "  " + ANSI_RESET);
                else if (chosenTiles.get(i).getColor().equals(Color.PINK))
                    System.out.println(i + 1 + ") " + ANSI_PURPLE_BACKGROUND + "  " + ANSI_RESET);
                else if (chosenTiles.get(i).getColor().equals(Color.GREEN))
                    System.out.println(i + 1 + ") " + ANSI_GREEN_BACKGROUND + "  " + ANSI_RESET);
                else if (chosenTiles.get(i).getColor().equals(Color.CYAN))
                    System.out.println(i + 1 + ") " + ANSI_CYAN_BACKGROUND + "  " + ANSI_RESET);
        }
    }


    public void addListener (ViewListener l){
        this.listener = l;
    }


    public void printGame(GameView gameView){
        printBoard(gameView.getBoard());
        for(String nickname:  gameView.getPlayersShelves().keySet()) {
            printShelf(gameView.getPlayersShelves().get(nickname), nickname);
        }
        for (int i=0; i<COMMON_CARDS_PER_GAME; i++){
            System.out.println(i+1 + ") " + gameView.getCommonGoals()[i] + "\n");
        }
        printPersonalGoalShelf(gameView.getPersonal());
        printChosenTiles(gameView.getChosenTiles(), gameView.getNickName());
    }

}

