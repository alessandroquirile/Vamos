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
import androidx.fragment.app.FragmentManager;

import com.androidbuts.multispinnerfilter.KeyPairBoolData;
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
import com.quiriletelese.troppadvisorproject.adapters.BottomSheetFilterRestaurants;
import com.quiriletelese.troppadvisorproject.dao_interfaces.CityDAO;
import com.quiriletelese.troppadvisorproject.dao_interfaces.RestaurantDAO;
import com.quiriletelese.troppadvisorproject.dao_interfaces.TypeOfCuisineDAO;
import com.quiriletelese.troppadvisorproject.factories.DAOFactory;
import com.quiriletelese.troppadvisorproject.interfaces.AutoCompleteTextViewsAccomodationFilterTextChangeListener;
import com.quiriletelese.troppadvisorproject.interfaces.Constants;
import com.quiriletelese.troppadvisorproject.interfaces.OnBottomSheetFilterSearchButtonClick;
import com.quiriletelese.troppadvisorproject.model_helpers.RestaurantFilter;
import com.quiriletelese.troppadvisorproject.model_helpers.PointSearch;
import com.quiriletelese.troppadvisorproject.models.Restaurant;
import com.quiriletelese.troppadvisorproject.utils.ConfigFileReader;
import com.quiriletelese.troppadvisorproject.views.RestaurantMapActivity;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBack;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alessandro Quirile, Mauro Telese
 */

public class RestaurantMapActivityController implements GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener,
        View.OnClickListener, OnBottomSheetFilterSearchButtonClick, AutoCompleteTextViewsAccomodationFilterTextChangeListener,
        Constants {

    private RestaurantMapActivity restaurantMapActivity;
    private BottomSheetFilterRestaurants bottomSheetFilterRestaurants = new BottomSheetFilterRestaurants();
    private RestaurantFilter restaurantFilter;
    private DAOFactory daoFactory = DAOFactory.getInstance();
    private Restaurant restaurant = null;
    private List<Restaurant> restaurants = new ArrayList<>();
    private List<String> typesOfCuisine = new ArrayList<>();
    private ArrayList<Marker> markers = new ArrayList<>();
    boolean isRelativeLayoutRestaurantInformationVisible = false, isLinearLayoutSearchRestaurantsVisible = true,
            isFloatingActionButtonCenterPositionOnRestaurantsVisible = true;

    public RestaurantMapActivityController(RestaurantMapActivity restaurantMapActivity) {
        this.restaurantMapActivity = restaurantMapActivity;
        restaurantFilter = getAccomodationFilter();
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
            case R.id.text_view_search_restaurants_on_map:
                showBottomSheetMapFilters();
                break;
            case R.id.image_view_restaurant_map_go_back:
                onBackPressed();
                break;
            case R.id.floating_action_button_center_position_on_restaurants:
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
        findRestarantsName(newText);
    }

    @Override
    public void onAutoCompleteTextViewAccomodtionCityTextChanged(final String newText) {
        findCitiesName(newText);
    }

    private void findByRsqlHelper(VolleyCallBack volleyCallBack, List<String> typesOfCuisine, PointSearch pointSearch,
                                  String rsqlQuery) {
        getResaurantDAO().findByRsql(volleyCallBack, typesOfCuisine, pointSearch, rsqlQuery, getContext(), 0, 10000);
    }

    private void findByNameLikeIgnoreCaseHelper(VolleyCallBack volleyCallBack, String name) {
        getResaurantDAO().findByNameLikeIgnoreCase(volleyCallBack, name, getContext(), 0, 10000);
    }

    public void findRestaurantsNameHelper(VolleyCallBack volleyCallBack, String name) {
        getResaurantDAO().findRestaurantsName(volleyCallBack, name, getContext());
    }

    public void findCitiesNameHelper(VolleyCallBack volleyCallBack, String name) {
        getCityDAO().findCitiesByName(volleyCallBack, name, getContext());
    }

    public void findTypeOfCuisineHelper(VolleyCallBack volleyCallBack) {
        getTypeOfCuisineDAO().getAll(volleyCallBack, getContext());
    }

    private void findByRsql(List<String> typesOfCuisine, PointSearch pointSearch, String rsqlQuery) {
        findByRsqlHelper(new VolleyCallBack() {
            @Override
            public void onSuccess(Object object) {
                setMapOnSuccess((List<Restaurant>) object);
            }

            @Override
            public void onError(String errorCode) {
                volleyCallbackOnError(errorCode);
            }

        }, typesOfCuisine, pointSearch, rsqlQuery);
    }

    private void findByNameLikeIgnoreCase(String name) {
        findByNameLikeIgnoreCaseHelper(new VolleyCallBack() {
            @Override
            public void onSuccess(Object object) {
                setMapOnSuccess((List<Restaurant>) object);
            }

            @Override
            public void onError(String errorCode) {
                volleyCallbackOnError(errorCode);
            }
        }, name);
    }

    private void findRestarantsName(String newText) {
        if (!newText.equals("")) {
            disableFieldsOnAutoCompleteTextViewNameChanged();
            findRestaurantsNameHelper(new VolleyCallBack() {
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
            findCitiesNameHelper(new VolleyCallBack() {
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

    private void findTypeOfCuisine() {
        findTypeOfCuisineHelper(new VolleyCallBack() {
            @Override
            public void onSuccess(Object object) {
                typesOfCuisine = (List<String>) object;
                initializeTypesOfCuisineField();
            }

            @Override
            public void onError(String errorCode) {

            }
        });
    }

    private void setMapOnSuccess(List<Restaurant> restaurants) {
        this.restaurants = restaurants;
        dismissBottomSheetFilterRestaurants();
        addMarkersOnSuccess(restaurants);
        zoomOnMap();
    }

    private void onBottomSheetFilterSearchButtonClickHelper() {
        createAccomodationFilter();
        detectSearchType();
    }

    private void disableFieldsOnAutoCompleteTextViewNameChanged() {
        bottomSheetFilterRestaurants.setAutoCompleteTextViewCityText("");
        bottomSheetFilterRestaurants.setSeekBarPriceEnabled(false);
        bottomSheetFilterRestaurants.setSeekBarRatingEnabled(false);
        bottomSheetFilterRestaurants.setSeekBarDistanceEnabled(false);
        bottomSheetFilterRestaurants.setSwitchCompatCertificateOfExcellenceEnabled(false);
        bottomSheetFilterRestaurants.setMultiSpinnerSearchTypesOfCuisineEnabled(false);
    }

    private void enableFieldsOnAutoCompleteTextViewNameChanged() {
        bottomSheetFilterRestaurants.setSeekBarPriceEnabled(true);
        bottomSheetFilterRestaurants.setSeekBarRatingEnabled(true);
        bottomSheetFilterRestaurants.setSeekBarDistanceEnabled(true);
        bottomSheetFilterRestaurants.setSwitchCompatCertificateOfExcellenceEnabled(true);
        bottomSheetFilterRestaurants.setMultiSpinnerSearchTypesOfCuisineEnabled(true);
    }

    private void disableFieldsOnAutoCompleteTextViewCityChanged() {
        bottomSheetFilterRestaurants.setAutoCompleteTextViewNameText("");
        bottomSheetFilterRestaurants.setSeekBarDistanceEnabled(false);
    }

    private void enableFieldsOnAutoCompleteTextViewCityChanged() {
        bottomSheetFilterRestaurants.setSeekBarDistanceEnabled(true);
    }

    private void createAccomodationFilter() {
        restaurantFilter = new RestaurantFilter();
        restaurantFilter.setName(getRestaurantNameValueFromBottomSheetFilter());
        restaurantFilter.setCity(getCityNameValueFromBottomSheetFilter());
        restaurantFilter.setAvaragePrice(getPriceValueFromBottomSheetFilter());
        restaurantFilter.setAvarageRating(getRatingValueFromBottomSheetFilter());
        restaurantFilter.setDistance(getDistanceValueFromBottomSheetFilter());
        restaurantFilter.setTypesOfCuisine(getMultiSpinnerSearchSelectedItemsFromBottomSheetFilter());
        restaurantFilter.setHasCertificateOfExcellence(getCertificateOfExcellenceFromBottomSheetFilter());
    }

    private void detectSearchType() {
        if (!isSearchingForName())
            findByRsql(isTypesOfCuisineListSelected() ? getAccomodationFilterTypesOfCuisine() : null,
                    isSearchingForCity() ? null : createPointSearch(),
                    !createRsqlString().equals("") ? createRsqlString() : "0");
        else
            findByNameLikeIgnoreCase(getAccomodationFilterNameValue());
    }

    private String getRestaurantNameValueFromBottomSheetFilter() {
        return bottomSheetFilterRestaurants.getAutoCompleteTextViewNameValue();
    }

    private String getCityNameValueFromBottomSheetFilter() {
        return extractCityName(bottomSheetFilterRestaurants.getAutoCompleteTextViewCityValue());
    }

    private Integer getPriceValueFromBottomSheetFilter() {
        return bottomSheetFilterRestaurants.getSeekBarPriceValue();
    }

    private Integer getRatingValueFromBottomSheetFilter() {
        return bottomSheetFilterRestaurants.getSeekBarRatingValue();
    }

    private Double getDistanceValueFromBottomSheetFilter() {
        return isDistanceSeekbarEnabled() && isDistanceDifferentFromZero() ?
                (double) bottomSheetFilterRestaurants.getSeekBarDistanceValue() : 1d;
    }

    private boolean getCertificateOfExcellenceFromBottomSheetFilter() {
        return bottomSheetFilterRestaurants.getSwitchCompatCertificateOfExcellenceIsSelected();
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

    private List<String> getMultiSpinnerSearchSelectedItemsFromBottomSheetFilter() {
        List<String> typeOfCuisine = new ArrayList<>();
        List<KeyPairBoolData> keyPairBoolDataList = bottomSheetFilterRestaurants.getMultiSpinnerSearchSelectedItems();
        for (KeyPairBoolData keyPairBoolData : keyPairBoolDataList)
            typeOfCuisine.add(keyPairBoolData.getName());
        return typeOfCuisine;
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
            findByNameLikeIgnoreCase(getRestaurantName());
        else
            findByRsql(null, getPointSearch(), getRsqlQuery());
    }

    private void addMarkersOnSuccess(List<Restaurant> restaurants) {
        clearAllMarkersOnMap();
        for (Restaurant restaurant : restaurants)
            markers.add(addMarker(restaurant));
    }

    private Marker addMarker(Restaurant restaurant) {
        return getGoogleMap().addMarker(createMarkerOptions(restaurant));
    }

    private void clearAllMarkersOnMap() {
        getGoogleMap().clear();
        markers = new ArrayList<>();
    }

    private MarkerOptions createMarkerOptions(Restaurant restaurant) {
        return new MarkerOptions()
                .position(createCoordinates(restaurant))
                .icon(setCustomMarker(getContext(), getRestaurantMarker()))
                .title(restaurant.getId());
    }

    private LatLng createCoordinates(Restaurant restaurant) {
        return new LatLng(getLatitude(restaurant), getLongitude(restaurant));
    }

    private Double getLatitude(Restaurant restaurant) {
        return restaurant.getPoint().getX();
    }

    private Double getLongitude(Restaurant restaurant) {
        return restaurant.getPoint().getY();
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
        if (restaurant != null) {
            if (isRelativeLayoutRestaurantInformationVisible)
                setRelativeLayoutHotelInformationInvisible();
            else
                setRelativeLayoutHotelInformationVisible();
        }
        if (isLinearLayoutSearchRestaurantsVisible)
            setLinearLayoutSearchRestaurantsInvisible();
        else
            setLinearLayoutSearchRestaurantsVisible();
        if (isFloatingActionButtonCenterPositionOnRestaurantsVisible)
            setFloatingActionButtonCenterPositionOnRestaurantsInvisible();
        else
            setFloatingActionButtonCenterPositionOnRestaurantsVisible();
    }

    private void onMarkerClickHelper(Marker marker) {
        marker.showInfoWindow();
        if (!isRelativeLayoutRestaurantInformationVisible)
            setRelativeLayoutHotelInformationVisible();
        if (!isLinearLayoutSearchRestaurantsVisible)
            setLinearLayoutSearchRestaurantsVisible();
        if (!isFloatingActionButtonCenterPositionOnRestaurantsVisible)
            setFloatingActionButtonCenterPositionOnRestaurantsVisible();
        setRelativeLayoutInformationHotelFields(marker);
    }

    private void setRelativeLayoutHotelInformationVisible() {
        isRelativeLayoutRestaurantInformationVisible = true;
        getRelativeLayoutDetails().setVisibility(View.VISIBLE);
        getRelativeLayoutDetails().animate().translationY(0);
    }

    private void setRelativeLayoutHotelInformationInvisible() {
        isRelativeLayoutRestaurantInformationVisible = false;
        getRelativeLayoutDetails().animate().translationY(
                getRelativeLayoutDetails().getHeight() + 100);
    }

    private void setRelativeLayoutInformationHotelFields(Marker marker) {
        restaurant = getRestaurantFromMarkerClick(marker.getTitle());
        //setRestaurantImage(restaurant);
        getTextViewName().setText(restaurant.getName());
        getTextViewRating().setText(createAvarageRatingString(restaurant));
        getTextViewAddress().setText(createAddressString(restaurant));
    }

    private void setRestaurantImage(Restaurant restaurant) {
        if (hasImage(restaurant))
            Picasso.with(getContext())
                    .load(restaurant.getImages().get(0))
                    .placeholder(R.drawable.troppadvisor_logo)
                    .into(getImageViewRestaurant());
        else
            getImageViewRestaurant().setImageDrawable(null);
    }

    private boolean hasImage(Restaurant restaurant) {
        return restaurant.isImagesGraterThanZero();
    }

    private String createAddressString(Restaurant restaurant) {
        String hotelAddress = "";
        hotelAddress = hotelAddress.concat(restaurant.getTypeOfAddress() + " ");
        hotelAddress = hotelAddress.concat(restaurant.getStreet() + ", ");
        hotelAddress = hotelAddress.concat(restaurant.getHouseNumber() + ", ");
        hotelAddress = hotelAddress.concat(restaurant.getCity() + ", ");
        hotelAddress = hotelAddress.concat(restaurant.getProvince() + ", ");
        hotelAddress = hotelAddress.concat(restaurant.getPostalCode());
        return hotelAddress;
    }

    private String createAvarageRatingString(Restaurant restaurant) {
        return !hasReviews(restaurant) ? getString(R.string.no_reviews) : createAvarageRatingStringHelper(restaurant);
    }

    private String createAvarageRatingStringHelper(Restaurant restaurant){
        return restaurant.getAvarageRating() + "/5 (" + restaurant.getTotalReviews() + " " + getString(R.string.reviews) + ")";
    }

    private boolean hasReviews(Restaurant restaurant){
        return !restaurant.getAvarageRating().equals(0);
    }

    private Restaurant getRestaurantFromMarkerClick(String restaurantId) {
        Restaurant restaurantToReturn = null;
        for (Restaurant restaurant : restaurants)
            if (restaurant.getId().equals(restaurantId)) {
                restaurantToReturn = restaurant;
                break;
            }
        return restaurantToReturn;
    }

    private void setLinearLayoutSearchRestaurantsVisible() {
        setIsLinearLayoutSearchRestaurantsVisible(true);
        getLinearLayoutSearchRestaurants().animate().translationY(0);
    }

    private void setLinearLayoutSearchRestaurantsInvisible() {
        setIsLinearLayoutSearchRestaurantsVisible(false);
        getLinearLayoutSearchRestaurants().animate().translationY(-getLinearLayoutSearchRestaurants()
                .getHeight() - 100);
    }

    private void setFloatingActionButtonCenterPositionOnRestaurantsVisible() {
        setIsFloatingActionButtonCenterPositionOnRestaurantsVisible(true);
        getFloatingActionButtonCenterPositionOnRestaurants().show();
    }

    private void setFloatingActionButtonCenterPositionOnRestaurantsInvisible() {
        setIsFloatingActionButtonCenterPositionOnRestaurantsVisible(false);
        getFloatingActionButtonCenterPositionOnRestaurants().hide();
    }

    private void setIsFloatingActionButtonCenterPositionOnRestaurantsVisible(boolean value) {
        isFloatingActionButtonCenterPositionOnRestaurantsVisible = value;
    }

    private void setIsLinearLayoutSearchRestaurantsVisible(boolean value) {
        isLinearLayoutSearchRestaurantsVisible = value;
    }

    private void showBottomSheetMapFilters() {
        bottomSheetFilterRestaurants.show(getSupportFragmentManager(), getTag());
        setBottomSheetFiltersFields();
        setTypesOfCuisine();
        bottomSheetFilterRestaurants.setOnBottomSheetFilterSearchButtonClick(this);
        bottomSheetFilterRestaurants.setAutoCompleteTextViewsAccomodationFilterTextChangeListener(this);
    }

    private void setTypesOfCuisine() {
        if (isTypesOfCuisineEmpty())
            findTypeOfCuisine();
        else
            setTypesOfCuisineNoVolley();
    }

    private void setTypesOfCuisineNoVolley() {
        new Handler().postDelayed(this::initializeTypesOfCuisineField, 100);
    }

    private void initializeTypesOfCuisineField() {
        bottomSheetFilterRestaurants.setTypeOfCuisineList(typesOfCuisine);
        if (!isAccomodationFilterNull())
            bottomSheetFilterRestaurants.setSelectedItems(getAccomodationFilterTypesOfCuisine());
    }

    private void setBottomSheetFiltersFields() {
        if (!isBottomSheetFilterRestaurantsVisible() && !isAccomodationFilterNull()) {
            new Handler().postDelayed(this::setFields, 100);
        }
    }

    private void setFields() {
        bottomSheetFilterRestaurants.setAutoCompleteTextViewNameText(getAccomodationFilterNameValue());
        bottomSheetFilterRestaurants.setAutoCompleteTextViewCityText(getAccomodationFilterCityValue());
        bottomSheetFilterRestaurants.setSeekBarPriceProgress(getAccomodationFilterAvaragePriceValue());
        bottomSheetFilterRestaurants.setSeekBarRatingProgress(getAccomodationFilterAvarageRatingValue());
        bottomSheetFilterRestaurants.setSeekBarDistanceProgress(getAccomodationFilterDistanceValue().intValue());
        if (isSearchingForCity() || isSearchingForName())
            bottomSheetFilterRestaurants.setSeekBarDistanceEnabled(false);
        bottomSheetFilterRestaurants.getSwitchCompatCertificateOfExcellence().setChecked(getAccomodationFilterHasCertificateOfExcellenceValue());
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
        showToastOnUiThread(R.string.no_restaurants_found_by_filter);
    }

    private void handleOtherVolleyError() {
        showToastOnUiThread(R.string.unexpected_error_while_fetch_data);
    }

    private void showToastOnUiThread(int string) {
        restaurantMapActivity.runOnUiThread(() -> {
            Toast.makeText(restaurantMapActivity, getString(string), Toast.LENGTH_SHORT).show();
        });
    }

    public void setComponentProperties() {
        getRelativeLayoutDetails().animate().translationY(getRelativeLayoutDetails().getHeight() + 100);
    }

    public void setListenerOnViewComponents() {
        getTextViewSearchOnMap().setOnClickListener(this);
        getImageViewMapGoBack().setOnClickListener(this);
        getFloatingActionButtonCenterPositionOnRestaurants().setOnClickListener(this);
    }

    private RestaurantDAO getResaurantDAO() {
        return daoFactory.getRestaurantDAO(getStorageTechnology(RESTAURANT_STORAGE_TECHNOLOGY));
    }

    private CityDAO getCityDAO() {
        return daoFactory.getCityDAO(getStorageTechnology(CITY_STORAGE_TECHNOLOGY));
    }

    private TypeOfCuisineDAO getTypeOfCuisineDAO() {
        return daoFactory.getTypeOfCuisineDAO(getStorageTechnology(TYPES_OF_CUISINE_STORAGE_TECHNOLOGY));
    }

    private String getStorageTechnology(String storageTechnology) {
        return ConfigFileReader.getProperty(storageTechnology, getContext());
    }

    private void dismissBottomSheetFilterRestaurants() {
        if (isBottomSheetFilterRestaurantsVisible())
            bottomSheetFilterRestaurants.dismiss();
    }

    private void onBackPressed() {
        restaurantMapActivity.onBackPressed();
    }

    private FragmentManager getSupportFragmentManager() {
        return restaurantMapActivity.getSupportFragmentManager();
    }

    private String getTag() {
        return bottomSheetFilterRestaurants.getTag();
    }

    private Context getContext() {
        return restaurantMapActivity.getApplicationContext();
    }

    private Resources getResources() {
        return restaurantMapActivity.getResources();
    }

    private String getString(int string) {
        return getResources().getString(string);
    }

    private Intent getIntent() {
        return restaurantMapActivity.getIntent();
    }

    private PointSearch getPointSearch() {
        return (PointSearch) getIntent().getSerializableExtra(POINT_SEARCH);
    }

    private String getRsqlQuery() {
        return getIntent().getStringExtra(RSQL_QUERY);
    }

    private boolean isIntentSearchingForName() {
        return getIntent().getBooleanExtra(SEARCH_FOR_NAME, false);
    }

    private RestaurantFilter getAccomodationFilter() {
        return (RestaurantFilter) getIntent().getSerializableExtra(ACCOMODATION_FILTER);
    }

    private AutoCompleteTextView getAutoCompleteTextViewName() {
        return bottomSheetFilterRestaurants.getAutoCompleteTextViewName();
    }

    private AutoCompleteTextView getAutoCompleteTextViewCity() {
        return bottomSheetFilterRestaurants.getAutoCompleteTextViewCity();
    }

    private int getArrayAdapterLayout() {
        return android.R.layout.select_dialog_item;
    }

    private GoogleMap getGoogleMap() {
        return restaurantMapActivity.getGoogleMap();
    }

    private int getRestaurantMarker() {
        return R.drawable.restaurant_marker;
    }

    private String getRestaurantName() {
        return restaurantMapActivity.getIntent().getStringExtra(NAME);
    }

    private boolean isAccomodationFilterNull() {
        return restaurantFilter == null;
    }

    private String getAccomodationFilterNameValue() {
        return restaurantFilter.getName();
    }

    private String getAccomodationFilterCityValue() {
        return restaurantFilter.getCity();
    }

    private Integer getAccomodationFilterAvaragePriceValue() {
        return restaurantFilter.getAvaragePrice();
    }

    private Integer getAccomodationFilterAvarageRatingValue() {
        return restaurantFilter.getAvarageRating();
    }

    private Double getAccomodationFilterDistanceValue() {
        return restaurantFilter.getDistance();
    }

    private List<String> getAccomodationFilterTypesOfCuisine() {
        return restaurantFilter.getTypesOfCuisine();
    }

    private boolean getAccomodationFilterHasCertificateOfExcellenceValue() {
        return restaurantFilter.isHasCertificateOfExcellence();
    }

    private TextView getTextViewSearchOnMap() {
        return restaurantMapActivity.getTextViewSearchOnMap();
    }

    private ImageView getImageViewMapGoBack() {
        return restaurantMapActivity.getImageViewMapGoBack();
    }

    private RelativeLayout getRelativeLayoutDetails() {
        return restaurantMapActivity.getRelativeLayoutDetails();
    }

    private TextView getTextViewName() {
        return restaurantMapActivity.getTextViewName();
    }

    private TextView getTextViewRating() {
        return restaurantMapActivity.getTextViewRating();
    }

    private TextView getTextViewAddress() {
        return restaurantMapActivity.getTextViewAddress();
    }

    private ImageView getImageViewRestaurant() {
        return restaurantMapActivity.getImageViewRestaurant();
    }

    private LinearLayout getLinearLayoutSearchRestaurants() {
        return restaurantMapActivity.getLinearLayoutSearchRestaurants();
    }

    private FloatingActionButton getFloatingActionButtonCenterPositionOnRestaurants() {
        return restaurantMapActivity.getFloatingActionButtonCenterPositionOnRestaurants();
    }

    private boolean isDistanceSeekbarEnabled() {
        return bottomSheetFilterRestaurants.getSeekBarDistance().isEnabled();
    }

    private boolean isDistanceDifferentFromZero() {
        return bottomSheetFilterRestaurants.getSeekBarDistanceValue() != 0;
    }

    private boolean isSearchingForName() {
        return !getAccomodationFilterNameValue().equals("");
    }

    private boolean isSearchingForCity() {
        return !getAccomodationFilterCityValue().equals("");
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

    private boolean isTypesOfCuisineListSelected() {
        return restaurantFilter != null && getAccomodationFilterTypesOfCuisine() != null && getAccomodationFilterTypesOfCuisine().size() != 0;
    }

    private boolean isAccomodationFilterHasCertificateOfExcellence() {
        return getAccomodationFilterHasCertificateOfExcellenceValue();
    }

    private boolean isBottomSheetFilterRestaurantsVisible() {
        return bottomSheetFilterRestaurants.isAdded();
    }

    private boolean isTypesOfCuisineEmpty() {
        return typesOfCuisine.isEmpty();
    }

}
