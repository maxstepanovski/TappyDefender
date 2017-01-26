package com.mambayamba.tappydefender;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;

/**
 * Created by макс on 26.01.2017.
 */

public class TDView extends SurfaceView implements Runnable {
    private volatile boolean playing;
    private Thread gameThread = null;
    private Canvas canvas;
    private SurfaceHolder ourHolder;
    private Paint paint;
    private int enemyCount = 5;

    private PlayerShip player;
    private List<EnemyShip> enemies = new ArrayList<>();

    public TDView(Context context, int screenSizeX, int screenSizeY) {
        super(context);
        ourHolder = getHolder();
        paint = new Paint();
        player = new PlayerShip(context, screenSizeX, screenSizeY);
        for(int i=0; i < enemyCount; ++i){
            EnemyShip enemy = new EnemyShip(context, screenSizeX, screenSizeY);
            enemies.add(enemy);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch(event.getAction() & event.ACTION_MASK){
            case MotionEvent.ACTION_DOWN:{
                player.setBoosting();
                Log.d("happy", "pressed!");
                break;
            }
            case MotionEvent.ACTION_UP:{
                player.stopBoosting();
                Log.d("happy", "released!");
                break;
            }
        }
        return true;
    }

    public void pause(){
        playing = false;
        try {
            gameThread.join();
            Log.d("happy", "game thread stopped!");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resume(){
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
        Log.d("happy", "game thread started!");
    }

    @Override
    public void run() {
        while(playing){
            update();
            draw();
            control();
        }
    }

    private void update() {
        player.update();
        for(EnemyShip enemy: enemies){
            enemy.update(player.getSpeed());
        }
    }

    private void draw() {
        if(ourHolder.getSurface().isValid()){
            //фиксируем холст
            canvas = ourHolder.lockCanvas();
            //чистим холст
            canvas.drawColor(Color.argb(255,0,0,0));
            //рисуем на холсте игрока и врагов
            canvas.drawBitmap(
                    player.getBitmap(),
                    player.getX(),
                    player.getY(),
                    paint
            );
            for(EnemyShip enemy: enemies){
                canvas.drawBitmap(
                        enemy.getBitmap(),
                        enemy.getX(),
                        enemy.getY(),
                        paint
                );
            }
            //рисуем всю сцену и разблокируем холст
            ourHolder.unlockCanvasAndPost(canvas);
        }

    }

    private void control(){
        try {
            sleep(17);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
