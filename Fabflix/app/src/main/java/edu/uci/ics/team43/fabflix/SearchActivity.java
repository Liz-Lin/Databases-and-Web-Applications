package edu.uci.ics.team43.fabflix;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SearchActivity extends AppCompatActivity {
    private EditText searchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Button searchButton = (Button) findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitSearchRequest();
            }
        });
        searchView = (EditText) findViewById(R.id.search_query);
    }

    private void submitSearchRequest() {
        String query = searchView.getText().toString();
        if (query.length() < 3) {
            Toast.makeText(getApplicationContext(), "Search Query should be longer than 3 characters", Toast.LENGTH_LONG).show();
        } else {
            Intent goToIntent = new Intent(this, MovieListActivity.class);

            goToIntent.putExtra("last_activity", "SearchActivity");
            goToIntent.putExtra("title", query);
            startActivity(goToIntent);
        }
    }
}
