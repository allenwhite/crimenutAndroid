package com.vigilanteosu.cse4471.vigilanteosuapp;

import android.app.ListActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.HashMap;


public class FeedActivity extends ListActivity {
    private final String getReportsUrl = "http://jeffcasavant.com:10100/vig/api/v1.0/list/reports?";
    private final String numPosts = "15";
    SessionManagement session;
    /* WE NEED BUTTONS TO GET TO THE OTHER SCREEN... ALSO MIGHT BE OF VALUE TO CHECK THAT
    THE TOKEN IS VALID (METHOD DNE RN THO)*/

    private HashMap<String, String>[] getReports(String reportUUID){
        HashMap<String, String>[] reports = new HashMap[15];
        SharedPreferences pref =
                getSharedPreferences(SessionManagement.PREF_NAME,
                        MODE_PRIVATE);
        String token = pref.getString("token", "");
        // If the token is not set
        if(token.equals("")){
            //TODO error
        }
        String url = createUrl(token, reportUUID, numPosts);
        return reports;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Feed", "FeedActivity.onCreate()");

        setContentView(R.layout.activity_feed);

        session = new SessionManagement(getApplicationContext());

        session.checkLogin();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.feed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }


}
