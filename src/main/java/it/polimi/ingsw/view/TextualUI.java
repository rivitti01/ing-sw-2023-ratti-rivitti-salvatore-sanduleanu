package it.polimi.ingsw.view;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.Shelf;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.Scanner;

import static it.polimi.ingsw.Costants.SHELF_COLUMN;
import static it.polimi.ingsw.Costants.SHELF_ROWS;
import static it.polimi.ingsw.model.Colors.*;
import static it.polimi.ingsw.model.Colors.ANSI_RESET;


public class TextualUI  implements  Runnable {

    private GameController controller;

    public TextualUI(GameController gc){
        this.controller = gc;
    }



    @Override
    public void run() {
        //noinspection InfiniteLoopStatement
        while (true) {
            System.out.println("--- WELCOME TO A NEW GAME OF 'MY_SHELFIE' :) ---");
            /* Player chooses */
            int n = askNumber();
            controller.setPlayerNumber(n);
            for (int i = 0; i < n; i++){
                String s = askNickName(i);
                controller.setPlayerNickname(s);
            }
            controller.play();
        }
    }

    public int askNumber() {
        while (true) {
            Scanner s = new Scanner(System.in);
            System.out.println("Type in the number of players that will take part in this game:");
            int input = s.nextInt();
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
            System.out.println("Plyer " + index + ", choose your nickname for the game:");
            String nickName = s.next();
            if (nickName.length() == 0){
                System.err.println("Sorry there was something wrong with your nickname :(");
                System.err.println("Please try again:");
            } else {
                return nickName;
            }
        }
    }

    public static class ShelfChange implements ChangeListener{
        private Shelf s;
        @Override
        public void stateChanged(ChangeEvent e) {
            s = (Shelf)e.getSource();
            for (int i = 0; i < SHELF_ROWS; i++) {
                System.out.println("  ");
                for (int j = 0; j < SHELF_COLUMN; j++) {
                    if(j==0)
                        System.out.print("|");
                    if (this.s.getTile(i, j) == null)
                        System.out.print("  " + "|");
                    else {
                        if (this.s.getTile(i, j).getColor() == Color.WHITE)
                            System.out.print(ANSI_WHITE_BACKGROUND + "  " + ANSI_RESET + "|");
                        else if (this.s.getTile(i, j).getColor() == Color.YELLOW)
                            System.out.print(ANSI_YELLOW_BACKGROUND + "  " + ANSI_RESET + "|");
                        else if (this.s.getTile(i, j).getColor() == Color.BLUE)
                            System.out.print(ANSI_BLUE_BACKGROUND + "  " + ANSI_RESET + "|");
                        else if (this.s.getTile(i, j).getColor() == Color.GREEN)
                            System.out.print(ANSI_GREEN_BACKGROUND + "  " + ANSI_RESET + "|");
                        else if (this.s.getTile(i, j).getColor() == Color.PINK)
                            System.out.print(ANSI_PURPLE_BACKGROUND + "  " + ANSI_RESET + "|");
                        else if (this.s.getTile(i, j).getColor() == Color.CYAN)
                            System.out.print(ANSI_CYAN_BACKGROUND + "  " + ANSI_RESET + "|");
                    }
                }
            }
            System.out.println("");
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

    public void update(/*TurnView model*/ ) {
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
        System.out.println("CPU chose: " + cpuChoice);*/
    }


}