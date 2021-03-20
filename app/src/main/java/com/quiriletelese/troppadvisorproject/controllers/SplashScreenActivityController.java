package com.quiriletelese.troppadvisorproject.controllers;

import android.content.Context;
import android.content.Intent;

import com.quiriletelese.troppadvisorproject.model_helpers.Constants;
import com.quiriletelese.troppadvisorproject.utils.UserSharedPreferences;
import com.quiriletelese.troppadvisorproject.views.HomeActivity;
import com.quiriletelese.troppadvisorproject.views.HomePageActivity;
import com.quiriletelese.troppadvisorproject.views.IntroActivity;
import com.quiriletelese.troppadvisorproject.views.SplashScreenActivity;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * @author Alessandro Quirile, Mauro Telese
 */

public class SplashScreenActivityController {

    private final SplashScreenActivity splashScreenActivity;
    private UserSharedPreferences userSharedPreferences;

    public SplashScreenActivityController(SplashScreenActivity splashScreenActivity) {
        this.splashScreenActivity = splashScreenActivity;
    }

    public boolean hasAppBeenOpenedForFirstTime() {
        return createUserSharedPreferences().getBooleanSharedPreferences(Constants.getIsAppOpenedForFirstTime());
    }

    @NotNull
    @Contract(" -> new")
    private UserSharedPreferences createUserSharedPreferences() {
        userSharedPreferences = new UserSharedPreferences(getContext());
        return userSharedPreferences;
    }

    private Context getContext() {
        return splashScreenActivity.getApplicationContext();
    }

    public void startNextActivity() {
        if (hasAppBeenOpenedForFirstTime())
            startIntroActivity();
        else
            startHomePageActivity();
        finish();
    }

    private void startHomePageActivity() {
        getContext().startActivity(createIntent(HomeActivity.class));
    }

    private void startIntroActivity() {
        getContext().startActivity(createIntent(IntroActivity.class));
    }

    @NotNull
    private Intent createIntent(Class<?> destinationClass) {
        Intent intent = new Intent(getContext(), destinationClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    private void finish(){
        splashScreenActivity.finish();
    }

}
