package com.quiriletelese.troppadvisorproject.controllers;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.adapters.RecyclerViewProfileBadgeAdapter;
import com.quiriletelese.troppadvisorproject.dao_interfaces.UserDAO;
import com.quiriletelese.troppadvisorproject.factories.DAOFactory;
import com.quiriletelese.troppadvisorproject.model_helpers.Constants;
import com.quiriletelese.troppadvisorproject.models.User;
import com.quiriletelese.troppadvisorproject.utils.ConfigFileReader;
import com.quiriletelese.troppadvisorproject.utils.UserSharedPreferences;
import com.quiriletelese.troppadvisorproject.views.SearchedUserProfileActivity;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBack;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchedUserProfileActivityController {

    private SearchedUserProfileActivity searchedUserProfileActivity;
    private final DAOFactory daoFactory = DAOFactory.getInstance();

    public SearchedUserProfileActivityController(SearchedUserProfileActivity searchedUserProfileActivity) {
        this.searchedUserProfileActivity = searchedUserProfileActivity;
    }

    private void findUserByEmailHelper(VolleyCallBack volleyCallBack) {
        getUserDAO().findByEmail(volleyCallBack, getEmailFromIntent(), getContext());
    }

    public void findUserByEmail() {
        findUserByEmailHelper(new VolleyCallBack() {
            @Override
            public void onSuccess(Object object) {
                volleyCallBackOnSuccess(object);
            }

            @Override
            public void onError(String errorCode) {
                showToastOnUiThred(R.string.unexpected_error_while_fetch_data);
            }
        });
    }

    private void volleyCallBackOnSuccess(Object object) {
        setProfileFields(object);
        initializeRecylerView(object);
    }

    public void setProfileFields(Object object) {
        User user = (User) object;
        setToolbarTitle(user);
        setTextViewUserTitleText(user);
        setCircleImageViewUser(user);
        setTextViewUserNameLastnameText(user);
        setTextViewUsernameText(user);
        setTextViewUserLevelText(user);
        setTextViewUserTotalReviewsText(user);
        setTextViewUserAvarageRatingText(user);
    }

    public void setToolbarTitle(User user) {
        searchedUserProfileActivity.setTitle(getUsername(user));
    }

    private void initializeRecylerView(Object object) {
        User user = (User) object;
        getRecyclerViewBadgeProfile().setLayoutManager(createLinearLayoutManager());
        getRecyclerViewBadgeProfile().setAdapter(createRecyclerViewAdapter(user));
    }

    private RecyclerViewProfileBadgeAdapter createRecyclerViewAdapter(User user) {
        return new RecyclerViewProfileBadgeAdapter(user.getObtainedBadges(), user.getMissingBadges(), getContext());
    }

    @NotNull
    @Contract(" -> new")
    private LinearLayoutManager createLinearLayoutManager() {
        return new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
    }

    private void showToastOnUiThred(int stringId) {
        searchedUserProfileActivity.runOnUiThread(() ->
                Toast.makeText(getContext(), getString(stringId), Toast.LENGTH_SHORT).show());
    }

    @NotNull
    @Contract(" -> new")
    private UserSharedPreferences createUserSharedPreferences() {
        return new UserSharedPreferences(getContext());
    }

    private Context getContext() {
        return searchedUserProfileActivity.getApplicationContext();
    }

    @NotNull
    private Resources getResources() {
        return getContext().getResources();
    }

    @NotNull
    private String getString(int string) {
        return getResources().getString(string);
    }

    private UserDAO getUserDAO() {
        return daoFactory.getUserDAO(getStorageTechnology(Constants.getUserStorageTechnology()));
    }

    private String getStorageTechnology(String storageTechnology) {
        return ConfigFileReader.getProperty(storageTechnology, getContext());
    }

    private TextView getTextViewUserTitle() {
        return searchedUserProfileActivity.getTextViewSearchedUserTitle();
    }

    private void setTextViewUserTitleText(User user) {
        getTextViewUserTitle().setText(user.getChosenTitle());
    }

    public CircleImageView getCircleImageViewUser() {
        return searchedUserProfileActivity.getCircleImageViewSearchedUser();
    }

    public void setCircleImageViewUser(User user) {
        if (userHasImage(user))
            Picasso.with(getContext()).load(getUserImage(user))
                    .fit()
                    .centerCrop()
                    .placeholder(R.drawable.user_profile_no_photo)
                    .error(R.drawable.picasso_error)
                    .into(getCircleImageViewUser());
        else
            getCircleImageViewUser().setImageResource(R.drawable.user_profile_no_photo);
    }

    public TextView getTextViewUserLevel() {
        return searchedUserProfileActivity.getTextViewSearchedUserLevel();
    }

    private void setTextViewUserLevelText(User user) {
        getTextViewUserLevel().setText(String.valueOf(getUserLevel(user)));
    }

    private TextView getTextViewUserNameLastname() {
        return searchedUserProfileActivity.getTextViewSearchedUserNameLastname();
    }

    private void setTextViewUserNameLastnameText(User user) {
        getTextViewUserNameLastname().setText(getUserName(user).concat(" ").concat(getUserLastname(user)));
    }

    public TextView getTextViewUsername() {
        return searchedUserProfileActivity.getTextViewSearchedUsername();
    }

    private void setTextViewUsernameText(User user) {
        getTextViewUsername().setText(getUsername(user));
    }

    private TextView getTextViewUserTotalReviews() {
        return searchedUserProfileActivity.getTextViewSearchedUserTotalReviews();
    }

    private void setTextViewUserTotalReviewsText(User user) {
        getTextViewUserTotalReviews().setText(String.valueOf(getUserTotalReviews(user)));
    }

    private TextView getTextViewUserAvarageRating() {
        return searchedUserProfileActivity.getTextViewSearchedUserAvarageRating();
    }

    private void setTextViewUserAvarageRatingText(User user) {
        getTextViewUserAvarageRating().setText(String.valueOf(getUserAvarageRating(user)));
    }

    public RecyclerView getRecyclerViewBadgeProfile() {
        return searchedUserProfileActivity.getRecyclerViewSearchedUserBadgeProfile();
    }

    private String getEmailFromIntent() {
        return getIntent().getStringExtra(Constants.getEmail());
    }

    private Intent getIntent() {
        return searchedUserProfileActivity.getIntent();
    }

    private boolean userHasImage(User user) {
        return !getUserImage(user).isEmpty();
    }

    private String getUserImage(User user) {
        return user.getImage();
    }

    private Double getUserLevel(User user) {
        return user.getLevel();
    }

    private String getUserName(User user) {
        return user.getName();
    }

    private String getUserLastname(User user) {
        return user.getLastName();
    }

    private String getUsername(User user) {
        return user.getUsername();
    }

    private Long getUserTotalReviews(User user) {
        return user.getTotalReviews();
    }

    private Double getUserAvarageRating(User user) {
        return user.getAvarageRating();
    }

}
