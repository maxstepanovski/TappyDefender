package com.mambayamba.tappydefender;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.annotation.Nullable;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
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
//        if(!bitmap.isMutable()){
//            bitmap = convertToMutable(context, bitmap);
//        }
//        bitmap.reconfigure(100, 80, Bitmap.Config.ARGB_4444);
        bitmap = getResizedBitmap(bitmap, 120, 100);
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
        x -= (int)Math.floor(playerSpeed/6);
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
