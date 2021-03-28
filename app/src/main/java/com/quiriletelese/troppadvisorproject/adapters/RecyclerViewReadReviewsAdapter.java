package com.quiriletelese.troppadvisorproject.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

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
            if (voter[0].equals(localUserEmail))
                changeThumbsColor(viewHolder, voter[voter.length - 1]);
        }
    }

    private void handleThumbsTextViews(ViewHolder viewHolder, int position) {
        viewHolder.textViewToatalThumbsUp.setText(String.valueOf(reviews.get(position).getTotalThumbUp()));
        viewHolder.textViewToatalThumbsDown.setText(String.valueOf(reviews.get(position).getTotalThumbDown()));
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
        private RatingBar ratingBarReviews;
        private TextView textViewTitle, textViewDate, textViewUser, textViewUserTitle,
                textViewReviewBody, textViewToatalThumbsUp, textViewToatalThumbsDown;
        private ImageView imageViewThumbUp, imageViewThumbDown, imageViewReport;
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
                case R.id.image_view_report_review:
                    showDialogReportReview();
                    break;
            }
        }

        private void initializeComponents() {
            circleImageViewUser = itemView.findViewById(R.id.circle_image_view_review_user);
            ratingBarReviews = itemView.findViewById(R.id.rating_bar_reviews);
            textViewTitle = itemView.findViewById(R.id.text_view_title);
            textViewDate = itemView.findViewById(R.id.text_view_date);
            textViewUser = itemView.findViewById(R.id.txt_view_review_user);
            textViewUserTitle = itemView.findViewById(R.id.txt_view_review_user_title);
            textViewReviewBody = itemView.findViewById(R.id.text_view_review_body);
            textViewToatalThumbsUp = itemView.findViewById(R.id.text_view_total_thumbs_up);
            textViewToatalThumbsDown = itemView.findViewById(R.id.text_view_total_thumbs_down);
            imageViewThumbUp = itemView.findViewById(R.id.image_view_thumb_up);
            imageViewThumbDown = itemView.findViewById(R.id.image_view_thumb_down);
            imageViewReport = itemView.findViewById(R.id.image_view_report_review);
        }

        private void initializeController() {
            readReviewThumbController = new ReadReviewThumbController(this.seeReviewsActivity);
        }

        private void setListenerOnComponents() {
            circleImageViewUser.setOnClickListener(this);
            imageViewThumbUp.setOnClickListener(this);
            imageViewThumbDown.setOnClickListener(this);
            imageViewReport.setOnClickListener(this);
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

        public void showDialogReportReview() {
            new AlertDialog.Builder(seeReviewsActivity)
                    .setTitle(getString(R.string.report_review))
                    .setMessage(getString(R.string.report_review_body))
                    .setPositiveButton("Invia segnalazione", ((dialogInterface, i) -> {
                        Toast.makeText(seeReviewsActivity, "Segnalazione inviata con successo", Toast.LENGTH_SHORT).show();
                    }))
                    .setNegativeButton("Annulla", null)
                    .setCancelable(false)
                    .create()
                    .show();
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
            if (!isSameUser()) {
                changeIconColor(vote);
                handleTotalThumbsCount(vote);
                disableThumbsIcon();
                readReviewThumbController.updateVoters(reviews.get(this.getAdapterPosition()).getId(), vote);
            } else
                readReviewThumbController.showToastOnUiThred(R.string.cannot_vote_your_own_review);
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

        @NotNull
        private Resources getResources() {
            return seeReviewsActivity.getResources();
        }

        @NotNull
        private String getString(int string) {
            return getResources().getString(string);
        }
    }
}