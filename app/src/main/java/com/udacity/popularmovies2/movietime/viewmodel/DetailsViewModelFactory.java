package com.udacity.popularmovies2.movietime.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.udacity.popularmovies2.movietime.database.AppDatabase;

public class DetailsViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    // Done (2) Add two member variables. One for the database and one for the taskId
    private final AppDatabase mDb;
    private final int mTaskId;

    public DetailsViewModelFactory(AppDatabase Db, int taskId) {
        mDb = Db;
        mTaskId = taskId;
    }
// Done (3) Initialize the member variables in the constructor with the parameters received

    // Done (4) Uncomment the following method
// Note: This can be reused with minor modifications
    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        //noinspection unchecked
        return (T) new DetailsViewModel(mDb, mTaskId);
    }
}