package com.quiriletelese.troppadvisorproject.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.controllers.ProfileFragmentController;
import com.quiriletelese.troppadvisorproject.controllers.SearchedUserProfileActivityController;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchedUserProfileActivity extends AppCompatActivity {

    private SearchedUserProfileActivityController searchedUserProfileActivityController;
    private CircleImageView circleImageViewSearchedUser;
    private TextView textViewSearchedUserTitle, textViewSearchedUserLevel, textViewSearchedUserNameLastname,
            textViewSearchedUsername, textViewSearchedUserTotalReviews, textViewSearchedUserAvarageRating;
    private RecyclerView recyclerViewSearchedUserBadgeProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searched_user_profile);

        initializeViewComponents();
        initializeController();
        findUserByEmail();
    }

    public void initializeViewComponents() {
        circleImageViewSearchedUser = findViewById(R.id.circle_image_view_searched_user);
        textViewSearchedUserTitle = findViewById(R.id.text_view_searched_user_title);
        textViewSearchedUserLevel = findViewById(R.id.text_view_searched_user_level);
        textViewSearchedUserNameLastname = findViewById(R.id.text_view_searched_user_name_lastname);
        textViewSearchedUsername = findViewById(R.id.text_view_searched_user_username);
        textViewSearchedUserTotalReviews = findViewById(R.id.text_view_searched_user_total_reviews);
        textViewSearchedUserAvarageRating = findViewById(R.id.text_view_searched_user_avarage_rating);
        recyclerViewSearchedUserBadgeProfile = findViewById(R.id.recycler_view_searched_user_badge_profile);
    }

    public void initializeController() {
        searchedUserProfileActivityController = new SearchedUserProfileActivityController(this);
    }

    private void findUserByEmail() {
        searchedUserProfileActivityController.findUserByEmail();
    }

    public CircleImageView getCircleImageViewSearchedUser() {
        return circleImageViewSearchedUser;
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

    public TextView getTextViewSearchedUsername() {
        return textViewSearchedUsername;
    }

    public TextView getTextViewSearchedUserTotalReviews() {
        return textViewSearchedUserTotalReviews;
    }

    public TextView getTextViewSearchedUserAvarageRating() {
        return textViewSearchedUserAvarageRating;
    }

    public RecyclerView getRecyclerViewSearchedUserBadgeProfile() {
        return recyclerViewSearchedUserBadgeProfile;
    }
}