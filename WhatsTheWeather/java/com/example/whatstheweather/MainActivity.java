package com.example.whatstheweather;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class MainActivity extends AppCompatActivity {

    private final String API_KEY = BuildConfig.API_KEY;
    private TextView city;
    private TextView weather;

    private String weatherText = "";

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        city = findViewById(R.id.city);
        weather = findViewById(R.id.weather);
    }

    public void getWeather(View view) {
        FutureTask<String> futureTask = new FutureTask<>(new Runnable() {
            @Override
            public void run() {
                try {

                    URL url = new URL("http://api.openweathermap.org/data/2.5/weather?q=" + city.getText() + "&appid=" + API_KEY);
                    InputStream in = url.openStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                    String result = "";
                    String line;
                    while ((line = reader.readLine()) != null)
                        result += line;

                    JSONObject root = new JSONObject(result);
                    JSONArray weather = root.getJSONArray("weather");
                    if (weather.length() > 0) {
                        String mainWeather = weather.getJSONObject(0).getString("main");
                        String descWeather = weather.getJSONObject(0).getString("description");
                        weatherText = mainWeather + "\n" + descWeather;
                    }

                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        }, "Task completed");

        executor.execute(futureTask);

        while (!futureTask.isDone());

        weather.setText(weatherText);
        ((Button) view).onEditorAction(EditorInfo.IME_ACTION_DONE);
    }
}