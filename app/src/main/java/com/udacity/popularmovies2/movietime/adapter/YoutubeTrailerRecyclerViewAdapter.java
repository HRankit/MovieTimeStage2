package com.udacity.popularmovies2.movietime.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.udacity.popularmovies2.movietime.R;
import com.udacity.popularmovies2.movietime.model.details.VideosResult;

import java.util.List;

@SuppressWarnings("ALL")
public class YoutubeTrailerRecyclerViewAdapter extends RecyclerView.Adapter<YoutubeTrailerRecyclerViewAdapter.TrailerHolder> {

    private final ItemClickListener mItemClickListener;
    private List<VideosResult> resultList;

    public YoutubeTrailerRecyclerViewAdapter(ItemClickListener listener) {
        mItemClickListener = listener;

    }

    @NonNull
    @Override
    public YoutubeTrailerRecyclerViewAdapter.TrailerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.youtube_image_play, parent, false);

        return new YoutubeTrailerRecyclerViewAdapter.TrailerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull YoutubeTrailerRecyclerViewAdapter.TrailerHolder holder, final int position) {


        Picasso.get()
                .load(makeThumbnailUrl(resultList.get(position).getKey()))
                .placeholder(R.drawable.progress_animation)
                .error(R.drawable.loader)
                .into(holder.youtubeImage);
        Log.d(getClass().getName(), makeThumbnailUrl(resultList.get(position).getKey()));


    }

    private String makeThumbnailUrl(String key) {
        return "http://img.youtube.com/vi/" + key + "/hqdefault.jpg";

    }

    public void setTasks(List<VideosResult> taskEntries) {
        resultList = taskEntries;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return resultList.size();
    }

    public interface ItemClickListener {
        void onItemClickListener(String itemId);
    }

    public class TrailerHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ImageView youtubeImage;


        TrailerHolder(View itemView) {
            super(itemView);
            youtubeImage = itemView.findViewById(R.id.youtubeImage);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            String elementId = resultList.get(getAdapterPosition()).getKey();
            mItemClickListener.onItemClickListener(elementId);
        }
    }
}

