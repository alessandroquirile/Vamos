package com.quiriletelese.troppadvisorproject.controllers;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.adapters.RecyclerViewAttractionAdapter;
import com.quiriletelese.troppadvisorproject.adapters.RecyclerViewAttractionsListAdapter;
import com.quiriletelese.troppadvisorproject.adapters.RecyclerViewHotelAdapter;
import com.quiriletelese.troppadvisorproject.adapters.RecyclerViewRestaurantAdapter;
import com.quiriletelese.troppadvisorproject.dao_interfaces.AttractionDAO;
import com.quiriletelese.troppadvisorproject.factories.DAOFactory;
import com.quiriletelese.troppadvisorproject.model_helpers.Constants;
import com.quiriletelese.troppadvisorproject.model_helpers.PointSearch;
import com.quiriletelese.troppadvisorproject.models.Attraction;
import com.quiriletelese.troppadvisorproject.models.Hotel;
import com.quiriletelese.troppadvisorproject.models.Restaurant;
import com.quiriletelese.troppadvisorproject.utils.ConfigFileReader;
import com.quiriletelese.troppadvisorproject.utils.GPSTracker;
import com.quiriletelese.troppadvisorproject.views.AttractionsListActivity;
import com.quiriletelese.troppadvisorproject.views.HomePageFragment;
import com.quiriletelese.troppadvisorproject.views.HotelsListActivity;
import com.quiriletelese.troppadvisorproject.views.RestaurantsListActivity;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBack;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * @author Alessandro Quirile, Mauro Telese
 */

public class HomePageFragmentController implements View.OnClickListener {

    private final HomePageFragment homePageFragment;
    private final DAOFactory daoFactory = DAOFactory.getInstance();
    private final GPSTracker gpsTracker;
    private RecyclerViewAttractionsListAdapter recyclerViewAttractionsListAdapter;
    private LinearLayoutManager linearLayoutManager;
    private PointSearch pointSearch;
    private final int size = 50;
    private int page = 0;
    private boolean isLoadingData = false;

    public HomePageFragmentController(HomePageFragment homePageFragment) {
        this.homePageFragment = homePageFragment;
        this.gpsTracker = createGpsTracker();
    }

    @Override
    public void onClick(View view) {
        onClickHelper(view);
    }

    public void findAttractionsByRsqlHelper(VolleyCallBack volleyCallBack, PointSearch pointSearch) {
        this.pointSearch = pointSearch;
        getAttractionDAO().findByRsql(volleyCallBack, pointSearch, "0", getContext(), page, size);
    }

    public void findByRsql() {
        findAttractionsByRsqlHelper(new VolleyCallBack() {
            @Override
            public void onSuccess(Object object) {
                volleyCallbackOnSuccess(object);
            }

            @Override
            public void onError(String errorCode) {
                volleyCallbackOnError(errorCode);
            }
        }, getLocation());
    }

    private void volleyCallbackOnSuccess(Object object) {
        List<Attraction> attractions = (List<Attraction>) object;
        if (isLoadingData)
            addNewAttractionsToList(attractions);
        else
            initializeRecyclerViewOnSuccess(attractions);
        setProgressBarVisibilityOnUiThred(View.INVISIBLE);
    }

    private void volleyCallbackOnError(@NotNull String errorCode) {
        setProgressBarVisibilityOnUiThred(View.INVISIBLE);
        switch (errorCode) {
            case "204":
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
        setProgressBarVisibilityOnUiThred(View.VISIBLE);
        findByRsql();
    }

    private void initializeRecyclerViewOnSuccess(List<Attraction> attractions) {
        setViewNoAttractionsErrorInvisible();
        linearLayoutManager = createLinearLayoutManager();
        recyclerViewAttractionsListAdapter = createRecyclerViewAttractionAdapter(attractions);
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
        setProgressBarVisibilityOnUiThred(View.INVISIBLE);
    }

    public PointSearch getLocation() {
        Double latitude = gpsTracker.getLatitude();
        Double longitude = gpsTracker.getLongitude();
        return createPointSearch(Arrays.asList(latitude, longitude));
    }

    @NotNull
    private PointSearch createPointSearch(@NotNull List<Double> pointSearchInformation) {
        PointSearch pointSearch = new PointSearch();
        pointSearch.setLatitude(pointSearchInformation.get(0));
        pointSearch.setLongitude(pointSearchInformation.get(1));
        pointSearch.setDistance(5.0);
        return pointSearch;
    }

    private void setProgressBarVisibilityOnUiThred(int visibility) {
        homePageFragment.getActivity().runOnUiThread(() -> getProgressBarLoadMore().setVisibility(visibility));
    }

    private void onClickHelper(View view) {
        switch (view.getId()) {
            case R.id.button_enable_position:
                startEnablePositionActivity();
                break;
            case R.id.button_provide_permission:
                requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, Constants.getAccessFineLocationCode());
                break;
        }
    }

    public void setListenerOnViewComponents() {
        getButtonEnablePosition().setOnClickListener(this);
        getButtonProvidePermission().setOnClickListener(this);
    }

    private void showToastVolleyError(int stringId) {
        setIsLoadingData(false);
        showToastOnUiThread(stringId);
    }

    private void showToastOnUiThread(int stringId) {
        homePageFragment.getActivity().runOnUiThread(() ->
                Toast.makeText(homePageFragment.getActivity(), getString(stringId), Toast.LENGTH_SHORT).show());
    }

    private Resources getResources() {
        return (homePageFragment.getActivity().getResources());
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
            initializeRecyclerViews();
            homePageFragment.getViewMissingLocationPermissionError().setVisibility(View.GONE);
        }
    }

    private void initializeRecyclerViews() {
        homePageFragment.initializeRecyclerView();
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
    private RecyclerViewAttractionsListAdapter createRecyclerViewAttractionAdapter(List<Attraction> attractions) {
        return new RecyclerViewAttractionsListAdapter(getContext(), attractions);
    }

    private void runNoAttractionsErrorOnUiThread() {
        requireActivity().runOnUiThread(this::setViewNoAttractionsErrorVisible);
    }

    private void startHotelsListActivity() {
        Intent hotelsListActivityIntent = createHotelsListActivityIntent();
        getContext().startActivity(hotelsListActivityIntent);
    }

    @NotNull
    private Intent createHotelsListActivityIntent() {
        Intent intentHotelsListActivity = new Intent(getContext(), HotelsListActivity.class);
        intentHotelsListActivity.putExtra(Constants.getPointSearch(), pointSearch);
        intentHotelsListActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intentHotelsListActivity;
    }

    private void startRestaurantsListActivity() {
        Intent restaurantsListActivityIntent = createRestaurantsListActivityIntent();
        getContext().startActivity(restaurantsListActivityIntent);
    }

    @NotNull
    private Intent createRestaurantsListActivityIntent() {
        Intent intentRestaurantsListActivity = new Intent(getContext(), RestaurantsListActivity.class);
        intentRestaurantsListActivity.putExtra(Constants.getPointSearch(), pointSearch);
        intentRestaurantsListActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intentRestaurantsListActivity;
    }

    private void startAttractionsListActivity() {
        Intent attractionsListActivityIntent = createAttractionsListActivityIntent();
        getContext().startActivity(attractionsListActivityIntent);
    }

    @NotNull
    private Intent createAttractionsListActivityIntent() {
        Intent intentAttractionsListActivity = new Intent(getContext(), AttractionsListActivity.class);
        intentAttractionsListActivity.putExtra(Constants.getPointSearch(), pointSearch);
        intentAttractionsListActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intentAttractionsListActivity;
    }

    private void startEnablePositionActivity() {
        getContext().startActivity(createEnablePositionActivityIntent());
    }

    @NotNull
    @Contract(" -> new")
    private Intent createEnablePositionActivityIntent() {
        return new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
    }

    public boolean checkPermission(String permission) {
        return ContextCompat.checkSelfPermission(getContext(), permission) != PackageManager.PERMISSION_DENIED;
    }

    public void requestPermission(String permission, int requestCode) {
        homePageFragment.requestPermissions(new String[]{permission}, requestCode);
    }

    private Context getContext() {
        return homePageFragment.getContext();
    }

    @NotNull
    private FragmentActivity requireActivity() {
        return homePageFragment.requireActivity();
    }

    @NotNull
    @Contract(" -> new")
    private GPSTracker createGpsTracker() {
        return new GPSTracker(homePageFragment.getActivity());
    }

    public boolean canGeolocate() {
        return createGpsTracker().canGetLocation();
    }

    public boolean areCoordinatesNull() {
        return gpsTracker.getLatitude() == 0 || gpsTracker.getLongitude() == 0;
    }

    private AttractionDAO getAttractionDAO() {
        return daoFactory.getAttractionDAO(getStorageTechnology(Constants.getAttractionStorageTechnology()));
    }

    private String getStorageTechnology(String storageTechnology) {
        return ConfigFileReader.getProperty(storageTechnology, getContext());
    }

    private RecyclerView getRecyclerViewAttractions() {
        return homePageFragment.getRecyclerViewAttractions();
    }

    private ProgressBar getProgressBarLoadMore() {
        return homePageFragment.getProgressBarAttractionHomeLoadMore();
    }

    private void setRecyclerViewAttractionOnSuccess() {
        getRecyclerViewAttractions().setLayoutManager(linearLayoutManager);
        getRecyclerViewAttractions().setAdapter(recyclerViewAttractionsListAdapter);
    }

    /*private void setShimmerRecyclerViewAttractionOnStart() {
        getShimmerRecyclerViewAttraction().setLayoutManager(linearLayoutManager);
        getShimmerRecyclerViewAttraction().showShimmer();
    }*/

    private Button getButtonProvidePermission() {
        return homePageFragment.getButtonProvidePermission();
    }

    private View getViewNoAttractionsError() {
        return homePageFragment.getViewNoAttractionsError();
    }

    public Button getButtonEnablePosition() {
        return homePageFragment.getButtonEnablePosition();
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

}