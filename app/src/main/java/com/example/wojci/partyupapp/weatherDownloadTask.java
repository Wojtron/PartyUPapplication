package com.example.wojci.partyupapp;


import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by wojci on 23.04.2018.
 */

    public class weatherDownloadTask extends AsyncTask<String,Void,String>{

        String result;


        @Override
        protected String doInBackground(String... urls)
        {

            result = "";
            URL url;
            HttpURLConnection urlConnection  = null;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while (data != -1)
                {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return  result;
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
         return null;
        }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        try {


            JSONObject myObject = new JSONObject(result);
            JSONObject main = new JSONObject(myObject.getString("main"));
            double temperature = Double.parseDouble(main.getString("temp" ));
            int celciusTemperature = (int)temperature - 273;
            String finalTemperature = Integer.toString(celciusTemperature) + " ℃";
            String placeName = myObject.getString("name");
            String placeStatus = myObject.getString("weather");

            WeatherActivity.placeTextView.setText(placeName);
            WeatherActivity.temperatureTextView.setText(finalTemperature);
            WeatherActivity.statusTextView.setText(placeStatus);



        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }
}
