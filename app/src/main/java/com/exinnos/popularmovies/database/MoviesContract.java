package com.exinnos.popularmovies.database;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by RAMPRASAD on 2/28/2016.
 */
public final class MoviesContract {

    // To be unique across apps,used package name of app
    public static final String CONTENT_AUTHORITY = "com.exinnos.popularmovies";

    // Base content URI
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Movies table path
    public static final String PATH_MOVIES = "movies";

    // Popular movies table path
    public static final String PATH_POPULAR_MOVIES = "popular_movies";

    // Highest rated movies table path
    public static final String PATH_HIGHEST_RATED_MOVIES = "highest_rated_movies";

    // Favorite movies table path
    public static final String PATH_FAVORITE_MOVIES = "favorite_movies";

    // Movie reviews table path
    public static final String PATH_MOVIE_REVIEWS = "movie_reviews";

    // Movie trailers table path
    public static final String PATH_MOVIE_TRAILERS = "movie_trailers";



    public MoviesContract() {
    }


    // Table for all movie details
    public static final class MoviesEntry implements BaseColumns {

        // Add Movies table path on top of base content URI
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

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

    // Table for popular movies
    public static final class PopularMoviesEntry implements BaseColumns {


        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_POPULAR_MOVIES).build();

        public static final String TABLE_NAME = "popular_movies";

        public static final String COLUMN_MOVIE_ID = "movie_id";

        public static Uri buildPopularMoviesUri() {
            return CONTENT_URI.buildUpon().build();
        }
    }


    // Table for highest rated movies
    public static final class HighestRatedMoviesEntry implements BaseColumns {


        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_HIGHEST_RATED_MOVIES).build();

        public static final String TABLE_NAME = "highest_rated_movies";

        public static final String COLUMN_MOVIE_ID = "movie_id";
    }


    // Table for favorite movies
    public static final class FavoriteMoviesEntry implements BaseColumns {


        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FAVORITE_MOVIES).build();

        public static final String TABLE_NAME = "favorite_movies";

        public static final String COLUMN_MOVIE_ID = "movie_id";
    }


    // Table for movie reviews
    public static final class MovieReviewsEntry implements BaseColumns {


        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIE_REVIEWS).build();

        public static final String TABLE_NAME = "movie_reviews";

        public static final String COLUMN_MOVIE_ID = "movie_id";
    }


    // Table for movie trailers
    public static final class MovieTrailersEntry implements BaseColumns {


        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIE_TRAILERS).build();

        public static final String TABLE_NAME = "movie_trailers";

        public static final String COLUMN_MOVIE_ID = "movie_id";
    }

}
