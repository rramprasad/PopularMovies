package com.exinnos.popularmovies.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

/**
 * Created by RAMPRASAD on 3/8/2016.
 */
public class MoviesSyncAdapter extends AbstractThreadedSyncAdapter {

    private ContentResolver mContentResolver;

    public MoviesSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);

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

    }
}
