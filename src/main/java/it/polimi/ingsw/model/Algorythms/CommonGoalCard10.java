package it.polimi.ingsw.model.Algorythms;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.Shelf;
import it.polimi.ingsw.model.Tile;

public class CommonGoalCard10 implements CardStrategy {

    private boolean[] colorsOccurence; //array in cui memorizzo se l'i-esimo colore è presente oppure no
    private boolean colorBonus; //lo devo usare per utilizzare lo stesso algoritmo 9
    private int rowCounter; // il numero di righe che superano il check (dovranno essere 2 per passare il check)

    public boolean algorythm(Shelf myShelf) {

        colorsOccurence = new boolean[6];


        for(int r=0; r<6; r++){
            colorsOccurence= new boolean[6];    // setto l'array delle occorrenze dei colori a default
            colorBonus= true;                    // setto a true per ogni ciclo
            for(int c=0; c<5 && myShelf.getTile(r,c)!=null ; c++){
                colorCheck(myShelf.getTile(r,c));
                if(c==4){                                                            //se ci sono 5 tessere nella riga e
                    for(int i=0; i<6 && colorsOccurence[i]==true || colorBonus; i++) { //se sono presenti tutti i colori-1 nell'array,
                        if (colorsOccurence[i]==false) colorBonus=false;
                        if (i == 5) rowCounter++; //aumento il contatore delle righe che soddisfano l'obbiettivo
                    }
                }
            }
            if(rowCounter==2) return true;  // se ci sono 2 righe che soddisfano i requisiti ritorno true
        }
        return false;
    }


    public String toString() {
        return "Due righe formate ciascuna da 5 diversi tipi di tessere.";
    }

    private void colorCheck(Tile tile){  //aggiorno l'array che controlla la presenza dei colori
        if(tile.getColor()== Color.GREEN) colorsOccurence[0]=true;
        if(tile.getColor()==Color.WHITE) colorsOccurence[1]=true;
        if(tile.getColor()==Color.YELLOW) colorsOccurence[2]=true;
        if(tile.getColor()==Color.BLUE) colorsOccurence[3]=true;
        if(tile.getColor()==Color.CYAN) colorsOccurence[4]=true;
        if(tile.getColor()==Color.PINK) colorsOccurence[5]=true;

    }
}