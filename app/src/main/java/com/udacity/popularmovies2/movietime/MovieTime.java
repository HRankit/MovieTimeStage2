package com.udacity.popularmovies2.movietime;

import android.app.Application;

public class MovieTime extends Application {
    private static String country;
    private static String language;

    public static String getCountry() {
        return MovieTime.country;
    }

    public static String getLanguage() {
        return MovieTime.language;
    }

    public void setLanguage(String someVariable) {
        MovieTime.language = someVariable;
    }

    public void setCounty(String someVariable) {
        MovieTime.country = someVariable;
    }


}
