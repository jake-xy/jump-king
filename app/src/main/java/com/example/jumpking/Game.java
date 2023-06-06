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

    public int GAME_SPEED = 30; // it's like working on 30 FPS
    public Rect playAreaRect;

    GameLoop gameLoop;
    // panels

    public MainLoop mainLoop;
    public Menu menuPanel;
    public Pause pausePanel;

    public double prevTime;
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
        menuPanel = new Menu(this);
        pausePanel = new Pause(this);

//        System.out.println("Screen w: " + getWidth() + ", h: " + getHeight());

        mainLoop.active = true;
        mainLoop.visible = true;
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

        // these methods return true if the event is handled
        if (mainLoop.onTouch(event)) return true;

        if (pausePanel.onTouch(event)) return true;

        return super.onTouchEvent(event);
    }

    public void update() {

        // get the delta time (dt)
        dt = (System.currentTimeMillis() - prevTime) / 1000;
        dt *= GAME_SPEED; // working in 30 FPS
        prevTime = System.currentTimeMillis();

        // update panels' game state
        mainLoop.update();
        pausePanel.update();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        // main loop
        mainLoop.draw(canvas);
        pausePanel.draw(canvas);

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
