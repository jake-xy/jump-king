package com.example.jumpking.objects;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import androidx.core.content.ContextCompat;

import com.example.jumpking.Game;
import com.example.jumpking.R;



public class Jumper {

    public Rect rect;
    Game game;
    Paint paint;

    // physics variables
    double xVel, yVel, chargeStartTime;
    boolean charging, jumping, onGround, moving, slopingDown;
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
    protected final boolean showHitbox = false;
    boolean plakda, falling;


    public Jumper(Game game) {
        this.game = game;

        // generate a new jumper if there is no save
        genereateNewJumper();

        // load save file

        // for the physics
        onGround = true;
        charging = false;
        jumping = false;
        moving = false;
        slopingDown = false;
        MOVE_VEL = game.playAreaRect.w * 0.00625;
        JUMP_X_VEL = game.playAreaRect.w * 0.0139;

        // for the animation
        initializeSprites();
        facingDirection = RIGHT;
        plakda = false;
        falling = false;

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
        if (!moving && onGround && !slopingDown) {
            // physics
            xVel = -MOVE_VEL;
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
        if (!moving && onGround && !slopingDown) {
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

            double chargeTimeInMillis = System.currentTimeMillis() - chargeStartTime;

            if (chargeTimeInMillis > 600) {
                chargeTimeInMillis = 600;
            }

            yVel = game.scaledY(-JUMP_VEL * chargeTimeInMillis/600);

            jumping = true;
            onGround = false;
        }
    }


    public void update(Tile[] tiles) {

        if (charging) {
            if (System.currentTimeMillis() - chargeStartTime >= 600) {
                stopCharge();
            }
        }

        if (!charging) {
            if (moving && !slopingDown) {
                // physics
                move(xVel *game.dt,  0, tiles);

                // animation
                if ((int)walkTick < 15 -1) {
                    walkTick += 1 *game.dt;
                }
                else {
                    walkTick = 0;
                }
            }

            // check if still on ground
            if (onGround && !plakda && !slopingDown) {
                // check if there is a tile below the dude
                Tile[] collidedTiles = new Tile[0];
                onGround = false;
                for (Tile tile : tiles) {
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

            if (slopingDown) {
                move(yVel*slopeXdir *game.dt, yVel *game.dt, tiles);
            }
            else {
//                System.out.println("yvel before: " + yVel);
                move(0, yVel *game.dt, tiles);
//                System.out.println("yvel after: " + yVel);
            }

            // gravity
            if (!onGround) {
                // 125% of JUMP_VEL is the max yVel
                if (yVel < game.scaledY(MAX_YVEL)) {
                    yVel += game.scaledY(3) *game.dt;
                }
            }

            // animation
            if (yVel > 0 && !falling) {
                peakY = rect.y - game.mainLoop.level*game.playAreaRect.h;
                falling = true;
            }
        }
    }


    public void move(double xVel, double yVel, Tile[] tiles) {
        // move rect in x direction
        rect.moveX(xVel);

        // handle collision
        for (Tile tile : tiles) {
            if (rect.collides(tile)) {
                // right collision (i.e., this.right collides with tile.left)
                if (xVel > 0 && !slopingDown) {
                    // rect collision
                    if (tile.type == Tile.RECT) {
                        rect.setX(tile.left - rect.w);
                        if (jumping) {
                            this.xVel /= -2;
                        }
                    }
                    // right slope collision
                    else {
                        if (!slopingDown) {
                            rect.setX(tile.left - rect.w);
                        }
                    }
                }
                // left collision
                else if (xVel < 0 && !slopingDown) {
                    // rect collision
                    if (tile.type == Tile.RECT) {
                        rect.setX(tile.right);
                        if (jumping) {
                            this.xVel /= -2;
                        }

//                        // condition to stop sloping down (when the dude bumps into a wall
//                        if (slopingDown) {
//                            slopingDown = false;
//                        }


                    }
                    // left slope collision
                    else {
                        if (!slopingDown) {
                            rect.setX(tile.right);
                        }
                    }
                }
            }
        }

        // move rect in y direction
        rect.moveY(yVel);

        Tile[] collidedTiles = new Tile[0]; // used for slope collision

        // handle collision
        for (Tile tile : tiles) {
            if (rect.collides(tile)) {
                collidedTiles = game.mainLoop.currentLevel.append(tile, collidedTiles);
                // bot collision (i.e., this.bot collides with tile.top)
                if (yVel > 0 && !slopingDown) {
                    // rect collision
                    if (tile.type == Tile.RECT) {
                        // physics
                        rect.setY(tile.top - rect.h);
                        this.yVel = 0;

                        // if airborne and it touches the ground, it's not moving already
                        if (!onGround) {
                            moving = false;
                            walkTick = 0;
                        }

                        // animation
                        if (!onGround) {
                            double distanceFell = (rect.y - game.mainLoop.level*game.playAreaRect.h) - peakY;

                            if (distanceFell >= game.playAreaRect.h*0.75) {
                                game.mainLoop.lButton.release();
                                game.mainLoop.rButton.release();
                                plakda = true;
                            }
                        }

                        // reset the physics variables
                        slopingDown = false;
                        falling = false;
                        jumping = false;
                        onGround = true;
                    }
                    // bot slope collision
                    else {
                        if (!slopingDown) {
                            rect.setY(tile.top - rect.h);
                        }
                    }
                }
                // top collision
                else if (yVel < 0 && !slopingDown) {
                    // rect collision
                    if (tile.type == Tile.RECT) {
                        rect.setY(tile.bot);
                        this.xVel /= 2;
                        this.yVel = 0;
                        jumping = false;
                    }
                    // top slope collision
                    else {
//                        if (!slopingDown) {
//                            rect.setY(tile.bot);
//                        }
                    }
                }
            }
        }

        // slope collision
        boolean hasRect = false;
        Tile collidedSlope = null;
        for (Tile tile : collidedTiles) {
            if (tile.type == Tile.RECT) {
                hasRect = true;
                break;
            }
            else {
                collidedSlope = tile;
            }
        }

        if (collidedTiles.length > 0 && !slopingDown) {
            // if no collision with a tile rect is present, it means collided tile is slope
            if (!hasRect && collidedSlope != null && yVel > 0) {

                // determine the x dir of the slope
                this.slopeXdir = collidedSlope.getSlopeXdir(tiles);

                // adjust the position to make the sloping smoother
                if (slopeXdir == 1) {
                    rect.setX(collidedSlope.left);
                }
                else {
                    rect.setX(collidedSlope.right - this.rect.w);
                }
                rect.setY(collidedSlope.top - rect.h);

                // when sloping down, the yVel is only 15% of the JUMPVEL
                this.yVel = JUMP_VEL * 0.15;

                moving = false;
                onGround = false;
                slopingDown = true;
            }
        }

        // condition to stop slopingdown
        // if the dude is airborne already or it only collides with a rect
        if ( (slopingDown && collidedTiles.length <= 0) || (slopingDown && hasRect && collidedSlope == null) ) {

            if (slopeXdir == 1) {
                // move right
                this.xVel = this.yVel/2;
                moving = true;
            }
            else {
                // move left
                this.xVel = -this.yVel/2;
                moving = true;
            }

            slopingDown = false;
        }

    }



    public void draw(Canvas canvas) {
        Bitmap finalDisplay;
        int idx = (int)( (int)walkTick / (15.0/3.0) ) + 1;

        if (charging) {
            finalDisplay = jumpBitmaps[0];
        }
        else if (plakda) {
            finalDisplay = jumpBitmaps[3];
        }
        else if (onGround) {
            if (moving) {
                // arrayIndexOutOfBounds error keeps on happening
                finalDisplay = walkBitmaps[idx];
            }
            else {
                finalDisplay = walkBitmaps[0];
            }
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
