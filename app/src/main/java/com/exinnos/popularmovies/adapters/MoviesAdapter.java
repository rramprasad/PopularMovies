package com.exinnos.popularmovies.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.exinnos.popularmovies.R;
import com.exinnos.popularmovies.database.MoviesContract;
import com.exinnos.popularmovies.util.AppConstants;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by RAMPRASAD on 2/7/2016.
 * Adapter class for Movies.
 */
public class MoviesAdapter extends CursorRecyclerViewAdapter<MoviesAdapter.MovieViewHolder> {

    private final OnMovieClickListener mMovieClickListener;
    private Context context;

    public MoviesAdapter(Context context, Cursor cursor, OnMovieClickListener onMovieClickListener) {
        super(context, cursor);

        this.context = context;
        this.mMovieClickListener = onMovieClickListener;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_grid_item,parent,false);

        MovieViewHolder movieViewHolder = new MovieViewHolder(view);
        return movieViewHolder;
    }

    @Override
    public void onBindViewHolder(MovieViewHolder customViewHolder, Cursor cursor) {
        String imageURL = AppConstants.MOVIE_POSTER_IMAGE_BASE_URL + cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_POSTER_PATH));

        Picasso.with(context)
                .load(imageURL)
                .placeholder(R.drawable.ic_maps_local_movies)
                .error(R.drawable.ic_alert_error)
                .into(customViewHolder.movieposterImageView);
    }

    public interface OnMovieClickListener {
        void onMovieClicked(int movieId);
    }

    /**
     * Custom view holder for a grid item.
     */
    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.movie_poster)
        protected ImageView movieposterImageView;

        public MovieViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            Cursor cursor = getCursor();

            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToPosition(getAdapterPosition());
                int movieId = cursor.getInt(cursor.getColumnIndex(MoviesContract.PopularMoviesEntry.COLUMN_MOVIE_ID));
                mMovieClickListener.onMovieClicked(movieId);
            }
        }
    }
}
