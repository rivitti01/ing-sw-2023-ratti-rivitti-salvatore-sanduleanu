package it.polimi.ingsw.model.Algorythms;
import static it.polimi.ingsw.Costants.*;
import it.polimi.ingsw.model.Shelf;
import it.polimi.ingsw.model.Tile;

public class CommonGoalCard4 implements CardStrategy {

    public boolean algorythm(Shelf myShelf) {
        Tile[][] copiedShelf = myShelf.getShelf();
        int squareCounter = 0;
        int r = 0, c = 0;
        for (r = 0; r < SHELF_ROWS-1; r++) {
            for (c = 0; c < SHELF_COLUMN-1; c++) {
                if (copiedShelf[r][c].getColor() != null
                        && copiedShelf[r][c].getColor().equals(copiedShelf[r + 1][c].getColor())
                        && copiedShelf[r + 1][c].getColor().equals(copiedShelf[r][c + 1].getColor())
                        && copiedShelf[r][c + 1].getColor().equals(copiedShelf[r + 1][c + 1].getColor())) {
                    copiedShelf[r][c] = null;
                    copiedShelf[r + 1][c] = null;
                    copiedShelf[r][c + 1] = null;
                    copiedShelf[r + 1][c + 1] = null;
                    squareCounter++;
                    c++;
                }
                if (squareCounter >= 2) {
                    return true;
                }
            }
        }
        return false;
    }


    @Override
    public String toString() {
        return """
                Two groups each containing 4 tiles of
                the same type in a 2x2 square. The tiles
                of one square can be different from
                those of the other square.""";
    }
}


