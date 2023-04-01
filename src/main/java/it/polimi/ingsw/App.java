package it.polimi.ingsw;

import it.polimi.ingsw.model.*;


import java.util.ArrayList;
import java.util.List;


public class App 
{
    public static void main( String[] args ) {

        List<Player> players = new ArrayList<>();
        //creare players con un ciclo che prende i nicknames

        Game game = new Game(4, players);
        game.startGame();
        game.endGame();

        System.out.println("HA VINTO :\n" + game.findWinner().getNickname());







    }
}
