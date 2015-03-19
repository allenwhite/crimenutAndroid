package com.vigilanteosu.cse4471.vigilanteosuapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

/**
 * Created by Allen on 1/25/15.
 */
public class BootReceiver extends WakefulBroadcastReceiver {
    static final String TAG = "GCMDemo";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";


    @Override
    public void onReceive(Context context, Intent intent) {
        GcmRegister register = new GcmRegister();

        final SharedPreferences prefs = register.getGCMPreferences(context);
        int appVersion = register.getAppVersion(context);
        Log.i(TAG, "clearing regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, "");
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();

        register.beginToRegister(context);
    }
}

