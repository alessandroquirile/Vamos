package com.quiriletelese.troppadvisorproject.views;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.controllers.SearchedUserProfileActivityController;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchedUserProfileActivity extends AppCompatActivity {

    private SearchedUserProfileActivityController searchedUserProfileActivityController;
    private CircleImageView circleImageViewSearchedUser;
    private ImageView imageViewCloseEnlarge, imageViewEnlarge;
    private TextView textViewSearchedUserTitle, textViewSearchedUserLevel, textViewSearchedUserNameLastname,
            textViewSearchedUserTotalReviews, textViewSearchedUserAvarageRating;
    private LinearLayout linearLayoutUserReviews;
    private LinearLayoutCompat linearLayoutEnlargedImage;
    private RecyclerView recyclerViewSearchedUserBadgeProfile;
    private TextView textViewSearchedUserReviewsLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searched_user_profile);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        initializeViewComponents();
        initializeController();
        setListenerOnViewComponents();
        findUserByEmail();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    public void initializeViewComponents() {
        circleImageViewSearchedUser = findViewById(R.id.circle_image_view_searched_user);
        imageViewCloseEnlarge = findViewById(R.id.image_view_close_enlarge_image);
        imageViewEnlarge = findViewById(R.id.profile_image_enlarge);
        textViewSearchedUserTitle = findViewById(R.id.text_view_searched_user_title);
        textViewSearchedUserLevel = findViewById(R.id.text_view_searched_user_level);
        textViewSearchedUserNameLastname = findViewById(R.id.text_view_searched_user_name_lastname);
        textViewSearchedUserTotalReviews = findViewById(R.id.text_view_searched_user_total_reviews);
        textViewSearchedUserAvarageRating = findViewById(R.id.text_view_searched_user_avarage_rating);
        linearLayoutUserReviews = findViewById(R.id.linear_layout_searched_user_reviews);
        linearLayoutEnlargedImage =  findViewById(R.id.enlarged_image_layout_searched_user);
        recyclerViewSearchedUserBadgeProfile = findViewById(R.id.recycler_view_searched_user_badge_profile);
        textViewSearchedUserReviewsLabel = findViewById(R.id.text_view_searched_user_reviews_label);
    }

    public void initializeController() {
        searchedUserProfileActivityController = new SearchedUserProfileActivityController(this);
    }

    public void setListenerOnViewComponents() {
        searchedUserProfileActivityController.setListenerOnViewComponents();
    }

    private void findUserByEmail() {
        searchedUserProfileActivityController.findUserByEmail();
    }

    public CircleImageView getCircleImageViewSearchedUser() {
        return circleImageViewSearchedUser;
    }

    public ImageView getImageViewCloseEnlarge() {
        return imageViewCloseEnlarge;
    }

    public ImageView getImageViewEnlarge() {
        return imageViewEnlarge;
    }

    public TextView getTextViewSearchedUserTitle() {
        return textViewSearchedUserTitle;
    }

    public TextView getTextViewSearchedUserLevel() {
        return textViewSearchedUserLevel;
    }

    public TextView getTextViewSearchedUserNameLastname() {
        return textViewSearchedUserNameLastname;
    }

    public TextView getTextViewSearchedUserTotalReviews() {
        return textViewSearchedUserTotalReviews;
    }

    public TextView getTextViewSearchedUserAvarageRating() {
        return textViewSearchedUserAvarageRating;
    }

    public LinearLayout getLinearLayoutUserReviews() {
        return linearLayoutUserReviews;
    }

    public LinearLayoutCompat getLinearLayoutEnlargedImage() {
        return linearLayoutEnlargedImage;
    }

    public RecyclerView getRecyclerViewSearchedUserBadgeProfile() {
        return recyclerViewSearchedUserBadgeProfile;
    }

    public TextView getTextViewSearchedUserReviewsLabel() {
        return textViewSearchedUserReviewsLabel;
    }
}