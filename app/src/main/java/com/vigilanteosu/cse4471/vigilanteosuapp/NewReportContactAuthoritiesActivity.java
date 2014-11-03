package com.vigilanteosu.cse4471.vigilanteosuapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class NewReportContactAuthoritiesActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_report_contact_authorities);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_report_contact_authorities, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void callColumbus(View view){
        //For the dial pad
        String number = "tel:6146454545";
        Intent i = new Intent(Intent.ACTION_DIAL );
        i.setData(Uri.parse(number));

        startActivity(i);
    }

    public void callOSU(View view){
        //For the dial pad
        String number = "tel:6142922121";
        Intent i = new Intent(Intent.ACTION_DIAL );
        i.setData(Uri.parse(number));

        startActivity(i);
    }

    public void contacted(View view){
        Intent intent = new Intent(this,NewReportActivity.class);
        startActivity(intent);
    }

}
