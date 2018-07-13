package com.udacity.popularmovies2.movietime.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.udacity.popularmovies2.movietime.R;
import com.udacity.popularmovies2.movietime.activity.Details2Activity;
import com.udacity.popularmovies2.movietime.model.details.MovieRecommendations;
import com.udacity.popularmovies2.movietime.utils.MiscFunctions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import static com.udacity.popularmovies2.movietime.utils.StaticConstants.MOVIE_ID_KEY;
import static com.udacity.popularmovies2.movietime.utils.StaticConstants.POSTER_URL_KEY;


public class RecommendationsPosterRecyclerViewAdapter extends RecyclerView.Adapter<RecommendationsPosterRecyclerViewAdapter.RecommendationsViewHolder> {
    private final Context mContext;
    private final List<MovieRecommendations> resultList;
    private int lastPosition = -1;

    public RecommendationsPosterRecyclerViewAdapter(Context context, List<MovieRecommendations> resultList) {
        this.resultList = resultList;
        this.mContext = context;
    }


    @NonNull
    @Override
    public RecommendationsPosterRecyclerViewAdapter.RecommendationsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.horizontal_poster_item, parent, false);

        return new RecommendationsPosterRecyclerViewAdapter.RecommendationsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecommendationsPosterRecyclerViewAdapter.RecommendationsViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        holder.card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigate(position);
            }
        });
        holder.movie_title.setText(resultList.get(position).getOriginalTitle());
        holder.movie_year.setText(sendBackYear(resultList.get(position).getReleaseDate()));
        holder.movie_rating.setText(resultList.get(position).getVoteAverage());

        MiscFunctions mf = new MiscFunctions();
        String genres = mf.genreFromID(resultList.get(position).getGenreIds());
        holder.movie_genre.setText(genres);


        holder.movie_poster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigate(position);
            }
        });
        Picasso.get()
                .load(resultList.get(position).getPosterPath())
                .placeholder(R.drawable.progress_animation)
                .error(R.drawable.loader)
                .into(holder.movie_poster);
        setAnimation(holder.card_view, position);
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

    private void navigate(int position) {

        Activity activity = (Activity) mContext;

        long i = resultList.get(position).getId();
        String posterURL = resultList.get(position).getPosterPath();

        Intent myIntent = new Intent(mContext, Details2Activity.class);
        myIntent.putExtra(POSTER_URL_KEY, posterURL);
        myIntent.putExtra(MOVIE_ID_KEY, i);

        activity.startActivity(myIntent);
        activity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return resultList.size();
    }


    class RecommendationsViewHolder extends RecyclerView.ViewHolder {
        final ImageView movie_poster;
        final TextView movie_title;
        final TextView movie_year, movie_genre;
        final TextView movie_rating;
        private final CardView card_view;

        RecommendationsViewHolder(View itemView) {
            super(itemView);
            movie_title = itemView.findViewById(R.id.person_name);
            movie_poster = itemView.findViewById(R.id.movie_poster);
            movie_year = itemView.findViewById(R.id.movie_year);
            movie_rating = itemView.findViewById(R.id.movie_rating);
            movie_genre = itemView.findViewById(R.id.movie_genre);
            card_view = itemView.findViewById(R.id.card_view);
        }
    }


}
