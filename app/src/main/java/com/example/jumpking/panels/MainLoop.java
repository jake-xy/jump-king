package com.example.jumpking.panels;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;

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
    public boolean active = false, visible = false;
    Jumper king;
    public int level = 0; // floorLevel is == level + 1
    public Level currentLevel;
    Level[] levels = new Level[164];

    private boolean showTiles = false;
    public XButton lButton, rButton, uButton;

    public MainLoop(Game game) {
        this.game = game;
        // initialize the levels
        for (int i = 0; i < 21; i++) {
            levels[i] = new Level(game, i);
        }

        currentLevel = levels[0];

        // initialize the king
        king = new Jumper(game);

        // debug
        System.out.println("king: " + king.rect.toString());

        // debug
        System.out.println("current level: " + currentLevel.level);
        System.out.println("current level tiles len: " + currentLevel.tiles.length);

        // debug
//        king.showHitbox = true;
//        showTiles = true;
//        levels[12] = new Level(game, 165);
//        setLevel(12);
//        king.rect.setY(game.playAreaRect.h * 0.367);
//        king.rect.setX(game.playAreaRect.left + game.playAreaRect.w * 0.03125);
//
        // slope thingy
//        showTiles = true;
//        king.showHitbox = true;
//        setLevel(6);
//        king.rect.setX(game.playAreaRect.left +  1*Level.tileW);
//        king.rect.setY(16*Level.tileH - king.rect.h);
//
//        setLevel(10);
//        king.rect.setX(game.playAreaRect.right - Level.tileW*6);
//        king.rect.setY(4*Level.tileH - king.rect.h);
        // debug end

        // initialize the buttons
        double bHeight = game.scaledY(160);
        lButton = new XButton(game, R.drawable.left, (int) game.scaledX(50), (int) (game.getHeight()-bHeight*1.5), (int) bHeight);
        rButton = new XButton(game, R.drawable.right, (int) (lButton.getX() + lButton.getWidth()*1.5), (int) lButton.getY(), (int) bHeight);
        uButton = new XButton(game, R.drawable.up, (int) (game.getWidth() - rButton.getWidth()), (int) rButton.getY(), (int) bHeight);
    }


    public void update() {
        if (!active) return;

        if (uButton.pressedDown) {
            king.startCharge();
        }
        else {
            king.stopCharge();
        }

        if (lButton.pressedDown) {
            king.moveLeft();
        }
        else if (rButton.pressedDown) {
            king.moveRight();
        }
        else {
            king.stopMoving();
        }

        // debug
        if (lButton.pressedDown && rButton.pressedDown) {
            pause();
        }

        king.update(currentLevel);

        if (king.rect.bot < game.playAreaRect.top) {
            king.rect.setY(game.playAreaRect.bot + king.rect.y);
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
        if (!visible) return;
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

    public boolean onTouch(MotionEvent event) {
        if (!active) return false;

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                int acID = event.getActionIndex();
                if (lButton.press((int) event.getX(acID), (int) event.getY(acID))) {
                    lButton.pointerID = event.getPointerId(acID);
                }
                if (rButton.press((int) event.getX(acID), (int) event.getY(acID))) {
                    rButton.pointerID = event.getPointerId(acID);
                }
                if (uButton.press((int) event.getX(acID), (int) event.getY(acID))) {
                    uButton.pointerID = event.getPointerId(acID);
                }
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                if (lButton.pointerID == event.getPointerId(event.getActionIndex())) {
                    lButton.release();
                }
                if (rButton.pointerID == event.getPointerId(event.getActionIndex())) {
                    rButton.release();
                }
                if (uButton.pointerID == event.getPointerId(event.getActionIndex())) {
                    uButton.release();
                }
                return true;
        }

        return false;
    }

    public void pause() {
        this.active = false;
        game.pausePanel.active = true;
        game.pausePanel.visible = true;
    }

    public void resume() {
        this.active = true;
        game.pausePanel.active = false;
        game.pausePanel.visible = false;
    }
}
