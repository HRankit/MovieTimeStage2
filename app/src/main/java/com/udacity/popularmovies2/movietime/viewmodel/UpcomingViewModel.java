package com.udacity.popularmovies2.movietime.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.udacity.popularmovies2.movietime.model.GetDataService;
import com.udacity.popularmovies2.movietime.model.main.RetroTMDB;
import com.udacity.popularmovies2.movietime.network.RetrofitClientInstance;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.udacity.popularmovies2.movietime.utils.StaticConstants.DEFAULT_PAGE_NUMBER;

public class UpcomingViewModel extends AndroidViewModel {


    private String TAG = getClass().getSimpleName();
    private MutableLiveData<RetroTMDB> movieList;
    private RetroTMDB result;

    public UpcomingViewModel(@NonNull final Application application) {
        super(application);
        performRequest(application);

    }

    public void performRequest(@NonNull final Application application) {
        GetDataService tmdbWebService = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<RetroTMDB> call = tmdbWebService.getUpcomingMovies(DEFAULT_PAGE_NUMBER);


        call.enqueue(new Callback<RetroTMDB>() {
            @Override
            public void onResponse(Call<RetroTMDB> call, Response<RetroTMDB> response) {
                if (response.isSuccessful() && response.code() == 200) {
                    Log.e("PopularMovieSteps", "Performed Request " + TAG);

                    result = response.body();
                    movieList.postValue(result);
                } else {
                    Log.e("PopularMovieSteps", "onResponse error " + response.message());
                }
            }

            @Override
            public void onFailure(Call<RetroTMDB> call, Throwable t) {
                Toast.makeText(application.getApplicationContext(), "Something went wrong...Please try later!" + t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public MutableLiveData<RetroTMDB> getUpcomingPageMovies() {
        if (movieList == null) {
            movieList = new MutableLiveData<RetroTMDB>();
        }
        return movieList;
    }
}