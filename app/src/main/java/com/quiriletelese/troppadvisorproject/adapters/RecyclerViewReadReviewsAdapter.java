package com.quiriletelese.troppadvisorproject.adapters;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.controllers.ReadReviewThumbController;
import com.quiriletelese.troppadvisorproject.models.Review;
import com.quiriletelese.troppadvisorproject.views.SeeReviewsActivity;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author Alessandro Quirile, Mauro Telese
 */

public class RecyclerViewReadReviewsAdapter extends RecyclerView.Adapter<RecyclerViewReadReviewsAdapter.ViewHolder> {

    private final Context context;
    private final SeeReviewsActivity seeReviewsActivity;
    private final List<Review> reviews;

    public RecyclerViewReadReviewsAdapter(Context context, SeeReviewsActivity seeReviewsActivity, List<Review> reviews) {
        this.context = context;
        this.seeReviewsActivity = seeReviewsActivity;
        this.reviews = reviews;
    }

    @NonNull
    @Override
    public RecyclerViewReadReviewsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_see_reviews_item, parent, false);
        return new ViewHolder(view, seeReviewsActivity);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewReadReviewsAdapter.ViewHolder holder, int position) {
        setFieldsOnBindViewHolder(holder, position);
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public void addListItems(List<Review> reviews) {
        this.reviews.addAll(reviews);
    }

    private void setFieldsOnBindViewHolder(@NotNull ViewHolder viewHolder, int position) {
        viewHolder.textViewTitle.setText(reviews.get(position).getTitle());
        viewHolder.textViewRating.setText(createRatingString(reviews.get(position).getRating(),
                isAnonymous(reviews.get(position)) ? context.getResources().getString(R.string.anonymous)
                        : reviews.get(position).getUser()));
        viewHolder.textViewReviewBody.setText(reviews.get(position).getDescription());
    }

    @NotNull
    private String createRatingString(@NotNull Double rating, String user) {
        int maxRatingValue = 5;
        String reviewRating = "";
        reviewRating = reviewRating.concat(rating.intValue() + "/" + maxRatingValue + " - " + user);
        return reviewRating;
    }

    private boolean isAnonymous(@NotNull Review review) {
        return review.getAnonymous();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView textViewTitle, textViewRating, textViewReviewBody;
        private ImageView imageViewThumbUp, imageViewThumbDown;
        private ReadReviewThumbController readReviewThumbController;

        public ViewHolder(@NonNull View itemView, SeeReviewsActivity seeReviewsActivity) {
            super(itemView);
            initializeComponents();
            initializeController();
            setListenerOnComponents();
        }

        @Override
        public void onClick(View view) {
            onClickHelper(view);
        }

        private void onClickHelper(View view) {
            switch (view.getId()) {
                case R.id.image_view_thumb_up:
                    doUpdateVoters(1);
                    break;
                case R.id.image_view_thumb_down:
                    doUpdateVoters(-1);
                    break;
            }
        }

        private void initializeComponents() {
            textViewTitle = itemView.findViewById(R.id.text_view_title);
            textViewRating = itemView.findViewById(R.id.text_view_rating);
            textViewReviewBody = itemView.findViewById(R.id.text_view_review_body);
            imageViewThumbUp = itemView.findViewById(R.id.image_view_thumb_up);
            imageViewThumbDown = itemView.findViewById(R.id.image_view_thumb_down);
        }

        private void initializeController() {
            readReviewThumbController = new ReadReviewThumbController(seeReviewsActivity);
        }

        private void setListenerOnComponents() {
            imageViewThumbUp.setOnClickListener(this);
            imageViewThumbDown.setOnClickListener(this);
        }

        private boolean hasLogged() {
            return readReviewThumbController.hasLogged();
        }

        private void showLoginDialog() {
            readReviewThumbController.showLoginDialog();
        }

        private void doUpdateVoters(int vote) {
            if (!hasLogged())
                showLoginDialog();
            else
                updateVoters(vote);
        }

        private void updateVoters(int vote) {
            readReviewThumbController.updateVoters(reviews.get(this.getAdapterPosition()).getId(), vote);
        }

    }

}