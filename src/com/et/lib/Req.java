package com.et.lib;

import org.json.JSONObject;

/**
 * Created by vp
 * on 10.10.16.
 */
public class Req {

    private JSONObject data;
    private SyncListener listener;

    public Req(JSONObject data, SyncListener listener) {
        this.data = data;
        this.listener = listener;
    }

    public JSONObject getData() {
        return data;
    }

    public SyncListener getListener() {
        return listener;
    }

    public interface SyncListener {
        void onResponce(JSONObject data);
        void onError(Exception e);
    }
}
