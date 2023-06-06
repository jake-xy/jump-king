package com.example.jumpking.objects;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.provider.MediaStore;

import androidx.core.content.ContextCompat;

import com.example.jumpking.Game;
import com.example.jumpking.R;



public class Jumper {

    public Rect rect;
    Game game;
    Paint paint;

    // physics variables
    double xVel, yVel, chargeStartTime, maxChargeTime = 600;
    boolean charging, onGround, moving, sloping;
    final double JUMP_VEL = 50, MAX_YVEL = JUMP_VEL*1.10;
    double MOVE_VEL, JUMP_X_VEL;
    int slopeXdir;
    double acc;

    // animation variables
    final static int RIGHT = 0;
    final static int LEFT = 1;
    int facingDirection;
    double ogH, walkTick, peakY;
    Bitmap[] walkBitmaps = new Bitmap[4];
    Bitmap[] jumpBitmaps = new Bitmap[4];
    Bitmap bumpBitmap;
    public boolean showHitbox = false;
    boolean plakda, falling, bumped;

    // sfx variables
    MediaPlayer jumpSFX, bumpSFX, landSFX, splatSFX;


    public Jumper(Game game) {
        this.game = game;

        // generate a new jumper if there is no save
        genereateNewJumper();

        // load save file

        // for the physics
        onGround = true;
        charging = false;
        moving = false;
        sloping = false;
        MOVE_VEL = game.playAreaRect.w * 0.00625;
        JUMP_X_VEL = game.playAreaRect.w * 0.0139;
        xVel = 0;
        yVel = 0;

        // for the animation
        initializeSprites();
        facingDirection = RIGHT;
        plakda = false;
        falling = false;
        bumped = false;

        // for the sound
        bumpSFX = MediaPlayer.create(game.getContext(), R.raw.king_bump);
        jumpSFX = MediaPlayer.create(game.getContext(), R.raw.king_jump);
        landSFX = MediaPlayer.create(game.getContext(), R.raw.king_land);
        splatSFX = MediaPlayer.create(game.getContext(), R.raw.king_splat);


        // for the hitbox
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        paint.setColor(ContextCompat.getColor(game.getContext(), R.color.red));
    }


    private void genereateNewJumper() {
        // dimension of the player relative to the play area (area; which is the size of the mid ground's image)
        this.ogH = game.playAreaRect.h * 0.072;
        double w = game.playAreaRect.w * 0.05;
        double bot = game.playAreaRect.h * 0.91;

        rect = new Rect(game.getWidth()/2 - w/2, bot - ogH, w, ogH);
    }


    private void initializeSprites() {
        walkTick = 0;

        // for the walking sprites
        Bitmap walkSprite;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;

        walkSprite = BitmapFactory.decodeResource(game.getResources(), R.drawable.king_walks, options);
        double height = rect.h;
        double width = walkSprite.getWidth()*height / walkSprite.getHeight();

        walkSprite = Bitmap.createScaledBitmap(walkSprite, (int) width, (int) height, true);

        double singleW = walkSprite.getWidth()/4;

        for (int i = 0; i < 4; i++) {
            walkBitmaps[i] = Bitmap.createBitmap(walkSprite, (int)(i*singleW), 0,(int) singleW,(int) height);
        }

        // for the jumping
        Bitmap jumpSprite;

        jumpSprite = BitmapFactory.decodeResource(game.getResources(), R.drawable.king_jumps, options);
        height = height*1.231; // jump sprite's height is 0.231 bigger than walk sprite
        width = jumpSprite.getWidth()*height / jumpSprite.getHeight();

        jumpSprite = Bitmap.createScaledBitmap(jumpSprite, (int) width, (int) height, true);

        singleW = jumpSprite.getWidth()/4;

        for (int i = 0; i < 4; i++) {
            jumpBitmaps[i] = Bitmap.createBitmap(jumpSprite, (int)(i*singleW), 0,(int) singleW,(int) height);
        }

        // for the bump
        bumpBitmap = BitmapFactory.decodeResource(game.getResources(), R.drawable.king_bumps);
        width = bumpBitmap.getWidth()*rect.h / bumpBitmap.getHeight();
        bumpBitmap = Bitmap.createScaledBitmap(bumpBitmap, (int) width, (int) height, true);
    }


    public void startCharge() {
        if (rect.h == ogH && onGround && !charging && !plakda) {
            chargeStartTime = System.currentTimeMillis();
            rect.setHeight(ogH * 0.654);
            rect.moveY(ogH - rect.h);
            charging = true;
        }
    }

    public void moveLeft() {
        if (!moving && onGround && !sloping) {
            // physics
            xVel = MOVE_VEL*-1;
            moving = true;

            // animation
            facingDirection = LEFT;
            walkTick = 0;
            if (plakda) {
                plakda = false;
            }
        }
    }


    public void moveRight() {
        if (!moving && onGround && !sloping) {
            // physics
            xVel = MOVE_VEL;
            moving = true;

            // animation
            facingDirection = RIGHT;
            walkTick = 0;
            if (plakda) {
                plakda = false;
            }
        }
    }

    public void stopMoving() {
        if (moving && onGround) {
            // physics
            xVel = 0;
            moving = false;

            // animation
            walkTick = 0;
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
                    // xvel when jumping relative to the play area w
                    xVel = -JUMP_X_VEL;
                }
                else {
                    xVel = JUMP_X_VEL;
                }
            }

            charging = false;
        }
    }

    public void jump() {
        if (onGround) {
            // physics
            double chargeTimeInMillis = System.currentTimeMillis() - chargeStartTime;

            if (chargeTimeInMillis > maxChargeTime) {
                chargeTimeInMillis = maxChargeTime;
            }

            yVel = game.scaledY(-JUMP_VEL * chargeTimeInMillis/maxChargeTime);

            // sfx
            jumpSFX.start();

            // update flags
            onGround = false;
        }
    }


    public void update(Level level) {

        if (charging) {
            if (System.currentTimeMillis() - chargeStartTime >= maxChargeTime) {
                stopCharge();
            }
        }

        if (!charging) {
            // check if still on ground
            if (onGround && !plakda && !sloping) {
                // check if there is a tile below the dude
                Tile[] collidedTiles = new Tile[0];
                onGround = false;
                for (Tile tile : level.tiles) {
                    Rect collisionRect = new Rect(rect.x, rect.y+1, rect.w, rect.h);
                    // save those tiles
                    if (collisionRect.collides(tile)) {
                        collidedTiles = game.mainLoop.currentLevel.append(tile, collidedTiles);
                    }
                }

                // check if there is still a tile that is rect below
                for (Tile tile : collidedTiles) {
                    // if there are any, the dude is still onGround
                    if (tile.type == Tile.RECT) {
                        onGround = true;
                        break;
                    }
                }

            }

            // movement when sloping
            if (sloping) {
                move(xVel *game.dt, yVel *game.dt, level);

                // gravity
                if (!onGround) {
                    // 125% of JUMP_VEL is the max yVel
                    if (yVel < game.scaledY(MAX_YVEL)) {
                        yVel += game.scaledY(1.5) *game.dt;
                        xVel += game.scaledY(1.5)*slopeXdir *game.dt;
                    }
                }

            }
            else {

                move(xVel *game.dt, yVel *game.dt, level);

                if (moving) {
                    // animation
                    if ((int)walkTick < 15 -1) {
                        walkTick += 1 *game.dt;
                    }
                    else {
                        walkTick = 0;
                    }
                }

                // gravity
                if (!onGround) {
                    // 125% of JUMP_VEL is the max yVel
                    if (yVel < game.scaledY(MAX_YVEL)) {
                        yVel += game.scaledY(3) *game.dt;
                    }
                }
            }


            // animation
            if (yVel > 0 && !falling) {
                peakY = rect.y - game.mainLoop.level*game.playAreaRect.h;
                falling = true;
            }
        }
    }


    public void move(double xVel, double yVel, Level level) {
        if (xVel == 0 && yVel == 0) return;

        // move x first
        moveX(xVel, level);

        // move y
        moveY(yVel, level);

        // condition to stop sloping
        if (sloping) {
            boolean collided = false;
            for (Tile tile : level.tiles) {
                if (rect.collides(tile)) {
                    System.out.println("colliding");
                    collided = true;
                }
            }

            // king's not sloping anymore when he's not in a slope tile
            if (!collided) {
                sloping = false;
            }

        }

    }


    public void moveX(double xVel, Level level) {
        // move x first
        rect.moveX(xVel);

        // then handle collision only when not sloping and when moving
        if (!sloping && yVel != 0) {
            // handle slope collision first
            for (Tile slopeTile : level.slopeTiles) {
                if (rect.collides(slopeTile)) {
                    int[] airDir = slopeTile.getAirDir(level.rectTiles);

                    if (airDir[0] == 1) { //   slope --> |\
                        // left collision
                        if (xVel < 0 && rect.left < slopeTile.centerX) {
                            // adjust x
                            rect.setX(slopeTile.centerX);
                        }
                        // right collision
                        else if (xVel > 0 && onGround) {
                            rect.setX(slopeTile.left - rect.w);
                        }
                    }
                    else if (airDir[0] == -1) { //  slope --> /|
                        // right collision
                        if (xVel > 0 && rect.right > slopeTile.centerX) {
                            // adjust x
                            rect.setX(slopeTile.centerX - rect.w);
                        }
                        // left collision
                        else if (xVel < 0 && onGround) {
                            rect.setX(slopeTile.right);
                        }
                    }
                }
            }
        }

        if (!sloping && xVel != 0) {
            // handle rect collision
            for (Tile rectTile : level.rectTiles) {
                if (rect.collides(rectTile) && rectTile.rectType == Tile.RECT_SOLID) {
                    // right collision
                    if (xVel > 0) {
                        //adjust x
                        rect.setX(rectTile.left - rect.w);
                    }
                    // left collision
                    else {
                        // adjust x
                        rect.setX(rectTile.right);
                    }

                    // bump
                    if (!onGround) {
                        this.xVel /= -2;
                        bumpSFX.start();
                    }
                }
            }
        }

    }


    public void moveY(double yVel, Level level) {
        // move y first
        rect.moveY(yVel);

        // only handle collision when not sloping and when moving
        if (!sloping && yVel != 0) {
            // handle slope collision
            for (Tile slopeTile : level.slopeTiles) {
                if (rect.collides(slopeTile)) {
                    int[] airDir = slopeTile.getAirDir(level.rectTiles);

                    // bot collision
                    if (airDir[1] == -1 && yVel > 0) {
                        if (rect.bot > slopeTile.centerY) { // dude is sloping
                            // adjust y
                            rect.setY(slopeTile.centerY - rect.h);

                            // initialize sloping
                            this.slopeXdir = airDir[0];
                            // when sloping down, the yVel is only 25% of the current yVel
                            this.yVel = this.yVel * 0.25;
                            this.xVel = this.yVel * slopeXdir;

                            moving = false;
                            onGround = false;
                            sloping = true;
                        }
                    }
                    // top collision
                    else if (airDir[1] == 1 && yVel < 0) {
                        if (rect.top < slopeTile.centerY) {
                            // adjust y
                            rect.setY(slopeTile.centerY);

                            // initialize sloping
                            this.slopeXdir = airDir[0];
                            this.xVel = this.yVel * slopeXdir;

                            moving = false;
                            onGround = false;
                            sloping = true;
                        }
                    }
                }
            }
        }

        if (!sloping && yVel != 0) {
            // handle rect collision
            for (Tile rectTile : level.rectTiles) {
                if (rect.collides(rectTile) && rectTile.rectType == Tile.RECT_SOLID) {
                    // bot collision
                    if (yVel > 0) {
                        // adjust y
                        rect.setY(rectTile.top - rect.h);

                        landSFX.start();

                        // splat animation
                        if (!onGround) {
                            double distanceFell = (rect.y - game.mainLoop.level*game.playAreaRect.h) - peakY;

                            if (distanceFell >= game.playAreaRect.h*0.75) {
                                game.mainLoop.lButton.release();
                                game.mainLoop.rButton.release();
                                plakda = true;

                                // sfx
                                splatSFX.start();
                            }
                        }

                        // reset physics variables
                        sloping = false;
                        bumped = false;
                        onGround = true;
                        this.xVel = 0;
                        this.yVel = 0;
                        moving = false;
                        // reset animation variables
                        walkTick = 0;
                        falling = false;
                    }
                    // top collision
                    else {
                        // adjust y
                        rect.setY(rectTile.bot);

                        this.xVel /= 2;
                        this.yVel = 0;
                        bumpSFX.start();
                    }

                }
            }
        }
    }


    public void draw(Canvas canvas) {
        Bitmap finalDisplay;

        if (charging) {
            finalDisplay = jumpBitmaps[0];
        }
        else if (plakda) {
            finalDisplay = jumpBitmaps[3];
        }
        else if (onGround) {
            if (moving) {
                int idx = (int)( (int)walkTick / (15.0/3.0) ) + 1;
                // arrayIndexOutOfBounds error keeps on happening
                try {
                    finalDisplay = walkBitmaps[idx];
                }
                catch (ArrayIndexOutOfBoundsException e) {
                    idx -= 1;
                    finalDisplay = walkBitmaps[idx];
                }
            }
            else {
                finalDisplay = walkBitmaps[0];
            }
        }
        else if (bumped) {
            finalDisplay = bumpBitmap;
        }
        else {
            if (yVel < 0) {
                finalDisplay = jumpBitmaps[1];
            }
            else {
                finalDisplay = jumpBitmaps[2];
            }
        }

        // flipp based on the direction
        finalDisplay = facingDirection == RIGHT ? finalDisplay : getFlippedBitmap(finalDisplay, true, false);

        canvas.drawBitmap(
                finalDisplay,
                (float) (rect.centerX - finalDisplay.getWidth()/2),
                (float) rect.top,
                null
        );

        if (showHitbox) {
            canvas.drawRect((float) rect.left, (float) rect.top, (float) rect.right, (float) rect.bot, paint);
        }
    }


    private Bitmap getFlippedBitmap(Bitmap source, boolean xFlip, boolean yFlip) {
        Matrix matrix = new Matrix();
        matrix.postScale(xFlip ? -1 : 1, yFlip ? -1 : 1, source.getWidth() / 2f, source.getHeight() / 2f);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }
}
