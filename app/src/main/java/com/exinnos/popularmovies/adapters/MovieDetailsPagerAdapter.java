package com.exinnos.popularmovies.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.exinnos.popularmovies.fragments.MovieSummaryFragment;

/**
 * Created by RAMPRASAD on 4/2/2016.
 * Adapter for movie details viewpager
 */
public class MovieDetailsPagerAdapter extends FragmentStatePagerAdapter{

    private static final int FRAGMENT_PAGES_COUNT = 3;
    private int mMovieId;

    public MovieDetailsPagerAdapter(FragmentManager supportFragmentManager,int movieId) {
        super(supportFragmentManager);
        this.mMovieId = movieId;
    }

    @Override
    public Fragment getItem(int position) {
        return MovieSummaryFragment.newInstance(mMovieId);
    }

    @Override
    public int getCount() {
        return FRAGMENT_PAGES_COUNT;
    }
}
