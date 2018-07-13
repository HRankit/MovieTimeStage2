package com.udacity.popularmovies2.movietime.datasource;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.DataSource;

import java.util.concurrent.Executor;

public class NowPlayingDataSourceFactory extends DataSource.Factory {
    private final MutableLiveData<NowPlayingDataSource> mutableLiveData;
    private final Executor executor;
    private final int mParam;


    public NowPlayingDataSourceFactory(Executor executor, int integer) {
        this.executor = executor;
        mutableLiveData = new MutableLiveData<>();
        this.mParam = integer;
    }

    @Override
    public DataSource create() {

        NowPlayingDataSource moviesDataSource = new NowPlayingDataSource(executor, mParam);
        mutableLiveData.postValue(moviesDataSource);
        return moviesDataSource;
    }

    public MutableLiveData<NowPlayingDataSource> getMutableLiveData() {
        return mutableLiveData;
    }
}