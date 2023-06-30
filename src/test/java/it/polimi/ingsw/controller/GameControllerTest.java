package it.polimi.ingsw.controller;

import it.polimi.ingsw.distributed.First;
import it.polimi.ingsw.distributed.rmi.ServerRMIImpl;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.util.Warnings;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import static it.polimi.ingsw.util.Costants.SHELF_COLUMN;
import static it.polimi.ingsw.util.Costants.SHELF_ROWS;
import static javax.management.Query.times;
import static org.junit.jupiter.api.Assertions.*;

public class GameControllerTest {

    private Game game;
    private List<Player> players;
    private ServerRMIImpl server;
    private First first;
    private GameController gameController;


    @BeforeEach
    public void setUp() throws RemoteException {
        game = new Game();
        gameController = new GameController(game);
        first = new First();
        players = new ArrayList<>();
        server = new ServerRMIImpl(game, gameController, first);
        game.addModelListener(server);

    }

    @Test
    public void testSetNumberPlayers() {
        gameController.setNumberPlayers(3);
        assertEquals(3, gameController.getNumberPlayers());
    }

    @Test
    public void testGetPlayers() {
        gameController.setNumberPlayers(2);
        gameController.setPlayerNickname("Alice");
        gameController.setPlayerNickname("Bob");
        assertEquals("Alice", gameController.getPlayers().get(0).getNickname());
        assertEquals("Bob", gameController.getPlayers().get(1).getNickname());
    }

    @Test
    public void testCheckGameInitialization_GameNotStarted() {
        gameController.setNumberPlayers(3);
        gameController.checkGameInitialization();
        assertFalse(game.isStart());
    }

    @Test
    public void testCheckGameInitialization_GameStarted() {
        gameController.setNumberPlayers(3);
        gameController.setPlayerNickname("John");
        gameController.setPlayerNickname("Alice");
        gameController.setPlayerNickname("Bob");
        gameController.checkGameInitialization();
        assertTrue(game.isStart());
    }

    @Test
    public void testInitializeModel() {
        gameController.setNumberPlayers(2);
        gameController.setPlayerNickname("Alice");
        gameController.setPlayerNickname("Bob");
        gameController.initializeModel();
        assertTrue(game.isStart());
        int expectedSize = gameController.getPlayers().size();
        assertEquals(expectedSize, game.getPlayers().size());
    }

    @Test
    public void testSetPlayerNickname_NicknameAvailable() {
        gameController.setNumberPlayers(3);
        gameController.setPlayerNickname("Alice");
        gameController.setPlayerNickname("Bob");
        boolean result = gameController.setPlayerNickname("Charlie");
        gameController.checkGameInitialization();
        assertTrue(result);
        assertEquals(3, game.getPlayers().size());
    }

    @Test
    public void testSetPlayerNickname_NicknameAlreadyTaken() {
        gameController.setNumberPlayers(3);
        gameController.setPlayerNickname("Alice");
        gameController.setPlayerNickname("Bob");
        boolean result = gameController.setPlayerNickname("Bob");
        assertFalse(result);
        gameController.setPlayerNickname("John");
        gameController.checkGameInitialization();
        assertEquals(3, game.getPlayers().size());
    }

    @Test
    public void testCheckingExistingNickname_NicknameExistsAndPlayerNotConnected() {
        gameController.setNumberPlayers(2);
        gameController.setPlayerNickname("Alice");
        gameController.getPlayers().get(0).setConnected(false);
        gameController.setPlayerNickname("Bob");
        gameController.getPlayers().get(1).setConnected(false);
        gameController.checkGameInitialization();
        boolean result = gameController.checkingExistingNickname("Alice");
        assertTrue(result);
    }

    @Test
    public void testCheckingExistingNickname_NicknameExistsAndPlayerConnected() {
        gameController.setNumberPlayers(2);
        gameController.setPlayerNickname("Alice");
        gameController.getPlayers().get(0).setConnected(true);
        gameController.setPlayerNickname("Bob");
        gameController.getPlayers().get(1).setConnected(false);
        gameController.checkGameInitialization();
        boolean result = gameController.checkingExistingNickname("Alice");
        assertFalse(result);
    }

    @Test
    public void testPlayerOffline_AllPlayersConnected() {
        gameController.setNumberPlayers(2);
        gameController.setPlayerNickname("Alice");
        gameController.getPlayers().get(0).setConnected(true);
        gameController.setPlayerNickname("Bob");
        gameController.getPlayers().get(1).setConnected(true);
        gameController.checkGameInitialization();
        boolean result = gameController.playerOffline();
        assertFalse(result);
    }

    @Test
    public void testPlayerOffline_AtLeastOnePlayerDisconnected() {
        gameController.setNumberPlayers(2);
        gameController.setPlayerNickname("Alice");
        gameController.getPlayers().get(0).setConnected(true);
        gameController.setPlayerNickname("Bob");
        gameController.getPlayers().get(1).setConnected(false);
        gameController.checkGameInitialization();
        boolean result = gameController.playerOffline();
        assertTrue(result);
    }



    @Test
    public void testStartGame() {
        gameController.setNumberPlayers(2);
        gameController.setPlayerNickname("Alice");
        gameController.setPlayerNickname("Bob");
        gameController.checkGameInitialization();
        assertTrue(game.isStart());
    }

    @Test
    public void testEndGame() {
        gameController.setNumberPlayers(2);
        gameController.setPlayerNickname("Alice");
        gameController.setPlayerNickname("Bob");
        gameController.checkGameInitialization();
        game.setEnd(true);
        assertTrue(game.isEnd());
    }

    @Test
    public void testAddPlayer() {
        gameController.setNumberPlayers(2);
        gameController.setPlayerNickname("Alice");
        gameController.setPlayerNickname("John");
        Player p1 = gameController.getPlayers().get(0);
        gameController.initializeModel();
        assertEquals(2, game.getPlayers().size());
        assertTrue(game.getPlayers().contains(p1));
    }

    @Test
    public void testRemovePlayer() {
        gameController.setNumberPlayers(2);
        gameController.setPlayerNickname("Bob");
        gameController.setPlayerNickname("Alice");
        gameController.setPlayerNickname("John");
        gameController.getPlayers().remove(0);
        gameController.initializeModel();
        assertEquals(2, game.getPlayers().size());
        assertFalse(game.getPlayers().contains("Bob"));
    }

    @Test
    public void testUpdatePlayerScore() {
        gameController.setNumberPlayers(2);
        gameController.setPlayerNickname("Alice");
        gameController.setPlayerNickname("Bob");
        gameController.checkGameInitialization();
        gameController.getPlayers().get(0).addPoints(100);
        assertEquals(100, gameController.getPlayers().get(0).getPoints());
    }

    @Test
    public void testGetTopScorers() {
        gameController.setNumberPlayers(3);
        gameController.setPlayerNickname("Alice");
        gameController.setPlayerNickname("Bob");
        gameController.setPlayerNickname("John");
        gameController.getPlayers().get(2).addPoints(200);
        gameController.checkGameInitialization();
        assertEquals(3, game.getPlayers().size());
    }

    @Test
    public void testSwitchCurrentPlayer() throws RemoteException {
        gameController.setNumberPlayers(4);
        gameController.setPlayerNickname("Alice");
        gameController.setPlayerNickname("Bob");
        gameController.setPlayerNickname("John");
        gameController.setPlayerNickname("Kevin");
        gameController.checkGameInitialization();
        Player p;
        int index;
        for(int i = 0; i< 4; i++){
            p = game.getCurrentPlayer();
            index = game.getPlayers().indexOf(p);
            assertEquals(i, index);
            gameController.switchCurrentPlayer(index);
            assertNotEquals(p, game.getCurrentPlayer());
        }
    }

    @Test
    public void testNextPlayer() throws RemoteException {
        gameController.setNumberPlayers(2);
        gameController.setPlayerNickname("Alice");
        gameController.setPlayerNickname("Bob");
        gameController.checkGameInitialization();
        for (int i=0; i<game.getBoard().getBoard().length; i++) {
            for(int j=0; j<game.getBoard().getBoard()[0].length; j++){
                Tile t = game.getBoard().popTile(i,j);
            }
        }
        assertTrue(game.getBoard().checkRefill());
        while(game.getPlayers().indexOf(game.getCurrentPlayer())!=game.getPlayers().size()-1){
            gameController.switchCurrentPlayer(game.getPlayers().indexOf(game.getCurrentPlayer()));
        }
        for (int i =0; i<SHELF_ROWS; i++){
            for(int j=0; j<SHELF_COLUMN; j++){
                game.getCurrentPlayer().getShelf().putTile(i, j, new Tile(Color.BLUE, 1));
            }
        }
        assertTrue(game.getCurrentPlayer().getShelf().isFull());
        gameController.nextPlayer();
        assertTrue(game.isLastTurn());
        assertTrue(game.isEnd());

    }

    @Test
    public void testSetChosenColumn_ValidColumn() {
        int column = 2;
        gameController.setNumberPlayers(2);
        gameController.setPlayerNickname("Alice");
        gameController.setPlayerNickname("Bob");
        gameController.checkGameInitialization();
        gameController.setChosenColumn(column);
        assertEquals(column, game.getCurrentPlayer().getChosenColumn());
        // Assert other expected behavior
    }

    @Test
    public void testSetChosenColumn_InvalidColumn() {
        int column = -1;
        gameController.setNumberPlayers(2);
        gameController.setPlayerNickname("Alice");
        gameController.setPlayerNickname("Bob");
        gameController.checkGameInitialization();
        gameController.setChosenColumn(column);
        assertEquals(Warnings.INVALID_COLUMN, game.getErrorType());
        // Assert other expected behavior
    }

    @Test
    public void testSetChosenColumn_TooManyChosenTiles() {
        int column = 3;
        gameController.setNumberPlayers(2);
        gameController.setPlayerNickname("Alice");
        gameController.setPlayerNickname("Bob");
        gameController.checkGameInitialization();
        gameController.setChosenColumn(column);
        game.getCurrentPlayer().getChosenTiles().add(new Tile(Color.BLUE)); // Add a chosen tile
        game.getCurrentPlayer().getChosenTiles().add(new Tile(Color.BLUE)); // Add a chosen tile
        game.getCurrentPlayer().getChosenTiles().add(new Tile(Color.BLUE)); // Add a chosen tile
        game.getCurrentPlayer().getShelf().putTile(0, 3, new Tile(Color.BLUE)); // Add a tile to the shelf
        game.getCurrentPlayer().getShelf().putTile(1, 3, new Tile(Color.BLUE)); // Add a tile to the shelf
        game.getCurrentPlayer().getShelf().putTile(2, 3, new Tile(Color.BLUE)); // Add a tile to the shelf
        game.getCurrentPlayer().getShelf().putTile(3, 3, new Tile(Color.BLUE)); // Add a tile to the shelf
        gameController.setChosenColumn(column);
        assertEquals(Warnings.INVALID_COLUMN, game.getErrorType());
        // Assert other expected behavior
    }

    @Test
    public void testSetChosenColumn_OneChosenTile() {
        int column = 4;
        gameController.setNumberPlayers(2);
        gameController.setPlayerNickname("Alice");
        gameController.setPlayerNickname("Bob");
        gameController.checkGameInitialization();
        game.getCurrentPlayer().getChosenTiles().add(new Tile(Color.BLUE)); // Add a chosen tile
        Player p = game.getCurrentPlayer();
        gameController.setChosenColumn(column);
        assertEquals(column, p.getChosenColumn());
        // Assert other expected behavior, such as calling dropTile(1)
    }

    @Test
    public void testSetChosenColumn_MultipleChosenTiles() {
        int column = 4;
        gameController.setNumberPlayers(2);
        gameController.setPlayerNickname("Alice");
        gameController.setPlayerNickname("Bob");
        gameController.checkGameInitialization();
        game.getCurrentPlayer().getChosenTiles().add(new Tile(Color.BLUE));
        game.getCurrentPlayer().getChosenTiles().add(new Tile(Color.PINK));
        Player p = game.getCurrentPlayer();
        gameController.setChosenColumn(column);
        assertEquals(column, p.getChosenColumn());
        // Assert other expected behavior, such as calling askOrder()
    }
    @Test
    public void testCheckCorrectCoordinates_ValidCoordinates() {
        int[] coordinates = {1,3};
        gameController.setNumberPlayers(2);
        gameController.setPlayerNickname("Alice");
        gameController.setPlayerNickname("Bob");
        gameController.checkGameInitialization();
        game.getBoard().putTile(new Tile(Color.BLUE), coordinates); // Place a tile at the coordinates
        gameController.checkCorrectCoordinates(coordinates);
        assertTrue(game.getCurrentPlayer().getChosenCoordinates().contains(coordinates));
        assertNull(game.getBoard().getTile(coordinates[0], coordinates[1]).getColor());
    }

    @Test
    public void testCheckCorrectCoordinates_InvalidCoordinates() {
        int[] coordinates = {2, 2}; // Assuming there is no tile placed at these coordinates
        gameController.setNumberPlayers(2);
        gameController.setPlayerNickname("Alice");
        gameController.setPlayerNickname("Bob");
        gameController.checkGameInitialization();
        gameController.checkCorrectCoordinates(coordinates);
        assertEquals(Warnings.INVALID_TILE, game.getErrorType());
        // Assert other expected behavior
    }

    @Test
    public void testDropTile_ValidTilePosition() throws RemoteException {
        gameController.setNumberPlayers(2);
        gameController.setPlayerNickname("Alice");
        gameController.setPlayerNickname("Bob");
        gameController.checkGameInitialization();
        game.getCurrentPlayer().getChosenTiles().add(new Tile(Color.PINK));
        Player p = game.getCurrentPlayer();
        gameController.setChosenColumn(0); // Set the chosen column
        // Assert the expected behavior
        assertEquals(5, p.getShelf().checkColumnEmptiness(0));
    }

    @Test
    public void testDropTile_InvalidTilePosition() throws RemoteException {
        gameController.setNumberPlayers(2);
        gameController.setPlayerNickname("Alice");
        gameController.setPlayerNickname("Bob");
        gameController.checkGameInitialization();
        game.getCurrentPlayer().getChosenTiles().add(new Tile(Color.PINK));
        gameController.dropTile(0); // Drop the tile at position 0

        // Assert the expected behavior
        assertEquals(Warnings.INVALID_ORDER, game.getErrorType());
        // Assert other expected behavior
    }

    @Test
    public void testCalculateWinner() {
        gameController.setNumberPlayers(2);
        gameController.setPlayerNickname("Alice");
        gameController.setPlayerNickname("Bob");
        gameController.checkGameInitialization();
        assertFalse(game.isEnd());
        gameController.calculateWinner();
        assertTrue(game.isEnd());
    }

    @Test

    void testReconnectedPlayer_PlayerExists_PlayerConnected() {
        gameController.setNumberPlayers(2);
        gameController.setPlayerNickname("Alice");
        gameController.setPlayerNickname("Bob");
        gameController.checkGameInitialization();
        gameController.getPlayers().get(0).setConnected(false);
        gameController.reconnectedPlayer(gameController.getPlayers().get(0).getNickname());
        assertTrue(gameController.getPlayers().get(0).isConnected());
    }

    @Test
    void testReconnectedPlayer_PlayerDoesNotExist_NoChanges() {
        gameController.setNumberPlayers(3);
        gameController.setPlayerNickname("Alice");
        gameController.setPlayerNickname("Bob");
        gameController.setPlayerNickname("John");
        gameController.checkGameInitialization();
        gameController.getPlayers().get(0).setConnected(false);
        gameController.getPlayers().get(1).setConnected(false);
        assertFalse(gameController.getPlayers().get(0).isConnected());
        assertFalse(gameController.getPlayers().get(1).isConnected());
    }

    @Test
    public void testPutBackTiles() {
        // Arrange
        gameController.setNumberPlayers(2);
        gameController.setPlayerNickname("Alice");
        gameController.setPlayerNickname("Bob");
        gameController.checkGameInitialization();
        Tile tile1 = new Tile(Color.PINK);
        Tile tile2 = new Tile(Color.BLUE);
        List<Tile> chosenTiles = new ArrayList<>();

        game.getCurrentPlayer().addChosenTile(tile1);
        game.getCurrentPlayer().addChosenTile(tile2);

        int[] coordinate1 = {4, 1};
        int[] coordinate2 = {5, 1};
        List<int[]> chosenCoordinates = new ArrayList<>();
        game.getCurrentPlayer().addChosenCoordinate(coordinate1);
        game.getCurrentPlayer().addChosenCoordinate(coordinate2);


        // Act
        gameController.putBackTiles(game.getCurrentPlayer());

        // Assert
        //assertTrue(game.getCurrentPlayer().getChosenTiles().isEmpty());
        //assertTrue(game.getCurrentPlayer().getChosenCoordinates().isEmpty());
        //assertTrue(game.getBoard().getBoard().contains(tile1));
        //assertTrue(board.getTiles().contains(tile2));
        assertEquals(tile1, game.getBoard().getTile(coordinate1[0], coordinate1[1]));
        assertEquals(tile2, game.getBoard().getTile(coordinate2[0], coordinate2[1]));
    }
}
