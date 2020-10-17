package com.quiriletelese.troppadvisorproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.models.Review;

import java.util.List;

/**
 * @author Alessandro Quirile, Mauro Telese
 */

public class RecyclerViewSeeReviewsAdapter extends RecyclerView.Adapter<RecyclerViewSeeReviewsAdapter.ViewHolder> {

    private final Context context;
    private final List<Review> reviews;

    public RecyclerViewSeeReviewsAdapter(Context context, List<Review> reviews) {
        this.context = context;
        this.reviews = reviews;
    }

    @NonNull
    @Override
    public RecyclerViewSeeReviewsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_see_reviews_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewSeeReviewsAdapter.ViewHolder holder, int position) {
        setFieldsOnBindViewHolder(holder, position);
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public void addListItems(List<Review> reviews) {
        this.reviews.addAll(reviews);
    }

    private void setFieldsOnBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.textViewTitle.setText(reviews.get(position).getTitle());
        viewHolder.textViewRating.setText(createRatingString(reviews.get(position).getRating(),
                isAnonymous(reviews.get(position)) ? context.getResources().getString(R.string.anonymous)
                        : reviews.get(position).getUser()));
        viewHolder.textViewReviewBody.setText(reviews.get(position).getDescription());
    }

    private String createRatingString(Double rating, String user) {
        String reviewRating = "";
        reviewRating = reviewRating.concat(rating.intValue() + " - " + user);
        return reviewRating;
    }

    private boolean isAnonymous(Review review) {
        return review.getAnonymous();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewTitle, textViewRating, textViewReviewBody;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            initializeComponents();
        }

        private void initializeComponents() {
            textViewTitle = itemView.findViewById(R.id.text_view_title);
            textViewRating = itemView.findViewById(R.id.text_view_rating);
            textViewReviewBody = itemView.findViewById(R.id.text_view_review_body);

        }

    }

}