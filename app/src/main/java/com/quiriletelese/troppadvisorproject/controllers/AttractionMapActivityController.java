package com.quiriletelese.troppadvisorproject.controllers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

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
import com.quiriletelese.troppadvisorproject.interfaces.AutoCompleteTextViewsAccomodationFilterTextChangeListener;
import com.quiriletelese.troppadvisorproject.interfaces.Constants;
import com.quiriletelese.troppadvisorproject.interfaces.OnBottomSheetFilterSearchButtonClick;
import com.quiriletelese.troppadvisorproject.model_helpers.AccomodationAttractionFilter;
import com.quiriletelese.troppadvisorproject.model_helpers.Address;
import com.quiriletelese.troppadvisorproject.model_helpers.PointSearch;
import com.quiriletelese.troppadvisorproject.models.Attraction;
import com.quiriletelese.troppadvisorproject.utils.ConfigFileReader;
import com.quiriletelese.troppadvisorproject.views.AttractionMapActivity;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBack;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBackCity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AttractionMapActivityController implements GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener,
        View.OnClickListener, OnBottomSheetFilterSearchButtonClick,
        AutoCompleteTextViewsAccomodationFilterTextChangeListener, Constants {

    private AttractionMapActivity attractionMapActivity;
    private BottomSheetFilterAttractions bottomSheetFilterAttractions = new BottomSheetFilterAttractions();
    private AccomodationAttractionFilter accomodationAttractionFilter;
    private DAOFactory daoFactory = DAOFactory.getInstance();
    private Attraction attraction = null;
    private List<Attraction> attractions = new ArrayList<>();
    private ArrayList<Marker> markers = new ArrayList<>();
    boolean isRelativeLayoutAttractionInformationVisible = false, isLinearLayoutSearchAttractionVisible = true,
            isFloatingActionButtonCenterPositionOnAttractionsVisible = true;

    public AttractionMapActivityController(AttractionMapActivity attractionMapActivity) {
        this.attractionMapActivity = attractionMapActivity;
        accomodationAttractionFilter = getAccomodationFilter();
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
        switch (view.getId()) {
            case R.id.text_view_search_attractions_on_map:
                showBottomSheetMapFilters();
                break;
            case R.id.image_view_attraction_map_go_back:
                onBackPressed();
                break;
            case R.id.floating_action_button_center_position_on_hotels:
                zoomOnMap();
                break;
        }
    }

    @Override
    public void OnBottomSheetFilterSearchButtonClick() {
        onBottomSheetFilterSearchButtonClickHelper();
    }

    @Override
    public void onAutoCompleteTextViewAccomodationNameTextChanged(String newText) {
        findAttractionsName(newText);
    }

    @Override
    public void onAutoCompleteTextViewAccomodtionCityTextChanged(final String newText) {
        findCitiesName(newText);
    }

    private void findByRsqlHelper(VolleyCallBack volleyCallBack, PointSearch pointSearch, String rsqlQuery) {
        getAttractionDAO().findByRsql(volleyCallBack, pointSearch, rsqlQuery, getContext(), 0, 10000);
    }

    private void findByNameLikeIgnoreCaseHelper(VolleyCallBack volleyCallBack, String name) {
        getAttractionDAO().findByNameLikeIgnoreCase(volleyCallBack, name, getContext(), 0, 10000);
    }

    public void findAttractionsNameHelper(VolleyCallBack volleyCallBack, String name) {
        getAttractionDAO().findHotelsName(volleyCallBack, name, getContext());
    }

    public void findCitiesNameHelper(VolleyCallBackCity volleyCallBackCity, String name) {
        getCityDAO().findCitiesByName(volleyCallBackCity, name, getContext());
    }

    private void findByRsql(PointSearch pointSearch, String rsqlQuery) {
        findByRsqlHelper(new VolleyCallBack() {
            @Override
            public void onSuccess(Object object) {
                setMapOnSuccess((List<Attraction>) object);
            }

            @Override
            public void onError(String errorCode) {
                volleyCallbackOnError(errorCode);
            }

        }, pointSearch, rsqlQuery);
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

    private void findAttractionsName(String newText) {
        if (!newText.equals("")) {
            disableFieldsOnAutoCompleteTextViewNameChanged();
            findAttractionsNameHelper(new VolleyCallBack() {
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

    private void findCitiesName(String newText) {
        if (!newText.equals("")) {
            disableFieldsOnAutoCompleteTextViewCityChanged();
            findCitiesNameHelper(new VolleyCallBackCity() {
                @Override
                public void onSuccess(Object object) {
                    setAutoCompleteTextViewCityAdapter((List<String>) object);
                }

                @Override
                public void onError(String error) {

                }
            }, newText);
        } else
            enableFieldsOnAutoCompleteTextViewCityChanged();
    }

    private void setMapOnSuccess(List<Attraction> attractions) {
        this.attractions = attractions;
        dismissBottomSheetFilterRestaurants();
        addMarkersOnSuccess(attractions);
        zoomOnMap();
    }

    private void onBottomSheetFilterSearchButtonClickHelper() {
        createAccomodationFilter();
        detectSearchType();
    }

    private void disableFieldsOnAutoCompleteTextViewNameChanged() {
        bottomSheetFilterAttractions.setAutoCompleteTextViewCityText("");
        bottomSheetFilterAttractions.setSeekBarPriceEnabled(false);
        bottomSheetFilterAttractions.setSeekBarRatingEnabled(false);
        bottomSheetFilterAttractions.setSeekBarDistanceEnabled(false);
        bottomSheetFilterAttractions.setSwitchCompatCertificateOfExcellenceEnabled(false);
    }

    private void enableFieldsOnAutoCompleteTextViewNameChanged() {
        bottomSheetFilterAttractions.setSeekBarPriceEnabled(true);
        bottomSheetFilterAttractions.setSeekBarRatingEnabled(true);
        bottomSheetFilterAttractions.setSeekBarDistanceEnabled(true);
        bottomSheetFilterAttractions.setSwitchCompatCertificateOfExcellenceEnabled(true);
    }

    private void disableFieldsOnAutoCompleteTextViewCityChanged() {
        bottomSheetFilterAttractions.setAutoCompleteTextViewNameText("");
        bottomSheetFilterAttractions.setSeekBarDistanceEnabled(false);
    }

    private void enableFieldsOnAutoCompleteTextViewCityChanged() {
        bottomSheetFilterAttractions.setSeekBarDistanceEnabled(true);
    }

    private void setBottomSheetFiltersFields() {
        if (!isBottomSheetFilterHotelsNull() && !isAccomodationFilterNull())
            new Handler().postDelayed(this::setFields, 100);
    }

    private void setFields() {
        bottomSheetFilterAttractions.setAutoCompleteTextViewNameText(getAccomodationFilterNameValue());
        bottomSheetFilterAttractions.setAutoCompleteTextViewCityText(getAccomodationFilterCityValue());
        bottomSheetFilterAttractions.setSeekBarPriceProgress(getAccomodationFilterAvaragePriceValue());
        bottomSheetFilterAttractions.setSeekBarRatingProgress(getAccomodationFilterAvarageRatingValue());
        bottomSheetFilterAttractions.setSeekBarDistanceProgress(getAccomodationFilterDistanceValue().intValue());
        if (isSearchingForCity())
            bottomSheetFilterAttractions.setSeekBarDistanceEnabled(false);
        bottomSheetFilterAttractions.setSwitchCompatCertificateOfExcellenceChecked(isAccomodationFilterHasCertificateOfExcellence());
    }

    private void volleyCallbackOnError(String errorCode) {
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
        showToastVolleyError(R.string.no_attractions_found_by_filter);
    }

    private void handleOtherVolleyError() {
        showToastVolleyError(R.string.unexpected_error_while_fetch_data);
    }

    private void showToastVolleyError(int string) {
        showToastOnUiThred(string);
    }

    private void showToastOnUiThred(int string) {
        attractionMapActivity.runOnUiThread(() -> {
            Toast.makeText(attractionMapActivity, getString(string), Toast.LENGTH_SHORT).show();
        });
    }

    private void createAccomodationFilter() {
        accomodationAttractionFilter = new AccomodationAttractionFilter();
        accomodationAttractionFilter.setName(getAttractionNameValueFromBottomSheetFilter());
        accomodationAttractionFilter.setCity(getCityNameValueFromBottomSheetFilter());
        accomodationAttractionFilter.setAvaragePrice(getPriceValueFromBottomSheetFilter());
        accomodationAttractionFilter.setAvarageRating(getRatingValueFromBottomSheetFilter());
        accomodationAttractionFilter.setDistance(getDistanceValueFromBottomSheetFilter());
        accomodationAttractionFilter.setHasCertificateOfExcellence(getCertificateOfExcellenceFromBottomSheetFilter());
    }

    private void detectSearchType() {
        if (!isSearchingForName())
            findByRsql(isSearchingForCity() ? null : createPointSearch(),
                    !createRsqlString().equals("") ? createRsqlString() : "0");
        else
            findByNameLikeIgnoreCase(getAttractionNameValueFromBottomSheetFilter());
    }

    private String getAttractionNameValueFromBottomSheetFilter() {
        return bottomSheetFilterAttractions.getAutoCompleteTextViewNameValue();
    }

    private String getCityNameValueFromBottomSheetFilter() {
        return extractCityName(bottomSheetFilterAttractions.getAutoCompleteTextViewCityValue());
    }

    private Integer getPriceValueFromBottomSheetFilter() {
        return bottomSheetFilterAttractions.getSeekBarPriceValue();
    }

    private Integer getRatingValueFromBottomSheetFilter() {
        return bottomSheetFilterAttractions.getSeekBarRatingValue();
    }

    private Double getDistanceValueFromBottomSheetFilter() {
        return isDistanceSeekbarEnabled() && isDistanceDifferentFromZero() ?
                (double) bottomSheetFilterAttractions.getSeekBarDistanceValue() : 1d;
    }

    private boolean getCertificateOfExcellenceFromBottomSheetFilter() {
        return bottomSheetFilterAttractions.getSwitchCompatCertificateOfExcellenceIsSelected();
    }

    private String checkCityNameValue(String rsqlString) {
        if (isSearchingForCity()) {
            String cityName = extractCityName(getAccomodationFilterCityValue());
            rsqlString = rsqlString.concat("address.city==" + cityName + ";");
        }
        return rsqlString;
    }

    private String extractCityName(String city) {
        return city.contains(",") ? city.substring(0, city.lastIndexOf(",")) : city;
    }

    private String checkPriceValue(String rsqlString) {
        if (!isAccomodationFilterAvaragePriceEqualsToZero()) {
            if (isAccomodationFilterAvaragePriceGreaterEqualsThan150())
                rsqlString = rsqlString.concat("avaragePrice=ge=" + getAccomodationFilterAvaragePriceValue() + ";");
            else
                rsqlString = rsqlString.concat("avaragePrice=le=" + getAccomodationFilterAvaragePriceValue() + ";");
        }
        return rsqlString;
    }

    private String checkRatingValue(String rsqlString) {
        if (!isAccomodationFilterAvarageRatingEqualsToZero())
            rsqlString = rsqlString.concat("avarageRating=ge=" + getAccomodationFilterAvarageRatingValue() + ";");
        return rsqlString;
    }

    private Double checkDistanceValue() {
        double distance = 1.0;
        if (!isAccomodationFilterDistanceEqualsToZero())
            distance = getAccomodationFilterDistanceValue();
        return distance;
    }

    private String checkCertificateOfExcellence(String rsqlString) {
        if (isAccomodationFilterHasCertificateOfExcellence())
            rsqlString = rsqlString.concat("certificateOfExcellence==" + "true;");
        return rsqlString;
    }

    private String createRsqlString() {
        String rsqlString = "";
        rsqlString = checkCityNameValue(rsqlString);
        rsqlString = checkPriceValue(rsqlString);
        rsqlString = checkRatingValue(rsqlString);
        rsqlString = checkCertificateOfExcellence(rsqlString);
        if (!rsqlString.equals(""))
            rsqlString = rsqlString.substring(0, rsqlString.lastIndexOf(";"));
        Log.d("RSQL-STRING", rsqlString);
        return rsqlString;
    }

    private PointSearch createPointSearch() {
        PointSearch pointSearch = getPointSearch();
        pointSearch.setDistance(isAccomodationFilterNull() ? 5d : checkDistanceValue());
        return pointSearch;
    }

    private void setAutoCompleteTextViewNameAdapter(List<String> hotelsName) {
        getAutoCompleteTextViewName().setAdapter(createAutoCompleteTextViewAdapter(hotelsName));
    }

    private void setAutoCompleteTextViewCityAdapter(List<String> citiesName) {
        getAutoCompleteTextViewCity().setAdapter(createAutoCompleteTextViewAdapter(citiesName));
    }

    private ArrayAdapter<String> createAutoCompleteTextViewAdapter(List<String> values) {
        return new ArrayAdapter<>(getContext(), getArrayAdapterLayout(), values);
    }

    public void addMarkersOnMap() {
        if (isIntentSearchingForName())
            findByNameLikeIgnoreCase(getAttractionName());
        else
            findByRsql(getPointSearch(), getRsqlQuery());
    }

    private void addMarkersOnSuccess(List<Attraction> attractions) {
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

    private MarkerOptions createMarkerOptions(Attraction attraction) {
        return new MarkerOptions()
                .position(new LatLng(attraction.getPoint().getX(), attraction.getPoint().getY()))
                .icon(setCustomMarker(getContext(), getAttractiontMarker()))
                .title(attraction.getId());
    }

    private BitmapDescriptor setCustomMarker(Context context, int id) {
        Drawable background = ContextCompat.getDrawable(context, id);
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
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
            if (isRelativeLayoutAttractionInformationVisible)
                setRelativeLayoutInformationInvisible();
            else
                setRelativeLayoutInformationVisible();
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
        if (!isRelativeLayoutAttractionInformationVisible)
            setRelativeLayoutInformationVisible();
        if (!isLinearLayoutSearchAttractionVisible)
            setLinearLayoutSearchAttractionsVisible();
        if (!isFloatingActionButtonCenterPositionOnAttractionsVisible)
            setFloatingActionButtonCenterPositionOnAttractionsVisible();
        setRelativeLayoutInformationsFields(marker);
    }

    private void setRelativeLayoutInformationVisible() {
        isRelativeLayoutAttractionInformationVisible = true;
        getRelativeLayoutDetails().setVisibility(View.VISIBLE);
        getRelativeLayoutDetails().animate().translationY(0);
    }

    private void setRelativeLayoutInformationInvisible() {
        isRelativeLayoutAttractionInformationVisible = false;
        getRelativeLayoutDetails().animate().translationY(getRelativeLayoutDetails().getHeight() + 100);
    }

    private void setRelativeLayoutInformationsFields(Marker marker) {
        attraction = getAttractionFromMarkerClick(marker.getTitle());
        //setAttractionImage(attraction);
        getTextViewName().setText(attraction.getName());
        getTextViewRating().setText(createReviewString(attraction.getAvarageRating()));
        getTextViewAddress().setText(createAddressString(attraction.getAddress()));
    }

    private void setAttractionImage(Attraction attraction) {
        if (hasImage(attraction))
            Picasso.with(getContext())
                    .load(attraction.getImages().get(0))
                    .placeholder(R.drawable.troppadvisor_logo)
                    .into(getImageViewAttraction());
        else
            getImageViewAttraction().setImageDrawable(null);
    }

    private boolean hasImage(Attraction attraction) {
        return attraction.getImages().size() > 0;
    }

    private String createAddressString(Address address) {
        String attractionAddress = "";
        attractionAddress = attractionAddress.concat(address.getType() + " ");
        attractionAddress = attractionAddress.concat(address.getStreet() + ", ");
        attractionAddress = attractionAddress.concat(address.getHouseNumber() + ", ");
        attractionAddress = attractionAddress.concat(address.getCity() + ", ");
        attractionAddress = attractionAddress.concat(address.getProvince() + ", ");
        attractionAddress = attractionAddress.concat(address.getPostalCode());
        return attractionAddress;
    }

    private String createReviewString(Integer review) {
        if (review.equals(0))
            return attractionMapActivity.getResources().getString(R.string.no_review);
        else {
            String rating = "";
            rating = rating.concat(review + "/5");
            return rating;
        }
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
        getLinearLayoutSearchAttractions().animate().translationY(-attractionMapActivity.
                getLinearLayoutSearchAttractions().getHeight() - 100);
    }

    private void setFloatingActionButtonCenterPositionOnAttractionsVisible() {
        isFloatingActionButtonCenterPositionOnAttractionsVisible = true;
        attractionMapActivity.getFloatingActionButtonCenterPositionOnAttractions().show();
    }

    private void setFloatingActionButtonCenterPositionOnAttractionsInvisible() {
        isFloatingActionButtonCenterPositionOnAttractionsVisible = false;
        attractionMapActivity.getFloatingActionButtonCenterPositionOnAttractions().hide();
    }

    private void showBottomSheetMapFilters() {
        bottomSheetFilterAttractions.show(attractionMapActivity.getSupportFragmentManager(), bottomSheetFilterAttractions.getTag());
        setBottomSheetFiltersFields();
        bottomSheetFilterAttractions.setOnBottomSheetFilterSearchButtonClick(this);
        bottomSheetFilterAttractions.setAutoCompleteTextViewsAccomodationFilterTextChangeListener(this);
    }

    private void showToastNoResults() {
        attractionMapActivity.runOnUiThread(() -> {
            Toast.makeText(attractionMapActivity, "Nessun attrazione trovata in base ai criteri di ricerca", Toast.LENGTH_SHORT).show();
        });
    }

    public void setComponentProperties() {
        getRelativeLayoutDetails().animate().translationY(
                getRelativeLayoutDetails().getHeight() + 100);
    }

    public void setListenerOnViewComponents() {
        getTextViewSearchOnMap().setOnClickListener(this);
        getImageViewMapGoBack().setOnClickListener(this);
        getFloatingActionButtonCenterPositionOnAttractions().setOnClickListener(this);
    }

    private AttractionDAO getAttractionDAO() {
        return daoFactory.getAttractionDAO(getStorageTechnology(ATTRACTION_STORAGE_TECHNOLOGY));
    }

    private CityDAO getCityDAO() {
        return daoFactory.getCityDAO(getStorageTechnology(CITY_STORAGE_TECHNOLOGY));
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

    private Context getContext() {
        return attractionMapActivity.getApplicationContext();
    }

    private Resources getResources() {
        return attractionMapActivity.getResources();
    }

    private String getString(int string) {
        return getResources().getString(string);
    }

    private Intent getIntent() {
        return attractionMapActivity.getIntent();
    }

    public PointSearch getPointSearch() {
        return (PointSearch) getIntent().getSerializableExtra(POINT_SEARCH);
    }

    private String getRsqlQuery() {
        return getIntent().getStringExtra(RSQL_QUERY);
    }

    private boolean isIntentSearchingForName() {
        return !(getIntent().getStringExtra(NAME) == null) && !getIntent().getStringExtra(NAME).equals("");
    }

    private AccomodationAttractionFilter getAccomodationFilter() {
        return (AccomodationAttractionFilter) getIntent().getSerializableExtra(ACCOMODATION_FILTER);
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

    private int getAttractiontMarker() {
        return R.drawable.attraction_marker;
    }

    private String getAttractionName() {
        return getIntent().getStringExtra(NAME);
    }

    private boolean isBottomSheetFilterHotelsVisible() {
        return bottomSheetFilterAttractions != null && bottomSheetFilterAttractions.isAdded();
    }

    private boolean isAccomodationFilterNull() {
        return accomodationAttractionFilter == null;
    }

    private String getAccomodationFilterNameValue() {
        return accomodationAttractionFilter.getName();
    }

    private String getAccomodationFilterCityValue() {
        return accomodationAttractionFilter.getCity();
    }

    private Integer getAccomodationFilterAvaragePriceValue() {
        return accomodationAttractionFilter.getAvaragePrice();
    }

    private Integer getAccomodationFilterAvarageRatingValue() {
        return accomodationAttractionFilter.getAvarageRating();
    }

    private Double getAccomodationFilterDistanceValue() {
        return accomodationAttractionFilter.getDistance();
    }

    private boolean getAccomodationFilterHasCertificateOfExcellenceValue() {
        return accomodationAttractionFilter.isHasCertificateOfExcellence();
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
        return !getAccomodationFilterNameValue().equals("");
    }

    private boolean isSearchingForCity() {
        return !getAccomodationFilterCityValue().equals("");
    }

    private boolean isBottomSheetFilterHotelsNull() {
        return bottomSheetFilterAttractions == null;
    }

    private boolean isAccomodationFilterAvaragePriceEqualsToZero() {
        return getAccomodationFilterAvaragePriceValue().equals(0);
    }

    private boolean isAccomodationFilterAvaragePriceGreaterEqualsThan150() {
        return getAccomodationFilterAvaragePriceValue() >= 150;
    }

    private boolean isAccomodationFilterAvarageRatingEqualsToZero() {
        return getAccomodationFilterAvarageRatingValue().equals(0);
    }

    private boolean isAccomodationFilterDistanceEqualsToZero() {
        return getAccomodationFilterDistanceValue().equals(0d);
    }

    private boolean isAccomodationFilterHasCertificateOfExcellence() {
        return getAccomodationFilterHasCertificateOfExcellenceValue();
    }

    private boolean isBottomSheetFilterAttractionsVisible() {
        return bottomSheetFilterAttractions.isAdded();
    }

}
