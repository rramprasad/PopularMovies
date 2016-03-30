package com.exinnos.popularmovies.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;


public class MoviesProvider extends ContentProvider {

    private static final int URI_POPULAR_MOVIES = 100;
    private static final int URI_POPULAR_MOVIES_WITH_ID = 101;

    private static final int URI_HIGHEST_RATED_MOVIES = 200;
    private static final int URI_HIGHEST_RATED_MOVIES_WITH_ID = 201;

    private static final int URI_FAVORITE_MOVIES = 300;

    private static final int URI_MOVIES = 400;
    private static final int URI_MOVIES_WITH_ID = 401;

    private static final int URI_MOVIE_REVIEWS = 500;

    private static final int URI_MOVIE_TRAILERS = 600;
    private static final String LOG_TAG = MoviesProvider.class.getSimpleName();


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
        sqLiteHighestRatedMoviesQueryBuilder.setTables(MoviesContract.HighestRatedMoviesEntry.TABLE_NAME + " INNER JOIN " +
                MoviesContract.MoviesEntry.TABLE_NAME + " ON " + MoviesContract.HighestRatedMoviesEntry.TABLE_NAME + "." + MoviesContract.HighestRatedMoviesEntry.COLUMN_MOVIE_ID +
                "=" + MoviesContract.MoviesEntry.TABLE_NAME + "." + MoviesContract.MoviesEntry._ID);

        // Favorite movies table Join
        sqLiteFavoriteMoviesQueryBuilder = new SQLiteQueryBuilder();
        sqLiteFavoriteMoviesQueryBuilder.setTables(MoviesContract.FavoriteMoviesEntry.TABLE_NAME + " INNER JOIN " +
                MoviesContract.MoviesEntry.TABLE_NAME + " ON " + MoviesContract.FavoriteMoviesEntry.TABLE_NAME + "." + MoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_ID +
                "=" + MoviesContract.MoviesEntry.TABLE_NAME + "." + MoviesContract.MoviesEntry._ID);

        // Movie reviews table Join
        sqLiteMovieReviewsQueryBuilder = new SQLiteQueryBuilder();
        sqLiteMovieReviewsQueryBuilder.setTables(MoviesContract.MovieReviewsEntry.TABLE_NAME + " INNER JOIN " +
                MoviesContract.MoviesEntry.TABLE_NAME + " ON " + MoviesContract.MovieReviewsEntry.TABLE_NAME + "." + MoviesContract.MovieReviewsEntry.COLUMN_MOVIE_ID +
                "=" + MoviesContract.MoviesEntry.TABLE_NAME + "." + MoviesContract.MoviesEntry._ID);

        // Movie trailers table Join
        sqLiteMovieTrailersQueryBuilder = new SQLiteQueryBuilder();
        sqLiteMovieTrailersQueryBuilder.setTables(MoviesContract.MovieTrailersEntry.TABLE_NAME + " INNER JOIN " +
                MoviesContract.MoviesEntry.TABLE_NAME + " ON " + MoviesContract.MovieTrailersEntry.TABLE_NAME + "." + MoviesContract.MovieTrailersEntry.COLUMN_MOVIE_ID +
                "=" + MoviesContract.MoviesEntry.TABLE_NAME + "." + MoviesContract.MoviesEntry._ID);
    }

    // Match Uri
    private UriMatcher buildUriMatcher() {

        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(MoviesContract.CONTENT_AUTHORITY, MoviesContract.PATH_MOVIES, URI_MOVIES);
        uriMatcher.addURI(MoviesContract.CONTENT_AUTHORITY, MoviesContract.PATH_MOVIES + "/#", URI_MOVIES_WITH_ID);
        uriMatcher.addURI(MoviesContract.CONTENT_AUTHORITY, MoviesContract.PATH_POPULAR_MOVIES, URI_POPULAR_MOVIES);
        uriMatcher.addURI(MoviesContract.CONTENT_AUTHORITY, MoviesContract.PATH_POPULAR_MOVIES + "/#", URI_POPULAR_MOVIES_WITH_ID);
        uriMatcher.addURI(MoviesContract.CONTENT_AUTHORITY, MoviesContract.PATH_HIGHEST_RATED_MOVIES, URI_HIGHEST_RATED_MOVIES);
        uriMatcher.addURI(MoviesContract.CONTENT_AUTHORITY, MoviesContract.PATH_HIGHEST_RATED_MOVIES + "/#", URI_HIGHEST_RATED_MOVIES_WITH_ID);
        uriMatcher.addURI(MoviesContract.CONTENT_AUTHORITY, MoviesContract.PATH_FAVORITE_MOVIES, URI_FAVORITE_MOVIES);
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

        switch (uriMatcher.match(uri)) {
            case URI_MOVIES:
                return MoviesContract.MoviesEntry.CONTENT_TYPE;
            case URI_MOVIES_WITH_ID:
                return MoviesContract.MoviesEntry.CONTENT_ITEM_TYPE;
            case URI_POPULAR_MOVIES:
                return MoviesContract.PopularMoviesEntry.CONTENT_TYPE;
            case URI_POPULAR_MOVIES_WITH_ID:
                return MoviesContract.PopularMoviesEntry.CONTENT_ITEM_TYPE;
            case URI_HIGHEST_RATED_MOVIES:
                return MoviesContract.HighestRatedMoviesEntry.CONTENT_TYPE;
            case URI_HIGHEST_RATED_MOVIES_WITH_ID:
                return MoviesContract.HighestRatedMoviesEntry.CONTENT_ITEM_TYPE;
            case URI_FAVORITE_MOVIES:
                return MoviesContract.FavoriteMoviesEntry.CONTENT_TYPE;
            case URI_MOVIE_REVIEWS:
                return MoviesContract.MovieReviewsEntry.CONTENT_TYPE;
            case URI_MOVIE_TRAILERS:
                return MoviesContract.MovieTrailersEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri" + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) throws SQLiteException {
        SQLiteDatabase writableDatabase = moviesDbHelper.getWritableDatabase();

        Uri returnUri = null;
        switch (uriMatcher.match(uri)) {
            case URI_MOVIES: {
                long rowId = writableDatabase.insert(MoviesContract.MoviesEntry.TABLE_NAME, null, contentValues);
                break;
            }
            case URI_POPULAR_MOVIES: {
                long rowId = writableDatabase.insert(MoviesContract.PopularMoviesEntry.TABLE_NAME, null, contentValues);
                break;
            }
            case URI_HIGHEST_RATED_MOVIES: {
                long rowId = writableDatabase.insert(MoviesContract.HighestRatedMoviesEntry.TABLE_NAME, null, contentValues);
                break;
            }
            case URI_FAVORITE_MOVIES: {
                long rowId = writableDatabase.insert(MoviesContract.FavoriteMoviesEntry.TABLE_NAME, null, contentValues);
                break;
            }
            case URI_MOVIE_REVIEWS: {
                long rowId = writableDatabase.insert(MoviesContract.MovieReviewsEntry.TABLE_NAME, null, contentValues);
                break;
            }
            case URI_MOVIE_TRAILERS: {
                long rowId = writableDatabase.insert(MoviesContract.MovieTrailersEntry.TABLE_NAME, null, contentValues);
                break;
            }
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] contentValues) {

        SQLiteDatabase writableDatabase = moviesDbHelper.getWritableDatabase();

        switch (uriMatcher.match(uri)) {
            case URI_MOVIES: {
                writableDatabase.beginTransaction();

                int rowsAdded = 0;

                try {
                    for (ContentValues value : contentValues) {
                        int affectedRowsCount = writableDatabase.update(MoviesContract.MoviesEntry.TABLE_NAME, value, MoviesContract.MoviesEntry.TABLE_NAME + "." + MoviesContract.MoviesEntry._ID + " = ?", new String[]{value.getAsString(MoviesContract.MoviesEntry._ID)});

                        if (affectedRowsCount == 0) {
                            long rowId = writableDatabase.insert(MoviesContract.MoviesEntry.TABLE_NAME, null, value);

                            if (rowId > 0) {
                                rowsAdded++;
                            }
                        }
                    }
                    writableDatabase.setTransactionSuccessful();
                } catch (SQLException exception) {
                    Log.d(LOG_TAG, "SQLException =>" + exception.getMessage());
                } finally {
                    writableDatabase.endTransaction();
                }

                getContext().getContentResolver().notifyChange(uri, null);

                return rowsAdded;
            }
            case URI_POPULAR_MOVIES: {
                writableDatabase.beginTransaction();

                int rowsAdded = 0;

                try {
                    for (ContentValues value : contentValues) {
                        int affectedRowsCount = writableDatabase.update(MoviesContract.PopularMoviesEntry.TABLE_NAME, value, MoviesContract.PopularMoviesEntry.TABLE_NAME + "." + MoviesContract.PopularMoviesEntry.COLUMN_MOVIE_ID + " = ?", new String[]{value.getAsString(MoviesContract.PopularMoviesEntry.COLUMN_MOVIE_ID)});

                        if (affectedRowsCount == 0) {
                            long rowId = writableDatabase.insert(MoviesContract.PopularMoviesEntry.TABLE_NAME, null, value);

                            if (rowId > 0) {
                                rowsAdded++;
                            }
                        }
                    }
                    writableDatabase.setTransactionSuccessful();
                } catch (SQLException exception) {
                    Log.d(LOG_TAG, "SQLException =>" + exception.getMessage());
                } finally {
                    writableDatabase.endTransaction();
                }

                getContext().getContentResolver().notifyChange(uri, null);

                return rowsAdded;
            }
            case URI_HIGHEST_RATED_MOVIES: {
                writableDatabase.beginTransaction();

                int rowsAdded = 0;

                try {
                    for (ContentValues value : contentValues) {
                        int affectedRowsCount = writableDatabase.update(MoviesContract.HighestRatedMoviesEntry.TABLE_NAME, value, MoviesContract.HighestRatedMoviesEntry.TABLE_NAME + "." + MoviesContract.HighestRatedMoviesEntry.COLUMN_MOVIE_ID + " = ?", new String[]{value.getAsString(MoviesContract.HighestRatedMoviesEntry.COLUMN_MOVIE_ID)});

                        if (affectedRowsCount == 0) {
                            long rowId = writableDatabase.insert(MoviesContract.HighestRatedMoviesEntry.TABLE_NAME, null, value);

                            if (rowId > 0) {
                                rowsAdded++;
                            }
                        }
                    }
                    writableDatabase.setTransactionSuccessful();
                } catch (SQLException exception) {
                    Log.d(LOG_TAG, "SQLException =>" + exception.getMessage());
                } finally {
                    writableDatabase.endTransaction();
                }

                getContext().getContentResolver().notifyChange(uri, null);

                return rowsAdded;
            }
            case URI_FAVORITE_MOVIES: {
                break;
            }
            case URI_MOVIE_REVIEWS: {
                break;
            }
            case URI_MOVIE_TRAILERS: {
                break;
            }
        }

        return 0;
    }

    @Override
    public boolean onCreate() {
        moviesDbHelper = new MoviesDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase readableDatabase = moviesDbHelper.getReadableDatabase();

        Cursor cursor = null;

        switch (uriMatcher.match(uri)) {

            case URI_MOVIES:
                cursor = readableDatabase.query(MoviesContract.MoviesEntry.TABLE_NAME, null, null, null, null, null, null);
                break;

            case URI_MOVIES_WITH_ID:
                cursor = readableDatabase.query(MoviesContract.MoviesEntry.TABLE_NAME, null, selection, selectionArgs, null, null, null);
                break;

            case URI_POPULAR_MOVIES:
                cursor = sqLitePopularMoviesQueryBuilder.query(readableDatabase, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case URI_POPULAR_MOVIES_WITH_ID:
                cursor = sqLitePopularMoviesQueryBuilder.query(readableDatabase, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case URI_HIGHEST_RATED_MOVIES:
                cursor = sqLiteHighestRatedMoviesQueryBuilder.query(readableDatabase, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case URI_HIGHEST_RATED_MOVIES_WITH_ID:
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
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        SQLiteDatabase writableDatabase = moviesDbHelper.getWritableDatabase();

        int updatedRowsCount = 0;
        switch (uriMatcher.match(uri)) {
            case URI_MOVIES: {
                updatedRowsCount = writableDatabase.update(MoviesContract.MoviesEntry.TABLE_NAME, contentValues, selection, selectionArgs);
                break;
            }
            case URI_POPULAR_MOVIES: {
                updatedRowsCount = writableDatabase.update(MoviesContract.PopularMoviesEntry.TABLE_NAME, contentValues, selection, selectionArgs);
                break;
            }
            case URI_HIGHEST_RATED_MOVIES: {
                updatedRowsCount = writableDatabase.update(MoviesContract.HighestRatedMoviesEntry.TABLE_NAME, contentValues, selection, selectionArgs);
                break;
            }
            case URI_FAVORITE_MOVIES: {
                updatedRowsCount = writableDatabase.update(MoviesContract.FavoriteMoviesEntry.TABLE_NAME, contentValues, selection, selectionArgs);
                break;
            }
            case URI_MOVIE_REVIEWS: {
                updatedRowsCount = writableDatabase.update(MoviesContract.MovieReviewsEntry.TABLE_NAME, contentValues, selection, selectionArgs);
                break;
            }
            case URI_MOVIE_TRAILERS: {
                updatedRowsCount = writableDatabase.update(MoviesContract.MovieTrailersEntry.TABLE_NAME, contentValues, selection, selectionArgs);
                break;
            }
        }

        if (updatedRowsCount > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return updatedRowsCount;
    }
}
