package it.polimi.ingsw.view;


import it.polimi.ingsw.util.ViewListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.rmi.RemoteException;

public class FXLoginController {

  /*  public FXLoginController(int playerNumber){
        numberPlayers = playerNumber;
    }*/



    private ViewListener listener;

  //  private int numberPlayers;

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
    private GridPane waitingPane;
    @FXML
    private ChoiceBox<Integer> numberBox;

    @FXML
    Pane startPane;
    @FXML
    Pane numberPane;

    public void initialize(){
        numberBox.setItems(numberList);
    }

    @FXML
    void exit(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    void play(ActionEvent event) {
    startPane.setVisible(false);
        /* if(numberPlayers<2 || numberPlayers>4)*/ numberPane.setVisible(true);
   // else nicknamePane.setVisible(true);
    }

    @FXML
    void sendNumber(ActionEvent event) throws RemoteException {
        numberPane.setVisible(false);
        nicknamePane.setVisible(true);
        listener.numberPartecipantsSetting(numberBox.getValue());
    }

    public void addListener(ViewListener l) {
        this.listener = l;

    }


    @FXML
    void joinGame(ActionEvent event) throws RemoteException {
        nicknamePane.setVisible(false);
        waitingPane.setVisible(true);
        listener.clientNickNameSetting(nicknameField.getText());
    }
}
