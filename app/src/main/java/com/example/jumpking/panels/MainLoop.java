package com.example.jumpking.panels;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.example.jumpking.Game;
import com.example.jumpking.R;
import com.example.jumpking.objects.Jumper;
import com.example.jumpking.objects.Level;
import com.example.jumpking.objects.Rect;
import com.example.jumpking.objects.Tile;
import com.example.jumpking.objects.XButton;

public class MainLoop {
    Game game;
    public boolean active = false;
    Jumper king;
    public int level = 0; // floorLevel is == level + 1
    public Level currentLevel;
    Level[] levels = new Level[164];

    private boolean showTiles = false;
    public XButton lButton, rButton, uButton;

    public MainLoop(Game game) {
        this.game = game;
        // initialize the levels
        for (int i = 0; i < 12; i++) {
            levels[i] = new Level(game, i);
        }
        currentLevel = levels[level];


        // initialize the king
        king = new Jumper(game);

        // debug
//        showTiles = true;
//        levels[12] = new Level(game, 165);
//        setLevel(12);
////        king.rect.setY(game.playAreaRect.h * 0.467);
////        king.rect.setX(game.playAreaRect.left + game.playAreaRect.w * 0.03125);
//
//        setLevel(6);
//        king.rect.setX(game.playAreaRect.left +  16*Level.tileW);
//        king.rect.setY(6*Level.tileH - king.rect.h);
//
//        setLevel(10);
//        king.rect.setX(game.playAreaRect.left +  13*Level.tileW);
//        king.rect.setY(26*Level.tileH - king.rect.h);
        // debug end

        // initialize the buttons
        double bHeight = game.scaledY(160);
        lButton = new XButton(game, R.drawable.left, (int) game.scaledX(50), (int) (game.getHeight()-bHeight*1.5), (int) bHeight);
        rButton = new XButton(game, R.drawable.right, (int) (lButton.getX() + lButton.getWidth()*1.5), (int) lButton.getY(), (int) bHeight);
        uButton = new XButton(game, R.drawable.up, (int) (game.getWidth() - rButton.getWidth()), (int) rButton.getY(), (int) bHeight);
    }


    public void update() {

        if (uButton.pressedDown) {
            king.startCharge();
        }
        else {
            king.stopCharge();
        }

        if (lButton.pressedDown) {
//            king.stopMoving();
            king.moveLeft();
        }
        else if (rButton.pressedDown) {
//            king.stopMoving();
            king.moveRight();
        }
        else {
            king.stopMoving();
        }

        king.update(currentLevel.tiles);

        if (king.rect.bot < game.playAreaRect.top) {
            king.rect.setY(game.playAreaRect.bot - king.rect.h);
            setLevel(level + 1);
        }

        else if (king.rect.bot > game.playAreaRect.bot) {
            king.rect.setY(game.playAreaRect.top - (game.playAreaRect.bot - king.rect.top) );
            setLevel(level - 1);
        }

    }


    public void setLevel(int level) {
        this.level = level;
        currentLevel = levels[level];
    }


    public void draw(Canvas canvas) {
//        Log.d("Mainloop.java", "draw()");

        // draw background
        currentLevel.drawBackground(canvas);

        // draw midground
        currentLevel.drawMidground(canvas);
            // player
        king.draw(canvas);

        // draw foreground
        currentLevel.drawForeground(canvas);

        // draw buttons
        lButton.draw(canvas);
        rButton.draw(canvas);
        uButton.draw(canvas);

        if (lButton.pressedDown) {
            Paint paint = new Paint();

            paint.setColor(ContextCompat.getColor(game.getContext(), R.color.purple_200));
            paint.setTextSize(40);
            canvas.drawText("Left", 20, 20, paint);
        }

        if (rButton.pressedDown) {
            Paint paint = new Paint();

            paint.setColor(ContextCompat.getColor(game.getContext(), R.color.purple_200));
            paint.setTextSize(40);
            canvas.drawText("Right", 20, 60, paint);
        }

        if (uButton.pressedDown) {
            Paint paint = new Paint();

            paint.setColor(ContextCompat.getColor(game.getContext(), R.color.purple_200));
            paint.setTextSize(40);

            canvas.drawText("Up", 20, 100, paint);
        }

        // tile hotboxes
        if (showTiles) {
            Paint paint = new Paint();
            paint.setColor(ContextCompat.getColor(game.getContext(), R.color.purple_200));
            paint.setStrokeWidth(2);
            paint.setStyle(Paint.Style.STROKE);
            // draw tiles (DEBUG)
            for (Tile tile : currentLevel.tiles) {
                if (tile.type == Tile.RECT) {
                    paint.setColor(ContextCompat.getColor(game.getContext(), R.color.red));
                }
                else {
                    paint.setColor(ContextCompat.getColor(game.getContext(), R.color.green));
                }
                canvas.drawRect((float) tile.left, (float) tile.top, (float) tile.right, (float) tile.bot, paint);
            }
        }
    }
}
