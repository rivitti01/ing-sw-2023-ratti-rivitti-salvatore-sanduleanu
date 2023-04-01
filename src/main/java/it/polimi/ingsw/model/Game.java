package it.polimi.ingsw.model;

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
    private Player currentPlayer;

    public Game(int participants, List<Player> players){
        commonGoals = new CommonGoalCard[COMMON_CARDS_PER_GAME];
        numberPartecipants = participants;
        this.bag = new Bag();
        this.board = new Board(participants);
        this.board.fillBoard(this.bag);
        this.players = players;
        setFirstPlayer();

        DeckPersonal deckPersonal = new DeckPersonal();
        for (Player p : players)
            p.setPrivateCard(deckPersonal.popPersonalCard());

        DeckCommon deckCommon = new DeckCommon();
        for(int i=0; i<COMMON_CARDS_PER_GAME; i++)
            commonGoals[i] = new CommonGoalCard(participants, deckCommon);
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

        currentPlayer = players.get(0);

        while(!lastTurn){  // turni finche nessuno ha riempito una shelf
            currentPlayer.play(this.board, this.commonGoals);

            if (currentPlayer.getShelf().isFull()){
                    currentPlayer.addPoints(END_GAME_POINT);
                    lastTurn = true;
                }
            if(players.iterator().hasNext()) //non sono ancora arrivato in fondo alla lista di players
                currentPlayer = players.iterator().next();
            else    //se arrivo in fondo torno al player con la sedia
                currentPlayer = players.get(0);
        }

        while(!players.iterator().next().getSeat()){  // ultimi turni finche il giocatore dopo e quello con la sedia
            currentPlayer.play(board, commonGoals);
            currentPlayer = players.iterator().next();
        }
    }

    public void endGame(){
        for(Player p : players) {
            p.addPoints(p.getShelf().checkAdjacents());
            p.addPoints(p.checkPersonalPoints());
            //punti dai gruppi sulla shelf aggiunti qui
            //punti personalGoalCard aggiunti qui
            //punti delle commonGoals gia eventualmente aggiunti ai punteggi in player.play()
            //punto della fine della partita e stato gia assegnato in game.startGame()
        }

    }
    public Player findWinner(){
        Player tempWinner = players.get(0);
        for(int i=1; i<players.size(); i++){
            if(players.get(i).getPoints() >= players.get(i-1).getPoints())
                tempWinner = players.get(i);
        }
         return tempWinner;
    }
}
