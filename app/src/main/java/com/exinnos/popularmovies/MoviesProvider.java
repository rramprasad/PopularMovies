package com.exinnos.popularmovies;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.exinnos.popularmovies.database.MoviesContract;
import com.exinnos.popularmovies.database.MoviesDbHelper;


public class MoviesProvider extends ContentProvider {

    private static final int URI_POPULAR_MOVIES = 100;
    private static final int URI_HIGHEST_RATED_MOVIES = 200;
    private static final int URI_FAVORITE_MOVIES = 300;
    private static final int URI_MOVIES = 400;
    private static final int URI_MOVIE_REVIEWS = 500;
    private static final int URI_MOVIE_TRAILERS = 600;

    private MoviesDbHelper moviesDbHelper;
    private static final SQLiteQueryBuilder sqLitePopularMoviesQueryBuilder;
    private static final SQLiteQueryBuilder sqLiteHighestRatedMoviesQueryBuilder;
    private static final SQLiteQueryBuilder sqLiteFavoriteMoviesQueryBuilder;
    private static final SQLiteQueryBuilder sqLiteMovieReviewsQueryBuilder;
    private static final SQLiteQueryBuilder sqLiteMovieTrailersQueryBuilder;

    private UriMatcher uriMatcher = buildUriMatcher();

    public MoviesProvider() {
    }

    static {

        // Popular movies table Join
        sqLitePopularMoviesQueryBuilder = new SQLiteQueryBuilder();
        sqLitePopularMoviesQueryBuilder.setTables(MoviesContract.PopularMoviesEntry.TABLE_NAME + " INNER JOIN " + MoviesContract.MoviesEntry.TABLE_NAME +
                " ON " + MoviesContract.PopularMoviesEntry.TABLE_NAME + "." + MoviesContract.PopularMoviesEntry.COLUMN_MOVIE_ID +
                "=" + MoviesContract.MoviesEntry.TABLE_NAME + "." + MoviesContract.MoviesEntry._ID);


        // Highest rated movies table Join
        sqLiteHighestRatedMoviesQueryBuilder = new SQLiteQueryBuilder();
        sqLiteHighestRatedMoviesQueryBuilder.setTables(MoviesContract.HighestRatedMoviesEntry.TABLE_NAME + " INNER JOIN "+
                MoviesContract.MoviesEntry.TABLE_NAME + " ON " + MoviesContract.HighestRatedMoviesEntry.TABLE_NAME + "." + MoviesContract.HighestRatedMoviesEntry.COLUMN_MOVIE_ID+
                "=" + MoviesContract.MoviesEntry.TABLE_NAME + "." + MoviesContract.MoviesEntry._ID);

        // Favorite movies table Join
        sqLiteFavoriteMoviesQueryBuilder = new SQLiteQueryBuilder();
        sqLiteFavoriteMoviesQueryBuilder.setTables(MoviesContract.FavoriteMoviesEntry.TABLE_NAME + " INNER JOIN "+
                MoviesContract.MoviesEntry.TABLE_NAME + " ON " + MoviesContract.FavoriteMoviesEntry.TABLE_NAME + "." + MoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_ID+
                "=" + MoviesContract.MoviesEntry.TABLE_NAME + "." + MoviesContract.MoviesEntry._ID);

        // Movie reviews table Join
        sqLiteMovieReviewsQueryBuilder = new SQLiteQueryBuilder();
        sqLiteMovieReviewsQueryBuilder.setTables(MoviesContract.MovieReviewsEntry.TABLE_NAME + " INNER JOIN "+
                MoviesContract.MoviesEntry.TABLE_NAME + " ON " + MoviesContract.MovieReviewsEntry.TABLE_NAME + "." + MoviesContract.MovieReviewsEntry.COLUMN_MOVIE_ID+
                "=" + MoviesContract.MoviesEntry.TABLE_NAME + "." + MoviesContract.MoviesEntry._ID);

        // Movie trailers table Join
        sqLiteMovieTrailersQueryBuilder = new SQLiteQueryBuilder();
        sqLiteMovieTrailersQueryBuilder.setTables(MoviesContract.MovieTrailersEntry.TABLE_NAME + " INNER JOIN "+
                MoviesContract.MoviesEntry.TABLE_NAME + " ON " + MoviesContract.MovieTrailersEntry.TABLE_NAME + "." + MoviesContract.MovieTrailersEntry.COLUMN_MOVIE_ID+
                "=" + MoviesContract.MoviesEntry.TABLE_NAME + "." + MoviesContract.MoviesEntry._ID);
    }

    // Match Uri
    private UriMatcher buildUriMatcher() {

        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(MoviesContract.CONTENT_AUTHORITY, MoviesContract.PATH_POPULAR_MOVIES, URI_POPULAR_MOVIES);
        uriMatcher.addURI(MoviesContract.CONTENT_AUTHORITY, MoviesContract.PATH_HIGHEST_RATED_MOVIES, URI_HIGHEST_RATED_MOVIES);
        uriMatcher.addURI(MoviesContract.CONTENT_AUTHORITY, MoviesContract.PATH_FAVORITE_MOVIES, URI_FAVORITE_MOVIES);
        uriMatcher.addURI(MoviesContract.CONTENT_AUTHORITY, MoviesContract.PATH_MOVIES, URI_MOVIES);
        uriMatcher.addURI(MoviesContract.CONTENT_AUTHORITY, MoviesContract.PATH_MOVIE_REVIEWS, URI_MOVIE_REVIEWS);
        uriMatcher.addURI(MoviesContract.CONTENT_AUTHORITY, MoviesContract.PATH_MOVIE_TRAILERS, URI_MOVIE_TRAILERS);

        return uriMatcher;
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

        Cursor cursor = null;

        switch (uriMatcher.match(uri)){

            case URI_POPULAR_MOVIES:
                cursor = sqLitePopularMoviesQueryBuilder.query(readableDatabase, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case URI_HIGHEST_RATED_MOVIES:
                cursor = sqLiteHighestRatedMoviesQueryBuilder.query(readableDatabase, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case URI_FAVORITE_MOVIES:
                cursor = sqLiteFavoriteMoviesQueryBuilder.query(readableDatabase, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case URI_MOVIE_REVIEWS:
                cursor = sqLiteMovieReviewsQueryBuilder.query(readableDatabase, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case URI_MOVIE_TRAILERS:
                cursor = sqLiteMovieTrailersQueryBuilder.query(readableDatabase, projection, selection, selectionArgs, null, null, sortOrder);
                break;
        }
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
