package com.exinnos.popularmovies.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.exinnos.popularmovies.BuildConfig;
import com.exinnos.popularmovies.R;
import com.exinnos.popularmovies.database.MoviesContract;
import com.exinnos.popularmovies.data.MovieDetails;
import com.exinnos.popularmovies.database.MoviesDbHelper;
import com.exinnos.popularmovies.network.MoviesAPIService;
import com.exinnos.popularmovies.util.AppConstants;
import com.exinnos.popularmovies.util.AppUtilities;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author RAMPRASAD
 *         Fragment for Movie details screen.
 */
public class MovieDetailFragment extends Fragment {

    private static final String ARG_MOVIE_ID = "arg_movie_id";
    private static final String LOG_TAG = "MovieDetailFragment";
    private int mMovieId;
    private OnMovieDetailFragmentListener mListener;
    private View rootView;

    @Bind(R.id.movie_title_textview)
    TextView movieTitleTextView;

    @Bind(R.id.release_date_textview)
    TextView releaseDateTextView;

    @Bind(R.id.rating_textview)
    TextView ratingTextView;

    @Bind(R.id.overview_textview)
    TextView overviewTextView;

    @Bind(R.id.movie_poster_imageview)
    ImageView moviePosterImageView;

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

        //movieTitleTextView = (TextView) rootView.findViewById(R.id.movie_title_textview);
        //releaseDateTextView = (TextView) rootView.findViewById(R.id.release_date_textview);
        //ratingTextView = (TextView) rootView.findViewById(R.id.rating_textview);
        //overviewTextView = (TextView) rootView.findViewById(R.id.overview_textview);
        //moviePosterImageView = (ImageView) rootView.findViewById(R.id.movie_poster_imageview);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (AppUtilities.isNetworkConnected(getActivity())) {
            fetechMovieDetailsFromServer(mMovieId);
        } else {
            Snackbar.make(rootView, getActivity().getResources().getString(R.string.network_connection_not_available), Snackbar.LENGTH_SHORT).show();
        }
    }

    /**
     * Fetch movie details from server.
     * @param mMovieId
     */
    private void fetechMovieDetailsFromServer(int mMovieId) {

        OkHttpClient httpClient = new OkHttpClient.Builder().addNetworkInterceptor(new StethoInterceptor()).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConstants.MOVIE_DETAILS_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build();

        MoviesAPIService moviesAPIService = retrofit.create(MoviesAPIService.class);

        Call<MovieDetails> movieDetailsCall = moviesAPIService.fetchMoviesDetails(mMovieId, BuildConfig.THE_MOVIE_DB_API_KEY);

        movieDetailsCall.enqueue(new Callback<MovieDetails>() {
            @Override
            public void onResponse(Call<MovieDetails> call, Response<MovieDetails> response) {

                Log.i(LOG_TAG,"on success "+response.isSuccess());

                if (response != null) {
                    MovieDetails movieDetails = response.body();

                    MoviesDbHelper moviesDbHelper = new MoviesDbHelper(getActivity());
                    SQLiteDatabase moviesDatabase = moviesDbHelper.getWritableDatabase();

                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MoviesContract.MoviesEntry._ID,movieDetails.getId());
                    contentValues.put(MoviesContract.MoviesEntry.COLUMN_ADULT,movieDetails.getAdult());
                    contentValues.put(MoviesContract.MoviesEntry.COLUMN_BACKDROP_PATH,movieDetails.getBackdropPath());
                    contentValues.put(MoviesContract.MoviesEntry.COLUMN_BUDGET,movieDetails.getBudget());
                    contentValues.put(MoviesContract.MoviesEntry.COLUMN_HOME_PAGE,movieDetails.getHomepage());
                    contentValues.put(MoviesContract.MoviesEntry.COLUMN_IMDB_ID,movieDetails.getImdbId());
                    contentValues.put(MoviesContract.MoviesEntry.COLUMN_ORIGINAL_LANGUAGE,movieDetails.getOriginalLanguage());
                    contentValues.put(MoviesContract.MoviesEntry.COLUMN_ORIGINAL_TITLE,movieDetails.getOriginalTitle());
                    contentValues.put(MoviesContract.MoviesEntry.COLUMN_OVERVIEW,movieDetails.getOverview());
                    contentValues.put(MoviesContract.MoviesEntry.COLUMN_POPULARITY,movieDetails.getPopularity());
                    contentValues.put(MoviesContract.MoviesEntry.COLUMN_POSTER_PATH,movieDetails.getPosterPath());
                    contentValues.put(MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE,movieDetails.getReleaseDate());
                    contentValues.put(MoviesContract.MoviesEntry.COLUMN_REVENUE,movieDetails.getRevenue());
                    contentValues.put(MoviesContract.MoviesEntry.COLUMN_RUNTIME,movieDetails.getRuntime());
                    contentValues.put(MoviesContract.MoviesEntry.COLUMN_STATUS,movieDetails.getStatus());
                    contentValues.put(MoviesContract.MoviesEntry.COLUMN_TAGLINE,movieDetails.getTagline());
                    contentValues.put(MoviesContract.MoviesEntry.COLUMN_VIDEO,movieDetails.getVideo());
                    contentValues.put(MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE,movieDetails.getVoteAverage());
                    contentValues.put(MoviesContract.MoviesEntry.COLUMN_VOTE_COUNT,movieDetails.getVoteCount());

                    moviesDatabase.insert(MoviesContract.MoviesEntry.TABLE_NAME,null,contentValues);

                    // Update on UI
                    updateOnUI(movieDetails);

                } else {
                    Snackbar.make(rootView, "Oops something went wrong.Try again.", Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MovieDetails> call, Throwable t) {
                Log.i(LOG_TAG,"Retrofit movies service on failure "+t.getMessage().toString());
            }
        });
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

        movieTitleTextView.setText(movieDetails.getTitle());

        releaseDateTextView.setText(AppUtilities.getFormattedDate(movieDetails.getReleaseDate()));

        ratingTextView.setText(String.format("%.1f/10", movieDetails.getVoteAverage()));

        overviewTextView.setText(movieDetails.getOverview());

        String imageURL = AppConstants.MOVIE_POSTER_IMAGE_W342_BASE_URL + movieDetails.getPosterPath();

        Picasso.with(getActivity())
                .load(imageURL)
                .placeholder(android.R.color.darker_gray)
                .error(android.R.drawable.stat_notify_error)
                .into(moviePosterImageView);
    }

    /**
     * Interface to communicate with host fragment.
     */
    public interface OnMovieDetailFragmentListener {
        void onMovieDetailFragmentInteraction();
    }
}
