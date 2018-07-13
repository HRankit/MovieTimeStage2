package com.udacity.popularmovies2.movietime.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.udacity.popularmovies2.movietime.database.AppDatabase;
import com.udacity.popularmovies2.movietime.database.MovieEntry;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    // Constant for logging
    private static final String TAG = MainViewModel.class.getSimpleName();

    private LiveData<List<MovieEntry>> movies;


    public MainViewModel(Application application) {
        super(application);

        AppDatabase database = AppDatabase.getInstance(this.getApplication());
        Log.d(TAG, "Actively retrieving the tasks from the DataBase");
        movies = database.movieDao().loadAllTasks();
    }

    public LiveData<List<MovieEntry>> getFavMovies() {
        return movies;
    }
}
