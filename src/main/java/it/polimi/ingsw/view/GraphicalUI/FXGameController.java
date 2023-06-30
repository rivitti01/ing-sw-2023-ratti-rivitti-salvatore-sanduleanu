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

        String tileColor = tile.getColor().toString();
        BufferedImage temp = tileImages[(Color.valueOf(tileColor).ordinal()) * 3 + (tile.getType())];

        return SwingFXUtils.toFXImage(temp, null);
    }

    private void loadTileImages(BufferedImage[] tiles) {
        Color[] colors = Color.values();
        for (int i = 0; i < Arrays.stream(colors).count()-1; i++)
            for (int j = 0; j < 3; j++) {
                try {
                    tiles[(i * 3) + j] = ImageIO.read(Objects.requireNonNull(this.getClass().getResourceAsStream("/images/tiles/" + j + colors[i] + ".png")));
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

    public void lastTurn(boolean playing) {
        Platform.runLater(()-> topText.setText("It's your last turn!!!"));
        newTurn(playing);
    }

    public void askOrder(){
        Platform.runLater(()->{
            if(currentState==CurrentState.CHOOSING_COLUMN)
                bottomText.setText("Column chosen correctly, choose the tile to drop now");
            if(currentState==CurrentState.CHOOSING_ORDER)
                bottomText.setText("Continue choosing the next tile to drop into the column");
        });
        currentState=CurrentState.CHOOSING_ORDER;

    }

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

    public void setPlayerNickname(String playerName){
        this.playerNick=playerName;
    }

    public void startGame(){

        linkOpponentItems(playersObjects);
        setMatrix(playerShelf, playerMatrix);
        setMatrix(gameBoard, boardMatrix);
        started=true;
    }

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

    public void invalidTile(){
        currentState=CurrentState.CHOOSING_ACTION;
        Platform.runLater(() -> bottomText.setText("Invalid tile, choose a proper tile or continue to column"));
    }

    public void maxTiles(){
        Platform.runLater(() -> bottomText.setText("You cannot take more tiles, click a column to drop into"));
        currentState=CurrentState.CHOOSING_COLUMN;
    }

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


    public void invalidColumn(){
        this.currentState = CurrentState.CHOOSING_COLUMN;
        Platform.runLater(() -> bottomText.setText("Not a valid column, choose another one"));
    }

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
        previousState = currentState;
        this.currentState = CurrentState.WAITING_FOR_CLIENTS;
        Platform.runLater(() -> topText.setText("Not enough players"));
        Platform.runLater(() -> bottomText.setText("Waiting for more players to continue the game..."));
    }

    public void playerDisconnected(String disconnectedPlayer){
        String tempString = ("--- " + disconnectedPlayer + " has disconnected ---\n");
        fieldContent.append(tempString);
        Platform.runLater(() -> chatArea.setText(fieldContent.toString()));
    }

    public void playerReconnected(String reconnectedPlayer){
        String tempString = ("--- " + reconnectedPlayer + " has reconnected ---\n");
        fieldContent.append(tempString );
        Platform.runLater(() -> chatArea.setText(fieldContent.toString()));
    }

    public void lastTurnReached(String shelfFiller){
   //     Platform.runLater(()-> shelfCompletedToken.setVisible(true));
    //    if(shelfFiller.equals(playerNick))
   //         Platform.runLater(()->playerCompletedToken.setVisible(true));
    }
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
    public void choosingColumn(){
        try {
            client.columnSetting(this.columnNumber);
        } catch (RemoteException e) {
            System.err.println("Errore in choosing Column GUI side");
            throw new RuntimeException(e);
        }
    }

    public void choosingOrder(){
        currentState=CurrentState.CHOOSING_ORDER;
        Platform.runLater(()->{
            bottomText.setText("Column Chosen! Choose the tile to drop now");
        });

    }

    public void invalidOrder(){
        currentState=CurrentState.CHOOSING_ORDER;
        Platform.runLater(()->bottomText.setText("Invalid chosen tile clicked, try again"));
    }

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

        if(numberBox.getValue()==2 || numberBox.getValue()==3 || numberBox.getValue()==4) {
            numberPane.setVisible(false);
            nicknamePane.setVisible(true);
            client.numberPartecipantsSetting(numberBox.getValue());
        }
        else Platform.runLater(()->askNumberLabel.setText("Choice a number before entering"));
    }

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
        Platform.runLater(()->{
            startPane.setVisible(false);
            nicknamePane.setVisible(true);
        });
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