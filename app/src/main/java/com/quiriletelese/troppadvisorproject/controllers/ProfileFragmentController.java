package com.quiriletelese.troppadvisorproject.controllers;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;
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
import com.quiriletelese.troppadvisorproject.views.EditProfileActivity;
import com.quiriletelese.troppadvisorproject.views.LoginActivity;
import com.quiriletelese.troppadvisorproject.views.ProfileFragment;
import com.quiriletelese.troppadvisorproject.views.SearchUsersActivity;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBack;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @author Alessandro Quirile, Mauro Telese
 */

public class ProfileFragmentController {

    private final ProfileFragment profileFragment;
    private final DAOFactory daoFactory = DAOFactory.getInstance();
    private final UserSharedPreferences userSharedPreferences;

    public ProfileFragmentController(ProfileFragment profileFragment) {
        this.profileFragment = profileFragment;
        this.userSharedPreferences = new UserSharedPreferences(getContext());
    }

    private void findUserByEmailHelper(VolleyCallBack volleyCallBack) {
        getUserDAO().findByEmail(volleyCallBack, getStringSharedPreferences(Constants.getEmail()), getContext());
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
        setTextViewUserTitleText(user);
        setCircleImageViewUser(user);
        setTextViewUserNameLastnameText(user);
        setTextViewUsernameText(user);
        setTextViewUserLevelText(user);
        setTextViewUserTotalReviewsText(user);
        setTextViewUserAvarageRatingText(user);
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

    public void checkLogin() {
        if (!hasLogged())
            showViewNoLoginProfileError();
        else
            hideViewNoLoginProfileError();
    }

    public void startLoginActivity() {
        getContext().startActivity(createStartLoginActivityIntent());
    }

    public void startLoginActivityFromLogOut() {
        clearUserSharedPreferences();
        getContext().startActivity(createStartLoginActivityIntent());
        showToastOnUiThred(R.string.add_make_login);
    }

    public void startSearchUserActivity() {
        getContext().startActivity(createSearchUserActivityIntent());
    }

    public void startEditProfileActivity() {
        getContext().startActivity(createEditProfileActivityIntent());
    }

    @NotNull
    @Contract(" -> new")
    private Intent createStartLoginActivityIntent() {
        Intent intentLoginActivity = new Intent(getContext(), LoginActivity.class);
        intentLoginActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intentLoginActivity;
    }

    @NotNull
    @Contract(" -> new")
    private Intent createSearchUserActivityIntent() {
        Intent intentSearchUserActivity = new Intent(getContext(), SearchUsersActivity.class);
        intentSearchUserActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intentSearchUserActivity;
    }

    @NotNull
    @Contract(" -> new")
    private Intent createEditProfileActivityIntent() {
        Intent intentEditProfileActivity = new Intent(getContext(), EditProfileActivity.class);
        intentEditProfileActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intentEditProfileActivity;
    }

    private void showViewNoLoginProfileError() {
        setViewNoLoginProfileErrorVisibility(View.VISIBLE);
    }

    private void hideViewNoLoginProfileError() {
        setViewNoLoginProfileErrorVisibility(View.GONE);
    }

    private void showToastOnUiThred(int stringId) {
        getActivity().runOnUiThread(() ->
                Toast.makeText(getContext(), getString(stringId), Toast.LENGTH_SHORT).show());
    }

    private void clearUserSharedPreferences() {
        userSharedPreferences.putStringSharedPreferences(Constants.getAccessToken(), "");
        userSharedPreferences.putStringSharedPreferences(Constants.getIdToken(), "");
        userSharedPreferences.putStringSharedPreferences(Constants.getRefreshToken(), "");
        userSharedPreferences.putStringSharedPreferences(Constants.getUsername(), "");
        userSharedPreferences.putStringSharedPreferences(Constants.getUserFirstName(), "");
        userSharedPreferences.putStringSharedPreferences(Constants.getFamilyName(), "");
        userSharedPreferences.putStringSharedPreferences(Constants.getEmail(), "");
    }

    public boolean hasLogged() {
        return !getAccessToken().equals("");
    }

    private void setViewNoLoginProfileErrorVisibility(int visibility) {
        getViewNoLoginProfileError().setVisibility(visibility);
    }

    private String getAccessToken() {
        return createUserSharedPreferences().getStringSharedPreferences(Constants.getAccessToken());
    }

    @NotNull
    @Contract(" -> new")
    private UserSharedPreferences createUserSharedPreferences() {
        return new UserSharedPreferences(getContext());
    }

    private Context getContext() {
        return profileFragment.getContext();
    }

    private FragmentActivity getActivity() {
        return profileFragment.getActivity();
    }

    @NotNull
    private Resources getResources() {
        return profileFragment.getResources();
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

    private String getStringSharedPreferences(String preference) {
        return userSharedPreferences.getStringSharedPreferences(preference);
    }

    private TextView getTextViewUserTitle() {
        return profileFragment.getTextViewUserTitle();
    }

    private void setTextViewUserTitleText(User user) {
        getTextViewUserTitle().setText(user.getChosenTitle());
    }

    public CircleImageView getCircleImageViewUser() {
        return profileFragment.getCircleImageViewUser();
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
        return profileFragment.getTextViewUserLevel();
    }

    private void setTextViewUserLevelText(User user) {
        getTextViewUserLevel().setText(String.valueOf(getUserLevel(user)));
    }

    private TextView getTextViewUserNameLastname() {
        return profileFragment.getTextViewUserNameLastname();
    }

    private void setTextViewUserNameLastnameText(User user) {
        getTextViewUserNameLastname().setText(getUserName(user).concat(" ").concat(getUserLastname(user)));
    }

    public TextView getTextViewUsername() {
        return profileFragment.getTextViewUsername();
    }

    private void setTextViewUsernameText(User user) {
        getTextViewUsername().setText(getUsername(user));
    }

    private TextView getTextViewUserTotalReviews() {
        return profileFragment.getTextViewUserTotalReviews();
    }

    private void setTextViewUserTotalReviewsText(User user) {
        getTextViewUserTotalReviews().setText(String.valueOf(getUserTotalReviews(user)));
    }

    private TextView getTextViewUserAvarageRating() {
        return profileFragment.getTextViewUserAvarageRating();
    }

    private void setTextViewUserAvarageRatingText(User user) {
        getTextViewUserAvarageRating().setText(String.valueOf(getUserAvarageRating(user)));
    }

    public RecyclerView getRecyclerViewBadgeProfile() {
        return profileFragment.getRecyclerViewBadgeProfile();
    }

    private View getViewNoLoginProfileError() {
        return profileFragment.getViewNoLoginProfileError();
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