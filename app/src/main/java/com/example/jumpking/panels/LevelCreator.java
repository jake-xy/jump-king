package com.example.jumpking.panels;

import android.graphics.Canvas;

import com.example.jumpking.Game;
import com.example.jumpking.objects.Level;

public class LevelCreator {

    public boolean active = false;
    Game game;
    Level level;

    public LevelCreator(Game game, int level) {
        this.game = game;
        this.level = new Level(game, level);
    }

    public void update() {

    }

    public void draw(Canvas canvas) {

    }
}
