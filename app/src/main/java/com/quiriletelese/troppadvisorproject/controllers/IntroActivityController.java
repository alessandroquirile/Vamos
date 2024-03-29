package com.quiriletelese.troppadvisorproject.controllers;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.adapters.ViewPagerIntroAdapter;
import com.quiriletelese.troppadvisorproject.model_helpers.Constants;
import com.quiriletelese.troppadvisorproject.utils.UserSharedPreferences;
import com.quiriletelese.troppadvisorproject.views.HomeActivity;
import com.quiriletelese.troppadvisorproject.views.HomePageActivity;
import com.quiriletelese.troppadvisorproject.views.IntroActivity;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * @author Alessandro Quirile, Mauro Telese
 */

public class IntroActivityController implements ViewPager.OnPageChangeListener, View.OnClickListener {

    private final IntroActivity introActivity;

    public IntroActivityController(IntroActivity introActivity) {
        this.introActivity = introActivity;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        addDots(position);
        if (isLastPage(position))
            setButtonPermissionVisibility(View.VISIBLE);
        else
            setButtonPermissionVisibility(View.INVISIBLE);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View view) {
        onClickHekper(view);
    }

    private void requestPermissions(String permission, int requestCode) {
        ActivityCompat.requestPermissions(introActivity, new String[]{permission}, requestCode);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onRequestPermissionsResult(int requestCode, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 100:
                checkPermissionResult(grantResults);
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkPermissionResult(@NonNull int[] grantResults) {
        if (isPermissionGranted(grantResults)) {
            writeSharedPreferences();
            startHomePageActivity();
        } else
            showMessageOKCancel((dialogInterface, i) -> requestPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Constants.getAccessFineLocationCode()));
    }

    private void showMessageOKCancel(DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(introActivity)
                .setView(getLayoutInflater().inflate(R.layout.dialog_missing_intro_location_permission_layout, null))
                .setPositiveButton("OK", okListener)
                .setNegativeButton(R.string.cancel, null)
                .create()
                .show();
    }

    @NotNull
    private LayoutInflater getLayoutInflater() {
        return introActivity.getLayoutInflater();
    }

    private void onClickHekper(@NotNull View view) {
        switch (view.getId()) {
            case R.id.button_permission:
                requestPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Constants.getAccessFineLocationCode());
                break;
        }
    }

    public void setListenerOnComponents() {
        getViewPager().addOnPageChangeListener(this);
        getButtonPermission().setOnClickListener(this);
    }

    public void addDots(int position) {
        TextView[] dots = new TextView[getViewPagerIntroAdapter().getCount()];
        getLinearLayoutDots().removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(getContext());
            dots[i].setText(Html.fromHtml("&#8226"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getColor(R.color.lightGrey));
            getLinearLayoutDots().addView(dots[i]);
        }
        if (dots.length > 0)
            dots[position].setTextColor(getColor(R.color.colorPrimary));
    }

    private void writeSharedPreferences() {
        UserSharedPreferences userSharedPreferences = createUserSharedPreferences();
        userSharedPreferences.putBooleanSharedPreferences(Constants.getIsAppOpenedForFirstTime(), false);
    }

    private void showToastOnUiThread(int stringId) {
        introActivity.runOnUiThread(() -> {
            Toast.makeText(introActivity, getString(stringId), Toast.LENGTH_LONG).show();
        });
    }

    @NotNull
    @Contract(" -> new")
    private UserSharedPreferences createUserSharedPreferences() {
        return new UserSharedPreferences(getContext());
    }

    private void startHomePageActivity() {
        getContext().startActivity(createIntent(HomeActivity.class));
    }

    @NotNull
    private Intent createIntent(Class<?> destinationClass) {
        Intent intent = new Intent(getContext(), destinationClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    private Context getContext() {
        return introActivity.getApplicationContext();
    }

    private Resources getResources() {
        return introActivity.getResources();
    }

    @NotNull
    private String getString(int string) {
        return getResources().getString(string);
    }

    private int getColor(int color) {
        return getResources().getColor(color);
    }

    private LinearLayout getLinearLayoutDots() {
        return introActivity.getLinearLayoutDots();
    }

    private ViewPager getViewPager() {
        return introActivity.getViewPager();
    }

    private ViewPagerIntroAdapter getViewPagerIntroAdapter() {
        return introActivity.getViewPagerIntroAdapter();
    }

    private Button getButtonPermission() {
        return introActivity.getButtonPermission();
    }

    private void setButtonPermissionVisibility(int visibility) {
        getButtonPermission().setVisibility(visibility);
    }

    private boolean isLastPage(int position) {
        return position == getViewPagerIntroAdapter().getCount() - 1;
    }

    private boolean isPermissionGranted(@NonNull int[] grantResults) {
        return grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
    }

}
