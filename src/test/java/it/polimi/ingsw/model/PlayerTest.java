package it.polimi.ingsw.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {
    Player p;
    PersonalGoalCard chosenCard;
    Board board;
    Bag bag;
   List<int[]> chosenCoords;


    @BeforeEach
    void setup(){
        bag = new Bag();
        this.chosenCard = new PersonalGoalCard("goalStrategy1");
        this.p = new Player("denis", chosenCard);
        board = new Board(4);
        board.fillBoard(bag);
        chosenCoords = new ArrayList<>(2);
    }

    @Test
    void checkPersonalPoints(){
       /* this.p.getShelf().putTile(0,0,new Tile(Color.PINK));
        this.p.getShelf().putTile(0,2,new Tile(Color.BLUE));
        this.p.getShelf().putTile(1,4,new Tile(Color.GREEN));
        this.p.getShelf().putTile(2,3,new Tile(Color.WHITE));
        this.p.getShelf().putTile(3,1,new Tile(Color.YELLOW));
        this.p.getShelf().putTile(5,2,new Tile(Color.CYAN));
        this.p.getShelf().putTile(0,1,new Tile(Color.GREEN));
        this.p.getShelf().putTile(5,0,new Tile(Color.WHITE));
        int points = p.checkPersonalPoints();
        int expectedPoints = 12;
        assertEquals(points, expectedPoints); */    //ok

      /*  this.p.getShelf().putTile(0,0,new Tile(Color.WHITE));
        this.p.getShelf().putTile(0,2,new Tile(Color.WHITE));
        this.p.getShelf().putTile(1,4,new Tile(Color.GREEN));
        this.p.getShelf().putTile(2,3,new Tile(Color.WHITE));
        this.p.getShelf().putTile(3,1,new Tile(Color.YELLOW));
        this.p.getShelf().putTile(5,2,new Tile(Color.CYAN));
        this.p.getShelf().putTile(0,1,new Tile(Color.GREEN));
        this.p.getShelf().putTile(5,0,new Tile(Color.WHITE));
        int points = p.checkPersonalPoints();
        int expectedPoints = 6;
        assertEquals(points, expectedPoints); */   //ok

        /*this.p.getShelf().putTile(0,0,new Tile(Color.WHITE));
        this.p.getShelf().putTile(0,2,new Tile(Color.WHITE));
        this.p.getShelf().putTile(1,4,new Tile(Color.WHITE));
        this.p.getShelf().putTile(2,3,new Tile(Color.BLUE));
        this.p.getShelf().putTile(3,1,new Tile(Color.WHITE));
        this.p.getShelf().putTile(5,2,new Tile(Color.WHITE));
        this.p.getShelf().putTile(0,1,new Tile(Color.GREEN));
        this.p.getShelf().putTile(5,0,new Tile(Color.WHITE));
        int points = p.checkPersonalPoints();
        int expectedPoints = 0;
        assertEquals(points, expectedPoints); */   //ok

        /*int points = this.p.checkPersonalPoints();
        int expectedPoints = 0;
        assertEquals(points, expectedPoints); */  //shelf vuota ok

    }

    @Test
    void getTile(){
        //serve gestire lo scanner se no entra in wait
        this.p.getTile(board, chosenCoords);
    }

    @Test
    void play() {
    }

    @Test
    void exeptionTester(){


    }

}