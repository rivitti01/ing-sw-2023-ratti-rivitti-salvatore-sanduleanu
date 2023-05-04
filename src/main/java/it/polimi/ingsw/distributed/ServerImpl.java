package it.polimi.ingsw.distributed;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.distributed.Client;
import it.polimi.ingsw.distributed.Server;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.util.ModelListener;

import java.beans.PropertyChangeEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerImpl implements Server, ModelListener {
    private Game model;

    private GameController controller;
    private Map<String, Client> connectedClients;

    public ServerImpl() {
        connectedClients = new HashMap<String, Client>();
        model = new Game();
        controller = new GameController(model);
        this.model.addModelListener(this);

    }

    public GameController getController() {
        return controller;
    }


    @Override
    public void register(Client c, String nickName) {
        connectedClients.put(nickName, c);
    }

    @Override
    public void nickNameSetting(String nickName) {

    }

    @Override
    public void numberOfPlayerSetting(int n) {

    }

    @Override
    public void startingGame() {

    }

    @Override
    public void droppingTiles(List<Tile> chosenTiles, int n) {

    }

    @Override
    public void chosenTilesSetting(List<Tile> tiles) {

    }

    @Override
    public void borderTilesSetting() {

    }

    @Override
    public void checkingCoordinates(int[] coordinates, List<Tile> borderTiles) {

    }

    @Override
    public void addingCoordinates(int[] coordinates) {

    }

    @Override
    public void addingTiles(int[] coordinates) {

    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

    }

    @Override
    public void onBoardChange(Board b) {

    }

    @Override
    public void onShelfChange(Shelf s) {

    }

    @Override
    public void pointsAssigned(int point) {

    }

    @Override
    public void playerGetsCommonPoints(String nickName) {

    }

    @Override
    public void printPlayerName(String nickName) {

    }

    @Override
    public void printTotalPoints(int n) {

    }

    @Override
    public void onCurrentPlayerSet(GameView gv) {

    }

    @Override
    public void onLastTurnSet(GameView gv) {

    }

    @Override
    public void showWinner(Player winner) {

    }

    @Override
    public void endGame() {

    }

    @Override
    public void firstTurn(GameView gv) {

    }
}
