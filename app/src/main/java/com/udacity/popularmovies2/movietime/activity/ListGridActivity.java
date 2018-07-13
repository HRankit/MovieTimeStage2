package com.udacity.popularmovies2.movietime.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.udacity.popularmovies2.movietime.BuildConfig;
import com.udacity.popularmovies2.movietime.R;
import com.udacity.popularmovies2.movietime.adapter.ListGridAdapter;
import com.udacity.popularmovies2.movietime.model.main.Result;
import com.udacity.popularmovies2.movietime.network.NetworkState;
import com.udacity.popularmovies2.movietime.settings.MyPreferenceActivity;
import com.udacity.popularmovies2.movietime.viewmodel.CustomViewModel;
import com.udacity.popularmovies2.movietime.viewmodel.SortedMoviesViewModel;

import static com.udacity.popularmovies2.movietime.utils.StaticConstants.NOTHING_TO_SHOW;
import static com.udacity.popularmovies2.movietime.utils.StaticConstants.WHAT_TO_SHOW;

@SuppressWarnings("ALL")
public class ListGridActivity extends AppCompatActivity {

    private static final String WHICH_CATEGORY_SHOWN = "isItPopularOrTop";
    private TextView no_network_tv;
    private ImageView no_network_image;
    private Button retry_button;
    private RecyclerView recyclerView;
    private int whatToShow;
    private ListGridAdapter adapter;

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt(WHICH_CATEGORY_SHOWN, whatToShow);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        whatToShow = savedInstanceState.getInt(WHICH_CATEGORY_SHOWN);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_grid);
        if (checkIfApiKeyIsProvided()) {
            Toast.makeText(this, R.string.no_api_key_Warning, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        recyclerView = findViewById(R.id.recyclerView);
        no_network_image = findViewById(R.id.no_network_image);
        no_network_tv = findViewById(R.id.no_network_tv);
        retry_button = findViewById(R.id.retry_button);

        Intent intent = getIntent();
        whatToShow = intent.getIntExtra(WHAT_TO_SHOW, NOTHING_TO_SHOW);


        checkNet(whatToShow);
        initToolbar();


    }

    private boolean checkIfApiKeyIsProvided() {
        String api_key = BuildConfig.TMDB_API_KEY;
        return TextUtils.isEmpty(api_key) || api_key.contains("YOUR_TMDB_API_KEY");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_listgrid, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_search:
                startActivity(new Intent(ListGridActivity.this, SearchActivity.class));
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                return true;
            case R.id.action_settings:
                startActivity(new Intent(ListGridActivity.this, MyPreferenceActivity.class));
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void checkNet(int whatToShow) {
        if (isNetworkAvailable(this)) {
            manipulateViews(true);
            performRequestLoadUI(whatToShow);
        } else {
            manipulateViews(false);
        }
    }


    private void performRequestLoadUI(int whichRequest) {
        showMoviesInGrid(whichRequest);
    }

    private void initToolbar() {
        Toolbar mTopToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mTopToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle("MovieTime");
        }
    }

    private void showMoviesInGrid(int whatToShow) {
        adapter = new ListGridAdapter(ListGridActivity.this);

        adapter.notifyDataSetChanged();


        SortedMoviesViewModel mMoviesViewModel = ViewModelProviders.of(this, new CustomViewModel(whatToShow)).get(SortedMoviesViewModel.class);
        mMoviesViewModel.getMoviesInTheaterList().observe(this, new Observer<PagedList<Result>>() {
            @Override
            public void onChanged(@Nullable PagedList<Result> movies) {
                adapter.submitList(movies);
            }
        });


        mMoviesViewModel.getNetworkStateLiveData().observe(this, new Observer<NetworkState>() {
            @Override
            public void onChanged(@Nullable NetworkState networkState) {
                adapter.setNetworkState(networkState);
            }
        });

        RecyclerView.LayoutManager mLayoutManager;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mLayoutManager = new GridLayoutManager(this, 2);
        } else {
            mLayoutManager = new GridLayoutManager(this, 4);
        }

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

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

    public void isNetAvailable(View view) {
        checkNet(whatToShow);
    }

    private void manipulateViews(Boolean isItNet) {
        if (isItNet) {
            recyclerView.setVisibility(View.VISIBLE);
            no_network_image.setVisibility(View.GONE);
            no_network_tv.setVisibility(View.GONE);
            retry_button.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.GONE);
            no_network_image.setVisibility(View.VISIBLE);
            no_network_tv.setVisibility(View.VISIBLE);
            retry_button.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);

    }
}
