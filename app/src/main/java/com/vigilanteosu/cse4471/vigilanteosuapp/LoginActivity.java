package com.vigilanteosu.cse4471.vigilanteosuapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInstaller;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences.Editor;

public class LoginActivity extends Activity {


    // Email, password edittext
    EditText txtUsername, txtPassword;

    // login button
    Button btnLogin;

    // signup button
    Button btnSignup;

    // Alert Dialog Manager
    //AlertDialogManager alert = new AlertDialogManager();

    // Session Manager Class
    // SessionManagement session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        // Session Manager
        // session = new SessionManagement(getApplicationContext());

        // Email, Password input text
        txtUsername = (EditText) findViewById(R.id.txtUsername);
        txtPassword = (EditText) findViewById(R.id.txtPassword);

        // Login button
        btnLogin = (Button) findViewById(R.id.btnLogin);

        // Signup button
        btnSignup = (Button) findViewById(R.id.btnSignup);

        //RequestQueue queue = Volley.newRequestQueue(this);

        final Context currentContext = this;


        // Login button click event
        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                // Get username, password from EditText
                String username = txtUsername.getText().toString();
                String password = txtPassword.getText().toString();
                // Check if username, password is filled
                if(username.trim().length() > 0 && password.trim().length() > 0){
                    // check if the user exists and the password is correct
                    String url ="http://jeffcasavant.com:10100/vig/api/v1.0/users/";
                    url = url.concat(username + "/" + password);

                    JsonObjectRequest jsObjRequest = new JsonObjectRequest
                            (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    //handle response
                                    Editor prefEdit =
                                            getSharedPreferences(SessionManagement.PREF_NAME,
                                                    MODE_PRIVATE).edit();
                                    if (response.has("token")) {
                                        try {

                                            prefEdit.putString(SessionManagement.API_TOKEN,
                                                    response.getString("token"));

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                        prefEdit.putBoolean(SessionManagement.IS_LOGIN, true);


                                    } else {
                                        prefEdit.putBoolean(SessionManagement.IS_LOGIN, false);
                                    }
                                    prefEdit.commit();
                                    //////
                                    Context context = getApplicationContext();
                                    GcmRegister register = new GcmRegister();
                                    register.sendRegistrationIdToBackend(context);

                                    SharedPreferences pref =
                                            getSharedPreferences(SessionManagement.PREF_NAME,
                                                    MODE_PRIVATE);
                                    if (pref.getBoolean(SessionManagement.IS_LOGIN, false)) {
                                        // Staring MainActivity
                                        Intent i = new Intent(getApplicationContext(),
                                                FeedActivity.class);
                                        startActivity(i);
                                        finish();
                                    } else {
                                        Toast.makeText(getApplicationContext(),
                                                "Login Failed...\nUsername/Password Incorrect",
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            }, new Response.ErrorListener() {

                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // TODO Auto-generated method stub

                                }
                            });

                    requestQueueSingleton.getInstance(currentContext).addToRequestQueue(jsObjRequest);

                }else{
                    // user didn't entered username or password
                    // Show alert asking him to enter the details
                    //alert.showAlertDialog(LoginActivity.this, "Login failed..", "Please enter username and password", false);
                    Toast.makeText(getApplicationContext(),
                            "Login failed...\nPlease enter username and password",
                            Toast.LENGTH_LONG).show();
                }

            }
        });

        // Signup button listener
    }

    public void openSignup(View view){
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
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
//        if (id == R.id.action_settings) {
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }
}
