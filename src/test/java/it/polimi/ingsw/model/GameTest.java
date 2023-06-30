package it.polimi.ingsw.model;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.distributed.First;
import it.polimi.ingsw.distributed.rmi.ServerRMIImpl;
import it.polimi.ingsw.util.ModelListener;
import it.polimi.ingsw.util.Warnings;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import static javax.management.Query.times;
import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    private Game game;
    private GameController gameController;
    private List<Player> players;
    private ServerRMIImpl server;
    private First first;
    @BeforeEach
    void setUp() throws RemoteException {
        game = new Game();
        gameController = new GameController(game);
        players = new ArrayList<>();
        first = new First();
        server = new ServerRMIImpl(game, gameController, first);
        game.addModelListener(server);
        players.add(new Player("John"));
        players.add(new Player("Alice"));
        game.startGame(2, players);
    }

    @Test
    void startGame() {
        // Call the method


        // Verify that the game is initialized correctly
        assertNotNull(game.getPlayers());
        assertEquals(2, game.getPlayers().size());
        assertNotNull(game.getCommonGoals());
        assertEquals(2, game.getCommonGoals().length);
        assertNotNull(game.getCurrentPlayer());

        // Verify that the dependencies are initialized
        assertNotNull(game.getBag());
        assertNotNull(game.getBoard());
        assertNotNull(game.getChat());
        assertFalse(game.getBoard().checkRefill());
        assertNotNull(game.getBoard().getBorderTiles());

    }

    @Test
    void getListener() {
        // Call the method and assert the result
        assertNotNull(game.getListener());
        for(ModelListener listener : game.getListener()){
            assertNotNull(listener);
        }
    }

    @Test
    void getChat() {
        // Call the method and assert the result
        assertNotNull(game.getChat());
    }

    @Test
    void setFirstPlayer() {
        // Call the method
        game.setFirstPlayer(players);

        // Verify that the first player and current player are set correctly
        assertNotNull(game.getPlayers());
        assertEquals(players.size(), game.getPlayers().size());
        assertNotNull(game.getCurrentPlayer());
        assertTrue(game.getPlayers().get(0).getSeat());
    }

    @Test
    void isLastTurn() {
        // Set the last turn flag
        game.setLastTurn(true);

        // Call the method and assert the result
        assertTrue(game.isLastTurn());
    }

    @Test
    void setLastTurn() {
        // Set the last turn flag
        game.setLastTurn(true);

        // Call the method and assert the flag is set correctly
        assertTrue(game.isLastTurn());
    }

    @Test
    void getPlayers() {
        // Call the method and assert the result
        assertNotNull(game.getPlayers());
    }

    @Test
    void getBoard() {
        // Call the method and assert the result
        assertNotNull(game.getBoard());
    }

    @Test
    void getBag() {
        // Call the method and assert the result
        assertNotNull(game.getBag());
    }

    @Test
    void getCommonGoals() {
        // Call the method and assert the result
        assertNotNull(game.getCommonGoals());
    }

    @Test
    void getCurrentPlayer() {
        // Call the method and assert the result
        assertNotNull(game.getCurrentPlayer());
    }

    @Test
    public void testGetAvailableTilesForCurrentPlayer_NoChosenTiles_ShouldReturnAllBorderTiles() {
        // Arrange
        List<int[]> expectedTiles = game.getBoard().getBorderTiles();

        // Act
        List<int[]> actualTiles = game.getAvailableTilesForCurrentPlayer();

        // Assert
        assertEquals(expectedTiles, actualTiles);
    }


    @Test
    void setCurrentPlayer() {
        Player currentPlayer = new Player("John");
        game.setCurrentPlayer(currentPlayer);

        assertEquals(currentPlayer, game.getCurrentPlayer(), "Current player should be set correctly");

    }

    @Test
    void setStart() {
        // Set start to true
        game.setStart(true);

        assertTrue(game.isStart(), "Start flag should be true");
    }

    @Test
    void setEnd() {
        // Set end to true
        game.setEnd(true);

        assertTrue(game.isEnd(), "End flag should be true");
    }

    @Test
    void popTileFromBoard() {

        // Specify the coordinates of the tile to be popped
        int[] coordinates = {2, 3};

        Tile tiletopop = game.getBoard().getTile(coordinates[0], coordinates[1]);

        // Call the method under test
        Tile poppedTile = game.popTileFromBoard(coordinates);


        // Verify that the popped tile is the same as the one returned by the board
        assertEquals(tiletopop, poppedTile);
    }

    @Test
    void setChosenColumnByPlayer() {// Arrange
        int chosenColumn = 2;

        // Act
        game.setChosenColumnByPlayer(chosenColumn);
        int actualChosenColumn = game.getCurrentPlayer().getChosenColumn();

        // Assert
        assertEquals(chosenColumn, actualChosenColumn);
    }

    @Test
    void askOrder() {
        // Call the method
        game.askOrder();

    }

    @Test
    void setErrorType() {
        // Arrange
        Warnings errorType = Warnings.INVALID_ACTION;

        // Act
        game.setErrorType(errorType);

        // Assert
        assertEquals(errorType, game.getErrorType());
    }

    @Test
    void getErrorType() {
        // Assert
        assertNull(game.getErrorType());
    }

    @Test
    void droppedTile() {
        // Create a tile and drop it into the player's shelf
        Tile tile = new Tile(Color.PINK, 3);
        game.droppedTile(tile, 0);

        // Check if the tile was dropped successfully
        Shelf playerShelf = game.getCurrentPlayer().getShelf();
        Assertions.assertEquals(tile, playerShelf.getTile(5, 0));
        Assertions.assertNull(playerShelf.getTile(0, 1));
    }

    @Test
    void newTurn() {
        // Call the method
        game.newTurn();

        // Verify that the current player is set correctly
        assertNotNull(game.getCurrentPlayer());
    }

    @Test
    void resumingTurn() {
        // Call the method
        game.resumingTurn();

        // Verify that the current player is set correctly
        assertNotNull(game.getCurrentPlayer());
    }

    @Test
    void endGame() {

        // Add points to players
        players.get(0).addPoints(10);
        players.get(1).addPoints(5);

        // End the game
        game.endGame();

        // Verify that the game ended
        assertTrue(game.isEnd());
    }

    @Test
    void addModelListener() throws RemoteException {
        ModelListener newListener = new ServerRMIImpl(game, gameController, first);

        // Verify that the new listener was added
        Assertions.assertTrue(game.getListener().contains(newListener));
    }

    @Test
    void removeModelListener() throws RemoteException {
        ModelListener newListener = new ServerRMIImpl(game, gameController, first);

        // Remove the listener
        game.removeModelListener(newListener);

        // Verify that the listener was removed
        Assertions.assertFalse(game.getListener().contains(newListener));
    }

    @Test
    void selectionControl() {
        game.selectionControl();
    }

    @Test
    void checkMaxNumberOfTilesChosen() {
        Tile t1 = new Tile(Color.PINK, 3);
        Tile t2 = new Tile(Color.PINK, 3);
        Tile t3 = new Tile(Color.PINK, 3);
        game.getCurrentPlayer().addChosenTile(t1);
        game.getCurrentPlayer().addChosenTile(t2);
        game.getCurrentPlayer().addChosenTile(t3);
        game.checkMaxNumberOfTilesChosen();
    }

    @Test
    void isEnd() {
        boolean isEnd = game.isEnd();

        assertFalse(isEnd);
    }

    @Test
    void isStart() {
        boolean isStart = game.isStart();

        assertFalse(isStart);
    }

    @Test
    void newMessage() {
        String sender = "Player1";
        String receiver = "Player2";
        String message = "Hello!";

        game.newMessage(sender, receiver, message);

        assertEquals(1, game.getChat().getChat().size());
        ChatMessage cm = game.getChat().getChat().get(0);
        assertEquals(sender, cm.getSender());
        assertEquals(receiver, cm.getReceiver());
        assertEquals(message, cm.getMessage());
    }
}