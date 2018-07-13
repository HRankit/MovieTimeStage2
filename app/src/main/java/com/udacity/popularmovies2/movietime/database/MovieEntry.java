package com.udacity.popularmovies2.movietime.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@SuppressWarnings("ALL")
@Entity(tableName = "fav_movies")
public class MovieEntry {

    @PrimaryKey(autoGenerate = true)
    private int id;


    private int tmdbid;
    private String details;
    private String title;


    @Ignore
    public MovieEntry(String details, int tmdbid, String title) {
        this.tmdbid = tmdbid;
        this.details = details;
        this.title = title;
    }

    public MovieEntry(int id, String details, int tmdbid, String title) {
        this.id = id;
        this.tmdbid = tmdbid;
        this.details = details;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getTmdbid() {
        return tmdbid;
    }

    public void setTmdbid(int tmdbid) {
        this.tmdbid = tmdbid;
    }


}
