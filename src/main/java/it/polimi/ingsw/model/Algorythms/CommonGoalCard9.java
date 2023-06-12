package it.polimi.ingsw.model.Algorythms;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.Shelf;
import it.polimi.ingsw.model.Tile;

import java.util.Arrays;

import static it.polimi.ingsw.util.Costants.SHELF_COLUMN;
import static it.polimi.ingsw.util.Costants.SHELF_ROWS;

public class CommonGoalCard9 implements CardStrategy {

    private boolean[] colorsOccurrence; //array in cui memorizzo se l'i-esimo colore è presente oppure no.


    public boolean algorythm(Shelf myShelf) {
        int tmp=0;                 // in tmp salvo l'indice i (mi serve al di fuori del ciclo in cui è usato come contatore).
        if(myShelf==null) return false;
        int columnCounter=0;           // il numero di colonne che superano il check (dovranno essere 2).
        colorsOccurrence = new boolean[6];

        for (int c = 0; c < SHELF_COLUMN; c++) {        //  scorro tutta la shelf.
            for (int r = 0; r < SHELF_ROWS; r++) {
                if(r<=SHELF_ROWS-6) {                  //faccio il controllo solo se ci sono almeno 6 tessere verso il basso
                    Arrays.fill(colorsOccurrence, false);  // resetto l'array ad ogni nuova tessera da cui parte l'analisi.
                    for (int i = r; i < r + 6 && myShelf.getTile(i, c) != null; i++) {   // controllo in verticale di 6 caselle.
                        colorCheck(myShelf.getTile(i, c));
                        if (i == r + 5) {                                            // se ci sono 6 tessere di fila nella colonna
                            for (int k = 0; k < 6 && colorsOccurrence[k]; k++) { // e
                                if (k == 5) {                                      // se sono presenti tutti i colori
                                    columnCounter++;                           // ho 1 colonna che soddisfa l'obiettivo.
                                    tmp = i;                                     // salvo i per saltare le caselle già analizzate.
                                }
                            }
                        }
                           if (tmp!=i && myShelf.getTile(tmp + 1, c) == null)
                              tmp = i+1;  // salto caselle anche se incontro una casella vuota.
                    }
                    if (columnCounter == 2)
                        return true;   //se ci sono 2 colonne che soddisfano l'obiettivo sono contento.
                    r=tmp;
                    tmp=0;                              //svuoto temp una volta usato
                }
            }
        }
    return false;
    }



    public String toString() {
        return "Due colonne formate ciascuna da 6 diversi tipi di tessere.";
    }



    private void colorCheck(Tile tile){  //aggiorno l'array che controlla la presenza dei colori.
        if(tile.getColor()== Color.GREEN) colorsOccurrence[0]=true;
        if(tile.getColor()==Color.WHITE) colorsOccurrence[1]=true;
        if(tile.getColor()==Color.YELLOW) colorsOccurrence[2]=true;
        if(tile.getColor()==Color.BLUE) colorsOccurrence[3]=true;
        if(tile.getColor()==Color.CYAN) colorsOccurrence[4]=true;
        if(tile.getColor()==Color.PINK) colorsOccurrence[5]=true;
    }

    public String getCardName(){return "9common";}
}