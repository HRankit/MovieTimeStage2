package com.udacity.popularmovies2.movietime.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.arch.paging.PagedListAdapter;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.udacity.popularmovies2.movietime.R;
import com.udacity.popularmovies2.movietime.activity.Details2Activity;
import com.udacity.popularmovies2.movietime.model.main.Result;
import com.udacity.popularmovies2.movietime.network.NetworkState;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import static com.udacity.popularmovies2.movietime.utils.StaticConstants.MOVIE_ID_KEY;
import static com.udacity.popularmovies2.movietime.utils.StaticConstants.POSTER_URL_KEY;

@SuppressWarnings("ALL")
public class ListGridAdapter extends PagedListAdapter<Result, RecyclerView.ViewHolder> {
    private static final int MOVIE_ITEM_VIEW_TYPE = 1;
    private static final int LOAD_ITEM_VIEW_TYPE = 0;
    private final Context mContext;
    private NetworkState mNetworkState;


    public ListGridAdapter(Context context) {
        super(Result.DIFF_CALL);
        mContext = context;
    }


    @Override
    public int getItemViewType(int position) {
        return (isLoadingData() && position == getItemCount() - 1) ? LOAD_ITEM_VIEW_TYPE : MOVIE_ITEM_VIEW_TYPE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;

        if (viewType == MOVIE_ITEM_VIEW_TYPE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.mainact_poster_item, parent, false);

            return new MyViewHolder1(view);
        } else {
            view = inflater.inflate(R.layout.load_progress_item, parent, false);
            return new ProgressViewHolder(view);
        }


    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyViewHolder1) {
            MyViewHolder1 movieViewHolder = (MyViewHolder1) holder;
            Result movie = getItem(position);
            movieViewHolder.bind(movie);

        }
    }


    private boolean isLoadingData() {
        return (mNetworkState != null && mNetworkState != NetworkState.LOADED);
    }

    public void setNetworkState(NetworkState networkState) {
        boolean wasLoading = isLoadingData();
        mNetworkState = networkState;
        boolean willLoad = isLoadingData();
        if (wasLoading != willLoad) {
            if (wasLoading) notifyItemRemoved(getItemCount());
            else notifyItemInserted(getItemCount());
        }
    }

    private void navigate(int tmdb_id, String poster_url) {

        Activity activity = (Activity) mContext;

        Intent myIntent = new Intent(mContext, Details2Activity.class);
        myIntent.putExtra(POSTER_URL_KEY, poster_url);
        myIntent.putExtra(MOVIE_ID_KEY, (long) tmdb_id);

        activity.startActivity(myIntent);
        activity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

    }

    private String sendBackYear(String str) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
        try {
            return dateFormat.format(dateFormat.parse(str));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    private static class ProgressViewHolder extends RecyclerView.ViewHolder {

        ProgressViewHolder(View itemView) {
            super(itemView);
        }
    }

    class MyViewHolder1 extends RecyclerView.ViewHolder {
        final ImageView movie_poster;
        final TextView movie_title;
        final TextView movie_year;
        final TextView movie_rating;

        MyViewHolder1(View itemView) {
            super(itemView);
            movie_title = itemView.findViewById(R.id.person_name);
            movie_poster = itemView.findViewById(R.id.movie_poster);
            movie_year = itemView.findViewById(R.id.movie_year);
            movie_rating = itemView.findViewById(R.id.movie_rating);
        }

        void bind(Result movie) {
            movie_title.setText(movie.getOriginalTitle());
            Picasso.get()
                    .load(movie.getPosterPath())
                    .placeholder(R.drawable.progress_animation)
                    .error(R.drawable.loader)
                    .into(movie_poster);
            movie_title.setText(movie.getOriginalTitle());
            movie_year.setText(sendBackYear(movie.getReleaseDate()));
            movie_rating.setText(movie.getVoteAverage());
            final int id = movie.getId();
            final String poster_url = movie.getPosterPath();

            movie_poster.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    navigate(id, poster_url);
                }
            });
        }
    }


}
