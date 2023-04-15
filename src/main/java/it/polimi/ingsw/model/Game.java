package it.polimi.ingsw.model;


import it.polimi.ingsw.view.TextualUI;

import javax.swing.event.ChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static it.polimi.ingsw.Costants.*;

public class Game  {
    private int numberPartecipants;
    private List<Player> players;
    private CommonGoalCard[] commonGoals;
    private Board board;
    private Bag bag;
    private Player currentPlayer;
    //per capire se si è completata una shelf o meno (l'ho messo come attributo perchè veniva usato in startGame()
    private boolean lastTurn;

    public Game(int participants, List<Player> players){
        commonGoals = new CommonGoalCard[COMMON_CARDS_PER_GAME];
        numberPartecipants = participants;
        this.bag = new Bag();
        this.board = new Board(participants);
        this.board.fillBoard(this.bag);
        this.players = players;
        this.lastTurn = false;
        setFirstPlayer();

        DeckPersonal deckPersonal = new DeckPersonal();
        for (Player p : this.players)
            p.setPrivateCard(deckPersonal.popPersonalCard());

        DeckCommon deckCommon = new DeckCommon();
        for(int i=0; i<COMMON_CARDS_PER_GAME; i++)
            commonGoals[i] = new CommonGoalCard(participants, deckCommon);
    }


    //Getters and Setters
    public void setFirstPlayer(){
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
    public boolean isLastTurn() {
        return this.lastTurn;
    }
    public List<Player> getPlayers() {
        return players;
    }
    public Board getBoard() {
        return board;
    }
    public Bag getBag() {
        return bag;
    }




    public void startGame(){
        int index = 0;
        currentPlayer = players.get(index);

        while(!this.lastTurn){  //turni finché nessuno ha riempito una shelf
            System.out.println("Gioca: " + currentPlayer.nickname);
            // scelta delle Tiles di ogni player da passare a getTile
            // scelta della colonna
            // scelta ordine delle tiles
            this.board.printBoard();
            currentPlayer.play(this.board, this.commonGoals);
            currentPlayer.printShelf();

            if(this.board.checkRefill())
                this.board.fillBoard(this.bag);

            if (currentPlayer.getShelf().isFull()){
                    currentPlayer.addPoints(END_GAME_POINT);
                    lastTurn = true;
                }
            index = ( index + 1 ) % players.size();
            currentPlayer = players.get(index);
        }

        while(!players.iterator().next().getSeat()){  // ultimi turni finche il giocatore dopo e quello con la sedia
            currentPlayer.play(board, commonGoals);
            currentPlayer = players.iterator().next();
        }
    }
    public void endGame(){
        for(Player p : this.players) {
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
