package com.example.jumpking.objects;

import android.graphics.Canvas;
import android.graphics.Paint;

import androidx.core.content.ContextCompat;

import com.example.jumpking.Game;
import com.example.jumpking.R;



public class Jumper {

    public Rect rect;
    Game game;
    Paint paint;
    double ogH;
    double xVel, yVel, chargeStartTime;
    boolean charging, jumping, falling, onGround, moving;
    final double JUMP_VEL = 50;

    public Jumper(Game game) {
        this.game = game;
        // dimension of the player relative to the play area (area; which is the size of the mid ground's image)
        double w = game.playAreaSize[0] * 0.05;
        ogH = game.playAreaSize[1] * 0.072;
        double bot = game.playAreaSize[1] * 0.91;

        rect = new Rect(game.getWidth()/2 - w/2, bot - ogH, w, ogH);

        paint = new Paint();
        paint.setColor(ContextCompat.getColor(game.getContext(), R.color.red));

        charging = false;
        jumping = false;
        falling = false;
        onGround = true;
        moving = false;
    }


    public void startCharge() {
        if (rect.h == ogH && onGround && !charging) {
            chargeStartTime = System.currentTimeMillis();
            rect.setHeight(ogH * 0.654);
            rect.moveY(ogH - rect.h);
            charging = true;
        }
    }

    public void moveLeft() {
        if (!moving && onGround) {
            xVel = -game.playAreaSize[0] * 0.00625;
            moving = true;
        }
    }


    public void moveRight() {
        if (!moving && onGround) {
            xVel = game.playAreaSize[0] * 0.00625;
            moving = true;
        }
    }

    public void stopMoving() {
        if (moving && onGround) {
            xVel = 0;
            moving = false;
        }
    }


    public void stopCharge() {
        if (charging) {
            rect.moveY(-(ogH - rect.h));
            rect.setHeight(ogH);
            jump();
            game.mainLoop.uButton.release();

            if (moving) {
                if (xVel < 0) {
                    xVel = -game.playAreaSize[0] * 0.0139;
                }
                else {
                    xVel = game.playAreaSize[0] * 0.0139;
                }
            }

            charging = false;
        }
    }

    public void jump() {
        if (onGround) {

            double chargeTimeInMillis = System.currentTimeMillis() - chargeStartTime;

            if (chargeTimeInMillis > 600) {
                chargeTimeInMillis = 600;
            }

            yVel = game.scaledY(-JUMP_VEL * chargeTimeInMillis/600);

            jumping = true;
            onGround = false;
        }
    }


    public void update(Rect[] tiles) {

        if (charging) {
            if (System.currentTimeMillis() - chargeStartTime >= 600) {
                stopCharge();
            }
        }

        if (!charging) {
            if (moving) {
                move(xVel * game.dt,  0, tiles);
            }

            onGround = false;
            move(0, yVel *game.dt, tiles);
            if (yVel < game.scaledY(JUMP_VEL)) {
                yVel += game.scaledY(3) *game.dt;
            }
        }

        // WORK ON THE UNTOG (top collision) and PLAKDA

    }


    public void move(double xVel, double yVel, Rect[] tiles) {
        // move rect in x direction
        rect.moveX(xVel);

        // handle collision
        for (Rect tile : tiles) {
            if (rect.collides(tile)) {
                // right collision (i.e., this.right collides with tile.left)
                if (xVel > 0) {
                    rect.setX(tile.left - rect.w);
                    this.xVel /= -2;
                }
                // left collision
                else if (xVel < 0) {
                    rect.setX(tile.right);
                    this.xVel /= -2;
                }
            }
        }

        // move rect in y direction
        rect.moveY(yVel);

        // handle collision
        for (Rect tile : tiles) {
            if (rect.collides(tile)) {
                // bot collision (i.e., this.bot collides with tile.top)
                if (yVel > 0) {
                    rect.setY(tile.top - rect.h);
                    this.yVel = 0;
                    onGround = true;
                    jumping = false;
                }
                // top collision
                else if (yVel < 0) {
                    rect.setY(tile.bot);
                    this.xVel /= 2;
                    this.yVel = 0;
                    jumping = false;
                }
            }
        }
    }



    public void draw(Canvas canvas) {

        canvas.drawRect((float) rect.left, (float) rect.top, (float) rect.right, (float) rect.bot, paint);
    }
}
