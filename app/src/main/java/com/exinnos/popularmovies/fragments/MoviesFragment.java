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
import com.exinnos.popularmovies.data.MoviesData;
import com.exinnos.popularmovies.network.MoviesAPIService;
import com.exinnos.popularmovies.util.AppConstants;
import com.exinnos.popularmovies.util.AppUtilities;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import java.util.ArrayList;
import java.util.List;

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
 *         Fragment for Movies list.
 */
public class MoviesFragment extends Fragment {

    private static final String SORT_ORDER_POPULARITY_DESC = "popularity.desc";
    private static final String SORT_ORDER_VOTE_AVERAGE_DESC = "vote_average.desc";
    private static final String LOG_TAG = "MoviesFragment";
    private OnMoviesFragmentListener mListener;
    private View rootView;

    @Bind(R.id.recyclerview_for_movies)
    RecyclerView moviesRecyclerView;

    private GridLayoutManager moviesGridLayoutManager;
    private ArrayList<Movie> moviesArrayList;
    private MoviesAdapter moviesAdapter;

    //@Bind(R.id.toolbar)
    Toolbar toolbar;

    //@Bind(R.id.movie_type_spinner)
    AppCompatSpinner moviesTypeSpinner;

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

        ButterKnife.bind(this,rootView);

        //moviesRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_for_movies);

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
            fetchMoviesFromServer(sortby);
        } else {
            Snackbar.make(rootView, getActivity().getResources().getString(R.string.network_connection_not_available), Snackbar.LENGTH_SHORT).show();
        }
    }

    /**
     * Fetch movies data from server.
     * @param sortby
     */
    private void fetchMoviesFromServer(String sortby) {

        OkHttpClient httpClient = new OkHttpClient.Builder().addNetworkInterceptor(new StethoInterceptor()).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConstants.MOVIES_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build();

        MoviesAPIService moviesAPIService = retrofit.create(MoviesAPIService.class);

        Call<MoviesData> moviesDataCall = moviesAPIService.fetchMoviesData(sortby, BuildConfig.THE_MOVIE_DB_API_KEY);

        moviesDataCall.enqueue(new Callback<MoviesData>() {

            @Override
            public void onResponse(Call<MoviesData> call, Response<MoviesData> response) {

                Log.i(LOG_TAG,"on success "+response.isSuccess());

                if(response != null){
                    List<Movie> movies = response.body().getMovies();
                    moviesArrayList.clear();
                    moviesArrayList.addAll(movies);
                }

                moviesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<MoviesData> call, Throwable t) {
                Log.i(LOG_TAG,"Retrofit movies service on failure "+t.getMessage().toString());
            }
        });

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
}
