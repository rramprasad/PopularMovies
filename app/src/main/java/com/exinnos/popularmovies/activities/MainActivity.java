package com.exinnos.popularmovies.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.exinnos.popularmovies.R;
import com.exinnos.popularmovies.fragments.MovieDetailFragment;
import com.exinnos.popularmovies.fragments.MoviesFragment;
import com.exinnos.popularmovies.sync.MoviesSyncAdapter;

import butterknife.ButterKnife;

/**
 * @author RAMPRASAD
 *         Main Activity for movies.
 */
public class MainActivity extends AppCompatActivity implements MoviesFragment.OnMoviesFragmentListener, MovieDetailFragment.OnMovieDetailFragmentListener {

    public static final String INTENT_KEY_MOVIE_ID = "intent_key_movie_id";
    private boolean twoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        // Set toolbar
        Toolbar toolBar = ButterKnife.findById(this, R.id.toolbar);
        setSupportActionBar(toolBar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        /**
         * Check detail container available or not.
         * It contains only sw600dp devices(tablets).
         */
        if (ButterKnife.findById(this, R.id.framelayout_detail_container) != null) {
            twoPane = true;
        }

        // Initialize movies sync adapter
        MoviesSyncAdapter.initializeSyncAdapter(this);
    }

    @Override
    public void onMovieSelected(int movieId) {
        if (twoPane) {
            // This is a tablet device.
            MovieDetailFragment movieDetailFragment = MovieDetailFragment.newInstance(movieId);
            getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_detail_container, movieDetailFragment).commit();
        } else {
            // This is a mobile device.
            Intent intent = new Intent(MainActivity.this, MovieDetailActivity.class);
            intent.putExtra(INTENT_KEY_MOVIE_ID, movieId);
            startActivity(intent);
        }
    }
}
