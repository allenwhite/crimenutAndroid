package com.vigilanteosu.cse4471.vigilanteosuapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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


public class NewReportActivity extends Activity {

    // Email, password edittext
    EditText txtReportTitle, txtReportWhere, txtReportWhen, txtDescription;

    //crime type
    Spinner crimeTypes;

    // submit button
    Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_report);


        txtReportTitle = (EditText) findViewById(R.id.reportTitleInput);
        txtReportWhere = (EditText) findViewById(R.id.reportWhereInput);
        txtReportWhen = (EditText) findViewById(R.id.reportWhenInput);
        txtDescription = (EditText) findViewById(R.id.editText2);

        btnSubmit = (Button) findViewById(R.id.btnSubmit);

        crimeTypes = (Spinner) findViewById(R.id.reportTypeOptions);

        final Context currentContext = this;

        btnSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Get report details from EditText, spinner
                SharedPreferences apiPrefs = getSharedPreferences(SessionManagement.API_TOKEN, Context.MODE_PRIVATE);
                String channel = (apiPrefs.getString(keyChannel, ""));

                String reportTitle = txtReportTitle.getText().toString();
                String reportWhere = txtReportWhere.getText().toString();
                String reportWhen = txtReportWhen.getText().toString();//??????never used???????????
                String reportDesc = txtDescription.getText().toString();
                String crimeType = crimeTypes.getSelectedItem().toString();

                // Check if username, password is filled
                if(reportTitle.trim().length() > 0 && reportWhere.trim().length() > 0 && reportWhen.trim().length() > 0 && reportDesc.trim().length() > 0 && crimeType.trim().length() > 0){
                    JSONObject reportObject = new JSONObject();
                    try {
                        reportObject.put("userid", reportTitle);//grab from user prefsss????????????
                        reportObject.put("title", reportTitle);
                        reportObject.put("severity", 1);//where does severity come from?????????????
                        reportObject.put("location", reportWhere);///drop pin???????????????????????
                        reportObject.put("description", reportDesc);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String reportUrl = "http://jeffcasavant.com:10100/vig/api/v1.0/reports";
                    JsonObjectRequest postObjRequest = new JsonObjectRequest(
                            Request.Method.POST,
                            reportUrl,
                            reportObject,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    // handle response
//                                    SharedPreferences.Editor prefEdit =
//                                            getSharedPreferences(SessionManagement.PREF_NAME,
//                                                    MODE_PRIVATE).?edit();
                                    //Log.d("LoginActivity: response", response.toString());
//                                    JSONObject responseReport = null;
//                                    try {
//                                        responseReport = response.getJSONObject("user");
//                                    } catch (JSONException e) {
//                                        e.printStackTrace();
//                                    }
                                    if (response.has("result")) {
                                        try {
                                             String result = response.getString("result");
                                            if(result.equals("true")){
                                                Toast.makeText(getApplicationContext(),
                                                        "Your report has been posted! Thanks for being a vigilante",
                                                        Toast.LENGTH_LONG).show();
                                                Intent i = new Intent(getApplicationContext(), FeedActivity.class);
                                                startActivity(i);
                                                finish();
                                            }else{
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
                            Toast.makeText(getApplicationContext(),"I don't know what is happening.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                    );

                    requestQueueSingleton.getInstance(currentContext).addToRequestQueue(postObjRequest);
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
        getMenuInflater().inflate(R.menu.new_report, menu);
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
}
