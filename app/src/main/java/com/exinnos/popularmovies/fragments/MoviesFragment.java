package com.exinnos.popularmovies.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.exinnos.popularmovies.R;

/**
 * @author RAMPRASAD
 * Fragment for Movies list.
 * Activities that contain this fragment must implement the
 * {@link MoviesFragment.OnMoviesFragmentListener} interface
 * to handle interaction events.
 */
public class MoviesFragment extends Fragment {

    private OnMoviesFragmentListener mListener;
    private View rootView;
    private RecyclerView moviesRecyclerView;

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

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMoviesFragmentListener) {
            mListener = (OnMoviesFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
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
        void onMoviesFragmentItemSelected();
    }
}
