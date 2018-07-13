package com.udacity.popularmovies2.movietime.viewmodel;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.util.Log;

import com.udacity.popularmovies2.movietime.datasource.NowPlayingDataSource;
import com.udacity.popularmovies2.movietime.datasource.NowPlayingDataSourceFactory;
import com.udacity.popularmovies2.movietime.model.main.Result;
import com.udacity.popularmovies2.movietime.network.NetworkState;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SortedMoviesViewModel extends ViewModel {
    private static final String TAG = "TheaterViewModel";
    private LiveData<PagedList<Result>> moviesInTheaterList;

    private LiveData<NetworkState> networkStateLiveData;
    private Executor executor;
    private LiveData<NowPlayingDataSource> dataSource;


    public SortedMoviesViewModel(int mParam) {
        Log.d(TAG, "MoviesInTheaterViewModel: ");
        executor = Executors.newFixedThreadPool(5);
//        GetDataService webService = ServiceGenerator.createService(TMDBWebService.class);
        NowPlayingDataSourceFactory factory = new NowPlayingDataSourceFactory(executor, mParam);
        dataSource = factory.getMutableLiveData();

        networkStateLiveData = Transformations.switchMap(factory.getMutableLiveData(), new Function<NowPlayingDataSource, LiveData<NetworkState>>() {
            @Override
            public LiveData<NetworkState> apply(NowPlayingDataSource source) {
                Log.d(TAG, "apply: network change");
                return source.getNetworkState();
            }
        });

        PagedList.Config pageConfig = (new PagedList.Config.Builder())
                .setEnablePlaceholders(true)
                .setInitialLoadSizeHint(10)
                .setPageSize(20).build();

        moviesInTheaterList = (new LivePagedListBuilder<Long, Result>(factory, pageConfig)).setFetchExecutor(executor)
//                .setBackgroundThreadExecutor(executor)
                .build();


    }


    public LiveData<PagedList<Result>> getMoviesInTheaterList() {
        Log.d(TAG, "getMoviesInTheaterList: ");
        return moviesInTheaterList;
    }

    public LiveData<NetworkState> getNetworkStateLiveData() {
        return networkStateLiveData;
    }
}
