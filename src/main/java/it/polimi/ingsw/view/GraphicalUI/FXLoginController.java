package it.polimi.ingsw.view.GraphicalUI;


import it.polimi.ingsw.util.ViewListener;
import it.polimi.ingsw.util.Warnings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.rmi.RemoteException;

public class FXLoginController {

  /*  public FXLoginController(int playerNumber){
        numberPlayers = playerNumber;
    }*/

    public FXLoginController(ViewListener listener, Scene game, FXGameController secondController){
        this.tempClient=listener;
        this.gameScene=game;
        this.gameController=secondController;
    }

    private FXGameController gameController;
    private Scene gameScene;
    private ViewListener tempClient;
    private ViewListener client;

    private boolean playerNumber;

    private final ObservableList<Integer> numberList = FXCollections.observableArrayList(2,3,4);

    @FXML
    private Button enterButton;

    @FXML
    private Button joinButton;

    @FXML
    private TextField nicknameField;

    @FXML
    private GridPane nicknamePane;

    @FXML
    private AnchorPane rootPane;


    @FXML
    private ChoiceBox<Integer> numberBox;

    @FXML
    Pane startPane;
    @FXML
    Pane numberPane;

    public void initialize(){
        client = tempClient;
        numberBox.setItems(numberList);
    }

    @FXML
    void exit(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    void play(ActionEvent event) {
    startPane.setVisible(false);
    if(playerNumber)
        numberPane.setVisible(true);
    else
        nicknamePane.setVisible(true);
    }

    @FXML
    void sendNumber(ActionEvent event) throws RemoteException {
        numberPane.setVisible(false);
        nicknamePane.setVisible(true);
        client.numberPartecipantsSetting(numberBox.getValue());
    }

    public void addListener(ViewListener lis) {
        this.client = lis;
    }


    @FXML
    void joinGame(ActionEvent event) throws RemoteException {
        client.clientNickNameSetting(nicknameField.getText());
        gameController.setPlayerNickname(nicknameField.getText());
        openSecondScene(event);
    }

    public void warnings(Warnings e) throws RemoteException{
        if(e==Warnings.GAME_ALREADY_STARTED){ /*succede qualcosa*/}
    }

    public void openSecondScene(ActionEvent actionEvent) {
        Stage primaryStage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        primaryStage.setScene(gameScene);
    }

    public void setPlayerNumber(boolean number) {
        this.playerNumber = number;
    }
}
