package com.udacity.popularmovies2.movietime.activity;

import android.Manifest;
import android.app.DownloadManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.udacity.popularmovies2.movietime.R;
import com.udacity.popularmovies2.movietime.adapter.CastRecyclerViewAdapter;
import com.udacity.popularmovies2.movietime.adapter.GenreChipsRecyclerViewAdapter;
import com.udacity.popularmovies2.movietime.adapter.RecommendationsPosterRecyclerViewAdapter;
import com.udacity.popularmovies2.movietime.adapter.ReviewsPagerAdapter;
import com.udacity.popularmovies2.movietime.database.AppDatabase;
import com.udacity.popularmovies2.movietime.database.AppExecutors;
import com.udacity.popularmovies2.movietime.database.MovieEntry;
import com.udacity.popularmovies2.movietime.model.GetDataService;
import com.udacity.popularmovies2.movietime.model.details.Details;
import com.udacity.popularmovies2.movietime.model.details.Videos;
import com.udacity.popularmovies2.movietime.network.DetailRetrofitClient;
import com.udacity.popularmovies2.movietime.viewmodel.DetailsViewModel;
import com.udacity.popularmovies2.movietime.viewmodel.DetailsViewModelFactory;

import java.io.File;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.udacity.popularmovies2.movietime.utils.StaticConstants.MOVIE_ID_KEY;

public class Details2Activity extends AppCompatActivity {
    private String movie_id;
    private Videos videos;
    private Details photoList;

    private AppDatabase mDb;
    private Boolean isMovieFav;
    private ImageButton favHearButton;
    private Call<Details> call;
    private MenuItem shareMenuItem;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_details);

        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey("movie_id")) {
            movie_id = extras.get(MOVIE_ID_KEY).toString();


            mDb = AppDatabase.getInstance(getApplicationContext());
            initViewModel(movie_id);

            Log.d(getClass().getName(), "This is the movie ID: " + movie_id);

        } else {
            onBackPressed();
        }

        initToolbar();

        if (isNetworkAvailable(this)) {
            networkAndPopulateUI(movie_id);
        } else {
            initViewModel1(movie_id);
        }

    }


    private void shareDetails() {
        String MovieID = String.valueOf(photoList.getId());
        String url = "https://themoviedb.org/movie/" + MovieID;
        String link = "<a href='" + url + "'>Clicking Here</a> or visit \r\n" + url;
        String shareBody = "Check out the original web page of " + photoList.getTitle() + " on The Movie DB by " + link;

        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/html");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Checkout this Movie");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(shareBody));
        startActivity(Intent.createChooser(sharingIntent, "Share using:"));
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

    private void initViewModel(String movie_id) {
        DetailsViewModelFactory addTaskViewModelFactory = new DetailsViewModelFactory(mDb, Integer.parseInt(movie_id));
        final DetailsViewModel addTaskViewModel = ViewModelProviders.of(this, addTaskViewModelFactory).get(DetailsViewModel.class);
        addTaskViewModel.checkIfMovieExists().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean isFav) {
                addTaskViewModel.checkIfMovieExists().removeObserver(this);
                markFavOrNot(isFav);
            }
        });


    }

    private void initViewModel1(String movie_id) {
        DetailsViewModelFactory addTaskViewModelFactory = new DetailsViewModelFactory(mDb, Integer.parseInt(movie_id));
        final DetailsViewModel addTaskViewModel = ViewModelProviders.of(this, addTaskViewModelFactory).get(DetailsViewModel.class);
        addTaskViewModel.getMovieData().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String taskEntry) {
                addTaskViewModel.getMovieData().removeObserver(this);
                Gson gson = new Gson();
                Details details = gson.fromJson(taskEntry, Details.class);
                generateDataList(details);

            }
        });


    }

    @Override
    public void onBackPressed() {
        if (call != null) {
            call.cancel();
        }
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);

    }

    private void markFavOrNot(Boolean isFav) {
        if (isFav == null) {
            return;
        }
        favHearButton = findViewById(R.id.markMovieFav);
        isMovieFav = isFav;

        if (!isFav) {
            favHearButton.setImageResource(R.drawable.ic_heart_unfilled);
        } else {
            favHearButton.setImageResource(R.drawable.ic_heart);
        }
    }


    private void networkAndPopulateUI(String movie_id) {

        GetDataService service = DetailRetrofitClient.getDetailRetrofitInstance().create(GetDataService.class);
        call = service.getMovieWithID(movie_id);
        call.enqueue(new Callback<Details>() {
            @Override
            public void onResponse(@NonNull Call<Details> call, @NonNull Response<Details> response) {
                generateDataList(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<Details> call, @NonNull Throwable t) {
                if (call.isCanceled()) {
                    Toast.makeText(Details2Activity.this, "Request Cancelled by User." + t.toString(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Details2Activity.this, "other larger issue, i.e. no network connection?" + t.toString(), Toast.LENGTH_SHORT).show();
                }
                Toast.makeText(Details2Activity.this, "Something went wrong...Please try later!" + t.toString(), Toast.LENGTH_SHORT).show();
                onBackPressed();
            }

        });
    }

    private void generateDataList(Details resultList) {
        photoList = resultList;
        loadBackDrop();

        if (isMovieFav) {
            DeleteMovieFromDB();
            downloadAndSaveLatestData();
        }

        Drawable somevectordrable;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            somevectordrable = getDrawable(R.drawable.left_trailer_icon);
        } else {
            somevectordrable = getResources().getDrawable(R.drawable.playbutton_svg_black);
        }

        Button play_trailer = findViewById(R.id.play_trailer);
        play_trailer.setCompoundDrawablesWithIntrinsicBounds(somevectordrable, null, null, null);

        shareMenuItem.setVisible(true);

        TextView movie_title = findViewById(R.id.movie_title_tv);
        movie_title.setText(photoList.getTitle());

        TextView movie_overview = findViewById(R.id.movie_overview_tv);
        if (TextUtils.isEmpty(photoList.getOverview())) {
            movie_overview.setText(R.string.overview_error_display);
            movie_overview.setGravity(Gravity.CENTER);
        } else {
            movie_overview.setText(photoList.getOverview());
        }

        TextView runtime_tv = findViewById(R.id.runtime_tv);
        runtime_tv.setText(photoList.getRuntimeInString());

        TextView user_rating_tv = findViewById(R.id.user_rating_tv);
        user_rating_tv.setText(photoList.getVoteAverage());

        TextView vote_count_tv = findViewById(R.id.vote_count_tv);
        vote_count_tv.setText(String.valueOf(photoList.getVoteCount()));

        TextView budget_tv = findViewById(R.id.budget_tv);
        budget_tv.setText(String.valueOf(photoList.getBudget()));

        TextView revenue_tv = findViewById(R.id.revenue_tv);
        revenue_tv.setText(String.valueOf(photoList.getRevenue()));

        TextView homepage_tv = findViewById(R.id.homepage_tv);
        final String homepage = photoList.getHomepage();
        homepage_tv.setText(homepage);
        homepage_tv.setTypeface(null, Typeface.BOLD_ITALIC);
        homepage_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(homepage));
                startActivity(browserIntent);
            }
        });


        TextView release_date_tv = findViewById(R.id.release_date_tv);
        release_date_tv.setText(photoList.getReleaseDate());


        TextView imdb_id_tv = findViewById(R.id.imdb_id_tv);
        imdb_id_tv.setText(getString(R.string.click_here));
        final String imdburl = photoList.getImdbId();
        imdb_id_tv.setTypeface(null, Typeface.BOLD_ITALIC);

        imdb_id_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(imdburl));
                startActivity(browserIntent);
            }
        });


        RecyclerView genre_chips_layout = findViewById(R.id.genre_chips_layout);
        genre_chips_layout.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        genre_chips_layout.setAdapter(new GenreChipsRecyclerViewAdapter(photoList.getGenres()));


        RecyclerView castRecyclerview = findViewById(R.id.castRecyclerview);
        castRecyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        castRecyclerview.setAdapter(new CastRecyclerViewAdapter(photoList.getCredits().getCast()));

        ViewPager reviewsViewPager = findViewById(R.id.reviews_pager);
        TabLayout tabLayout = findViewById(R.id.tab_layout);

        RecyclerView recommendationsRV = findViewById(R.id.recommendationsRV);
        recommendationsRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recommendationsRV.setAdapter(new RecommendationsPosterRecyclerViewAdapter(this, photoList.getRecommendations().getResults()));

        RecyclerView similarRV = findViewById(R.id.similarRV);
        similarRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        similarRV.setAdapter(new RecommendationsPosterRecyclerViewAdapter(this, photoList.getSimilar().getResults()));


        if (photoList.getReviews().getResults().size() > 0) {
            ReviewsPagerAdapter reviewsPagerAdapter = new ReviewsPagerAdapter(this, photoList.getReviews().getResults());
            reviewsViewPager.setAdapter(reviewsPagerAdapter);

            tabLayout.setupWithViewPager(reviewsViewPager, true);
        } else {
            reviewsViewPager.setVisibility(View.GONE);
            tabLayout.setVisibility(View.GONE);
            TextView review_label = findViewById(R.id.review_label);
            review_label.setGravity(Gravity.CENTER);
            review_label.setText(R.string.no_reviews);
        }
        videos = photoList.getVideos();
    }

    private void loadBackDrop() {
        ImageView iv = findViewById(R.id.cover_image);

        File file = new File(Environment.DIRECTORY_DOWNLOADS, "/MovieTime/" + "/" + photoList.getId() + ".png");
        if (file.exists()) {
            Picasso.get()
                    .load(file)
                    .fit()
                    .placeholder(R.drawable.progress_animation)
                    .error(R.drawable.ic_launcher_background)
                    .into(iv);
        } else {
            Picasso.get()
                    .load(getString(R.string.url_image_w342) + photoList.getBackdropPath())
                    .fit()
                    .placeholder(R.drawable.progress_animation)
                    .error(R.drawable.ic_launcher_background)
                    .into(iv);
        }
    }


    public void showTrailer(View view) {
        if (videos.getResults().size() > 0) {
            Intent i = new Intent(this, YTPlayer.class);
            Gson gson = new Gson();
            i.putExtra("data", gson.toJson(videos));
            startActivity(i);
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        } else {
            Toast.makeText(Details2Activity.this, "Videos are currently unable. Please try again later...", Toast.LENGTH_LONG).show();
        }

    }


    public void markMovieFav(View view) {
        markUnmarMovieFav();
    }

    private void markUnmarMovieFav() {
        if (isMovieFav) {
            DeleteMovieFromDB();
            favHearButton.setImageResource(R.drawable.ic_heart_unfilled);
            isMovieFav = false;
        } else {
            downloadAndSaveLatestData();
        }
    }

    private void DeleteMovieFromDB() {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDb.movieDao().DeleteMovie(Integer.parseInt(movie_id));
            }
        });
    }

    private void downloadAndSaveLatestData() {
        Gson gson = new Gson();


        final MovieEntry movie = new MovieEntry(gson.toJson(photoList), Integer.parseInt(movie_id), photoList.getTitle());
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDb.movieDao().insertTask(movie);
            }
        });
        favHearButton.setImageResource(R.drawable.ic_heart);
        isMovieFav = true;
        DownloadFile();
    }

    private void DownloadFile() {

        if (isStoragePermissionGranted()) {
            Uri download = Uri.parse(getString(R.string.url_image_w342) + photoList.getBackdropPath());

            DownloadManager.Request request = new DownloadManager.Request(download);
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
            request.setAllowedOverRoaming(false);
            request.setTitle("Downloading " + photoList.getOriginalTitle() + ".png");
            request.setDescription("Saving the movie for offline mode.");
            request.setVisibleInDownloadsUi(true);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "/MovieTime/" + "/" + photoList.getId() + ".png");

            DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            if (downloadManager != null) {
                downloadManager.enqueue(request);
            }
        }
    }

    private boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            DownloadFile();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        shareMenuItem = menu.findItem(R.id.action_share);
        shareMenuItem.setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_share:
                shareDetails();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void toggleView(final View view) {

        final View v = findViewById(R.id.movie_details_info);

        Button show_more_button = findViewById(R.id.show_more_button);


        if (v.getVisibility() == View.VISIBLE) {
            show_more_button.setText(R.string.show_info_button_label);

            TranslateAnimation animate = new TranslateAnimation(0, v.getWidth(), 0, 0);
            animate.setDuration(500);
            animate.setFillAfter(true);
            v.startAnimation(animate);
            v.setVisibility(View.GONE);
        } else {
            show_more_button.setText(R.string.hide_info_button_label);

            TranslateAnimation animate = new TranslateAnimation(v.getWidth(), 0, 0, 0);
            animate.setDuration(500);
            animate.setFillAfter(true);
            v.startAnimation(animate);
            v.setVisibility(View.VISIBLE);


        }
    }
}
