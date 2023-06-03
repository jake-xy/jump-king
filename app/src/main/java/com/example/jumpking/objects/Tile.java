package com.example.jumpking.objects;

public class Tile extends Rect{

    public final static int RECT = 0, SLOPE = 1;
    /*
        DOWN_RIGHT means the slope is going down and the direction is right
        UP_RIGHT is a slope that is on the ceiling.. it means the slope is going up and the direction is right
     */
    public final static int DOWN_RIGHT = 2, DOWN_LEFT = 3, UP_RIGHT = 4, UP_LEFT = 5;

    public int type;
    public int slopeType;

    Tile (double x, double y, double w, double h, int type) {
        super(x, y, w, h);
        this.type = type;
    }

    public int getSlopeXdir(Tile[] tiles) {

        Tile testTIle = new Tile(this.x+Level.tileW/2, this.y, this.w, this.h, RECT);

        for (int i = 0; i < tiles.length; i++) {
            // check the other tiles for collision
            if (tiles[i].type != SLOPE) {
                if (testTIle.collides(tiles[i])) {
                    return -1;
                }
            }
        }

        return 1;
    }
}
