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
    boolean charging, jumping, onGround, moving;
    final double JUMP_VEL = 50, MAX_YVEL = JUMP_VEL*1.10;


    // animation variables
    final static int RIGHT = 0;
    final static int LEFT = 1;
    int facingDirection;
    double ogH, walkTick, distanceFell;
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
        charging = false;
        jumping = false;
        onGround = true;
        moving = false;

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
        if (!moving && onGround) {
            // physics
            xVel = -game.playAreaRect.w * 0.00625;
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
        if (!moving && onGround) {
            // physics
            xVel = game.playAreaRect.w * 0.00625;
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
                    xVel = -game.playAreaRect.w * 0.0139;
                }
                else {
                    xVel = game.playAreaRect.w * 0.0139;
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
                // physics
                move(xVel * game.dt,  0, tiles);

                // animation
                if ((int)walkTick < 15 -1) {
                    walkTick += 1 *game.dt;
                }
                else {
                    walkTick = 0;
                }
            }

            // check if still on ground
            if (onGround && !plakda) {
                // when on ground, check if there is still a tile below the dude
                // if there is non, make the dude fall
                onGround = false;
                for (Rect tile : tiles) {
                    Rect collisionRect = new Rect(rect.x, rect.y+1, rect.w, rect.h);
                    if (collisionRect.collides(tile)) {
                        onGround = true;
                        break;
                    }
                }
            }

            move(0, yVel *game.dt, tiles);
            // 125% of JUMP_VEL is the max yVel
            if (yVel < game.scaledY(MAX_YVEL)) {
                yVel += game.scaledY(3) *game.dt;
            }

            // animation
            if (yVel > 0 && !falling) {
                distanceFell = 0;
                falling = true;
            }
            if (falling) {
                distanceFell += yVel;
            }
        }
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
                    if (jumping) {
                        this.xVel /= -2;
                    }
                }
                // left collision
                else if (xVel < 0) {
                    rect.setX(tile.right);
                    if (jumping) {
                        this.xVel /= -2;
                    }
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
                        if (distanceFell >= game.playAreaRect.h/2) {
                            plakda = true;
                        }
                    }

                    falling = false;
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
        Bitmap finalDisplay;
        int idx = (int)( (int)walkTick / (15.0/4.0) );

        finalDisplay = walkBitmaps[0];

        if (charging) {
            finalDisplay = jumpBitmaps[0];
        }
        else if (plakda) {
            finalDisplay = jumpBitmaps[3];
        }
        else if (onGround) {
            // arrayIndexOutOfBounds error keeps on happening
            finalDisplay = walkBitmaps[idx];
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
