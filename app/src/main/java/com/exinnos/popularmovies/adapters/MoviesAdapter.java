package com.exinnos.popularmovies.adapters;

import android.content.Context;
import android.support.design.widget.Snackbar;
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

    private Context context;
    private ArrayList<Movie> moviesArrayList;

    public MoviesAdapter(Context context, ArrayList<Movie> moviesArrayList) {
        this.context = context;
        this.moviesArrayList = moviesArrayList;
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
                //.centerCrop()
                .into(customViewHolder.movieposterImageView);

        customViewHolder.movieposterImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*MovieViewHolder movieViewHolder = (MovieViewHolder) view.getTag();
                int position = movieViewHolder.getPosition();

                Movie movie = moviesArrayList.get(position);
                Snackbar.make(view,movie.getTitle(),Snackbar.LENGTH_SHORT).show();*/
            }
        });
    }

    @Override
    public int getItemCount() {
        return ((moviesArrayList != null) ? moviesArrayList.size() : 0);
    }

    /**
     * Custom view holder for a grid item.
     */
    public class MovieViewHolder extends RecyclerView.ViewHolder {
        protected ImageView movieposterImageView;

        public MovieViewHolder(View itemView) {
            super(itemView);
            this.movieposterImageView = (ImageView)itemView.findViewById(R.id.movie_poster);
        }
    }
}
