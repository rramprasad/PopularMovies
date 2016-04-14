package com.exinnos.popularmovies.fragments;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.exinnos.popularmovies.R;
import com.exinnos.popularmovies.adapters.MovieReviewsAdapter;
import com.exinnos.popularmovies.database.MoviesContract;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class MovieReviewsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String ARG_MOVIE_ID = "arg_movie_id";
    private static final int MOVIE_REVIEWS_LOADER = 600;
    private static final String LOG_TAG = MovieReviewsFragment.class.getSimpleName();
    @Bind(R.id.recyclerview_for_movie_reviews)
    RecyclerView movieReviewsRecyclerView;
    private int mMovieId;
    private MovieReviewsAdapter movieReviewsAdapter;

    public MovieReviewsFragment() {
        // Required empty public constructor
    }

    public static MovieReviewsFragment newInstance(int movieId) {
        MovieReviewsFragment movieReviewsFragment = new MovieReviewsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_MOVIE_ID, movieId);
        movieReviewsFragment.setArguments(args);
        return movieReviewsFragment;
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
        View rootView = inflater.inflate(R.layout.fragment_movie_reviews, container, false);

        ButterKnife.bind(this, rootView);

        movieReviewsRecyclerView.setHasFixedSize(true);

        movieReviewsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        movieReviewsAdapter = new MovieReviewsAdapter(getActivity(), null);

        movieReviewsRecyclerView.setAdapter(movieReviewsAdapter);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(MOVIE_REVIEWS_LOADER, null, this);
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri movieReviewsUri = MoviesContract.MovieReviewsEntry.buildMovieReviewsUri();
        return new CursorLoader(getActivity(), movieReviewsUri, null, MoviesContract.MovieReviewsEntry.COLUMN_MOVIE_ID + "=?", new String[]{String.valueOf(mMovieId)}, null);
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor cursor) {
        movieReviewsAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
        movieReviewsAdapter.swapCursor(null);
    }

}
