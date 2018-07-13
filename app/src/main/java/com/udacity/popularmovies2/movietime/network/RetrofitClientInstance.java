package com.udacity.popularmovies2.movietime.network;

import com.udacity.popularmovies2.movietime.MovieTime;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClientInstance {

    private static final String BASE_URL = " http://api.themoviedb.org/3/";
    private static Retrofit retrofit;
    private static Interceptor apiKeyInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {

            String country = MovieTime.getCountry();
            String language = MovieTime.getLanguage();

            Request originalRequest = chain.request();
            HttpUrl originalHttpUrl = originalRequest.url();

            HttpUrl url = originalHttpUrl
                    .newBuilder()
                    .addQueryParameter("region", country)
                    .addQueryParameter("language", language)
                    .build();
            Request request = originalRequest.newBuilder().url(url).build();
            return chain.proceed(request);
        }
    };

    public static Retrofit getRetrofitInstance() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(apiKeyInterceptor)
                .addInterceptor(interceptor)
                .build();

        if (retrofit == null) {
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }

        return retrofit;
    }
}
