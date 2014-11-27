package com.gmail.dajinchu.wifipeertopeer;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Random;

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
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        int x,y;
        for(int i=0; i<SHIP_NUM; i++){
            x=random.nextInt(GameView.WIDTH);
            y=random.nextInt(GameView.HEIGHT);
            gameView.ships.add(new Ship(x,y, me));
            sb.append(x);
            sb.append(",");
            sb.append(y);
            sb.append(" ");
        }
        sb.setLength(sb.length() - 1);
        Log.d(TAG, sb.toString());
        sendText(sb.toString(), GameActivity.DATA_FULL);
    }

    class ServerSend{

    }
}
