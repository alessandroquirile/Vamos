package com.quiriletelese.troppadvisorproject.controllers;

import android.content.Context;
import android.content.Intent;

import com.quiriletelese.troppadvisorproject.interfaces.Constants;
import com.quiriletelese.troppadvisorproject.utils.UserSharedPreferences;
import com.quiriletelese.troppadvisorproject.views.HomePageActivity;
import com.quiriletelese.troppadvisorproject.views.IntroActivity;
import com.quiriletelese.troppadvisorproject.views.SplashScreenActivity;

/**
 * @author Alessandro Quirile, Mauro Telese
 */

public class SplashScreenActivityController implements Constants {

    private SplashScreenActivity splashScreenActivity;

    public SplashScreenActivityController(SplashScreenActivity splashScreenActivity) {
        this.splashScreenActivity = splashScreenActivity;
    }

    public boolean isAppOpenedForFirstTime() {
        return createUserSharedPreferences().getBooleanSharedPreferences(IS_APP_OPENED_FOR_FIRST_TIME);
    }

    private UserSharedPreferences createUserSharedPreferences() {
        return new UserSharedPreferences(getContext());
    }

    private Context getContext() {
        return splashScreenActivity.getApplicationContext();
    }

    public void startNextActivity() {
        if (isAppOpenedForFirstTime())
            startIntroActivity();
        else
            startHomePageActivity();
        finish();
    }

    private void startHomePageActivity() {
        getContext().startActivity(createIntent(HomePageActivity.class));
    }

    private void startIntroActivity() {
        getContext().startActivity(createIntent(IntroActivity.class));
    }

    private Intent createIntent(Class<?> destinationClass) {
        Intent intent = new Intent(getContext(), destinationClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    private void finish(){
        splashScreenActivity.finish();
    }

}
