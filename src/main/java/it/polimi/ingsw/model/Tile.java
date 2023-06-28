
package it.polimi.ingsw.model;

import java.io.Serializable;

public class Tile implements Serializable {
    private final Color color;
    private int type = 0;

    /**
     * Constructs a tile with the specified color.
     *
     * @param color the color of the tile
     */
    public Tile(Color color){
        this.color = color;
    }

    /**
     * Constructs a tile with the specified color and type.
     *
     * @param color the color of the tile
     * @param type the type of the tile
     */
    public Tile(Color color, int type){
        this.color = color;
        this.type = type;
    }


    /**
     * Returns the color of the tile.
     *
     * @return the color of the tile
     */
    public Color getColor(){
        return color;
    }

    /**
     * Returns the type of the tile.
     *
     * @return the type of the tile
     */
    public int getType(){
        return type;
    }

}
