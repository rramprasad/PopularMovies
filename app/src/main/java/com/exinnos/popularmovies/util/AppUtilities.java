package com.exinnos.popularmovies.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by RAMPRASAD on 2/20/2016.
 */
public class AppUtilities {

    /**
     * Check Network connection of device.
     *
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();

    }


    /**
     * Format give date string
     *
     * @param releaseDate
     * @return
     */
    public static String getFormattedDate(String releaseDate) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");//2016-02-09

        try {
            Date parsedDate = simpleDateFormat.parse(releaseDate);

            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy MMM dd");
            String formattedDate = outputFormat.format(parsedDate);
            return formattedDate;
        } catch (ParseException e) {
            return "";
        }

    }
}
