package com.example.jumpking;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.example.jumpking.objects.Rect;
import com.example.jumpking.objects.XButton;
import com.example.jumpking.panels.*;

public class Game extends SurfaceView implements SurfaceHolder.Callback {

    public Rect playAreaRect;

    GameLoop gameLoop;
    // panels

    public MainLoop mainLoop;
    Menu menu;
    Pause pause;
    LevelCreator levelCreator;

    double prevTime;
    public double dt;

    public Game(Context context) {
        super(context);

        // get surface holder
        SurfaceHolder surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);

        gameLoop = new GameLoop(this, surfaceHolder);

        setFocusable(true);

    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        mainLoop = new MainLoop(this);
        menu = new Menu(this);
        pause = new Pause(this);

        System.out.println("Screen w: " + getWidth() + ", h: " + getHeight());

        mainLoop.active = true;
        prevTime = System.currentTimeMillis();
        gameLoop.startLoop();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                int acID = event.getActionIndex();
                if (mainLoop.active) {
                    if (mainLoop.lButton.press((int) event.getX(acID), (int) event.getY(acID))) {
                        mainLoop.lButton.pointerID = event.getPointerId(acID);
                    }
                    if (mainLoop.rButton.press((int) event.getX(acID), (int) event.getY(acID))) {
                        mainLoop.rButton.pointerID = event.getPointerId(acID);
                    }
                    if (mainLoop.uButton.press((int) event.getX(acID), (int) event.getY(acID))) {
                        mainLoop.uButton.pointerID = event.getPointerId(acID);
                    }
                }
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                if (mainLoop.active) {
                    if (mainLoop.lButton.pointerID == event.getPointerId(event.getActionIndex())) {
                        mainLoop.lButton.release();
                    }
                    if (mainLoop.rButton.pointerID == event.getPointerId(event.getActionIndex())) {
                        mainLoop.rButton.release();
                    }
                    if (mainLoop.uButton.pointerID == event.getPointerId(event.getActionIndex())) {
                        mainLoop.uButton.release();
                    }
                }
                return true;
        }

        return super.onTouchEvent(event);
    }

    public void update() {

        // get the delta time (dt)
        dt = (System.currentTimeMillis() - prevTime) / 1000;
        dt *= 30; // working in 30 FPS
        prevTime = System.currentTimeMillis();

        // update activated panels' game state
        if (mainLoop.active) {
            mainLoop.update();
        }

    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        // main loop
        if (mainLoop.active) {
            mainLoop.draw(canvas);
        }

        // FPS
        drawFps(canvas);

    }

    public double scaledX(double x) {
        return getWidth() * (x/1794); //1794 is w of the screen I worked with
    }

    public double scaledY(double y) {
        return getHeight() * (y/1017); //1017 is h of the screen I worked with
    }

    public void drawFps(Canvas canvas) {
        Paint paint = new Paint();

        paint.setColor(ContextCompat.getColor(getContext(), R.color.purple_200));
        paint.setTextSize(40);

        canvas.drawText("UPS: " + (int)gameLoop.getAverageUPS(), 90, 100, paint);
        canvas.drawText("FPS: " + (int)gameLoop.getAverageFPS(), 90, 160, paint);
    }


    public void drawText(String text, int x, int y) {
        Canvas canvas = getHolder().lockCanvas();
        Paint paint = new Paint();

        paint.setColor(ContextCompat.getColor(getContext(), R.color.purple_200));
        paint.setTextSize(40);
        canvas.drawText(text, x, y, paint);

        getHolder().unlockCanvasAndPost(canvas);
    }
}
