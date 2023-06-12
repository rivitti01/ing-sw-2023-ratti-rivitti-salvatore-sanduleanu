package it.polimi.ingsw.view.GraphicalUI;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.util.ViewListener;
import it.polimi.ingsw.util.Warnings;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.image.WritableImage;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.*;
import java.util.stream.Collectors;

import static it.polimi.ingsw.util.Costants.*;
import static java.awt.image.BufferedImage.TYPE_INT_ARGB;

public class FXGameController {

    public FXGameController(ViewListener listener) {
        this.tempClient = listener;
    }



    private ViewListener tempClient;
    private ViewListener client;

    public GameView model;
    private StringBuilder fieldContent;
    private Stack<Node> chosenTiles = new Stack<>();
    private int tileCount;
    private boolean columnChosen;
    private TurnState turn;
    private String playerNick;
    boolean started;

    private List <PlayerObjects> playersObjects = new ArrayList<>(1);
    private BufferedImage[] tileImages = new BufferedImage[12];
    private Node[][] playerMatrix = new Node[6][5];
    private Node[][] boardMatrix = new Node[9][9];

    public enum TurnState {
        OPPONENT, PLAYER, LAST_ROUND, LAST_PLAYER
    }

    public void initialize() {
        client = tempClient;
        loadTileImages(tileImages);
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
        if (turn == TurnState.PLAYER && tileCount > 0 && tileCount < 4) {
            try {
                int offsetX = (GridPane.getColumnIndex((Node) event.getSource()) - 1) / 2;
                client.columnSetting(GridPane.getColumnIndex((Node) event.getSource()) - offsetX - 1);
                columnChosen = true;
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void chosenTile(MouseEvent event) {
        if (turn == TurnState.PLAYER && tileCount < 3 && !columnChosen) {
            try {
                int[] coordinates = new int[2];
                int offsetX = (GridPane.getRowIndex((Node) event.getSource()) - 1) / 2;
                int offsetY = (GridPane.getColumnIndex((Node) event.getSource()) - 1) / 2;
                coordinates[0] = (GridPane.getColumnIndex((Node) event.getSource()) - offsetX - 1);
                coordinates[1] = (GridPane.getRowIndex((Node) event.getSource()) - offsetY - 1);
                this.client.checkingCoordinates(coordinates);
                chosenTiles.add((Node)event.getSource());
                tileCount++;
                ColorAdjust chosenEffect = new ColorAdjust(0, 0.2, 0.2, 0.1);
                ((ImageView) (event.getSource())).setEffect(chosenEffect);
            } catch (InputMismatchException | RemoteException e1) {
                if (e1 instanceof RemoteException)
                    ((RemoteException) e1).printStackTrace();
            }
        }

        if (turn == TurnState.PLAYER && tileCount < 4 && tileCount > 0 && columnChosen) {
            ColorAdjust orderEffect = new ColorAdjust(0, 0.6, 0.6, 0.25);
            for(Node n : chosenTiles){
                try {
                    if (((Node) (event.getSource())).equals(n)) {
                        ((ImageView)n).setEffect(orderEffect);
                        chosenTiles.pop();
                        client.tileToDrop(chosenTiles.indexOf(n));
                    }
                }catch(RemoteException e){
                    e.printStackTrace();
                }
            }

        }
    }

    private void setMatrix(GridPane grid, Node[][] Matrix) {
        for (Node node : grid.getChildren()) {
            if (node instanceof ImageView) {
                int offsetY = (GridPane.getRowIndex(node) - 1) / 2;
                int offsetX = (GridPane.getColumnIndex(node) - 1) / 2;
                Matrix[GridPane.getColumnIndex(node) - offsetY - 1][GridPane.getRowIndex(node) - offsetX - 1] = node;
            }
        }
    }

    public void printGame(GameView gameView) {


        setGameScene(gameView);
        printTurnState(gameView.getNickName());
        if (model.getNameGoals() != gameView.getNameGoals()) printCommonGoals(gameView.getNameGoals());
        if (model.getBoard() != gameView.getBoard()) printBoard(gameView.getBoard());
        if (model.getPlayersShelves() != gameView.getPlayersShelves()) printShelves(gameView.getPlayersShelves());
        if (model.getPersonal() != gameView.getPersonal()) printPersonalGoalShelf(gameView.getPersonal());
        //printChosenTiles(gameView.getChosenTiles(), gameView.getNickName());
        model = gameView;

    }

    public void printShelves(Map<String, Shelf> playerShelves) {
        Node[][] tmpMatrix = new Node[6][5];
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
                    ((ImageView) tmpMatrix[i][j]).setImage(getTileImage(tmpShelf.getTile(i, j)));
                }
            }
        }
    }

    private void printBoard(Board b) {
        for (int i = 0; i < b.getSize(); i++)
            for (int j = 0; j < b.getSize(); j++) {
                if (b.getTile(i, j) != null && b.getTile(i, j).getColor() != null && b.getTile(i, j).getColor() != Color.TRANSPARENT)
                    ((ImageView) boardMatrix[i][j]).setImage(getTileImage(b.getTile(i, j)));
            }
    }

    public void printChat(ChatView chat) {
        fieldContent = new StringBuilder("");

        for (String message : chat.getChat()) {
            fieldContent.append("" + message + "\n");
        }
        chatArea.setText(fieldContent.toString());
    }

    private void printPersonalGoalShelf(PersonalGoalCard personal) {
        File p = new File("src/main/resources/images/personal goals/" + personal.toString() + "_personal.png");
        BufferedImage tmp = null;
        try {
            tmp = ImageIO.read(p);
        } catch (IOException e) {
            e.printStackTrace();
        }

        personalGoal.setImage(SwingFXUtils.toFXImage(tmp, null));
    }

    private void printCommonGoals(String[] goals) {
        BufferedImage c = null;
        for (int i = 0; i < COMMON_CARDS_PER_GAME; i++) {

            try {
                c = ImageIO.read(new File("src/main/resources/images/common goals/" + goals[i] + ".png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (i == 0) firstCommonView.setImage(SwingFXUtils.toFXImage(c, null));
            if (i == 1) secondCommonView.setImage(SwingFXUtils.toFXImage(c, null));
        }
    }

    private void printChosenTiles() {
    }

    private Image getTileImage(Tile tile) {

        String tileColor = tile.getColor().toString();
        BufferedImage temp = tileImages[(Color.valueOf(tileColor).ordinal()) * 3 + (tile.getType())];

        return SwingFXUtils.toFXImage(temp, null);
    }

    private void loadTileImages(BufferedImage[] tiles) {
        Color[] colors = Color.values();
        for (int i = 0; i < Arrays.stream(colors).count(); i++)
            for (int j = 0; i < 3; i++) {
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

        //if(x.visible=true){

        //}

        if(b)
            turn = TurnState.PLAYER;
        else turn = TurnState.OPPONENT;
        tileCount = 0;
    }

    public void waitingTurn() {
        turn = TurnState.OPPONENT;
    }

    public void lastTurn(boolean playing) {
        turn = TurnState.LAST_PLAYER;
    }

    public void lastTurnReached() {
        turn = TurnState.LAST_ROUND;
    }

    public void printTurnState(String playingPlayer) {

        if(started)
            printToken(playingPlayer);


        switch (turn) {
            case PLAYER:
                topText.setText("It's your turn");
                bottomText.setText("Choose available tiles, then click on the column you want to fill");
                break;
            case OPPONENT:
                topText.setText("It's " + playingPlayer + "'s turn");
                bottomText.setText("Wait for your turn");
                break;
            case LAST_PLAYER:
                topText.setText("It's your last turn");
                bottomText.setText("Choose available tiles");
                break;

            default:
                if (!playingPlayer.equals(playerNick)) {
                    topText.setText("It's " + playingPlayer + "'s turn");
                    bottomText.setText("Last round starts from now!");
                } else {
                    topText.setText("It's your turn");
                    bottomText.setText("Last round starts from now!");
                }
        }
    }

    public void printFinalPoints(Map<String, Integer> chart) {
        int winner = -1;
        for (String s : chart.keySet()) {
            if (chart.get(s) > winner) winner = chart.get(s);
            if (s.equals(playerNick)) bottomText.setText("You scored " + chart.get(s) + " points");
            else for (int i = 0; i < chart.size() - 1; i++)
                if (playersObjects.get(i).getName().equals(s)) {
                    playersObjects.get(i).getLabelPoints().setText("Points: " + chart.get(s));
                    playersObjects.get(i).getLabelPoints().setVisible(true);
                }
        }
        topText.setText("Game has ended. " + winner + " won!");
    }

    protected static class PlayerObjects {
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

    public void warnings(Warnings e) throws RemoteException {
        switch (e) {
            case INVALID_TILE -> bottomText.setText("Wrong tile chosen, try again.");
            case INVALID_COLUMN -> bottomText.setText("Invalid column, try again");
            case INVALID_ORDER -> {
                // System.err.println("AOO metti una posizione sensata.");
                // askOrder();
            }
            case YOUR_TURN ->{}
        }

    }

    public void setPlayerNickname(String playerName){
        this.playerNick=playerName;
    }

    public void startGame(){

        linkOpponentItems(playersObjects);
        setMatrix(playerShelf, playerMatrix);
        setMatrix(gameBoard, boardMatrix);
        waitingPane.setVisible(false);
        gamePane.setVisible(true);
        started=true;
    }

    public void setGameScene(GameView model){
        playerName.setText(model.getNickName());


        Set<String> playerNameSet = model.getPlayersShelves().keySet();
        List<String> nickList = playerNameSet.stream().collect(Collectors.toCollection(ArrayList::new));

        for(int i=0; i<model.getPlayersShelves().size()-1; i++){
            if(nickList.get(i).equals(playerNick)) {
                nickList.remove(i);
                i--;
            }
            else {
                playersObjects.get(i).getLabelNick().setVisible(true);
                playersObjects.get(i).setNick(nickList.get(i));
                playersObjects.get(i).getLabelNick().setText(nickList.get(i));
                playersObjects.get(i).getSeat().setVisible(true);
                playersObjects.get(i).getGrid().setVisible(true);

            }
        }

    }

    private void printToken(String firstPlayer){

        File f = new File("src/main/resources/images/firstplayertoken");
        BufferedImage firstTmp = null;
        try {
            firstTmp = ImageIO.read(f);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (PlayerObjects p : playersObjects)
            if (p.getName().equals(firstPlayer)) {
                p.getSeat().setImage(SwingFXUtils.toFXImage(firstTmp, null));
                started = false;
            }

        if(started)
            playerToken.setImage(SwingFXUtils.toFXImage(firstTmp, null));

    }
}