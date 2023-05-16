package it.polimi.ingsw.model;


import it.polimi.ingsw.distributed.Client;
import it.polimi.ingsw.util.Warnings;
import it.polimi.ingsw.util.ModelListener;
import it.polimi.ingsw.util.Warnings;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static it.polimi.ingsw.util.Costants.*;

public class Game {
    private int numberPartecipants;
    private List<Player> players= new ArrayList<>();
    private CommonGoalCard[] commonGoals;
    private Board board;
    private Bag bag;
    private Player currentPlayer;
    //per capire se si è completata una shelf o meno (l'ho messo come attributo perchè veniva usato in startGame()
    private boolean lastTurn;
    private boolean start = false;
    private boolean end=false;
    private ModelListener listener;
    private Warnings errorType = null;

    public void startGame(int numberParticipants, Map<String, Client> players){
        this.numberPartecipants = numberParticipants;
        this.bag = new Bag();
        this.board = new Board(this.numberPartecipants);
        this.board.fillBoard(this.bag);
        this.board.setBorderTiles();
        for(Map.Entry<String, Client> entry : players.entrySet()){
            this.players.add(new Player(entry.getKey()));
        }
        setFirstPlayer();
        this.currentPlayer = this.players.get(0);
        this.commonGoals = new CommonGoalCard[2];
        DeckCommon deckCommon = new DeckCommon();
        for(int i=0; i<COMMON_CARDS_PER_GAME; i++)
            commonGoals[i] = new CommonGoalCard(this.numberPartecipants, deckCommon);
        DeckPersonal deckPersonal = new DeckPersonal();
        for (int i=0; i<this.players.size(); i++)
            this.players.get(i).setPrivateCard(deckPersonal.popPersonalCard());
        listener.printGame();
        listener.newTurn(currentPlayer);
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
        setCurrentPlayer(players.get(0));
    }
    public boolean isLastTurn() {
        return this.lastTurn;
    }
    public void setLastTurn(boolean lastTurn) {
        this.lastTurn = lastTurn;
        listener.isLastTurn();
    }
    public List<Player> getPlayers() {
        return this.players;
    }
    public Board getBoard() {
        return board;
    }
    public Bag getBag() {
        return bag;
    }
    public CommonGoalCard[] getCommonGoals() {
        return commonGoals;
    }
    public Player getCurrentPlayer() {
        return currentPlayer;
    }
    public List<int[]> getAvailableTilesForCurrentPlayer(){
        int[]  chosenCoordinates1;
        int[]  chosenCoordinates2;
        try {
            chosenCoordinates1 = this.currentPlayer.getChosenCoordinates().get(0);
        }catch (IndexOutOfBoundsException e1){
            chosenCoordinates1 = null;
        }
        try{
            chosenCoordinates2 = this.currentPlayer.getChosenCoordinates().get(1);
        }catch (IndexOutOfBoundsException e2){
            chosenCoordinates2 = null;
        }
        return this.board.filterAvailableTiles(chosenCoordinates1, chosenCoordinates2, this.board.getBorderTiles());
    }
    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }
    public void setStart(boolean s){
        boolean old = this.start;
        this.start = s;
    }
    public void setEnd(boolean e){
        boolean old = this.end;
        this.end = e;
    }
    ////////////////////////////////////////////////////
    public Tile popTileFromBoard(int[] coordinates){
        Tile poppedTile = this.board.popTile(coordinates[0], coordinates[1]);
        listener.printGame();
        return poppedTile;
    }
    public void setChosenColumnByPlayer(int c){
        this.currentPlayer.setChosenColumn(c);

        listener.askOrder();

    }
    public Warnings getErrorType() {
        return errorType;
    }
    public void setErrorType(Warnings errorType){
        this.errorType = errorType;
        listener.error(errorType, this.currentPlayer);
    }
    public void droppedTile(Tile tile, int column){
        this.currentPlayer.getChosenTiles().remove(tile);
        this.currentPlayer.getShelf().dropTile(tile, column);
        listener.printGame();
        if(!this.currentPlayer.getChosenTiles().isEmpty())
            listener.askOrder();
    }
    public void newTurn(){
        listener.printGame();
        listener.newTurn(this.currentPlayer);
    }

    ////////////////////////////////////////////////////
    public void endGame(){
        setEnd(true);
        for(Player p : this.players) {
            p.addPoints(p.getShelf().checkAdjacents());
            p.addPoints(p.checkPersonalPoints());
            //punti dai gruppi sulla shelf aggiunti qui
            //punti personalGoalCard aggiunti qui
            //punti delle commonGoals gia eventualmente aggiunti
            //punto della fine della partita gia assegnato
        }

    }
    public void findWinner(){
        Player tempWinner = players.get(0);
        for(int i=1; i<players.size(); i++){
            if(players.get(i).getPoints() >= players.get(i-1).getPoints())
                tempWinner = players.get(i);
        }
    }
    public void addModelListener(ModelListener l){
        this.listener = l ;
    }
    public void selectionControl() {
        if (this.currentPlayer.getChosenTiles().size()==0) {
            listener.error(Warnings.INVALID_ACTION, this.getCurrentPlayer());
        } else {
            listener.askColumn();
        }
    }
    public void checkMaxNumberOfTilesChosen() {
        if (this.currentPlayer.getShelf().getMaxColumnSpace() == this.currentPlayer.getChosenTiles().size() ||
                getAvailableTilesForCurrentPlayer().isEmpty() ||
                this.currentPlayer.getChosenTiles().size() == 3){
            this.listener.error(Warnings.MAX_TILES_CHOSEN, this.getCurrentPlayer());
        }else{
            listener.askAction();
        }
    }
}
