package it.polimi.ingsw.model;


import it.polimi.ingsw.distributed.rmi.ServerImpl;
import it.polimi.ingsw.distributed.socket.ServerHandler;
import it.polimi.ingsw.util.Warnings;
import it.polimi.ingsw.util.ModelListener;



import java.util.ArrayList;
import java.util.List;

import java.util.Random;

import static it.polimi.ingsw.util.Costants.*;

public class Game {
    private int numberPartecipants;
    private List<Player> players;
    private CommonGoalCard[] commonGoals;
    private Board board;
    private Bag bag;
    private Player currentPlayer;
    private Chat chat;
    //per capire se si è completata una shelf o meno (l'ho messo come attributo perchè veniva usato in startGame()
    private boolean lastTurn;
    private boolean start = false;
    private boolean end=false;
    private List<ModelListener> listener;
    private Warnings errorType = null;

    public void startGame(int numberParticipants, List<Player> players){
        this.chat = new Chat();
        this.numberPartecipants = numberParticipants;
        this.players = new ArrayList<>();
        this.bag = new Bag();
        this.board = new Board(this.numberPartecipants);
        this.board.fillBoard(this.bag);
        this.board.setBorderTiles();
        this.players = players;
        setFirstPlayer();
        this.currentPlayer = this.players.get(0);
        this.commonGoals = new CommonGoalCard[2];
        DeckCommon deckCommon = new DeckCommon();
        for(int i=0; i<COMMON_CARDS_PER_GAME; i++)
            commonGoals[i] = new CommonGoalCard(this.numberPartecipants, deckCommon);
        DeckPersonal deckPersonal = new DeckPersonal();
        for (int i=0; i<this.players.size(); i++)
            this.players.get(i).setPrivateCard(deckPersonal.popPersonalCard());
        listener.forEach(ModelListener::printGame);//listener.printGame();
        listener.forEach(x->x.gameStarted(currentPlayer));//listener.newTurn(currentPlayer);
    }


    public Chat getChat() {
        return chat;
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
        listener.forEach(ModelListener::isLastTurn);//listener.isLastTurn();
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
        this.start = s;
    }
    public void setEnd(boolean e){
        this.end = e;
    }
    ////////////////////////////////////////////////////
    public Tile popTileFromBoard(int[] coordinates){
        Tile poppedTile = this.board.popTile(coordinates[0], coordinates[1]);
        listener.forEach(ModelListener::printGame);//listener.printGame();
        return poppedTile;
    }
    public void setChosenColumnByPlayer(int c){
        this.currentPlayer.setChosenColumn(c);
        listener.forEach(ModelListener::askOrder);//listener.askOrder();
    }
    public Warnings getErrorType() {
        return errorType;
    }
    public void setErrorType(Warnings errorType){
        this.errorType = errorType;
        listener.forEach(x->x.warning(this.errorType, this.currentPlayer));//listener.warning(errorType, this.currentPlayer);
    }
    public void setErrorType(Warnings errorType, String nickname){
        listener.forEach(x->x.warning(errorType, nickname));//listener.warning(errorType, this.currentPlayer);
    }

    public void droppedTile(Tile tile, int column){
        this.currentPlayer.getShelf().dropTile(tile, column);
        listener.forEach(ModelListener::printGame);//listener.printGame();
        if(!this.currentPlayer.getChosenTiles().isEmpty())
            listener.forEach(ModelListener::askOrder);//listener.askOrder();
    }
    public void newTurn(){
        listener.forEach(ModelListener::printGame);//listener.printGame();
        listener.forEach(x->x.newTurn(this.currentPlayer));//listener.newTurn(this.currentPlayer);
    }

    ////////////////////////////////////////////////////
    public void endGame() {
        setEnd(true);
        for(Player p : this.players) {
            p.addPoints(p.getShelf().checkAdjacents());
            p.addPoints(p.checkPersonalPoints());

            listener.forEach(ModelListener::finalPoints);//this.listener.finalPoints();
            //punti dai gruppi sulla shelf aggiunti qui
            //punti personalGoalCard aggiunti qui
            //punti delle commonGoals gia eventualmente aggiunti
            //punto della fine della partita gia assegnato
        }
        this.listener.forEach(ModelListener::finalPoints);//finalPoints();

    }


    public void addModelListener(ModelListener l){
        if (listener == null)
            listener = new ArrayList<>();
        listener.add(l);
    }
    public void selectionControl() {
        if (this.currentPlayer.getChosenTiles().size()==0) {
            listener.forEach(x->x.warning(Warnings.INVALID_ACTION, this.getCurrentPlayer()));//listener.warning(Warnings.INVALID_ACTION, this.getCurrentPlayer());
        } else {
            listener.forEach(ModelListener::askColumn);//listener.askColumn();
        }
    }
    public void checkMaxNumberOfTilesChosen() {
        if (this.currentPlayer.getShelf().getMaxColumnSpace() == this.currentPlayer.getChosenTiles().size() ||
                getAvailableTilesForCurrentPlayer().isEmpty() ||
                this.currentPlayer.getChosenTiles().size() == 3){
            listener.forEach(x->x.warning(Warnings.MAX_TILES_CHOSEN, this.getCurrentPlayer()));//this.listener.warning(Warnings.MAX_TILES_CHOSEN, this.getCurrentPlayer());
        }
        else listener.forEach(x->x.askAction());
    }

    public boolean isEnd() {
        return end;
    }
    public boolean isStart() {
        return start;
    }

    public void newMessage(String sender, String receiver, String message)  {
        this.chat.newMessage(sender, receiver, message);
        listener.forEach(ModelListener::printGame);
    }

}
