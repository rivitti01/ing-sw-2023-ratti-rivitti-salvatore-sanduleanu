package it.polimi.ingsw.model;
import it.polimi.ingsw.model.Algorythms.*;
import it.polimi.ingsw.model.Board;

import java.util.ArrayList;
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

    public Game(int participants, List<Player> players){
        commonGoals = new CommonGoalCard[COMMON_CARDS_PER_GAME];
        numberPartecipants = participants;
        this.bag = new Bag();
        this.board = new Board(participants);
        this.board.fillBoard(this.bag);
        this.players = players;
        DeckPersonal deckPersonal = new DeckPersonal();
        setFirstPlayer();
        for (Player p : players) {
            p.setPrivateCard(deckPersonal.popPersonalCard());
        }
        commonGoals = new CommonGoalCard[2];
        commonGoals[0] = new CommonGoalCard(participants);
        commonGoals[1] = new CommonGoalCard(participants);
    }

    private void setFirstPlayer(){
        Random random = new Random();
        int tmp = random.nextInt(this.numberPartecipants);
        List<Player> tempList = new ArrayList<>();
        while(players.size() != 0){
            if (tmp < players.size()){
                tempList.add(players.remove(tmp));
            }else {
                tempList.add(players.remove(0));
            }
        }
        players = tempList;
        players.get(0).setSeat(true);
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
