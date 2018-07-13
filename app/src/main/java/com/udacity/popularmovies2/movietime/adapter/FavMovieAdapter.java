package com.udacity.popularmovies2.movietime.adapter;

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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.udacity.popularmovies2.movietime.R;
import com.udacity.popularmovies2.movietime.activity.Details2Activity;
import com.udacity.popularmovies2.movietime.database.MovieEntry;
import com.udacity.popularmovies2.movietime.model.details.Details;
import com.udacity.popularmovies2.movietime.utils.MiscFunctions;

import java.util.List;

import static com.udacity.popularmovies2.movietime.utils.StaticConstants.MOVIE_ID_KEY;
import static com.udacity.popularmovies2.movietime.utils.StaticConstants.POSTER_URL_KEY;

public class FavMovieAdapter extends RecyclerView.Adapter<FavMovieAdapter.MyViewHolder1> {
    private final List<MovieEntry> resultList;
    private final Context mContext;
    private int lastPosition = -1;


    public FavMovieAdapter(Context context, List<MovieEntry> resultList) {
        this.resultList = resultList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public FavMovieAdapter.MyViewHolder1 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.horizontal_poster_item, parent, false);

        return new FavMovieAdapter.MyViewHolder1(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final FavMovieAdapter.MyViewHolder1 holder, final int position) {

        String str = resultList.get(position).getDetails();

        Gson gson = new Gson();
        Details details = gson.fromJson(str, Details.class);


        holder.card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigate(position);
            }
        });
        holder.movie_title.setText(details.getOriginalTitle());
        holder.movie_year.setText(details.getReleaseDate());
        holder.movie_rating.setText(details.getVoteAverage());

        MiscFunctions mf = new MiscFunctions();
        String genres = mf.genreFromText(details.getGenres());
        holder.movie_genre.setText(genres);


        holder.movie_poster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigate(position);
            }
        });


        Picasso.get()
                .load("http://image.tmdb.org/t/p/w185" + details.getPosterPath())
                .placeholder(R.drawable.progress_animation)
                .error(R.drawable.loader)
                .into(holder.movie_poster);
        setAnimation(holder.card_view, position);
    }

    private void navigate(int position) {

        String str = resultList.get(position).getDetails();

        Gson gson = new Gson();
        Details d = gson.fromJson(str, Details.class);


        Activity activity = (Activity) mContext;

        long i = d.getId();

        String posterURL = d.getPosterPath();

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

    public class MyViewHolder1 extends RecyclerView.ViewHolder {
        final ImageView movie_poster;
        final TextView movie_title;
        final TextView movie_year, movie_genre;
        final TextView movie_rating;
        final RelativeLayout movie_poster_background;
        private final CardView card_view;

        MyViewHolder1(View itemView) {
            super(itemView);
            movie_title = itemView.findViewById(R.id.person_name);
            movie_poster = itemView.findViewById(R.id.movie_poster);
            movie_year = itemView.findViewById(R.id.movie_year);
            movie_rating = itemView.findViewById(R.id.movie_rating);
            movie_genre = itemView.findViewById(R.id.movie_genre);
            movie_poster_background = itemView.findViewById(R.id.movie_poster_background);
            card_view = itemView.findViewById(R.id.card_view);
        }
    }
}
