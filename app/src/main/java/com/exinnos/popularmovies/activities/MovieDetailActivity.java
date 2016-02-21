package com.exinnos.popularmovies.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.exinnos.popularmovies.R;
import com.exinnos.popularmovies.fragments.MovieDetailFragment;

/**
 * @author RAMPRASAD
 *         Activity for movie details
 */
public class MovieDetailActivity extends AppCompatActivity implements MovieDetailFragment.OnMovieDetailFragmentListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        int movieId = getIntent().getIntExtra(MainActivity.INTENT_KEY_MOVIE_ID, 0);

        MovieDetailFragment movieDetailFragment = MovieDetailFragment.newInstance(movieId);
        getSupportFragmentManager().beginTransaction().replace(R.id.movie_detail_container, movieDetailFragment).commit();

    }

    @Override
    public void onMovieDetailFragmentInteraction() {
        // do nothing
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
