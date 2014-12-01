package com.vigilanteosu.cse4471.vigilanteosuapp;

import android.app.ListActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class FeedActivity extends ListActivity {
    private final String getReportsUrl = "http://jeffcasavant.com:10100/vig/api/v1.0/list/reports";
    private final String numPosts = "15";
    private final String startReportUUID = "00000000-0000-0000-0000-000000000000";
    SessionManagement session;
    /* WE NEED BUTTONS TO GET TO THE OTHER SCREEN... ALSO MIGHT BE OF VALUE TO CHECK THAT
    THE TOKEN IS VALID (METHOD DNE RN THO)*/

    private String createUrl(String token, String reportUUID, String numPosts){
        return getReportsUrl + String.format("?token=%s&reportid=%s&number_of_posts=%s", token, reportUUID, numPosts);
    }

    private HashMap<String, String>[] getReports(String reportUUID){
        final HashMap<String, String>[] reports = new HashMap[15];
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
//                                if(jsobj.has("description")){
//                                    try {
//                                        report.put("description", jsobj.getString("description"));
//                                    } catch (JSONException e) {
//                                        e.printStackTrace();
//                                    }
//                                }
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
                                reports[i] = report;
                            }
                            FeedArrayAdapter faa = new FeedArrayAdapter(currentContext, reports);
                            setListAdapter(faa);
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Feed", "FeedActivity.onCreate()");

        setContentView(R.layout.activity_feed);

        session = new SessionManagement(getApplicationContext());
        session.checkLogin();

        HashMap<String, String>[] reports = getReports(startReportUUID);

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
