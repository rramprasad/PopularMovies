package com.exinnos.popularmovies.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.exinnos.popularmovies.R;
import com.exinnos.popularmovies.database.MoviesContract;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by RAMPRASAD on 4/3/2016.
 * Adapter class for movie reviews
 */
public class MovieReviewsAdapter extends CursorRecyclerViewAdapter<MovieReviewsAdapter.CustomViewHolder> {

    private Cursor cursor;
    private Context mContext;

    public MovieReviewsAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        this.mContext = context;
        this.cursor = cursor;
    }

    @Override
    public void onBindViewHolder(MovieReviewsAdapter.CustomViewHolder customViewHolder, Cursor cursor) {
        customViewHolder.reviewTextView.setText(cursor.getString(cursor.getColumnIndex(MoviesContract.MovieReviewsEntry.COLUMN_CONTENT)));
        customViewHolder.authorTextView.setText(cursor.getString(cursor.getColumnIndex(MoviesContract.MovieReviewsEntry.COLUMN_AUTHOR)));
    }

    @Override
    public MovieReviewsAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.movie_review_item, parent, false);
        CustomViewHolder customViewHolder = new CustomViewHolder(view);
        return customViewHolder;
    }


    /**
     * Custom viewholder
     */
    public class CustomViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.review_textview)
        protected TextView reviewTextView;

        @Bind(R.id.author_textview)
        protected TextView authorTextView;

        public CustomViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

    }
}
