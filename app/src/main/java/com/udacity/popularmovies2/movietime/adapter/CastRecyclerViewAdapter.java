package com.udacity.popularmovies2.movietime.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.udacity.popularmovies2.movietime.R;
import com.udacity.popularmovies2.movietime.model.details.Cast;

import java.util.List;

public class CastRecyclerViewAdapter extends RecyclerView.Adapter<CastRecyclerViewAdapter.CastViewHolder> {
    private final List<Cast> castList;

    public CastRecyclerViewAdapter(List<Cast> castList) {
        this.castList = castList;
    }


    @NonNull
    @Override
    public CastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cast_card_item, parent, false);

        return new CastViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CastViewHolder holder, int position) {
        Cast c = castList.get(position);
        holder.original_name.setText(c.getName());
        holder.character_name.setText(c.getCharacter());
        Picasso.get().load(c.getProfilePath()).into(holder.cast_image);

    }

    @Override
    public int getItemCount() {
        return castList.size();
    }

    class CastViewHolder extends RecyclerView.ViewHolder {
        final TextView original_name, character_name;
        final ImageView cast_image;

        CastViewHolder(View itemView) {
            super(itemView);
            original_name = itemView.findViewById(R.id.original_name);
            character_name = itemView.findViewById(R.id.character_name);
            cast_image = itemView.findViewById(R.id.cast_image);

        }
    }
}
