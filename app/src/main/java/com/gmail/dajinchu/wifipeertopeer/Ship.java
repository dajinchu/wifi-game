package com.gmail.dajinchu.wifipeertopeer;

import java.io.Serializable;

/**
 * Created by Da-Jin on 11/25/2014.
 */
public class Ship implements Serializable{
    double x, y, xVel=0,yVel=0;
    //int destx, desty;//Eventually give this back to give player control over individual ships
    //int color;

    Player my_owner;

    public Ship(int x, int y, Player owner){
        this.x = x;
        this.y = y;
        my_owner = owner;
    }


    public void frame(){
        double deltax = my_owner.destx - x;
        double deltay = my_owner.desty - y;
        double dist = Math.sqrt(Math.pow(deltay,2)+Math.pow(deltax,2));
        if(dist<GameActivity.STAR_RADIUS){
            xVel = yVel = 0;
            return;
        }
        double travelRatio = GameActivity.ACCELERATION/dist;
        if(Math.sqrt(Math.pow(deltay*travelRatio,2)+Math.pow(deltax*travelRatio,2))<GameActivity.TERMINAL_VELOCITY){
            xVel+=deltax*travelRatio;
            yVel+=deltay*travelRatio;
        }
        x += xVel;
        y += yVel;
    }
}
