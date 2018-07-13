package com.udacity.popularmovies2.movietime.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.udacity.popularmovies2.movietime.R;
import com.udacity.popularmovies2.movietime.activity.Details2Activity;
import com.udacity.popularmovies2.movietime.model.search.KnownFor;
import com.udacity.popularmovies2.movietime.model.search.SearchResults;

import java.util.List;

import static com.udacity.popularmovies2.movietime.utils.StaticConstants.MEDIA_TYPE;
import static com.udacity.popularmovies2.movietime.utils.StaticConstants.MOVIE_ID_KEY;

public class SearchRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int MOVIE_ITEM_VIEW_TYPE = 1;
    private static final int PERSON_ITEM_VIEW_TYPE = 2;
    private static final int TV_ITEM_VIEW_TYPE = 3;
    private final Context mContext;
    private final List<SearchResults> resultList;

    public SearchRecyclerViewAdapter(Context context, List<SearchResults> searchResults) {
        mContext = context;
        resultList = searchResults;
    }

    @Override
    public int getItemViewType(int position) {
        return isItMovieOrPerson(position);
    }

    private int isItMovieOrPerson(int position) {

        switch (resultList.get(position).getMediaType()) {
            case "movie":
                return MOVIE_ITEM_VIEW_TYPE;

            case "person":
                return PERSON_ITEM_VIEW_TYPE;

            case "tv":
                return TV_ITEM_VIEW_TYPE;

            default:
                return 0;
        }
    }

    @Override
    public int getItemCount() {
        return resultList.size();
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case MOVIE_ITEM_VIEW_TYPE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.movie_search_item, parent, false);
                return new MovieViewHolder(view);

            case PERSON_ITEM_VIEW_TYPE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.person_search_item, parent, false);
                return new PersonViewHolder(view);
            default:
                LayoutInflater layoutInflater =
                        LayoutInflater.from(parent.getContext());
                view = layoutInflater.inflate(
                        android.R.layout.simple_list_item_1,
                        parent, false);
                return new TodoHolder(view);

        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MovieViewHolder) {
            MovieViewHolder movieViewHolder = (MovieViewHolder) holder;
            movieViewHolder.bind(resultList.get(position), position);
        } else if (holder instanceof PersonViewHolder) {
            PersonViewHolder personViewHolder = (PersonViewHolder) holder;
            personViewHolder.bind(resultList.get(position), position);

        }
    }

    private String convertListToString(List<KnownFor> kf) {
        StringBuilder a = new StringBuilder();
        a.append("KNOWN FOR: \r\n");

        for (int i = 0; i < kf.size(); i++) {
            a.append(kf.get(i).getOriginalTitle()).append(", ");
        }
        return a.toString();
    }

    private void navigate(int position, int ItemViewType) {
        Activity activity = (Activity) mContext;
        Intent myIntent;
        if (ItemViewType == MOVIE_ITEM_VIEW_TYPE) {
            long i = resultList.get(position).getId();
            myIntent = new Intent(mContext, Details2Activity.class);
            myIntent.putExtra(MOVIE_ID_KEY, i);
            myIntent.putExtra(MEDIA_TYPE, ItemViewType);
            activity.startActivity(myIntent);
            activity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        }
    }

    class TodoHolder extends RecyclerView.ViewHolder {

        TodoHolder(View itemView) {
            super(itemView);
        }
    }

    class MovieViewHolder extends RecyclerView.ViewHolder {
        final ImageView movie_poster;
        final TextView movie_title;
        final TextView movie_overview;
        final TextView movie_rating;
        final ConstraintLayout cl;

        MovieViewHolder(View itemView) {
            super(itemView);
            movie_poster = itemView.findViewById(R.id.movie_poster);
            movie_title = itemView.findViewById(R.id.person_name);
            movie_rating = itemView.findViewById(R.id.movie_rating);
            movie_overview = itemView.findViewById(R.id.movie_overview);
            cl = itemView.findViewById(R.id.movie_search_layout);

        }

        void bind(SearchResults movie, final int position) {
            movie_title.setText(movie.getOriginalTitle());
            Picasso.get()
                    .load(movie.getPosterPath())
                    .placeholder(R.drawable.progress_animation)
                    .error(R.drawable.loader)
                    .into(movie_poster);
            movie_title.setText(movie.getOriginalTitle());
            movie_rating.setText(movie.getVoteAverage());
            movie_overview.setText(movie.getOverview());
            cl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    navigate(position, MOVIE_ITEM_VIEW_TYPE);
                }
            });
        }
    }

    class PersonViewHolder extends RecyclerView.ViewHolder {
        final ImageView movie_poster;
        final TextView person_name, known_for;
        final ConstraintLayout cl;


        PersonViewHolder(View itemView) {
            super(itemView);
            movie_poster = itemView.findViewById(R.id.movie_poster);
            person_name = itemView.findViewById(R.id.person_name);
            known_for = itemView.findViewById(R.id.known_for);
            cl = itemView.findViewById(R.id.person_search_layout);

        }

        void bind(SearchResults movie, final int position) {
            person_name.setText(movie.getName());
            known_for.setText(convertListToString(movie.getKnownFor()));
            Picasso.get()
                    .load(movie.getProfilePath())
                    .placeholder(R.drawable.progress_animation)
                    .error(R.drawable.loader)
                    .into(movie_poster);
            cl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    navigate(position, PERSON_ITEM_VIEW_TYPE);
                }
            });
        }
    }

}

