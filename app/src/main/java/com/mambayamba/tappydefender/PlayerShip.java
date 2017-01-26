package com.mambayamba.tappydefender;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

/**
 * Created by макс on 26.01.2017.
 */

public class PlayerShip {
    private final int GRAVITY = -12;
    private final int MIN_SPEED = 1;
    private final int MAX_SPEED = 20;
    private int minY, maxY;
    private Bitmap bitmap;
    private int x,y;
    private int speed;
    private boolean boosting;

    public PlayerShip(Context context, int screenSizeX, int screenSizeY) {
        speed = 1;
        x = 100;
        y = 50;
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ship);
        boosting = false;
        minY = 0;
        maxY = screenSizeY - bitmap.getHeight();
    }

    public void update(){
        if(boosting){
            speed += 3;
        }else{
            speed -=5;
        }

        if(speed > MAX_SPEED){
            speed = MAX_SPEED;
        }
        if(speed < MIN_SPEED){
            speed = MIN_SPEED;
        }

        y -=(speed + GRAVITY);

        if(y > maxY){
            y = maxY;
        }
        if(y < minY){
            y = minY;
        }
        Log.d("happy", x + " : "+y+" speed"+ speed);
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public int getSpeed() {
        return speed;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void stopBoosting(){
        boosting = false;
    }

    public void setBoosting(){
        boosting = true;
    }
}
