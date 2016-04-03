package com.exinnos.popularmovies.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.exinnos.popularmovies.BuildConfig;
import com.exinnos.popularmovies.R;
import com.exinnos.popularmovies.adapters.MoviesAdapter;
import com.exinnos.popularmovies.data.Movie;
import com.exinnos.popularmovies.data.MovieReview;
import com.exinnos.popularmovies.data.MovieReviewsData;
import com.exinnos.popularmovies.data.MovieTrailer;
import com.exinnos.popularmovies.data.MovieTrailersData;
import com.exinnos.popularmovies.data.MoviesData;
import com.exinnos.popularmovies.database.MoviesContract;
import com.exinnos.popularmovies.database.MoviesDbHelper;
import com.exinnos.popularmovies.network.MoviesAPIService;
import com.exinnos.popularmovies.util.AppConstants;
import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by RAMPRASAD on 3/8/2016.
 */
public class MoviesSyncAdapter extends AbstractThreadedSyncAdapter {

    private static final int HOUR_IN_SECS = 60 * 60; // 1 Hour = 60 seconds * 60 minutes
    private static final int SYNC_INTERVAL = 2 * HOUR_IN_SECS ; // 2 hour
    private static final int SYNC_FLEX_TIME = SYNC_INTERVAL/6 ; // 20 minutes
    private static final String LOG_TAG = MoviesAdapter.class.getSimpleName();

    private static final String SORT_ORDER_POPULARITY_DESC = "popularity.desc";
    private static final String SORT_ORDER_VOTE_AVERAGE_DESC = "vote_average.desc";

    private ContentResolver mContentResolver;
    private Context mContext;

    public MoviesSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);

        this.mContext = context;

        // Get instance of Content resolver
        mContentResolver = context.getContentResolver();
    }

    public MoviesSyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);

        // Get instance of Content resolver
        mContentResolver = context.getContentResolver();
    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String authority, ContentProviderClient contentProviderClient, SyncResult syncResult) {
        Log.d("popular","Sync started");

        String time = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss").format(new Date());
        getContext().getSharedPreferences("com.exinnos.popularmovies",Context.MODE_PRIVATE).edit().putString("sync started_"+time,time).commit();

        syncPopularMovies();
        syncHighestRatedMovies();
    }

    /**
     * Fetch popular movies from server
     * and insert into local 'movies' table and 'popular_movies'
     * tables
     */
    private void syncPopularMovies() {

        Log.d("popular", "sync popular movies");

        OkHttpClient httpClient = new OkHttpClient.Builder().addNetworkInterceptor(new StethoInterceptor()).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConstants.MOVIES_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build();

        MoviesAPIService moviesAPIService = retrofit.create(MoviesAPIService.class);

        Call<MoviesData> moviesDataCall = moviesAPIService.fetchPopularMoviesData(BuildConfig.THE_MOVIE_DB_API_KEY);

        moviesDataCall.enqueue(new Callback<MoviesData>() {
            @Override
            public void onResponse(Call<MoviesData> call, Response<MoviesData> response) {

                Log.d("popular", "onResponse of popular movies sync");

                if (response != null) {
                    List<Movie> moviesList = response.body().getMovies();

                    Log.d("popular",  "moviesList.size => " + moviesList.size());

                    ContentValues[] cvv1 = new ContentValues[moviesList.size()];
                    ContentValues[] cvv2 = new ContentValues[moviesList.size()];

                    for (int i = 0; i < moviesList.size(); i++) {

                        Movie movie = moviesList.get(i);

                        ContentValues moviesContentValues = new ContentValues();
                        moviesContentValues.put(MoviesContract.MoviesEntry._ID, movie.getId());
                        moviesContentValues.put(MoviesContract.MoviesEntry.COLUMN_ADULT, movie.getAdult());
                        moviesContentValues.put(MoviesContract.MoviesEntry.COLUMN_BACKDROP_PATH, movie.getBackdropPath());
                        moviesContentValues.put(MoviesContract.MoviesEntry.COLUMN_ORIGINAL_LANGUAGE, movie.getOriginalLanguage());
                        moviesContentValues.put(MoviesContract.MoviesEntry.COLUMN_ORIGINAL_TITLE, movie.getOriginalTitle());
                        moviesContentValues.put(MoviesContract.MoviesEntry.COLUMN_OVERVIEW, movie.getOverview());
                        moviesContentValues.put(MoviesContract.MoviesEntry.COLUMN_POPULARITY, movie.getPopularity());
                        moviesContentValues.put(MoviesContract.MoviesEntry.COLUMN_POSTER_PATH, movie.getPosterPath());
                        moviesContentValues.put(MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
                        moviesContentValues.put(MoviesContract.MoviesEntry.COLUMN_VIDEO, movie.getVideo());
                        moviesContentValues.put(MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE, movie.getVoteAverage());
                        moviesContentValues.put(MoviesContract.MoviesEntry.COLUMN_VOTE_COUNT, movie.getVoteCount());

                        cvv1[i] = moviesContentValues;

                        ContentValues popularMoviesContentValues = new ContentValues();
                        popularMoviesContentValues.put(MoviesContract.PopularMoviesEntry.COLUMN_MOVIE_ID, movie.getId());

                        cvv2[i] = popularMoviesContentValues;

                        syncMovieTrailers(movie.getId());
                        syncMovieReviews(movie.getId());
                    }

                    mContentResolver.bulkInsert(MoviesContract.MoviesEntry.CONTENT_URI,cvv1);

                    mContentResolver.bulkInsert(MoviesContract.PopularMoviesEntry.CONTENT_URI,cvv2);

                    String time = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss").format(new Date());
                    getContext().getSharedPreferences("com.exinnos.popularmovies",Context.MODE_PRIVATE).edit().putString("popular_movies_sync_completed_"+time,time).commit();

                }
            }

            @Override
            public void onFailure(Call<MoviesData> call, Throwable t) {
                Log.d("popular", "Retrofit movies service on failure " + t.getMessage());
            }
        });
    }

    /**
     * Sync movie trailers by given Id
     * @param movieId
     */
    private void syncMovieTrailers(int movieId) {

        Log.d("popular", "sync movie trailers");

        OkHttpClient httpClient = new OkHttpClient.Builder().addNetworkInterceptor(new StethoInterceptor()).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConstants.MOVIES_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build();

        MoviesAPIService moviesAPIService = retrofit.create(MoviesAPIService.class);

        Call<MovieTrailersData> movieTrailersCall = moviesAPIService.fetchMovieTrailers(movieId, BuildConfig.THE_MOVIE_DB_API_KEY);

        movieTrailersCall.enqueue(new Callback<MovieTrailersData>() {
            @Override
            public void onResponse(Call<MovieTrailersData> call, Response<MovieTrailersData> response) {

                Log.d("popular", "onResponse of movie trailer sync");

                if (response != null &&  response.body() != null) {

                    List<MovieTrailer> movieTrailers = response.body().getMovieTrailers();

                    int movieId = response.body().getId();

                    Log.d("popular",  "movieTrailers.size => " + movieTrailers.size());

                    ContentValues[] cvv1 = new ContentValues[movieTrailers.size()];

                    for (int i = 0; i < movieTrailers.size(); i++) {

                        MovieTrailer movieTrailer = movieTrailers.get(i);

                        ContentValues movieTrailerContentValues = new ContentValues();
                        movieTrailerContentValues.put(MoviesContract.MovieTrailersEntry._ID, movieTrailer.getId());
                        movieTrailerContentValues.put(MoviesContract.MovieTrailersEntry.COLUMN_MOVIE_ID, movieId);
                        movieTrailerContentValues.put(MoviesContract.MovieTrailersEntry.COLUMN_ISO6391, movieTrailer.getIso6391());
                        movieTrailerContentValues.put(MoviesContract.MovieTrailersEntry.COLUMN_ISO31661, movieTrailer.getIso31661());
                        movieTrailerContentValues.put(MoviesContract.MovieTrailersEntry.COLUMN_KEY, movieTrailer.getKey());
                        movieTrailerContentValues.put(MoviesContract.MovieTrailersEntry.COLUMN_NAME, movieTrailer.getName());
                        movieTrailerContentValues.put(MoviesContract.MovieTrailersEntry.COLUMN_SITE, movieTrailer.getSite());
                        movieTrailerContentValues.put(MoviesContract.MovieTrailersEntry.COLUMN_SIZE, movieTrailer.getSize());
                        movieTrailerContentValues.put(MoviesContract.MovieTrailersEntry.COLUMN_TYPE, movieTrailer.getType());

                        cvv1[i] = movieTrailerContentValues;
                    }

                    mContentResolver.bulkInsert(MoviesContract.MovieTrailersEntry.CONTENT_URI,cvv1);

                    String time = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss").format(new Date());
                    getContext().getSharedPreferences("com.exinnos.popularmovies",Context.MODE_PRIVATE).edit().putString("movie_trailers_sync_completed_"+time,time).commit();

                }
            }

            @Override
            public void onFailure(Call<MovieTrailersData> call, Throwable t) {
                Log.d("popular", "Retrofit movies service on failure " + t.getMessage());
            }
        });
    }

    /**
     * Sync movie Reviews by given Id
     * @param movieId
     */
    private void syncMovieReviews(int movieId) {

        Log.d("popular", "sync movie reviews");

        OkHttpClient httpClient = new OkHttpClient.Builder().addNetworkInterceptor(new StethoInterceptor()).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConstants.MOVIES_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build();

        MoviesAPIService moviesAPIService = retrofit.create(MoviesAPIService.class);

        Call<MovieReviewsData> movieReviewsCall = moviesAPIService.fetchMovieReviews(movieId, BuildConfig.THE_MOVIE_DB_API_KEY);

        movieReviewsCall.enqueue(new Callback<MovieReviewsData>() {
            @Override
            public void onResponse(Call<MovieReviewsData> call, Response<MovieReviewsData> response) {

                Log.d("popular", "onResponse of movie reviews sync");

                if (response != null && response.body() != null) {
                    List<MovieReview> movieReviews = response.body().getMovieReviews();

                    int movieId = response.body().getId();

                    Log.d("popular",  "movieReviews.size => " + movieReviews.size());

                    ContentValues[] cvv1 = new ContentValues[movieReviews.size()];

                    for (int i = 0; i < movieReviews.size(); i++) {

                        MovieReview movieReview = movieReviews.get(i);

                        ContentValues movieReviewContentValues = new ContentValues();
                        movieReviewContentValues.put(MoviesContract.MovieReviewsEntry._ID, movieReview.getId());
                        movieReviewContentValues.put(MoviesContract.MovieReviewsEntry.COLUMN_MOVIE_ID, movieId);
                        movieReviewContentValues.put(MoviesContract.MovieReviewsEntry.COLUMN_AUTHOR, movieReview.getAuthor());
                        movieReviewContentValues.put(MoviesContract.MovieReviewsEntry.COLUMN_CONTENT, movieReview.getContent());
                        movieReviewContentValues.put(MoviesContract.MovieReviewsEntry.COLUMN_URL, movieReview.getUrl());

                        cvv1[i] = movieReviewContentValues;
                    }

                    mContentResolver.bulkInsert(MoviesContract.MovieReviewsEntry.CONTENT_URI,cvv1);

                    String time = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss").format(new Date());
                    getContext().getSharedPreferences("com.exinnos.popularmovies",Context.MODE_PRIVATE).edit().putString("movie_reviews_sync_completed_"+time,time).commit();
                }
            }

            @Override
            public void onFailure(Call<MovieReviewsData> call, Throwable t) {
                Log.d("popular", "Retrofit movies service on failure " + t.getMessage());
            }
        });
    }


    /**
     * Fetch highest rated movies from server
     * and insert into local 'movies' table and 'highest_rated_movies'
     * tables
     */
    private void syncHighestRatedMovies() {

        OkHttpClient httpClient = new OkHttpClient.Builder().addNetworkInterceptor(new StethoInterceptor()).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConstants.MOVIES_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build();

        MoviesAPIService moviesAPIService = retrofit.create(MoviesAPIService.class);

        Call<MoviesData> moviesDataCall = moviesAPIService.fetchHighestRatedMoviesData(BuildConfig.THE_MOVIE_DB_API_KEY);

        moviesDataCall.enqueue(new Callback<MoviesData>() {
            @Override
            public void onResponse(Call<MoviesData> call, Response<MoviesData> response) {

                Log.i(LOG_TAG, "on success " + response.isSuccess());

                if (response != null) {
                    List<Movie> moviesList = response.body().getMovies();

                    Log.i(LOG_TAG, "Highest rated moviesList.size => " + moviesList.size());

                    ContentValues[] cvv1 = new ContentValues[moviesList.size()];
                    ContentValues[] cvv2 = new ContentValues[moviesList.size()];

                    for (int i = 0; i < moviesList.size() ; i++) {

                         Movie movie = moviesList.get(i);

                        ContentValues moviesContentValues = new ContentValues();
                        moviesContentValues.put(MoviesContract.MoviesEntry._ID, movie.getId());
                        moviesContentValues.put(MoviesContract.MoviesEntry.COLUMN_ADULT, movie.getAdult());
                        moviesContentValues.put(MoviesContract.MoviesEntry.COLUMN_BACKDROP_PATH, movie.getBackdropPath());
                        moviesContentValues.put(MoviesContract.MoviesEntry.COLUMN_ORIGINAL_LANGUAGE, movie.getOriginalLanguage());
                        moviesContentValues.put(MoviesContract.MoviesEntry.COLUMN_ORIGINAL_TITLE, movie.getOriginalTitle());
                        moviesContentValues.put(MoviesContract.MoviesEntry.COLUMN_OVERVIEW, movie.getOverview());
                        moviesContentValues.put(MoviesContract.MoviesEntry.COLUMN_POPULARITY, movie.getPopularity());
                        moviesContentValues.put(MoviesContract.MoviesEntry.COLUMN_POSTER_PATH, movie.getPosterPath());
                        moviesContentValues.put(MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
                        moviesContentValues.put(MoviesContract.MoviesEntry.COLUMN_VIDEO, movie.getVideo());
                        moviesContentValues.put(MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE, movie.getVoteAverage());
                        moviesContentValues.put(MoviesContract.MoviesEntry.COLUMN_VOTE_COUNT, movie.getVoteCount());

                        cvv1[i] = moviesContentValues;

                        ContentValues highestRatedMoviesContentValues = new ContentValues();
                        highestRatedMoviesContentValues.put(MoviesContract.HighestRatedMoviesEntry.COLUMN_MOVIE_ID, movie.getId());

                        cvv2[i] = highestRatedMoviesContentValues;

                        syncMovieTrailers(movie.getId());
                        syncMovieReviews(movie.getId());
                    }

                    mContentResolver.bulkInsert(MoviesContract.MoviesEntry.CONTENT_URI,cvv1);
                    mContentResolver.bulkInsert(MoviesContract.HighestRatedMoviesEntry.CONTENT_URI,cvv2);

                    String time = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss").format(new Date());
                    getContext().getSharedPreferences("com.exinnos.popularmovies",Context.MODE_PRIVATE).edit().putString("highest_rated_movies_sync_completed_"+time,time).commit();

                }
            }

            @Override
            public void onFailure(Call<MoviesData> call, Throwable t) {
                Log.i(LOG_TAG, "Retrofit movies service on failure " + t.getMessage());
            }
        });
    }


    /**
     * Get sync account
     * @param context
     * @return account
     */
    public static Account getSyncAccount(Context context){

        Log.d("popular", "getSyncAccount");
        // instance of account manager
        AccountManager accountManager = (AccountManager)context.getSystemService(Context.ACCOUNT_SERVICE);

        Account account = new Account(context.getString(R.string.app_name), context.getString(R.string.movies_sync_account_type));

        if(accountManager.getPassword(account) == null){
            // Account doesn't exist

            if(!accountManager.addAccountExplicitly(account,"",null)){
                return null; // return null,if account not created
            }

            //Account created explicitly,call onAccountCreated() method
            onAccountCreated(account,context);
        }


        return account;
    }

    /**
     * Called after new account created
     * @param account
     * @param context
     */
    private static void onAccountCreated(Account account, Context context) {

        Log.d("popular", "onAccountCreated");

        // Configure periodic sync with SYNC_INTERVAL,SYNC_FLEX_TIME
        MoviesSyncAdapter.configurePeriodicExecution(context,SYNC_INTERVAL,SYNC_FLEX_TIME);

        ContentResolver.setSyncAutomatically(account,context.getString(R.string.movies_content_authority),true);

        syncImmediately(context);
    }


    /**
     * Sync the sync adapter immediately.
     * @param context
     */
    public static void syncImmediately(Context context) {

        Log.d("popular", "syncImmediately");

        Account syncAccount = getSyncAccount(context);

        String contentAuthority = context.getString(R.string.movies_content_authority);

        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED,true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL,true);
        ContentResolver.requestSync(syncAccount,contentAuthority,bundle);
    }

    /**
     * Helper method to periodic sync of sync adapter
     * @param context
     * @param syncInterval
     * @param syncFlexTime
     */
    private static void configurePeriodicExecution(Context context, int syncInterval, int syncFlexTime) {

        Account syncAccount = getSyncAccount(context);

        String contentAuthority = context.getString(R.string.movies_content_authority);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            SyncRequest syncRequest = new SyncRequest.Builder()
                    .syncPeriodic(syncInterval, syncFlexTime)
                    .setSyncAdapter(syncAccount, contentAuthority)
                    .setExtras(new Bundle())
                    .build();

            ContentResolver.requestSync(syncRequest);
        }
        else{

            ContentResolver.addPeriodicSync(syncAccount,contentAuthority,new Bundle(),syncInterval);
        }
    }

    /**
     * Initialize sync adapter
     */
    public static void initializeSyncAdapter(Context context){
        getSyncAccount(context);
    }
}
