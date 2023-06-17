package it.polimi.ingsw.model;



import java.util.ArrayList;
import java.util.List;
import static it.polimi.ingsw.util.Costants.*;

public class Player {
    final String nickname;
    private Shelf shelf;
    private boolean seat;
    private boolean[] goalsCompleted;
    private List<int[]> chosenCoordinates;
    private List<Tile> chosenTiles;
    private int chosenColumn;
    private PersonalGoalCard personalGoalCard;
    private int points;
    private int personalGoalPoints;
    private int adjacencyPoints;
    private boolean isChatting;

    public Player(String nickname){
        this.nickname = nickname;
        shelf = new Shelf();
        chosenCoordinates = new ArrayList<>();
        chosenTiles = new ArrayList<>();
        this.goalsCompleted = new boolean[COMMON_CARDS_PER_GAME];
        this.isChatting = false;
        points = 0;
        personalGoalPoints = 0;
        adjacencyPoints = 0;
        chosenColumn = -1;
    }
    public String getNickname(){return this.nickname;}
    public void setSeat(boolean seat) {
        boolean oldValue = this.seat;
        this.seat = seat;
    }
    public void setPrivateCard(PersonalGoalCard personalGoalCard){
        this.personalGoalCard = personalGoalCard;
    }
    public void addPoints(int points){
        this.points += points;
    }
    public void addPoints(CommonGoalCard card){
        this.points += card.getPoint();
    }

    public int getPersonalGoalPoints(){
        return this.personalGoalPoints;
    }
    public int checkPersonalPoints(){
        int count = 0;
        for (int i=0; i<SHELF_ROWS; i++){
            for(int j=0; j<SHELF_COLUMN; j++){
                if(personalGoalCard.goalsShelf[i][j]!=null && this.shelf.getTile(i, j)!=null &&
                        personalGoalCard.goalsShelf[i][j].getColor().equals(this.shelf.getTile(i, j).getColor()))
                    count++;
            }
        }
        return switch (count) {
            case 1 -> personalGoalPoints = 1;
            case 2 -> personalGoalPoints = 2;
            case 3 -> personalGoalPoints = 4;
            case 4 -> personalGoalPoints = 6;
            case 5 -> personalGoalPoints = 9;
            case 6 -> personalGoalPoints = 12;
            default -> personalGoalPoints = 0;
        };
    }
    public Shelf getShelf(){return this.shelf;}
    public int getPoints(){return this.points;}
    public List<int[]> getChosenCoordinates(){return this.chosenCoordinates;}
    public void addChosenCoordinate(int[] coordinates){
        this.chosenCoordinates.add(coordinates);
    }
    public void addChosenTile(Tile tile){
        this.chosenTiles.add(tile);

    }
    public PersonalGoalCard getPersonalGoalCard(){
        return this.personalGoalCard;
    }
    public void reset(CommonGoalCard[] cards){
        chosenTiles = new ArrayList<>();
        chosenCoordinates = new ArrayList<>(2);
        for (int i = 0; i < COMMON_CARDS_PER_GAME; i++) {
            if (!this.goalsCompleted[i] && cards[i].algorythm(this.shelf)) { // controlla per ogni common se e stato fatto l obiettivo
                addPoints(cards[i].getPoint());
                this.goalsCompleted[i] = true;
            }
        }
        this.adjacencyPoints = this.shelf.checkAdjacents();
        this.personalGoalPoints = checkPersonalPoints();
    }

    public List<Tile> getChosenTiles() {
        return chosenTiles;
    }
    public void setChosenTiles(List<Tile> chosenTiles) {
        this.chosenTiles = chosenTiles;
    }
    /*
    private List<Tile> chooseOrder(List<Tile> chosenTiles){
        System.out.println("Selezionare l'ordine di inserimento,\ndalla posizione PIU BASSA alla PIU ALTA:\n");
        List<Tile> tmp = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        do{
            for (int i = 0; i < chosenTiles.size(); i++) {
                System.out.println("[" + i + "]" + " " + chosenTiles.get(i).getColor());
            }
            try {
                int pos = Integer.parseInt(scanner.nextLine());
                if (pos < 0 || pos >= chosenTiles.size())
                    System.out.println("posizione non valida!\nRiprovare");
                else tmp.add(chosenTiles.remove(pos));
            }catch(NumberFormatException e){
                System.out.println("ERRORE! Non hai inserito un numero.\nRiprova");
            }
        }while (chosenTiles.size()!=0);
        return tmp;
    }
    */
    ////////////////////////////////////////////
    public int getChosenColumn() {
        return chosenColumn;
    }
    public void setChosenColumn(int chosenColumn) {
        this.chosenColumn = chosenColumn;
    }

    public int getAdjacencyPoints() {
        return adjacencyPoints;
    }

    public void setAdjacencyPoints(int adjacencyPoints) {
        this.adjacencyPoints = adjacencyPoints;
    }
}
