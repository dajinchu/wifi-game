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
    ArrayList<Ship> remove_ships = new ArrayList<Ship>();//ships to be removed
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
        mPaint.setColor(Color.BLACK);
        canvas.drawCircle(destx,desty,GameActivity.DEST_RADIUS,mPaint);
        mPaint.setColor(color);
        for(Ship ship : my_ships){
            canvas.drawCircle((int)ship.x,(int)ship.y,5,mPaint);
        }
    }
    public void frame(){
        for(Ship ship : my_ships){
            ship.frame();
        }
        for(Ship ship: remove_ships){
            my_ships.remove(ship);
        }
        remove_ships.clear();
    }

    private void readObject(ObjectInputStream in) throws IOException,ClassNotFoundException {
        in.defaultReadObject();
        mPaint = new Paint();
        Log.i("Player", "Getting de-serialized!");
    }
}
