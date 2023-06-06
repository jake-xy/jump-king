package com.example.jumpking.panels;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;

import androidx.core.content.ContextCompat;

import com.example.jumpking.Game;
import com.example.jumpking.R;
import com.example.jumpking.objects.XButton;

public class Pause {

    public boolean active = false;
    public boolean visible = false;
    Game game;
    XButton button;

    public Pause(Game game) {
        this.game = game;
    }


    public void update() {
        if (!active) return;

        if (button.pressedDown && !game.mainLoop.active) {
            game.mainLoop.resume();
            button.release();
        }

    }

    public void draw(Canvas canvas) {
        if (!visible) return;

        // draw black
        Paint paint = new Paint();
        paint.setColor(ContextCompat.getColor(game.getContext(), R.color.black));
        paint.setAlpha(100);
        canvas.drawRect(0, 0, game.getWidth(), game.getHeight(), paint);

        button.draw(canvas);
    }

    public boolean onTouch(MotionEvent event) {
        if (!active) return false;

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                int acID = event.getActionIndex();
                if (button.press((int) event.getX(acID), (int) event.getY(acID))) {
                    button.pointerID = event.getPointerId(acID);
                }
                return true;
        }

        return false;
    }


    public void activate(XButton pauseButton) {
        this.button = pauseButton;
        this.active = true;
        this.visible = true;
    }
}
