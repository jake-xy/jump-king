package com.example.jumpking;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

public class GameLoop extends Thread{
    public static final double MAX_UPS = 60.0; // target FPS
    private static final double UPS_PERIOD = 1E+3/MAX_UPS;
    private boolean running = false;
    private SurfaceHolder surfaceHolder;
    private Game game;
    private double averageUPS, averageFPS;

    public long startTime, elapsedTime, sleepTime;

    public GameLoop(Game game, SurfaceHolder surfaceHolder) {
        this.surfaceHolder = surfaceHolder;
        this.game = game;
    }

    public double getAverageUPS() {
        return averageUPS;
    }

    public double getAverageFPS() {
        return averageFPS;
    }

    @Override
    public void run() {
        Log.d("GameLoop.java", "run()");
        super.run();
        // Declare time and cycle variables
        int updateCount = 0, frameCount = 0;

        // game loop
        startTime =  System.currentTimeMillis();
        Canvas canvas = null;
        while (running) {

            // try to update and render game
            try {
                synchronized (surfaceHolder) {
                    canvas = surfaceHolder.lockCanvas();
                    game.update();
                    updateCount += 1;
                    game.draw(canvas);
                }
            }
            catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            finally {
                if (canvas != null) {
                    try {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                        frameCount += 1;
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }


            // pause game loop to not exceed target UPS
            // I have no idea how tf sleep time calculation works man -jake
            elapsedTime = System.currentTimeMillis() - startTime;
            sleepTime = (long)(updateCount*UPS_PERIOD - elapsedTime);
            if (sleepTime > 0) {
                try {
                    sleep(sleepTime);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // skip frames to keep up with target UPS
            while (sleepTime < 0 && updateCount < MAX_UPS-1) {
                game.update();
                updateCount += 1;
                elapsedTime = System.currentTimeMillis() - startTime;
                sleepTime = (long)(updateCount*UPS_PERIOD - elapsedTime);
            }

            // calculate average UPS and FPS
            elapsedTime = System.currentTimeMillis() - startTime;
            if (elapsedTime >= 1000) {
                averageUPS = updateCount / (elapsedTime * 1E-3); //1E-3 is 10^-3 (it just means divide by 1000)
                averageFPS = frameCount / (elapsedTime * 1E-3);
                updateCount = 0;
                frameCount = 0;
                startTime = System.currentTimeMillis();
            }
        }
    }

    public void startLoop() {
        Log.d("GameLoop.java", "startLoop()");
        running = true;
        start();
    }

    public void stopLoop() {
        Log.d("GameLoop.java", "stopLoop()");
        running = false;
        // wait for thread to join
        try {
            join();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
