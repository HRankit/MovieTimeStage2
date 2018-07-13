package com.udacity.popularmovies2.movietime.model.details;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Videos {

    @SerializedName("results")
    @Expose
    private List<VideosResult> results = null;


    public List<VideosResult> getResults() {
        return results;
    }

    public void setResults(List<VideosResult> results) {
        this.results = results;
    }

}