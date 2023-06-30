
package it.polimi.ingsw.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static it.polimi.ingsw.util.Costants.COMMON_CARDS_PER_GAME;
import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {
    private Player player;

    @BeforeEach
    public void setUp() {
        player = new Player("John");
    }

    @Test
    public void testGetNickname() {
        String nickname = player.getNickname();
        assertEquals("John", nickname);
    }

    @Test
    public void testSetSeat() {
        player.setSeat(true);
        boolean seat = player.getSeat();
        assertTrue(seat);
    }

    @Test
    public void testSetConnected() {
        player.setConnected(true);
        boolean connected = player.isConnected();
        assertTrue(connected);
    }

    @Test
    public void testSetPrivateCard() {
        PersonalGoalCard goalCard = new PersonalGoalCard("goalpersonal1");
        player.setPrivateCard(goalCard);
        PersonalGoalCard retrievedCard = player.getPersonalGoalCard();
        assertEquals(goalCard, retrievedCard);
    }

    @Test
    public void testAddPoints() {
        player.addPoints(10);
        int points = player.getPoints();
        assertEquals(10, points);
    }

    @Test
    public void testGetPersonalGoalPoints() {
        player.addPoints(20);
        int personalGoalPoints = player.getPersonalGoalPoints();
        assertEquals(0, personalGoalPoints);
    }

    @Test
    public void testCheckPersonalPoints() {
        Shelf shelf = player.getShelf();
        PersonalGoalCard goalCard = new PersonalGoalCard("goalpersonal1");
        goalCard.goalsShelf[0][0] = new Tile(Color.BLUE);
        shelf.setTile(0, 0, new Tile(Color.BLUE));

        player.setPrivateCard(goalCard);
        int personalGoalPoints = player.checkPersonalPoints();
        assertEquals(1, personalGoalPoints);
    }

    @Test
    public void testGetShelf() {
        Shelf shelf = player.getShelf();
        assertNotNull(shelf);
    }

    @Test
    public void testGetPoints() {
        int points = player.getPoints();
        assertEquals(0, points);
    }

    @Test
    public void testGetChosenCoordinates() {
        List<int[]> chosenCoordinates = player.getChosenCoordinates();
        assertNotNull(chosenCoordinates);
        assertTrue(chosenCoordinates.isEmpty());
    }

    @Test
    public void testAddChosenCoordinate() {
        int[] coordinates = {1, 2};
        player.addChosenCoordinate(coordinates);
        List<int[]> chosenCoordinates = player.getChosenCoordinates();
        assertEquals(1, chosenCoordinates.size());
        assertArrayEquals(coordinates, chosenCoordinates.get(0));
    }

    @Test
    public void testAddChosenTile() {
        Tile tile = new Tile(Color.PINK);
        player.addChosenTile(tile);
        List<Tile> chosenTiles = player.getChosenTiles();
        assertEquals(1, chosenTiles.size());
        assertEquals(tile, chosenTiles.get(0));
    }

    @Test
    public void testReset() {




        int points = player.getPoints();
        int personalGoalPoints = player.getPersonalGoalPoints();

        assertEquals(0, points);
        assertEquals(0, personalGoalPoints);
    }

    @Test
    public void testGetChosenTiles() {
        List<Tile> chosenTiles = player.getChosenTiles();
        assertNotNull(chosenTiles);
        assertTrue(chosenTiles.isEmpty());
    }

    @Test
    public void testGetChosenColumn() {
        int chosenColumn = player.getChosenColumn();
        assertEquals(-1, chosenColumn);
    }

    @Test
    public void testSetChosenColumn() {
        player.setChosenColumn(2);
        int chosenColumn = player.getChosenColumn();
        assertEquals(2, chosenColumn);
    }

    @Test
    public void testGetAdjacencyPoints() {
        int adjacencyPoints = player.getAdjacencyPoints();
        assertEquals(0, adjacencyPoints);
    }



}

