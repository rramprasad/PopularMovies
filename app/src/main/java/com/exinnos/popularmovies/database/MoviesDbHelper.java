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


    private static final String SQL_CREATE_MOVIES_ENTRY = "CREATE TABLE "+ MoviesContract.MoviesEntry.TABLE_NAME+"("+
            MoviesContract.MoviesEntry._ID + " INTEGER PRIMARY KEY,"+
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

    private static final String SQL_DELETE_MOVIES_ENTRY = "";

    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIES_ENTRY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL(SQL_DELETE_MOVIES_ENTRY);
        onCreate(sqLiteDatabase);
    }
}
