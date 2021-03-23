package com.quiriletelese.troppadvisorproject.controllers;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.adapters.RecyclerViewProfileBadgeAdapter;
import com.quiriletelese.troppadvisorproject.dao_interfaces.UserDAO;
import com.quiriletelese.troppadvisorproject.factories.DAOFactory;
import com.quiriletelese.troppadvisorproject.model_helpers.Constants;
import com.quiriletelese.troppadvisorproject.models.User;
import com.quiriletelese.troppadvisorproject.utils.ConfigFileReader;
import com.quiriletelese.troppadvisorproject.utils.UserSharedPreferences;
import com.quiriletelese.troppadvisorproject.views.EditProfileActivity;
import com.quiriletelese.troppadvisorproject.views.LeaderboardActivity;
import com.quiriletelese.troppadvisorproject.views.LoginActivity;
import com.quiriletelese.troppadvisorproject.views.ProfileActivity;
import com.quiriletelese.troppadvisorproject.views.SearchUsersActivity;
import com.quiriletelese.troppadvisorproject.views.UserReviewsActivity;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBack;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

/**
 * @author Alessandro Quirile, Mauro Telese
 */

public class ProfileFragmentController implements View.OnClickListener {

    private final ProfileActivity profileActivity;
    private final DAOFactory daoFactory = DAOFactory.getInstance();
    private final UserSharedPreferences userSharedPreferences;
    private User user;

    public ProfileFragmentController(ProfileActivity profileActivity) {
        this.profileActivity = profileActivity;
        this.userSharedPreferences = new UserSharedPreferences(getContext());
    }

    @Override
    public void onClick(View view) {
        onClickHelper(view);
    }

    private void findUserByEmailHelper(VolleyCallBack volleyCallBack) {
        getUserDAO().findByEmail(volleyCallBack, getStringSharedPreferences(Constants.getEmail()), getContext());
    }

    public void findUserByEmail() {
        findUserByEmailHelper(new VolleyCallBack() {
            @Override
            public void onSuccess(Object object) {
                user = (User) object;
                volleyCallBackOnSuccess(object);
            }

            @Override
            public void onError(String errorCode) {
                showToastOnUiThred(R.string.unexpected_error_while_fetch_data);
            }
        });
    }

    private void onClickHelper(View view) {
        switch (view.getId()) {
            case R.id.circle_image_view_user:
                enlargeImageView();
                break;
            case R.id.image_view_close_enlarge_image:
                closeEnlargedImageView();
                break;
            case R.id.linear_layout_user_reviews:
                startUserReviewsActivity();
                break;
        }
    }

    public void setListenerOnViewComponents() {
        getCircleImageViewUser().setOnClickListener(this);
        getLinearLayoutUserReviews().setOnClickListener(this);
        getImageViewCloseEnlarge().setOnClickListener(this);
    }

    private void enlargeImageView() {
        if (userHasImage(user)) {
            Animation aniFade = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
            setEnlargedImageViewUser(user);
            getLinearLayoutEnlargedImage().setVisibility(View.VISIBLE);
            getLinearLayoutEnlargedImage().startAnimation(aniFade);
        } else
            showToastOnUiThred(R.string.no_photo_to_view);

    }

    private void closeEnlargedImageView() {
        Animation aniFade = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);
        getLinearLayoutEnlargedImage().setVisibility(View.INVISIBLE);
        getLinearLayoutEnlargedImage().startAnimation(aniFade);
    }

    private void startUserReviewsActivity() {
        if (user.getTotalReviews() > 0)
            getContext().startActivity(createUserReviewsActivityIntent());
        else showToastOnUiThred(R.string.no_reviews_exists);
    }

    private Intent createUserReviewsActivityIntent() {
        Intent intentUserReviewsActivity = new Intent(getContext(), UserReviewsActivity.class);
        intentUserReviewsActivity.putExtra(Constants.getId(), user.getId());
        intentUserReviewsActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intentUserReviewsActivity;
    }

    private void volleyCallBackOnSuccess(Object object) {
        if (!checkTapTargetBooleanPreferences())
            setTapTargetSequence();
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
        setTextViewUserReviewsLabel(user);
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
        profileActivity.startActivityForResult(createStartLoginActivityIntent(), Constants.getLaunchLoginActivity());
    }

    public void startLoginActivityFromLogOut() {
        clearUserSharedPreferences();
        profileActivity.startActivityForResult(createStartLoginActivityIntent(), Constants.getLaunchLoginActivity());
        //showToastOnUiThred(R.string.add_make_login);
    }

    public void startSearchUserActivity() {
        getContext().startActivity(createSearchUserActivityIntent());
    }

    public void startEditProfileActivity() {
        profileActivity.startActivityForResult(createEditProfileActivityIntent(), Constants.getLaunchEditProfileActivity());
    }

    public void startLeaderboardActivity() {
        getContext().startActivity(createLeaderboardActivityIntent());
    }

    @NotNull
    @Contract(" -> new")
    public Intent createStartLoginActivityIntent() {
        return new Intent(getContext(), LoginActivity.class);
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
        intentEditProfileActivity.putExtra(Constants.getUserIntentExtra(), user);
        return intentEditProfileActivity;
    }

    @NotNull
    @Contract(" -> new")
    private Intent createLeaderboardActivityIntent() {
        Intent intentEditProfileActivity = new Intent(getContext(), LeaderboardActivity.class);
        intentEditProfileActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intentEditProfileActivity;
    }

    public void handleOnActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d("PROFILE REQ - RES", requestCode + " - " + resultCode);
        if (resultCode == RESULT_OK) {
            if (requestCode == Constants.getLaunchLoginActivity()) {
                hideViewNoLoginProfileError();
                profileActivity.invalidateOptionsMenu();
                checkLogin();
                findUserByEmail();
            } else if (requestCode == Constants.getLaunchEditProfileActivity())
                findUserByEmail();
        } else {
            if (requestCode == Constants.getLaunchLoginActivity()) {
                profileActivity.invalidateOptionsMenu();
                showViewNoLoginProfileError();
            }
        }
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
        return profileActivity.getApplicationContext();
    }

    private FragmentActivity getActivity() {
        return profileActivity;
    }

    @NotNull
    private Resources getResources() {
        return profileActivity.getResources();
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

    public Toolbar getToolbar() {
        return profileActivity.getToolbar();
    }

    private TextView getTextViewUserTitle() {
        return profileActivity.getTextViewUserTitle();
    }

    private void setTextViewUserTitleText(User user) {
        getTextViewUserTitle().setText(user.getChosenTitle());
    }

    public CircleImageView getCircleImageViewUser() {
        return profileActivity.getCircleImageViewUser();
    }

    public ImageView getImageViewCloseEnlarge() {
        return profileActivity.getImageViewCloseEnlarge();
    }

    public ImageView getImageViewEnlarge() {
        return profileActivity.getImageViewEnlarge();
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

    public void setEnlargedImageViewUser(User user) {
        Picasso.with(getContext()).load(getUserImage(user))
                .fit()
                .centerCrop()
                .placeholder(R.drawable.user_profile_no_photo)
                .error(R.drawable.picasso_error)
                .into(getImageViewEnlarge());
    }

    public TextView getTextViewUserLevel() {
        return profileActivity.getTextViewUserLevel();
    }

    private void setTextViewUserLevelText(User user) {
        getTextViewUserLevel().setText(String.valueOf(getUserLevel(user)));
    }

    private TextView getTextViewUserNameLastname() {
        return profileActivity.getTextViewUserNameLastname();
    }

    private void setTextViewUserNameLastnameText(User user) {
        getTextViewUserNameLastname().setText(getUserName(user).concat(" ").concat(getUserLastname(user)));
    }

    public TextView getTextViewUsername() {
        return profileActivity.getTextViewUsername();
    }

    private void setTextViewUsernameText(User user) {
        getTextViewUsername().setText(getUsername(user));
    }

    private TextView getTextViewUserTotalReviews() {
        return profileActivity.getTextViewUserTotalReviews();
    }

    private void setTextViewUserTotalReviewsText(User user) {
        getTextViewUserTotalReviews().setText(String.valueOf(getUserTotalReviews(user)));
    }

    private TextView getTextViewUserAvarageRating() {
        return profileActivity.getTextViewUserAvarageRating();
    }

    private void setTextViewUserAvarageRatingText(User user) {
        getTextViewUserAvarageRating().setText(String.valueOf(user.getAvarageRating()));
    }

    private TextView getTextViewUserReviewsLabel() {
        return profileActivity.getTextViewUserReviewsLabel();
    }

    private void setTextViewUserReviewsLabel(User user) {
        if (user.getTotalReviews() == 1)
            getTextViewUserReviewsLabel().setText(getString(R.string.review));
    }

    public LinearLayout getLinearLayoutUserReviews() {
        return profileActivity.getLinearLayoutUserReviews();
    }

    public LinearLayoutCompat getLinearLayoutEnlargedImage() {
        return profileActivity.getLinearLayoutEnlargedImage();
    }

    public RecyclerView getRecyclerViewBadgeProfile() {
        return profileActivity.getRecyclerViewBadgeProfile();
    }

    private View getViewNoLoginProfileError() {
        return profileActivity.getViewNoLoginProfileError();
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

    private boolean checkTapTargetBooleanPreferences() {
        return new UserSharedPreferences(getContext()).constains(Constants.getTapTargetProfile());
    }

    private void writeTapTargetBooleanPreferences() {
        new UserSharedPreferences(getContext()).putBooleanSharedPreferences(Constants.getTapTargetProfile(), true);
    }

    public void setTapTargetSequence() {
        new TapTargetSequence(profileActivity).targets(
                createTapTargetForToolbar(R.id.search_user_profile, getString(R.string.search_user_profile_tap_title),
                        getString(R.string.search_user_profile_tap_description), 50),
                createTapTargetForToolbar(R.id.edit_profile, getString(R.string.edit_profile),
                        getString(R.string.edit_profile_tap_description), 50),
                createTapTargetForToolbar(R.id.leaderboard, getString(R.string.leaderboard),
                        getString(R.string.leaderboard_tap_description), 50),
                createTapTargetForToolbar(R.id.logout_profile, getString(R.string.logout),
                        getString(R.string.logout_profile_tap_description), 50),
                createTapTarget(getTextViewUserTitle(), getString(R.string.user_title_profile_tap_title),
                        getString(R.string.user_title_profile_tap_description), 70),
                createTapTarget(getTextViewUserLevel(), getString(R.string.user_level_profile_tap_title),
                        getString(R.string.user_level_profile_tap_description), 50),
                createTapTarget(getTextViewUserTotalReviews(), getString(R.string.read_all_reviews),
                        getString(R.string.read_all_reviews_tap_description), 50),
                createTapTarget(getTextViewUserAvarageRating(), getString(R.string.avarage_rating),
                        getString(R.string.avarage_rating_tap_description), 50))
                .listener(new TapTargetSequence.Listener() {
                    // This listener will tell us when interesting(tm) events happen in regards
                    // to the sequence
                    @Override
                    public void onSequenceFinish() {
                        writeTapTargetBooleanPreferences();
                    }

                    @Override
                    public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {
                        // Perform action for the current target
                    }

                    @Override
                    public void onSequenceCanceled(TapTarget lastTarget) {
                        // Boo
                    }
                }).start();

    }

    private TapTarget createTapTargetForToolbar(int menuItemId, String title, String body, int radius) {
        return TapTarget.forToolbarMenuItem(getToolbar(), menuItemId, title, body)
                .dimColor(android.R.color.black)
                .outerCircleColor(R.color.colorPrimary)
                .textColor(android.R.color.white)
                .targetCircleColor(R.color.white)
                .drawShadow(true)
                .cancelable(false)
                .tintTarget(true)
                .targetRadius(radius);
    }

    private TapTarget createTapTarget(View view, String title, String body, int radius) {
        return TapTarget.forView(view, title, body)
                .dimColor(android.R.color.black)
                .outerCircleColor(R.color.colorPrimary)
                .textColor(android.R.color.white)
                .targetCircleColor(R.color.white)
                .drawShadow(true)
                .cancelable(false)
                .tintTarget(true)
                .targetRadius(radius);
    }
}