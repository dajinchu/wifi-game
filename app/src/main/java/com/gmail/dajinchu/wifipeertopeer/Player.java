package com.gmail.dajinchu.wifipeertopeer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.ArrayList;

/**
 * Created by Da-Jin on 11/27/2014.
 */
public class Player {
    ArrayList<Ship> my_ships = new ArrayList<Ship>();//ships under this Player's control
    int playerNumber;//For identification across devices, each number corresponds to a color
    int destx=50,desty=50;

    //Graphics
    static int[] colorMap = new int[]{Color.BLUE, Color.RED};//number->color link
    final int color;//For graphics, may be replaced with Bitmap
    Paint mPaint = new Paint();

    public Player(int playerNumber){
        this.playerNumber = playerNumber;
        color = colorMap[playerNumber];
        mPaint.setColor(color);
    }
    public void drawShips(Canvas canvas){
        for(Ship ship : my_ships){
            canvas.drawCircle((int)ship.x,(int)ship.y,5,mPaint);
        }
    }

}
