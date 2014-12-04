package com.vigilanteosu.cse4471.vigilanteosuapp;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;

import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class ViewIncidentActivity extends FragmentActivity {

    GoogleMap googleMap;
    private ReplyArrayAdapter raa;

    private String reportid;

    public void postReply(final Intent intent){

        EditText comment = (EditText) findViewById(R.id.replyBox);
        String reply = comment.getText().toString();
        final Context currentContext = this;

        // Check if username, password is filled
        if(reply.trim().length() > 0){
            JSONObject replyObject = new JSONObject();
            try {
                replyObject.put("body", reply);
                replyObject.put("reportid", reportid);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String replyUrl = "http://jeffcasavant.com:10100/vig/api/v1.0/reports/reply?token=";

            ///lets give this a try
            SessionManagement session;
            session = new SessionManagement(getApplicationContext());

            HashMap<String, String> token = session.getUserToken();

            String tkn = token.get("apiToken");

            if(tkn.equals("")){
                Toast.makeText(getApplicationContext(),
                        "Are you even logged in, bro?",
                        Toast.LENGTH_LONG).show();
            }else {

                replyUrl = replyUrl.concat(tkn);

                JsonObjectRequest postObjRequest = new JsonObjectRequest(
                        Request.Method.POST,
                        replyUrl,
                        replyObject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                // handle response
                                if (response.has("result")) {
                                    try {
                                        String result = response.getString("result");
                                        if (result.equals("true")) {
                                            Toast.makeText(getApplicationContext(),
                                                    "Your comment has been posted! Thanks for being a vigilante",
                                                    Toast.LENGTH_LONG).show();
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Toast.makeText(getApplicationContext(),
                                                    "Something went amiss posting your comment... please try again",
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(),
                                            "Failed to post your comment... please try again",
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Something went wrong, please try again in five minutes",
                                Toast.LENGTH_LONG).show();
                    }
                });
                requestQueueSingleton.getInstance(currentContext).addToRequestQueue(postObjRequest);
            }
        }else{
            // user didn't entered username or password
            // Show alert asking him to enter the details
            //alert.showAlertDialog(LoginActivity.this, "Login failed..", "Please enter username and password", false);
            Toast.makeText(getApplicationContext(),
                    "Post failed...\nPlease fill out all report details",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void createMapView(double lon, double lat, String loc){
        /**
         * Catch the null pointer exception that
         * may be thrown when initialising the map
         */
        try {
            if(null == googleMap){
                googleMap = ((MapFragment) getFragmentManager().findFragmentById(
                        R.id.mapView)).getMap();

                /**
                 * If the map is still null after attempted initialisation,
                 * show an error to the user
                 */
                if(null == googleMap) {
                    Toast.makeText(getApplicationContext(),"Error creating map", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (NullPointerException exception){
            Toast.makeText(getApplicationContext(),"somthing failed wit yo map", Toast.LENGTH_LONG).show();
        }

        if(null != googleMap){
            googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(lat, lon))
                            .title(loc)
                            .draggable(true)
            );

            CameraUpdate center=
                    CameraUpdateFactory.newLatLng(new LatLng(lat,lon));
            CameraUpdate zoom=CameraUpdateFactory.zoomTo(15);

            googleMap.moveCamera(center);
            googleMap.animateCamera(zoom);
        }
    }


    private void setReplies(JSONArray rps, int i, HashMap<String, String>[] reports) {
        HashMap<String, String> report = new HashMap<String, String>();
        JSONObject jsobj = null;
        try {
            jsobj = (JSONObject)rps.get(i);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(jsobj.has("body")){
            try {
                report.put("body", jsobj.getString("body"));
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
        reports[i] = report;
    }

    private HashMap<String, String>[] getReplies(String reportid){
        final HashMap<String, String>[] replies;
        replies = new HashMap[200];

        SessionManagement session;
        session = new SessionManagement(getApplicationContext());

        HashMap<String, String> token = session.getUserToken();

        String tkn = token.get("apiToken");
        // If the token is not set
        if(tkn.equals("")){
            //TODO error
        }
        String url = "http://jeffcasavant.com:10100/vig/api/v1.0/list/replies?token="+ tkn + "&reportid=" + reportid;

        final Context currentContext = this;
        final Activity currentActivity = this;

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //handle response
                        if(response.has("replies")){
                            JSONArray rps = null;
                            try {
                                rps = response.getJSONArray("replies");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            for(int i=0;i<rps.length();i++){
                                setReplies(rps, i, replies);
                            }
                            // This creates the ListView from the reports
                            HashMap<String, String>[] properReply = new HashMap[rps.length()];
                            System.arraycopy(replies, 0, properReply, 0, rps.length());

                                final ReplyArrayAdapter adapter= new ReplyArrayAdapter(currentContext, properReply);
                                ListView listview = (ListView)currentActivity.findViewById(R.id.list);
                                listview.setAdapter(adapter);
                                setListViewHeightBasedOnChildren(listview);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub

                    }
                });
        requestQueueSingleton.getInstance(this).addToRequestQueue(jsObjRequest);
        return replies;
    }



    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        View listItem = listAdapter.getView(listAdapter.getCount()-1, null, listView);
        listItem.measure(0, 0);
        totalHeight += listItem.getMeasuredHeight();
        totalHeight += listItem.getMeasuredHeight();

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount()));//-1?
        listView.setLayoutParams(params);
        listView.requestLayout();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_incident);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        String lats = getIntent().getExtras().getString("lat");
        String lons = getIntent().getExtras().getString("lon");

        reportid = getIntent().getExtras().getString("reportid");

        if(!lats.equals("nah") && !lons.equals("nah")){
            double lat = Double.parseDouble(lats);
            double lon = Double.parseDouble(lons);
            createMapView(lon,lat,getIntent().getExtras().getString("reportLocation"));
        }else{
            MapFragment mapFrag = ((MapFragment) getFragmentManager().findFragmentById(R.id.mapView));

            mapFrag.getView().setVisibility(View.INVISIBLE);
            ViewGroup.LayoutParams params = mapFrag.getView().getLayoutParams();
            params.height = 0;
            mapFrag.getView().setLayoutParams(params);
        }

        TextView title = (TextView)findViewById(R.id.report_title);
        TextView loc = (TextView)findViewById(R.id.report_location);
        TextView time = (TextView)findViewById(R.id.report_time);
        TextView desc = (TextView)findViewById(R.id.report_desc);

        int severity = getIntent().getExtras().getInt("severity");
        title.setText(getIntent().getExtras().getString("reportTitle"));
        loc.setText(getIntent().getExtras().getString("reportLocation"));
        time.setText(getIntent().getExtras().getString("reportTime"));
        desc.setText(getIntent().getExtras().getString("reportDesc"));

        ImageView severityIcon = (ImageView)findViewById(R.id.severity_icon);

        switch(severity){
            case 0:
                severityIcon.setImageResource(R.drawable.zeroseverity);
                break;
            case 1:
                severityIcon.setImageResource(R.drawable.oneseverity);
                break;
            case 2:
                severityIcon.setImageResource(R.drawable.twoseverity);
                break;
            case 3:
                severityIcon.setImageResource(R.drawable.threeseverity);
                break;
            case 4:
                severityIcon.setImageResource(R.drawable.fourseverity);
                break;
            default:
                severityIcon.setImageResource(R.drawable.zeroseverity);
                break;
        }

        //load replies? remember to call this method ya doof
        getReplies(reportid);

        Button btnSubmit = (Button)findViewById(R.id.btnPostReply);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                postReply(getIntent());

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.feed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_compose:
                openCompose();
                return true;
            case android.R.id.home:
                Intent myIntent = new Intent(getApplicationContext(), FeedActivity.class);
                startActivityForResult(myIntent, 0);
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
