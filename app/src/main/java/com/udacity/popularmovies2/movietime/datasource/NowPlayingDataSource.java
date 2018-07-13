package com.udacity.popularmovies2.movietime.datasource;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.PageKeyedDataSource;
import android.support.annotation.NonNull;
import android.util.Log;

import com.udacity.popularmovies2.movietime.model.GetDataService;
import com.udacity.popularmovies2.movietime.model.main.Result;
import com.udacity.popularmovies2.movietime.model.main.RetroTMDB;
import com.udacity.popularmovies2.movietime.network.NetworkState;
import com.udacity.popularmovies2.movietime.network.RetrofitClientInstance;

import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.Executor;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.udacity.popularmovies2.movietime.utils.StaticConstants.DEFAULT_PAGE_NUMBER;
import static com.udacity.popularmovies2.movietime.utils.StaticConstants.NOW_PLAYING_MOVIES;
import static com.udacity.popularmovies2.movietime.utils.StaticConstants.POPULAR_MOVIES;
import static com.udacity.popularmovies2.movietime.utils.StaticConstants.TOP_RATED_MOVIES;
import static com.udacity.popularmovies2.movietime.utils.StaticConstants.UPCOMING_MOVIES;


public class NowPlayingDataSource extends PageKeyedDataSource<Long, Result> {
    private static final String TAG = "MoviesInTheaterDataSou";
    private final int parame;
    private final GetDataService tmdbWebService;
    private final MutableLiveData<NetworkState> networkState;
    private final MutableLiveData<NetworkState> initialLoading;
    private final Executor retryExecutor;
    private Call<RetroTMDB> call;

    public NowPlayingDataSource(Executor retryExecutor, int mParam) {

        tmdbWebService = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        networkState = new MutableLiveData<>();
        initialLoading = new MutableLiveData<>();
        this.retryExecutor = retryExecutor;
        this.parame = mParam;
    }


    public MutableLiveData<NetworkState> getNetworkState() {
        return networkState;
    }

    public MutableLiveData getInitialLoading() {
        return initialLoading;
    }


    @Override
    public void loadInitial(@NonNull LoadInitialParams<Long> params, @NonNull final LoadInitialCallback<Long, Result> callback) {
        Log.d(TAG, "loadInitial: ");
        initialLoading.postValue(NetworkState.LOADING);
        networkState.postValue(NetworkState.LOADING);

        switch (parame) {
            case POPULAR_MOVIES:
                call = tmdbWebService.getPopularMovies(DEFAULT_PAGE_NUMBER);
                break;
            case TOP_RATED_MOVIES:
                call = tmdbWebService.getTopRatedMovies(DEFAULT_PAGE_NUMBER);
                break;
            case UPCOMING_MOVIES:
                call = tmdbWebService.getUpcomingMovies(DEFAULT_PAGE_NUMBER);
                break;
            case NOW_PLAYING_MOVIES:
                call = tmdbWebService.getNowPlayingMovies(DEFAULT_PAGE_NUMBER);
                break;
        }


        call.enqueue(new Callback<RetroTMDB>() {
            @Override
            public void onResponse(@NonNull Call<RetroTMDB> call, @NonNull Response<RetroTMDB> response) {
                String responseString;
                List<Result> moviesList;
                if (response.isSuccessful() && response.code() == 200) {

                    initialLoading.postValue(NetworkState.LOADING);
                    networkState.postValue(NetworkState.LOADED);
                    callback.onResult(response.body().getResults(), null, (long) 2);

                } else {
                    Log.e(TAG, "onResponse error " + response.message());
                    initialLoading.postValue(new NetworkState(NetworkState.Status.FAILED, response.message()));
                    networkState.postValue(new NetworkState(NetworkState.Status.FAILED, response.message()));
                }
            }

            @Override
            public void onFailure(Call<RetroTMDB> call, Throwable t) {
                String errorMessage = t.getMessage();
                Log.e(TAG, "onFailure: " + errorMessage);
                networkState.postValue(new NetworkState(NetworkState.Status.FAILED, errorMessage));
            }

        });
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Long> params, @NonNull LoadCallback<Long, Result> callback) {

    }

    @Override
    public void loadAfter(@NonNull final LoadParams<Long> params, @NonNull final LoadCallback<Long, Result> callback) {
        networkState.postValue(NetworkState.LOADING);
        switch (parame) {
            case POPULAR_MOVIES:
                call = tmdbWebService.getPopularMovies(params.key);
                break;
            case TOP_RATED_MOVIES:
                call = tmdbWebService.getTopRatedMovies(params.key);
                break;
            case UPCOMING_MOVIES:
                call = tmdbWebService.getUpcomingMovies(params.key);
                break;
            case NOW_PLAYING_MOVIES:
                call = tmdbWebService.getNowPlayingMovies(params.key);
                break;
        }


        call.enqueue(new Callback<RetroTMDB>() {
            @Override
            public void onResponse(@NonNull Call<RetroTMDB> call, @NonNull Response<RetroTMDB> response) {
                JSONObject responseJson;
                String responseString;
                List<Result> moviesList;
                Long nextKey;

                if (response.isSuccessful() && response.code() == 200) {

                    initialLoading.postValue(NetworkState.LOADING);
                    networkState.postValue(NetworkState.LOADED);

                    nextKey = (params.key == response.body().getTotalPages()) ? null : params.key + 1;

                    callback.onResult(response.body().getResults(), nextKey);


                } else {
                    Log.e(TAG, "onResponse error " + response.message());
                    networkState.postValue(new NetworkState(NetworkState.Status.FAILED, response.message()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<RetroTMDB> call, @NonNull Throwable t) {
                String errorMessage = t.getMessage();
                Log.e(TAG, "onFailure: " + errorMessage);
                networkState.postValue(new NetworkState(NetworkState.Status.FAILED, errorMessage));
            }
        });
    }


}

