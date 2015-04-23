package com.vigilanteosu.cse4471.vigilanteosuapp;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

/***
 * Author: Al Pal
 * This is the news feed for reports
 */
public class FeedActivity extends ListActivity {
    private final String getReportsUrl = "http://crimenut.maxwellbuck.com/reports/feed";
    private final String numPosts = "20";
    private FeedArrayAdapter faa;
    SessionManagement session;

    GoogleApiClient mGoogleApiClient;
    boolean mRequestingLocationUpdates;
    //LocationServices mLastLocation;
    Location mCurrentLocation;
    /****************************************/

    Context context;
    /****************************************/


    private HashMap<String, String>[] getReports(int page){
        final HashMap<String, String>[] reports;
        final int startPos;

        reports = new HashMap[20];
        startPos = 0;
        SharedPreferences pref =
                getSharedPreferences(SessionManagement.PREF_NAME,
                        MODE_PRIVATE);
        String token = pref.getString(SessionManagement.API_TOKEN, "");
        // If the token is not set
        if(token.equals("")){
            //TODO error
        }

        JSONObject reportObject = new JSONObject();
        try {
            reportObject.put("token", token);
            reportObject.put("lon", "-83.0117700");
            reportObject.put("lat", "40.0089060");
            reportObject.put("page", page);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = getReportsUrl;
        final Context currentContext = this;
        final ListActivity currentActivity = this;
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST,
                        url, reportObject, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //handle response
                        if(response.has("reports")){
                            JSONArray rps = null;
                            try {
                                rps = response.getJSONArray("reports");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            for(int i=0;i<rps.length();i++){
                                setReport(rps, i, startPos,reports);
                            }
                            // This creates the ListView from the reports
                            HashMap<String, String>[] properReport = new HashMap[startPos + rps.length()];
                            System.arraycopy(reports, 0, properReport, 0, startPos + rps.length());
                            if(reports.length == 20) {
                                faa = new FeedArrayAdapter(currentContext, reports);
                                setListAdapter(faa);
                            }else{
                                final FeedArrayAdapter adapter= new FeedArrayAdapter(currentContext, properReport);
                                setListAdapter(adapter);
                                ListView listview = currentActivity.getListView();
                                listview.setSelection(startPos);
                            }
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub

                    }
                });
        requestQueueSingleton.getInstance(this).addToRequestQueue(jsObjRequest);
        return reports;
    }

    private void setReport(JSONArray rps, int i, int startPos,HashMap<String, String>[] reports) {
        HashMap<String, String> report = new HashMap<String, String>();
        JSONObject jsobj = null;
        try {
            jsobj = (JSONObject)rps.get(i);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(jsobj.has("subject")){
            try {
                String subject = jsobj.getString("subject");

                report.put("title", subject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if(jsobj.has("time_began")){
            try {
                String thyme;
                if(jsobj.getString("time_began").equals("None")){
                    if (jsobj.get("time_reported").equals("None")){
                        thyme = "";

                    }else{
                        thyme = jsobj.getString("time_reported");
                    }
                }else{
                    thyme = jsobj.getString("time_began");
                }

                int pos = thyme.lastIndexOf('.');
                if(pos > 0){
                    thyme = thyme.substring(0, pos);
                }

                report.put("time", thyme);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if(jsobj.has("description")){
            try {
                report.put("description", jsobj.getString("description"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if(jsobj.has("house_number")){
            try {
                String housNum = jsobj.getString("house_number");
                String streetPrefix = jsobj.getString("street_prefix");
                String street = jsobj.getString("street");
                String streetSuffix = jsobj.getString("street_suffix");

                if (housNum.equals("None")){housNum = "";}
                if (streetPrefix.equals("None")){streetPrefix = "";}
                if (street.equals("None")){street = "";}
                if (streetSuffix.equals("None")){streetSuffix = "";}

                report.put("location", housNum + " " + streetPrefix + " " + street + " " + streetSuffix);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if(jsobj.has("id")){
            try {
                report.put("reportid", jsobj.getString("id"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if(jsobj.has("lon")){
            try {
                if (jsobj.get("lon").equals("None")){
                    report.put("lon", "lon_reported_from");
                }else{
                    report.put("lon", jsobj.getString("lon"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if(jsobj.has("lat")){
            try {
                if (jsobj.get("lat").equals("None")){
                    report.put("lat", "lat_reported_from");
                }else{
                    report.put("lat", jsobj.getString("lat"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        reports[i+startPos] = report;
    }

    private Button addButton(){
        ListView listview = this.getListView();
        Button btn = new Button(this);
        btn.setText("More Reports");
        btn.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.FILL_PARENT,
                AbsListView.LayoutParams.WRAP_CONTENT));
        listview.addFooterView(btn);
        return btn;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        session = new SessionManagement(getApplicationContext());
        session.checkLogin();

        final HashMap<String, String>[] reports = getReports(1);
        final ListView listview = this.getListView();
        Button btn = addButton();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View arg0) {
                getReports(Integer.parseInt(reports[reports.length - 1].get("page")));
            }
        });

        context = getApplicationContext();
        GcmRegister register = new GcmRegister();
        register.beginToRegister(context);

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
            case android.R.id.home:
                Intent myIntent = new Intent(this, FeedActivity.class);
                startActivity(myIntent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void openCompose(){
        Intent intent = new Intent(this, NewReportStartActivity.class);
        startActivity(intent);
    }

}
