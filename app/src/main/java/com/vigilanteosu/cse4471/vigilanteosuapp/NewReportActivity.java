package com.vigilanteosu.cse4471.vigilanteosuapp;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;


public class NewReportActivity extends Activity {

    /* NEED LAT AND LON,
    *
    * IMPLEMENT SUBJECT CODE
    *
    * PERPS {}
    *
    * OFFENSES {}
    *
    * PROPERTY {}
    *
    * */

    // title, location (address), description, subject code is spinner
    EditText txtReportWhere, txtDescription;
    Spinner sevSpinner;
    DatePicker datePicker;
    TimePicker timePicker;
    // submit button
    Button btnSubmit;

    String[] pickerData = {"Theft", "Property Damage", "Burglary","Assault","Menacing", "Vandalism","Robbery","Other"};
    String[] subjectCodes = {"115",   "551",            "6969",     "254",     "255",     "554",     "450",    "0000"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_report);

        getActionBar().setDisplayHomeAsUpEnabled(true);




        txtReportWhere = (EditText) findViewById(R.id.reportWhereInput);
        txtDescription = (EditText) findViewById(R.id.editText2);
        sevSpinner = (Spinner) findViewById(R.id.reportSpinner);
        timePicker = (TimePicker) findViewById(R.id.timeSpinner);
        datePicker = (DatePicker) findViewById(R.id.dateSpinner);


        //hide year cuz its dumb
        try {
            Field f[] = datePicker.getClass().getDeclaredFields();
            for (Field field : f) {
                if (field.getName().equals("mYearPicker") || field.getName().equals("mYearSpinner")) {
                    field.setAccessible(true);
                    Object yearPicker = new Object();
                    yearPicker = field.get(datePicker);
                    ((View) yearPicker).setVisibility(View.GONE);
                }
            }
        }catch (SecurityException e) {
            Log.d("ERROR", e.getMessage());
        }catch (IllegalArgumentException e) {
            Log.d("ERROR", e.getMessage());
        }catch (IllegalAccessException e) {
            Log.d("ERROR", e.getMessage());
        }




        txtReportWhere.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        txtDescription.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });


        btnSubmit = (Button) findViewById(R.id.btnSubmit);

        final Context currentContext = this;

        btnSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Get report details from EditText, spinner

                btnSubmit.setEnabled(false);


                String reportWhere = txtReportWhere.getText().toString();
                String reportDesc = txtDescription.getText().toString();
                String reportSev = sevSpinner.getSelectedItem().toString();

                //spinner subject -> subject code

                int index = Arrays.asList(pickerData).indexOf(reportSev);
                String subjectCode = subjectCodes[index];

                //format date & time  yyyy-MM-dd hh:mm:ss
                int   day   = datePicker.getDayOfMonth();
                int   month = datePicker.getMonth();
                int   year  = datePicker.getYear();
                int   hrs   = timePicker.getCurrentHour();
                int   min   = timePicker.getCurrentMinute();

                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, day, hrs, min, 0);


                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                String formatedDate = sdf.format(calendar.getTime());
//                Log.v("WMWMWMWMWMW", formatedDate);

                //get lat and lon


                // Check if location and description is filled
                if(reportWhere.trim().length() > 0  && reportDesc.trim().length() > 0){

                    ///lets give this a try
                    SessionManagement session;
                    session = new SessionManagement(getApplicationContext());
                    HashMap<String, String> token = session.getUserToken();
                    String tkn = token.get("apiToken");


                    JSONObject reportObject = new JSONObject();
                    JSONObject emptyJSON = new JSONObject();

                    try {
                        reportObject.put("address_line1", reportWhere);
                        reportObject.put("description", reportDesc);
                        reportObject.put("time_began", formatedDate);
                        reportObject.put("time_ended", formatedDate);
                        reportObject.put("subjectcode", subjectCode);
                        reportObject.put("token", tkn);
                        reportObject.put("lat_reported_from", "-83.0117700");////
                        reportObject.put("lon_reported_from", "40.0089060");////

                        reportObject.put("offenses", emptyJSON);
                        reportObject.put("perpetrators", emptyJSON);
                        reportObject.put("property", emptyJSON);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String reportUrl = "http://crimenut.maxwellbuck.com/reports/new";



                    if(tkn.equals("")){
                        Toast.makeText(getApplicationContext(),
                                "Are you even logged in, bro?",
                                Toast.LENGTH_LONG).show();
                    }else{

                        JsonObjectRequest postObjRequest = new JsonObjectRequest(
                                Request.Method.POST,
                                reportUrl,
                                reportObject,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        // handle response
                                        if (response.has("id")) {
                                            try {
                                                String result = response.getString("result");
                                                if (result.equals("true")) {
                                                    Toast.makeText(getApplicationContext(),
                                                            "Your report has been posted! Thanks for being a Crime Nut",
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
                                            try{
                                                String result = response.getString("ERROR");
                                                Toast.makeText(getApplicationContext(),
                                                        "Failed to post your report... " + result,
                                                        Toast.LENGTH_LONG).show();
                                            }catch (JSONException e){
                                                Toast.makeText(getApplicationContext(),
                                                        "Failed to post your report...",
                                                        Toast.LENGTH_LONG).show();
                                            }
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
                btnSubmit.setEnabled(true);
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

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


}
