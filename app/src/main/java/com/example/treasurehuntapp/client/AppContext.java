package com.example.treasurehuntapp.client;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.List;
import java.util.Map;

import treasurehunt.model.Account;
import treasurehunt.client.Configuration;
import treasurehunt.model.Course;
import treasurehunt.model.Step;
import treasurehunt.model.StepComposite;

public class AppContext {

    private static AppContext mInstance;
    public static Context mCtx;
    private RequestQueue mRequestQueue;
    public Account account;
    public List<Course> nearestCourse;
    public Map<Integer, StepComposite> stepsMap;
    public int nbStep=0;


    private AppContext(Context context) {
        mCtx = context.getApplicationContext();
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
            mRequestQueue = Volley.newRequestQueue(mCtx);
        }
        return mRequestQueue;
    }

    public void setRESTServerIp(String ip) {
        Configuration.baseUrl = String.format("http://%s/TreasureHuntApiRestServer/api/",ip);
    }
}
