package com.exinnos.popularmovies.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.exinnos.popularmovies.BuildConfig;
import com.exinnos.popularmovies.R;
import com.exinnos.popularmovies.adapters.MoviesAdapter;
import com.exinnos.popularmovies.data.Movie;
import com.exinnos.popularmovies.util.AppConstants;
import com.exinnos.popularmovies.util.AppUtilities;

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
import java.util.ArrayList;

/**
 * @author RAMPRASAD
 *         Fragment for Movies list.
 */
public class MoviesFragment extends Fragment {

    private static final String SORT_ORDER_POPULARITY_DESC = "popularity.desc";
    private static final String SORT_ORDER_VOTE_AVERAGE_DESC = "vote_average.desc";
    private OnMoviesFragmentListener mListener;
    private View rootView;
    private RecyclerView moviesRecyclerView;
    private GridLayoutManager moviesGridLayoutManager;
    private ArrayList<Movie> moviesArrayList;
    private MoviesAdapter moviesAdapter;
    private Toolbar toolbar;
    private AppCompatSpinner moviesTypeSpinner;
    private String sortByArray[] = {"Most popular", "Highest Rated"};

    public MoviesFragment() {
        // Required empty public constructor
    }

    /**
     * Factory method to create a new instance of
     * this fragment.
     */
    public static MoviesFragment newInstance() {
        MoviesFragment fragment = new MoviesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_movies, container, false);

        moviesRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_for_movies);

        moviesRecyclerView.setHasFixedSize(true);

        moviesGridLayoutManager = new GridLayoutManager(getActivity(), 2);

        moviesRecyclerView.setLayoutManager(moviesGridLayoutManager);

        moviesArrayList = new ArrayList<>();
        moviesAdapter = new MoviesAdapter(getActivity(), moviesArrayList, new MoviesAdapter.OnMovieClickListener() {
            @Override
            public void onMovieClicked(int movieId) {
                mListener.onMovieSelected(movieId);
            }
        });

        moviesRecyclerView.setAdapter(moviesAdapter);


        return rootView;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        moviesTypeSpinner = (AppCompatSpinner) toolbar.findViewById(R.id.movie_type_spinner);

        ArrayAdapter<String> sortByArrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.simple_spinner_item, sortByArray);
        sortByArrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);

        moviesTypeSpinner.setAdapter(sortByArrayAdapter);

        moviesTypeSpinner.setPopupBackgroundResource(R.color.colorPrimary);

        moviesTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                switch (position) {
                    case 0:
                        loadMovies(SORT_ORDER_POPULARITY_DESC);
                        break;
                    case 1:
                        loadMovies(SORT_ORDER_VOTE_AVERAGE_DESC);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // do nothing
            }
        });

        int selectedItemPosition = moviesTypeSpinner.getSelectedItemPosition();
        switch (selectedItemPosition) {
            case 0:
                loadMovies(SORT_ORDER_POPULARITY_DESC);
                break;
            case 1:
                loadMovies(SORT_ORDER_VOTE_AVERAGE_DESC);
                break;
        }

        super.onActivityCreated(savedInstanceState);
    }

    /**
     * Load movies in background
     *
     * @param sortby
     */
    private void loadMovies(String sortby) {
        if (AppUtilities.isNetworkConnected(getActivity())) {
            new FetchMoviesAsyncTask().execute(sortby);
        } else {
            Snackbar.make(rootView, getActivity().getResources().getString(R.string.network_connection_not_available), Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMoviesFragmentListener) {
            mListener = (OnMoviesFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnMoviesFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Interface to communicate with host Activity.
     */
    public interface OnMoviesFragmentListener {
        void onMovieSelected(int movieId);
    }

    /**
     * Async task to fetch movies based on given sort by parameter.
     */
    private class FetchMoviesAsyncTask extends AsyncTask<String, Void, Movie[]> {

        private final String LOG_TAG = FetchMoviesAsyncTask.class.getSimpleName();
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Updating...");
            progressDialog.show();
        }

        @Override
        protected Movie[] doInBackground(String... params) {
            // If no params given,return.
            if (params.length == 0) {
                return null;
            }

            HttpURLConnection httpURLConnection = null;
            BufferedReader bufferedReader = null;
            String responseJSONString = null;

            try {
                final String SORY_BY_PARAM = "sort_by";
                final String API_KEY_PARAM = "api_key";

                Uri.Builder uriBuilder = Uri.parse(AppConstants.MOVIES_BASE_URL).buildUpon();
                uriBuilder.appendQueryParameter(SORY_BY_PARAM, params[0]);
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
                while ((responseLine = bufferedReader.readLine()) != null) {
                    responseStringBuffer.append(responseLine);
                }

                // If no response available,return.
                if (responseStringBuffer.length() == 0) {
                    return null;
                }

                // Response json string
                responseJSONString = responseStringBuffer.toString();

                Movie[] movies = parseMoviesJSON(responseJSONString);

                return movies;

            } catch (MalformedURLException e) {
                Log.e(LOG_TAG, "MalformedURLException on URL", e);
                return null;
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error", e);
                return null;
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(LOG_TAG, "Json parse error", e);
                return null;
            } finally {

                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }

                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        Log.e(LOG_TAG, "exception while closing buffered reader", e);
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(Movie[] moviesArray) {
            super.onPostExecute(moviesArray);

            if (moviesArray != null) {

                moviesArrayList.clear();
                for (Movie movie : moviesArray) {
                    moviesArrayList.add(movie);
                }

                moviesAdapter.notifyDataSetChanged();
            }

            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }

        /**
         * Parse given response JSON String.
         *
         * @param responseJSONString
         * @throws JSONException
         */
        private Movie[] parseMoviesJSON(String responseJSONString) throws JSONException {

            final String KEY_RESULTS = "results";
            final String KEY_POSTER_PATH = "poster_path";
            final String KEY_OVERVIEW = "overview";
            final String KEY_RELEASE_DATE = "release_date";
            final String KEY_ID = "id";
            final String KEY_TITLE = "original_title";
            final String KEY_VOTE_AVERAGE = "vote_average";


            JSONObject moviesJsonObject = new JSONObject(responseJSONString);

            JSONArray moviesResultsJSONArray = moviesJsonObject.getJSONArray(KEY_RESULTS);

            int moviesLength = moviesResultsJSONArray.length();

            Movie[] moviesArray = new Movie[moviesLength];

            for (int i = 0; i < moviesResultsJSONArray.length(); i++) {
                JSONObject movieJsonObject = moviesResultsJSONArray.getJSONObject(i);
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

                moviesArray[i] = movie;
            }

            return moviesArray;
        }
    }
}
