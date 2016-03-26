package com.exinnos.popularmovies.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
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
import com.exinnos.popularmovies.data.MoviesData;
import com.exinnos.popularmovies.database.MoviesContract;
import com.exinnos.popularmovies.database.MoviesDbHelper;
import com.exinnos.popularmovies.network.MoviesAPIService;
import com.exinnos.popularmovies.util.AppConstants;
import com.facebook.stetho.okhttp3.StethoInterceptor;

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
        Log.d(LOG_TAG,"Sync started");

        syncPopularMovies();
        syncHighestRatedMovies();
    }

    /**
     * Fetch popular movies from server
     * and insert into local 'movies' table and 'popular_movies'
     * tables
     */
    private void syncPopularMovies() {

        OkHttpClient httpClient = new OkHttpClient.Builder().addNetworkInterceptor(new StethoInterceptor()).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConstants.MOVIES_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build();

        MoviesAPIService moviesAPIService = retrofit.create(MoviesAPIService.class);

        Call<MoviesData> moviesDataCall = moviesAPIService.fetchMoviesData(SORT_ORDER_POPULARITY_DESC, BuildConfig.THE_MOVIE_DB_API_KEY);

        moviesDataCall.enqueue(new Callback<MoviesData>() {
            @Override
            public void onResponse(Call<MoviesData> call, Response<MoviesData> response) {

                Log.i(LOG_TAG, "on success " + response.isSuccess());

                if (response != null) {
                    List<Movie> moviesList = response.body().getMovies();

                    Log.i(LOG_TAG, "moviesList.size => " + moviesList.size());

                    for (Movie movie : moviesList) {

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

                        String movieIdString = String.valueOf(movie.getId());

                        Cursor moviesCursor = mContentResolver.query(MoviesContract.MoviesEntry.buildMoviesWithIdUri(movie.getId()), null, MoviesContract.MoviesEntry.TABLE_NAME+"."+MoviesContract.MoviesEntry._ID + " = ?", new String[]{movieIdString}, null);

                        Log.i(LOG_TAG, "moviesCursor.getCount() => " +moviesCursor.getCount());

                        if(moviesCursor.getCount() > 0){
                            mContentResolver.update(MoviesContract.MoviesEntry.CONTENT_URI, moviesContentValues, MoviesContract.MoviesEntry._ID + "= ?",new String[]{movieIdString});
                        }
                        else{
                            mContentResolver.insert(MoviesContract.MoviesEntry.CONTENT_URI, moviesContentValues);
                        }

                        // Store on popular movies table
                        Cursor popularMoviesCursor = mContentResolver.query(MoviesContract.PopularMoviesEntry.buildPopularMoviesWithIdUri(movie.getId()), null, MoviesContract.PopularMoviesEntry.TABLE_NAME+"."+MoviesContract.PopularMoviesEntry._ID + " = ?", new String[]{movieIdString}, null);

                        ContentValues popularMoviesContentValues = new ContentValues();
                        popularMoviesContentValues.put(MoviesContract.PopularMoviesEntry.COLUMN_MOVIE_ID, movie.getId());

                        Log.i(LOG_TAG, "popularMoviesCursor.getCount() => " +popularMoviesCursor.getCount());

                        if(popularMoviesCursor.getCount() > 0){
                            mContentResolver.update(MoviesContract.PopularMoviesEntry.CONTENT_URI, popularMoviesContentValues, MoviesContract.PopularMoviesEntry.COLUMN_MOVIE_ID + "= ?",new String[]{movieIdString});
                        }
                        else{
                            mContentResolver.insert(MoviesContract.PopularMoviesEntry.CONTENT_URI,popularMoviesContentValues);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<MoviesData> call, Throwable t) {
                Log.i(LOG_TAG, "Retrofit movies service on failure " + t.getMessage());
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

        Call<MoviesData> moviesDataCall = moviesAPIService.fetchMoviesData(SORT_ORDER_VOTE_AVERAGE_DESC, BuildConfig.THE_MOVIE_DB_API_KEY);

        moviesDataCall.enqueue(new Callback<MoviesData>() {
            @Override
            public void onResponse(Call<MoviesData> call, Response<MoviesData> response) {

                Log.i(LOG_TAG, "on success " + response.isSuccess());

                if (response != null) {
                    List<Movie> moviesList = response.body().getMovies();

                    Log.i(LOG_TAG, "moviesList.size => " + moviesList.size());

                    for (Movie movie : moviesList) {

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

                        String movieIdString = String.valueOf(movie.getId());

                        Cursor moviesCursor = mContentResolver.query(MoviesContract.MoviesEntry.buildMoviesWithIdUri(movie.getId()), null, MoviesContract.MoviesEntry.TABLE_NAME+"."+MoviesContract.MoviesEntry._ID + " = ?", new String[]{movieIdString}, null);

                        Log.i(LOG_TAG, "moviesCursor.getCount() => " +moviesCursor.getCount());

                        if(moviesCursor.getCount() > 0){
                            mContentResolver.update(MoviesContract.MoviesEntry.CONTENT_URI, moviesContentValues, MoviesContract.MoviesEntry._ID + "= ?",new String[]{movieIdString});
                        }
                        else{
                            mContentResolver.insert(MoviesContract.MoviesEntry.CONTENT_URI, moviesContentValues);
                        }

                        // Store on highest rated movies table
                        Cursor highestRatedMoviesCursor = mContentResolver.query(MoviesContract.HighestRatedMoviesEntry.buildHighestRatedMoviesWithIdUri(movie.getId()), null, MoviesContract.HighestRatedMoviesEntry.TABLE_NAME+"."+MoviesContract.HighestRatedMoviesEntry._ID + " = ?", new String[]{movieIdString}, null);

                        ContentValues highestRatedMoviesContentValues = new ContentValues();
                        highestRatedMoviesContentValues.put(MoviesContract.HighestRatedMoviesEntry.COLUMN_MOVIE_ID, movie.getId());

                        Log.i(LOG_TAG, "highestRatedMoviesCursor.getCount() => " +highestRatedMoviesCursor.getCount());

                        if(highestRatedMoviesCursor.getCount() > 0){
                            mContentResolver.update(MoviesContract.HighestRatedMoviesEntry.CONTENT_URI, highestRatedMoviesContentValues, MoviesContract.HighestRatedMoviesEntry.COLUMN_MOVIE_ID + "= ?",new String[]{movieIdString});
                        }
                        else{
                            mContentResolver.insert(MoviesContract.HighestRatedMoviesEntry.CONTENT_URI,highestRatedMoviesContentValues);
                        }
                    }
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
