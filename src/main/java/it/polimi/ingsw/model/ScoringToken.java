package it.polimi.ingsw.model;

public class ScoringToken {
    final int score;

    /*public int getScore(){ NON HA SENSO, SCORE è FINAL NON PRIVATE, SI PUò TRANQUILLAMENTE ACCEDERE ALL'ATTRIBUTO SENZA NESSUNA PAURA
        return this.score;
    }*/
    public ScoringToken(int points){
        this.score = points;
    }
    /*public void givePoint(Player player){
        player.addPoints(); // va ragionato o tolto del tutto
    }*/

}
