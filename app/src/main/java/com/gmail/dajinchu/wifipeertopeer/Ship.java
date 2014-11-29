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

    public Ship(int x, int y, Player owner, GameActivity activity){
        this.x = x;
        this.y = y;
        my_owner = owner;
        this.activity = activity;
    }


    public void frame(){
        double deltax = my_owner.destx - x;
        double deltay = my_owner.desty - y;
        double dist = Math.sqrt(Math.pow(deltay,2)+Math.pow(deltax,2));
        double clipped_speed;
        //double travelRatio = GameActivity.ACCEL/dist;
        double xyVelocity = Math.sqrt(xVel*xVel+yVel*yVel);
        //if(xyVelocity<GameActivity.TERMINAL_VELOCITY){
            //Accelerate only if we are moving slower than terminal velocity
            if(dist<GameActivity.DEST_RADIUS*2){
                //Arrived near destination, DECELERATE
                //Using Vf^2 = Vi^2 + 2*a*d
                //Algebra: (Vf^2-Vi^2)/2d = a
                /*double xaccel = (0-(xVel*xVel))/(2*deltax);
                double yaccel = (0-(yVel*yVel))/(2*deltay);
                xVel+=deltax*xaccel/dist;
                yVel+=deltay*yaccel/dist;
                Log.i("Ship", xaccel + " "+ xVel);
            */

                //Closer we get, slower we get, its a proportion
                clipped_speed = GameActivity.TERMINAL_VELOCITY * (dist /(GameActivity.DEST_RADIUS*2));


                }else{
                clipped_speed = GameActivity.TERMINAL_VELOCITY;
                //Not in dest, ACCELERATE as usual
            }
        //}

        double travelRatio = (clipped_speed-xyVelocity)/dist;
        xVel+=deltax*travelRatio;
        yVel+=deltay*travelRatio;
        x += xVel;
        y += yVel;
        Ship target = getTarget();
        if(target != null){
            target.my_owner.remove_ships.add(target);//TODO maybbe just iterate through a clone and remove from real?
            my_owner.remove_ships.add(this);
        }
    }

    public void arrive(){
        double desiredx = my_owner.destx-x;
        double desiredy = my_owner.desty-y;

        double dist = Math.sqrt(Math.pow(desiredx,2)+Math.pow(desiredy,2));//Magnitude of desired

        //m is speed to multiply
        double speed;
        if(dist<GameActivity.DEST_RADIUS){
            //Closer we get, slower we get, its a proportion
            speed = GameActivity.TERMINAL_VELOCITY * (dist /(GameActivity.DEST_RADIUS*2));
        } else{
            speed = GameActivity.TERMINAL_VELOCITY;
        }

        desiredx*=speed;
        desiredy*=speed;

        double steeringx = desiredx-xVel;
        double steeringy = desiredy-yVel;

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
