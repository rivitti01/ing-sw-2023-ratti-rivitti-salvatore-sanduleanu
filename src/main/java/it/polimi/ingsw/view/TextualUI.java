package it.polimi.ingsw.view;


import it.polimi.ingsw.model.*;
import it.polimi.ingsw.util.Warnings;
import it.polimi.ingsw.util.ViewListener;

import java.rmi.RemoteException;
import java.util.*;

import static it.polimi.ingsw.util.Costants.*;
import static it.polimi.ingsw.view.Colors.*;



public class TextualUI {
    private ViewListener listener;



    private final Scanner scanner = new Scanner(System.in);

    public void newTurn() throws RemoteException {
        System.out.println("È IL TUO TURNO");
        chooseAction();
    }
    public void lastTurn(){
        System.out.println("È IL TUO ULTIMO TURNO");
        try {
            chooseAction();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }


    public void chat(){
        System.out.println("scrivi qualcosa a qualcuno!");
        String message = scanner.nextLine();
        try {
            this.listener.newMessage(message);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
    public void askOrder() {
        int tilePosition;
        while(true) {
            System.out.println("seleziona la tile da inserire prima: ");
            try {
                tilePosition = scanner.nextInt();
                listener.tileToDrop(tilePosition);
                return;
            } catch (InputMismatchException | RemoteException e) {
                String invalidInput = scanner.next();
                scanner.nextLine();
                if(invalidInput.equals("chat")) {
                   chat();
                } else {
                    System.out.println("ERRORE! Non hai inserito un numero.\nRiprova");
                }
            }
        }
    }
    public void askColumn() {
        int column;
        while (true) {
            System.out.print("Selezionare una colonna valida dove inserire la/e tessera/e scelta/e\nColonna: ");
            try {
                column = scanner.nextInt();
                scanner.nextLine();
                this.listener.columnSetting(column);
                return;
            }catch (InputMismatchException | RemoteException e){
                String invalidInput = scanner.next();
                if(invalidInput.equals("chat")) {
                    chat();
                } else {
                    System.out.println("ERRORE! non hai inserito un numero.\nRiprova");
                }
            }
        }

    }
    public void chooseAction() throws RemoteException {
        while(true) {
            System.out.println("""
                    [S] : seleziona una tessera disponibile
                    [Q] : passa alla selezione della colonna
                    [chat] scrivi qualcosa nella chat""");

            String s = scanner.nextLine();
            switch (s.toUpperCase()) {
                case "Q" -> {
                    listener.endsSelection();
                    return;
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
                        return;
                    } catch (InputMismatchException e1) {
                        String invalidInput = scanner.next();
                        if (invalidInput.equals("chat")) {
                            chat();

                        } else {
                            System.err.println("Inserire un numero");
                            System.out.println();
                        }
                        return;
                    }
                }
                case "CHAT" -> chat();
                default -> System.err.println("Non conosco questo comando.\nRiprova");
            }

        }
    }
    public void askNumber() throws RemoteException {
        System.out.println("Type in the number of players that will take part in this game:");
        int input;
        while (true) {
            if (scanner.hasNextInt()) {
                input = scanner.nextInt();
                if (input < 2 || input > 4){
                    System.err.println("Sorry you cannot play with this much players :(");
                    System.err.println("Please enter a number in between 2 and 4:");
                }else {
                    break;
                }
            } else {
                System.err.println("Enter a valid value");
                scanner.nextLine(); // Consuma il valore non intero inserito
            }
        }
        this.listener.numberPartecipantsSetting(input);
    }
    public void askNickName() throws RemoteException {
        boolean validName = false;
        String nickName = null;
        while (!validName) {
            System.out.println("Choose your nickname for the game:");
            nickName = scanner.next();
            scanner.nextLine();
            if (nickName.length() == 0){
                System.err.println("Sorry there was something wrong with your nickname :(");
                System.err.println("Please try again:");
            } else {
                validName=true;
            }
        }
        listener.clientNickNameSetting(nickName);
    }
    public void warning(Warnings e) throws RemoteException {
        switch (e){
            case INVALID_TILE -> {
                System.out.println("La tile selezionata non può essere scelta. Sceglierne un'altra:");
                try {
                    chooseAction();
                } catch (RemoteException ex) {
                    throw new RuntimeException(ex);
                }
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
                try {
                    chooseAction();
                } catch (RemoteException ex) {
                    throw new RuntimeException(ex);
                }
            }
            case GAME_ALREADY_STARTED -> System.err.println("A game has already started, you cannot play. Sorry :(");
            case MAX_TILES_CHOSEN -> {
                System.err.println("Hai raggiunto il numero massimo di tessere prendibili.");
                askColumn();
            }
            case INVALID_ORDER -> {
                System.err.println("AOO metti una posizione sensata.");
                askOrder();
            }
            case WAIT -> System.out.println("Loading. Wait...");
            case OK_JOINER -> System.out.println("name set correctly");
            case YOUR_TURN -> newTurn();
            case CORRECT_CORD -> {

            }
            case CONTINUE_TO_CHOOSE -> chooseAction();
            case ASK_COLUMN -> askColumn();
            case ASK_ORDER -> askOrder();
        }
    }
    public void lastTurnReached(String nickname){
        System.out.println(nickname + " ha riempito la shelf\nInizia l'ultimo giro");
    }
    public void printBoard(Board b) {
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
    private void printTile(Tile t) {
        if (t == null || t.getColor() == null)
            System.out.print("  " + "|");
        else {
            if (t.getColor() == Color.WHITE)
                System.out.print(ANSI_WHITE_BACKGROUND + "  " + ANSI_RESET + "|");
            else if (t.getColor() == Color.YELLOW)
                System.out.print(ANSI_YELLOW_BACKGROUND + "  " + ANSI_RESET + "|");
            else if (t.getColor() == Color.BLUE)
                System.out.print(ANSI_BLUE_BACKGROUND + "  " + ANSI_RESET + "|");
            else if (t.getColor() == Color.GREEN)
                System.out.print(ANSI_GREEN_BACKGROUND + "  " + ANSI_RESET + "|");
            else if (t.getColor() == Color.PINK)
                System.out.print(ANSI_PURPLE_BACKGROUND + "  " + ANSI_RESET + "|");
            else if (t.getColor() == Color.CYAN)
                System.out.print(ANSI_CYAN_BACKGROUND + "  " + ANSI_RESET + "|");
        }
    }
    public void printShelves(Map<String, Shelf> playerShelves) {
        for(String s : playerShelves.keySet()) {
            System.out.print(s + "'s shelf:\t\t\t\t");
        }
        System.out.println();
        for (int i = 0; i < SHELF_ROWS; i++) {
            for(String s: playerShelves.keySet()){
                Shelf tmpShelf = playerShelves.get(s);
                System.out.print("|");
                for(int j=0; j<SHELF_COLUMN; j++){
                    printTile(tmpShelf.getTile(i, j));
                }
                System.out.print("\t\t");
            }
            System.out.println();
        }
        System.out.println();
        System.out.println();
    }
    public void printPersonalGoalShelf(PersonalGoalCard personalGoalCard){
        System.out.println("This is your personal goal card:");
        for (int i = 0; i < SHELF_ROWS; i++) {
            System.out.println("  ");
            for (int j = 0; j < SHELF_COLUMN; j++) {
                if (j == 0)
                    System.out.print("    |");
                if (personalGoalCard.getGoalsShelf()[i][j] == null)
                    System.out.print("  " + "|");
                else {
                    printTile(personalGoalCard.getGoalsShelf()[i][j]);
                }
            }

        }
        char checkmark = '✓';
        System.out.println();
        System.out.println(checkmark + "  | 1 | 2 | 3 | 4 | 5 | 6 |");
        System.out.println("X  | 1 | 2 | 4 | 6 | 9 | 12 |");
        System.out.println();
    }
    public void printChosenTiles(List<Tile> chosenTiles, String nickname) {
        if (!chosenTiles.isEmpty()) {
            System.out.println("******************* CHOSEN TILES BY " + nickname + " **************************************");
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
    public void printFinalPoints(Map<String, Integer> chart){
        System.out.println("******************** GAME ENDED ***************************************");
        System.out.println();
        System.out.println("                    FINAL POINTS                   ");

        for(String s : chart.keySet()){
            System.out.println("--------------------------------------------------");
            System.out.println(s + ":" + chart.get(s));
        }
        System.out.println("--------------------------------------------------");
    }
    public void addListener (ViewListener l){
        this.listener = l;
    }
    public void printGame(GameView gameView){
        try {
            this.printChat(gameView.getChat());
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        System.out.println("******************* COMMON GOAL CARDS *************************************");
        for (int i=0; i<COMMON_CARDS_PER_GAME; i++){
            System.out.println(i+1 + ") " + gameView.getCommonGoals()[i] + "\n");
        }
        System.out.println("******************* BOARD **************************************");
        printBoard(gameView.getBoard());
        System.out.println("******************* SHELVES **************************************");
        printShelves(gameView.getPlayersShelves());
        printPersonalGoalShelf(gameView.getPersonal());

        printChosenTiles(gameView.getChosenTiles(), gameView.getNickName());
        System.out.println("E' il turno di: "+gameView.getNickName());
    }

    public void printChat(ChatView chatView) throws RemoteException {
        System.out.println("******************* CHAT **************************************");
        List<String> chat = chatView.getChat();
        for(String message : chat)
            System.out.println(message);
    }



    public void waitingTurn() {
        String input;
        try {
            while (true) {
                if (scanner.hasNext()) {
                    input = scanner.nextLine();
                    if (input.equals("chat")) {
                        chat();
                    } else {
                        System.out.println("It's not your turn.\nYou can type [chat] to write in the chat.");
                    }
                } else {
                    // Break the loop when it's not the player's turn anymore
                    break;
                }
            }
        }catch (NoSuchElementException e) {
        }
    }





}

