package com.vigilanteosu.cse4471.vigilanteosuapp;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

/**
 * Created by Allen on 1/25/15.
 */
public class GcmIntentService extends IntentService {

    private NotificationManager mNotificationManager;

    static final String TAG = "GCMDemo";

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "There are things happening");
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                extras.putBoolean("err",true);
                sendNotification(extras);
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
                extras.putBoolean("del", true);
                sendNotification(extras);
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // This loop represents the service doing some work.
                for (int i=0; i<5; i++) {
                    Log.i(TAG, "Working... " + (i+1)
                            + "/5 @ " + SystemClock.elapsedRealtime());
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                    }
                }
                Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
                // Post notification of received message.
                sendNotification(extras);
                Log.i(TAG, "Received: " + extras.toString());
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(Bundle msg) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        if(msg.containsKey("del") || msg.containsKey("err")){
            //idk what i would do but if I did this is where it would happen
        }else {

            String title;
            String ticker;
            String text;
            if(msg.containsKey("title")){
                title = msg.getString("title");
            }else{
                title = "Vigilante";
            }
            if(msg.containsKey("tickerText")){
                ticker = msg.getString("tickerText");
            }else{
                ticker = "Your neighbor has reported an incident!";
            }
            if(msg.containsKey("message")){
                text = msg.getString("message");
            }else{
                text = "Your neighbor has reported an incident!";
            }
//check if its just a post
            PendingIntent contentIntent;
            if(text.contains("replied")){
                contentIntent = PendingIntent.getActivity(this, 0,
                        new Intent(this, ViewIncidentActivity.class), 0);
                //////NEED SOME WAY TO SEND IT TO THE EXACT EVENT!!!!!!!!
            }else{
                contentIntent = PendingIntent.getActivity(this, 0,
                        new Intent(this, FeedActivity.class), 0);
            }

            Notification notification =
                    new NotificationCompat.Builder(this)
                            //.setSmallIcon(R.drawable.ic_stat_gcm)
                            .setContentTitle(title)
                            .setContentText(text)
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                            .setAutoCancel(true)
                            .setVibrate(new long[]{200, 200, 200, 200, 200, 200})
                            .setContentIntent(contentIntent)
                            .setTicker(ticker).build();


            mNotificationManager.notify(1, notification);
        }
    }
}

