package com.example.wojci.partyupapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

public class MarkerActivity extends AppCompatActivity {


    private static final String TAG = "MapActivity";

    //Intent Variables
    private EditText mTitle;
    private EditText mDescription;
    private Spinner mSpinner;
    private Button mAccept;

   private String titleText;
   private String snippetText;
   private String spinnerText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker);


        //initializing Intent variables.
        mTitle = (EditText)  findViewById(R.id.editTextTitle);
        mDescription = (EditText) findViewById(R.id.editTextDescription);
        mSpinner = (Spinner)  findViewById(R.id.colorSpinner);
        mAccept = (Button)  findViewById(R.id.buttonAccept);

    }

    public void finishIntent(View v)
    {
        if(mTitle.getText().toString().length() >2) {

            titleText = mTitle.getText().toString();
            snippetText = mDescription.getText().toString();
            spinnerText = mSpinner.getSelectedItem().toString();

            Intent backToMap = new Intent (MarkerActivity.this,MapActivity.class);
            backToMap.putExtra("markerTitle", titleText);
            backToMap.putExtra("markerSnippet", snippetText);
            backToMap.putExtra("markerSpinner", spinnerText);

            setResult(RESULT_OK, backToMap);
            finish();
        }
        else
        {
            Toast.makeText(this, "Enter at least three letters to the Title", Toast.LENGTH_SHORT).show();
        }
    }
}

