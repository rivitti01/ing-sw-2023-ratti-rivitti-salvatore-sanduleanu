package it.polimi.ingsw.view;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.*;


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Scanner;

import static it.polimi.ingsw.Costants.*;
import static it.polimi.ingsw.model.Colors.*;



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

        System.out.println("inizio gioco...");

        while(true){
            System.out.println("È il turno di " + this.model.getCurrentPlayer().getNickname());
                int[] coordinates = askCoordinates();

        }

/*        while (true) {
            System.out.println("Turno di: " + this.controller.getCurrentPlayer().getNickname());
            //stampare la board
            //stampare le coordinate disponibili
            System.out.println("Questa e la tua carta obiettivo personale:");
            System.out.println("scegli delle coordinate valide");
            //controller deve chiamare getTile in Player con le coordinate come parametro al posto di avere gli Scanner in Player

        }

 */
    }

    public int[] askCoordinates(){
        Scanner scanner = new Scanner(System.in);
        int[] coordinates = new int[2];

        for(int i=0; i<MAX_TILES_PER_TURN; i++) {
            while (true) {
                System.out.println("[S] : seleziona una tessera disponibile\n[Q] : passa alla selezione della colonna");

                String s = scanner.nextLine();
                if (!s.equals("")) {
                    switch (s.toUpperCase()) {
                        case "Q":
                            if (i == 0)
                                System.err.println("Selezionare almeno una tessera!");
                            else
                                return null;
                            break;

                        case "S":
                            System.out.println("seleziona tessera");
                            //while(true)... scelta x scelta y return
                            break;
                        default:
                            System.err.println("Non conosco questo comando.\nRiprova");
                            break;
                    }
                } else {
                    System.err.println("Selezionare un comando!");
                }

            }
        }
        return null;
    }
    public int askNumber() {
        Scanner s = new Scanner(System.in);
        while (true) {
            System.out.println("Type in the number of players that will take part in this game:");
            int input = 0;
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

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("board".equals(evt.getPropertyName())) {
            Board b = (Board) evt.getSource();

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
            System.out.println("");
            System.out.println("");
            System.out.println("");
        } else if ("shelf".equals(evt.getPropertyName())) {
            Shelf s = (Shelf) evt.getSource();
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
        }else if ("seat".equals(evt.getPropertyName())){
            System.out.println("The first player is: " + evt.getNewValue());
        }
    }







    /*public Choice askPlayer() {
        Scanner s = new Scanner(System.in);
        System.out.println("Make your choice: ");
        System.out.println(
                "Signs: " +
                        Arrays.stream(Choice.values())
                                .map(Choice::name)
                                .collect(
                                        Collectors.joining(",", "[", "]")));
        while (true) {
            String input = s.next();
            try {
                return Choice.valueOf(input);
            } catch (IllegalArgumentException e) {
                System.err.println("I don't know this sign: " + input);
                System.err.println("Try again...");
            }
        }
    }*/

 /*   public void update(TurnView model ) {
        /*switch (arg) {
            case CPU_CHOICE -> showChoices(model);
            case OUTCOME -> {
                showOutcome(model);
                this.setState(State.WAITING_FOR_PLAYER);
            }
            default -> System.err.println("Ignoring event from " + model + ": " + arg);
        }
    }*/

    /*private void showOutcome(TurnView model) {
        Outcome o = model.getOutcome();
        if (o == null) {
            return;
        }
        // Output Outcome
        switch (o) {
            case WIN -> System.out.println("You win! :)");
            case DRAW -> System.out.println("Draw... -.-");
            case LOSE -> System.out.println("You lose! :(");
        }
    }*/

    /*private void showChoices(TurnView model) {
        Choice cpuChoice = model.getCpuChoice();
        if (cpuChoice == null) {
            return;
        }
        // Show CPU's choice
        System.out.println("CPU chose: " + cpuChoice);
    }

    */


}