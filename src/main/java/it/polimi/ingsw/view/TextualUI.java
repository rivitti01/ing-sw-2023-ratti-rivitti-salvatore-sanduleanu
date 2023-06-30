package it.polimi.ingsw.view;


import it.polimi.ingsw.model.*;
import it.polimi.ingsw.util.CurrentState;
import it.polimi.ingsw.util.Warnings;
import it.polimi.ingsw.util.ViewListener;

import java.rmi.RemoteException;
import java.util.*;

import static it.polimi.ingsw.util.Costants.*;
import static it.polimi.ingsw.view.Colors.*;



public class TextualUI implements UI {
    private ViewListener listener;
    private final Scanner scanner = new Scanner(System.in);
    private CurrentState previousState = null;
    private CurrentState currentState = null;

    /**
     * Notifies the player that a new turn has started.
     *
     * @param playing A boolean indicating if it's the player's turn to play.
     * @throws RemoteException If a remote communication error occurs.
     */
    public void newTurn(boolean playing) throws RemoteException {
        if(playing) {
            this.currentState = CurrentState.CHOOSING_ACTION;
            System.out.println("IT'S YOUR TURN");
            chooseAction();
        }else {
            this.currentState = CurrentState.WAITING_TURN;
            waitingTurn();
        }
    }

    /**
     * Resumes the player's turn or continues waiting for the turn.
     *
     * @param playing A boolean indicating if it's the player's turn to play.
     */
    public void resumingTurn(boolean playing)   {
        if(playing)
            this.currentState = this.previousState;
        else
            waitingTurn();
    }

    /**
     * Asks the player to choose the order of dropping tiles.
     * This method sets the current state to choosing order.
     */
    @Override
    public void askOrder() {
        System.out.println("choose the tile to drop first: ");
        this.currentState = CurrentState.CHOOSING_ORDER;
    }

    /**
     * Handles the last turn of the game for the player.
     * If the player is currently playing, it sets the current state to choosing action
     * and prompts the player to choose an action.
     * If the player is not playing, it sets the current state to waiting turn.
     *
     * @param playing true if the player is currently playing, false otherwise.
     * @throws RuntimeException if an error occurs during the method execution.
     */
    public void lastTurn(boolean playing){
        try {
            if(playing) {
                this.currentState = CurrentState.CHOOSING_ACTION;
                System.out.println("IT'S YOUR LAST TURN!");
                chooseAction();
            }else {
                waitingTurn();
                this.currentState = CurrentState.WAITING_TURN;
            }
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Enters the chat mode for the player.
     * Sets the current state to chatting and prompts the player to send a message.
     * Messages can be sent to all players or privately by starting the message with "@nickname".
     */
    public void chat(){
        this.currentState = CurrentState.CHATTING;
        System.out.println("Send a message to someone (begin with @nickname to send it privately)");
    }

    /**
     * Prompts the player to choose a column to drop their tiles.
     * Sets the current state to choosing column and provides a message with instructions.
     */
    public void askColumn() {
        this.currentState = CurrentState.CHOOSING_COLUMN;
        System.out.println("Select a valid column to drop your tiles");
    }

    /**
     * Sets the current state to choosing action.
     * Allows the player to choose an action to perform during their turn.
     */
    public void chooseAction() throws RemoteException {
        this.currentState = CurrentState.CHOOSING_ACTION;

    }

    /**
     * Checks the player's input and updates the current state accordingly.
     * Performs different actions based on the input provided by the player.
     *
     * @param input the player's input
     * @throws RemoteException if a remote communication error occurs
     */
    public void checkAction(String input) throws RemoteException {
        switch (input.toUpperCase()) {
            case "Q" -> {
                this.currentState = CurrentState.CHOOSING_COLUMN;
                listener.endsSelection();
            }
            case "S" -> {
                this.currentState = CurrentState.CHOOSING_TILE;
                System.out.println("Select a tile :");
            }
            case "CHAT" -> {
                this.currentState = CurrentState.CHATTING;
                chat();
            }
            default -> System.out.println(ANSI_RED_BACKGROUND + "I don'know this command.\nTry again..." + ANSI_RESET);
        }
    }

    /**
     * Prompts the user to enter the number of players that will take part in the game.
     * Reads the input from the user and validates it.
     * If the input is a valid number of players (between 2 and 4), it calls the listener's
     * {@code numberPartecipantsSetting()} method to handle the setting of the number of players.
     * If the input is invalid, error messages are displayed and the user is prompted again.
     *
     * @throws RemoteException if a remote communication error occurs
     */
    public void askNumber() throws RemoteException {
        System.out.println("Type in the number of players that will take part in this game:");
        int input;
        while (true) {
            if (scanner.hasNextInt()) {
                input = scanner.nextInt();
                if (input < MIN_NUMBER_PLAYERS || input > MAX_NUMBER_PLAYERS){
                    System.out.println(ANSI_RED_BACKGROUND + "Sorry you cannot play with this much players :(" + ANSI_RESET);
                    System.out.println(ANSI_RED_BACKGROUND + "Please enter a number in between 2 and 4:" + ANSI_RESET);
                }else {
                    break;
                }
            } else {
                System.out.println(ANSI_RED_BACKGROUND + "Enter a valid value" + ANSI_RESET);
                scanner.nextLine(); // Consuma il valore non intero inserito
            }
        }
        this.listener.numberPartecipantsSetting(input);
    }

    /**
     * Prompts the user to choose a nickname for the game.
     * Reads the input from the user and validates it.
     * If the input is a valid nickname (not empty), it calls the listener's
     * {@code clientNickNameSetting()} method to handle the setting of the nickname.
     * If the input is invalid (empty), error messages are displayed and the user is prompted again.
     *
     * @throws RemoteException if a remote communication error occurs
     */
    public void askNickName() throws RemoteException {
        boolean validName = false;
        String nickName = null;
        while (!validName) {
            System.out.println("Choose your nickname for the game:");
            nickName = scanner.next();
            scanner.nextLine();
            if (nickName.length() == 0){
                System.out.println(ANSI_RED_BACKGROUND + "Sorry there was something wrong with your nickname :(" + ANSI_RESET);
                System.out.println(ANSI_RED_BACKGROUND + "Please try again:" + ANSI_RESET);
            } else {
                validName=true;
            }
        }
        listener.clientNickNameSetting(nickName);
    }

    /**
     * Prompts the user who was disconnected from a game to enter their old nickname.
     * Reads the input from the user and validates it.
     * If the input is a valid nickname (not empty), it calls the listener's
     * {@code checkingExistingNickname()} method to handle the validation of the nickname.
     * If the input is invalid (empty), error messages are displayed and the user is prompted to enter a valid nickname.
     *
     * @throws RuntimeException if a runtime exception occurs while handling the existing nickname check
     */
    public void askExistingNickname(){
        boolean validName = false;
        String nickName = null;
        while (!validName) {
            System.out.println("A game is already going on. If you were disconnected please type your old nickname");
            nickName = scanner.next();
            scanner.nextLine();
            if (nickName.length() == 0){
                System.out.println(ANSI_RED_BACKGROUND + "nickname cannot be empty!" + ANSI_RESET);
                System.out.println(ANSI_RED_BACKGROUND + "Please try again:" + ANSI_RESET);
            } else {
                validName=true;
            }
        }
        try {
            this.listener.checkingExistingNickname(nickName);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Handles different types of warnings received from the server.
     * Based on the type of warning, appropriate actions are taken.
     * The method throws a {@code RemoteException} if a remote exception occurs while handling the warning.
     *
     * @param e the type of warning received from the server
     * @throws RemoteException if a remote exception occurs while handling the warning
     */
    public void warning(Warnings e) throws RemoteException {
        switch (e){
            case INVALID_TILE -> {
                System.out.println("The selected tile cannot be chosen. Choose another one: ");
                try {
                    chooseAction();
                } catch (RemoteException ex) {
                    throw new RuntimeException(ex);
                }
            }
            case INVALID_NICKNAME -> {
                System.out.println(ANSI_RED_BACKGROUND + "This nickname is already taken, choose another one: " + ANSI_RESET);
                askNickName();
            }
            case INVALID_COLUMN -> {
                System.out.println(ANSI_RED_BACKGROUND + "Wrong column selected, please try again:" + ANSI_RESET);
                this.currentState = CurrentState.CHOOSING_COLUMN;
                askColumn();
            }
            case INVALID_ACTION -> {
                System.out.println(ANSI_RED_BACKGROUND + "Please choose at least one tile:" + ANSI_RESET);
                this.currentState = CurrentState.CHOOSING_ACTION;
                try {
                    chooseAction();
                } catch (RemoteException ex) {
                    throw new RuntimeException(ex);
                }
            }
            case GAME_ALREADY_STARTED -> System.out.println(ANSI_RED_BACKGROUND + "A game has already started, you cannot play. Sorry :(" + ANSI_RESET);
            case MAX_TILES_CHOSEN -> {
                this.currentState = CurrentState.CHOOSING_COLUMN;
                System.out.println("You reached the max number of chosen tiles");
                askColumn();
            }
            case INVALID_ORDER -> {
                System.out.println(ANSI_RED_BACKGROUND + "This tile cannot be chosen to be dropped. Please try again..." + ANSI_RESET);
                this.currentState = CurrentState.CHOOSING_ORDER;
                askOrder();
            }
            case WAIT, OK_JOINER -> System.out.println("Loading. Wait...");
            case INVALID_CHAT_MESSAGE -> System.out.println(ANSI_RED_BACKGROUND + """
                    Invalid message!
                    Type a public message
                    or type |@nickname| |message to send privately|""" + ANSI_RESET);
            case IVALID_RECEIVER -> System.out.println(ANSI_RED_BACKGROUND + "Are you sure this nickname exist? Maybe you misspelled it :(" + ANSI_RESET);
            case YOUR_TURN -> newTurn(true);
            case NOT_YOUR_TURN -> newTurn(false);
            case CORRECT_CORD -> {}
            case CONTINUE_TO_CHOOSE -> chooseAction();
            case ASK_COLUMN -> askColumn();
            case ASK_ORDER -> askOrder();
            case SET_NUMBER_PLAYERS, INVALID_NUMBER_PLAYERS -> askNumber();
            case ASK_NICKNAME -> askNickName();
            case CLIENT_DISCONNECTED -> System.out.println(ANSI_RED_BACKGROUND + "One player has disconnected from the game" + ANSI_RESET);
            case WAITING_FOR_MORE_PLAYERS -> {
                System.out.println(ANSI_RED_BACKGROUND + "Waiting for more players to continue the game..." + ANSI_RESET);
                this.previousState = this.currentState;
                this.currentState = CurrentState.WAITING_FOR_CLIENTS;
            }
            case NO_PLAYERS_LEFT ->System.out.println("NO PLAYERS LEFT. YOU WON!");
            case RECONNECTION -> {
                this.currentState = CurrentState.WAITING_TURN;
                System.out.println("You have been RE-connected. Please wait for your turn");
                openScanner();
            }
            case INVALID_RECONNECTION_NICKNAME -> askExistingNickname();
            case CLIENT_RECONNECTED -> System.out.println("A player Re-connected to the game");
        }
    }

    /**
     * Notifies that the last turn has been reached for a player.
     *
     * @param nickname the nickname of the player who completed their shelf
     */
    public void lastTurnReached(String nickname){
        System.out.println(nickname + " completed their shelf\nBeginning last turn...");
    }

    /**
     * Prints the game board with the given Board object.
     *
     * @param b the Board object representing the game board
     */
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
        System.out.println("\t\t\t\t\t\t\t\t 3 adjacency: 2 POINTS;\t\t\t 5 adjacency: 5 POINTS");
        System.out.println("\t\t\t\t\t\t\t\t 4 adjacency: 3 POINTS;\t\t\t 5+ adjacency: 8 POINTS");
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

    /**
     * Prints the shelves of players with their corresponding tiles.
     *
     * @param playerShelves a map containing the player names as keys and their Shelf objects as values
     */
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

    /**
     * Prints the personal goal shelf of a player.
     *
     * @param personalGoalCard the personal goal card of the player
     */
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

    /**
     * Prints the chosen tiles by a player.
     *
     * @param chosenTiles the list of chosen tiles
     * @param nickname    the nickname of the player
     */
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

    /**
     * Prints the final points chart at the end of the game.
     *
     * @param chart a map containing player names as keys and their corresponding points as values
     * @param winnerNickname the nickname of the winner
     */
    public void printFinalPoints(Map<String, Integer> chart, String winnerNickname){
        System.out.println("******************** GAME ENDED ***************************************");
        System.out.println();
        System.out.println("                    FINAL POINTS                   ");

        for(String s : chart.keySet()){
            System.out.println("--------------------------------------------------");
            System.out.println(s + ":" + chart.get(s));
        }
        System.out.println("--------------------------------------------------");
        System.out.println("THE WINNER IS: " + winnerNickname);
    }

    /**
     * Sets the listener for the view.
     *
     * @param l the ViewListener to be set as the listener for the view
     */
    public void addListener (ViewListener l){
        this.listener = l;
    }

    /**
     * Prints the game view, including the chat, common goal cards, board, shelves, personal goal shelf,
     * chosen tiles, and current turn information.
     *
     * @param gameView the GameView object representing the game state to be printed
     * @throws RuntimeException if there is a remote exception when accessing the chat view or handling options
     */
    public void printGame(GameView gameView){
        try {
            this.printChat(gameView.getChatView());
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        System.out.println("******************* COMMON GOAL CARDS *************************************");
        System.out.println(1 + ") " + gameView.getCommonGoals()[0]);
        for(int j=gameView.getCommonGoal1().size()-1; j>=0; j--)
            System.out.print("[ " + gameView.getCommonGoal1().get(j) + " ]" + "    ");
        System.out.println("\n");
        System.out.println(2 + ") " + gameView.getCommonGoals()[1]);
        for(int j=gameView.getCommonGoal2().size()-1; j>=0; j--)
            System.out.print("[ " + gameView.getCommonGoal2().get(j) + " ]" + "    ");
        System.out.println();
        System.out.println();



        System.out.println("******************* BOARD **************************************");
        printBoard(gameView.getBoard());
        System.out.println("******************* SHELVES **************************************");
        printShelves(gameView.getPlayersShelves());

        System.out.println("************ Your Points: " + gameView.getPoints() + "        ***********\n");
        printPersonalGoalShelf(gameView.getPersonal());

        printChosenTiles(gameView.getChosenTiles(), gameView.getNickName());
        System.out.println("It's " + gameView.getNickName() + "'s turn");
        if(!gameView.isYourTurn())
            System.out.println("It's not your turn.\nYou can type [chat] to write something in the chat.");
        else {
            try {
                options();
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Prints the chat messages from the provided ChatView.
     *
     * @param chatView the ChatView object representing the chat messages to be printed
     * @throws RemoteException if there is a remote exception while accessing the chat messages
     */
    public void printChat(ChatView chatView) throws RemoteException {
        System.out.println("******************* CHAT **************************************");
        List<String> chat = chatView.getChat();
        for(String message : chat)
            System.out.println(message);
    }

    /**
     * Sets the current state to waiting for the player's turn.
     */

    public void waitingTurn() {
        this.currentState = CurrentState.WAITING_TURN;
    }


    /**
     * Notifies the client that the game has started.
     *
     * @param yourTurn a boolean indicating whether it is the client's turn or not
     */

    @Override
    public void gameStarted(boolean yourTurn) {
        if(yourTurn) {
            try {
                chooseAction();
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }else
            waitingTurn();
        openScanner();
    }

    private void openScanner(){
        Thread inputThread = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            CurrentState oldState = null;
            while (true) {
                String input = scanner.nextLine();
                if (input.length() > 0) {
                    if (this.currentState != CurrentState.WAITING_FOR_CLIENTS && input.equals("chat")) {
                        oldState = this.currentState;
                        this.currentState = CurrentState.CHATTING;
                        System.out.println("Send a message to someone (begin with @nickname to send it privately)");
                    } else {
                        try {
                            switch (this.currentState) {
                                case CHOOSING_ACTION -> checkAction(input);
                                case WAITING_TURN -> System.out.println(ANSI_RED_BACKGROUND + "It's not your turn.\nYou can type [chat] to write in the chat." + ANSI_RESET);
                                case CHATTING -> {
                                    this.currentState = oldState;
                                    this.listener.newMessage(input);
                                }
                                case CHOOSING_TILE -> {
                                    try {
                                        // Split the input string into an array of strings
                                        String[] parts = input.split(" ");

                                        // Extract the numbers from the array of strings
                                        int number1 = Integer.parseInt(parts[0]);
                                        int number2 = Integer.parseInt(parts[1]);
                                        int[] coordinates = {number1, number2};
                                        this.currentState = CurrentState.CHOOSING_ACTION;
                                        this.listener.checkingCoordinates(coordinates);
                                    } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                                        System.out.println(ANSI_RED_BACKGROUND + "You chose invalid coordinates\nPlease try again..." + ANSI_RESET);
                                    }
                                }
                                case CHOOSING_COLUMN -> {
                                    try {
                                        int column = Integer.parseInt(input);
                                        this.listener.columnSetting(column);
                                    } catch (NumberFormatException e) {
                                        System.out.println(ANSI_RED_BACKGROUND + "You must insert a column. Please try again..." + ANSI_RESET);
                                    }
                                }
                                case CHOOSING_ORDER -> {
                                    try {
                                        int tilePosition = Integer.parseInt(input);
                                        listener.tileToDrop(tilePosition);
                                    } catch (NumberFormatException e) {
                                        System.out.println(ANSI_RED_BACKGROUND + "You must insert a position. Please try again..." + ANSI_RESET);
                                    }
                                }
                                case WAITING_FOR_CLIENTS -> System.out.println(ANSI_RED_BACKGROUND + "Waiting for more players to continue the game..." + ANSI_RESET);
                            }
                        } catch (RemoteException e) {
                            handleRemoteException();
                        }
                    }
                } else
                    System.out.println(ANSI_RED_BACKGROUND + "Empry String! Try again..." + ANSI_RESET);
            }

        });
        inputThread.start();
    }

    private void handleRemoteException(){
        System.out.println(ANSI_RED_BACKGROUND + "Server has crushed! Exiting..." + ANSI_RESET);
        System.exit(1);
    }

    private void options() throws RemoteException {
        if(this.currentState != null) {
            switch (this.currentState) {
                case CHOOSING_ACTION -> System.out.println("""
                [S] : Select an available tile
                [Q] : Quit selecting tiles
                [chat] Chat with the connected players""");
                case CHOOSING_ORDER -> askOrder();
                case CHOOSING_COLUMN -> askColumn();
                case WAITING_TURN -> waitingTurn();
                case CHATTING -> chat();
                case CHOOSING_TILE -> System.out.println("Choose the tile's coordinates (format: x y)");
            }
        }
    }

    /**
     * Notifies the client that a player has reconnected to the game.
     *
     * @param nickname the nickname of the player who reconnected
     */
    public void clientReconnected(String nickname){
        System.out.println(nickname + " RE-connected to the game.");
    }

    /**
     * Notifies the client that a player has disconnected from the game.
     *
     * @param nickname the nickname of the player who disconnected
     */
    public void clientDisconnected(String nickname){
        System.out.println(nickname + " disconnected from the game.");
    }

}