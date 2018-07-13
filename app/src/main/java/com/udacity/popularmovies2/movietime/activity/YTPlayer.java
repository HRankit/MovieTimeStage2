package com.udacity.popularmovies2.movietime.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.gson.Gson;
import com.udacity.popularmovies2.movietime.BuildConfig;
import com.udacity.popularmovies2.movietime.R;
import com.udacity.popularmovies2.movietime.adapter.YoutubeTrailerRecyclerViewAdapter;
import com.udacity.popularmovies2.movietime.model.details.Videos;

public class YTPlayer extends AppCompatActivity implements
        YouTubePlayer.OnInitializedListener, YouTubePlayer.PlayerStateChangeListener, YoutubeTrailerRecyclerViewAdapter.ItemClickListener {

    private String videoID;
    private YouTubePlayer Player;
    private boolean fullScreen;
    private String YT_API_KEY;
    private boolean isApiKeyPresent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_player);


        if (savedInstanceState != null) {
            fullScreen = savedInstanceState.getBoolean("fullScreen");
        }

        YT_API_KEY = BuildConfig.YOUTUBE_API_KEY;

        Gson gson = new Gson();
        String strObj = getIntent().getStringExtra("data");
        Videos v = gson.fromJson(strObj, Videos.class);
        videoID = v.getResults().get(0).getKey();


        initToolbar();
        YouTubePlayerSupportFragment youTubePlayerView = (YouTubePlayerSupportFragment) getSupportFragmentManager().findFragmentById(R.id.ytPlayer);
        isApiKeyPresent = checkIfApiKeyIsProvided();
        if (isApiKeyPresent) {
            youTubePlayerView.initialize(YT_API_KEY, YTPlayer.this);
        } else {
            youTubePlayerView.getView().setVisibility(View.GONE);
        }

        initRecyclerView(v);
    }

    private boolean checkIfApiKeyIsProvided() {
        String youtube_api_key = BuildConfig.YOUTUBE_API_KEY;
        if (TextUtils.isEmpty(youtube_api_key) || youtube_api_key.contains("YOUR_YOUTUBE_API_KEY")) {
            Toast.makeText(this, R.string.no_ytapi_key_Warning, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }


    private void initRecyclerView(Videos v) {
        RecyclerView recyclerView = findViewById(R.id.yt_other_recyclerview);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        YoutubeTrailerRecyclerViewAdapter adapter = new YoutubeTrailerRecyclerViewAdapter(this);
        adapter.setTasks(v.getResults());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
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


    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
        Player = youTubePlayer;
        Player.setOnFullscreenListener(new YouTubePlayer.OnFullscreenListener() {
            @Override
            public void onFullscreen(boolean _isFullScreen) {
                fullScreen = _isFullScreen;
                Player.play();
            }
        });

        if (!wasRestored) {
            Player.cueVideo(videoID);
            Player.setPlayerStateChangeListener(this);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putBoolean("fullscreen", fullScreen);
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        fullScreen = savedInstanceState.getBoolean("fullscreen");
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        if (youTubeInitializationResult.isUserRecoverableError()) {
            youTubeInitializationResult.getErrorDialog(this, 1).show();
        } else {
            String errorMessage = String.format(youTubeInitializationResult.toString());
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            getYouTubePlayerProvider().initialize(YT_API_KEY, this);
        }
    }

    private YouTubePlayer.Provider getYouTubePlayerProvider() {
        return (YouTubePlayerView) findViewById(R.id.ytPlayer);
    }

    @Override
    public void onLoading() {
        if (!TextUtils.isEmpty(videoID) && Player != null)
            Player.play();
    }

    @Override
    public void onLoaded(String s) {

    }

    @Override
    public void onAdStarted() {

    }

    @Override
    public void onVideoStarted() {

    }

    @Override
    public void onVideoEnded() {

    }

    @Override
    public void onError(YouTubePlayer.ErrorReason errorReason) {

    }

    @Override
    public void onBackPressed() {
        if (fullScreen) {
            Player.setFullscreen(false);
            Player.play();
        } else {
            if (Player != null) {
                Player.release();
            }
            super.onBackPressed();
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);

        }
    }


    @Override
    public void onItemClickListener(String itemId) {
        videoID = itemId;
        if (isApiKeyPresent) {
            Player.loadVideo(itemId);
        } else {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + videoID));
            startActivity(browserIntent);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
