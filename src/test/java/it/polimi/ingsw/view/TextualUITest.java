package it.polimi.ingsw.view;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.Bag;
import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.GameView;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TextualUITest {
    TextualUI tui = new TextualUI();


    private GameView gv;
    private Game g;
    private GameController gc;


    @BeforeEach
    void setUp(){
      g =new Game();

    }

    @Test
    void run() {

        Bag bag = new Bag();
        Board board = new Board(2);
        board.fillBoard(bag);
        tui.printBoard(board);
    }





}