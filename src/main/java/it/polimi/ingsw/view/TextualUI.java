package it.polimi.ingsw.view;

import it.polimi.ingsw.model.Game;

import it.polimi.ingsw.util.Observable_1;
import it.polimi.ingsw.util.Observer_1;

import java.util.Scanner;

public class TextualUI extends Observable_1 implements Observer_1, Runnable {




    @Override
    public void update(Observable_1 o, Object arg) {

    }

    private enum State {
        WAITING_FOR_PLAYER,
        WAITING_FOR_OUTCOME
    }

    private State state = State.WAITING_FOR_PLAYER;
    private final Object lock = new Object();

    private State getState() {
        synchronized (lock) {
            return state;
        }
    }

    private void setState(State state) {
        synchronized (lock) {
            this.state = state;
            lock.notifyAll();
        }
    }

    @Override
    public void run() {
        //noinspection InfiniteLoopStatement
        while (true) {
            while (getState() == State.WAITING_FOR_OUTCOME) {
                synchronized (lock) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        System.err.println("Interrupted while waiting for server: " + e.getMessage());
                    }
                }
            }
            System.out.println("--- WELCOME TO A NEW GAME OF 'MY_SHELFIE' :) ---");
            /* Player chooses */
            int n = askNumber();
            setChanged();
            notifyObservers(n);
            for (int i = 0; i < n; i++){
                String s = askNickName(i);
                setChanged();
                notifyObservers(s);
            }
            setState(State.WAITING_FOR_OUTCOME);
        }
    }

    public int askNumber() {
        while (true) {
            Scanner s = new Scanner(System.in);
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