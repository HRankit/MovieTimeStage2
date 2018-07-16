package com.udacity.popularmovies2.movietime.activity;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.udacity.popularmovies2.movietime.BuildConfig;
import com.udacity.popularmovies2.movietime.MovieTime;
import com.udacity.popularmovies2.movietime.R;
import com.udacity.popularmovies2.movietime.adapter.FavMovieAdapter;
import com.udacity.popularmovies2.movietime.adapter.MoviePosterRecyclerViewAdapter;
import com.udacity.popularmovies2.movietime.database.MovieEntry;
import com.udacity.popularmovies2.movietime.model.main.RetroTMDB;
import com.udacity.popularmovies2.movietime.settings.MyPreferenceActivity;
import com.udacity.popularmovies2.movietime.viewmodel.MainViewModel;
import com.udacity.popularmovies2.movietime.viewmodel.NowPlayingViewModel;
import com.udacity.popularmovies2.movietime.viewmodel.PopularViewModel;
import com.udacity.popularmovies2.movietime.viewmodel.TopRatedViewModel;
import com.udacity.popularmovies2.movietime.viewmodel.UpcomingViewModel;

import java.util.List;

import static com.udacity.popularmovies2.movietime.utils.StaticConstants.NOW_PLAYING_MOVIES;
import static com.udacity.popularmovies2.movietime.utils.StaticConstants.POPULAR_MOVIES;
import static com.udacity.popularmovies2.movietime.utils.StaticConstants.TOP_RATED_MOVIES;
import static com.udacity.popularmovies2.movietime.utils.StaticConstants.UPCOMING_MOVIES;
import static com.udacity.popularmovies2.movietime.utils.StaticConstants.WHAT_TO_SHOW;

public class MainActivity extends AppCompatActivity {

    private RecyclerView nowPlayingRV, topRatedRV, popularRV, upcomingRV, favoriteRV;
    private TextView no_network_tv, now_playing_label, top_rated_label, popularMovies_label, upcomingMovies_label, favMovies_label;

    private ImageView no_network_image;
    private Button retry_button;

    private RelativeLayout noInternetLayout;
    private LinearLayout movielayout;
    private PopularViewModel popularViewModel;
    private TopRatedViewModel topRatedViewModel;
    private NowPlayingViewModel nowPlayingViewModel;
    private UpcomingViewModel upcomingViewModel;
    private String SCROLL_POSITION = "ARTICLE_SCROLL_POSITION";
    private String NOW_PLAYING_POSITION = "NOW_PLAYING_POSITION";
    private String POPULAR_POSITION = "POPULAR_POSITION";
    private String TOP_RATED_POSITION = "TOP_RATED_POSITION";
    private String UPCOMING_POSITION = "UPCOMING_POSITION";
    private String FAVORITE_POSITION = "FAVORITE_POSITION";
    private ScrollView mScrollView;


    private final BroadcastReceiver networkChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("app", "Network connectivity change");
            if (isNetworkAvailable(MainActivity.this)) {
                if (movielayout.getVisibility() != View.VISIBLE) {
                    manipulateViews(true);
                    forceViewModelRefresh();
                }
            } else {
                manipulateViews(false);
            }

        }

    };
    private Parcelable nowPlayingState;
    private Parcelable upcominState;
    private Parcelable topRatedState;
    private Parcelable popularState;
    private Parcelable favoriteState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        checkIfApiKeyProvidedOrExit();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);

        String country_preference = settings.getString("country_preference", "IN");
        String language_preference = settings.getString("language_preference", "en");

        MovieTime mt = new MovieTime();
        mt.setCounty(country_preference);
        mt.setLanguage(language_preference);

        initView();
        setupViewModel();
        checkNet();

    }

    private void forceViewModelRefresh() {
        nowPlayingViewModel.performRequest(getApplication());
        popularViewModel.performRequest(getApplication());
        topRatedViewModel.performRequest(getApplication());
        upcomingViewModel.performRequest(getApplication());
    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkChangeReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(MainActivity.this, MyPreferenceActivity.class));
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

            return true;
        }

        if (id == R.id.action_search) {
            startActivity(new Intent(MainActivity.this, SearchActivity.class));
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupViewModel() {
        final MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getFavMovies().observe(this, new Observer<List<MovieEntry>>() {
            @Override
            public void onChanged(@Nullable List<MovieEntry> movieEntries) {
                generateFavList(movieEntries, favoriteRV);
            }
        });


        popularViewModel = ViewModelProviders.of(this).get(PopularViewModel.class);
        popularViewModel.getPopularFirstPageMovies().observe(this, new Observer<RetroTMDB>() {
            @Override
            public void onChanged(@Nullable RetroTMDB movieEntries) {
                generateDataList(movieEntries, popularRV);
            }
        });

        topRatedViewModel = ViewModelProviders.of(this).get(TopRatedViewModel.class);
        topRatedViewModel.getTopRatedPageMovies().observe(this, new Observer<RetroTMDB>() {
            @Override
            public void onChanged(@Nullable RetroTMDB movieEntries) {
                generateDataList(movieEntries, topRatedRV);
            }
        });

        nowPlayingViewModel = ViewModelProviders.of(this).get(NowPlayingViewModel.class);
        nowPlayingViewModel.getNowPlayingPageMovies().observe(this, new Observer<RetroTMDB>() {
            @Override
            public void onChanged(@Nullable RetroTMDB movieEntries) {
                generateDataList(movieEntries, nowPlayingRV);
            }
        });

        upcomingViewModel = ViewModelProviders.of(this).get(UpcomingViewModel.class);
        upcomingViewModel.getUpcomingPageMovies().observe(this, new Observer<RetroTMDB>() {
            @Override
            public void onChanged(@Nullable RetroTMDB movieEntries) {
                generateDataList(movieEntries, upcomingRV);
            }
        });

    }


    private void generateDataList(RetroTMDB photoList, RecyclerView rv) {
        rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rv.setItemAnimator(new DefaultItemAnimator());
        MoviePosterRecyclerViewAdapter adapter = new MoviePosterRecyclerViewAdapter(MainActivity.this, photoList.getResults());
        rv.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        restoreScrollState();
    }

    private void restoreScrollState() {
        if (nowPlayingState != null) {
            nowPlayingRV.getLayoutManager().onRestoreInstanceState(nowPlayingState);
        }
        if (popularState != null) {
            popularRV.getLayoutManager().onRestoreInstanceState(popularState);
        }
        if (topRatedState != null) {
            topRatedRV.getLayoutManager().onRestoreInstanceState(topRatedState);
        }
        if (upcominState != null) {
            upcomingRV.getLayoutManager().onRestoreInstanceState(upcominState);
        }
        if (favoriteState != null) {
            favoriteRV.getLayoutManager().onRestoreInstanceState(favoriteState);
        }
    }

    private void generateFavList(List<MovieEntry> photoList, RecyclerView rv) {
        rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rv.setItemAnimator(new DefaultItemAnimator());
        FavMovieAdapter adapter = new FavMovieAdapter(MainActivity.this, photoList);
        rv.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void isNetAvailable(View view) {
        checkNet();
    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }


    private void manipulateViews(Boolean isItNet) {
        if (isItNet) {
            nowPlayingRV.setVisibility(View.VISIBLE);
            topRatedRV.setVisibility(View.VISIBLE);
            popularRV.setVisibility(View.VISIBLE);
            upcomingRV.setVisibility(View.VISIBLE);
            favoriteRV.setVisibility(View.VISIBLE);

            favMovies_label.setVisibility(View.VISIBLE);
            now_playing_label.setVisibility(View.VISIBLE);
            top_rated_label.setVisibility(View.VISIBLE);
            popularMovies_label.setVisibility(View.VISIBLE);
            upcomingMovies_label.setVisibility(View.VISIBLE);
            movielayout.setVisibility(View.VISIBLE);


            noInternetLayout.setVisibility(View.GONE);
            no_network_image.setVisibility(View.GONE);
            no_network_tv.setVisibility(View.GONE);
            retry_button.setVisibility(View.GONE);

        } else {
            nowPlayingRV.setVisibility(View.GONE);
            topRatedRV.setVisibility(View.GONE);
            popularRV.setVisibility(View.GONE);
            upcomingRV.setVisibility(View.GONE);
            favoriteRV.setVisibility(View.GONE);
            favMovies_label.setVisibility(View.GONE);
            now_playing_label.setVisibility(View.GONE);
            top_rated_label.setVisibility(View.GONE);
            popularMovies_label.setVisibility(View.GONE);
            upcomingMovies_label.setVisibility(View.GONE);
            movielayout.setVisibility(View.GONE);

            noInternetLayout.setVisibility(View.VISIBLE);
            favoriteRV.setVisibility(View.VISIBLE);
            favMovies_label.setVisibility(View.VISIBLE);
            no_network_image.setVisibility(View.VISIBLE);
            no_network_tv.setVisibility(View.VISIBLE);
            retry_button.setVisibility(View.VISIBLE);

        }
    }

    private void checkNet() {
        if (isNetworkAvailable(this)) {
            manipulateViews(true);
            forceViewModelRefresh();
        } else {
            manipulateViews(false);
        }
    }


    private void initView() {
        mScrollView = findViewById(R.id.mainact_scrollview);
        nowPlayingRV = findViewById(R.id.nowPlayingRV);
        topRatedRV = findViewById(R.id.topRatedRV);
        popularRV = findViewById(R.id.popularRV);
        upcomingRV = findViewById(R.id.upcomingRV);
        favoriteRV = findViewById(R.id.favoriteRV);
        no_network_image = findViewById(R.id.no_network_image);
        no_network_tv = findViewById(R.id.no_network_tv);
        retry_button = findViewById(R.id.retry_button);

        noInternetLayout = findViewById(R.id.noInternetLayout);
        movielayout = findViewById(R.id.movielayout);

        favMovies_label = findViewById(R.id.favMovies_label);
        now_playing_label = findViewById(R.id.now_playing_label);
        top_rated_label = findViewById(R.id.top_rated_label);
        popularMovies_label = findViewById(R.id.popularMovies_label);
        upcomingMovies_label = findViewById(R.id.upcomingMovies_label);

        Button showmore_now_playing_label = findViewById(R.id.showmore_now_playing_button);
        Button showmore_top_rated_label = findViewById(R.id.showmore_top_rated_label);
        Button showmore_popular_label = findViewById(R.id.showmore_popular_label);
        Button showmore_upcoming_label = findViewById(R.id.showmore_upcoming_label);


        Toolbar mTopToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mTopToolbar);

        scrollListener(topRatedRV, showmore_top_rated_label);
        scrollListener(nowPlayingRV, showmore_now_playing_label);
        scrollListener(upcomingRV, showmore_upcoming_label);
        scrollListener(popularRV, showmore_popular_label);


    }

    private void scrollListener(RecyclerView rv, final Button btn) {
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollHorizontally(1)) {
                    btn.setVisibility(View.VISIBLE);
                } else {
                    btn.setVisibility(View.GONE);
                }
            }
        });
    }

    private void checkIfApiKeyProvidedOrExit() {
        String tmdb_api_key = BuildConfig.TMDB_API_KEY;
        Log.d(getClass().getName(), tmdb_api_key);
        if (TextUtils.isEmpty(tmdb_api_key) || tmdb_api_key.contains("YOUR_TMDB_API_KEY")) {
            Toast.makeText(this, R.string.no_api_key_Warning, Toast.LENGTH_LONG).show();
            finish();
        }

    }


    public void nowPlayingShowMore(View view) {
        Intent i = new Intent(this, ListGridActivity.class);
        i.putExtra(WHAT_TO_SHOW, NOW_PLAYING_MOVIES);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

    }

    public void topRatedShowMore(View view) {
        Intent i = new Intent(this, ListGridActivity.class);
        i.putExtra(WHAT_TO_SHOW, TOP_RATED_MOVIES);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

    }

    public void upcomingShowMore(View view) {
        Intent i = new Intent(this, ListGridActivity.class);
        i.putExtra(WHAT_TO_SHOW, UPCOMING_MOVIES);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

    }

    public void popularShowMore(View view) {
        Intent i = new Intent(this, ListGridActivity.class);
        i.putExtra(WHAT_TO_SHOW, POPULAR_MOVIES);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putIntArray(SCROLL_POSITION,
                new int[]{mScrollView.getScrollX(), mScrollView.getScrollY()});
        if (nowPlayingRV != null) {
            outState.putParcelable(NOW_PLAYING_POSITION, nowPlayingRV.getLayoutManager().onSaveInstanceState());
        }
        if (topRatedRV != null) {
            outState.putParcelable(NOW_PLAYING_POSITION, topRatedRV.getLayoutManager().onSaveInstanceState());
        }
        if (popularRV != null) {
            outState.putParcelable(NOW_PLAYING_POSITION, popularRV.getLayoutManager().onSaveInstanceState());
        }
        if (favoriteRV != null) {
            outState.putParcelable(NOW_PLAYING_POSITION, favoriteRV.getLayoutManager().onSaveInstanceState());
        }
        if (upcomingRV != null) {
            outState.putParcelable(NOW_PLAYING_POSITION, upcomingRV.getLayoutManager().onSaveInstanceState());
        }
        super.onSaveInstanceState(outState);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        final int[] position = savedInstanceState.getIntArray(SCROLL_POSITION);
        if (position != null)
            mScrollView.post(new Runnable() {
                public void run() {
                    mScrollView.scrollTo(position[0], position[1]);
                }
            });

        if (savedInstanceState instanceof Bundle) {
            nowPlayingState = savedInstanceState.getParcelable(NOW_PLAYING_POSITION);
            upcominState = savedInstanceState.getParcelable(UPCOMING_POSITION);
            topRatedState = savedInstanceState.getParcelable(TOP_RATED_POSITION);
            popularState = savedInstanceState.getParcelable(POPULAR_POSITION);
            favoriteState = savedInstanceState.getParcelable(FAVORITE_POSITION);
        }
        super.onRestoreInstanceState(savedInstanceState);

    }
}
