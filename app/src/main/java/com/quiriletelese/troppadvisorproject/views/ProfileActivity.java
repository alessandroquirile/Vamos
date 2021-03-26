package com.quiriletelese.troppadvisorproject.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.controllers.ProfileFragmentController;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private ProfileFragmentController profileFragmentController;
    private Toolbar toolbar;
    private CircleImageView circleImageViewUser;
    private ImageView imageViewCloseEnlarge, imageViewEnlarge;
    private TextView textViewUserTitle, textViewUserLevel, textViewUserWallet, textViewUserNameLastname,
            textViewUsername, textViewUserTotalReviews, textViewUserAvarageRating;
    private LinearLayout linearLayoutUserReviews;
    private LinearLayoutCompat linearLayoutEnlargedImage;
    private RecyclerView recyclerViewBadgeProfile;
    private View viewNoLoginProfileError;
    private TextView textViewUserReviewsLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        toolbar = findViewById(R.id.tool_bar_profile);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icon_arrow_back_white);
        getSupportActionBar().setTitle(null);

        initializeViewComponents();
        initializeController();
        setListenerOnViewComponents();
        findUserByEmail();
        checkLogin();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        inflateMenu(menu, getMenuInflater());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onOptionsItemSelectedHelper(item);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        profileFragmentController.handleOnActivityResult(requestCode, resultCode, data);
    }

    private void inflateMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        if (!isLogged())
            inflater.inflate(R.menu.menu_profile_login, menu);
        else
            inflater.inflate(R.menu.menu_profile_logout, menu);
    }

    private void onOptionsItemSelectedHelper(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.login_profile:
                startLoginActivity();
                break;
            case R.id.logout_profile:
                startLoginActivityFromLogOut();
                break;
            case R.id.search_user_profile:
                startSerchUserActivity();
                break;
            case R.id.edit_profile:
                startEditProfileActivity();
                break;
            case R.id.leaderboard:
                startLeaderboardActivity();
                break;
        }
    }

    public void initializeViewComponents() {
        textViewUserTitle = findViewById(R.id.text_view_user_title);
        circleImageViewUser = findViewById(R.id.circle_image_view_user);
        imageViewCloseEnlarge = findViewById(R.id.image_view_close_enlarge_image);
        imageViewEnlarge = findViewById(R.id.profile_image_enlarge);
        textViewUserLevel = findViewById(R.id.text_view_user_level);
        textViewUserWallet = findViewById(R.id.text_view_user_wallet);
        textViewUserNameLastname = findViewById(R.id.text_view_user_name_lastname);
        textViewUsername = findViewById(R.id.text_view_username);
        textViewUserTotalReviews = findViewById(R.id.text_view_user_total_reviews);
        textViewUserAvarageRating = findViewById(R.id.text_view_user_avarage_rating);
        linearLayoutUserReviews = findViewById(R.id.linear_layout_user_reviews);
        linearLayoutEnlargedImage =  findViewById(R.id.enlarged_image_layout);
        recyclerViewBadgeProfile = findViewById(R.id.recycler_view_badge_profile);
        viewNoLoginProfileError = findViewById(R.id.no_login_profile_error_layout);
        textViewUserReviewsLabel = findViewById(R.id.text_view_user_reviews_label);
    }

    public void initializeController() {
        profileFragmentController = new ProfileFragmentController(this);
    }

    public void setListenerOnViewComponents() {
        profileFragmentController.setListenerOnViewComponents();
    }

    private void findUserByEmail() {
        profileFragmentController.findUserByEmail();
    }

    private void checkLogin() {
        profileFragmentController.checkLogin();
    }

    private boolean isLogged() {
        return profileFragmentController.hasLogged();
    }

    private void startLoginActivity() {
        profileFragmentController.startLoginActivity();
    }

    private void startLoginActivityFromLogOut() {
        profileFragmentController.startLoginActivityFromLogOut();
    }

    private void startSerchUserActivity() {
        profileFragmentController.startSearchUserActivity();
    }

    private void startEditProfileActivity() {
        profileFragmentController.startEditProfileActivity();
    }

    private void startLeaderboardActivity() {
        profileFragmentController.startLeaderboardActivity();
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public TextView getTextViewUserTitle() {
        return textViewUserTitle;
    }

    public CircleImageView getCircleImageViewUser() {
        return circleImageViewUser;
    }

    public ImageView getImageViewCloseEnlarge() {
        return imageViewCloseEnlarge;
    }

    public ImageView getImageViewEnlarge() {
        return imageViewEnlarge;
    }

    public TextView getTextViewUserLevel() {
        return textViewUserLevel;
    }

    public TextView getTextViewUserWallet() {
        return textViewUserWallet;
    }

    public TextView getTextViewUserNameLastname() {
        return textViewUserNameLastname;
    }

    public TextView getTextViewUsername() {
        return textViewUsername;
    }

    public TextView getTextViewUserTotalReviews() {
        return textViewUserTotalReviews;
    }

    public TextView getTextViewUserAvarageRating() {
        return textViewUserAvarageRating;
    }

    public LinearLayout getLinearLayoutUserReviews() {
        return linearLayoutUserReviews;
    }

    public LinearLayoutCompat getLinearLayoutEnlargedImage() {
        return linearLayoutEnlargedImage;
    }

    public RecyclerView getRecyclerViewBadgeProfile() {
        return recyclerViewBadgeProfile;
    }

    public View getViewNoLoginProfileError() {
        return viewNoLoginProfileError;
    }

    public TextView getTextViewUserReviewsLabel() {
        return textViewUserReviewsLabel;
    }
}