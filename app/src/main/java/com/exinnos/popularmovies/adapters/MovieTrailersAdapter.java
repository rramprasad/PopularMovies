package com.exinnos.popularmovies.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.exinnos.popularmovies.R;
import com.exinnos.popularmovies.database.MoviesContract;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by RAMPRASAD on 4/3/2016.
 * Adapter class for movie trailers
 */
public class MovieTrailersAdapter extends CursorRecyclerViewAdapter<MovieTrailersAdapter.CustomViewHolder>{

    private Cursor cursor;
    private Context mContext;

    public MovieTrailersAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        this.mContext = context;
        this.cursor = cursor;
    }

    @Override
    public void onBindViewHolder(MovieTrailersAdapter.CustomViewHolder customViewHolder, Cursor cursor) {
        customViewHolder.playImageView.setTag(cursor.getString(cursor.getColumnIndex(MoviesContract.MovieTrailersEntry._ID)));
        customViewHolder.trailerTextView.setText(cursor.getString(cursor.getColumnIndex(MoviesContract.MovieTrailersEntry.COLUMN_NAME)));
    }

    @Override
    public MovieTrailersAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.movie_trailer_item, parent, false);
        CustomViewHolder customViewHolder = new CustomViewHolder(view);
        return customViewHolder;
    }


    /**
     * Custom viewholder
     */
    public class CustomViewHolder extends RecyclerView.ViewHolder{

        @Bind(R.id.play_imageview)
        protected ImageView playImageView;

        @Bind(R.id.trailer_textview)
        protected TextView trailerTextView;

        public CustomViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this,itemView);
        }

    }
}
