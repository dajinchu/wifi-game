package com.gmail.dajinchu.wifipeertopeer;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by Da-Jin on 11/19/2014.
 */
public class Client extends GameActivity{

    String TAG = "Client";

    public static String HOST_INTENT = "42" ,PORT_INTENT = "666";

    private static final int SOCKET_TIMEOUT = 5000;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Log.i(TAG, "Client started");
        Intent intent = getIntent();
        int port = intent.getIntExtra(PORT_INTENT, 8988);
        String host = intent.getStringExtra(HOST_INTENT);
        new ASyncConnect(host, port).execute();


    }

    @Override
    public void sendInitMatchData() {
        Log.i("CLient", "Ready to start(sendInitMatchData has been called from sendStartGame");
    }

    @Override
    public void onConnectionComplete() {
        sendStartCmd();
        onStartGame();
    }

    class ASyncConnect extends AsyncTask<Void,Void,Void> {

        String host;
        int port;

        public ASyncConnect(String host, int port) {
            this.host = host;
            this.port = port;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                client = new Socket();
                Log.i(TAG, "Socket connection began");
                client.bind(null);
                client.connect(new InetSocketAddress(host, port), SOCKET_TIMEOUT);
                Log.i(TAG, "Socket connection finished");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            startSendReceive();
        }
    }

}
