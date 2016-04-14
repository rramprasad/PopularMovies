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

    private static boolean twoPane = false;
    private final OnMovieClickListener mMovieClickListener;
    private Context context;
    private int mSelectedPosition = -1;
    private int selectedPosition;

    public MoviesAdapter(Context context, Cursor cursor, OnMovieClickListener onMovieClickListener) {
        super(context, cursor);

        this.context = context;
        this.mMovieClickListener = onMovieClickListener;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_grid_item, parent, false);

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

        if (customViewHolder.movie_poster_selection != null) {
            if (mSelectedPosition == cursor.getPosition()) {
                customViewHolder.movie_poster_selection.setVisibility(View.VISIBLE);
            } else {
                customViewHolder.movie_poster_selection.setVisibility(View.GONE);
            }
        }
    }

    public int getSelectedPosition() {
        return mSelectedPosition;
    }

    public void setSelectedPosition(int selectedPosition) {
        mSelectedPosition = selectedPosition;
        notifyDataSetChanged();
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

        //@Bind(R.id.movie_poster_selection)
        protected ImageView movie_poster_selection;

        public MovieViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            movie_poster_selection = (ImageView) itemView.findViewById(R.id.movie_poster_selection);

            if (movie_poster_selection != null) {
                twoPane = true;
            }

            itemView.setClickable(true);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            Cursor cursor = getCursor();

            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToPosition(getAdapterPosition());

                if (twoPane) {
                    mSelectedPosition = getAdapterPosition();
                    notifyDataSetChanged();
                }

                int movieId = cursor.getInt(cursor.getColumnIndex(MoviesContract.PopularMoviesEntry.COLUMN_MOVIE_ID));
                mMovieClickListener.onMovieClicked(movieId);
            }
        }
    }
}
