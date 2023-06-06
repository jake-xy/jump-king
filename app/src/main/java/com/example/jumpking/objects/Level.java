package com.example.jumpking.objects;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.example.jumpking.Game;
import com.example.jumpking.R;

public class Level {

    Game game;
    public Bitmap bgBitmap, mgBitmap, fgBitmap;
    public static Bitmap levelsBitmap;
    public Bitmap levelBitmap;
    public int level;
    private int floorLevel;
    public static int tileW, tileH;
    public double left, top;
    public Tile[] tiles = new Tile[0];
    public Tile[] rectTiles = new Tile[0];
    public Tile[] slopeTiles = new Tile[0];


    public Level(Game game, int level) {
        this.game = game;
        this.level = level;

        if (levelsBitmap == null) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;

            levelsBitmap = BitmapFactory.decodeResource(game.getResources(), R.drawable.levels, options);
        }

        generateLevel();
    }

    private void generateLevel() {
        floorLevel = level+1;

        System.out.println("floor: " + floorLevel);

        // Generating the bitmaps for the current level
        int id = game.getResources().getIdentifier("mg" + (floorLevel), "drawable", game.getContext().getPackageName());
        mgBitmap = BitmapFactory.decodeResource(game.getResources(), id);
        id = game.getResources().getIdentifier("fg" + (floorLevel), "drawable", game.getContext().getPackageName());
        fgBitmap = BitmapFactory.decodeResource(game.getResources(), id);
        id = game.getResources().getIdentifier("bg" + (floorLevel), "drawable", game.getContext().getPackageName());
        bgBitmap = BitmapFactory.decodeResource(game.getResources(), id);

        double height = game.getHeight();
        double width = mgBitmap.getWidth()*height / mgBitmap.getHeight();

        // resize
        if (bgBitmap != null) {
            bgBitmap = Bitmap.createScaledBitmap(bgBitmap, (int) width, (int) height, true);
        }
        if (mgBitmap != null) {
            mgBitmap = Bitmap.createScaledBitmap(mgBitmap, (int) width, (int) height, true);
        }
        if (fgBitmap != null) {
            fgBitmap = Bitmap.createScaledBitmap(fgBitmap, (int) width, (int) height, true);
        }


        this.left = game.getWidth()/2 - mgBitmap.getWidth()/2;
        this.top = 0;

        // save the size inside game class
        game.playAreaRect = new Rect(left, top, mgBitmap.getWidth(), mgBitmap.getHeight());

        tileW = mgBitmap.getWidth()/60; // 60 x 45 is how many tiles in a play area
        tileH = mgBitmap.getHeight()/45;

        generateCollideTiles();
        // debug
        System.out.println("tiles len: " + tiles.length);
    }



    public void generateCollideTiles() {
        tiles = new Tile[0];
        int levelW = 60; // pixel size of levels.png (where the rects are located)
        int levelH = 45;
        int x = (int)(level/13);
        int y = (level - 13*(int)(level/13));

        // debug
        System.out.println("level: " + level);
        System.out.println("x: " + x + " y: " + y);

        levelBitmap = Bitmap.createBitmap(levelsBitmap, x * levelW, y * levelH, levelW, levelH);

        for (int px = 0; px < levelBitmap.getWidth(); px++) {
            for (int py = 0; py < levelBitmap.getHeight(); py++) {
                int colour = levelBitmap.getPixel(px, py);
                int red = Color.red(colour);
                int green = Color.green(colour);
                int blue = Color.blue(colour);

                // black (rects)
                if (red == 0 && green == 0 && blue == 0) {
                    tiles = append(new Tile(left + px*tileW,top + py*tileH, tileW, tileH, Tile.RECT), tiles);
                    tiles[tiles.length - 1].rectType = Tile.RECT_SOLID;
                    rectTiles = append(new Tile(left + px*tileW,top + py*tileH, tileW, tileH, Tile.RECT), rectTiles);
                    rectTiles[rectTiles.length - 1].rectType = Tile.RECT_SOLID;
                }
                // red (slopes)
                else if (red == 255 && green == 0 && blue == 0) {
                    tiles = append(new Tile(left + px*tileW,top + py*tileH, tileW, tileH, Tile.SLOPE), tiles);
                    slopeTiles = append(new Tile(left + px*tileW,top + py*tileH, tileW, tileH, Tile.SLOPE), slopeTiles);
                }
                // grey (ghost rect)
                else if (red == 128 && green == 128 && blue == 128) {
                    tiles = append(new Tile(left + px*tileW,top + py*tileH, tileW, tileH, Tile.RECT), tiles);
                    tiles[tiles.length - 1].rectType = Tile.RECT_GHOST;
                    rectTiles = append(new Tile(left + px*tileW,top + py*tileH, tileW, tileH, Tile.RECT), rectTiles);
                    rectTiles[rectTiles.length - 1].rectType = Tile.RECT_GHOST;
                }

            }
        }
    }



    public void setLevel(int level) {
        this.level = level;
        generateLevel();
    }

    public void drawMidground(Canvas canvas) {
        if (mgBitmap != null) {
            canvas.drawBitmap(mgBitmap, (float) left, (float) top, null);
        }
    }

    public void drawBackground(Canvas canvas) {
//        // debug
//        System.out.println("drawBackGround() level: " + level);
        if (bgBitmap != null) {
            canvas.drawBitmap(bgBitmap, (float) left, (float) top, null);
        }
    }

    public void drawForeground(Canvas canvas) {
        if (fgBitmap != null) {
            canvas.drawBitmap(fgBitmap, (float) left, (float) top, null);
        }
    }

    public Rect[] append(Rect item, Rect[] array) {
        Rect[] out = new Rect[array.length + 1];

        for (int i = 0; i < array.length; i++) {
            out[i] = array[i];
        }
        out[array.length] = item;

        return out;
    }

    public static Tile[] append(Tile item, Tile[] array) {
        Tile[] out = new Tile[array.length + 1];

        for (int i = 0; i < array.length; i++) {
            out[i] = array[i];
        }
        out[array.length] = item;

        return out;
    }
}
