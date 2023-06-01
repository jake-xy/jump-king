package com.example.jumpking.objects;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;

import com.example.jumpking.Game;

public class XButton {

    public Rect rect;
    Game game;
    Bitmap bitmap;
    Paint paint;

    public boolean pressedDown;
    public int pointerID;

    public XButton(Game game, int imgResID, double height) {
        this.game = game;
        pressedDown = false;
        paint = new Paint();
        paint.setAlpha(200);
        pointerID = -1;

        bitmap = BitmapFactory.decodeResource(game.getResources(), imgResID);
        // get scaled width based on passed height and bitmap's og dimension
        double width = bitmap.getWidth()*height / bitmap.getHeight();

        // resize
        bitmap = Bitmap.createScaledBitmap(bitmap, (int) width, (int) height, true);

        rect = new Rect(0, 0, width, height);
    }


    public XButton(Game game, int imgResID, int rectX, int rectY, int height) {
        this(game, imgResID, height);

        this.rect.setX(rectX);
        this.rect.setY(rectY);
    }


    public boolean press(int x, int y) {

        if (!pressedDown && rect.collides(x, y)) {
            setDimension(rect.w - 20, rect.h - 20);
            rect.moveX(10);
            rect.moveY(10);
            pressedDown = true;
            return true;
        }

        return false;
    }


    public void release() {

        if (pressedDown) {
            setDimension(rect.w + 20, rect.h + 20);
            rect.moveX(-10);
            rect.moveY(-10);
            pointerID = -1;
            pressedDown = false;
        }

    }


    public boolean listenForEvent(MotionEvent event) {
        // do the thing with multiple touches from youtube guy
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                Log.d("XButton.java", "POINTER DOWN EVENT");
                if (rect.collides((int) event.getX(), (int) event.getY())) {
                    if (!pressedDown) {
                        pointerID = event.getPointerId(event.getActionIndex());
                        pressedDown = true;
                    }
                }
                return false;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_BUTTON_RELEASE:
                Log.d("XButton.java", "POINTER UP EVENT");
                if (event.getPointerId(event.getActionIndex()) == pointerID && pressedDown) {
                    pressedDown = false;
                }
                return true;
        }

        return false;
    }


    public void draw(Canvas canvas) {

        canvas.drawBitmap(bitmap, (float) rect.left, (float) rect.top, paint);

    }


    public void setX(double x) {
        this.rect.setX(x);
    }

    public void setY(double y) {
        this.rect.setY(y);
    }

    public void setHeight(double height) {
        double width = rect.w*height / rect.h;

        rect.setHeight(height);
        rect.setWidth(width);

        bitmap = Bitmap.createScaledBitmap(bitmap, (int) width, (int) height, true);
    }

    public void setDimension(double width, double height) {
        rect.setHeight(height);
        rect.setWidth(width);

        bitmap = Bitmap.createScaledBitmap(bitmap, (int) width, (int) height, true);
    }

    public double getWidth() {
        return rect.w;
    }

    public double getHeight() {
        return rect.h;
    }

    public double getY() {
        return rect.y;
    }

    public double getX() {
        return rect.x;
    }
}
