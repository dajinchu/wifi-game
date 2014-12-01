package com.gmail.dajinchu.wifipeertopeer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;


/**
 * Created by Da-Jin on 11/20/2014.
 */
public class GameView extends View {

    public static final int HEIGHT = 400, WIDTH = 600;

    Paint mPaint = new Paint();

    GameActivity activity;


    public GameView(Context context) {
        super(context);
        init();
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init(){
        activity = (GameActivity) getContext();
        mPaint.setColor(Color.BLUE);
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
}
