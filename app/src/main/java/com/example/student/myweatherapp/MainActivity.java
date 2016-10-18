package com.example.student.myweatherapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity {


    TextView textViewCity, textViewUpdated, textViewDetails,
            textViewHumidity, textViewPressure, textViewTemp;

    String city, updated, details, humidity, pressure, temp;

    private String queryStringCity="Sydney";

    private String OPEN_WEATHER_MAP_URL=
            "http://api.openweathermap.org/data/2.5/weather"+
                    "?q="+ queryStringCity +
                    "&units=metric"+
                    "&appid=5d8185e279dfca4c8c4c9344f61cf3d7";




    public class AsyncWeather extends AsyncTask<Void, Void, JSONObject>
    {
        @Override
        protected JSONObject doInBackground(Void... params)
        {
            JSONObject jsonWeather = null;
            jsonWeather = getWeatherJSON();
            return jsonWeather;

        }

        private JSONObject getWeatherJSON()
        {
            try
            {
                //API call
                URL url = new URL(OPEN_WEATHER_MAP_URL);

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();

                BufferedReader reader =
                        new BufferedReader(
                                new InputStreamReader(urlConnection.getInputStream())
                                );

                StringBuilder json = new StringBuilder();
                String line="";

                while ((line = reader.readLine()) != null)
                {
                    json.append(line + "\n");
                }
                reader.close();

                JSONObject weatherData = new JSONObject(json.toString());

                if (weatherData.getInt("cod") != 200)
                {
                    return null;
                }

                return weatherData;
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(JSONObject jsonObject)
        {
            try
            {
                if (jsonObject != null)
                {
                    JSONObject weather = jsonObject.getJSONArray("weather").getJSONObject(0);
                    JSONObject main = jsonObject.getJSONObject("main");

                    city = jsonObject.getString("name");
                    DateFormat dateFormat = DateFormat.getDateInstance();
                    updated = dateFormat.format(new Date(jsonObject.getLong("dt") * 1000));
                    details = weather.getString("description");
                    humidity = main.getString("humidity") +"%";
                    pressure = main.getString("pressure") + "hPa";
                    temp = String.format("%.2f", main.getDouble("temp")) + "°";

                    textViewCity.setText(city);
                    textViewUpdated.setText(updated);
                    textViewDetails.setText(details);
                    textViewHumidity.setText(humidity);
                    textViewPressure.setText(pressure);
                    textViewTemp.setText(temp);
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        textViewCity=(TextView) findViewById(R.id.textViewCity);
        textViewUpdated=(TextView) findViewById(R.id.textViewUpdated);
        textViewDetails=(TextView) findViewById(R.id.textViewDetails);
        textViewHumidity=(TextView) findViewById(R.id.textViewHumidity);
        textViewPressure=(TextView) findViewById(R.id.textViewPressure);
        textViewTemp=(TextView) findViewById(R.id.textViewTemp);

        new AsyncWeather().execute();

        getSupportActionBar().hide();


    }

    public void onClickRefresh(View v)
    {
        EditText editTextCity=(EditText) findViewById(R.id.editTextCity);

        queryStringCity = editTextCity.getText().toString();

        if (queryStringCity=="")
        {
            queryStringCity="Sydney";
        }

        //queryStringCity.replaceAll("[ ]", "%20");
        try {
            queryStringCity = java.net.URLEncoder.encode(queryStringCity, "UTF-8").replace("+", "%20");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        OPEN_WEATHER_MAP_URL=
                "http://api.openweathermap.org/data/2.5/weather"+
                        "?q="+ queryStringCity +
                        "&units=metric"+
        "&appid=5d8185e279dfca4c8c4c9344f61cf3d7";

        new AsyncWeather().execute();

    }



}
