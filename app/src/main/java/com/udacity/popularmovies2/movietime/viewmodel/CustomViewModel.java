package com.udacity.popularmovies2.movietime.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

public class CustomViewModel extends ViewModelProvider.NewInstanceFactory {

    private int mParam;


    public CustomViewModel(int param) {
        mParam = param;
    }


    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new SortedMoviesViewModel(mParam);
    }

}
