package com.example.jumpking.objects;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.jumpking.Game;

public class Level {

    Line[] lines;
    Game game;
    Bitmap bgBitmap;
    public int level;

    public Level(Game game, int bgResourceID, int level) {
        this.game = game;
        this.level = level;

        bgBitmap = BitmapFactory.decodeResource(game.getResources(), bgResourceID);
    }

}
