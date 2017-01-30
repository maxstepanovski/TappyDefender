package com.mambayamba.tappydefender;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
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
    private Rect hitBox;
    private int[] images;

    public EnemyShip(Context context, int screenSizeX, int screenSizeY){
        images = new int[]{R.drawable.enemy,
                R.drawable.enemy2,
                R.drawable.enemy3
        };
        Random generator = new Random();
        int skin = generator.nextInt(3);
        bitmap = BitmapFactory.decodeResource(context.getResources(), images[skin]);
        bitmap = getResizedBitmap(bitmap, 120, 100);
        maxX = screenSizeX;
        maxY = screenSizeY;
        minX = 0;
        minY = 0;


        speed = generator.nextInt(6)+10;
        y = generator.nextInt(screenSizeY)-bitmap.getHeight();
        x = screenSizeX;

        hitBox = new Rect(x, y, x+bitmap.getWidth(), y+bitmap.getHeight());
    }

    public void update(int playerSpeed){
        x -= (int)Math.floor(playerSpeed/6);
        x -= speed;

        if(x < minX - bitmap.getWidth()){
            Random generator = new Random();
            speed = generator.nextInt(6)+10;
            x = maxX;
            y = generator.nextInt(maxY) - bitmap.getHeight();
        }

        hitBox.left = x;
        hitBox.top = y;
        hitBox.right = x + bitmap.getWidth();
        hitBox.bottom = y + bitmap.getHeight();
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

    public Rect getHitBox() {
        return hitBox;
    }

    public void setX(int x) {
        this.x = x;
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }
}
