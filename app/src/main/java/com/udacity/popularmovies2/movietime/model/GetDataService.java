package com.udacity.popularmovies2.movietime.model;

import com.udacity.popularmovies2.movietime.BuildConfig;
import com.udacity.popularmovies2.movietime.model.details.Details;
import com.udacity.popularmovies2.movietime.model.main.RetroTMDB;
import com.udacity.popularmovies2.movietime.model.search.Search;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GetDataService {

    String api_key = BuildConfig.TMDB_API_KEY;
    String MOVIE = "movie/";

    @GET(MOVIE + "popular?api_key=" + api_key)
    Call<RetroTMDB> getPopularMovies(@Query("page") long page);

    @GET(MOVIE + "top_rated?api_key=" + api_key)
    Call<RetroTMDB> getTopRatedMovies(@Query("page") long page);

    @GET(MOVIE + "now_playing?api_key=" + api_key)
    Call<RetroTMDB> getNowPlayingMovies(@Query("page") long page);

    @GET(MOVIE + "upcoming?api_key=" + api_key)
    Call<RetroTMDB> getUpcomingMovies(@Query("page") long page);

    @GET(MOVIE + "{id}?api_key=" + api_key + "&append_to_response=videos%2Ccredits%2Creviews%2Ckeywords%2Csimilar%2Crecommendations")
    Call<Details> getMovieWithID(@Path("id") String id);

    @GET("search/multi?api_key=" + api_key + "&page=1")
    Call<Search> getSearch(@Query("query") String query);

}

