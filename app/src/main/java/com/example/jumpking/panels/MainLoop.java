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
import com.example.jumpking.objects.XButton;

public class MainLoop {
    Game game;
    public boolean active = false;
    Jumper king;
    int level = 0;
    public Level currentLevel;
    Level[] levels = new Level[164];

    private boolean showTiles = false;

//    public XButton[] dPads = new XButton[3];
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

        // initialize the buttons
        double bHeight = game.scaledY(160);
        lButton = new XButton(game, R.drawable.left, (int) game.scaledY(190), (int) (game.getHeight()-bHeight*2), (int) bHeight);
        rButton = new XButton(game, R.drawable.right, (int) (lButton.getX() + lButton.getWidth()*2), (int) lButton.getY(), (int) bHeight);
        uButton = new XButton(game, R.drawable.up, (int) (game.getWidth() - rButton.getWidth()*2), (int) rButton.getY(), (int) bHeight);
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

        if (king.rect.bot <= 0) {
            king.rect.setY(game.playAreaRect.bot - king.rect.h);
            setLevel(level + 1);
        }

        else if (king.rect.top > game.playAreaRect.bot) {
            king.rect.setY(0);
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
            for (Rect tile : currentLevel.tiles) {
                canvas.drawRect((float) tile.left, (float) tile.top, (float) tile.right, (float) tile.bot, paint);
            }
        }
    }
}
