package com.example.treasurehuntapp.client;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import treasurehunt.model.Account;
import treasurehunt.client.Configuration;

public class AppContext {

    private static AppContext mInstance;
    private static Context mCtx;
    private RequestQueue mRequestQueue;
    public Account account;

    private AppContext(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();
        account = null;
    }

    public static synchronized AppContext getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new AppContext(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public void setRESTServerIp(String ip) {
        Configuration.baseUrl = String.format("http://%s/TreasureHuntApiRestServer/api/",ip);
    }
}
