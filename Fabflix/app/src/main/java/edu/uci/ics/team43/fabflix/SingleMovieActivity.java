package edu.uci.ics.team43.fabflix;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class SingleMovieActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_movie);
        Bundle bundle = getIntent().getExtras();
        ((TextView) findViewById(R.id.movie_title)).setText(bundle.getString("movie_title"));
        ((TextView) findViewById(R.id.movie_year)).setText("Year: " + bundle.getString("movie_year"));
        ((TextView) findViewById(R.id.movie_director)).setText("Director: " + bundle.getString("movie_director"));
        ((TextView) findViewById(R.id.movie_genres)).setText("Genres: " + bundle.getString("movie_genres"));
        ((TextView) findViewById(R.id.movie_stars)).setText("Stars: " + bundle.getString("movie_stars"));
        ((TextView) findViewById(R.id.movie_rating)).setText("Rating: " + bundle.getString("movie_rating"));
    }
}
