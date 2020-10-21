package com.quiriletelese.troppadvisorproject.controllers;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.model_helpers.Constants;
import com.quiriletelese.troppadvisorproject.utils.UserSharedPreferences;
import com.quiriletelese.troppadvisorproject.views.LoginActivity;
import com.quiriletelese.troppadvisorproject.views.ProfileFragment;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * @author Alessandro Quirile, Mauro Telese
 */

public class ProfileFragmentController {

    private final ProfileFragment profileFragment;
    private final UserSharedPreferences userSharedPreferences;

    public ProfileFragmentController(ProfileFragment profileFragment) {
        this.profileFragment = profileFragment;
        this.userSharedPreferences = new UserSharedPreferences(getContext());
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

    @NotNull
    @Contract(" -> new")
    private Intent createStartLoginActivityIntent() {
        Intent intentLoginActivity = new Intent(getContext(), LoginActivity.class);
        intentLoginActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intentLoginActivity;
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

    public void setProfileFields(){
        setTextViewText(getTextViewName(), getStringSharedPreferences(Constants.getUserFirstName()));
        setTextViewText(getTextViewFamilyName(), getStringSharedPreferences(Constants.getFamilyName()));
        setTextViewText(getTextViewEmail(), getStringSharedPreferences(Constants.getEmail()));
        setTextViewText(getTextViewUserName(), getStringSharedPreferences(Constants.getUsername()));
    }

    public boolean hasLogged() {
        return !getAccessToken().equals("");
    }

    private View getViewNoLoginProfileError() {
        return profileFragment.getViewNoLoginProfileError();
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

    private String getStringSharedPreferences(String preference){
        return userSharedPreferences.getStringSharedPreferences(preference);
    }

    private TextView getTextViewName(){
        return profileFragment.getTextViewName();
    }

    private TextView getTextViewFamilyName(){
        return profileFragment.getTextViewFamilyName();
    }

    private TextView getTextViewEmail(){
        return profileFragment.getTextViewEmail();
    }

    private TextView getTextViewUserName(){
        return profileFragment.getTextViewUserName();
    }

    private void setTextViewText(@NotNull TextView textView, String value) {
        textView.setText(value);
    }

}