package com.udacity.popularmovies2.movietime.model.details;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Keywords {

    @SerializedName("keywords")
    @Expose
    private List<Keyword> keywords = null;

    public List<Keyword> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<Keyword> keywords) {
        this.keywords = keywords;
    }

}