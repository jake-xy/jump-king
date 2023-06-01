package com.example.jumpking.objects;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import com.example.jumpking.Game;
import com.example.jumpking.R;

public class Level {

    Line[] lines;
    Game game;
    public Bitmap bgBitmap, mgBitmap, fgBitmap;
    public int level;
    public double left, top;

    public Level(Game game, int level) {
        this.game = game;
        this.level = level;

        generateLevel();
    }

    private void generateLevel() {
        // Generating the bitmaps for the current level
        int id = game.getResources().getIdentifier("mg" + (level+1), "drawable", game.getContext().getPackageName());
        mgBitmap = BitmapFactory.decodeResource(game.getResources(), id);
        id = game.getResources().getIdentifier("fg" + (level+1), "drawable", game.getContext().getPackageName());
        fgBitmap = BitmapFactory.decodeResource(game.getResources(), id);
        id = game.getResources().getIdentifier("bg" + (level+1), "drawable", game.getContext().getPackageName());
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

        //
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
}
