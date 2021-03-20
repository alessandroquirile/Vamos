package com.quiriletelese.troppadvisorproject.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.controllers.ReadReviewThumbController;
import com.quiriletelese.troppadvisorproject.model_helpers.Constants;
import com.quiriletelese.troppadvisorproject.models.Review;
import com.quiriletelese.troppadvisorproject.models.User;
import com.quiriletelese.troppadvisorproject.utils.UserSharedPreferences;
import com.quiriletelese.troppadvisorproject.views.SearchedUserProfileActivity;
import com.quiriletelese.troppadvisorproject.views.SeeReviewsActivity;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

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
        setImage(viewHolder, position);
        viewHolder.textViewTitle.setText(reviews.get(position).getTitle());
        viewHolder.textViewRating.setText(createRatingString(reviews.get(position).getRating(), position));
        setUserName(viewHolder, position);
        viewHolder.textViewReviewBody.setText(reviews.get(position).getDescription());
        handleThumbsButton(viewHolder, position);
    }

    @NotNull
    private String createRatingString(@NotNull Double rating, int position) {
        int maxRatingValue = 5;
        String reviewRating = "";
        reviewRating = reviewRating.concat(rating.intValue() + "/" + maxRatingValue + " " + reviews.get(position).getAddedDate() /*createDateString(position)*/);
        return reviewRating;
    }

    @NotNull
    private String createUserNameString(@NotNull User user) {
        return user.getName().concat("/").concat(user.getUsername());
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setImage(RecyclerViewReadReviewsAdapter.ViewHolder viewHolder, int position) {
        if (hasImage(position)) {
            Picasso.with(context).load(getUserImage(position))
                    .fit()
                    .centerCrop()
                    .placeholder(R.drawable.user_profile_no_photo)
                    .error(R.drawable.picasso_error)
                    .into(viewHolder.circleImageViewUser);
        } else
            viewHolder.circleImageViewUser.setImageDrawable(context.getResources().getDrawable(R.drawable.user_profile_no_photo));
    }

    private boolean hasImage(int position) {
        return reviews.get(position).getUser().hasImage();
    }

    private String getUserImage(int position) {
        return reviews.get(position).getUserImage();
    }

    private void setUserName(ViewHolder viewHolder, int position) {
        viewHolder.textViewUser.setText(createUserNameString(reviews.get(position).getUser()));
    }

    private void handleThumbsButton(ViewHolder viewHolder, int position) {
        for (String user : reviews.get(position).getVoters()) {
            String localUserEmail = getLocalUserEmail();
            String[] voter = user.split("/", 0);
                if (voter[0].equals(localUserEmail)) {
                    changeThumbsColor(viewHolder, voter[voter.length-1]);
                }
        }
    }

    private void changeThumbsColor(ViewHolder viewHolder, String vote) {
        switch (vote) {
            case "Y":
                viewHolder.imageViewThumbUp.setImageDrawable(context.getDrawable(R.drawable.icon_thumb_up_green));
                break;
            case "N":
                viewHolder.imageViewThumbDown.setImageDrawable(context.getDrawable(R.drawable.icon_thumb_down_green));
                break;
        }
        disableThumbsIcon(viewHolder);
    }

    private String getLocalUserEmail() {
        return new UserSharedPreferences(context).getStringSharedPreferences(Constants.getEmail());
    }

    private void disableThumbsIcon(ViewHolder viewHolder) {
        viewHolder.imageViewThumbUp.setClickable(false);
        viewHolder.imageViewThumbDown.setClickable(false);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final SeeReviewsActivity seeReviewsActivity;
        private CircleImageView circleImageViewUser;
        private TextView textViewTitle, textViewRating, textViewUser, textViewReviewBody;
        private ImageView imageViewThumbUp, imageViewThumbDown;
        private ReadReviewThumbController readReviewThumbController;

        public ViewHolder(@NonNull View itemView, SeeReviewsActivity seeReviewsActivity) {
            super(itemView);
            this.seeReviewsActivity = seeReviewsActivity;
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
                case R.id.circle_image_view_review_user:
                    startUserActivity();
                    break;
                case R.id.image_view_thumb_up:
                    doUpdateVoters(1);
                    break;
                case R.id.image_view_thumb_down:
                    doUpdateVoters(-1);
                    break;
            }
        }

        private void initializeComponents() {
            circleImageViewUser = itemView.findViewById(R.id.circle_image_view_review_user);
            textViewTitle = itemView.findViewById(R.id.text_view_title);
            textViewRating = itemView.findViewById(R.id.text_view_rating);
            textViewUser = itemView.findViewById(R.id.txt_view_review_user);
            textViewReviewBody = itemView.findViewById(R.id.text_view_review_body);
            imageViewThumbUp = itemView.findViewById(R.id.image_view_thumb_up);
            imageViewThumbDown = itemView.findViewById(R.id.image_view_thumb_down);
        }

        private void initializeController() {
            readReviewThumbController = new ReadReviewThumbController(this.seeReviewsActivity);
        }

        private void setListenerOnComponents() {
            circleImageViewUser.setOnClickListener(this);
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

        private void startUserActivity() {
            context.startActivity(createUserActivityIntent());
        }

        private Intent createUserActivityIntent() {
            Intent intentUserActivity = new Intent(context, SearchedUserProfileActivity.class);
            intentUserActivity.putExtra(Constants.getEmail(), reviews.get(this.getAdapterPosition()).getUser().getEmail());
            intentUserActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            return intentUserActivity;
        }

        private boolean isSameUser() {
            return readReviewThumbController.isSameUser(reviews.get(this.getAdapterPosition()).getUser().getEmail());
        }

        private void updateVoters(int vote) {
            changeIconColor(vote);
            readReviewThumbController.updateVoters(reviews.get(this.getAdapterPosition()).getId(), vote);
            disableThumbsIcon();
//            if (!isSameUser()) {
//                changeIconColor(vote);
//                readReviewThumbController.updateVoters(reviews.get(this.getAdapterPosition()).getId(), vote);
//                disableThumbsIcon();
//            } else
//                readReviewThumbController.showToastOnUiThred(R.string.cannot_vote_your_own_review);
        }

        private void changeIconColor(int vote) {
            switch (vote) {
                case 1:
                    imageViewThumbUp.setImageDrawable(seeReviewsActivity.getResources().getDrawable(R.drawable.icon_thumb_up_green));
                    break;
                case -1:
                    imageViewThumbDown.setImageDrawable(seeReviewsActivity.getResources().getDrawable(R.drawable.icon_thumb_down_green));
                    break;
            }
        }

        private void disableThumbsIcon() {
            imageViewThumbUp.setClickable(false);
            imageViewThumbDown.setClickable(false);
        }

    }

}