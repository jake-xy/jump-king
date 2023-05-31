package com.example.jumpking;

import android.content.Context;
import android.graphics.Canvas;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import com.example.jumpking.objects.Level;
import com.example.jumpking.panels.*;

public class Game extends SurfaceView implements SurfaceHolder.Callback {

    GameLoop gameLoop;
    // panels

    MainLoop mainLoop;
    Menu menu;
    Pause pause;
    LevelCreator levelCreator;

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
        levelCreator = new LevelCreator(this);
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {

    }

    public void update() {

    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);


    }
}
