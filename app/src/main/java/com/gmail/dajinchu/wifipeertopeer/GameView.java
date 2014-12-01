package com.gmail.dajinchu.wifipeertopeer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
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

    volatile boolean running = false;
    SurfaceHolder holder;
    Thread renderThread = null;

    //Memory savers
    Canvas canvas;

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        holder = getHolder();
        activity = (GameActivity) context;

        DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
        screenheight = dm.heightPixels;
        screenwidth = dm.widthPixels;
        density = dm.density;
    }

    public void resume(){
        running = true;
        renderThread  = new Thread(this);
        renderThread.start();
        //TODO figure out what to do to recover
    }


    @Override
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
    }

    @Override
    public void run() {
        while(running){
            if(!holder.getSurface().isValid())
                continue;//Wait for validity
            canvas = holder.lockCanvas();
            if(activity.players!=null) {
                for (Player player : activity.players) {
                    player.drawShips(canvas);
                }
            }
            holder.unlockCanvasAndPost(canvas);
            update();
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
