package com.et.lib;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by vp
 * on 03.10.16.
 */
public class CometClient {

    private static CometClient instance;
    private Comet comet;
    private String sendUrl;
    private boolean isRunning = false;
    private BlockingQueue<JSONObject> queue = new ArrayBlockingQueue<JSONObject>(20);
    private Timer timer;

    private CometClient(String receiveUrl, String sendUrl, Comet.CometListener listener) {
        this.sendUrl = sendUrl;
        comet = new Comet(receiveUrl, listener);
    }

    public static CometClient getInstance(String receiveUrl, String sendUrl, Comet.CometListener listener) {
        if (instance == null) instance = new CometClient(receiveUrl, sendUrl, listener);
        if (!instance.comet.isRunning()) instance.comet = new Comet(receiveUrl, listener);
        return instance;
    }

    public void add(JSONObject jo) {
        queue.add(jo);
        if (!isRunning) {
            isRunning = true;
            timer = new Timer();
            timer.schedule(new Sender(), 0, 1000);
        }
    }

    private class Sender extends TimerTask {
        @Override
        public void run() {
            try {
                JSONArray arr = new JSONArray();
                while (queue.size() > 0) {
                    arr.put(queue.take());
                }
                if (arr.length() > 0) {
                    new Http().post(sendUrl, arr.toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        if (isRunning) {
            timer.cancel();
            isRunning = false;
        }
    }
}
