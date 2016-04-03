package com.exinnos.popularmovies.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by RAMPRASAD on 2/28/2016.
 */
public class MoviesDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Movies.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String NUMERIC_TYPE = " NUMERIC";
    private static final String REAL_TYPE = " REAL";
    private static final String COMMA_SEP = ",";
    public static final String INTEGER_PRIMARY_KEY_AUTOINCREMENT = " INTEGER PRIMARY KEY AUTOINCREMENT";



    // Create movies table
    private static final String SQL_CREATE_MOVIES_TABLE = "CREATE TABLE "+ MoviesContract.MoviesEntry.TABLE_NAME+" ( "+
            MoviesContract.MoviesEntry._ID + " INTEGER PRIMARY KEY" + COMMA_SEP +
            MoviesContract.MoviesEntry.COLUMN_ADULT + NUMERIC_TYPE + COMMA_SEP +
            MoviesContract.MoviesEntry.COLUMN_BACKDROP_PATH + TEXT_TYPE + COMMA_SEP +
            MoviesContract.MoviesEntry.COLUMN_BUDGET + INTEGER_TYPE + COMMA_SEP +
            MoviesContract.MoviesEntry.COLUMN_HOME_PAGE + TEXT_TYPE + COMMA_SEP +
            MoviesContract.MoviesEntry.COLUMN_IMDB_ID + TEXT_TYPE + COMMA_SEP +
            MoviesContract.MoviesEntry.COLUMN_ORIGINAL_LANGUAGE + TEXT_TYPE + COMMA_SEP +
            MoviesContract.MoviesEntry.COLUMN_ORIGINAL_TITLE + TEXT_TYPE + COMMA_SEP +
            MoviesContract.MoviesEntry.COLUMN_OVERVIEW + TEXT_TYPE + COMMA_SEP +
            MoviesContract.MoviesEntry.COLUMN_POPULARITY + REAL_TYPE + COMMA_SEP +
            MoviesContract.MoviesEntry.COLUMN_POSTER_PATH + TEXT_TYPE + COMMA_SEP +
            MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE + TEXT_TYPE + COMMA_SEP +
            MoviesContract.MoviesEntry.COLUMN_REVENUE + INTEGER_TYPE + COMMA_SEP +
            MoviesContract.MoviesEntry.COLUMN_RUNTIME + INTEGER_TYPE + COMMA_SEP +
            MoviesContract.MoviesEntry.COLUMN_STATUS + TEXT_TYPE + COMMA_SEP +
            MoviesContract.MoviesEntry.COLUMN_TAGLINE + TEXT_TYPE + COMMA_SEP +
            MoviesContract.MoviesEntry.COLUMN_VIDEO + NUMERIC_TYPE + COMMA_SEP +
            MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE + REAL_TYPE + COMMA_SEP +
            MoviesContract.MoviesEntry.COLUMN_VOTE_COUNT + INTEGER_TYPE + ")";

    // delete movies table
    private static final String SQL_DELETE_MOVIES_ENTRY = "DROP TABLE IF EXISTS "+ MoviesContract.MoviesEntry.TABLE_NAME;



    // create popular movies table
    private static final String SQL_CREATE_POPULAR_MOVIES_TABLE = "CREATE TABLE "+ MoviesContract.PopularMoviesEntry.TABLE_NAME + "( "+
            MoviesContract.PopularMoviesEntry._ID + INTEGER_PRIMARY_KEY_AUTOINCREMENT + COMMA_SEP +
            MoviesContract.PopularMoviesEntry.COLUMN_MOVIE_ID + INTEGER_TYPE + COMMA_SEP +
            " FOREIGN KEY ("+ MoviesContract.PopularMoviesEntry.COLUMN_MOVIE_ID + ") REFERENCES "+ MoviesContract.MoviesEntry.TABLE_NAME + " ("+ MoviesContract.MoviesEntry._ID + ")"+ COMMA_SEP +
            " UNIQUE ("+ MoviesContract.PopularMoviesEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";

    // delete popular movies table
    private static final String SQL_DELETE_POPULAR_MOVIES_TABLE = "DROP TABLE IF EXISTS " + MoviesContract.PopularMoviesEntry.TABLE_NAME;


    // create highest rated movies table
    private static final String SQL_CREATE_HIGHEST_RATED_MOVIES_TABLE = "CREATE TABLE "+ MoviesContract.HighestRatedMoviesEntry.TABLE_NAME + "( "+
            MoviesContract.HighestRatedMoviesEntry._ID + INTEGER_PRIMARY_KEY_AUTOINCREMENT + COMMA_SEP +
            MoviesContract.HighestRatedMoviesEntry.COLUMN_MOVIE_ID + INTEGER_TYPE + ")";

    // delete highest rated movies table
    private static final String SQL_DELETE_HIGHEST_RATED_MOVIES_TABLE = "DROP TABLE IF EXISTS " + MoviesContract.HighestRatedMoviesEntry.TABLE_NAME;


    // create favorite movies table
    private static final String SQL_CREATE_FAVORITE_MOVIES_TABLE = "CREATE TABLE "+ MoviesContract.FavoriteMoviesEntry.TABLE_NAME + "( "+
            MoviesContract.FavoriteMoviesEntry._ID + INTEGER_PRIMARY_KEY_AUTOINCREMENT + COMMA_SEP +
            MoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_ID + INTEGER_TYPE + ")";

    // delete favorite movies table
    private static final String SQL_DELETE_FAVORITE_MOVIES_TABLE = "DROP TABLE IF EXISTS " + MoviesContract.FavoriteMoviesEntry.TABLE_NAME;


    // create movie reviews table
    private static final String SQL_CREATE_MOVIE_REVIEWS_TABLE = "CREATE TABLE "+ MoviesContract.MovieReviewsEntry.TABLE_NAME + "( "+
            MoviesContract.MovieReviewsEntry._ID + " TEXT PRIMARY KEY" + COMMA_SEP +
            MoviesContract.MovieReviewsEntry.COLUMN_MOVIE_ID + INTEGER_TYPE + COMMA_SEP +
            MoviesContract.MovieReviewsEntry.COLUMN_AUTHOR + TEXT_TYPE + COMMA_SEP +
            MoviesContract.MovieReviewsEntry.COLUMN_CONTENT + TEXT_TYPE + COMMA_SEP +
            MoviesContract.MovieReviewsEntry.COLUMN_URL + TEXT_TYPE + ")";

    // delete movie reviews table
    private static final String SQL_DELETE_MOVIE_REVIEWS_TABLE = "DROP TABLE IF EXISTS " + MoviesContract.MovieReviewsEntry.TABLE_NAME;


    // create movie trailers table
    private static final String SQL_CREATE_MOVIE_TRAILERS_TABLE = "CREATE TABLE "+ MoviesContract.MovieTrailersEntry.TABLE_NAME + "( "+
            MoviesContract.MovieTrailersEntry._ID + " TEXT PRIMARY KEY" + COMMA_SEP +
            MoviesContract.MovieTrailersEntry.COLUMN_MOVIE_ID + INTEGER_TYPE + COMMA_SEP +
            MoviesContract.MovieTrailersEntry.COLUMN_ISO6391 + TEXT_TYPE + COMMA_SEP +
            MoviesContract.MovieTrailersEntry.COLUMN_ISO31661 + TEXT_TYPE + COMMA_SEP +
            MoviesContract.MovieTrailersEntry.COLUMN_KEY + TEXT_TYPE + COMMA_SEP +
            MoviesContract.MovieTrailersEntry.COLUMN_NAME + TEXT_TYPE + COMMA_SEP +
            MoviesContract.MovieTrailersEntry.COLUMN_SITE + TEXT_TYPE + COMMA_SEP +
            MoviesContract.MovieTrailersEntry.COLUMN_SIZE + INTEGER_TYPE + COMMA_SEP +
            MoviesContract.MovieTrailersEntry.COLUMN_TYPE + TEXT_TYPE + ")";

    // delete movie reviews table
    private static final String SQL_DELETE_MOVIE_TRAILERS_TABLE = "DROP TABLE IF EXISTS " + MoviesContract.MovieTrailersEntry.TABLE_NAME;


    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_POPULAR_MOVIES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_HIGHEST_RATED_MOVIES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITE_MOVIES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_REVIEWS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TRAILERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        // Delete old data
        sqLiteDatabase.execSQL(SQL_DELETE_MOVIES_ENTRY);
        sqLiteDatabase.execSQL(SQL_DELETE_POPULAR_MOVIES_TABLE);
        sqLiteDatabase.execSQL(SQL_DELETE_HIGHEST_RATED_MOVIES_TABLE);
        sqLiteDatabase.execSQL(SQL_DELETE_FAVORITE_MOVIES_TABLE);
        sqLiteDatabase.execSQL(SQL_DELETE_MOVIE_REVIEWS_TABLE);
        sqLiteDatabase.execSQL(SQL_DELETE_MOVIE_TRAILERS_TABLE);
        onCreate(sqLiteDatabase);
    }
}
