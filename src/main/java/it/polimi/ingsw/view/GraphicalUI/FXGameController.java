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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.*;
import java.util.stream.Collectors;
import static it.polimi.ingsw.util.Costants.*;
public class FXGameController {

    public FXGameController(ViewListener listener) {
        this.tempClient = listener;
    }



    private final ViewListener tempClient;
    private ViewListener client;

    public GameView model;
    private StringBuilder fieldContent;
    private TurnState turn;
    private CurrentState currentState;
    private String playerNick;
    boolean started;


    Node[][] tmpMatrix;
    private List <PlayerObjects> playersObjects;
    private BufferedImage[] tileImages = new BufferedImage[18];
    private Node[][] playerMatrix = new Node[6][5];
    private Node[][] boardMatrix = new Node[9][9];

    public enum TurnState {
        OPPONENT, PLAYER, LAST_ROUND, LAST_PLAYER
    }

    public void initialize() {
        client = tempClient;
        loadTileImages(tileImages);
        numberBox.setItems(numberList);
        chatArea.setText("Start the message with @nickname to send a message to a specific player.");
    }

    @FXML
    private BorderPane gamePane;
    @FXML
    private GridPane waitingPane;

    @FXML
    private Label ThirdOpponentName;

    @FXML
    private Label bottomText;

    @FXML
    private TextArea chatArea;

    @FXML
    private TextField chatField;

    @FXML
    private Label chatErrorLabel;

    @FXML
    private ImageView firstCommonView;

    @FXML
    private Label firstOpponentName;

    @FXML
    private GridPane firstOpponentShelf;

    @FXML
    private ImageView firstOpponentToken;

    @FXML
    private GridPane gameBoard;

    @FXML
    private ImageView personalGoal;

    @FXML
    private Label playerName;

    @FXML
    private GridPane playerShelf;

    @FXML
    private ImageView playerToken;

    @FXML
    private ImageView secondCommonView;

    @FXML
    private Label secondOpponentName;

    @FXML
    private GridPane secondOpponentShelf;

    @FXML
    private ImageView secondOpponentToken;

    @FXML
    private GridPane thirdOpponentShelf;

    @FXML
    private ImageView thirdOpponentToken;
    @FXML
    private Label thirdOpponentName;

    @FXML
    private Label topText;
    @FXML
    private Label firstPoints;
    @FXML
    private Label secondPoints;
    @FXML
    private Label thirdPoints;

    @FXML
    private ImageView firstChosenTile;
    @FXML
    private ImageView secondChosenTile;
    @FXML
    private ImageView thirdChosenTile;



    @FXML
    void sendMessage(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            try {
                client.newMessage(chatField.getText());
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @FXML
    void chosenColumn(MouseEvent event) {
        if (turn == TurnState.PLAYER && (currentState == CurrentState.CHOOSING_COLUMN || currentState == CurrentState.CHOOSING_ACTION)) {
            try {
                int offsetX = (GridPane.getColumnIndex((Node) event.getSource()) - 1) / 2;
                client.columnSetting(GridPane.getColumnIndex((Node) event.getSource()) - offsetX - 1);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void chosenTile(MouseEvent event) {
        if (turn == TurnState.PLAYER && currentState!=CurrentState.CHOOSING_COLUMN) {
            try {
                int[] coordinates = new int[2];
                int offsetX = (GridPane.getRowIndex((Node) event.getSource()) - 1) / 2;
                int offsetY = (GridPane.getColumnIndex((Node) event.getSource()) - 1) / 2;
                coordinates[0] = (GridPane.getColumnIndex((Node) event.getSource()) - offsetX - 1);
                coordinates[1] = (GridPane.getRowIndex((Node) event.getSource()) - offsetY - 1);
                this.client.checkingCoordinates(coordinates);
            } catch (InputMismatchException | RemoteException e1) {
                if (e1 instanceof RemoteException)
                    ((RemoteException) e1).printStackTrace();
            }
        }
    }

    @FXML
    void chosenOrder(MouseEvent event){
        ImageView tmp = (ImageView)event.getSource();
        if(turn == TurnState.PLAYER && currentState!=CurrentState.CHOOSING_ORDER) {
            if(tmp==firstChosenTile) {
                try {
                    client.tileToDrop(0);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }

            if(tmp == secondChosenTile){
                try {
                    client.tileToDrop(1);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }

            if(tmp == thirdChosenTile){
                try {
                    client.tileToDrop(2);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void setMatrix(GridPane grid, Node[][] Matrix) {
        for (Node node : grid.getChildren()) {
            if (node instanceof ImageView) {
                int offsetY = (GridPane.getRowIndex(node) - 1) / 2;
                int offsetX = (GridPane.getColumnIndex(node) - 1) / 2;
                Matrix[GridPane.getRowIndex(node) - offsetY -1][GridPane.getColumnIndex(node) - offsetX -1] = node;
            }
        }
    }

    public void printGame(GameView gameView) {
        if(model==null) {
            initializeGraphicObjects();
            startGame();
            setGameScene(gameView);
            Platform.runLater(()-> waitingPane.setVisible(false));
        }
        printTurnState(gameView.getNickName());
        if (model == null || model.getNameGoals() != gameView.getNameGoals())
            printCommonGoals(gameView.getNameGoals());
        if (model == null ||  model.getBoard() != gameView.getBoard())
            printBoard(gameView.getBoard());
        if (model == null ||  model.getPlayersShelves() != gameView.getPlayersShelves())
            printShelves(gameView.getPlayersShelves());
        if (model == null ||  model.getPersonal() != gameView.getPersonal())
            printPersonalGoalShelf(gameView.getPersonal());
        if (model == null ||  model.getChosenTiles()!=gameView.getChosenTiles() || model.getNickName()!=gameView.getNickName())
            printChosenTiles(gameView.getChosenTiles(), gameView.getNickName());

        model = gameView;

    }

    public void printShelves(Map<String, Shelf> playerShelves) {


        tmpMatrix = new Node[6][5];
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
                        Platform.runLater(() -> ((ImageView) tmpMatrix[finalI][finalJ]).setImage(getTileImage(tmpShelf.getTile(finalI, finalJ))));
                    }
                }
            }
        }
    }

    private void printBoard(Board b) {
        for (int i = 0; i < b.getSize(); i++)
            for (int j = 0; j < b.getSize(); j++) {
                if (b.getTile(i, j) != null && b.getTile(i, j).getColor() != null && b.getTile(i, j).getColor() != Color.TRANSPARENT) {
                    int finalI = i;
                    int finalJ = j;
                    Platform.runLater(() -> ((ImageView) boardMatrix[finalI][finalJ]).setImage(getTileImage(b.getTile(finalI, finalJ))));
                }
            }
    }

    public void printChat(ChatView chat) {
        fieldContent = new StringBuilder("");

        for (String message : chat.getChat()) {
            fieldContent.append("" + message + "\n");
        }
        Platform.runLater(() -> chatArea.setText(fieldContent.toString()));
    }

    private void printPersonalGoalShelf(PersonalGoalCard personal) {
        File p = new File("src/main/resources/images/personal goals/" + personal.getCardName() + "_personal.png");
        BufferedImage tmp = null;
        try {
            tmp = ImageIO.read(p);
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
                c = ImageIO.read(new File("src/main/resources/images/common goals/" + goals[i] + ".jpg"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            BufferedImage finalC = c;
            if (i == 0) Platform.runLater(() -> firstCommonView.setImage(SwingFXUtils.toFXImage(finalC, null)));
            if (i == 1) Platform.runLater(() -> secondCommonView.setImage(SwingFXUtils.toFXImage(finalC, null)));
        }
    }

    private void printChosenTiles(List<Tile> chosenTiles, String nickname) {

        Platform.runLater(() -> bottomText.setText(nickname + " is choosing tile order: "));

        if(chosenTiles.size()>=1) {
            Platform.runLater(() -> {
                firstChosenTile.setImage(getTileImage(chosenTiles.get(0)));
                firstChosenTile.setVisible(true);
            });
        }
        else Platform.runLater(() -> firstChosenTile.setVisible(false));

        if(chosenTiles.size()>=2){
            Platform.runLater(() -> {
                secondChosenTile.setImage(getTileImage(chosenTiles.get(1)));
                secondChosenTile.setVisible(true);
            });
        }
        else Platform.runLater(() -> secondChosenTile.setVisible(false));

        if(chosenTiles.size()>=3){
            Platform.runLater(() -> {
                thirdChosenTile.setImage(getTileImage(chosenTiles.get(2)));
                thirdChosenTile.setVisible(true);
            });
        }
        else Platform.runLater(() -> thirdChosenTile.setVisible(false));

    }

    private Image getTileImage(Tile tile) {

        String tileColor = tile.getColor().toString();
        BufferedImage temp = tileImages[(Color.valueOf(tileColor).ordinal()) * 3 + (tile.getType())];

        return SwingFXUtils.toFXImage(temp, null);
    }

    private void loadTileImages(BufferedImage[] tiles) {
        Color[] colors = Color.values();
        for (int i = 0; i < Arrays.stream(colors).count()-1; i++)
            for (int j = 0; j < 3; j++) {
                try {
                    tiles[(i * 3) + j] = ImageIO.read(new File("src/main/resources/images/tiles/" + j + colors[i] + ".png"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
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

    public void newTurn(boolean b) {
        if(b) {
            turn = TurnState.PLAYER;
            currentState = CurrentState.CHOOSING_ACTION;
        }

        else{
            turn = TurnState.OPPONENT;
            currentState = CurrentState.WAITING_TURN;
        }
    }

    public void waitingTurn() {
        turn = TurnState.OPPONENT;
    }

    public void lastTurn(boolean playing) {
        turn = TurnState.LAST_PLAYER;
    }

    public void askOrder(){
        currentState=CurrentState.CHOOSING_ORDER;
    }
    public void lastTurnReached() {
        turn = TurnState.LAST_ROUND;
    }

    public void printTurnState(String playingPlayer) {

        if(playingPlayer == playerNick)
            turn=TurnState.PLAYER;
        else turn=TurnState.OPPONENT;

        if(started)
            Platform.runLater(() -> printToken(playingPlayer));


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

    public void printFinalPoints(Map<String, Integer> chart) {
        int winner = -1;
        for (String s : chart.keySet()) {
            if (chart.get(s) > winner) winner = chart.get(s);
            if (s.equals(playerNick))
                Platform.runLater(() -> bottomText.setText("You scored " + chart.get(s) + " points"));
            else
                for (int i = 0; i < chart.size() - 1; i++)
                    if (playersObjects.get(i).getName().equals(s)) {
                        int finalI = i;
                        Platform.runLater(() -> {
                            playersObjects.get(finalI).getLabelPoints().setText("Points: " + chart.get(s));
                            playersObjects.get(finalI).getLabelPoints().setVisible(true);
                        });
                    }
        }
        int finalWinner = winner;
        Platform.runLater(() -> topText.setText("Game has ended. " + finalWinner + " won!"));
    }

    private class PlayerObjects {

        public PlayerObjects(){
            shelf = new Node[9][9];
            seat = new ImageView();
            labelNick = new Label();
            points = new Label();
            shelfPane = new GridPane();
        }
        private String nick;
        private Node[][] shelf;
        private ImageView seat;
        private Label labelNick;
        private Label points;
        private GridPane shelfPane;

        public Node[][] getShelfMatrix() {
            return this.shelf;
        }

        public ImageView getSeat() {
            return this.seat;
        }

        public Label getLabelNick() {
            return this.labelNick;
        }

        public Label getLabelPoints() {
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

        public void setLabelNick(Label label) {
            this.labelNick = label;
        }

        public void setShelfMatrix(Node[][] matrix) {
            this.shelf = matrix;
        }

        public void setLabelPoints(Label pointsLabel) {
            this.points = pointsLabel;
        }

        private void setGrid(GridPane shelf){
            shelfPane=shelf;
        }

    }

    public void setPlayerNickname(String playerName){
        this.playerNick=playerName;
    }

    public void startGame(){

        System.out.println("fff");
        linkOpponentItems(playersObjects);
        setMatrix(playerShelf, playerMatrix);
        setMatrix(gameBoard, boardMatrix);
        Platform.runLater(()->{
            waitingPane.setVisible(false);
            gamePane.setVisible(true);
        });
        started=true;
    }

    public void setGameScene(GameView model){
        Platform.runLater(() -> playerName.setText(model.getNickName()));


        Set<String> playerNameSet = model.getPlayersShelves().keySet();
        List<String> nickList = playerNameSet.stream().collect(Collectors.toCollection(ArrayList::new));

        for(int i=0; i<model.getPlayersShelves().size()-1; i++){
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
                });
            }
        }

    }

    private void printToken(String firstPlayer){

        File f = new File("src/main/resources/images/firstplayertoken.png");
        BufferedImage firstTmp = null;
        try {
            firstTmp = ImageIO.read(f);
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

    public void invalidTile(){
        Platform.runLater(() -> bottomText.setText("Invalid tile, try again then click on the column you want to fill"));
    }

    public void maxTiles(){
        currentState=CurrentState.CHOOSING_COLUMN;
    }

    public void  chooseNext(){
        currentState=CurrentState.CHOOSING_ACTION;

    }

    public void invalidColumn(){
        this.currentState = CurrentState.CHOOSING_COLUMN;
        Platform.runLater(() -> bottomText.setText("Not a valid column, choose another one"));
    }

    public void invalidChatMessage(){
        Platform.runLater(() -> chatErrorLabel.setText("invalid message"));
    }

    public void invalidReceiver(){
        Platform.runLater(() -> chatErrorLabel.setText("invalid message receiver"));
    }

    public void reconnectedMessage(){
        Platform.runLater(() -> bottomText.setText("You have been disconnected. Please wait for your turn"));
        currentState=CurrentState.WAITING_TURN;
    }

    public void noPlayersLeft(){
        Platform.runLater(() -> topText.setText("No players left"));
        Platform.runLater(() -> bottomText.setText("You won!"));
    }

    public void waitingToContinue(){
        this.currentState = CurrentState.WAITING_FOR_CLIENTS;
        Platform.runLater(() -> topText.setText("Not enough players"));
        Platform.runLater(() -> bottomText.setText("Waiting for more players to continue the game..."));
    }

    public void playerDisconnected(){
        Platform.runLater(() -> topText.setText("A player has disconnected."));
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
    private Button enterButton;
    @FXML
    private Button joinButton;
    @FXML
    private TextField nicknameField;
    @FXML
    private Label nickLabel;
    @FXML
    private GridPane nicknamePane;
    @FXML
    private GridPane waitingLoginPane;
    @FXML
    private Label waitingLabel;
    @FXML
    private AnchorPane rootPane;

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

    @FXML
    void sendNumber(ActionEvent event) throws RemoteException {
        numberPane.setVisible(false);
        nicknamePane.setVisible(true);
        client.numberPartecipantsSetting(numberBox.getValue());
    }

    @FXML
    void joinGame(ActionEvent event) throws RemoteException {
        if(loginState==LoginState.RECONNECTING){
            client.checkingExistingNickname(nicknameField.getText());
        }
        else {
            client.clientNickNameSetting((nicknameField.getText()));
        }
        setPlayerNickname(nicknameField.getText());
    }

    public void setPlayerNumber(boolean number) {
        loginState=LoginState.ASK_NUMBER;
    }

    public void invalidNickname(){
        Platform.runLater(() -> nickLabel.setText("Nickname already chosen, try again"));
    }

    public void invalidReconnectionNickname(){
        Platform.runLater(() -> nickLabel.setText("Nickname already used or there wasn't a player with this nickname"));
    }

    public void askReconnectingNickname(){
        loginState=LoginState.RECONNECTING;
    }

    public void waitForNumber(){
        loginState=LoginState.WAITING_FOR_NUMBER;
    }

    public void askNickname(){
        loginState=LoginState.DEFAULT_NICK;
        if(waitingLoginPane.isVisible())
            Platform.runLater(()-> {
                nicknamePane.setVisible(true);
                waitingLoginPane.setVisible(false);
            });
    }

    public void gameAlreadyStarted(){
        loginState=LoginState.GAME_ALREADY_STARTED;
    }

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