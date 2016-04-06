package com.exinnos.popularmovies.fragments;


import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.exinnos.popularmovies.R;
import com.exinnos.popularmovies.data.MovieDetails;
import com.exinnos.popularmovies.database.MoviesContract;
import com.exinnos.popularmovies.util.AppConstants;
import com.exinnos.popularmovies.util.AppUtilities;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class MovieSummaryFragment extends Fragment  implements LoaderManager.LoaderCallbacks<Cursor>{


    private static final String ARG_MOVIE_ID = "arg_movie_id";
    private static final int MOVIE_SUMMARY_LOADER = 300;
    private static final String LOG_TAG = MovieSummaryFragment.class.getSimpleName();
    private int mMovieId;

    @Bind(R.id.overview_textview)
    TextView overviewTextView;

    @Bind(R.id.movie_poster_imageview)
    ImageView moviePosterImageView;

    public MovieSummaryFragment() {
        // Required empty public constructor
    }

    public static MovieSummaryFragment newInstance(int movieId) {
        MovieSummaryFragment fragment = new MovieSummaryFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_MOVIE_ID, movieId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mMovieId = getArguments().getInt(ARG_MOVIE_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_movie_summary, container, false);

        ButterKnife.bind(this,rootView);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        getLoaderManager().initLoader(MOVIE_SUMMARY_LOADER,null,this);
    }



    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri movieDetailsUri = MoviesContract.MoviesEntry.buildMoviesWithIdUri(mMovieId);
        return new CursorLoader(getActivity(),movieDetailsUri,null, MoviesContract.MoviesEntry._ID+"=?",new String[]{String.valueOf(mMovieId)},null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.d(LOG_TAG,"cursor count =>"+cursor.getCount());

        if(cursor != null && cursor.getCount() > 0){

            if(cursor.moveToFirst()){
                Log.d(LOG_TAG,"movie name =>"+cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_ORIGINAL_TITLE)));
                MovieDetails movieDetails = new MovieDetails();
                movieDetails.setOverview(cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_OVERVIEW)));
                movieDetails.setPosterPath(cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_POSTER_PATH)));

                updateOnUI(movieDetails);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    /**
     * Update movie details on UI.
     *
     * @param movieDetails
     */
    private void updateOnUI(MovieDetails movieDetails) {

        if(movieDetails == null){
            return;
        }

        overviewTextView.setText(movieDetails.getOverview());

        String imageURL = AppConstants.MOVIE_POSTER_IMAGE_W342_BASE_URL + movieDetails.getPosterPath();

        Picasso.with(getActivity())
                .load(imageURL)
                .placeholder(R.drawable.ic_maps_local_movies)
                .error(R.drawable.ic_alert_error)
                .into(moviePosterImageView);
    }
}
