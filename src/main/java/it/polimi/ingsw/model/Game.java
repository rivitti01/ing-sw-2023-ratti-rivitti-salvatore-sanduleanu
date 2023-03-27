package it.polimi.ingsw.model;
import it.polimi.ingsw.model.Algorythms.*;
import it.polimi.ingsw.model.Board;

import java.util.List;
import java.util.Random;

import static it.polimi.ingsw.Costants.*;

public class Game {
    private int numberPartecipants;
    private List<Player> players;
    private CommonGoalCard[] commonGoals;
    private Board board;
    private Bag bag;
    private boolean hasStarted;
    private boolean endPointGiven;
    private Player currentPlayer;

    public Game(int partecipants){
        commonGoals = new CommonGoalCard[COMMON_CARDS_PER_GAME];
        numberPartecipants = partecipants;
        this.bag = new Bag();
        this.board = new Board(partecipants);
        this.board.fillBoard(this.bag);


        DeckPersonal deckPersonal = new DeckPersonal();
        setFirstPlayer();
        for (Player p : players) {
            p.setPrivateCard(deckPersonal.popPersonalCard());
        }

            Random random = new Random();
            //si potrebbe fare deck common che crea e shuffla e fa pop come il deckPersonals (non so se come ho fatto qui cosi si evitano ripetizioni)
            CardStrategy[] strategies = {new CommonGoalCard1(), new CommonGoalCard2(), new CommonGoalCard3(), new CommonGoalCard4(), new CommonGoalCard5(), new CommonGoalCard6(), new CommonGoalCard7(), new CommonGoalCard8(), new CommonGoalCard9(), new CommonGoalCard10(), new CommonGoalCard11(), new CommonGoalCard12()};
            for(int i=0; i< COMMON_CARDS_PER_GAME; i++){
                int tmp = random.nextInt(DECK_SIZE); //random numbers between 0 and DECK_SIZE-1
                commonGoals[i] = new CommonGoalCard(strategies[tmp], partecipants);
            } // VA CAMBIATO

    }

    private void setFirstPlayer(){
        Random random = new Random();
        int tmp = random.nextInt(this.numberPartecipants);
        for(int i=0; i< players.size(); i++) {
            if(i==tmp)
                players.get(i).setSeat(true);
            else
                players.get(i).setSeat(false);
        }
    }

    public void startGame(){
        boolean lastTurn = false;
        //players.get(0).getTiles(board); // NON E DETTO CHE GET(0) ABBIA LA SEDIA
        //........
        for (Player p: players){
            if (p.getSeat()) currentPlayer = p;
        }
        while(!lastTurn){
            currentPlayer.play(this.board, this.commonGoals);

            if (currentPlayer.getShelf().isFull()){
                    currentPlayer.addPoints(END_GAME_POINT);
                    lastTurn = true;
                }
            }
    }
    public void endGame(){}
    public Player findWinner(){
        return null; // CONTROLLO SU OGNI POINTS DI PLAYER....
    }//cose sul main
}
