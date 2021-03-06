package com.exinnos.popularmovies.fragments;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.exinnos.popularmovies.R;
import com.exinnos.popularmovies.adapters.MovieDetailsPagerAdapter;
import com.exinnos.popularmovies.data.MovieDetails;
import com.exinnos.popularmovies.database.MoviesContract;
import com.exinnos.popularmovies.util.AppUtilities;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author RAMPRASAD
 *         Fragment for Movie details screen.
 */
public class MovieDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String ARG_MOVIE_ID = "arg_movie_id";
    private static final String LOG_TAG = "MovieDetailFragment";
    private static final int MOVIE_DETAILS_LOADER = 200;
    private static final int FAVORITE_MOVIE_DETAILS_LOADER = 600;
    private static final String TAG_FAVORITE = "favorite";
    private static final String TAG_NOT_FAVORITE = "not_favorite";
    private static final String KEY_VIEW_PAGER_CURRENT_ITEM = "key_viewpager_current_item";
    @Bind(R.id.movie_details_top_layout)
    LinearLayout movieDetailsTopLayout;
    @Bind(R.id.movie_title_textview)
    TextView movieTitleTextView;
    @Bind(R.id.release_date_textview)
    TextView releaseDateTextView;
    @Bind(R.id.rating_textview)
    TextView ratingTextView;
    @Bind(R.id.favorite_fab)
    FloatingActionButton favoriteFab;
    @Bind(R.id.movie_details_viewpager)
    ViewPager movieDetailViewPager;
    @Bind(R.id.movie_detail_tab_layout)
    TabLayout movieDetailTabLayout;
    private int mMovieId;


    private OnMovieDetailFragmentListener mListener;
    private View rootView;

    private ContentResolver mContentResolver;
    private int mCurrentViewPagerCurrentItem = 0;


    public MovieDetailFragment() {
        // Required empty public constructor
    }

    public static MovieDetailFragment newInstance(int movieId) {
        MovieDetailFragment fragment = new MovieDetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_MOVIE_ID, movieId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setRetainInstance(true);

        if (getArguments() != null) {
            mMovieId = getArguments().getInt(ARG_MOVIE_ID);
        }

        Log.d(LOG_TAG, "onCreate " + mCurrentViewPagerCurrentItem);

        if (savedInstanceState != null) {
            mCurrentViewPagerCurrentItem = savedInstanceState.getInt(KEY_VIEW_PAGER_CURRENT_ITEM);
            Log.d(LOG_TAG, "onCreate after restore => " + mCurrentViewPagerCurrentItem);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        ButterKnife.bind(this, rootView);

        if (mMovieId == 0) {
            movieDetailsTopLayout.setVisibility(View.GONE);
            favoriteFab.setVisibility(View.GONE);

            return rootView;
        }


        List<String> fragmentTitleList = new ArrayList<String>();
        fragmentTitleList.add("SUMMARY");
        fragmentTitleList.add("TRAILERS");
        fragmentTitleList.add("REVIEWS");

        MovieDetailsPagerAdapter movieDetailsPagerAdapter = new MovieDetailsPagerAdapter(getChildFragmentManager(), mMovieId, fragmentTitleList);
        movieDetailViewPager.setAdapter(movieDetailsPagerAdapter);
        movieDetailViewPager.setCurrentItem(mCurrentViewPagerCurrentItem);

        movieDetailTabLayout.setupWithViewPager(movieDetailViewPager);

        favoriteFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String favoriteTag = view.getTag().toString();

                if (favoriteTag.equalsIgnoreCase(TAG_FAVORITE)) {
                    removeFromFavorites();
                    view.setTag(TAG_NOT_FAVORITE);
                    favoriteFab.setImageResource(R.drawable.ic_favorite_border_white_24dp);
                } else {
                    addToFavorites();
                    view.setTag(TAG_FAVORITE);
                    favoriteFab.setImageResource(R.drawable.ic_favorite_white_24dp);
                }
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mContentResolver = getActivity().getContentResolver();
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mMovieId > 0) {
            getLoaderManager().initLoader(MOVIE_DETAILS_LOADER, null, this);
            getLoaderManager().initLoader(FAVORITE_MOVIE_DETAILS_LOADER, null, this);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMovieDetailFragmentListener) {
            mListener = (OnMovieDetailFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnMovieDetailFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(LOG_TAG, "onSaveInstanceState movieDetailViewPager.getCurrentItem() => " + movieDetailViewPager.getCurrentItem());
        outState.putInt(KEY_VIEW_PAGER_CURRENT_ITEM, movieDetailViewPager.getCurrentItem());
        super.onSaveInstanceState(outState);
    }

    /**
     * Update movie details on UI.
     *
     * @param movieDetails
     */
    private void updateOnUI(MovieDetails movieDetails) {

        if (movieDetails == null) {
            return;
        }

        movieTitleTextView.setText(movieDetails.getOriginalTitle());

        releaseDateTextView.setText(AppUtilities.getFormattedDate(movieDetails.getReleaseDate()));

        ratingTextView.setText(String.format("%.1f/10", movieDetails.getVoteAverage()));
    }


    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {

        if (id == MOVIE_DETAILS_LOADER) {
            Uri movieDetailsUri = MoviesContract.MoviesEntry.buildMoviesWithIdUri(mMovieId);
            return new CursorLoader(getActivity(), movieDetailsUri, null, MoviesContract.MoviesEntry._ID + "=?", new String[]{String.valueOf(mMovieId)}, null);
        } else if (id == FAVORITE_MOVIE_DETAILS_LOADER) {
            Uri favoriteMovieUri = MoviesContract.FavoriteMoviesEntry.buildFavoriteMoviesUri();
            return new CursorLoader(getActivity(), favoriteMovieUri, null, MoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_ID + "=?", new String[]{String.valueOf(mMovieId)}, null);
        }

        return null;
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor cursor) {

        if (loader.getId() == MOVIE_DETAILS_LOADER) {
            Log.d(LOG_TAG, "cursor count =>" + cursor.getCount());

            if (cursor != null && cursor.getCount() > 0) {

                if (cursor.moveToFirst()) {
                    Log.d(LOG_TAG, "movie name =>" + cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_ORIGINAL_TITLE)));
                    MovieDetails movieDetails = new MovieDetails();
                    movieDetails.setId(cursor.getInt(cursor.getColumnIndex(MoviesContract.MoviesEntry._ID)));
                    movieDetails.setAdult(cursor.getInt(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_ADULT)) == 1);
                    movieDetails.setBackdropPath(cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_BACKDROP_PATH)));
                    movieDetails.setBudget(cursor.getInt(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_BUDGET)));
                    movieDetails.setHomepage(cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_HOME_PAGE)));
                    movieDetails.setImdbId(cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_IMDB_ID)));
                    movieDetails.setOriginalLanguage(cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_ORIGINAL_LANGUAGE)));
                    movieDetails.setOriginalTitle(cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_ORIGINAL_TITLE)));
                    movieDetails.setOverview(cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_OVERVIEW)));
                    movieDetails.setPopularity(cursor.getDouble(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_POPULARITY)));
                    movieDetails.setPosterPath(cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_POSTER_PATH)));
                    movieDetails.setReleaseDate(cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE)));
                    movieDetails.setRevenue(cursor.getInt(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_REVENUE)));
                    movieDetails.setRuntime(cursor.getInt(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_RUNTIME)));
                    movieDetails.setStatus(cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_STATUS)));
                    movieDetails.setTagline(cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_TAGLINE)));
                    movieDetails.setVideo(cursor.getInt(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_VIDEO)) == 1);
                    movieDetails.setVoteAverage(cursor.getDouble(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE)));
                    movieDetails.setVoteCount(cursor.getInt(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_VOTE_COUNT)));

                    updateOnUI(movieDetails);
                }
            }
        } else if (loader.getId() == FAVORITE_MOVIE_DETAILS_LOADER) {

            if (cursor.getCount() > 0) {
                // it is a favorite movie
                favoriteFab.setTag(TAG_FAVORITE);
                favoriteFab.setImageResource(R.drawable.ic_favorite_white_24dp);
            } else {
                // it is not a favorite movie
                favoriteFab.setTag(TAG_NOT_FAVORITE);
                favoriteFab.setImageResource(R.drawable.ic_favorite_border_white_24dp);
            }
        }
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

    }

    // Remove current movie from favorites
    private void removeFromFavorites() {
        Uri favoriteMoviesUri = MoviesContract.FavoriteMoviesEntry.buildFavoriteMoviesUri();
        mContentResolver.delete(favoriteMoviesUri, MoviesContract.FavoriteMoviesEntry.TABLE_NAME + "." + MoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_ID + " = ?", new String[]{String.valueOf(mMovieId)});
    }

    // Add current movie to favorites
    private void addToFavorites() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_ID, mMovieId);

        Uri favoriteMoviesUri = MoviesContract.FavoriteMoviesEntry.buildFavoriteMoviesUri();
        mContentResolver.insert(favoriteMoviesUri, contentValues);
    }

    /**
     * Interface to communicate with host fragment.
     */
    public interface OnMovieDetailFragmentListener {

    }

}
