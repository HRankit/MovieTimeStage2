package com.udacity.popularmovies2.movietime.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.udacity.popularmovies2.movietime.database.AppDatabase;

public class DetailsViewModel extends ViewModel {

    // Constant for logging
    private static final String TAG = DetailsViewModel.class.getSimpleName();

    private LiveData<Boolean> isFav;
    private LiveData<String> movie;


    public DetailsViewModel(AppDatabase database, int taskId) {
        isFav = database.movieDao().checkIfMovieExists(taskId);
        movie = database.movieDao().loadMovieById(taskId);
    }


    public LiveData<Boolean> checkIfMovieExists() {
        return isFav;
    }


    public LiveData<String> getMovieData() {
        return movie;
    }

}