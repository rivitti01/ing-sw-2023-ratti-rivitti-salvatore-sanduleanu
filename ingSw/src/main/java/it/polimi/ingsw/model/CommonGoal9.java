package it.polimi.ingsw.model;

public class CommonGoal9 implements CardStrategy {

    private boolean[] colorsOccurence; //array in cui memorizzo se l'i-esimo colore è presente oppure no
    private int columnCounter; // il numero di colonne che superano il check (dovranno essere 2)

    public boolean algorythm(Shelf myShelf) {

        colorsOccurence = new boolean[6];

        for(int c=0; c<5; c++){
            for(int r=0; r<6 && myShelf.getTile(r,c)!=null ; r++){
                colorCheck(myShelf.getTile(r,c));
                if(r==5){                                              //se ci sono 6 tessere nella colonna e
                    for(int i=0; i<6 && colorsOccurence[i]==true; i++) //se sono presenti tutti i colori,
                        if (i == 5 ) columnCounter++;  //aumento il contatore delle colonne che soddisfano l'obbiettivo
                }
            }
            if(columnCounter==2) return true;  // se ci sono 2 colonne che soddisfano i requisiti ritorno true
            colorsOccurence= new boolean[6];    // resetto l'array delle occorrenze dei colori
        }
        return false;
    }


    public String toString() {
        return "Due colonne formate ciascuna da 6 diversi tipi di tessere.";
    }

    private void colorCheck(Tile tile){  //aggiorno l'array che controlla la presenza dei colori
        if(tile.getColor()==Color.GREEN) colorsOccurence[0]=true;
        if(tile.getColor()==Color.WHITE) colorsOccurence[1]=true;
        if(tile.getColor()==Color.YELLOW) colorsOccurence[2]=true;
        if(tile.getColor()==Color.BLUE) colorsOccurence[3]=true;
        if(tile.getColor()==Color.CYAN) colorsOccurence[4]=true;
        if(tile.getColor()==Color.PINK) colorsOccurence[5]=true;

    }
}