package com.quiriletelese.troppadvisorproject.controllers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.widget.Toast;

import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.dao_interfaces.ReviewDAO;
import com.quiriletelese.troppadvisorproject.factories.DAOFactory;
import com.quiriletelese.troppadvisorproject.model_helpers.Constants;
import com.quiriletelese.troppadvisorproject.utils.ConfigFileReader;
import com.quiriletelese.troppadvisorproject.utils.UserSharedPreferences;
import com.quiriletelese.troppadvisorproject.views.LoginActivity;
import com.quiriletelese.troppadvisorproject.views.UserReviewsActivity;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBack;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class ReadUserReviewsThumbController {

    private final DAOFactory daoFactory = DAOFactory.getInstance();
    private final UserReviewsActivity userReviewsActivity;
    private AlertDialog alertDialog;

    public ReadUserReviewsThumbController(UserReviewsActivity userReviewsActivity) {
        this.userReviewsActivity = userReviewsActivity;
    }

    private void updateVotersHelper(VolleyCallBack volleyCallBack, String id, int vote) {
        getReviewDao().updateVoters(volleyCallBack, id, getEmail(), vote, getContext());
    }

    public void updateVoters(String id, int vote) {
        updateVotersHelper(new VolleyCallBack() {
            @Override
            public void onSuccess(Object object) {
                showToastOnUiThred(R.string.successfully_voted);
            }

            @Override
            public void onError(String errorCode) {
                showToastOnUiThred(R.string.vote_error);
            }
        }, id, vote);
    }

    public boolean hasLogged() {
        return !getEmail().equals("");
    }

    public void showLoginDialog() {
        AlertDialog.Builder alertDialogBuilder = createAlertDialogBuilder();
        alertDialogBuilder.setTitle(R.string.do_login);
        alertDialogBuilder.setMessage(R.string.do_login_for_vote);
        alertDialogBuilder.setPositiveButton(R.string.do_login, (dialogInterface, i) -> startLoginActivity());
        alertDialogBuilder.setNegativeButton(R.string.cancel, (dialogInterface, i) -> dismissDialog());
        alertDialogBuilder.setCancelable(false);
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @NotNull
    @Contract(" -> new")
    private AlertDialog.Builder createAlertDialogBuilder() {
        return new AlertDialog.Builder(userReviewsActivity);
    }

    private void dismissDialog() {
        alertDialog.dismiss();
    }

    private void startLoginActivity() {
        userReviewsActivity.startActivity(new Intent(getContext(), LoginActivity.class));
    }

    private String getEmail() {
        return createUserSharedPreferences().getStringSharedPreferences(Constants.getEmail());
    }

    private UserSharedPreferences createUserSharedPreferences() {
        return new UserSharedPreferences(userReviewsActivity.getApplicationContext());
    }

    private Context getContext() {
        return userReviewsActivity.getApplicationContext();
    }

    private Resources getResources() {
        return userReviewsActivity.getResources();
    }

    @NotNull
    private String getString(int string) {
        return getResources().getString(string);
    }

    private ReviewDAO getReviewDao() {
        return daoFactory.getReviewDAO(getStorageTechnology(Constants.getReviewStorageTechnology()));
    }

    private String getStorageTechnology(String storageTechnology) {
        return ConfigFileReader.getProperty(storageTechnology, getContext());
    }

    public void showToastOnUiThred(int stringId) {
        userReviewsActivity.runOnUiThread(() ->
                Toast.makeText(getContext(), getString(stringId), Toast.LENGTH_SHORT).show());
    }

    private String getUserEmail(){
        return new UserSharedPreferences(userReviewsActivity).getStringSharedPreferences(Constants.getEmail());
    }

    public boolean isSameUser(String email){
        return email.equals(getUserEmail());
    }
}
