package com.mambayamba.tappydefender;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.Random;

/**
 * Created by макс on 26.01.2017.
 */

public class EnemyShip {
    private Bitmap bitmap;
    private int x, y;
    private int maxX, minX;
    private int maxY, minY;
    private int speed = 1;

    public EnemyShip(Context context, int screenSizeX, int screenSizeY){
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.enemy);
        maxX = screenSizeX;
        maxY = screenSizeY;
        minX = 0;
        minY = 0;

        Random generator = new Random();
        speed = generator.nextInt(6)+10;
        y = generator.nextInt(screenSizeY)-bitmap.getHeight();
        x = screenSizeX;
    }

    public void update(int playerSpeed){
        x -= playerSpeed;
        x -= speed;

        if(x < minX - bitmap.getWidth()){
            Random generator = new Random();
            speed = generator.nextInt(6)+10;
            x = maxX;
            y = generator.nextInt(maxY) - bitmap.getHeight();
        }
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

}
