package com.example.wojci.partyupapp;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

public class WeatherActivity extends AppCompatActivity {


    static TextView placeTextView;
    static TextView temperatureTextView;
    static TextView statusTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        //ZAKOMENTARZOWANE NA CZAS OBRONY XDD

        /*placeTextView = (TextView) findViewById(R.id.weatherNameTV);
        temperatureTextView = (TextView) findViewById(R.id.weatherTemperatureTV);
        //statusTextView = (TextView) findViewById(R.id.weatherStatusTV);
          LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        String provider = locationManager.getBestProvider(new Criteria(), false);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationManager.getLastKnownLocation(provider);
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        weatherDownloadTask task = new weatherDownloadTask();
        task.execute("http://samples.openweathermap.org/data/2.5/weather?lat="+String.valueOf(lat)+ "&lon=" + String.valueOf(lng)+"&appid=0762285ed75250dc274323796b0f19dc");*/
    }
}
