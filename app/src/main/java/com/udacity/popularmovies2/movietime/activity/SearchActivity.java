package com.udacity.popularmovies2.movietime.activity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.udacity.popularmovies2.movietime.R;
import com.udacity.popularmovies2.movietime.adapter.SearchRecyclerViewAdapter;
import com.udacity.popularmovies2.movietime.model.GetDataService;
import com.udacity.popularmovies2.movietime.model.search.Search;
import com.udacity.popularmovies2.movietime.network.RetrofitClientInstance;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {

    private RecyclerView SearchResultsRV;
    private Call<Search> call;

    @Override
    public void onBackPressed() {
        if (call != null) {
            call.cancel();
        }
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        SearchResultsRV = findViewById(R.id.SearchResultsRV);
        final EditText editText = findViewById(R.id.EditText01);

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    clearTextAndHideKeyboard(editText);
                    return true;
                }
                return false;
            }
        });
        ImageButton Button01 = findViewById(R.id.Button01);
        Button01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearTextAndHideKeyboard(editText);
            }
        });

        initToolbar();
    }

    private void clearTextAndHideKeyboard(EditText editText) {
        editText.clearFocus();
        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        performSearch(editText.getText().toString());
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

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }

    private void performSearch(String text) {
        if (isNetworkAvailable(this)) {

            GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
            call = service.getSearch(text);
            call.enqueue(new Callback<Search>() {
                @Override
                public void onResponse(@NonNull Call<Search> call, @NonNull Response<Search> response) {
                    if (response.isSuccessful() && response.code() == 200) {
                        generateDataList(response.body());
                    } else {
                        Toast.makeText(SearchActivity.this, "Something went wrong.. Please try agian later", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Search> call, @NonNull Throwable t) {
                    if (call.isCanceled()) {
                        Toast.makeText(SearchActivity.this, "Request Cancelled by User." + t.toString(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SearchActivity.this, "other larger issue, i.e. no network connection?" + t.toString(), Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(SearchActivity.this, "Something went wrong...Please try later!" + t.toString(), Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }

            });

        } else {
            SearchResultsRV.setVisibility(View.GONE);
        }
    }

    private void generateDataList(Search rs) {
        SearchResultsRV.setLayoutManager(new LinearLayoutManager(this));
        SearchResultsRV.setItemAnimator(new DefaultItemAnimator());
        SearchRecyclerViewAdapter adapter = new SearchRecyclerViewAdapter(SearchActivity.this, rs.getResults());
        SearchResultsRV.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
