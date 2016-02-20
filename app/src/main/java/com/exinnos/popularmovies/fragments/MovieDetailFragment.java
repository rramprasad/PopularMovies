package com.exinnos.popularmovies.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
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
import com.exinnos.popularmovies.data.Movie;
import com.exinnos.popularmovies.util.AppConstants;
import com.exinnos.popularmovies.util.AppUtilities;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author RAMPRASAD
 * Fragment for Movie details screen.
 * Activities that contain this fragment must implement the
 * {@link MovieDetailFragment.OnMovieDetailFragmentListener} interface
 * to handle interaction events.
 * Use the {@link MovieDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MovieDetailFragment extends Fragment {

    private static final String ARG_MOVIE_ID= "arg_movie_id";

    private int mMovieId;

    private OnMovieDetailFragmentListener mListener;
    private View rootView;
    private TextView movieTitleTextView;
    private TextView releaseDateTextView;
    private TextView ratingTextView;
    private TextView overviewTextView;
    private ImageView moviePosterImageView;

    public MovieDetailFragment() {
        // Required empty public constructor
    }

    public static MovieDetailFragment newInstance(int movieId) {
        MovieDetailFragment fragment = new MovieDetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_MOVIE_ID,movieId);
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

        movieTitleTextView = (TextView) rootView.findViewById(R.id.movie_title_textview);
        releaseDateTextView = (TextView) rootView.findViewById(R.id.release_date_textview);
        ratingTextView = (TextView) rootView.findViewById(R.id.rating_textview);
        overviewTextView = (TextView) rootView.findViewById(R.id.overview_textview);
        moviePosterImageView = (ImageView) rootView.findViewById(R.id.movie_poster_imageview);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        if(AppUtilities.isNetworkConnected(getActivity())){
            new FetchMovieDetailsAsyncTask().execute(mMovieId);
        }
        else{
            Snackbar.make(rootView,getActivity().getResources().getString(R.string.network_connection_not_available),Snackbar.LENGTH_SHORT).show();
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

    /**
     * Interface to communicate with host fragment.
     */
    public interface OnMovieDetailFragmentListener {
        void onMovieDetailFragmentInteraction();
    }

    /**
     * Async Task to fetch Movie details on background.
     */
    private class FetchMovieDetailsAsyncTask extends AsyncTask<Integer, Void, Movie> {

        private final String LOG_TAG = FetchMovieDetailsAsyncTask.class.getSimpleName();

        @Override
        protected Movie doInBackground(Integer... params) {
            // If no params given,return.
            if(params.length == 0){
                return null;
            }

            int movieId = params[0];

            HttpURLConnection httpURLConnection = null;
            BufferedReader bufferedReader = null;
            String responseJSONString = null;

            try {
                final String API_KEY_PARAM = "api_key";

                Uri.Builder uriBuilder = Uri.parse(AppConstants.MOVIE_DETAILS_BASE_URL+"/"+movieId).buildUpon();
                uriBuilder.appendQueryParameter(API_KEY_PARAM, BuildConfig.THE_MOVIE_DB_API_KEY);
                Uri uri = uriBuilder.build();

                URL url = new URL(uri.toString());

                //Open the connection
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();


                // Read response from input stream.
                InputStream inputStream = httpURLConnection.getInputStream();

                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                StringBuffer responseStringBuffer = new StringBuffer();

                String responseLine;
                while ((responseLine=bufferedReader.readLine()) != null){
                    responseStringBuffer.append(responseLine);
                }

                // If no response available,return.
                if(responseStringBuffer.length() == 0){
                    return null;
                }

                // Response json string
                responseJSONString = responseStringBuffer.toString();

                Movie movie = parseMoviesDetailsJSON(responseJSONString);

                return movie;

            } catch (MalformedURLException e) {
                Log.e(LOG_TAG,"MalformedURLException on URL",e);
                return null;
            } catch (IOException e) {
                Log.e(LOG_TAG,"Error",e);
                return null;
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(LOG_TAG,"Json parse error",e);
                return null;
            } finally {

                if(httpURLConnection != null){
                    httpURLConnection.disconnect();
                }

                if(bufferedReader != null){
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        Log.e(LOG_TAG,"exception while closing buffered reader",e);
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(Movie movie) {
            super.onPostExecute(movie);

            if(movie != null){
                updateOnUI(movie);
            }
            else{
                Snackbar.make(rootView,"Oops something went wrong.Try again.",Snackbar.LENGTH_SHORT).show();
            }

        }

        /**
         * Parse given response JSON String.
         * @param responseJSONString
         * @throws JSONException
         */
        private Movie parseMoviesDetailsJSON(String responseJSONString) throws JSONException{

            final String KEY_POSTER_PATH = "poster_path";
            final String KEY_OVERVIEW = "overview";
            final String KEY_RELEASE_DATE = "release_date";
            final String KEY_ID = "id";
            final String KEY_TITLE = "original_title";
            final String KEY_VOTE_AVERAGE = "vote_average";


            JSONObject movieJsonObject = new JSONObject(responseJSONString);

            String posterPath = movieJsonObject.getString(KEY_POSTER_PATH);
            String overView = movieJsonObject.getString(KEY_OVERVIEW);
            String releaseDate = movieJsonObject.getString(KEY_RELEASE_DATE);
            int movieId = movieJsonObject.getInt(KEY_ID);
            String keyTitle = movieJsonObject.getString(KEY_TITLE);
            double voteAverage = movieJsonObject.getDouble(KEY_VOTE_AVERAGE);

            Movie movie = new Movie();
            movie.setPosterPath(posterPath);
            movie.setOverView(overView);
            movie.setReleaseDate(releaseDate);
            movie.setMovieId(movieId);
            movie.setTitle(keyTitle);
            movie.setVoteAverage(voteAverage);

            return movie;
        }
    }

    /**
     * Update movie details on UI.
     * @param movie
     */
    private void updateOnUI(Movie movie) {

        movieTitleTextView.setText(movie.getTitle());

        releaseDateTextView.setText(AppUtilities.getFormattedDate(movie.getReleaseDate()));

        ratingTextView.setText(String.format("%.1f/10",movie.getVoteAverage()));

        overviewTextView.setText(movie.getOverView());

        String imageURL = AppConstants.MOVIE_POSTER_IMAGE_W342_BASE_URL + movie.getPosterPath();

        //String imageURL = AppConstants.MOVIE_POSTER_IMAGE_BASE_URL + movie.getPosterPath();

        //String imageURL = AppConstants.MOVIE_POSTER_IMAGE_W780_BASE_URL + movie.getPosterPath();

        Picasso.with(getActivity())
                .load(imageURL)
                //.fit()
                .placeholder(android.R.color.darker_gray)
                .error(android.R.drawable.stat_notify_error)
                .into(moviePosterImageView);
    }
}
