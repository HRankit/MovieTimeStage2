package com.udacity.popularmovies2.movietime.settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.udacity.popularmovies2.movietime.R;

/**
 * Created by Warcode on 11/3/2016.
 */
public class PrefFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.country);
    }
}