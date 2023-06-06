package com.example.jumpking.panels;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;

import androidx.core.content.ContextCompat;

import com.example.jumpking.Game;
import com.example.jumpking.R;

public class Pause {

    public boolean active = false;
    public boolean visible = false;
    Game game;

    public Pause(Game game) {
        this.game = game;
    }


    public void update() {
        if (!active) return;
    }

    public void draw(Canvas canvas) {
        if (!visible) return;

        // draw black
        Paint paint = new Paint();
        paint.setColor(ContextCompat.getColor(game.getContext(), R.color.black));
        paint.setAlpha(100);
        canvas.drawRect(0, 0, game.getWidth(), game.getHeight(), paint);
    }

    public boolean onTouch(MotionEvent event) {
        if (!active) return false;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                game.mainLoop.rButton.release();
                game.mainLoop.lButton.release();
                game.mainLoop.resume();
                return true;
        }

        return false;
    }


}
