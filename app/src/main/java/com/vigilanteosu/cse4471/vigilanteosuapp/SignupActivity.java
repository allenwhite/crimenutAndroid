package com.vigilanteosu.cse4471.vigilanteosuapp;

import android.app.Activity;
import android.app.DownloadManager;
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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;


public class SignupActivity extends Activity {


    // Email, password edittext
    EditText txtUsername, txtPassword;

    // Signup button
    Button btnBecomeVigilante;

    // Already Registered button
    Button btnRegistered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        // Session Manager
        // session = new SessionManagement(getApplicationContext());

        // Email, Password input text
        txtUsername = (EditText) findViewById(R.id.txtUsername);
        txtPassword = (EditText) findViewById(R.id.txtPassword);

        // Signup button
        btnBecomeVigilante = (Button) findViewById(R.id.btnBecomeVigilante);

        //RequestQueue queue = Volley.newRequestQueue(this);

        final Context currentContext = this;


        // Signup button click event
        btnBecomeVigilante.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                // Get username, password from EditText
                String username = txtUsername.getText().toString();
                String password = txtPassword.getText().toString();
                // Check if username, password is filled
                if (username.trim().length() > 0 && password.trim().length() > 0) {
                    JSONObject userObject = new JSONObject();
                    try {
                        userObject.put("username", username);
                        userObject.put("password", password);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String signupUrl = "http://jeffcasavant.com:10100/vig/api/v1.0/users";
                    JsonObjectRequest signupObjRequest = new JsonObjectRequest(
                                    Request.Method.POST,
                                    signupUrl,
                                    userObject,
                                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // handle response
                            SharedPreferences.Editor prefEdit =
                                    getSharedPreferences(SessionManagement.PREF_NAME,
                                            MODE_PRIVATE).edit();
                            Log.d("LoginActivity: response", response.toString());
                            JSONObject responseUser = null;
                            try {
                                responseUser = response.getJSONObject("user");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (responseUser.has("id")) {
                                try {
                                    prefEdit.putString(SessionManagement.API_TOKEN,
                                            responseUser.getString("id"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                prefEdit.putBoolean(SessionManagement.IS_LOGIN, true);
                            } else {
                                prefEdit.putBoolean(SessionManagement.IS_LOGIN, false);
                            }
                            prefEdit.apply();
                            SharedPreferences pref =
                                    getSharedPreferences(SessionManagement.PREF_NAME, MODE_PRIVATE);
                            if (pref.getBoolean(SessionManagement.IS_LOGIN, false)) {
                                // Staring MainActivity
                                Intent i = new Intent(getApplicationContext(), FeedActivity.class);
                                startActivity(i);
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        "Signup Failed...\nUsername already Exists",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }
                    );

                    requestQueueSingleton.getInstance(currentContext).addToRequestQueue(signupObjRequest);

                } else {
                    Toast.makeText(getApplicationContext(),
                            "Signup failed...\nPlease enter username and password",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        // Signup button listener
        btnRegistered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View arg0) {
                // Staring LoginActivity
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
                finish();
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void openCompose(){
        Intent intent = new Intent(this, NewReportStartActivity.class);
        startActivity(intent);
    }
}
