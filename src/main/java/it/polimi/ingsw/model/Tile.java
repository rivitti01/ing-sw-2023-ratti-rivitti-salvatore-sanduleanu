package it.polimi.ingsw.model;

public class Tile {
    final Color color;
    private int type;

    public Tile(Color color){
        this.color = color;
    }
    public Tile(Color color, int type){
        this.color = color;
        this.type = type;
    }

    public Color getColor(){
        return color;
    }
    public int getType(){
        return type;
    }

}
