package com.vigilanteosu.cse4471.vigilanteosuapp;

import android.content.Context;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Allen on 11/2/14.
 */
public class requestQueueSingleton {
    private static requestQueueSingleton mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;

    private requestQueueSingleton(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();


    }

    public static synchronized requestQueueSingleton getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new requestQueueSingleton(context);
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

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }


}
