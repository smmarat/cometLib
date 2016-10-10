package com.et.lib;

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
    private BlockingQueue<Req> queue = new ArrayBlockingQueue<Req>(20);
    private Timer timer;

    private CometClient(String sendUrl) {
        this.sendUrl = sendUrl;
    }

    public void connect(String receiveUrl, Comet.CometListener listener) {
        comet = new Comet(receiveUrl, listener);
    }

    public static CometClient getInstance(String sendUrl) {
        if (instance == null) instance = new CometClient(sendUrl);
        return instance;
    }

    public void add(Req r) {
        queue.add(r);
        if (!isRunning) {
            isRunning = true;
            timer = new Timer();
            timer.schedule(new Sender(), 0, 1000);
        }
    }

    private class Sender extends TimerTask {
        @Override
        public void run() {
            while (queue.size() > 0) {
                Req r = queue.poll();
                try {
                    String s = new Http().post(sendUrl, r.getData().toString());
                    JSONObject jo = new JSONObject(s);
                    r.getListener().onResponce(jo);
                } catch (Exception e) {
                    r.getListener().onError(e);
                }
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
