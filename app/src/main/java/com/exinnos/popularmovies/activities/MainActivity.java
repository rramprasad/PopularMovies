package com.exinnos.popularmovies.activities;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;

import com.exinnos.popularmovies.R;

/**
 * @author RAMPRASAD
 * Main Activity for movies.
 */
public class MainActivity extends AppCompatActivity {

    private boolean twoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set toolbar
        Toolbar toolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);

        /**
         * Check detail container available or not.
         * It contains only sw600dp devices(tablets).
         */
        if(findViewById(R.id.framelayout_detail_container) != null){
            twoPane = true;
        }
    }
}
