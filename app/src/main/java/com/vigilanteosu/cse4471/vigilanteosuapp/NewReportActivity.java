package com.vigilanteosu.cse4471.vigilanteosuapp;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class NewReportActivity extends Activity {

    // Email, password edittext
    EditText txtReportTitle, txtReportWhere, txtDescription;

    Spinner sevSpinner;

    // submit button
    Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_report);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        txtReportTitle = (EditText) findViewById(R.id.reportTitleInput);
        txtReportWhere = (EditText) findViewById(R.id.reportWhereInput);
        txtDescription = (EditText) findViewById(R.id.editText2);
        sevSpinner = (Spinner) findViewById(R.id.reportSpinner);

        btnSubmit = (Button) findViewById(R.id.btnSubmit);

        final Context currentContext = this;

        btnSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Get report details from EditText, spinner

                String reportTitle = txtReportTitle.getText().toString();
                String reportWhere = txtReportWhere.getText().toString();

                String reportDesc = txtDescription.getText().toString();

                String reportSev = sevSpinner.getSelectedItem().toString();
                int severity = Integer.parseInt(reportSev) - 1;
                // Check if username, password is filled
                if(reportTitle.trim().length() > 0 && reportWhere.trim().length() > 0  && reportDesc.trim().length() > 0){
                    JSONObject reportObject = new JSONObject();
                    try {
                        reportObject.put("title", reportTitle);

                        reportObject.put("severity", severity);
                        reportObject.put("location", reportWhere);
                        reportObject.put("description", reportDesc);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String reportUrl = "http://jeffcasavant.com:10100/vig/api/v1.0/reports?token=";

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

                        reportUrl = reportUrl.concat(tkn);

                        JsonObjectRequest postObjRequest = new JsonObjectRequest(
                                Request.Method.POST,
                                reportUrl,
                                reportObject,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        // handle response
                                        if (response.has("result")) {
                                            try {
                                                String result = response.getString("result");
                                                if (result.equals("true")) {
                                                    Toast.makeText(getApplicationContext(),
                                                            "Your report has been posted! Thanks for being a vigilante",
                                                            Toast.LENGTH_LONG).show();
                                                    Intent i = new Intent(getApplicationContext(), FeedActivity.class);
                                                    startActivity(i);
                                                    finish();
                                                } else {
                                                    Toast.makeText(getApplicationContext(),
                                                            "Something went amiss posting your report... please try again",
                                                            Toast.LENGTH_LONG).show();
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        } else {
                                            Toast.makeText(getApplicationContext(),
                                                    "Failed to post your report... please try again",
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
