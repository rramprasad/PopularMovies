package com.exinnos.popularmovies;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by RAMPRASAD on 2/27/2016.
 */
public class MoviesApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Stetho.initializeWithDefaults(this);
    }
}
