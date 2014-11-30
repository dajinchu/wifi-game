package com.gmail.dajinchu.wifipeertopeer;

import java.io.Serializable;

/**
 * Created by Da-Jin on 11/25/2014.
 */
public class Ship implements Serializable{
    double x, y, xVel=0,yVel=0;
    boolean arrived = false, wanderArrived = true;//wanderArrived needs to be true when not arrived to trigger finding a new wander when arrived at Player destx
    int wanderdestx, wanderdesty;//Eventually give this back to give player control over individual ships
    //int color;

    //Temp stuff, local variables here to save memory
    double minx,maxx,miny,maxy;

    Player my_owner;
    GameActivity activity;
    private double desiredx, desiredy, dist, speed, steeringx,steeringy, steerMagnitude, ratio;

    public Ship(int x, int y, Player owner, GameActivity activity){
        this.x = x;
        this.y = y;
        my_owner = owner;
        this.activity = activity;
    }


    public void frame(){
        arrive();
        /*Ship target = getTarget();
        if(target != null){
            target.my_owner.remove_ships.add(target);//TODO maybbe just iterate through a clone and remove from real?
            my_owner.remove_ships.add(this);
        }*/
    }

    public void arrive(){
        desiredx = my_owner.destx-x;
        desiredy = my_owner.desty-y;

        dist = Math.sqrt(Math.pow(desiredx,2)+Math.pow(desiredy,2));//Magnitude of desired

        //m is speed to multiply
        if(dist<GameActivity.DEST_RADIUS){
            //Closer we get, slower we get, its a proportion
            speed = GameActivity.TERMINAL_VELOCITY * (dist /GameActivity.DEST_RADIUS);
        } else{
            speed = GameActivity.TERMINAL_VELOCITY;
        }

        desiredx*=speed;
        desiredy*=speed;

        steeringx = desiredx-xVel;
        steeringy = desiredy-yVel;

        steerMagnitude = Math.sqrt(steeringx*steeringx+steeringy*steeringy);
        if(steerMagnitude>GameActivity.MAX_FORCE){
            ratio = GameActivity.MAX_FORCE/steerMagnitude;
            steeringx*=ratio;
            steeringy*=ratio;
        }

        xVel += steeringx;
        yVel += steeringy;

        x += xVel;
        y += yVel;

    }

    public Ship getTarget(){
        //pre-calculation bounds for efficiency
        minx=x-GameActivity.ENGAGEMENT_RANGE;
        miny=y-GameActivity.ENGAGEMENT_RANGE;
        maxx=x+GameActivity.ENGAGEMENT_RANGE;
        maxy=y+GameActivity.ENGAGEMENT_RANGE;

        for(Player enemy: activity.players){
            if(enemy != my_owner){
                for(Ship ship : enemy.my_ships){
                    if(minx<ship.x&&ship.x<maxx&&miny<ship.y&&ship.y<maxy){
                        if(Math.pow(ship.x-x,2)+Math.pow(ship.y-y,2)<GameActivity.ENGAGEMENT_RANGE){
                            return ship;
                        }
                    }
                }
            }
        }
        return null;
    }
}
