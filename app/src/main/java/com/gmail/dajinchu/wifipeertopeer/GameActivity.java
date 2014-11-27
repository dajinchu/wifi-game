package com.gmail.dajinchu.wifipeertopeer;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Da-Jin on 11/19/2014.
 */
public abstract class GameActivity extends Activity{

    static final int SHIP_NUM = 20;
    static final double ACCELERATION = 1;//Switch system to per second
    static final double TERMINAL_VELOCITY = 1;
    static final int FRAMERATE = 20;

    String TAG = "GameActivity";

    //UI
    TextView outputText;
    GameView gameView;
    Handler handler = new Handler();

    Socket client;//One end of communication socket. Server has its own ServerSocket
    PrintWriter send;//PrintWriter to send info
    //TODO chang ClientSend and ServerReceive names.

    public final static String START = "SG";
    public final static String DATA_FULL = "DATA";


    Player enemy, me;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);

        outputText = (TextView)findViewById(R.id.output);

        gameView = (GameView)findViewById(R.id.game);
        gameView.setBackgroundColor(Color.WHITE);
        gameView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    moveDot((int)event.getX(),(int)event.getY());
                    sendText(String.valueOf((int) event.getX()) + "x" + String.valueOf((int) event.getY()));
                }
                return true;
            }
        });
    }

    public abstract void sendInitMatchData();

    public void parseFullData(String message){
        Log.i(TAG, "parsing full data");
        String[] shipcoords = message.split(" ");//Coords of ships
        String[] coords = new String[2];

        for(String str : shipcoords){
            coords = str.split(",");
            gameView.ships.add(new Ship(Integer.parseInt(coords[0]),Integer.parseInt(coords[1]),me));
        }
    }
    public void parseMessage(String message){
        displayText(message);
        if(message.equals(START)){//TODO just make a start function that CAN be overridden by server
            sendInitMatchData();//For client, nothing, for Server, send match data
            Thread t = new Thread() {

                @Override
                public void run() {
                    try {
                        while (!isInterrupted()) {
                            Thread.sleep(FRAMERATE);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    gameView.frame();
                                }
                            });
                        }
                    } catch (InterruptedException e) {
                    }
                }
            };

            t.start();
            return;
        }
        String[] coords = message.split("x");
        moveDot(Integer.parseInt(coords[0]),Integer.parseInt(coords[1]));

    }

    public void startSendReceive(){
        try {
            Log.i(TAG, "Getting streams");
            OutputStream oStream = client.getOutputStream();
            send = new PrintWriter(oStream, true);
            //sendText("30x40");

            InputStream istream = client.getInputStream();
            BufferedReader read = new BufferedReader(new InputStreamReader(istream));
            new Thread(new ServerReceive(read)).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void displayText(final String text){
        handler.post(new Runnable(){
            @Override
            public void run() {
                outputText.setText(text);
            }
        });
    }
    public void moveDot(final int x, final int y){
        handler.post(new Runnable(){//TODO remove this? if editing destx thats not interacting with UI right?
            @Override
            public void run() {
                me.destx = x;
                me.desty = y;

                //gameView.invalidate();
            }
        });
    }
    class ClientSend extends AsyncTask<String,Void,Void> {

        PrintWriter writer;

        public ClientSend(PrintWriter writer){
            this.writer = writer;
        }


        @Override
        protected Void doInBackground(String... write) {
            Log.i("CLient","Indeed, CLientSend thread has noticed and is now sending "+write[0]);
            writer.println(MainActivity.START_MSG);
            writer.println(write[0]);//TODO replace with asyncTask! this while loop will go round and eat CPU, also there is already handler read in GameActivity
            writer.println(write[1]);
            return null;
        }

    }
    class ServerReceive implements Runnable {
        BufferedReader readStream;
        TextView data;

        public ServerReceive(BufferedReader stream) {
            readStream = stream;
            this.data = data;
        }

        @Override
        public void run() {
            StringBuilder sb = new StringBuilder();
            String line;
            while(true) {
                try {
                    if ((line = readStream.readLine()) != null) {
                        Log.i(TAG, line+" was received");
                        if (line.equals(MainActivity.START_MSG)) {
                            Log.i(TAG, "StartMSG tag FOUND");
                            sb = new StringBuilder();
                        } else if(line.equals(GameActivity.DATA_FULL)){
                            Log.i(TAG, sb.toString()+" going to parseFullData");
                            parseFullData(sb.toString());
                            sb.delete(0,sb.length());
                        } else if (line.equals(MainActivity.END_MSG)) {
                            parseMessage(sb.toString());
                            sb.delete(0, sb.length());//TODO is this neccesary? idk
                        } else {
                            sb.append(line);
                        }
                    }
                } catch (IOException e) {
                }
            }
        }
    }

    public void sendText(String text){
        sendText(text, MainActivity.END_MSG);
    }
    public void sendText(String text, String TAG) {

        Log.i("CLient", "telling Async to write");
        new ClientSend(send).execute(text, TAG);
    }
}
