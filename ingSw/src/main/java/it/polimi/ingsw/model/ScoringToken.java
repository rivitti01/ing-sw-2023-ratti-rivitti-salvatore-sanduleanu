package it.polimi.ingsw.model;

public class ScoringToken {
    final int score;

    public int getScore(){
        return this.score;
    }
    public ScoringToken(int points){
        this.score = points;
    }
    /*public void givePoint(Player player){
        player.addPoints(); // va ragionato o tolto del tutto
    }*/
}
