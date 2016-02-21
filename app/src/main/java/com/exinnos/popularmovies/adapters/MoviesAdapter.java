package com.exinnos.popularmovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.exinnos.popularmovies.R;
import com.exinnos.popularmovies.data.Movie;
import com.exinnos.popularmovies.util.AppConstants;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by RAMPRASAD on 2/7/2016.
 * Adapter class for Movies.
 */
public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {

    private final OnMovieClickListener mMovieClickListener;
    private Context context;
    private ArrayList<Movie> moviesArrayList;

    public MoviesAdapter(Context context, ArrayList<Movie> moviesArrayList, OnMovieClickListener onMovieClickListener) {
        this.context = context;
        this.moviesArrayList = moviesArrayList;
        this.mMovieClickListener = onMovieClickListener;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_grid_item, null);

        MovieViewHolder movieViewHolder = new MovieViewHolder(view);
        return movieViewHolder;
    }

    @Override
    public void onBindViewHolder(MovieViewHolder customViewHolder, int position) {

        String imageURL = AppConstants.MOVIE_POSTER_IMAGE_BASE_URL + moviesArrayList.get(position).getPosterPath();

        Picasso.with(context)
                .load(imageURL)
                .placeholder(android.R.color.darker_gray)
                .error(android.R.drawable.stat_notify_error)
                .into(customViewHolder.movieposterImageView);

        /*customViewHolder.movieposterImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                *//*MovieViewHolder movieViewHolder = (MovieViewHolder) view.getTag();
                int position = movieViewHolder.getPosition();

                Movie movie = moviesArrayList.get(position);
                Snackbar.make(view,movie.getTitle(),Snackbar.LENGTH_SHORT).show();*//*
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return ((moviesArrayList != null) ? moviesArrayList.size() : 0);
    }

    public interface OnMovieClickListener {
        void onMovieClicked(int movieId);
    }

    /**
     * Custom view holder for a grid item.
     */
    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected ImageView movieposterImageView;

        public MovieViewHolder(View itemView) {
            super(itemView);
            this.movieposterImageView = (ImageView) itemView.findViewById(R.id.movie_poster);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            int adapterPosition = getAdapterPosition();
            int movieId = moviesArrayList.get(adapterPosition).getMovieId();

            mMovieClickListener.onMovieClicked(movieId);
        }
    }
}
