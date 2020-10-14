package com.quiriletelese.troppadvisorproject.controllers;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.interfaces.Constants;
import com.quiriletelese.troppadvisorproject.utils.UserSharedPreferences;
import com.quiriletelese.troppadvisorproject.views.LoginActivity;
import com.quiriletelese.troppadvisorproject.views.ProfileFragment;

/**
 * @author Alessandro Quirile, Mauro Telese
 */

public class ProfileFragmentController implements Constants {

    private ProfileFragment profileFragment;
    private UserSharedPreferences userSharedPreferences;

    public ProfileFragmentController(ProfileFragment profileFragment) {
        this.profileFragment = profileFragment;
        this.userSharedPreferences = new UserSharedPreferences(getContext());
    }

    public void checkLogin() {
        if (!isLogged())
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

    private Intent createStartLoginActivityIntent() {
        return new Intent(getContext(), LoginActivity.class);
    }

    private void showViewNoLoginProfileError() {
        setViewNoLoginProfileErrorVisibility(View.VISIBLE);
    }

    private void hideViewNoLoginProfileError() {
        setViewNoLoginProfileErrorVisibility(View.GONE);
    }

    private void showToastOnUiThred(int string) {
        getActivity().runOnUiThread(() -> {
            Toast.makeText(getContext(), getString(string), Toast.LENGTH_SHORT).show();
        });
    }

    private void clearUserSharedPreferences() {
        userSharedPreferences.putStringSharedPreferences(ACCESS_TOKEN, "");
        userSharedPreferences.putStringSharedPreferences(ID_TOKEN, "");
        userSharedPreferences.putStringSharedPreferences(REFRESH_TOKEN, "");
        userSharedPreferences.putStringSharedPreferences(USERNAME, "");
        userSharedPreferences.putStringSharedPreferences(USER_FIRST_NAME, "");
        userSharedPreferences.putStringSharedPreferences(FAMILY_NAME, "");
        userSharedPreferences.putStringSharedPreferences(EMAIL, "");
    }

    public void setProfileFields(){
        setTextViewText(getTextViewName(), getStringSharedPreferences(USER_FIRST_NAME));
        setTextViewText(getTextViewFamilyName(), getStringSharedPreferences(FAMILY_NAME));
        setTextViewText(getTextViewEmail(), getStringSharedPreferences(EMAIL));
        setTextViewText(getTextViewUserName(), getStringSharedPreferences(USERNAME));
    }

    public boolean isLogged() {
        return !getAccessToken().equals("");
    }

    private View getViewNoLoginProfileError() {
        return profileFragment.getViewNoLoginProfileError();
    }

    private void setViewNoLoginProfileErrorVisibility(int visibility) {
        getViewNoLoginProfileError().setVisibility(visibility);
    }

    private String getAccessToken() {
        return createUserSharedPreferences().getStringSharedPreferences(ACCESS_TOKEN);
    }

    private UserSharedPreferences createUserSharedPreferences() {
        return new UserSharedPreferences(getContext());
    }

    private Context getContext() {
        return profileFragment.getContext();
    }

    private FragmentActivity getActivity() {
        return profileFragment.getActivity();
    }

    private Resources getResources() {
        return profileFragment.getResources();
    }

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

    private void setTextViewText(TextView textView, String value){
        textView.setText(value);
    }

}