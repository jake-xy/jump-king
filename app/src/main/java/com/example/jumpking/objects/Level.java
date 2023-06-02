package com.example.jumpking.objects;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;

import com.example.jumpking.Game;
import com.example.jumpking.R;

public class Level {

    Game game;
    public Bitmap bgBitmap, mgBitmap, fgBitmap;
    public static Bitmap levelsBitmap;
    public Bitmap levelBitmap;
    public int level;
    private int floorLevel;
    public int tileW, tileH;
    public double left, top;
    public Rect[] tiles = new Rect[0];


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
        if (bgBitmap != null)
            bgBitmap = Bitmap.createScaledBitmap(bgBitmap, (int) width, (int) height, true);
        if (mgBitmap != null)
            mgBitmap = Bitmap.createScaledBitmap(mgBitmap, (int) width, (int) height, true);
        if (fgBitmap != null)
            fgBitmap = Bitmap.createScaledBitmap(fgBitmap, (int) width, (int) height, true);


        this.left = game.getWidth()/2 - mgBitmap.getWidth()/2;
        this.top = 0;

        // save the size inside game class
        game.playAreaRect = new Rect(left, top, mgBitmap.getWidth(), mgBitmap.getHeight());

        tileW = mgBitmap.getWidth()/60; // 60 x 45 is how many tiles in a play area
        tileH = mgBitmap.getHeight()/45;

        generateCollideTiles();
    }



    public void generateCollideTiles() {
        tiles = new Rect[0];
        int levelW = 60; // pixel size of levels.png (where the rects are located)
        int levelH = 45;
        int x = (int)(floorLevel/13);
        int y = (floorLevel - 13*(int)(floorLevel/13) - 1);

        levelBitmap = Bitmap.createBitmap(levelsBitmap, x * levelW, y * levelH, levelW, levelH);

        for (int px = 0; px < levelBitmap.getWidth(); px++) {
            for (int py = 0; py < levelBitmap.getHeight(); py++) {
                int colour = levelBitmap.getPixel(px, py);
                int red = Color.red(colour);
                int green = Color.green(colour);
                int blue = Color.blue(colour);
                int alpha = Color.alpha(colour);

                if (red == 0 && green == 0 && blue == 0) {
                    tiles = append(new Rect(left + px*tileW,top + py*tileH, tileW, tileH), tiles);
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
        if (bgBitmap != null) {
            canvas.drawBitmap(bgBitmap, (float) left, (float) top, null);
        }
    }

    public void drawForeground(Canvas canvas) {
        if (fgBitmap != null) {
            canvas.drawBitmap(fgBitmap, (float) left, (float) top, null);
        }
    }

    private Rect[] append(Rect item, Rect[] array) {
        Rect[] out = new Rect[array.length + 1];

        for (int i = 0; i < array.length; i++) {
            out[i] = array[i];
        }
        out[array.length] = item;

        return out;
    }
}
