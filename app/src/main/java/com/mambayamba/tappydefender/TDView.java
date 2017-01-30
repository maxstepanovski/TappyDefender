package com.mambayamba.tappydefender;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioAttributes;
import android.media.SoundPool;
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
    public static final int TOTAL_DISTANCE = 15000;
    public static final String PREFS = "com.mambayamba.tappydefender";
    private static final String RECORD = "record";
    private static final long NO_VALUE = 0;
    private static final long DEFAULT_RECORD = 100000;
    private SharedPreferences prefs;
    private SoundPool soundPool;
    private int start = -1, bump = -1, destroy = -1, win = -1;
    private volatile boolean playing;
    private Thread gameThread = null;
    private Canvas canvas;
    private SurfaceHolder ourHolder;
    private Paint paint;
    private int enemyCount = 5;
    private int specCount = 500;
    private Context context;
    private boolean gameOver;
    int screenSizeX, screenSizeY;

    private long distanceRemaining;
    private long timeTaken;
    private long timeStarted;
    private long fastestTime;

    private PlayerShip player;
    private List<EnemyShip> enemies = new ArrayList<>();
    private List<SpaceDust> specs = new ArrayList<>();

    public TDView(Context context, int screenSizeX, int screenSizeY) {
        super(context);
        this.context = context;
        this.screenSizeX = screenSizeX;
        this.screenSizeY = screenSizeY;

        soundPool = new SoundPool.Builder()
                .setMaxStreams(10)
                .setAudioAttributes(new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build())
                .build();
        start = soundPool.load(context, R.raw.start, 0);
        bump = soundPool.load(context, R.raw.destroyed, 0);
        destroy = soundPool.load(context, R.raw.destroyed, 0);
        win = soundPool.load(context, R.raw.win, 0);

        ourHolder = getHolder();
        paint = new Paint();
        gameOver = false;
        player = new PlayerShip(context, screenSizeX, screenSizeY);

        prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        if(prefs.contains(RECORD))
            fastestTime = prefs.getLong(RECORD, DEFAULT_RECORD);
        else
            fastestTime = DEFAULT_RECORD;

        for(int i=0; i < enemyCount; ++i){
            EnemyShip enemy = new EnemyShip(context, screenSizeX, screenSizeY);
            enemies.add(enemy);
        }
        for(int i=0; i<specCount; ++i){
            SpaceDust spec = new SpaceDust(screenSizeX, screenSizeY);
            specs.add(spec);
        }
        startGame();
    }

    private void startGame(){
        player.setX(0);
        for(EnemyShip enemy: enemies){
            enemy.setX(screenSizeX);
        }
        distanceRemaining = TOTAL_DISTANCE;
        player.setShields(3);
        timeTaken = 0;
        timeStarted = System.currentTimeMillis();
        gameOver = false;
        soundPool.play(start, 1, 1, 0, 0, 1);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch(event.getAction() & event.ACTION_MASK){
            case MotionEvent.ACTION_DOWN:{
                player.setBoosting();
                if(gameOver)
                    startGame();
                break;
            }
            case MotionEvent.ACTION_UP:{
                player.stopBoosting();
                break;
            }
        }
        return true;
    }

    public void pause(){
        playing = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resume(){
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
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
        boolean hitDetected = false;
        player.update();
        for(EnemyShip enemy: enemies){
            enemy.update(player.getSpeed());
            if(Rect.intersects(player.getHitBox(), enemy.getHitBox())){
                hitDetected = true;
                enemy.setX(screenSizeX);
            }
        }
        if(hitDetected){
            player.reduceShields();
            if(player.getShields() >= 0){
                soundPool.play(bump, 1,1,0,0,1);
            }else {
                gameOver = true;
                soundPool.play(destroy, 1,1,0,0,1);
            }
        }
        for(int i=0; i<specCount; ++i){
            specs.get(i).update(player.getSpeed());
        }
        if(!gameOver){
            distanceRemaining -= player.getSpeed();
            timeTaken = System.currentTimeMillis() - timeStarted;
        }
        if(distanceRemaining <= 0){
            if(timeTaken < fastestTime){
                fastestTime = timeTaken;
                SharedPreferences.Editor editor = prefs.edit();
                editor.putLong(RECORD, timeTaken);
                editor.apply();
            }
            soundPool.play(win, 1, 1, 0, 0, 1);
            distanceRemaining = 0;
            gameOver = true;
        }
    }

    private void draw() {
        if(ourHolder.getSurface().isValid()){
            canvas = ourHolder.lockCanvas();
            canvas.drawColor(Color.argb(255,0,0,0));
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
            paint.setColor(Color.argb(255,255,255,255));
            for(int i=0; i<specCount; ++i)
                canvas.drawPoint(specs.get(i).getX(), specs.get(i).getY(), paint);

            if(!gameOver){
                paint.setTextAlign(Paint.Align.LEFT);
                paint.setColor(Color.CYAN);
                paint.setTextSize(25);
                canvas.drawText("Fastest time: " + fastestTime, 10, 20, paint);
                canvas.drawText("Time taken: " + timeTaken, screenSizeX/2, 20, paint);
                canvas.drawText("Distance: " + distanceRemaining, 10, screenSizeY - 20, paint);
                canvas.drawText("Shields: " + player.getShields(), screenSizeX/3, screenSizeY - 20, paint);
                canvas.drawText("Speed: " + player.getSpeed(), 2*(screenSizeX/3), screenSizeY - 20, paint);
            }else{
                paint.setTextAlign(Paint.Align.CENTER);
                paint.setColor(Color.CYAN);
                paint.setTextSize(80);
                canvas.drawText("Game Over", screenSizeX/2, 100, paint);
                paint.setTextSize(25);
                canvas.drawText("Fastest: " + fastestTime , screenSizeX/2, 160, paint);
                canvas.drawText("Time: " + timeTaken, screenSizeX/2, 200, paint);
                paint.setTextSize(80);
                canvas.drawText("Нажми, чтобы повторить, мудила!", screenSizeX/2, 350, paint);
            }
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
