/*
package it.polimi.ingsw.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {
    Player p;
    Player p2;
    Player p3;
    Player p4;
    PersonalGoalCard chosenCard;
    Board board;
    Bag bag;
    DeckCommon deckCommon;
    CommonGoalCard[] commonGoalCards;
   List<int[]> chosenCoords;


    @BeforeEach
    void setup(){
        this.bag = new Bag();
        this.chosenCard = new PersonalGoalCard("goalStrategy1");
        this.p = new Player("denis", this.chosenCard);
        this.p2 = new Player("denis2", this.chosenCard);
        this.p3 = new Player("denis3", this.chosenCard);
        this.p4 = new Player("denis4", this.chosenCard);
        this.board = new Board(4);
        this.board.fillBoard(bag);
        this.deckCommon = new DeckCommon();
        commonGoalCards = new CommonGoalCard[2];
        for(int i=0; i<2; i++)
            commonGoalCards[i] = new CommonGoalCard(4, deckCommon);
    }

    @Test
    void checkPersonalPoints(){
       // this.p.getShelf().putTile(0,0,new Tile(Color.PINK));
        this.p.getShelf().putTile(0,2,new Tile(Color.BLUE));
        this.p.getShelf().putTile(1,4,new Tile(Color.GREEN));
        this.p.getShelf().putTile(2,3,new Tile(Color.WHITE));
        this.p.getShelf().putTile(3,1,new Tile(Color.YELLOW));
        this.p.getShelf().putTile(5,2,new Tile(Color.CYAN));
        this.p.getShelf().putTile(0,1,new Tile(Color.GREEN));
        this.p.getShelf().putTile(5,0,new Tile(Color.WHITE));
        int points1 = p.checkPersonalPoints();
        int expectedPoints = 12;
        assertEquals(points1, expectedPoints);     //ok

        this.p2.getShelf().putTile(0,0,new Tile(Color.WHITE));
        this.p2.getShelf().putTile(0,2,new Tile(Color.WHITE));
        this.p2.getShelf().putTile(1,4,new Tile(Color.GREEN));
        this.p2.getShelf().putTile(2,3,new Tile(Color.WHITE));
        this.p2.getShelf().putTile(3,1,new Tile(Color.YELLOW));
        this.p2.getShelf().putTile(5,2,new Tile(Color.CYAN));
        this.p2.getShelf().putTile(0,1,new Tile(Color.GREEN));
        this.p2.getShelf().putTile(5,0,new Tile(Color.WHITE));
        int points2 = p2.checkPersonalPoints();
        int expectedPoints2 = 6;
        assertEquals(points2, expectedPoints2);    //ok

        this.p3.getShelf().putTile(0,0,new Tile(Color.WHITE));
        this.p3.getShelf().putTile(0,2,new Tile(Color.WHITE));
        this.p3.getShelf().putTile(1,4,new Tile(Color.WHITE));
        this.p3.getShelf().putTile(2,3,new Tile(Color.BLUE));
        this.p3.getShelf().putTile(3,1,new Tile(Color.WHITE));
        this.p3.getShelf().putTile(5,2,new Tile(Color.WHITE));
        this.p3.getShelf().putTile(0,1,new Tile(Color.GREEN));
        this.p3.getShelf().putTile(5,0,new Tile(Color.WHITE));
        int points3 = p3.checkPersonalPoints();
        int expectedPoints3 = 0;
        assertEquals(points3, expectedPoints3);   //ok

        int points4 = this.p4.checkPersonalPoints();
        int expectedPoints4 = 0;
        assertEquals(points4, expectedPoints4);  //shelf vuota ok

    }



}

 */