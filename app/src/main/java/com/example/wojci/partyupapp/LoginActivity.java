package com.example.wojci.partyupapp;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final int ERROR_SERVICES_REQUEST = 001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button goNext = findViewById(R.id.buttonLogIn);


    }

    public void logIn (View v)
    {
        checkServices();

         Intent ToMap = new Intent (this, MapActivity.class); //intent which is sending us to map activity
        startActivity(ToMap);

    }

    public boolean checkServices() //method which is checking google services version is OK
    {
        Log.d(TAG, "checkServices: Checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(LoginActivity.this);

        if(available == ConnectionResult.SUCCESS)
        {
            //google services version is appropriate and device can make map request.
            Log.d(TAG, "checkServices: Services are good");
            return true;
        }
        else if (GoogleApiAvailability.getInstance().isUserResolvableError(available))
        {
            //there is a problem with google services but it can be fixed
            Log.d(TAG,"checkServices: Error with services, but it can be fixed");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(LoginActivity.this, available, ERROR_SERVICES_REQUEST);
            dialog.show();
        }
        else
        {
            //if there is nothing to be done and we have some kind of problem
            Toast.makeText(this, "Something is wrong \n (issue is probably your version of Google Services)", Toast.LENGTH_SHORT).show();
        }
        return false;
    }





}