package com.gmail.dajinchu.wifipeertopeer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


/**
 * Created by Da-Jin on 11/20/2014.
 */
public class GameView extends SurfaceView implements Runnable{

    public static final int HEIGHT = 400, WIDTH = 600;

    Paint mPaint = new Paint();

    GameActivity activity;

    int screenheight;
    int screenwidth;
    float density;

    private volatile boolean running = false;
    SurfaceHolder holder;
    Thread renderThread = null;

    //Memory savers
    Canvas canvas;

    int frames=0, frames1 =0;
    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        holder = getHolder();
        activity = (GameActivity) context;

        DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
        screenheight = dm.heightPixels;
        screenwidth = dm.widthPixels;
        density = dm.density;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            activity.moveDot((int) event.getX(), (int) event.getY());
            activity.sendDestination(activity.me.playerNumber, (int) event.getX(), (int) event.getY());
            //TODO get rid off? sendText(String.valueOf((int) event.getX()) + "x" + String.valueOf((int) event.getY()));
        }
        return true;
    }

    public void resume(){
        running = true;
        renderThread  = new Thread(this);
        renderThread.start();
        //TODO figure out what to do to recover
    }


    /*@Override
    public void onDraw(Canvas canvas){
        //long start = System.currentTimeMillis();
        //super.onDraw(canvas);
        if(activity.players!=null){
            for(Player player : activity.players){
                player.drawShips(canvas);
            }
        }
        //canvas.drawCircle(100,100,5,mPaint);
        activity.frames++;
        //Log.i("DRAW BENCHMARK", ""+(System.currentTimeMillis() - start));
    }*/

    @Override
    public void run() {
        final long start = SystemClock.uptimeMillis();
        long strt;
        while(running){
            try {
                Thread.sleep(12);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //frames1++;
            //update();*/


            if(!holder.getSurface().isValid()||!activity.gameStarted)
                continue;//Try again
            //strt = SystemClock.uptimeMillis();
            canvas = holder.lockCanvas();
            canvas.drawColor(Color.WHITE);
            if(activity.players!=null) {
                for (Player player : activity.players) {
                    player.drawShips(canvas);
                }
            }

            holder.unlockCanvasAndPost(canvas);
            //Log.i("GameView", String.valueOf(SystemClock.uptimeMillis()-strt));
            frames++;
            Log.w("GameView Benchmark", frames + " " + frames1 + " " + (frames / ((SystemClock.uptimeMillis() - start) / 1000.0)));
        }
    }

    private void update(){
        if(activity.players!=null){//TODO is this necessary?
            for(Player player : activity.players){
                player.frame();
            }
        }
    }

    public void pause(){
        running = false;
        boolean retry = true;
        while(retry){
            try{
                renderThread.join();
                retry = false;
            }catch (InterruptedException e){
                //retry
            }
        }
    }
}
