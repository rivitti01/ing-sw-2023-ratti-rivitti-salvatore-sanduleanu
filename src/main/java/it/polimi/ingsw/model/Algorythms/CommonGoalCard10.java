package it.polimi.ingsw.model.Algorythms;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.Shelf;
import it.polimi.ingsw.model.Tile;

import static it.polimi.ingsw.Costants.SHELF_COLUMN;
import static it.polimi.ingsw.Costants.SHELF_ROWS;

public class CommonGoalCard10 implements CardStrategy {

    private boolean[] colorsOccurence; //array in cui memorizzo se l'i-esimo colore è presente oppure no

    private int rowCounter; // il numero di righe che superano il check (dovranno essere 2 per passare il check)

    public boolean algorythm(Shelf myShelf) {
        int tmp=0;        // in tmp salvo l'indice i (mi serve al di fuori del ciclo in cui è usato come contatore).
        boolean colorBonus;   // mi serve per riutilizzare l'algoritmo della carta 9

        for (int r = 0; r < SHELF_ROWS; r++) {        //  scorro tutta la shelf.
            for (int c = 0; c < SHELF_COLUMN; c++) {
                colorsOccurence = new boolean[6];  // resetto l'array ad ogni nuova tessera da cui parte l'analisi.
                colorBonus= true;                 // mi serve a true per ogni ciclo.
                for (int i = c; i < c + 5 && myShelf.getTile(r, i) != null; i++) {   // controllo in orizzontale di 5 caselle.
                    colorCheck(myShelf.getTile(r, i));
                    if (i == c + 4) {                                            // se ci sono 5 tessere di fila nella riga
                        for (int k = 0; k < 6 && colorsOccurence[k] || colorBonus; k++) { // e
                            if (!colorsOccurence[i]) colorBonus=false;
                            if (k == 5) {                                      // se sono presenti tutti i colori - 1
                                rowCounter++;                           // ho una riga che soddisfa l'obiettivo.
                                tmp = i;                                     // salvo i per saltare le caselle già analizzate.
                            }
                        }
                    }
                    if (myShelf.getTile(r, tmp+1) == null && tmp!=i)
                        tmp = i+1;  // salto caselle anche se incontro una casella vuota.
                }
                if(rowCounter==2) return true;   //se ci sono 2 colonne che soddisfano l'obiettivo sono contento.
                c=tmp;
                tmp=0;                           //svuoto temp una volta usato
            }
        }
        return false;
    }


    public String toString() {
        return "Due righe formate ciascuna da 5 diversi tipi di tessere.";
    }


    private void colorCheck(Tile tile){  //aggiorno l'array che controlla la presenza dei colori.
        if(tile.getColor()== Color.GREEN) colorsOccurence[0]=true;
        if(tile.getColor()==Color.WHITE) colorsOccurence[1]=true;
        if(tile.getColor()==Color.YELLOW) colorsOccurence[2]=true;
        if(tile.getColor()==Color.BLUE) colorsOccurence[3]=true;
        if(tile.getColor()==Color.CYAN) colorsOccurence[4]=true;
        if(tile.getColor()==Color.PINK) colorsOccurence[5]=true;

    }
}