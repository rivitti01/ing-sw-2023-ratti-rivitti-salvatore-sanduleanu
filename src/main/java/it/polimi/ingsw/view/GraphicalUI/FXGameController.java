package it.polimi.ingsw.view.GraphicalUI;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.util.CurrentState;
import it.polimi.ingsw.util.ViewListener;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.*;
import java.util.stream.Collectors;
import static it.polimi.ingsw.util.Costants.*;
public class FXGameController {

    /**
     * Constructs a new FXGameController with the specified ViewListener.
     *
     * @param listener the ViewListener object to be associated with the controller
     */
    public FXGameController(ViewListener listener) {
        this.tempClient = listener;
    }

    private int columnNumber;

    private final ViewListener tempClient;
    private ViewListener client;

    public GameView model;
    private StringBuilder fieldContent = new StringBuilder();
    private TurnState turn;
    private CurrentState currentState;
    private String playerNick;

    private CurrentState previousState;
    private boolean started;


    private Node[][] tmpMatrix = new Node[6][5];
    private List <PlayerObjects> playersObjects;
    private BufferedImage[] tileImages = new BufferedImage[18];
    private Node[][] playerMatrix = new Node[6][5];
    private Node[][] boardMatrix = new Node[9][9];
    private boolean isEnding = false;

    /**
     * Represents the possible states or phases of a turn in a game.
     */
    public enum TurnState {
        OPPONENT, PLAYER, LAST_ROUND, LAST_PLAYER
    }

    /**
     * Initializes the game controller by setting up the necessary components and variables.
     * This method should be called before starting the game.
     */
    public void initialize() {
        client = tempClient;
        numberBox.setItems(numberList);
        chatArea.setText("Start the message with @nickname to send a message to a specific player.");
    }

    @FXML
    private BorderPane gamePane;
    @FXML
    private GridPane waitingPane;

    @FXML
    private Text bottomText;

    @FXML
    private TextArea chatArea;

    @FXML
    private TextField chatField;

    @FXML
    private Label chatErrorLabel;

    @FXML
    private ImageView firstCommonView;

    @FXML
    private Text firstOpponentName;

    @FXML
    private GridPane firstOpponentShelf;

    @FXML
    private ImageView firstOpponentToken;

    @FXML
    private GridPane gameBoard;

    @FXML
    private ImageView personalGoal;

    @FXML
    private Text playerName;

    @FXML
    private GridPane playerShelf;

    @FXML
    private ImageView playerToken;

    @FXML
    private ImageView secondCommonView;

    @FXML
    private Text secondOpponentName;

    @FXML
    private GridPane secondOpponentShelf;

    @FXML
    private ImageView secondOpponentToken;

    @FXML
    private GridPane thirdOpponentShelf;

    @FXML
    private ImageView thirdOpponentToken;
    @FXML
    private Text thirdOpponentName;

    @FXML
    private Text topText;
    @FXML
    private Label playerPoints;
    @FXML
    private Text firstPoints;
    @FXML
    private Text secondPoints;
    @FXML
    private Text thirdPoints;

    @FXML
    private ImageView firstChosenTile;
    @FXML
    private ImageView secondChosenTile;
    @FXML
    private ImageView thirdChosenTile;



    /**
     * Sends a message when the ENTER key is pressed in the chatField.
     * The message is retrieved from the chatField and sent using the client object.
     * After sending the message, the chatField is cleared.
     *
     * @param event the KeyEvent object representing the key event
     */
    @FXML
    void sendMessage(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            try {
                client.newMessage(chatField.getText());
                chatField.clear();
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Handles the event when a tile is chosen by a mouse click.
     * If it is the player's turn and the current state is either CHOOSING_TILE or CHOOSING_ACTION,
     * the method retrieves the coordinates of the clicked tile and sends them to the client for further processing.
     *
     * @param event the MouseEvent object representing the mouse click event
     */
    @FXML
    void chosenTile(MouseEvent event){
            if (turn == TurnState.PLAYER && (currentState==CurrentState.CHOOSING_TILE) || (currentState==CurrentState.CHOOSING_ACTION)) {
                try {
                    int[] coordinates = new int[2];
                    int offsetColumn = (GridPane.getColumnIndex((Node) event.getSource()) - 1) / 2;
                    int offsetRow = (GridPane.getRowIndex((Node) event.getSource()) - 1) / 2;
                    coordinates[0] = (GridPane.getRowIndex((Node) event.getSource()) - offsetRow - 1);
                    coordinates[1] = (GridPane.getColumnIndex((Node) event.getSource()) - offsetColumn - 1);
                    this.client.checkingCoordinates(coordinates);
                } catch (InputMismatchException | RemoteException e1) {
                    if (e1 instanceof RemoteException)
                        ((RemoteException) e1).printStackTrace();
                }
            }
    }

    /**
     * Handles the event when a column is chosen by a mouse click.
     * If it is the player's turn and the current state is either CHOOSING_COLUMN or CHOOSING_ACTION,
     * the method retrieves the chosen column number from the event and notifies the client that the selection ends.
     *
     * @param event the MouseEvent object representing the mouse click event
     */
    @FXML
    void chosenColumn(MouseEvent event) {
        if (turn == TurnState.PLAYER && (currentState == CurrentState.CHOOSING_COLUMN || currentState == CurrentState.CHOOSING_ACTION)) {
            try {
                int offsetX = (GridPane.getColumnIndex((Node) event.getSource()) - 1) / 2;
                this.columnNumber = GridPane.getColumnIndex((Node) event.getSource()) - offsetX - 1;
                client.endsSelection();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }



    /**
     * Handles the event when an order is chosen by a mouse click.
     * If it is the player's turn, the current state is CHOOSING_ORDER, and the clicked order has an associated image,
     * the method determines which order was chosen, notifies the client, and updates the corresponding chosen tile's image to null.
     *
     * @param event the MouseEvent object representing the mouse click event
     */
    @FXML
    void chosenOrder(MouseEvent event){
        ImageView tmp = (ImageView)event.getSource();
        if(turn == TurnState.PLAYER && currentState==CurrentState.CHOOSING_ORDER && tmp.getImage()!=null) {
            if(tmp==firstChosenTile && firstChosenTile.getImage()!=null) {
                try {
                    client.tileToDrop(1);
                    Platform.runLater(()-> firstChosenTile.setImage(null));
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }

            if(tmp == secondChosenTile && secondChosenTile.getImage()!=null){
                try {
                    client.tileToDrop(2);
                    Platform.runLater(()-> secondChosenTile.setImage(null));
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }

            if(tmp == thirdChosenTile && thirdChosenTile.getImage()!=null){
                try {
                    client.tileToDrop(3);
                    Platform.runLater(()-> thirdChosenTile.setImage(null));
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void setMatrix(GridPane grid, Node[][] matrix) {
        for (Node node : grid.getChildren()) {
            if (node instanceof ImageView) {
                int offsetY = (GridPane.getRowIndex(node) - 1) / 2;
                int offsetX = (GridPane.getColumnIndex(node) - 1) / 2;
                matrix[GridPane.getRowIndex(node) - offsetY -1][GridPane.getColumnIndex(node) - offsetX -1] = node;
            }
        }
    }


    /**
     * Prints the game view based on the provided GameView object.
     * If the model is null, initializes the graphic objects, starts the game, sets the game scene,
     * and prints the player's token asynchronously using the game view's nickname.
     * If the current state is WAITING_TURN, clears the bottomText.
     * Prints the player's points if they are nonzero.
     * Prints the common goals if the model is null or the name goals have changed.
     * Prints the board if the model is null or the board has changed.
     * Prints the players' shelves if the model is null or the shelves have changed.
     * Prints the personal goal shelf if the model is null or the personal goal has changed.
     * Prints the chosen tiles if the model is null or the chosen tiles or the nickname have changed.
     * Prints the chat if the model is not null.
     * If the model is null, makes the necessary UI components visible.
     *
     * @param gameView the GameView object representing the current state of the game
     */
    public void printGame(GameView gameView) {
        if(model==null) {
            initializeGraphicObjects();
            startGame();
            setGameScene(gameView);
            Platform.runLater(()-> {
                printToken(gameView.getNickName());
            });
        }

        if(currentState==CurrentState.WAITING_TURN)
            Platform.runLater(()-> bottomText.setText(""));

        if (gameView.getPoints() != 0)
            printPlayerPoints(gameView.getPoints());
        if (model == null || model.getNameGoals() != gameView.getNameGoals())
            printCommonGoals(gameView.getNameGoals());
        if (model == null ||  model.getBoard() != gameView.getBoard())
            printBoard(gameView.getBoard());
        if (model == null ||  model.getPlayersShelves() != gameView.getPlayersShelves())
            printShelves(gameView.getPlayersShelves());
        if (model == null ||  model.getPersonal() != gameView.getPersonal())
            printPersonalGoalShelf(gameView.getPersonal());
        if (model == null ||  model.getChosenTiles()!=gameView.getChosenTiles() || !(model.getNickName().equals(gameView.getNickName())))
            printChosenTiles(gameView.getChosenTiles(), gameView.getNickName());
        if (model!=null)
            printChat(gameView.getChatView());

        if(model==null)
            Platform.runLater(()->{
                entireLoginPane.setVisible(false);
                waitingPane.setVisible(false);
                gamePane.setVisible(true);
            });

        model = gameView;

    }

    /**
     * Prints the shelves for each player based on the provided playerShelves map.
     * For each player in the map, it retrieves the corresponding shelf matrix and shelf object.
     * It iterates over the shelf matrix and, if a tile exists at a specific position, updates the corresponding ImageView with the tile image.
     *
     * @param playerShelves a map representing the shelves of each player, with player nicknames as keys and Shelf objects as values
     */
    public void printShelves(Map<String, Shelf> playerShelves) {

        for (String s : playerShelves.keySet()) {
            if (s.equals(playerNick))
                tmpMatrix = playerMatrix;
            else {
                for (int i = 0; i < playerShelves.size()-1; i++) {
                        if (playersObjects.get(i).getName().equals(s)) {
                        tmpMatrix = playersObjects.get(i).getShelfMatrix();
                    }
                }
            }
            Shelf tmpShelf = playerShelves.get(s);

            for (int i = 0; i < SHELF_ROWS; i++) {
                for (int j = 0; j < SHELF_COLUMN; j++) {
                    if (tmpShelf.getTile(i, j) != null) {
                        int finalI = i;
                        int finalJ = j;
                        final ImageView tmpTileImage = ((ImageView) tmpMatrix[finalI][finalJ]);
                        Platform.runLater(() -> {
                            tmpTileImage.setImage(getTileImage(tmpShelf.getTile(finalI, finalJ)));
                        });
                    }
                }
            }
        }
    }

    private void printBoard(Board b) {
        for (int i = 0; i < b.getSize(); i++)
            for (int j = 0; j < b.getSize(); j++) {
                if (b.getTile(i, j).getColor() != Color.TRANSPARENT) {
                    int finalI = i;
                    int finalJ = j;
                    if (b.getTile(i, j).getColor() != null && b.getTile(i, j) != null) {
                        Platform.runLater(() -> {
                            Platform.runLater(() -> ((ImageView) boardMatrix[finalI][finalJ]).setImage(getTileImage(b.getTile(finalI, finalJ))));
                        });
                    }
                    if(b.getTile(i, j).getColor() == null || b.getTile(i, j) == null){
                        Platform.runLater(() -> ((ImageView) boardMatrix[finalI][finalJ]).setImage(null));
                    }
                }
            }
    }

    /**
     * Prints the chat messages based on the provided ChatView object.
     * Initializes a StringBuilder to store the contents of the chat field.
     * Iterates over each message in the chat view and appends it to the StringBuilder with a line break.
     * Updates the chatArea text field with the contents of the StringBuilder on the JavaFX application thread.
     *
     * @param chat the ChatView object representing the chat messages
     */
    public void printChat(ChatView chat) {
        fieldContent = new StringBuilder("");

        for (String message : chat.getChat()) {
            fieldContent.append("" + message + "\n");
        }
        Platform.runLater(() -> chatArea.setText(fieldContent.toString()));
    }

    private void printPersonalGoalShelf(PersonalGoalCard personal) {
        BufferedImage tmp = null;
        try {
            tmp = ImageIO.read(Objects.requireNonNull(this.getClass().getResourceAsStream("/images/personal goals/" + personal.getCardName() + "_personal.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedImage finalTmp = tmp;
        Platform.runLater(() -> personalGoal.setImage(SwingFXUtils.toFXImage(finalTmp, null)));
    }

    private void printCommonGoals(String[] goals) {
        BufferedImage c = null;
        for (int i = 0; i < COMMON_CARDS_PER_GAME; i++) {

            try {
                c = ImageIO.read(Objects.requireNonNull(this.getClass().getResourceAsStream("/images/common goals/" + goals[i] + ".jpg")));
            } catch (IOException e) {
                e.printStackTrace();
            }
            BufferedImage finalC = c;
            if (i == 0) Platform.runLater(() -> firstCommonView.setImage(SwingFXUtils.toFXImage(finalC, null)));
            if (i == 1) Platform.runLater(() -> secondCommonView.setImage(SwingFXUtils.toFXImage(finalC, null)));
        }
    }

    private void printChosenTiles(List<Tile> chosenTiles, String nickname) {
        if(chosenTiles.size() > 0 && currentState==CurrentState.WAITING_TURN)
            Platform.runLater(() -> bottomText.setText(nickname + " is choosing tile order: "));

        if (chosenTiles.size() == 0){
            Platform.runLater(() -> {
                firstChosenTile.setImage(null);
                firstChosenTile.setVisible(true);
                secondChosenTile.setVisible(false);
                secondChosenTile.setImage(null);
                thirdChosenTile.setVisible(false);
                thirdChosenTile.setImage(null);
            });
        }

        if (chosenTiles.size() == 1) {
            Platform.runLater(() -> {
                firstChosenTile.setImage(getTileImage(chosenTiles.get(0)));
                firstChosenTile.setVisible(true);
                secondChosenTile.setVisible(false);
                secondChosenTile.setImage(null);
                thirdChosenTile.setVisible(false);
                thirdChosenTile.setImage(null);
            });
        }

        if (chosenTiles.size() == 2) {
            Platform.runLater(() -> {
                firstChosenTile.setImage(getTileImage(chosenTiles.get(0)));
                firstChosenTile.setVisible(true);
                secondChosenTile.setVisible(true);
                secondChosenTile.setImage(getTileImage(chosenTiles.get(1)));
                thirdChosenTile.setVisible(false);
                thirdChosenTile.setImage(null);
            });
        }

        if (chosenTiles.size() == 3) {
            Platform.runLater(() -> {
                firstChosenTile.setImage(getTileImage(chosenTiles.get(0)));
                firstChosenTile.setVisible(true);
                secondChosenTile.setVisible(true);
                secondChosenTile.setImage(getTileImage(chosenTiles.get(1)));
                thirdChosenTile.setVisible(true);
                thirdChosenTile.setImage(getTileImage(chosenTiles.get(2)));
            });
        }
    }

    private Image getTileImage(Tile tile) {
        BufferedImage c = null;
        String tileColor = tile.getColor().toString().toLowerCase();
            try {
                c = ImageIO.read(Objects.requireNonNull(this.getClass().getResourceAsStream("/images/tiles/" + tile.getType() + tileColor + ".png")));
            } catch (IOException e) {
                e.printStackTrace();
            }

        return SwingFXUtils.toFXImage(c, null);
    }

    private void linkOpponentItems(List<PlayerObjects> playersObjects) {
        playersObjects.get(0).setToken(firstOpponentToken);
        playersObjects.get(0).setLabelNick(firstOpponentName);
        playersObjects.get(0).setLabelPoints(firstPoints);

        playersObjects.get(1).setToken(secondOpponentToken);
        playersObjects.get(1).setLabelNick(secondOpponentName);
        playersObjects.get(1).setLabelPoints(secondPoints);

        playersObjects.get(2).setToken(thirdOpponentToken);
        playersObjects.get(2).setLabelNick(thirdOpponentName);
        playersObjects.get(2).setLabelPoints(thirdPoints);

        playersObjects.get(0).setGrid(firstOpponentShelf);
        playersObjects.get(1).setGrid(secondOpponentShelf);
        playersObjects.get(2).setGrid(thirdOpponentShelf);

        setMatrix(firstOpponentShelf, playersObjects.get(0).getShelfMatrix());
        setMatrix(secondOpponentShelf, playersObjects.get(1).getShelfMatrix());
        setMatrix(thirdOpponentShelf, playersObjects.get(2).getShelfMatrix());
    }

    /**
     * Starts a new turn in the game based on the provided boolean value.
     * If the boolean value is true, sets the turn state to PLAYER and the current state to CHOOSING_TILE.
     * Updates the UI elements on the JavaFX application thread to reflect the player's turn.
     * If the boolean value is false, sets the turn state to OPPONENT and the current state to WAITING_TURN.
     * Updates the topText UI element on the JavaFX application thread to indicate that it's the opponent's turn.
     *
     * @param b a boolean value indicating whether it's the player's turn (true) or the opponent's turn (false)
     */
    public void newTurn(boolean b) {
        if(b) {
            turn = TurnState.PLAYER;
            currentState = CurrentState.CHOOSING_TILE;
            Platform.runLater(()->{
                topText.setText("It's your turn!");
                bottomText.setVisible(true);
                bottomText.setText("Choose at least 1 tile");
            });
        }
        else{
            turn = TurnState.OPPONENT;
            currentState = CurrentState.WAITING_TURN;
            Platform.runLater(()->topText.setText("Wait for your turn"));
        }
    }

    /**
     * Sets the last turn in the game based on the provided boolean value.
     * Updates the topText UI element on the JavaFX application thread to indicate that it's the player's last turn.
     * Invokes the newTurn method to start the last turn with the specified playing status.
     *
     * @param playing a boolean value indicating whether the player is still playing (true) or not (false) during the last turn
     */
    public void lastTurn(boolean playing) {
        Platform.runLater(()-> topText.setText("It's your last turn!!!"));
        newTurn(playing);
    }

    /**
     * Asks the player to choose the order of tiles during the game.
     * Updates the bottomText UI element on the JavaFX application thread based on the current state.
     * If the current state is CHOOSING_COLUMN, sets the bottomText to indicate that the column has been chosen correctly and the player should choose the tile to drop now.
     * If the current state is CHOOSING_ORDER, sets the bottomText to indicate that the player should continue choosing the next tile to drop into the column.
     * Updates the current state to CHOOSING_ORDER.
     */
    public void askOrder(){
        Platform.runLater(()->{
            if(currentState==CurrentState.CHOOSING_COLUMN)
                bottomText.setText("Column chosen correctly, choose the tile to drop now");
            if(currentState==CurrentState.CHOOSING_ORDER)
                bottomText.setText("Continue choosing the next tile to drop into the column");
        });
        currentState=CurrentState.CHOOSING_ORDER;

    }

    /**
     * Prints the turn state on the UI elements based on the current turn and the playing player's name.
     * Updates the topText and bottomText UI elements on the JavaFX application thread to reflect the current turn state.
     *
     * @param playingPlayer the name of the player whose turn it is
     */
    public void printTurnState(String playingPlayer) {
        switch (turn) {
            case PLAYER:
                Platform.runLater(() -> {
                    topText.setText("It's your turn");
                    bottomText.setText("Choose available tiles, then click on the column you want to fill");
                });
                break;
            case OPPONENT:
                Platform.runLater(() -> {
                    topText.setText("It's " + playingPlayer + "'s turn");
                    bottomText.setText("Wait for your turn");
                });
                break;
            case LAST_PLAYER:
                Platform.runLater(() -> {
                    topText.setText("It's your last turn");
                    bottomText.setText("Choose available tiles");
                });
                break;

            default:
                if (!playingPlayer.equals(playerNick))
                    Platform.runLater(() -> {
                        topText.setText("It's " + playingPlayer + "'s turn");
                        bottomText.setText("Last round starts from now!");
                    });
                else Platform.runLater(() -> {
                        topText.setText("It's your turn");
                        bottomText.setText("Last round starts from now!");
                    });
        }
    }

    /**
     * Prints the final points and announces the winner of the game based on the provided chart and the winner's name.
     * Updates the labelPoints UI elements for each player on the JavaFX application thread to display their respective points.
     * Updates the topText UI element to announce the winner of the game.
     *
     * @param chart a map containing the player names as keys and their corresponding points as values
     * @param won   the name of the player who won the game
     */
    public void printFinalPoints(Map<String, Integer> chart, String won) {

        for (String s : chart.keySet()) {
            for (int i = 0; i < chart.size()-1; i++) {
                if (s.equals(playerNick)) {
                    i--;
                    continue;
                }

                if (playersObjects.get(i).getName().equals(s)) {
                    int finalI = i;
                    Platform.runLater(() -> {
                        playersObjects.get(finalI).getLabelPoints().setText("Points: " + chart.get(s));
                        playersObjects.get(finalI).getLabelPoints().setVisible(true);
                    });
                }
            }
        }
        Platform.runLater(() -> topText.setText("GAME IS OVER!!! " + won + " won!"));
    }

    private void printPlayerPoints(int pnt){
        Platform.runLater(()->playerPoints.setText("Earned Points: " + pnt));
    }

    private class PlayerObjects {

        /**
         * Constructs a new PlayerObjects instance.
         * Initializes the instance variables for player information, shelf, seat, labelNick, points, and shelfPane.
         */
        public PlayerObjects(){
            nick = new String();
            shelf = new Node[6][5];
            seat = new ImageView();
            labelNick = new Text();
            points = new Text();
            shelfPane = new GridPane();
        }
        private String nick;
        private Node[][] shelf;
        private ImageView seat;
        private Text labelNick;
        private Text points;
        private GridPane shelfPane;

        public Node[][] getShelfMatrix() {
            return this.shelf;
        }

        public ImageView getSeat() {
            return this.seat;
        }

        public Text getLabelNick() {
            return this.labelNick;
        }

        public Text getLabelPoints() {
            return this.points;
        }

        public String getName() {
            return this.nick;
        }

        public GridPane getGrid(){
            return this.shelfPane;
        }
        public void setNick(String nickname) {
            this.nick = nickname;
        }

        public void setToken(ImageView seatToken) {
            this.seat = seatToken;
        }

        public void setLabelNick(Text label) {
            this.labelNick = label;
        }

        public void setShelfMatrix(Node[][] matrix) {
            this.shelf = matrix;
        }

        public void setLabelPoints(Text pointsLabel) {
            this.points = pointsLabel;
        }

        private void setGrid(GridPane shelf){
            shelfPane=shelf;
        }

    }

    /**
     * Sets the nickname of the player.
     *
     * @param playerName the nickname to set for the player
     */
    public void setPlayerNickname(String playerName){
        this.playerNick=playerName;
    }

    /**
     * Starts the game by linking opponent items, setting up the player matrix and game board matrices,
     * and marking the game as started.
     * This method should be called at the beginning of the game.
     */
    public void startGame(){

        linkOpponentItems(playersObjects);
        setMatrix(playerShelf, playerMatrix);
        setMatrix(gameBoard, boardMatrix);
        started=true;
    }

    /**
     * Sets up the game scene with the provided game model.
     *
     * @param model the game model containing the necessary information for setting up the scene
     */
    public void setGameScene(GameView model){
        final String finalPlayerNick = playerNick;
        Platform.runLater(() -> playerName.setText(finalPlayerNick));


        Set<String> playerNameSet = model.getPlayersShelves().keySet();
        List<String> nickList = playerNameSet.stream().collect(Collectors.toCollection(ArrayList::new));

        for(int i=0; i<model.getPlayers().size()-1; i++){
            if(nickList.get(i).equals(playerNick)) {
                nickList.remove(i);
                i--;
            }
            else {
                int finalI = i;
                Platform.runLater(()->{
                    playersObjects.get(finalI).getLabelNick().setVisible(true);
                    playersObjects.get(finalI).setNick(nickList.get(finalI));
                    playersObjects.get(finalI).getLabelNick().setText(nickList.get(finalI));
                    playersObjects.get(finalI).getSeat().setVisible(true);
                    playersObjects.get(finalI).getGrid().setVisible(true);
                    if(playersObjects.get(finalI).getName().equals(model.getPlayers().get(0)))
                        printToken(model.getPlayers().get(0));
                });
            }
        }
    }

    private void printToken(String firstPlayer){

        BufferedImage firstTmp = null;
        try {
            firstTmp = ImageIO.read(Objects.requireNonNull(this.getClass().getResourceAsStream("/images/firstplayertoken.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedImage finalFirstTmp = firstTmp;
        for (PlayerObjects p : playersObjects)
            if (p.getName()!=null && p.getName().equals(firstPlayer)) {
                Platform.runLater(() -> p.getSeat().setImage(SwingFXUtils.toFXImage(finalFirstTmp, null)));
                started = false;
            }
        if(started) {
            Platform.runLater(() -> playerToken.setImage(SwingFXUtils.toFXImage(finalFirstTmp, null)));
        }

    }

    /**
     * Handles the case when an invalid tile is chosen.
     * Updates the current state to CHOOSING_ACTION and displays an appropriate message in the bottom text area.
     * This method is called when a player selects an invalid tile during their turn.
     */
    public void invalidTile(){
        currentState=CurrentState.CHOOSING_ACTION;
        Platform.runLater(() -> bottomText.setText("Invalid tile, choose a proper tile or continue to column"));
    }

    /**
     * Handles the situation when the maximum number of tiles has been reached.
     * Updates the current state to CHOOSING_COLUMN and displays a message in the bottom text area.
     * This method is called when a player has reached the maximum number of tiles they can choose during their turn.
     */
    public void maxTiles(){
        Platform.runLater(() -> bottomText.setText("You cannot take more tiles, click a column to drop into"));
        currentState=CurrentState.CHOOSING_COLUMN;
    }

    /**
     * Handles the selection of the next action during a player's turn.
     * Updates the current state based on the number of chosen tiles and displays a corresponding message in the bottom text area.
     * This method is called when the player decides to choose the next action during their turn.
     *
     * @param next true if the player wants to choose the next action, false otherwise
     */
    public void chooseNext(boolean next){
        if(next) {
            if (model.getChosenTiles().size() == 0)
                currentState = CurrentState.CHOOSING_TILE;
            else {
                currentState = CurrentState.CHOOSING_ACTION;
                Platform.runLater(() -> bottomText.setText("Continue to choose tiles or click the column to drop into."));
            }
        }

        if(!next){
            if (model.getChosenTiles().size() == 0)
                currentState = CurrentState.CHOOSING_TILE;
            if (model.getChosenTiles().size()==1) {
                currentState = CurrentState.CHOOSING_ACTION;
                Platform.runLater(() -> bottomText.setText("Continue to choose tiles or click the column to drop into."));
            }
            if (model.getChosenTiles().size()==2) {
                currentState = CurrentState.CHOOSING_ACTION;
                Platform.runLater(() -> bottomText.setText("Continue to choose tiles or click the column to drop into."));
            }
        }
    }


    /**
     * Handles the selection of an invalid column during a player's turn.
     * Updates the current state to indicate that the player needs to choose another column and displays a corresponding message in the bottom text area.
     * This method is called when the player selects an invalid column to drop a tile into.
     */
    public void invalidColumn(){
        this.currentState = CurrentState.CHOOSING_COLUMN;
        Platform.runLater(() -> bottomText.setText("Not a valid column, choose another one"));
    }

    /**
     * Displays an error message indicating that the chat message is invalid.
     * The error message is shown temporarily for a few seconds and then hidden.
     * This method is called when an invalid chat message is attempted to be sent.
     */
    public void invalidChatMessage(){
        Thread t = new Thread() {
            public void run() {
                Platform.runLater(() -> {
                            chatErrorLabel.setText("invalid message");
                            chatErrorLabel.setVisible(true);
                        });
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                Platform.runLater(()-> chatErrorLabel.setVisible(false));
            }};
        t.start();
    }

    /**
     * Displays an error message indicating that the message receiver is invalid.
     * This method is called when an invalid receiver is specified for a chat message.
     * The error message is displayed immediately on the chat error label.
     */
    public void invalidReceiver(){
        Platform.runLater(() -> chatErrorLabel.setText("invalid message receiver"));
    }

    /**
     * Displays a reconnected message and updates the game state after a reconnection.
     * This method is called when a player is reconnected to the game after a disconnection.
     * The bottom text is updated to inform the player that they have been disconnected and need to wait for their turn.
     * The game state is set to "waiting for turn" to reflect the current state after reconnection.
     */
    public void reconnectedMessage(){
        Platform.runLater(() -> bottomText.setText("You have been disconnected. Please wait for your turn"));
        currentState=CurrentState.WAITING_TURN;
    }

    /**
     * Handles the situation when there are no players left in the game.
     * This method is called when the game has ended and there are no active players remaining.
     * It updates the top text to indicate that there are no players left and sets the bottom text to display a victory message for the current player.
     */
    public void noPlayersLeft(){
        Platform.runLater(() -> topText.setText("No players left"));
        Platform.runLater(() -> bottomText.setText("You won!"));
    }

    /**
     * Handles the situation when there are not enough players to continue the game.
     * This method is called when the game is waiting for additional players to join.
     * It saves the previous state of the game, sets the current state to waiting for clients, and updates the top and bottom texts to inform the user about the waiting status.
     */
    public void waitingToContinue(){
        previousState = currentState;
        this.currentState = CurrentState.WAITING_FOR_CLIENTS;
        Platform.runLater(() -> topText.setText("Not enough players"));
        Platform.runLater(() -> bottomText.setText("Waiting for more players to continue the game..."));
    }

    /**
     * Handles the situation when a player has disconnected from the game.
     * This method is called when a player disconnects, and it updates the chat area with a message indicating the disconnection.
     *
     * @param disconnectedPlayer The nickname of the disconnected player.
     */
    public void playerDisconnected(String disconnectedPlayer){
        String tempString = ("--- " + disconnectedPlayer + " has disconnected ---\n");
        fieldContent.append(tempString);
        Platform.runLater(() -> chatArea.setText(fieldContent.toString()));
    }

    /**
     * Handles the situation when a player has reconnected to the game after a disconnection.
     * This method is called when a player reconnects, and it updates the chat area with a message indicating the reconnection.
     *
     * @param reconnectedPlayer The nickname of the reconnected player.
     */
    public void playerReconnected(String reconnectedPlayer){
        String tempString = ("--- " + reconnectedPlayer + " has reconnected ---\n");
        fieldContent.append(tempString );
        Platform.runLater(() -> chatArea.setText(fieldContent.toString()));
    }

    /**
     * Handles the situation when the last turn of the game is reached.
     * This method is called when the game reaches its last turn, and it performs actions based on the shelf filler.
     *
     * @param shelfFiller The nickname of the player who filled their shelf.
     */
    public void lastTurnReached(String shelfFiller){
   //     Platform.runLater(()-> shelfCompletedToken.setVisible(true));
    //    if(shelfFiller.equals(playerNick))
   //         Platform.runLater(()->playerCompletedToken.setVisible(true));
    }

    /**
     * Resumes the game after a disconnection or interruption.
     * This method is called when the game is being resumed after a disconnection or interruption,
     * and it restores the game state based on the previous state.
     *
     * @param playing Indicates if the player is currently playing.
     */
    public void resuming(boolean playing){
        if(playing){
            switch(previousState) {
                case CHOOSING_ACTION -> {
                    currentState=CurrentState.CHOOSING_ACTION;
                    chooseNext(true);
                }
                case CHOOSING_COLUMN -> {
                    currentState=CurrentState.CHOOSING_COLUMN;
                    choosingColumn();
                }
                case CHOOSING_TILE -> {
                    currentState=CurrentState.CHOOSING_TILE;
                    Platform.runLater(() -> {
                        topText.setText("It's your turn!");
                        bottomText.setText("Choose at least 1 tile");
                    });
                }
                case CHOOSING_ORDER -> choosingOrder();
            }
        }
    }

    /**
     * Notifies the server about the column chosen by the player.
     * This method is called when the player has chosen a column to drop their tiles into.
     * It sends the selected column number to the server through the client interface.
     *
     * @throws RuntimeException if there is an error in the remote communication with the server.
     */
    public void choosingColumn(){
        try {
            client.columnSetting(this.columnNumber);
        } catch (RemoteException e) {
            System.err.println("Errore in choosing Column GUI side");
            throw new RuntimeException(e);
        }
    }

    /**
     * Sets the current state to CHOOSING_ORDER and updates the GUI.
     * This method is called when the player has chosen a column and needs to select the tile to drop into the column.
     * It updates the bottom text in the GUI to prompt the player to choose the tile.
     */
    public void choosingOrder(){
        currentState=CurrentState.CHOOSING_ORDER;
        Platform.runLater(()->{
            bottomText.setText("Column Chosen! Choose the tile to drop now");
        });

    }

    /**
     * Sets the current state to CHOOSING_ORDER and updates the GUI.
     * This method is called when the player has clicked an invalid tile while choosing the order of tiles to drop.
     * It updates the bottom text in the GUI to display an error message and prompt the player to try again.
     */
    public void invalidOrder(){
        currentState=CurrentState.CHOOSING_ORDER;
        Platform.runLater(()->bottomText.setText("Invalid chosen tile clicked, try again"));
    }

    /**
     * Sets the current state to CHOOSING_TILE and updates the GUI.
     * This method is called when the player has selected a column without any tiles to drop.
     * It updates the bottom text in the GUI to display an error message and prompt the player to choose at least 1 tile first.
     */
    public void columnWithoutTiles(){
        currentState=CurrentState.CHOOSING_TILE;
        Platform.runLater(()->bottomText.setText("No tiles to drop to the column, choose at least 1 tile first"));
    }




    private void initializeGraphicObjects(){

        PlayerObjects firstPlayerObjs = new PlayerObjects();
        PlayerObjects secondPlayerObjs = new PlayerObjects();
        PlayerObjects thirdPlayerObjs = new PlayerObjects();
        playersObjects = new ArrayList<>();
        playersObjects.add(firstPlayerObjs);
        playersObjects.add(secondPlayerObjs);
        playersObjects.add(thirdPlayerObjs);

    }



    ////////////LOGIN//////////////////
    private LoginState loginState;
    private final ObservableList<Integer> numberList = FXCollections.observableArrayList(2,3,4);

    @FXML
    private StackPane entireLoginPane;
    @FXML
    private Button enterButton;
    @FXML
    private Button joinButton;
    @FXML
    private TextField nicknameField;
    @FXML
    private Text nickLabel;
    @FXML
    private GridPane nicknamePane;
    @FXML
    private GridPane waitingLoginPane;
    @FXML
    private Label waitingLabel;
    @FXML
    private Label askNumberLabel;

    @FXML
    private ChoiceBox<Integer> numberBox;

    @FXML
    Pane startPane;
    @FXML
    Pane numberPane;

    @FXML
    void exit(ActionEvent event) {
        System.exit(0);
    }

    /**
     * Handles the "Play" button event.
     * This method is called when the "Play" button is clicked.
     * It controls the visibility of different panes based on the login state.
     *
     * @param event The event triggered by clicking the "Play" button.
     */
    @FXML
    void play(ActionEvent event) {
        startPane.setVisible(false);
        switch (loginState) {
            case ASK_NUMBER -> numberPane.setVisible(true);
            case DEFAULT_NICK -> nicknamePane.setVisible(true);
            case WAITING_FOR_NUMBER -> {
                waitingLoginPane.setVisible(true);
                waitingLabel.setText("Host is creating the game. Wait...");
            }
            case GAME_ALREADY_STARTED -> {
                waitingLoginPane.setVisible(true);
                waitingLabel.setText("Game is full, you cannot play. Sorry :(");
            }
        }
    }

    /**
     * Handles the event when the number of participants is sent.
     * This method is called when the "Send" button is clicked after selecting the number of participants.
     * It validates the selected number and updates the GUI accordingly.
     *
     * @param event The event triggered by clicking the "Send" button.
     * @throws RemoteException if there is a remote communication error.
     */
    @FXML
    void sendNumber(ActionEvent event) throws RemoteException {

        if(numberBox.getValue()==2 || numberBox.getValue()==3 || numberBox.getValue()==4) {
            numberPane.setVisible(false);
            nicknamePane.setVisible(true);
            client.numberPartecipantsSetting(numberBox.getValue());
        }
        else Platform.runLater(()->askNumberLabel.setText("Choice a number before entering"));
    }

    /**
     * Handles the event when the user joins the game.
     * This method is called when the "Join Game" button is clicked.
     * It validates the nickname entered by the user and performs the necessary actions to join the game.
     *
     * @param event The event triggered by clicking the "Join Game" button.
     * @throws RemoteException if there is a remote communication error.
     */
    @FXML
    void joinGame(ActionEvent event) throws RemoteException {
        if(loginState==LoginState.RECONNECTING){
            setPlayerNickname(nicknameField.getText());
            client.checkingExistingNickname(nicknameField.getText());

        }
        else {
            if(nicknameField.getText().equals(""))
                Platform.runLater(()->nickLabel.setText("Empty nickname, try again."));

            else {
                client.clientNickNameSetting((nicknameField.getText()));
                setPlayerNickname(nicknameField.getText());
            }
        }
    }

    /**
     * Sets the player number based on the provided boolean value.
     * This method is called to set the login state to ask for the player number.
     *
     * @param number A boolean value indicating whether the player number is requested or not.
     */
    public void setPlayerNumber(boolean number) {
        loginState=LoginState.ASK_NUMBER;
    }

    /**
     * Displays an error message indicating that the entered nickname is already chosen by another player.
     * This method is called when the entered nickname is invalid.
     */
    public void invalidNickname(){
        Platform.runLater(() -> nickLabel.setText("Nickname already chosen, try again"));
    }

    /**
     * Displays an error message indicating that the reconnection nickname is already used or there wasn't a player with this nickname.
     * This method is called when the entered reconnection nickname is invalid during a player's reconnection.
     */
    public void invalidReconnectionNickname(){
        Platform.runLater(() -> nickLabel.setText("Nickname already used or there wasn't a player with this nickname"));
    }

    /**
     * Sets the login state to indicate that the player is reconnecting and prompts the player to enter a reconnecting nickname.
     * This method also updates the GUI to hide the start pane and show the nickname pane.
     * It is called when the player wants to reconnect to a game session.
     */
    public void askReconnectingNickname(){
        loginState=LoginState.RECONNECTING;
        Platform.runLater(()->{
            startPane.setVisible(false);
            nicknamePane.setVisible(true);
        });
    }

    /**
     * Sets the login state to indicate that the player is waiting for the host to specify the number of participants.
     * This method is called when the player is waiting for the host to create the game.
     */
    public void waitForNumber(){
        loginState=LoginState.WAITING_FOR_NUMBER;
    }

    /**
     * Sets the login state to indicate that the player is being asked to enter a nickname.
     * This method is called when the player needs to provide a nickname to join the game.
     * If the player was previously in the waiting state, the waiting screen is hidden and the nickname screen is displayed.
     */
    public void askNickname(){
        loginState=LoginState.DEFAULT_NICK;
        if(waitingLoginPane.isVisible())
            Platform.runLater(()-> {
                nicknamePane.setVisible(true);
                waitingLoginPane.setVisible(false);
            });
    }

    /**
     * Sets the login state to indicate that the game has already started.
     * This method is called when a player tries to join a game that has already started and cannot accommodate additional players.
     * The login state is updated accordingly to prevent the player from joining the game.
     */
    public void gameAlreadyStarted(){
        loginState=LoginState.GAME_ALREADY_STARTED;
    }

    /**
     * Sets the joining pane to display the waiting pane and hide the nickname pane.
     * This method is called when a player is in the process of joining a game and is waiting for the game to start.
     * The joining pane is updated accordingly to show the waiting pane and hide the nickname pane.
     */
    public void setJoiningPane(){
        Platform.runLater(()->{
            waitingPane.setVisible(true);
            nicknamePane.setVisible(false);
        });


    }
    private enum LoginState{
        WAITING_FOR_NUMBER, GAME_ALREADY_STARTED, DEFAULT_NICK, RECONNECTING, ASK_NUMBER
    }

}