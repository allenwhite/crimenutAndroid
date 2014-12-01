package com.vigilanteosu.cse4471.vigilanteosuapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;


public class FeedActivity extends Activity {

    SessionManagement session;
/* WE NEED BUTTONS TO GET TO THE OTHER SCREEN... ALSO MIGHT BE OF VALUE TO CHECK THAT
THE TOKEN IS VALID (METHOD DNE RN THO)*/

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
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.feed, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_compose:
                openCompose();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void openCompose(){
        Intent intent = new Intent(this, NewReportStartActivity.class);
        startActivity(intent);
    }



}
