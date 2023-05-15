package it.polimi.ingsw.view;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.GameView;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TextualUITest {


    private GameView gv;
    private Game g;
    private GameController gc;


    @BeforeEach
    void setUp(){
      g =new Game();

    }

    @Test
    void run() {
    }


}