package com.exinnos.popularmovies.fragments;

import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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

import com.exinnos.popularmovies.R;
import com.exinnos.popularmovies.adapters.MoviesAdapter;
import com.exinnos.popularmovies.data.Movie;
import com.exinnos.popularmovies.database.MoviesContract;
import com.exinnos.popularmovies.util.AppUtilities;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author RAMPRASAD
 *         Fragment for Movies list.
 */
public class MoviesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = "MoviesFragment";
    private static final int POPULAR_MOVIES_LOADER = 1;
    private static final int HIGHEST_RATED_MOVIES_LOADER = 2;
    private static final int FAVORITE_MOVIES_LOADER = 3;

    private static final String[] POPULAR_MOVIES_COLUMNS = {MoviesContract.PopularMoviesEntry.TABLE_NAME + "." + MoviesContract.PopularMoviesEntry._ID, MoviesContract.PopularMoviesEntry.COLUMN_MOVIE_ID, MoviesContract.MoviesEntry.COLUMN_POSTER_PATH};

    private static final String[] HIGHEST_RATED_MOVIES_COLUMNS = {MoviesContract.HighestRatedMoviesEntry.TABLE_NAME + "." + MoviesContract.HighestRatedMoviesEntry._ID,
            MoviesContract.HighestRatedMoviesEntry.COLUMN_MOVIE_ID, MoviesContract.MoviesEntry.COLUMN_POSTER_PATH};

    private static final String[] FAVORITE_MOVIES_COLUMNS = {MoviesContract.FavoriteMoviesEntry.TABLE_NAME + "." + MoviesContract.FavoriteMoviesEntry._ID,
            MoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_ID, MoviesContract.MoviesEntry.COLUMN_POSTER_PATH};
    private static final String KEY_MOVIE_CURRENT_POSITION = "key_movie_current_position";
    @Bind(R.id.recyclerview_for_movies)
    RecyclerView moviesRecyclerView;
    Toolbar toolbar;
    AppCompatSpinner moviesTypeSpinner;
    private OnMoviesFragmentListener mListener;
    private View rootView;
    private GridLayoutManager moviesGridLayoutManager;
    private ArrayList<Movie> moviesArrayList;
    private MoviesAdapter moviesAdapter;
    private String sortByArray[] = {"Most popular", "Highest Rated", "My Favorite"};
    private int mSelectedMoviePosition = -1;
    //private int previousPosition = -1;
    //private int mConfigChanged = 0;
    //private int selectedItemPosition;

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
        setHasOptionsMenu(true);

        //setRetainInstance(true);

        if (savedInstanceState != null) {
            mSelectedMoviePosition = savedInstanceState.getInt(KEY_MOVIE_CURRENT_POSITION);
            //mConfigChanged = 1;
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_movies, container, false);

        ButterKnife.bind(this, rootView);

        moviesRecyclerView.setHasFixedSize(true);

        if (AppUtilities.getDeviceOrientation(getActivity()) == Configuration.ORIENTATION_PORTRAIT) {
            moviesGridLayoutManager = new GridLayoutManager(getActivity(), 2);
        } else {
            moviesGridLayoutManager = new GridLayoutManager(getActivity(), 3);
        }


        moviesRecyclerView.setLayoutManager(moviesGridLayoutManager);

        moviesArrayList = new ArrayList<>();

        moviesAdapter = new MoviesAdapter(getActivity(), null, new MoviesAdapter.OnMovieClickListener() {
            @Override
            public void onMovieClicked(int movieId) {
                mListener.onMovieSelected(movieId);
            }
        });

        moviesRecyclerView.setAdapter(moviesAdapter);

        moviesAdapter.setSelectedPosition(mSelectedMoviePosition);

        return rootView;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        moviesTypeSpinner = (AppCompatSpinner) toolbar.findViewById(R.id.movie_type_spinner);

        // Initialize movies sync adapter
        //MoviesSyncAdapter.initializeSyncAdapter(getActivity());

        ArrayAdapter<String> sortByArrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.simple_spinner_item, sortByArray);
        sortByArrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);

        moviesTypeSpinner.setAdapter(sortByArrayAdapter);

        moviesTypeSpinner.setPopupBackgroundResource(R.color.colorPrimary);


        // On spinner item change listener
        moviesTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                //moviesAdapter.setSelectedPosition(mSelectedMoviePosition);

                //mSelectedMoviePosition = -1;

                /*if(mConfigChanged <= 0){
                    moviesAdapter.setSelectedPosition(-1);
                    mSelectedMoviePosition = -1;
                    moviesRecyclerView.scrollToPosition(0);
                }

                mConfigChanged = -1;*/

                /*if(previousPosition != -1){
                    moviesAdapter.setSelectedPosition(-1);
                    mSelectedMoviePosition = -1;
                    moviesRecyclerView.scrollToPosition(0);
                }

                previousPosition = position;*/

                switch (position) {
                    case 0:
                        //getLoaderManager().initLoader(POPULAR_MOVIES_LOADER,null,MoviesFragment.this);
                        initOrRestartLoader(POPULAR_MOVIES_LOADER, null, MoviesFragment.this);
                        break;
                    case 1:
                        //getLoaderManager().initLoader(HIGHEST_RATED_MOVIES_LOADER,null,MoviesFragment.this);
                        initOrRestartLoader(HIGHEST_RATED_MOVIES_LOADER, null, MoviesFragment.this);
                        break;
                    case 2:
                        //getLoaderManager().initLoader(FAVORITE_MOVIES_LOADER,null,MoviesFragment.this);
                        initOrRestartLoader(FAVORITE_MOVIES_LOADER, null, MoviesFragment.this);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // do nothing
            }
        });


        // Load movies on startup
        /*int selectedItemPosition = moviesTypeSpinner.getSelectedItemPosition();
        switch (selectedItemPosition) {
            case 0:
                getLoaderManager().initLoader(POPULAR_MOVIES_LOADER,null,this);
                //initOrRestartLoader(POPULAR_MOVIES_LOADER,null,this);
                break;
            case 1:
                getLoaderManager().initLoader(HIGHEST_RATED_MOVIES_LOADER,null,this);
                //initOrRestartLoader(HIGHEST_RATED_MOVIES_LOADER,null,this);
                break;
            case 2:
                getLoaderManager().initLoader(FAVORITE_MOVIES_LOADER,null,this);
                //initOrRestartLoader(FAVORITE_MOVIES_LOADER,null,this);
                break;
        }*/

        super.onActivityCreated(savedInstanceState);
    }

    private void initOrRestartLoader(int loaderId, Bundle args, LoaderManager.LoaderCallbacks callbacks) {

        if (getLoaderManager().getLoader(loaderId) == null) {
            getLoaderManager().initLoader(loaderId, args, callbacks);
        } else {
            getLoaderManager().restartLoader(loaderId, args, callbacks);
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        mSelectedMoviePosition = moviesAdapter.getSelectedPosition();
        outState.putInt(KEY_MOVIE_CURRENT_POSITION, mSelectedMoviePosition);
        super.onSaveInstanceState(outState);
    }

    /*@Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        mConfigChanged = 1;
    }*/

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {

        if (id == POPULAR_MOVIES_LOADER) {
            String sortOrder = MoviesContract.MoviesEntry.COLUMN_POPULARITY + " DESC";

            Uri popularMoviesUri = MoviesContract.PopularMoviesEntry.buildPopularMoviesUri();

            return new CursorLoader(getActivity(), popularMoviesUri, POPULAR_MOVIES_COLUMNS, null, null, sortOrder);
        } else if (id == HIGHEST_RATED_MOVIES_LOADER) {

            String sortOrder = MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE + " DESC";

            Uri highestRatedMoviesUri = MoviesContract.HighestRatedMoviesEntry.buildHighestRatedMoviesUri();

            return new CursorLoader(getActivity(), highestRatedMoviesUri, HIGHEST_RATED_MOVIES_COLUMNS, null, null, sortOrder);
        } else if (id == FAVORITE_MOVIES_LOADER) {


            Uri favoriteMoviesUri = MoviesContract.FavoriteMoviesEntry.buildFavoriteMoviesUri();

            return new CursorLoader(getActivity(), favoriteMoviesUri, FAVORITE_MOVIES_COLUMNS, null, null, null);
        }


        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        int selectedItemPosition = moviesTypeSpinner.getSelectedItemPosition();

        Log.d(LOG_TAG, "onLoadFinished");
        Log.d(LOG_TAG, "selectedItemPosition =>" + selectedItemPosition);

        if (loader.getId() == POPULAR_MOVIES_LOADER && selectedItemPosition == 0) {
            Log.d(LOG_TAG, "POPULAR_MOVIES_LOADER mSelectedMoviePosition =>" + mSelectedMoviePosition);
            moviesAdapter.swapCursor(cursor);
            moviesAdapter.setSelectedPosition(mSelectedMoviePosition);
        } else if (loader.getId() == HIGHEST_RATED_MOVIES_LOADER && selectedItemPosition == 1) {
            Log.d(LOG_TAG, "HIGHEST_RATED_MOVIES_LOADER mSelectedMoviePosition=>" + mSelectedMoviePosition);
            moviesAdapter.swapCursor(cursor);
            moviesAdapter.setSelectedPosition(mSelectedMoviePosition);
        } else if (loader.getId() == FAVORITE_MOVIES_LOADER && selectedItemPosition == 2) {
            Log.d(LOG_TAG, "FAVORITE_MOVIES_LOADER mSelectedMoviePosition=>" + mSelectedMoviePosition);
            moviesAdapter.swapCursor(cursor);
            moviesAdapter.setSelectedPosition(mSelectedMoviePosition);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        moviesAdapter.swapCursor(null);
    }

    /**
     * Interface to communicate with host Activity.
     */
    public interface OnMoviesFragmentListener {
        void onMovieSelected(int movieId);
    }
}
