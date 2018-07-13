package com.udacity.popularmovies2.movietime.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.udacity.popularmovies2.movietime.R;
import com.udacity.popularmovies2.movietime.model.details.Review;

import java.util.List;

public class ReviewsPagerAdapter extends PagerAdapter {

    private final List<Review> reviewList;
    private final Context mContext;

    public ReviewsPagerAdapter(Context context, List<Review> reviewList) {
        this.mContext = context;
        this.reviewList = reviewList;
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {


        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        View itemView = inflater.inflate(R.layout.review_card_item, container,
                false);

        Review r = reviewList.get(position);

        TextView author_name = itemView.findViewById(R.id.author_name);
        author_name.setText(r.getAuthor());

        TextView review = itemView.findViewById(R.id.review);
        review.setText(r.getContent());

        TextView circleName = itemView.findViewById(R.id.circle_name);
        Log.d(getClass().getName(), r.getAuthor().trim().substring(0, 1));
        circleName.setText(r.getAuthor().trim().substring(0, 1));


        container.addView(itemView);

        return itemView;
    }


    @Override
    public int getCount() {
        return reviewList.size();
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        (container).removeView((View) object);

    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (object);
    }
}
