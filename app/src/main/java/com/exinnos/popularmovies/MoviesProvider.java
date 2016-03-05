package com.exinnos.popularmovies;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.exinnos.popularmovies.database.MoviesContract;
import com.exinnos.popularmovies.database.MoviesDbHelper;

public class MoviesProvider extends ContentProvider {

    private MoviesDbHelper moviesDbHelper;
    private static final SQLiteQueryBuilder sqLiteQueryBuilder;

    public MoviesProvider() {
    }


    static {
        sqLiteQueryBuilder = new SQLiteQueryBuilder();

        sqLiteQueryBuilder.setTables(MoviesContract.PopularMoviesEntry.TABLE_NAME + " INNER JOIN " + MoviesContract.MoviesEntry.TABLE_NAME +
                " ON " + MoviesContract.PopularMoviesEntry.TABLE_NAME + "." + MoviesContract.PopularMoviesEntry.COLUMN_MOVIE_ID +
                "=" + MoviesContract.MoviesEntry.TABLE_NAME + "." + MoviesContract.MoviesEntry._ID);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean onCreate() {
        moviesDbHelper = new MoviesDbHelper(getContext());
        return true;
    }

    //SELECT popular_movies.movie_id,movies.poster_path FROM popular_movies INNER JOIN movies ON popular_movies.movie_id = movies._id

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase readableDatabase = moviesDbHelper.getReadableDatabase();
        //return readableDatabase.query(MoviesContract.PopularMoviesEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);

        return sqLiteQueryBuilder.query(readableDatabase,projection,selection,selectionArgs,null,null,sortOrder);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
