package com.gmail.dajinchu.wifipeertopeer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Da-Jin on 11/27/2014.
 */
public class Player implements Serializable {
    ArrayList<Ship> my_ships = new ArrayList<Ship>();//ships under this Player's control
    int playerNumber;//For identification across devices, each number corresponds to a color
    int destx=50,desty=50;

    //Graphics
    static int[] colorMap = new int[]{Color.BLUE, Color.RED};//number->color link
    final int color;//For graphics, may be replaced with Bitmap
    transient Paint mPaint = new Paint();

    public Player(int playerNumber){
        this.playerNumber = playerNumber;
        color = colorMap[playerNumber];
    }
    public void drawShips(Canvas canvas){
        mPaint.setColor(color);
        for(Ship ship : my_ships){
            canvas.drawCircle((int)ship.x,(int)ship.y,5,mPaint);
        }
        mPaint.setColor(Color.BLACK);
        canvas.drawCircle(destx,desty,GameActivity.STAR_RADIUS,mPaint);
    }
    public void frame(){
        for(Ship ship : my_ships){
            ship.frame();
        }
    }

    private void readObject(ObjectInputStream in) throws IOException,ClassNotFoundException {
        in.defaultReadObject();
        mPaint = new Paint();
        Log.i("Player", "Getting de-serialized!");
    }
}
