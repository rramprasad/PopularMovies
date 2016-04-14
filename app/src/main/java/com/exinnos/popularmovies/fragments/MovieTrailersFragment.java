package com.exinnos.popularmovies.fragments;


import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.exinnos.popularmovies.R;
import com.exinnos.popularmovies.adapters.MovieTrailersAdapter;
import com.exinnos.popularmovies.database.MoviesContract;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Adapter for Movie trailers
 */
public class MovieTrailersFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {


    private static final String ARG_MOVIE_ID = "arg_movie_id";
    private static final int MOVIE_TRAILERS_LOADER = 500;
    private static final String LOG_TAG = MovieTrailersFragment.class.getSimpleName();
    @Bind(R.id.recyclerview_for_movie_trailers)
    RecyclerView movieTrailersRecyclerView;
    private int mMovieId;
    private MovieTrailersAdapter movieTrailersAdapter;

    public MovieTrailersFragment() {
        // Required empty public constructor
    }

    public static MovieTrailersFragment newInstance(int movieId) {
        MovieTrailersFragment movieTrailersFragment = new MovieTrailersFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_MOVIE_ID, movieId);
        movieTrailersFragment.setArguments(args);
        return movieTrailersFragment;
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
        View rootView = inflater.inflate(R.layout.fragment_movie_trailers, container, false);

        ButterKnife.bind(this, rootView);

        movieTrailersRecyclerView.setHasFixedSize(true);

        movieTrailersRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        movieTrailersAdapter = new MovieTrailersAdapter(getActivity(), null, new MovieTrailersAdapter.OnTrailerClickListener() {
            @Override
            public void onTrailerClicked(String trailerId) {
                playTrailerOnYoutube(trailerId);
            }
        });

        movieTrailersRecyclerView.setAdapter(movieTrailersAdapter);

        return rootView;
    }

    /**
     * Play trailer
     *
     * @param trailerId
     */
    private void playTrailerOnYoutube(String trailerId) {

        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + trailerId));
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + trailerId));
            startActivity(intent);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(MOVIE_TRAILERS_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri movieTrailersUri = MoviesContract.MovieTrailersEntry.buildMovieTrailersUri();
        return new CursorLoader(getActivity(), movieTrailersUri, null, MoviesContract.MovieTrailersEntry.COLUMN_MOVIE_ID + "=?", new String[]{String.valueOf(mMovieId)}, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        movieTrailersAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        movieTrailersAdapter.swapCursor(null);
    }
}
