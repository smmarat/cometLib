package com.et.lib;

import org.json.JSONObject;

import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: vp
 * Date: 15.04.15
 * Time: 14:36
 */
public class Comet extends Thread {

    private CometListener listener;
    public static int MAX_RETRY = 3;
    private String id;
    private String url;
    private boolean running;
    private boolean isLog = false;

    public Comet(String url, boolean isLog, CometListener listener) {
        this.listener = listener;
        this.url = url;
        this.isLog = isLog;
        this.id = UUID.randomUUID().toString().replace("-", "");
        this.start();
    }

    @Override
    public void run() {
        this.listener.onConnected(id);
        int r = 0;
        running = true;
        while (running && r <= MAX_RETRY) {
            try {
                String id = UUID.randomUUID().toString().replace("-", "");
                if (isLog) System.out.println("=---> [" + id + "] " + url);
                String s = new Http().get(url, true);
                if (isLog) System.out.println("<---= [" + id + "] " + s);
                JSONObject jo = new JSONObject(s);
                listener.onData(jo);
                r = 0;
                sleep(1000);
            } catch (Exception e) {
                listener.onError(e);
                r++;
            }
        }
        running = false;
        this.listener.onDisconnected(id);
    }

    public void disconnect() {
        running = false;
        interrupt();
    }

    public boolean isRunning() {
        return running;
    }

    public interface CometListener {
        void onConnected(String id);
        void onDisconnected(String id);
        void onData(JSONObject data);
        void onError(Exception e);
    }
}
