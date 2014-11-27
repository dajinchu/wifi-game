package com.gmail.dajinchu.wifipeertopeer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;


/**
 * Created by Da-Jin on 11/20/2014.
 */
public class GameView extends View {

    public static final int HEIGHT = 400, WIDTH = 600;

    public ArrayList<Ship> ships = new ArrayList<Ship>();

    Paint mPaint = new Paint();

    public GameView(Context context) {
        super(context);
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init(){

    }

    public void frame(){
        for(Ship ship : ships){
            ship.frame();
        }

        invalidate();
    }

    @Override
    public void onDraw(Canvas canvas){
        super.onDraw(canvas);
        mPaint.setColor(Color.rgb(127,255,127));
        for(Ship ship : ships){
            canvas.drawCircle((int)ship.x,(int)ship.y,5,mPaint);
        }
        mPaint.setColor(Color.RED);
        canvas.drawCircle(me.destx,me.desty,3,mPaint);
    }
}
