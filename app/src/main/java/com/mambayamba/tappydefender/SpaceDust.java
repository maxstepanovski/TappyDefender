package com.mambayamba.tappydefender;

import java.util.Random;

/**
 * Created by макс on 26.01.2017.
 */

public class SpaceDust {
    private int x, y, speed;
    private int minX, minY, maxX, maxY;

    public SpaceDust(int screenSizeX, int screenSizeY){
        minX = 0;
        minY = 0;
        maxX = screenSizeX;
        maxY = screenSizeY;

        Random generator = new Random();
        speed = generator.nextInt(10);
        x = generator.nextInt(maxX);
        y = generator.nextInt(maxY);
    }

    public void update(int playerSpeed){
        x -= playerSpeed;
        x -= speed;

        if(x < minX){
            x = maxX;
            Random generator = new Random();
            y = generator.nextInt(maxY);
            speed = generator.nextInt(10);
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
