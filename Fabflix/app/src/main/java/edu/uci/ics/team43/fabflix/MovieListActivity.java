package edu.uci.ics.team43.fabflix;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MovieListActivity extends Activity {
    private MovieListViewAdapter adapter;
    private ArrayList<Movie> movies;
    private Map<String, String> params;
    private Integer offset;
    private Integer limit;
    private ListView listView;
    private Button prevButton;
    private Button nextButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        movies = new ArrayList<>();
        listView = findViewById(R.id.list);
        adapter = new MovieListViewAdapter(movies, this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = movies.get(position);
                Intent goToIntent = new Intent(getApplicationContext(), SingleMovieActivity.class);
                goToIntent.putExtra("last_activity", "MovieListActivity");
                goToIntent.putExtra("movie_id", movie.getId());
                goToIntent.putExtra("movie_title", movie.getTitle());
                goToIntent.putExtra("movie_year", movie.getYear());
                goToIntent.putExtra("movie_director", movie.getDirector());
                goToIntent.putExtra("movie_stars", movie.getStars());
                goToIntent.putExtra("movie_genres", movie.getGenres());
                goToIntent.putExtra("movie_rating", movie.getRating());
                startActivity(goToIntent);
            }
        });
        nextButton = (Button) findViewById(R.id.nextBtn);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getMovies(1);
            }
        });
        prevButton = (Button) findViewById(R.id.prevBtn);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getMovies(-1);
            }
        });
        limit = Integer.parseInt(getResources().getString(R.string.API_movie_list_limit));
        getMovies(0);
    }

    public void getMovies(int pageOffset)
    {
        if (!movies.isEmpty()) movies.clear();

        if (params==null){
            params=new HashMap<>();
            Bundle bundle = getIntent().getExtras();
            offset = bundle.getInt("offset",0);
            String queryTitle = bundle.getString("title");
            params.put("MovieTitle", queryTitle);
            params.put("offset", offset.toString());
            params.put("all", "0");
            params.put("orderBy", "title");
            params.put("isASC", "1");
            params.put("limit", limit.toString());
            params.put("is_android", "1");

        }
//        offset = Math.max(offset+pageOffset, 0);
//        params.put("offset", Integer.toString(Math.max(offset+pageOffset, 0)));
        offset = Math.max(offset+ (pageOffset*limit) , 0);
        params.put("offset", Integer.toString(offset));
        // TODO: make GET request here
        makeMovieListRequest();

    }

    private void makeMovieListRequest(){
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        String request_url = getResources().getString(R.string.API_host)+ getResources().getString(R.string.API_movie_list);
        StringRequest listRequest = new StringRequest(Request.Method.POST,
                request_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonResponse = new JSONObject(response);
                            if (!jsonResponse.getString("status").equals("success")){
                                Toast.makeText(getApplicationContext(),"message: "+ jsonResponse.getString("message"), Toast.LENGTH_LONG).show();
                                return;
                            }

                            JSONArray jsonArray = jsonResponse.getJSONArray("data");
                            for(int i = 0; i< jsonArray.length(); i++){
                                JSONObject jsonMovie = jsonArray.getJSONObject(i);
                                String stars = createStarListString(jsonMovie.getJSONArray("stars"));
                                Movie movie = new Movie(
                                        jsonMovie.getString("movie_id"),
                                        jsonMovie.getString("movie_title"),
                                        jsonMovie.getString("movie_year"),
                                        jsonMovie.getString("movie_director"),
                                        stars,
                                        jsonMovie.getString("genres"),
                                        jsonMovie.getString("rating")
                                        );
                                movies.add(movie);

                            }
                            adapter.notifyDataSetChanged();
                            listView.setSelectionAfterHeaderView();
                            if (offset==0) {
                                prevButton.setEnabled(false);
                            }
                            else{
                                prevButton.setEnabled(true);
                            }
                            if (movies.size()<limit){
                                nextButton.setEnabled(false);
                            }
                            else {
                                nextButton.setEnabled(true);
                            }
                        }catch (Exception e){
                            Log.d("response error ", e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.d("VolleyError:",volleyError.toString());
                        Toast.makeText(getApplicationContext(),
                                "cannot access the server, try login again.", Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };
        queue.add(listRequest);
    }
    private String createStarListString(JSONArray stars) throws JSONException{
        String starString = "";
        for (int i = 0; i < stars.length();i++){
            starString += stars.getJSONObject(i).getString("star_name");
            if (i != stars.length()-1){
                starString +=", ";
            }
        }
        return starString;
    }



}
