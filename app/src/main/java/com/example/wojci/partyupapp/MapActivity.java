package com.example.wojci.partyupapp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by wojci on 02.04.2018.
 */
public class MapActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener{



    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: Ready!");
        mMap = googleMap;

        if (mLocationPermissionsGranted) {
            getActualLocation();

            //if below is checking is requesting for the permission to get location.
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true); // setting location.
            //
            mMap.getUiSettings().setMyLocationButtonEnabled(false); //disabling location button.

            locationInit();
            markerInit();
            showWeather();
            try {
                enterToChat();
            }
            catch(Exception e)
            {

            }
            }
    }

    //constans variables
    private final int MARKER_RESULT_POSITIVE = 1;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 0001;
    private static final float DEFAULT_ZOOM =  15f;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40,-168),new LatLng(71,136));

    //variables
    private Boolean isPartyClicked = false;
    private Boolean mLocationPermissionsGranted = false;
    private Boolean markerChecker= false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;
    private GoogleApiClient mGoogleApiClient;
    private DatabaseReference markerRef;
    private Location location;

    private List<Marker> markerList;
    private LatLng actualLatLng;
    private String markerName;
    private String markerDescription;
    private String markerColor;
    private String key;
    private String markerID;


    //widgets
    private AutoCompleteTextView mSearchText;
    private ImageView mGps;
    private ImageView mOwnMarker;
    private ImageView mWeather;
    private ImageView mChat;

    private static final String TAG = "MapActivity";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        //Edit Text and Buttons on main View.
        mSearchText = (AutoCompleteTextView) findViewById(R.id.input_search);
        mGps = (ImageView) findViewById(R.id.ic_gps);
        mOwnMarker = (ImageView) findViewById(R.id.ic_partys);
        mWeather = (ImageView) findViewById(R.id.ic_weather);
        mChat = (ImageView) findViewById(R.id.ic_chat);
        markerRef = FirebaseDatabase.getInstance().getReference("markers");

        getLocationPermission();
    }

   @Override
    protected void onStart() {
        super.onStart();

        markerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot markerSnapshot: dataSnapshot.getChildren())
                {

                    firebaseMarkerRetrieving fmr = markerSnapshot.getValue(firebaseMarkerRetrieving.class);

                    try{

                   Marker marker = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(Double.parseDouble(fmr.getLng()),Double.parseDouble(fmr.getLat())))
                            .title(fmr.getTitle())
                            .snippet(fmr.getDescription())
                            .icon(BitmapDescriptorFactory.defaultMarker(Float.parseFloat(fmr.getColor()))));
                     }catch (Exception e)
                    {
                     e.printStackTrace();
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


////////////////////////////// MAPS API ////////////////////////////

    //initializing search bar (search textEdit)
    private void locationInit() {
        Log.d(TAG, "locationInit: initializing");


        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();


        mPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient, LAT_LNG_BOUNDS, null);

        mSearchText.setAdapter(mPlaceAutocompleteAdapter);

        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        keyEvent.getAction() == KeyEvent.ACTION_DOWN || //Action will start on Enter Key on keyboard.
                        keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) //Action will start on Enter tap (right bottom corner icon).
                {

                    //execute method for searching
                    geoLocating();
                }

                return false;
            }
        });


        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: GPS icon has been clicked");
                getActualLocation();
            }
        });
        hideKeyboard();
    }

    //This method is used to locate some place - it will return details of that location at the logs
    //but it won't change camera position.
    private void geoLocating()
    {
        Log.d(TAG, "geoLocating: in progress");

        String searchString = mSearchText.getText().toString();

        Geocoder geocoder = new Geocoder(MapActivity.this);
        List<Address> list = new ArrayList<>();
        try
        {
            list = geocoder.getFromLocationName(searchString, 1 );
        }
        catch (IOException e )
        {
            Log.e(TAG, "geoLocate: IOException " + e.getMessage());
        }
        if(list.size() > 0)
        {
            Address address = list.get(0);
            Log.d(TAG, "geoLocating: Your location " + address.toString() + " has been founded");

            //moving camera to the found location
            cameraMove(new LatLng(address.getLatitude(),address.getLongitude()),DEFAULT_ZOOM,address.getAddressLine(0));
        }
    }


    //We are using method below to get our Location.
    private void getActualLocation()
    {
        Log.d(TAG, "getActualLocation: getting current location of the device");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (mLocationPermissionsGranted) {
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful())
                        {
                            Log.d(TAG, "onComplete: found location!");
                            Location currLocation = (Location) task.getResult();
                            cameraMove(new LatLng(currLocation.getLatitude(), currLocation.getLongitude()),DEFAULT_ZOOM, "Your Location");
                        }
                        else
                        {
                            Log.d(TAG, "onComplete: can't find your location");
                            Toast.makeText(MapActivity.this, "Can't reach Your location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
        catch (SecurityException e)
        {
            Log.e(TAG, "getActualLocation: SecurityException: " + e.getMessage() );
        }
    }


    //Method which moving camera.
    private void cameraMove (LatLng latLng, float Zoom, String name)
    {
        Log.d(TAG, "cameraMove: camera moves to lat: " +latLng.latitude + ", lng " +latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,Zoom));

        if (!name.equals("Your Location")) {
            MarkerOptions options = new MarkerOptions().position(latLng).title(name);
            mMap.addMarker(options);
        }
        hideKeyboard();
    }


    //initializing map
    private void initMap()
    {
        Log.d(TAG, "initMap: initializing");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(MapActivity.this);
    }

    //Method which creates short dialog with user, to gain permissions
    private void getLocationPermission() {

        Log.d(TAG, "getLocationPermission: getting permissions");
        String[] permissions = {FINE_LOCATION, COARSE_LOCATION};

        //checking the permissions.
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED) {

                mLocationPermissionsGranted = true;
                initMap();
            }

            else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        }
        else
        {
            ActivityCompat.requestPermissions(this,permissions,LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    //result which is to happen after getLocationPermission.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionsGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                // grantResults.length>0 means that if there are some permission granted
                // (no permissions == 0 )
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {

                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission denied");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted=true;

                    //if everything is ok, we can initialize map.
                    initMap();
                }
            }
        }
    }
    private void hideKeyboard()
    {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    /////////////////////// MARKER INITIALIZER /////////////////////////

    private void markerInit()
    {

        mOwnMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPartyClick();
            }
        });
    }

    ///////////////////////////////////MARKER MAKER ///////////////////////////////////////////

    //Marker Color Changer.
    private float colorSpinner (String spinnerItem)
    {
        float Color = 50.0f;        // z jakiegs powodu progrma bierze pod uwage ta wartosc a nie sprawdza tych ponizej.
        if (spinnerItem.equals("red"))
        {
            Color = 0.0f;
        }
        else if (spinnerItem.equals("orange"))
        {
            Color = 30.0f;
        }
        else if (spinnerItem.equals("yellow"))
        {
            Color = 60.0f;
        }
        else if (spinnerItem.equals("green"))
        {
            Color =120.0f;
        }
        else if (spinnerItem.equals("blue"))
        {
            Color = 210.0f;
        }
        else if (spinnerItem.equals("purple"))
        {
            Color = 270.0f;
        }
        return Color;
    }




    private void markerResultInitializer()
    {
    Intent toMarkerMaker = new Intent(MapActivity.this,MarkerActivity.class);
                        Bundle latitudeLongitude = new Bundle();
                        startActivityForResult(toMarkerMaker,MARKER_RESULT_POSITIVE);
    }

     @Override
     protected void onActivityResult(int requestCode, int resultCode, Intent toMarkerMaker){
     super.onActivityResult(requestCode,resultCode,toMarkerMaker);
     if(requestCode == 1 && resultCode==RESULT_OK) {

    markerName = toMarkerMaker.getStringExtra("markerTitle");
    markerDescription = toMarkerMaker.getStringExtra("markerSnippet");
    markerColor = toMarkerMaker.getStringExtra("markerSpinner");

    Marker marker = mMap.addMarker(new MarkerOptions()
    .position(actualLatLng)
    .title(markerName)
    .snippet(markerDescription)
    .icon(BitmapDescriptorFactory.defaultMarker(colorSpinner(markerColor))));
    marker.showInfoWindow();
    }
    String lat = String.valueOf(actualLatLng.longitude);
    String lng= String.valueOf(actualLatLng.latitude);

    String markerColorString = String.valueOf(colorSpinner(markerColor)+"f");

    key = markerRef.push().getKey();
    markerRef.child(key).child("lat").setValue(lat);
    markerRef.child(key).child("lng").setValue(lng);
    markerRef.child(key).child("title").setValue(markerName);
    markerRef.child(key).child("description").setValue(markerDescription);
    markerRef.child(key).child("color").setValue(markerColorString);
    }





    private void onPartyClick()
    {
        if (isPartyClicked == false)
        {
            Log.d(TAG, "markerCreator: turned on");
            isPartyClicked = true;
            mOwnMarker.setBackgroundColor(getResources().getColor(R.color.button_pressed)); // changing background color to the white


            //method which gives us coordinates of touched point
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {

                    actualLatLng = latLng;
                    if (isPartyClicked ==true)
                    {

                        float Color = 0.0f;

                            Log.d(TAG, "onPartyClick - LatLong getter:   Longtitude: " + latLng.longitude + "  and Latitude:  " + latLng.latitude);

                            isPartyClicked = false;
                            mOwnMarker.setBackgroundColor(getResources().getColor(R.color.button_release));//changing background back to transparent ;

                        markerResultInitializer();

                    }
                }
            });
        }
        else
        {
            Log.d(TAG, "onPartyClick: turned off");
            isPartyClicked = false;
            mOwnMarker.setBackgroundColor(getResources().getColor(R.color.button_release));//changing background back to transparent on unclick;
        }
    }

    /////////////////////// WEATHER API ///////////////////////

    private void showWeather()
    {

        mWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toWeather = new Intent(MapActivity.this, WeatherActivity.class);
                startActivity(toWeather);

            }
        });

    }

    ////////////////////// CHAT BASED ON FIREBASE ///////////////////////

    private void enterToChat()
    {
        mChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toChat = new Intent(MapActivity.this, ChatActivity.class);
                startActivity(toChat);
            }
        });
    }
}


