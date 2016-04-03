package com.exinnos.popularmovies.fragments;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.exinnos.popularmovies.R;
import com.exinnos.popularmovies.adapters.MovieDetailsPagerAdapter;
import com.exinnos.popularmovies.database.MoviesContract;
import com.exinnos.popularmovies.data.MovieDetails;
import com.exinnos.popularmovies.util.AppConstants;
import com.exinnos.popularmovies.util.AppUtilities;
import com.squareup.picasso.Picasso;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author RAMPRASAD
 *         Fragment for Movie details screen.
 */
public class MovieDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String ARG_MOVIE_ID = "arg_movie_id";
    private static final String LOG_TAG = "MovieDetailFragment";
    private static final int MOVIE_DETAILS_LOADER = 200;
    private int mMovieId;
    private OnMovieDetailFragmentListener mListener;
    private View rootView;

    @Bind(R.id.movie_title_textview)
    TextView movieTitleTextView;

    @Bind(R.id.release_date_textview)
    TextView releaseDateTextView;

    @Bind(R.id.rating_textview)
    TextView ratingTextView;

    /*@Bind(R.id.overview_textview)
    TextView overviewTextView;

    @Bind(R.id.movie_poster_imageview)
    ImageView moviePosterImageView;*/

    @Bind(R.id.movie_details_viewpager)
    ViewPager movieDetailViewPager;

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
        if (getArguments() != null) {
            mMovieId = getArguments().getInt(ARG_MOVIE_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        ButterKnife.bind(this,rootView);

        MovieDetailsPagerAdapter movieDetailsPagerAdapter = new MovieDetailsPagerAdapter(getChildFragmentManager(),mMovieId);
        //movieDetailViewPager.setOffscreenPageLimit(0);
        movieDetailViewPager.setAdapter(movieDetailsPagerAdapter);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        getLoaderManager().initLoader(MOVIE_DETAILS_LOADER,null,this);
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

    /**
     * Update movie details on UI.
     *
     * @param movieDetails
     */
    private void updateOnUI(MovieDetails movieDetails) {

        if(movieDetails == null){
            return;
        }

        movieTitleTextView.setText(movieDetails.getOriginalTitle());

        releaseDateTextView.setText(AppUtilities.getFormattedDate(movieDetails.getReleaseDate()));

        ratingTextView.setText(String.format("%.1f/10", movieDetails.getVoteAverage()));

        /*overviewTextView.setText(movieDetails.getOverview());

        String imageURL = AppConstants.MOVIE_POSTER_IMAGE_W342_BASE_URL + movieDetails.getPosterPath();

        Picasso.with(getActivity())
                .load(imageURL)
                .placeholder(android.R.color.darker_gray)
                .error(android.R.drawable.stat_notify_error)
                .into(moviePosterImageView);*/
    }


    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri movieDetailsUri = MoviesContract.MoviesEntry.buildMoviesWithIdUri(mMovieId);
        return new CursorLoader(getActivity(),movieDetailsUri,null, MoviesContract.MoviesEntry._ID+"=?",new String[]{String.valueOf(mMovieId)},null);
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor cursor) {

        Log.d(LOG_TAG,"cursor count =>"+cursor.getCount());

        if(cursor != null && cursor.getCount() > 0){

            if(cursor.moveToFirst()){
                Log.d(LOG_TAG,"movie name =>"+cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_ORIGINAL_TITLE)));
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
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

    }

    /**
     * Interface to communicate with host fragment.
     */
    public interface OnMovieDetailFragmentListener {
        void onMovieDetailFragmentInteraction();
    }
}
