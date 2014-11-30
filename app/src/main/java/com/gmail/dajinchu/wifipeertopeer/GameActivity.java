package com.gmail.dajinchu.wifipeertopeer;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Da-Jin on 11/19/2014.
 */
public abstract class GameActivity extends Activity{

    static final int SHIP_NUM = 1;
    static final double DEST_RADIUS = 50;
    static final double ENGAGEMENT_RANGE = 1;
    static final double ACCEL = .01;//Switch system to per second
    static final double TERMINAL_VELOCITY = .05;
    static final int FRAMERATE = 20;

    String TAG = "GameActivity";

    //UI
    TextView outputText;
    GameView gameView;
    Handler handler = new Handler();

    Socket client;//One end of communication socket. Server has its own ServerSocket
    ObjectOutputStream send;//PrintWriter to send info
    //TODO chang ClientSend and ServerReceive names.

    public final static String START = "SG";
    public final static String RANDOM_SEED = "RS";
    public final static String DATA_FULL = "DATA";
    public final static String CLICKCOORD = "CLICK";
    public final static String PLAYER_NUM = "PNUM";

    Player[] players;
    Player me;

    public Random random;


    ArrayList<Point> tests = new ArrayList<Point>();
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
                    sendDestination(me.playerNumber,(int)event.getX(), (int)event.getY());
                    //TODO get rid off? sendText(String.valueOf((int) event.getX()) + "x" + String.valueOf((int) event.getY()));
                }
                return true;
            }
        });
    }

    public void initWithSeed(long randomSeed){
        players = new Player[2];//TODO TEMP, have send of num of playas
        players[0] = new Player(0);
        players[1] = new Player(1);
        random = new Random(randomSeed);
        int x,y;
        for(Player player : players){
            for(int i=0; i<SHIP_NUM; i++){
                x=random.nextInt(GameView.WIDTH);
                y=random.nextInt(GameView.HEIGHT);
                player.my_ships.add(new Ship(x,y,player, this));
            }
        }
    }

    public void onStartGame(){
        Log.i(TAG, "onStartGame called");
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
    }
    public abstract void sendInitMatchData();
    public abstract void onConnectionComplete();

    //TODO Move stuff to networking, Server/Client
    private class StartSendReceive extends AsyncTask<Void,Void,Void>{
        protected Void doInBackground(Void... Voids) {
            try {
                Log.i(TAG, "Getting streams");
                OutputStream oStream = client.getOutputStream();
                send = new ObjectOutputStream(oStream);
                //sendText("30x40");
                System.out.println(send);

                InputStream istream = client.getInputStream();
                ObjectInputStream read = new ObjectInputStream(istream);
                new Thread(new ServerReceive(read)).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void v) {
            onConnectionComplete();
        }

    }

    public void startSendReceive(){
        new StartSendReceive().execute();
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
    class ClientSend extends AsyncTask<Void,Void,Void> {

        ObjectOutputStream writer;
        Serializable[] objects;
        String tag;

        public ClientSend(ObjectOutputStream writer, String tag, Serializable... objects){
            this.writer = writer;
            this.objects = objects;
            this.tag = tag;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                writer.writeUTF(tag);
                for(Serializable object : objects){
                    writer.writeObject(object);
                }
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }return null;
        }

    }
    class ServerReceive implements Runnable {
        ObjectInputStream readStream;
        TextView data;

        public ServerReceive(ObjectInputStream stream) {
            readStream = stream;
            this.data = data;
        }

        @Override
        public void run() {
            String tag;
            Log.i("Receive","Listening stuff");
            while(true) {
                try {
                    if ((tag = readStream.readUTF()) != null) {
                        Log.i("Receive",tag+" was received");
                        if (tag.equals(DATA_FULL)) {
                            players = (Player[])readStream.readObject();
                        }
                        if (tag.equals(CLICKCOORD)) {
                            int playernum = (Integer)readStream.readObject();
                            players[playernum].destx = (Integer)readStream.readObject();
                            players[playernum].desty = (Integer)readStream.readObject();
                        }
                        if (tag.equals(START)) {
                            onStartGame();
                        }
                        if(tag.equals(RANDOM_SEED)){
                            initWithSeed((Long)readStream.readObject());
                        }
                        if(tag.equals(PLAYER_NUM)){
                            me = players[(Integer)readStream.readObject()];
                        }
                    }
                } catch (IOException e) {
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void sendObject(String tag, Serializable... objects){
        System.out.println(send);
        new ClientSend(send, tag, objects).execute();
    }

    public void sendAllData(Player[] p){
        Log.i(TAG, "Sending all data");
        sendObject(DATA_FULL, p);
    }
    public void sendStartCmd(){
        Log.i(TAG, "Sending start");
        sendObject(START);
    }
    public void sendDestination(int playernum, int destx, int desty){
        Log.i(TAG, "Sending destination");
        sendObject(CLICKCOORD, playernum, destx,desty);
    }
    public void sendSeed(long seed){
        Log.i(TAG, "Sending seed for Random");
        sendObject(RANDOM_SEED, seed);
    }
    public void sendPlayerNum(int num){
        Log.i(TAG, "Sending player number");
        sendObject(PLAYER_NUM, num);
    }
}
