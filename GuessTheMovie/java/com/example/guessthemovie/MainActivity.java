package com.example.guessthemovie;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private ImageView imageView;
    private Button[] buttons = new Button[4];
    private final String base = "https://www.filmweb.pl/ajax/ranking/film/";
    private final Random random = new Random();

    private Bitmap bitmap;
    private String[] names = new String[4];

    private final Pattern pattern = Pattern.compile("<img class=\"filmPoster__image FilmPoster\" (data-src|src)=\"([\\w\\:\\/\\.]*)\" alt=\\\"([\\w\\sąęóżźćśłńĄĘÓŻŹĆŚŁŃ:()]*)\\\"");

    private class Movie {
        public String name = null;
        public String imageUrl = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        buttons[0] = findViewById(R.id.button1);
        buttons[1] = findViewById(R.id.button2);
        buttons[2] = findViewById(R.id.button3);
        buttons[3] = findViewById(R.id.button4);

        nextTurn();
    }

    public void choose(View view) {
        nextTurn();
    }

    private void nextTurn() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // read random page ( 1 to 20 ) html content
                    int page = random.nextInt(20) + 1;
                    URL url = new URL(base + page);
                    InputStream in = url.openStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    String content = "";
                    String line;

                    while ((line = reader.readLine()) != null)
                        content += line + "\n";

                    List<Movie> movies = new ArrayList<>();

                    // parse images and names
                    Matcher matcher = pattern.matcher(content);
                    while (matcher.find()) {
                        Movie movie = new Movie();
                        movie.imageUrl = matcher.group(2);
                        movie.name = matcher.group(3);
                        movies.add(movie);
                    }

                    // log movies
                    for (int i = 0; i < movies.size(); i++)
                        Log.i("Movie " + i + ":", movies.get(i).name + " " + movies.get(i).imageUrl);

                    // something went wrong
//                    if (i < 24)
//                        throw new RuntimeException("Not enough images");

                    // choose random answers
                    List<Integer> rands = new ArrayList<>();
                    for (int i = 0; i < 4; i++) {
                        int randomValue;
                        while (true) {
                            randomValue = random.nextInt(movies.size());
                            if (!rands.contains(randomValue)) {
                                rands.add(randomValue);
                                break;
                            }
                        }
                    }

                    for (int i = 0; i < 4; i++)
                        names[i] = movies.get(i).name;

                    // choose correct answer
                    int correct = random.nextInt(4);
                    URL imageUrl = new URL(movies.get(correct).imageUrl);
                    InputStream imageStream = imageUrl.openStream();
                    bitmap = BitmapFactory.decodeStream(imageStream);

                    imageView.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();

        try {
            thread.join();
            imageView.setImageBitmap(bitmap);
            for (int i = 0; i < 4; i++)
                buttons[i].setText(names[i]);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}