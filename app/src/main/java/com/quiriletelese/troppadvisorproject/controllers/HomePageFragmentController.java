package com.quiriletelese.troppadvisorproject.controllers;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.adapters.RecyclerViewAttractionsListAdapter;
import com.quiriletelese.troppadvisorproject.dao_interfaces.AttractionDAO;
import com.quiriletelese.troppadvisorproject.dao_interfaces.UserDAO;
import com.quiriletelese.troppadvisorproject.factories.DAOFactory;
import com.quiriletelese.troppadvisorproject.model_helpers.Constants;
import com.quiriletelese.troppadvisorproject.model_helpers.PointSearch;
import com.quiriletelese.troppadvisorproject.models.Attraction;
import com.quiriletelese.troppadvisorproject.utils.ConfigFileReader;
import com.quiriletelese.troppadvisorproject.utils.UserSharedPreferences;
import com.quiriletelese.troppadvisorproject.views.AttractionsListActivity;
import com.quiriletelese.troppadvisorproject.views.HomeActivity;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBack;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author Alessandro Quirile, Mauro Telese
 */

public class HomePageFragmentController implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, LocationListener {

    private final HomeActivity homeActivity;
    private final DAOFactory daoFactory = DAOFactory.getInstance();
    private final UserSharedPreferences userSharedPreferences;
    private RecyclerViewAttractionsListAdapter recyclerViewAttractionsListAdapter;
    private LinearLayoutManager linearLayoutManager;
    private PointSearch pointSearch = null;
    private final int size = 50;
    private int page = 0;
    private boolean isLoadingData = false;
    private boolean isLocated = false;
    private AlertDialog alertDialog;
    private final LocationManager locationManager;

    public HomePageFragmentController(HomeActivity homeActivity) {
        this.homeActivity = homeActivity;
        userSharedPreferences = new UserSharedPreferences(getContext());
        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public void onClick(View view) {
        onClickHelper(view);
    }

    @Override
    public void onRefresh() {
        findByRsql(pointSearch);
    }

    @Override
    public void onLocationChanged(Location location) {
        pointSearch = createPointSearch(Arrays.asList(location.getLatitude(), location.getLongitude()));
        if (!isLocated) {
            initializeRecyclerViewAttractions(pointSearch);
            if (!checkTapTargetBooleanPreferences())
                setTapTargetSequence();
            isLocated = true;
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {
        getViewNoGeolocationError().setVisibility(View.GONE);
    }

    @Override
    public void onProviderDisabled(String s) {
        isLocated = false;
        getViewNoGeolocationError().setVisibility(View.VISIBLE);
    }

    public void findAttractionsByRsqlHelper(VolleyCallBack volleyCallBack, PointSearch pointSearch) {
        getAttractionDAO().findByRsql(volleyCallBack, pointSearch, "0", getContext(), page, size, true);
    }

    public void updateDailyUserLevelHelper(VolleyCallBack volleyCallBack) {
        getUserDAO().updateDailyUserLevel(volleyCallBack, getEmail(), getContext());
    }

    public void findByRsql(PointSearch pointSearch) {
        findAttractionsByRsqlHelper(new VolleyCallBack() {
            @Override
            public void onSuccess(Object object) {
                volleyCallbackOnSuccess(object, pointSearch);
            }

            @Override
            public void onError(String errorCode) {
                volleyCallbackOnError(errorCode);
            }
        }, pointSearch);
    }

    private void updateDailyUserLevel() {
        updateDailyUserLevelHelper(new VolleyCallBack() {
            @Override
            public void onSuccess(Object object) {

            }

            @Override
            public void onError(String errorCode) {

            }
        });
    }

    public void initializeRecyclerViewAttractions(PointSearch pointSearch) {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION))
            findByRsql(pointSearch);
        else
            getViewMissingLocationPermissionError().setVisibility(View.VISIBLE);
    }

    private void volleyCallbackOnSuccess(Object object, PointSearch pointSearch) {
        List<Attraction> attractions = (List<Attraction>) object;
        if (isLoadingData)
            addNewAttractionsToList(attractions);
        else
            initializeRecyclerViewOnSuccess(attractions, pointSearch);
        setViewVisibility(getProgressBarLoadMore(), View.GONE);
        getSwipeRefreshLayoutHomeFrament().setRefreshing(false);
    }

    private void volleyCallbackOnError(@NotNull String errorCode) {
        setViewVisibility(getProgressBarLoadMore(), View.GONE);
        getSwipeRefreshLayoutHomeFrament().setRefreshing(false);
        switch (errorCode) {
            case "204":
                if (!isLoadingData)
                    initializeRecyclerViewOnError(errorCode);
                break;
            default:
                handleOtherVolleyError(errorCode);
                break;
        }
    }

    private void handleOtherVolleyError(String errorCode) {
        initializeRecyclerViewOnError(errorCode);
        showToastVolleyError(R.string.unexpected_error_while_fetch_data);
    }

    public void addRecyclerViewOnScrollListener() {
        getRecyclerViewAttractions().addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isScrollingDown(dy))
                    if (hasScrolledToLastItem(recyclerView))
                        loadMoreAttractions();
            }
        });
    }

    private boolean isScrollingDown(int dy) {
        return dy > 0;
    }

    private boolean hasScrolledToLastItem(@NotNull RecyclerView recyclerView) {
        return !recyclerView.canScrollVertically(1);
    }

    private void loadMoreAttractions() {
        page++;
        setIsLoadingData(true);
        setViewVisibility(getProgressBarLoadMore(), View.VISIBLE);
        findByRsql(pointSearch);
    }

    private void initializeRecyclerViewOnSuccess(List<Attraction> attractions, PointSearch pointSearch) {
        setViewNoAttractionsErrorInvisible();
        linearLayoutManager = createLinearLayoutManager();
        recyclerViewAttractionsListAdapter = createRecyclerViewAttractionAdapter(attractions, pointSearch);
        setRecyclerViewAttractionOnSuccess();
    }

    private void initializeRecyclerViewOnError(String errorCode) {
        switch (errorCode) {
            case "204":
                runNoAttractionsErrorOnUiThread();
                break;
            default:
                runNoAttractionsErrorOnUiThread();
                showToastVolleyError(R.string.unexpected_error_while_fetch_data);
        }
    }

    private void addNewAttractionsToList(List<Attraction> attractions) {
        recyclerViewAttractionsListAdapter.addListItems(attractions);
        recyclerViewAttractionsListAdapter.notifyDataSetChanged();
        setViewVisibility(getProgressBarLoadMore(), View.GONE);
    }

    @NotNull
    private PointSearch createPointSearch(@NotNull List<Double> pointSearchInformation) {
        PointSearch pointSearch = new PointSearch();
        pointSearch.setLatitude(pointSearchInformation.get(0));
        pointSearch.setLongitude(pointSearchInformation.get(1));
        pointSearch.setDistance(10.0);
        return pointSearch;
    }

    private void setViewVisibility(View view, int visibility) {
        homeActivity.runOnUiThread(() -> view.setVisibility(visibility));
    }

    private void onClickHelper(View view) {
        switch (view.getId()) {
            case R.id.button_provide_permission:
                requestPermissions(Manifest.permission.ACCESS_FINE_LOCATION);
                break;
        }
    }

    public void checkDailyReward() {
        if (hasLogged() && (!existDailyReward() || !isSameDay())) {
            showDailyRewardDialog();
            updateDailyUserLevel();
            saveSharedPreferencesDailyReward();
        }
    }

    private boolean hasLogged() {
        return !getAccessToken().equals("");
    }

    private String getAccessToken() {
        return createUserSharedPreferences().getStringSharedPreferences(Constants.getAccessToken());
    }

    private String getEmail() {
        return createUserSharedPreferences().getStringSharedPreferences(Constants.getEmail());
    }

    private UserSharedPreferences createUserSharedPreferences() {
        return new UserSharedPreferences(getContext());
    }

    private boolean existDailyReward() {
        return userSharedPreferences.constains(Constants.getDailyReward());
    }

    private boolean isSameDay() {
        return userSharedPreferences.getStringSharedPreferences(Constants.getDailyReward()).equals(getDayMonthYear());
    }

    private void saveSharedPreferencesDailyReward() {
        userSharedPreferences.putStringSharedPreferences(Constants.getDailyReward(), getDayMonthYear());
    }

    private String getDayMonthYear() {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_MONTH) + "-" + calendar.get(Calendar.MONTH) + "-" + calendar.get(Calendar.YEAR);
    }

    private void showDailyRewardDialog() {
        AlertDialog.Builder alertDialogBuilder = createAlertDialogBuilder();
        alertDialogBuilder.setView(getLayoutInflater().inflate(getAlertDialogLayout(), null));
        alertDialogBuilder.setPositiveButton("OK", null);
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @NotNull
    @Contract(" -> new")
    private AlertDialog.Builder createAlertDialogBuilder() {
        return new AlertDialog.Builder(homeActivity);
    }

    @NotNull
    private LayoutInflater getLayoutInflater() {
        return homeActivity.getLayoutInflater();
    }

    private int getAlertDialogLayout() {
        return R.layout.dialog_daily_reward_layout;
    }

    public void setListenerOnViewComponents() {
        getSwipeRefreshLayoutHomeFrament().setOnRefreshListener(this);
        getButtonProvidePermission().setOnClickListener(this);
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        } else
            requestPermissions(Manifest.permission.ACCESS_FINE_LOCATION);
    }

    private void requestPermissions(String permission) {
        ActivityCompat.requestPermissions(homeActivity, new String[]{permission}, Constants.getAccessFineLocationCode());
    }

    private void showToastVolleyError(int stringId) {
        setIsLoadingData(false);
        showToastOnUiThread(stringId);
    }

    private void showToastOnUiThread(int stringId) {
        homeActivity.runOnUiThread(() ->
                Toast.makeText(homeActivity, getString(stringId), Toast.LENGTH_SHORT).show());
    }

    private Resources getResources() {
        return (homeActivity.getResources());
    }

    @NotNull
    private String getString(int string) {
        return getResources().getString(string);
    }

    private void setIsLoadingData(boolean value) {
        isLoadingData = value;
    }

    public void onRequestPermissionsResultHelper(int requestCode, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 100:
                checkPermissionResult(grantResults);
                break;
        }
    }

    private void checkPermissionResult(@NonNull int[] grantResults) {
        if (isPermissionGranted(grantResults)) {
            Log.d("PERMISSION", "LOCATION PERMISSION GRANTED");
            getSwipeRefreshLayoutHomeFrament().setRefreshing(true);
            getViewMissingLocationPermissionError().setVisibility(View.GONE);
            if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION))
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        } else {
            getSwipeRefreshLayoutHomeFrament().setRefreshing(false);
            getViewMissingLocationPermissionError().setVisibility(View.VISIBLE);
        }
    }

    @NotNull
    @Contract(" -> new")
    private LinearLayoutManager createLinearLayoutManager() {
        return new LinearLayoutManager(getContext(), setRecyclerViewHorizontalOrientation(), false);
    }

    private int setRecyclerViewHorizontalOrientation() {
        return RecyclerView.VERTICAL;
    }

    @NotNull
    @Contract("_ -> new")
    private RecyclerViewAttractionsListAdapter createRecyclerViewAttractionAdapter(List<Attraction> attractions, PointSearch pointSearch) {
        return new RecyclerViewAttractionsListAdapter(getContext(), attractions, pointSearch);
    }

    private void runNoAttractionsErrorOnUiThread() {
        homeActivity.runOnUiThread(this::setViewNoAttractionsErrorVisible);
    }

    public void startAttractionsListActivity() {
        if (pointSearch != null) {
            Intent attractionsListActivityIntent = createAttractionsListActivityIntent();
            getContext().startActivity(attractionsListActivityIntent);
        } else
            Toast.makeText(getContext(), getString(R.string.position_calculation_in_progress), Toast.LENGTH_SHORT).show();
    }

    @NotNull
    private Intent createAttractionsListActivityIntent() {
        Intent intentAttractionsListActivity = new Intent(getContext(), AttractionsListActivity.class);
        intentAttractionsListActivity.putExtra(Constants.getPointSearch(), pointSearch);
        intentAttractionsListActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intentAttractionsListActivity;
    }

    public boolean checkPermission(String permission) {
        return ActivityCompat.checkSelfPermission(getContext(), permission) != PackageManager.PERMISSION_DENIED;
    }

    private Context getContext() {
        return homeActivity.getApplicationContext();
    }

    private AttractionDAO getAttractionDAO() {
        return daoFactory.getAttractionDAO(getStorageTechnology(Constants.getAttractionStorageTechnology()));
    }

    private UserDAO getUserDAO() {
        return daoFactory.getUserDAO(getStorageTechnology(Constants.getUserStorageTechnology()));
    }

    private String getStorageTechnology(String storageTechnology) {
        return ConfigFileReader.getProperty(storageTechnology, getContext());
    }

    public Toolbar getToolbar() {
        return homeActivity.getToolbar();
    }

    public SwipeRefreshLayout getSwipeRefreshLayoutHomeFrament() {
        return homeActivity.getSwipeRefreshLayoutHomeFrament();
    }

    private RecyclerView getRecyclerViewAttractions() {
        return homeActivity.getRecyclerViewAttractions();
    }

    private ProgressBar getProgressBarLoadMore() {
        return homeActivity.getProgressBarAttractionHomeLoadMore();
    }

    private void setRecyclerViewAttractionOnSuccess() {
        getRecyclerViewAttractions().setLayoutManager(linearLayoutManager);
        getRecyclerViewAttractions().setAdapter(recyclerViewAttractionsListAdapter);
        recyclerViewAttractionsListAdapter.notifyDataSetChanged();
    }

    private Button getButtonProvidePermission() {
        return homeActivity.getButtonProvidePermission();
    }

    private View getViewNoAttractionsError() {
        return homeActivity.getViewNoAttractionsError();
    }

    private void setViewNoAttractionsErrorVisible() {
        getViewNoAttractionsError().setVisibility(View.VISIBLE);
    }

    private void setViewNoAttractionsErrorInvisible() {
        getViewNoAttractionsError().setVisibility(View.GONE);
    }

    public boolean isPermissionGranted(@NonNull int[] grantResults) {
        return grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
    }

    public View getViewNoGeolocationError() {
        return homeActivity.getViewNoGeolocationError();
    }

    public View getViewMissingLocationPermissionError() {
        return homeActivity.getViewMissingLocationPermissionError();
    }

    private boolean checkTapTargetBooleanPreferences() {
        return new UserSharedPreferences(getContext()).constains(Constants.getTapTargetHome());
    }

    private void writeTapTargetBooleanPreferences() {
        new UserSharedPreferences(getContext()).putBooleanSharedPreferences(Constants.getTapTargetHome(), true);
    }

    public void setTapTargetSequence() {
        new TapTargetSequence(homeActivity).targets(
                createTapTargetForToolbar(R.id.profile, getString(R.string.profile_tap_title),
                        getString(R.string.profile_tap_description), 50),
                createTapTargetForToolbar(R.id.search_attractions, getString(R.string.search_attractions_tap_title),
                        getString(R.string.search_attractions_tap_description), 50))
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

    private TapTarget create(View view, String title, String body, int radius) {
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