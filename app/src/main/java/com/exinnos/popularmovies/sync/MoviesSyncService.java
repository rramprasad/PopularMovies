package com.exinnos.popularmovies.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by RAMPRASAD on 3/8/2016.
 */
public class MoviesSyncService extends Service {

    private MoviesSyncAdapter mMoviesSyncAdapter;
    private static final Object syncAdapterLock = new Object();

    @Override
    public void onCreate() {
        synchronized (syncAdapterLock){
            if(mMoviesSyncAdapter == null){
                mMoviesSyncAdapter = new MoviesSyncAdapter(getApplicationContext(),true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mMoviesSyncAdapter.getSyncAdapterBinder();
    }
}
