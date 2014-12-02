package com.vigilanteosu.cse4471.vigilanteosuapp;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/***
 * Author: Neil Madsen
 * This is the news feed for reports
 */
public class FeedActivity extends ListActivity {
    private final String getReportsUrl = "http://jeffcasavant.com:10100/vig/api/v1.0/list/reports";
    private final String numPosts = "15";
    private final String startReportUUID = "00000000-0000-0000-0000-000000000000";
    private FeedArrayAdapter faa;
    SessionManagement session;
    /* WE NEED BUTTONS TO GET TO THE OTHER SCREEN... ALSO MIGHT BE OF VALUE TO CHECK THAT
    THE TOKEN IS VALID (METHOD DNE RN THO)*/

    private String createUrl(String token, String reportUUID, String numPosts){
        return getReportsUrl + String.format("?token=%s&reportid=%s&number_of_posts=%s", token, reportUUID, numPosts);
    }

    private HashMap<String, String>[] getReports(String reportUUID, HashMap<String,String>[] currentReports){
        final HashMap<String, String>[] reports;
        final int startPos;
        if(currentReports != null) {
            reports = new HashMap[currentReports.length + 15];
            System.arraycopy(currentReports, 0, reports, 0, currentReports.length);
            startPos = currentReports.length;
        }
        else {
            reports = new HashMap[15];
            startPos = 0;
        }
        SharedPreferences pref =
                getSharedPreferences(SessionManagement.PREF_NAME,
                        MODE_PRIVATE);
        String token = pref.getString("apiToken", "");
        // If the token is not set
        if(token.equals("")){
            //TODO error
        }
        String url = createUrl(token, reportUUID, numPosts);
        final Context currentContext = this;
        final ListActivity currentActivity = this;
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
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
                            if(reports.length == 15) {
                                faa = new FeedArrayAdapter(currentContext, reports);
                                setListAdapter(faa);
                            }
                            else{
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
        if(jsobj.has("title")){
            try {
                report.put("title", jsobj.getString("title"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if(jsobj.has("time")){
            try {
                report.put("time", jsobj.getString("time"));
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
        if(jsobj.has("severity")){
            try {
                report.put("severity", jsobj.getString("severity"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if(jsobj.has("location")){
            try {
                report.put("location", jsobj.getString("location"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if(jsobj.has("reportid")){
            try {
                report.put("reportid", jsobj.getString("reportid"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        reports[i+startPos] = report;
    }

    /**
     * Author: Neil Madsen
     * This adds a button at the bottom of the newsFeed
     * that allows the user to load more reports
     */
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
        Log.d("Feed", "FeedActivity.onCreate()");

        setContentView(R.layout.activity_feed);

        session = new SessionManagement(getApplicationContext());
        session.checkLogin();
        final HashMap<String, String>[] reports = getReports(startReportUUID, null);
        final ListView listview = this.getListView();
        Button btn = addButton();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View arg0) {
                getReports(reports[reports.length - 1].get("reportid"), reports);
            }
        });
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
