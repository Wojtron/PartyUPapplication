package com.example.wojci.partyupapp;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by wojci on 03.05.2018.
 */


public class firebaseMarkerRetrieving {

    public String lat;
    public String lng;
    public String title;
    public String description;
    public String color;

    public firebaseMarkerRetrieving()
    {
    }

    public firebaseMarkerRetrieving(String lat, String lng, String title, String description, String color) {
        this.lat = lat;
        this.lng = lng;
        this.title = title;
        this.description = description;
        this.color = color;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
