package com.exinnos.popularmovies.database;

import android.provider.BaseColumns;

/**
 * Created by RAMPRASAD on 2/28/2016.
 */
public final class MoviesContract {

    public MoviesContract() {
    }

    public static final class MoviesEntry implements BaseColumns{

        public static final String TABLE_NAME = "movies";

        public static final String COLUMN_ADULT = "adult";
        public static final String COLUMN_BACKDROP_PATH = "backdrop_path";
        public static final String COLUMN_BUDGET = "budget";
        public static final String COLUMN_HOME_PAGE = "homepage";
        public static final String COLUMN_IMDB_ID = "imdb_id";
        public static final String COLUMN_ORIGINAL_LANGUAGE = "original_language";
        public static final String COLUMN_ORIGINAL_TITLE = "original_title";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_REVENUE = "revenue";
        public static final String COLUMN_RUNTIME = "runtime";
        public static final String COLUMN_STATUS = "status";
        public static final String COLUMN_TAGLINE = "tagline";
        public static final String COLUMN_VIDEO = "video";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_VOTE_COUNT = "vote_count";
    }
}
