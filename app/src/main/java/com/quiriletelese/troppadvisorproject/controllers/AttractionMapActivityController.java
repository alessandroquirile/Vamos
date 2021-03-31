package com.quiriletelese.troppadvisorproject.controllers;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.adapters.BottomSheetFilterAttractions;
import com.quiriletelese.troppadvisorproject.dao_interfaces.AttractionDAO;
import com.quiriletelese.troppadvisorproject.dao_interfaces.CityDAO;
import com.quiriletelese.troppadvisorproject.factories.DAOFactory;
import com.quiriletelese.troppadvisorproject.model_helpers.AttractionFilter;
import com.quiriletelese.troppadvisorproject.model_helpers.Constants;
import com.quiriletelese.troppadvisorproject.model_helpers.PointSearch;
import com.quiriletelese.troppadvisorproject.models.Attraction;
import com.quiriletelese.troppadvisorproject.util_interfaces.AutoCompleteTextViewsAccomodationFilterTextChangeListener;
import com.quiriletelese.troppadvisorproject.util_interfaces.BottomSheetFilterSearchButtonClick;
import com.quiriletelese.troppadvisorproject.utils.ConfigFileReader;
import com.quiriletelese.troppadvisorproject.views.AttractionDetailActivity;
import com.quiriletelese.troppadvisorproject.views.AttractionMapActivity;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBack;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Alessandro Quirile, Mauro Telese
 */

public class AttractionMapActivityController implements GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener,
        View.OnClickListener, BottomSheetFilterSearchButtonClick, AutoCompleteTextViewsAccomodationFilterTextChangeListener {

    private final AttractionMapActivity attractionMapActivity;
    private final BottomSheetFilterAttractions bottomSheetFilterAttractions = new BottomSheetFilterAttractions();
    private AttractionFilter attractionFilter;
    private final DAOFactory daoFactory = DAOFactory.getInstance();
    private Attraction attraction = null;
    private List<Attraction> attractions = new ArrayList<>();
    private ArrayList<Marker> markers = new ArrayList<>();
    private boolean isRelativeLayoutAttractionInformationVisible = false;
    private boolean isLinearLayoutSearchAttractionVisible = true;
    private boolean isFloatingActionButtonCenterPositionOnAttractionsVisible = true;
    private Marker latestMarkerClicked;
    private AlertDialog alertDialog;

    public AttractionMapActivityController(AttractionMapActivity attractionMapActivity) {
        this.attractionMapActivity = attractionMapActivity;
        attractionFilter = getAttractionFilter();
    }

    @Override
    public void onMapClick(LatLng latLng) {
        onMapClickHelper();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        onMarkerClickHelper(marker);
        return true;
    }

    @Override
    public void onClick(View view) {
        onClickHelper(view);
    }

    @Override
    public void onBottomSheetFilterSearchButtonClick() {
        onBottomSheetFilterSearchButtonClickHelper();
    }

    @Override
    public void onAutoCompleteTextViewAccomodationNameTextChanged(String newText) {
        findAttractionsName(newText);
    }

    @Override
    public void onAutoCompleteTextViewAccomodationCityTextChanged(final String newText) {
        findCitiesName(newText);
    }

    private void findByRsqlHelper(VolleyCallBack volleyCallBack, PointSearch pointSearch, String rsqlQuery, boolean canPutPointSearch) {
        getAttractionDAO().findByRsql(volleyCallBack, pointSearch, rsqlQuery, getContext(), 0, 10000, canPutPointSearch);
    }

    private void findByNameLikeIgnoreCaseHelper(VolleyCallBack volleyCallBack, String name) {
        getAttractionDAO().findByNameLikeIgnoreCase(volleyCallBack, name, getContext(), 0, 10000);
    }

    public void findAttractionsNamesHelper(VolleyCallBack volleyCallBack, String name) {
        getAttractionDAO().findAttractionsName(volleyCallBack, name, getContext());
    }

    public void findCitiesNamesHelper(VolleyCallBack volleyCallBack, String name) {
        getCityDAO().findCitiesByName(volleyCallBack, name, getContext());
    }

    private void findByRsql(PointSearch pointSearch, String rsqlQuery, boolean canPutPointSearch) {
        findByRsqlHelper(new VolleyCallBack() {
            @Override
            public void onSuccess(Object object) {
                setMapOnSuccess((List<Attraction>) object);
            }

            @Override
            public void onError(String errorCode) {
                volleyCallbackOnError(errorCode);
            }

        }, pointSearch, rsqlQuery, canPutPointSearch);
    }

    private void findByNameLikeIgnoreCase(String name) {
        findByNameLikeIgnoreCaseHelper(new VolleyCallBack() {
            @Override
            public void onSuccess(Object object) {
                setMapOnSuccess((List<Attraction>) object);
            }

            @Override
            public void onError(String errorCode) {
                volleyCallbackOnError(errorCode);
            }
        }, name);
    }

    private void findAttractionsName(@NotNull String newText) {
        if (!newText.equals("")) {
            disableFieldsOnAutoCompleteTextViewNameChanged();
            findAttractionsNamesHelper(new VolleyCallBack() {
                @Override
                public void onSuccess(Object object) {
                    setAutoCompleteTextViewNameAdapter((List<String>) object);
                }

                @Override
                public void onError(String errorCode) {

                }
            }, newText);
        } else
            enableFieldsOnAutoCompleteTextViewNameChanged();
    }

    private void findCitiesName(@NotNull String newText) {
        if (!newText.equals("")) {
            disableFieldsOnAutoCompleteTextViewCityChanged();
            findCitiesNamesHelper(new VolleyCallBack() {
                @Override
                public void onSuccess(Object object) {
                    setAutoCompleteTextViewCityAdapter((List<String>) object);
                }

                @Override
                public void onError(String errorCode) {

                }
            }, newText);
        } else
            enableFieldsOnAutoCompleteTextViewCityChanged();
    }

    private void setMapOnSuccess(List<Attraction> attractions) {
        if (alertDialog != null)
            alertDialog.dismiss();
        this.attractions = attractions;
        dismissBottomSheetFilterRestaurants();
        addMarkersOnSuccess(attractions);
        zoomOnMap();
    }

    private void onBottomSheetFilterSearchButtonClickHelper() {
        showWaitSearchResultDialog();
        createAttractionFilter();
        detectSearchType();
    }

    private void showWaitSearchResultDialog() {
        AlertDialog.Builder alertDialogBuilder = createAlertDialogBuilder();
        alertDialogBuilder.setView(getLayoutInflater().inflate(getAlertDialogLayout(), null));
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @NotNull
    @Contract(" -> new")
    private AlertDialog.Builder createAlertDialogBuilder() {
        return new AlertDialog.Builder(attractionMapActivity);
    }

    @NotNull
    private LayoutInflater getLayoutInflater() {
        return attractionMapActivity.getLayoutInflater();
    }

    private int getAlertDialogLayout() {
        return R.layout.dialog_wait_search_result;
    }

    private void onClickHelper(@NotNull View view) {
        switch (view.getId()) {
            case R.id.relative_layout_attraction_details:
                startDetailActivity();
                break;
            case R.id.text_view_search_attractions_on_map:
                showBottomSheetMapFilters();
                break;
            case R.id.image_view_attraction_map_go_back:
                onBackPressed();
                break;
            case R.id.floating_action_button_center_position_on_attractions:
                zoomOnMap();
                break;
        }
    }

    private void disableFieldsOnAutoCompleteTextViewNameChanged() {
        bottomSheetFilterAttractions.setAutoCompleteTextViewCityText("");
        bottomSheetFilterAttractions.setSeekBarPriceEnabled(false);
        bottomSheetFilterAttractions.setSeekBarRatingEnabled(false);
        bottomSheetFilterAttractions.setSeekBarDistanceEnabled(false);
        bottomSheetFilterAttractions.setSwitchCompatCertificateOfExcellenceEnabled(false);
        bottomSheetFilterAttractions.setSwitchCompatAcceptFreeAccesEnabled(false);
    }

    private void enableFieldsOnAutoCompleteTextViewNameChanged() {
        bottomSheetFilterAttractions.setSeekBarPriceEnabled(true);
        bottomSheetFilterAttractions.setSeekBarRatingEnabled(true);
        bottomSheetFilterAttractions.setSeekBarDistanceEnabled(true);
        bottomSheetFilterAttractions.setSwitchCompatCertificateOfExcellenceEnabled(true);
        bottomSheetFilterAttractions.setSwitchCompatAcceptFreeAccesEnabled(true);
    }

    private void disableFieldsOnAutoCompleteTextViewCityChanged() {
        bottomSheetFilterAttractions.setAutoCompleteTextViewNameText("");
        bottomSheetFilterAttractions.setSeekBarDistanceEnabled(false);
    }

    private void enableFieldsOnAutoCompleteTextViewCityChanged() {
        bottomSheetFilterAttractions.setSeekBarDistanceEnabled(true);
    }

    private void setBottomSheetFiltersFields() {
        if (!isAttractionFilterNull())
            new Handler().postDelayed(this::setFields, 100);
    }

    private void setFields() {
        bottomSheetFilterAttractions.setAutoCompleteTextViewNameText(getAttractionFilterNameValue());
        bottomSheetFilterAttractions.setAutoCompleteTextViewCityText(getAttractionFilterCityValue());
        bottomSheetFilterAttractions.setSeekBarPriceProgress(getAttractionFilterAvaragePriceValue());
        bottomSheetFilterAttractions.setSeekBarRatingProgress(getAttractionFilterAvarageRatingValue());
        bottomSheetFilterAttractions.setSeekBarDistanceProgress(getAttractionFilterDistanceValue().intValue());
        if (isSearchingForCity())
            bottomSheetFilterAttractions.setSeekBarDistanceEnabled(false);
        bottomSheetFilterAttractions.setSwitchCompatCertificateOfExcellenceChecked(isAttractionFilterHasCertificateOfExcellence());
        bottomSheetFilterAttractions.setSwitchCompatAcceptFreeAccesChecked(isAttractionFilterHasAcceptFreeAccess());
    }

    private void volleyCallbackOnError(@NotNull String errorCode) {
        if (alertDialog != null)
            alertDialog.dismiss();
        switch (errorCode) {
            case "204":
                handle204VolleyError();
                break;
            default:
                handleOtherVolleyError();
                break;
        }
    }

    private void handle204VolleyError() {
        showToastOnUiThread(R.string.no_attractions_found_by_filter);
    }

    private void handleOtherVolleyError() {
        showToastOnUiThread(R.string.unexpected_error_while_fetch_data);
    }

    private void showToastOnUiThread(int stringId) {
        attractionMapActivity.runOnUiThread(() ->
                Toast.makeText(attractionMapActivity, getString(stringId), Toast.LENGTH_SHORT).show());
    }

    private void createAttractionFilter() {
        attractionFilter = new AttractionFilter();
        attractionFilter.setName(getAttractionNameValueFromBottomSheetFilter());
        attractionFilter.setCity(getCityNameValueFromBottomSheetFilter());
        attractionFilter.setAvaragePrice(getPriceValueFromBottomSheetFilter());
        attractionFilter.setAvarageRating(getRatingValueFromBottomSheetFilter());
        attractionFilter.setDistance(getDistanceValueFromBottomSheetFilter());
        attractionFilter.setHasCertificateOfExcellence(getCertificateOfExcellenceFromBottomSheetFilter());
        attractionFilter.setHasFreeAccess(getFreeAccessFromBottomSheetFilter());
    }

    private void detectSearchType() {
        if (!isSearchingForName())
            findByRsql(createPointSearch(), !createRsqlString().equals("") ? createRsqlString() : "0", !isSearchingForCity());
//            findByRsql(isSearchingForCity() ? null : createPointSearch(),
//                    !createRsqlString().equals("") ? createRsqlString() : "0");
        else
            findByNameLikeIgnoreCase(getAttractionNameValueFromBottomSheetFilter());
    }

    private String getAttractionNameValueFromBottomSheetFilter() {
        return bottomSheetFilterAttractions.getAutoCompleteTextViewNameValue();
    }

    private String getCityNameValueFromBottomSheetFilter() {
        return extractCityName(bottomSheetFilterAttractions.getAutoCompleteTextViewCityValue());
    }

    @NotNull
    private Integer getPriceValueFromBottomSheetFilter() {
        return bottomSheetFilterAttractions.getSeekBarPriceValue();
    }

    @NotNull
    private Integer getRatingValueFromBottomSheetFilter() {
        return bottomSheetFilterAttractions.getSeekBarRatingValue();
    }

    @NotNull
    private Double getDistanceValueFromBottomSheetFilter() {
        if (bottomSheetFilterAttractions.getSeekBarDistanceValue() != 0)
            return (double) bottomSheetFilterAttractions.getSeekBarDistanceValue();
        else
            return 1d;
    }

    private boolean getCertificateOfExcellenceFromBottomSheetFilter() {
        return bottomSheetFilterAttractions.getSwitchCompatCertificateOfExcellenceIsSelected();
    }

    private boolean getFreeAccessFromBottomSheetFilter() {
        return bottomSheetFilterAttractions.getSwitchCompatAcceptFreeAccesIsSelecteds();
    }

    private String checkCityNameValue(String rsqlString) {
        if (isSearchingForCity()) {
            String cityName = extractCityName(getAttractionFilterCityValue());
            rsqlString = rsqlString.concat("address.city==\"" + cityName + "\";");
        }
        return rsqlString;
    }

    private String extractCityName(@NotNull String city) {
        return city.contains(",") ? city.substring(0, city.lastIndexOf(",")) : city.trim();
    }

    private String checkPriceValue(String rsqlString) {
        if (!isAttractionFilterAvaragePriceEqualsToZero()) {
            if (isAttractionFilterAvaragePriceGreaterEqualsThan150())
                rsqlString = rsqlString.concat("maxPrice=ge=" + getAttractionFilterAvaragePriceValue() + ";");
            else
                rsqlString = rsqlString.concat("maxPrice=le=" + getAttractionFilterAvaragePriceValue() + ";");
        }
        return rsqlString;
    }

    private String checkRatingValue(String rsqlString) {
        if (!isAttractionFilterAvarageRatingEqualsToZero())
            rsqlString = rsqlString.concat("avarageRating=ge=" + getAttractionFilterAvarageRatingValue() + ";");
        return rsqlString;
    }

    private Double checkDistanceValue() {
        double distance = 1.0;
        if (!isAttractionFilterDistanceEqualsToZero())
            distance = getAttractionFilterDistanceValue();
        return distance;
    }

    private String checkCertificateOfExcellence(String rsqlString) {
        if (isAttractionFilterHasCertificateOfExcellence())
            rsqlString = rsqlString.concat("certificateOfExcellence==" + "true;");
        return rsqlString;
    }

    private String checkAcceptFreeAccess(String rsqlString) {
        if (isAttractionFilterHasAcceptFreeAccess())
            rsqlString = rsqlString.concat("freeAccessPrice=gt=0;");
        return rsqlString;
    }

    @NotNull
    private String createRsqlString() {
        String rsqlString = "";
        rsqlString = checkCityNameValue(rsqlString);
        rsqlString = checkPriceValue(rsqlString);
        rsqlString = checkRatingValue(rsqlString);
        rsqlString = checkCertificateOfExcellence(rsqlString);
        rsqlString = checkAcceptFreeAccess(rsqlString);
        if (!rsqlString.equals(""))
            rsqlString = rsqlString.substring(0, rsqlString.lastIndexOf(";"));
        Log.d("RSQL-STRING MAP", rsqlString);
        return rsqlString;
    }

    @NotNull
    private PointSearch createPointSearch() {
        PointSearch pointSearch = getPointSearch();
        pointSearch.setDistance(isAttractionFilterNull() ? 5d : checkDistanceValue());
        return pointSearch;
    }

    private void setAutoCompleteTextViewNameAdapter(List<String> hotelsName) {
        getAutoCompleteTextViewName().setAdapter(createAutoCompleteTextViewAdapter(hotelsName));
    }

    private void setAutoCompleteTextViewCityAdapter(List<String> citiesName) {
        getAutoCompleteTextViewCity().setAdapter(createAutoCompleteTextViewAdapter(citiesName));
    }

    @NotNull
    @Contract("_ -> new")
    private ArrayAdapter<String> createAutoCompleteTextViewAdapter(List<String> values) {
        return new ArrayAdapter<>(getContext(), getArrayAdapterLayout(), values);
    }

    public void addMarkersOnMap() {
        if (!isAttractionFilterNull()) {
            if (isSearchingForName())
                findByNameLikeIgnoreCase(getAttractionName());
            else if (isSearchingForCity())
                findByRsql(null, getRsqlQuery(), false);
            else
                findByRsql(getPointSearch(), getRsqlQuery(), true);
        } else
            findByRsql(getPointSearch(), getRsqlQuery(), true);
    }

    private void addMarkersOnSuccess(@NotNull List<Attraction> attractions) {
        clearAllMarkerOnMap();
        for (Attraction attraction : attractions)
            markers.add(addMarker(attraction));
    }

    private Marker addMarker(Attraction attraction) {
        return getGoogleMap().addMarker(createMarkerOptions(attraction));
    }

    private void clearAllMarkerOnMap() {
        getGoogleMap().clear();
        markers = new ArrayList<>();
    }

    @NotNull
    private MarkerOptions createMarkerOptions(@NotNull Attraction attraction) {
        return new MarkerOptions()
                .position(new LatLng(attraction.getLatitude(), attraction.getLongitude()))
                .icon(setCustomMarker(getContext(), getAttractionMarker()))
                .title(attraction.getId());
    }

    @NotNull
    private BitmapDescriptor setCustomMarker(Context context, int id) {
        Drawable background = ContextCompat.getDrawable(context, id);
        Objects.requireNonNull(background).setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void zoomOnMap() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markers)
            builder.include(marker.getPosition());
        LatLngBounds bounds = builder.build();
        int padding = 200;
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        getGoogleMap().animateCamera(cameraUpdate);
    }

    @SuppressLint("MissingPermission")
    public void setMapProperties() {
        getGoogleMap().getUiSettings().setMyLocationButtonEnabled(false);
        getGoogleMap().getUiSettings().setMapToolbarEnabled(false);
        getGoogleMap().setMyLocationEnabled(true);
        getGoogleMap().setOnMarkerClickListener(this);
        getGoogleMap().setOnMapClickListener(this);
    }

    private void onMapClickHelper() {
        if (attraction != null) {
            if (isRelativeLayoutAttractionInformationVisible) {
                setRelativeLayoutDetailsInvisible();
                latestMarkerClicked.setIcon(setCustomMarker(getContext(), getAttractionMarker()));
            } else
                setRelativeLayoutDetailsVisible();
        }
        if (isLinearLayoutSearchAttractionVisible)
            setLinearLayoutSearchAttractionsInvisible();
        else
            setLinearLayoutSearchAttractionsVisible();
        if (isFloatingActionButtonCenterPositionOnAttractionsVisible)
            setFloatingActionButtonCenterPositionOnAttractionsInvisible();
        else
            setFloatingActionButtonCenterPositionOnAttractionsVisible();
    }

    private void onMarkerClickHelper(Marker marker) {
        latestMarkerClicked = marker;
        setMarkerClicked(marker.getId());
        if (!isRelativeLayoutAttractionInformationVisible)
            setRelativeLayoutDetailsVisible();
        if (!isLinearLayoutSearchAttractionVisible)
            setLinearLayoutSearchAttractionsVisible();
        if (!isFloatingActionButtonCenterPositionOnAttractionsVisible)
            setFloatingActionButtonCenterPositionOnAttractionsVisible();
        setRelativeLayoutDetailsFields(marker);
    }

    private void setMarkerClicked(String id) {
        for (Marker marker : markers) {
            if (marker.getId().equals(id))
                marker.setIcon(setCustomMarker(getContext(), getAttractionMarkerClicked()));
            else
                marker.setIcon(setCustomMarker(getContext(), getAttractionMarker()));
        }
    }

    private void setRelativeLayoutDetailsVisible() {
        isRelativeLayoutAttractionInformationVisible = true;
        getRelativeLayoutDetails().setVisibility(View.VISIBLE);
        getRelativeLayoutDetails().animate().translationY(0);
    }

    private void setRelativeLayoutDetailsInvisible() {
        isRelativeLayoutAttractionInformationVisible = false;
        getRelativeLayoutDetails().animate().translationY(getRelativeLayoutDetails().getHeight() + 100);
    }

    private void setRelativeLayoutDetailsFields(@NotNull Marker marker) {
        attraction = getAttractionFromMarkerClick(marker.getTitle());
        setImage(attraction);
        getTextViewName().setText(attraction.getName());
        getTextViewRating().setText(createAvarageRatingString(attraction));
        getTextViewAddress().setText(createAddressString(attraction));
        marker.setTitle(getTextViewName().getText().toString());
        marker.showInfoWindow();
        marker.setTitle(attraction.getId());
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setImage(Attraction attraction) {
        if (hasImage(attraction))
            Picasso.with(getContext())
                    .load(attraction.getImages().get(0))
                    .placeholder(R.drawable.app_icon_no_background)
                    .error(R.drawable.picasso_error)
                    .into(getImageViewAttraction());
        else
            getImageViewAttraction().setImageDrawable(getResources().getDrawable(R.drawable.picasso_error));
    }

    private boolean hasImage(@NotNull Attraction attraction) {
        return attraction.hasImage();
    }

    @NotNull
    private String createAddressString(@NotNull Attraction attraction) {
        String attractionAddress = "";
        attractionAddress = attractionAddress.concat(attraction.getTypeOfAddress() + " ");
        attractionAddress = attractionAddress.concat(attraction.getStreet() + ", ");
        if (!attraction.getHouseNumber().isEmpty())
            attractionAddress = attractionAddress.concat(attraction.getHouseNumber() + ", ");
        attractionAddress = attractionAddress.concat(attraction.getCity() + ", ");
        if (!attraction.getProvince().equals(attraction.getCity()))
            attractionAddress = attractionAddress.concat(attraction.getProvince() + ", ");
        attractionAddress = attractionAddress.concat(attraction.getPostalCode());
        return attractionAddress;
    }

    private String createAvarageRatingString(Attraction attraction) {
        return !hasReviews(attraction) ? getString(R.string.no_reviews) : createAvarageRatingStringHelper(attraction);
    }

    @NotNull
    private String createAvarageRatingStringHelper(@NotNull Attraction attraction) {
        if (attraction.getTotalReviews() == 1)
            return attraction.getAvarageRating().intValue() + "/5 (" + attraction.getTotalReviews() + " " + getString(R.string.review) + ")";
        else
            return attraction.getAvarageRating().intValue() + "/5 (" + attraction.getTotalReviews() + " " + getString(R.string.reviews) + ")";
    }

    private boolean hasReviews(@NotNull Attraction attraction) {
        return attraction.hasReviews();
    }

    private Attraction getAttractionFromMarkerClick(String attractionId) {
        Attraction attractionToReturn = null;
        for (Attraction attraction : attractions)
            if (attraction.getId().equals(attractionId)) {
                attractionToReturn = attraction;
                break;
            }
        return attractionToReturn;
    }

    private void setLinearLayoutSearchAttractionsVisible() {
        isLinearLayoutSearchAttractionVisible = true;
        getLinearLayoutSearchAttractions().animate().translationY(0);
    }

    private void setLinearLayoutSearchAttractionsInvisible() {
        isLinearLayoutSearchAttractionVisible = false;
        getLinearLayoutSearchAttractions().animate().translationY(-getLinearLayoutSearchAttractions()
                .getHeight() - 100);
    }

    private void setFloatingActionButtonCenterPositionOnAttractionsVisible() {
        isFloatingActionButtonCenterPositionOnAttractionsVisible = true;
        getFloatingActionButtonCenterPositionOnAttractions().show();
    }

    private void setFloatingActionButtonCenterPositionOnAttractionsInvisible() {
        isFloatingActionButtonCenterPositionOnAttractionsVisible = false;
        getFloatingActionButtonCenterPositionOnAttractions().hide();
    }

    private void showBottomSheetMapFilters() {
        bottomSheetFilterAttractions.show(getSupportFragmentManager(), getTag());
        setBottomSheetFiltersFields();
        bottomSheetFilterAttractions.setBottomSheetFilterSearchButtonClick(this);
        bottomSheetFilterAttractions.setAutoCompleteTextViewsAccomodationFilterTextChangeListener(this);
    }

    public void setComponentProperties() {
        getRelativeLayoutDetails().animate().translationY(getRelativeLayoutDetails().getHeight() + 100);
    }

    public void setListenerOnViewComponents() {
        getRelativeLayoutDetails().setOnClickListener(this);
        getTextViewSearchOnMap().setOnClickListener(this);
        getImageViewMapGoBack().setOnClickListener(this);
        getFloatingActionButtonCenterPositionOnAttractions().setOnClickListener(this);
    }

    private void startDetailActivity() {
        Intent detailActivityIntent = new Intent(getContext(), AttractionDetailActivity.class);
        detailActivityIntent.putExtra(Constants.getId(), attraction.getId());
        detailActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(detailActivityIntent);
    }

    private AttractionDAO getAttractionDAO() {
        return daoFactory.getAttractionDAO(getStorageTechnology(Constants.getAttractionStorageTechnology()));
    }

    private CityDAO getCityDAO() {
        return daoFactory.getCityDAO(getStorageTechnology(Constants.getCityStorageTechnology()));
    }

    private String getStorageTechnology(String storageTechnology) {
        return ConfigFileReader.getProperty(storageTechnology, getContext());
    }

    private void dismissBottomSheetFilterRestaurants() {
        if (isBottomSheetFilterAttractionsVisible())
            bottomSheetFilterAttractions.dismiss();
    }

    private void onBackPressed() {
        attractionMapActivity.onBackPressed();
    }

    @NotNull
    private FragmentManager getSupportFragmentManager() {
        return attractionMapActivity.getSupportFragmentManager();
    }

    private String getTag() {
        return bottomSheetFilterAttractions.getTag();
    }

    private Context getContext() {
        return attractionMapActivity.getApplicationContext();
    }

    private Resources getResources() {
        return attractionMapActivity.getResources();
    }

    @NotNull
    private String getString(int string) {
        return getResources().getString(string);
    }

    private Intent getIntent() {
        return attractionMapActivity.getIntent();
    }

    public PointSearch getPointSearch() {
        return (PointSearch) getIntent().getSerializableExtra(Constants.getPointSearch());
    }

    private String getRsqlQuery() {
        return getIntent().getStringExtra(Constants.getRsqlQuery());
    }

    private AttractionFilter getAttractionFilter() {
        return (AttractionFilter) getIntent().getSerializableExtra(Constants.getAccomodationFilter());
    }

    private AutoCompleteTextView getAutoCompleteTextViewName() {
        return bottomSheetFilterAttractions.getAutoCompleteTextViewName();
    }

    private AutoCompleteTextView getAutoCompleteTextViewCity() {
        return bottomSheetFilterAttractions.getAutoCompleteTextViewCity();
    }

    private int getArrayAdapterLayout() {
        return android.R.layout.select_dialog_item;
    }

    private GoogleMap getGoogleMap() {
        return attractionMapActivity.getGoogleMap();
    }

    private int getAttractionMarker() {
        return R.drawable.attraction_marker;
    }

    private int getAttractionMarkerClicked() {
        return R.drawable.attraction_marker_clicked;
    }

    private String getAttractionName() {
        return getIntent().getStringExtra(Constants.getName());
    }

    private boolean isAttractionFilterNull() {
        return attractionFilter == null;
    }

    private String getAttractionFilterNameValue() {
        return attractionFilter.getName();
    }

    private String getAttractionFilterCityValue() {
        return attractionFilter.getCity();
    }

    private Integer getAttractionFilterAvaragePriceValue() {
        return attractionFilter.getAvaragePrice();
    }

    private Integer getAttractionFilterAvarageRatingValue() {
        return attractionFilter.getAvarageRating();
    }

    private Double getAttractionFilterDistanceValue() {
        return attractionFilter.getDistance();
    }

    private boolean getAttractionFilterHasCertificateOfExcellenceValue() {
        return attractionFilter.isHasCertificateOfExcellence();
    }

    private boolean getAttractionFilterHasFreeAccessValue() {
        return attractionFilter.isHasFreeAccess();
    }

    private TextView getTextViewSearchOnMap() {
        return attractionMapActivity.getTextViewSearchOnMap();
    }

    private ImageView getImageViewMapGoBack() {
        return attractionMapActivity.getImageViewMapGoBack();
    }

    private RelativeLayout getRelativeLayoutDetails() {
        return attractionMapActivity.getRelativeLayoutDetails();
    }

    private TextView getTextViewName() {
        return attractionMapActivity.getTextViewName();
    }

    private TextView getTextViewRating() {
        return attractionMapActivity.getTextViewRating();
    }

    private TextView getTextViewAddress() {
        return attractionMapActivity.getTextViewAddress();
    }

    private ImageView getImageViewAttraction() {
        return attractionMapActivity.getImageViewAttraction();
    }

    private LinearLayout getLinearLayoutSearchAttractions() {
        return attractionMapActivity.getLinearLayoutSearchAttractions();
    }

    private FloatingActionButton getFloatingActionButtonCenterPositionOnAttractions() {
        return attractionMapActivity.getFloatingActionButtonCenterPositionOnAttractions();
    }

    private boolean isDistanceSeekbarEnabled() {
        return bottomSheetFilterAttractions.getSeekBarDistance().isEnabled();
    }

    private boolean isDistanceDifferentFromZero() {
        return bottomSheetFilterAttractions.getSeekBarDistanceValue() != 0;
    }

    private boolean isSearchingForName() {
        return !getAttractionFilterNameValue().equals("");
    }

    private boolean isSearchingForCity() {
        return !getAttractionFilterCityValue().equals("");
    }

    private boolean isAttractionFilterAvaragePriceEqualsToZero() {
        return getAttractionFilterAvaragePriceValue().equals(0);
    }

    private boolean isAttractionFilterAvaragePriceGreaterEqualsThan150() {
        return getAttractionFilterAvaragePriceValue() >= 150;
    }

    private boolean isAttractionFilterAvarageRatingEqualsToZero() {
        return getAttractionFilterAvarageRatingValue().equals(0);
    }

    private boolean isAttractionFilterDistanceEqualsToZero() {
        return getAttractionFilterDistanceValue().equals(0d);
    }

    private boolean isAttractionFilterHasCertificateOfExcellence() {
        return getAttractionFilterHasCertificateOfExcellenceValue();
    }

    private boolean isAttractionFilterHasAcceptFreeAccess() {
        return getAttractionFilterHasFreeAccessValue();
    }

    private boolean isBottomSheetFilterAttractionsVisible() {
        return bottomSheetFilterAttractions.isAdded();
    }

}
