package com.quiriletelese.troppadvisorproject.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.controllers.ReadUserReviewsThumbController;
import com.quiriletelese.troppadvisorproject.model_helpers.Constants;
import com.quiriletelese.troppadvisorproject.models.Review;
import com.quiriletelese.troppadvisorproject.models.User;
import com.quiriletelese.troppadvisorproject.utils.UserSharedPreferences;
import com.quiriletelese.troppadvisorproject.views.UserReviewsActivity;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerViewUserReviewsAdapter extends RecyclerView.Adapter<RecyclerViewUserReviewsAdapter.ViewHolder> {

    private final Context context;
    private final UserReviewsActivity userReviewsActivity;
    private final List<Review> reviews;

    public RecyclerViewUserReviewsAdapter(Context context, UserReviewsActivity userReviewsActivity, List<Review> reviews) {
        this.context = context;
        this.userReviewsActivity = userReviewsActivity;
        this.reviews = reviews;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_user_revies_adapter_layout, parent, false);
        return new RecyclerViewUserReviewsAdapter.ViewHolder(view, userReviewsActivity);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        setFieldsOnBindViewHolder(holder, position);
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public void addListItems(List<Review> reviews) {
        this.reviews.addAll(reviews);
    }

    private void setFieldsOnBindViewHolder(@NotNull RecyclerViewUserReviewsAdapter.ViewHolder viewHolder, int position) {
        viewHolder.textViewAccomodationName.setText(reviews.get(position).getAccomodationName());
        setImage(viewHolder, position);
        viewHolder.ratingBarReviews.setRating(reviews.get(position).getRating().floatValue());
        viewHolder.textViewTitle.setText(reviews.get(position).getTitle());
        viewHolder.textViewDate.setText(reviews.get(position).getAddedDate());
        setUserName(viewHolder, position);
        viewHolder.textViewUserTitle.setText(reviews.get(position).getUser().getChosenTitle());
        viewHolder.textViewReviewBody.setText(reviews.get(position).getDescription());
        handleThumbsButton(viewHolder, position);
        handleThumbsTextViews(viewHolder, position);
    }

    @NotNull
    private String createUserNameString(@NotNull User user) {
        return user.getName().concat(" ").concat(user.getLastName()).concat(" - ").concat(user.getUsername());
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setImage(RecyclerViewUserReviewsAdapter.ViewHolder viewHolder, int position) {
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

    private void setUserName(RecyclerViewUserReviewsAdapter.ViewHolder viewHolder, int position) {
        viewHolder.textViewUser.setText(createUserNameString(reviews.get(position).getUser()));
    }

    private void handleThumbsButton(RecyclerViewUserReviewsAdapter.ViewHolder viewHolder, int position) {
        for (String user : reviews.get(position).getVoters()) {
            String localUserEmail = getLocalUserEmail();
            String[] voter = user.split("/", 0);
            if (voter[0].equals(localUserEmail)) {
                changeThumbsColor(viewHolder, voter[voter.length - 1]);
            }
        }
    }

    private void handleThumbsTextViews(RecyclerViewUserReviewsAdapter.ViewHolder viewHolder, int position) {
        viewHolder.textViewToatalThumbsUp.setText(String.valueOf(reviews.get(position).getTotalThumbUp()));
        viewHolder.textViewToatalThumbsDown.setText(String.valueOf(reviews.get(position).getTotalThumbDown()));
    }

    private void changeThumbsColor(RecyclerViewUserReviewsAdapter.ViewHolder viewHolder, String vote) {
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

    private void disableThumbsIcon(RecyclerViewUserReviewsAdapter.ViewHolder viewHolder) {
        viewHolder.imageViewThumbUp.setClickable(false);
        viewHolder.imageViewThumbDown.setClickable(false);
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final UserReviewsActivity userReviewsActivity;
        private CircleImageView circleImageViewUser;
        private RatingBar ratingBarReviews;
        private TextView textViewAccomodationName, textViewTitle, textViewDate, textViewUser, textViewUserTitle,
                textViewReviewBody, textViewToatalThumbsUp, textViewToatalThumbsDown;
        private ImageView imageViewThumbUp, imageViewThumbDown;
        private ReadUserReviewsThumbController readUserReviewsThumbController;

        public ViewHolder(@NonNull View itemView, UserReviewsActivity userReviewsActivity) {
            super(itemView);
            this.userReviewsActivity = userReviewsActivity;
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
                case R.id.image_view_thumb_up_user_review:
                    doUpdateVoters(1);
                    break;
                case R.id.image_view_thumb_down_user_review:
                    doUpdateVoters(-1);
                    break;
            }
        }

        private void initializeComponents() {
            circleImageViewUser = itemView.findViewById(R.id.circle_image_view_user_review_user);
            ratingBarReviews = itemView.findViewById(R.id.rating_bar_user_review);
            textViewAccomodationName = itemView.findViewById(R.id.text_view_user_review_attraction_name);
            textViewTitle = itemView.findViewById(R.id.text_view_user_review_title);
            textViewDate = itemView.findViewById(R.id.text_view_date_user_review);
            textViewUser = itemView.findViewById(R.id.txt_view_user_review_name);
            textViewUserTitle = itemView.findViewById(R.id.txt_view_user_review_title);
            textViewReviewBody = itemView.findViewById(R.id.text_view_user_review_body);
            textViewToatalThumbsUp = itemView.findViewById(R.id.text_view_total_thumbs_up_user_review);
            textViewToatalThumbsDown = itemView.findViewById(R.id.text_view_total_thumbs_down_user_review);
            imageViewThumbUp = itemView.findViewById(R.id.image_view_thumb_up_user_review);
            imageViewThumbDown = itemView.findViewById(R.id.image_view_thumb_down_user_review);
        }

        private void initializeController() {
            readUserReviewsThumbController = new ReadUserReviewsThumbController(this.userReviewsActivity);
        }

        private void setListenerOnComponents() {
            circleImageViewUser.setOnClickListener(this);
            imageViewThumbUp.setOnClickListener(this);
            imageViewThumbDown.setOnClickListener(this);
        }

        private boolean hasLogged() {
            return readUserReviewsThumbController.hasLogged();
        }

        private void showLoginDialog() {
            readUserReviewsThumbController.showLoginDialog();
        }

        private void doUpdateVoters(int vote) {
            if (!hasLogged())
                showLoginDialog();
            else
                updateVoters(vote);
        }

        private boolean isSameUser() {
            return readUserReviewsThumbController.isSameUser(reviews.get(this.getAdapterPosition()).getUser().getEmail());
        }

        private void updateVoters(int vote) {
            if (!isSameUser()) {
                changeIconColor(vote);
                handleTotalThumbsCount(vote);
                disableThumbsIcon();
                readUserReviewsThumbController.updateVoters(reviews.get(this.getAdapterPosition()).getId(), vote);
            } else
                readUserReviewsThumbController.showToastOnUiThred(R.string.cannot_vote_your_own_review);
        }

        private void changeIconColor(int vote) {
            switch (vote) {
                case 1:
                    imageViewThumbUp.setImageDrawable(userReviewsActivity.getResources().getDrawable(R.drawable.icon_thumb_up_green));
                    break;
                case -1:
                    imageViewThumbDown.setImageDrawable(userReviewsActivity.getResources().getDrawable(R.drawable.icon_thumb_down_green));
                    break;
            }
        }

        private void disableThumbsIcon() {
            imageViewThumbUp.setClickable(false);
            imageViewThumbDown.setClickable(false);
        }

        private void handleTotalThumbsCount(int vote) {
            switch (vote) {
                case 1:
                    textViewToatalThumbsUp.setText(String.valueOf(reviews.get(this.getAdapterPosition()).getTotalThumbUp() + 1));
                    break;
                case -1:
                    textViewToatalThumbsDown.setText(String.valueOf(reviews.get(this.getAdapterPosition()).getTotalThumbDown() + 1));
                    break;
            }
        }
    }
}
