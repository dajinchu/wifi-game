package com.gmail.dajinchu.wifipeertopeer;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Created by Da-Jin on 11/19/2014.
 */
public class Server extends GameActivity{
    String TAG = "Server";
    ServerSocket serverSocket;


    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        Log.i(TAG, "onCreate was called on Server");
        new ASyncConnect().execute();
    }

    class ASyncConnect extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                if(serverSocket==null){
                    serverSocket = new ServerSocket(8988);
                    Log.i(TAG, "Server socket opened");
                }
                client = serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;//TODO make this like client
        }

        @Override
        protected void onPostExecute(Void v) {
            startSendReceive();
        }
    }

    public void sendInitMatchData(){
        long seed = System.currentTimeMillis();
        sendSeed(seed);
        initWithSeed(seed);
        me = players[0];//Server is p0
        sendPlayerNum(1);//Client is p1
        //Log.i(TAG, me.my_ships.size()+ "ships");
        //sendAllData(me,enemy);
    }

    @Override
    public void onConnectionComplete() {
        sendInitMatchData();
        sendStartCmd();
        onStartGame();
    }

    class ServerSend{

    }
}
