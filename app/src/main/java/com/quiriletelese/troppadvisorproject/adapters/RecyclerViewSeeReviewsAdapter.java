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

public class RecyclerViewSeeReviewsAdapter extends RecyclerView.Adapter<RecyclerViewSeeReviewsAdapter.ViewHolder> {

    private Context context;
    private List<Review> reviews;

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

    public void addListItems(List<Review> reviews){
        this.reviews.addAll(reviews);
    }

    private void setFieldsOnBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.textViewTitle.setText(reviews.get(position).getTitle());
        if (!reviews.get(position).getAnonymous())
            viewHolder.textViewRating.setText(createRatingString(reviews.get(position).getRating(), reviews.get(position).getUser()));
        else
            viewHolder.textViewRating.setText(createRatingString(reviews.get(position).getRating(), context.getResources().getString(R.string.anonymous)));
        viewHolder.textViewReviewBody.setText(reviews.get(position).getDescription());
    }

    private String createRatingString(Float rating, String user) {
        String reviewRating = "";
        reviewRating = reviewRating.concat(rating + " - " + user);
        return reviewRating;

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private Context context;
        private TextView textViewTitle, textViewRating, textViewReviewBody;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            initializeComponents();
        }

        private void initializeComponents() {
            context = itemView.getContext();
            textViewTitle = itemView.findViewById(R.id.text_view_title);
            textViewRating = itemView.findViewById(R.id.text_view_rating);
            textViewReviewBody = itemView.findViewById(R.id.text_view_review_body);

        }

    }

}