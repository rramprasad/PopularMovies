package com.exinnos.popularmovies.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by RAMPRASAD on 3/7/2016.
 */
public class MoviesAuthenticatorService extends Service {

    private MoviesAuthenticator moviesAuthenticator;

    @Override
    public void onCreate() {
        moviesAuthenticator = new MoviesAuthenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return moviesAuthenticator.getIBinder();
    }
}
