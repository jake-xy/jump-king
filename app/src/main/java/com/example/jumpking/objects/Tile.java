package com.example.jumpking.objects;

public class Tile extends Rect{

    public final static int
        RECT = 0,
        SLOPE = 1,
        RECT_SOLID = 2,
        RECT_GHOST = 3
    ;


    /*
        DOWN_RIGHT means the slope is going down and the direction is right
        UP_RIGHT is a slope that is on the ceiling.. it means the slope is going up and the direction is right
     */
    public final static int DOWN_RIGHT = 2, DOWN_LEFT = 3, UP_RIGHT = 4, UP_LEFT = 5;

    public int type;
    public int rectType;

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

    public int[] getAirDir(Tile[] rectTiles) {
        int[] out = {1, 1};
        /*
        Returns the direction (either positive or negative) of the side of the tile that is air.
        |\
        | \
        ___\
        The slope tile above has an air direction of {x: 1, y: -1}
         */

        Tile yTestTile = new Tile(this.x, this.y + 1, this.w, this.h, RECT);
        Tile xTestTile = new Tile(this.x + 1, this.y, this.w, this.h, RECT);

        for (Tile tile : rectTiles) {
            // checking y dir
            if (yTestTile.collides(tile)) {
                out[1] = -1;
            }

            // checking x dir
            if (xTestTile.collides(tile)) {
                out[0] = -1;
            }
        }

        return out;
    }
}
