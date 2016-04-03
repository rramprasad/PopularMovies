package com.exinnos.popularmovies.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.exinnos.popularmovies.data.Movie;
import com.exinnos.popularmovies.fragments.MovieReviewsFragment;
import com.exinnos.popularmovies.fragments.MovieSummaryFragment;
import com.exinnos.popularmovies.fragments.MovieTrailersFragment;

/**
 * Created by RAMPRASAD on 4/2/2016.
 * Adapter for movie details viewpager
 */
public class MovieDetailsPagerAdapter extends FragmentPagerAdapter {

    private static final int FRAGMENT_PAGES_COUNT = 3;
    private int mMovieId;

    public MovieDetailsPagerAdapter(FragmentManager supportFragmentManager,int movieId) {
        super(supportFragmentManager);
        this.mMovieId = movieId;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return MovieSummaryFragment.newInstance(mMovieId);
            case 1:
                return MovieTrailersFragment.newInstance(mMovieId);
            case 2:
                return MovieReviewsFragment.newInstance(mMovieId);
            default:
                return MovieSummaryFragment.newInstance(mMovieId);
        }
    }

    @Override
    public int getCount() {
        return FRAGMENT_PAGES_COUNT;
    }
}
