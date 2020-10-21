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
import com.quiriletelese.troppadvisorproject.model_helpers.Constants;
import com.quiriletelese.troppadvisorproject.model_helpers.PointSearch;
import com.quiriletelese.troppadvisorproject.model_helpers.RestaurantFilter;
import com.quiriletelese.troppadvisorproject.models.Restaurant;
import com.quiriletelese.troppadvisorproject.util_interfaces.AutoCompleteTextViewsAccomodationFilterTextChangeListener;
import com.quiriletelese.troppadvisorproject.util_interfaces.BottomSheetFilterSearchButtonClick;
import com.quiriletelese.troppadvisorproject.utils.ConfigFileReader;
import com.quiriletelese.troppadvisorproject.views.RestaurantMapActivity;
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

public class RestaurantMapActivityController implements GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener,
        View.OnClickListener, BottomSheetFilterSearchButtonClick, AutoCompleteTextViewsAccomodationFilterTextChangeListener {

    private final RestaurantMapActivity restaurantMapActivity;
    private final BottomSheetFilterRestaurants bottomSheetFilterRestaurants = new BottomSheetFilterRestaurants();
    private RestaurantFilter restaurantFilter;
    private final DAOFactory daoFactory = DAOFactory.getInstance();
    private Restaurant restaurant = null;
    private List<Restaurant> restaurants = new ArrayList<>();
    private List<String> typesOfCuisine = new ArrayList<>();
    private ArrayList<Marker> markers = new ArrayList<>();
    private boolean isRelativeLayoutRestaurantInformationVisible = false;
    private boolean isLinearLayoutSearchRestaurantsVisible = true;
    private boolean isFloatingActionButtonCenterPositionOnRestaurantsVisible = true;
    private TypeOfCuisineDAO typeOfCuisineDAO;
    private RestaurantDAO restaurantDAO;
    private CityDAO cityDAO;

    public RestaurantMapActivityController(RestaurantMapActivity restaurantMapActivity) {
        this.restaurantMapActivity = restaurantMapActivity;
        restaurantFilter = getRestaurantFilter();
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
        findRestarantsName(newText);
    }

    @Override
    public void onAutoCompleteTextViewAccomodationCityTextChanged(final String newText) {
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
        createRestaurantFilter();
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

    private void createRestaurantFilter() {
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
            findByRsql(isTypesOfCuisineListSelected() ? getRestaurantFilterTypesOfCuisine() : null,
                    isSearchingForCity() ? null : createPointSearch(),
                    !createRsqlString().equals("") ? createRsqlString() : "0");
        else
            findByNameLikeIgnoreCase(getRestaurantFilterNameValue());
    }

    private String getRestaurantNameValueFromBottomSheetFilter() {
        return bottomSheetFilterRestaurants.getAutoCompleteTextViewNameValue();
    }

    private String getCityNameValueFromBottomSheetFilter() {
        return extractCityName(bottomSheetFilterRestaurants.getAutoCompleteTextViewCityValue());
    }

    @NotNull
    private Integer getPriceValueFromBottomSheetFilter() {
        return bottomSheetFilterRestaurants.getSeekBarPriceValue();
    }

    @NotNull
    private Integer getRatingValueFromBottomSheetFilter() {
        return bottomSheetFilterRestaurants.getSeekBarRatingValue();
    }

    @NotNull
    private Double getDistanceValueFromBottomSheetFilter() {
        return isDistanceSeekbarEnabled() && isDistanceDifferentFromZero() ?
                (double) bottomSheetFilterRestaurants.getSeekBarDistanceValue() : 1d;
    }

    private boolean getCertificateOfExcellenceFromBottomSheetFilter() {
        return bottomSheetFilterRestaurants.getSwitchCompatCertificateOfExcellenceIsSelected();
    }

    private String checkCityNameValue(String rsqlString) {
        if (isSearchingForCity()) {
            String cityName = extractCityName(getRestaurantFilterCityValue());
            rsqlString = rsqlString.concat("address.city==" + cityName + ";");
        }
        return rsqlString;
    }

    private String extractCityName(@NotNull String city) {
        return city.contains(",") ? city.substring(0, city.lastIndexOf(",")) : city.trim();
    }

    private String checkPriceValue(String rsqlString) {
        if (!isRestaurantFilterAvaragePriceEqualsToZero()) {
            if (isRestaurantFilterAvaragePriceGreaterEqualsThan150())
                rsqlString = rsqlString.concat("avaragePrice=ge=" + getRestaurantFilterAvaragePriceValue() + ";");
            else
                rsqlString = rsqlString.concat("avaragePrice=le=" + getRestaurantFilterAvaragePriceValue() + ";");
        }
        return rsqlString;
    }

    private String checkRatingValue(String rsqlString) {
        if (!isRestaurantFilterAvarageRatingEqualsToZero())
            rsqlString = rsqlString.concat("avarageRating=ge=" + getRestaurantFilterAvarageRatingValue() + ";");
        return rsqlString;
    }

    private Double checkDistanceValue() {
        double distance = 1.0;
        if (!isRestaurantFilterDistanceEqualsToZero())
            distance = getRestaurantFilterDistanceValue();
        return distance;
    }

    private String checkCertificateOfExcellence(String rsqlString) {
        if (isRestaurantFilterHasCertificateOfExcellence())
            rsqlString = rsqlString.concat("certificateOfExcellence==" + "true;");
        return rsqlString;
    }

    @NotNull
    private List<String> getMultiSpinnerSearchSelectedItemsFromBottomSheetFilter() {
        List<String> typeOfCuisine = new ArrayList<>();
        List<KeyPairBoolData> keyPairBoolDataList = bottomSheetFilterRestaurants.getMultiSpinnerSearchSelectedItems();
        for (KeyPairBoolData keyPairBoolData : keyPairBoolDataList)
            typeOfCuisine.add(keyPairBoolData.getName());
        return typeOfCuisine;
    }

    @NotNull
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

    @NotNull
    private PointSearch createPointSearch() {
        PointSearch pointSearch = getPointSearch();
        pointSearch.setDistance(isRestaurantFilterNull() ? 5d : checkDistanceValue());
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
        if (isSearchingForName())
            findByNameLikeIgnoreCase(getRestaurantName());
        else if (isSearchingForCity())
            findByRsql(restaurantFilter.getTypesOfCuisine(), null, getRsqlQuery());
        else
            findByRsql(restaurantFilter.getTypesOfCuisine(), getPointSearch(), getRsqlQuery());
    }

    private void addMarkersOnSuccess(@NotNull List<Restaurant> restaurants) {
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

    @NotNull
    private MarkerOptions createMarkerOptions(Restaurant restaurant) {
        return new MarkerOptions()
                .position(createCoordinates(restaurant))
                .icon(setCustomMarker(getContext(), getRestaurantMarker()))
                .title(restaurant.getId());
    }

    @NotNull
    @Contract("_ -> new")
    private LatLng createCoordinates(@NotNull Restaurant restaurant) {
        return new LatLng(restaurant.getLatitude(), restaurant.getLongitude());
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

    private void onMarkerClickHelper(@NotNull Marker marker) {
        setMarkerClicked(marker.getId());
        if (!isRelativeLayoutRestaurantInformationVisible)
            setRelativeLayoutHotelInformationVisible();
        if (!isLinearLayoutSearchRestaurantsVisible)
            setLinearLayoutSearchRestaurantsVisible();
        if (!isFloatingActionButtonCenterPositionOnRestaurantsVisible)
            setFloatingActionButtonCenterPositionOnRestaurantsVisible();
        setRelativeLayoutInformationHotelFields(marker);
    }

    private void setMarkerClicked(String id) {
        for (Marker marker : markers) {
            if (marker.getId().equals(id))
                marker.setIcon(setCustomMarker(getContext(), getRestaurantMarkerClicked()));
            else
                marker.setIcon(setCustomMarker(getContext(), getRestaurantMarker()));
        }
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

    private void setRelativeLayoutInformationHotelFields(@NotNull Marker marker) {
        restaurant = getRestaurantFromMarkerClick(marker.getTitle());
        setImage(restaurant);
        getTextViewName().setText(restaurant.getName());
        getTextViewRating().setText(createAvarageRatingString(restaurant));
        getTextViewAddress().setText(createAddressString(restaurant));
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setImage(Restaurant restaurant) {
        if (hasImage(restaurant))
            Picasso.with(getContext())
                    .load(restaurant.getImages().get(0))
                    .placeholder(R.drawable.troppadvisor_logo)
                    .error(R.drawable.picasso_error)
                    .into(getImageViewRestaurant());
        else
            getImageViewRestaurant().setImageDrawable(getResources().getDrawable(R.drawable.picasso_error));
    }

    private boolean hasImage(@NotNull Restaurant restaurant) {
        return restaurant.hasImage();
    }

    @NotNull
    private String createAddressString(@NotNull Restaurant restaurant) {
        String restaurantAddress = "";
        restaurantAddress = restaurantAddress.concat(restaurant.getTypeOfAddress() + " ");
        restaurantAddress = restaurantAddress.concat(restaurant.getStreet() + ", ");
        restaurantAddress = restaurantAddress.concat(restaurant.getHouseNumber() + ", ");
        restaurantAddress = restaurantAddress.concat(restaurant.getCity() + ", ");
        if (!restaurant.getProvince().equals(restaurant.getCity()))
            restaurantAddress = restaurantAddress.concat(restaurant.getProvince() + ", ");
        restaurantAddress = restaurantAddress.concat(restaurant.getPostalCode());
        return restaurantAddress;
    }

    private String createAvarageRatingString(Restaurant restaurant) {
        return !hasReviews(restaurant) ? getString(R.string.no_reviews) : createAvarageRatingStringHelper(restaurant);
    }

    @NotNull
    private String createAvarageRatingStringHelper(@NotNull Restaurant restaurant) {
        return restaurant.getAvarageRating().intValue() + "/5 (" + restaurant.getTotalReviews() + " " + getString(R.string.reviews) + ")";
    }

    private boolean hasReviews(@NotNull Restaurant restaurant) {
        return restaurant.hasReviews();
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
        bottomSheetFilterRestaurants.setBottomSheetFilterSearchButtonClick(this);
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
        if (!isRestaurantFilterNull())
            bottomSheetFilterRestaurants.setSelectedItems(getRestaurantFilterTypesOfCuisine());
    }

    private void setBottomSheetFiltersFields() {
        if (!isBottomSheetFilterRestaurantsVisible() && !isRestaurantFilterNull()) {
            new Handler().postDelayed(this::setFields, 100);
        }
    }

    private void setFields() {
        bottomSheetFilterRestaurants.setAutoCompleteTextViewNameText(getRestaurantFilterNameValue());
        bottomSheetFilterRestaurants.setAutoCompleteTextViewCityText(getRestaurantFilterCityValue());
        bottomSheetFilterRestaurants.setSeekBarPriceProgress(getRestaurantFilterAvaragePriceValue());
        bottomSheetFilterRestaurants.setSeekBarRatingProgress(getRestaurantFilterAvarageRatingValue());
        bottomSheetFilterRestaurants.setSeekBarDistanceProgress(getRestaurantFilterDistanceValue().intValue());
        if (isSearchingForCity() || isSearchingForName())
            bottomSheetFilterRestaurants.setSeekBarDistanceEnabled(false);
        bottomSheetFilterRestaurants.getSwitchCompatCertificateOfExcellence().setChecked(getRestaurantFilterHasCertificateOfExcellenceValue());
    }

    private void volleyCallbackOnError(@NotNull String errorCode) {
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
        restaurantMapActivity.runOnUiThread(() ->
                Toast.makeText(restaurantMapActivity, getString(string), Toast.LENGTH_SHORT).show());
    }

    public void setComponentProperties() {
        getRelativeLayoutDetails().animate().translationY(getRelativeLayoutDetails().getHeight() + 100);
    }

    private void onClickHelper(View view) {
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

    public void setListenerOnViewComponents() {
        getTextViewSearchOnMap().setOnClickListener(this);
        getImageViewMapGoBack().setOnClickListener(this);
        getFloatingActionButtonCenterPositionOnRestaurants().setOnClickListener(this);
    }

    private RestaurantDAO getResaurantDAO() {
        restaurantDAO = daoFactory.getRestaurantDAO(getStorageTechnology(Constants.getRestaurantStorageTechnology()));
        return restaurantDAO;
    }

    private CityDAO getCityDAO() {
        cityDAO = daoFactory.getCityDAO(getStorageTechnology(Constants.getCityStorageTechnology()));
        return cityDAO;
    }

    private TypeOfCuisineDAO getTypeOfCuisineDAO() {
        typeOfCuisineDAO = daoFactory.getTypeOfCuisineDAO(getStorageTechnology(Constants.getTypesOfCuisineStorageTechnology()));
        return typeOfCuisineDAO;
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
        return (PointSearch) getIntent().getSerializableExtra(Constants.getPointSearch());
    }

    private String getRsqlQuery() {
        return getIntent().getStringExtra(Constants.getRsqlQuery());
    }

    private RestaurantFilter getRestaurantFilter() {
        return (RestaurantFilter) getIntent().getSerializableExtra(Constants.getAccomodationFilter());
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

    private int getRestaurantMarkerClicked() {
        return R.drawable.restaurant_marker_clicked;
    }

    private String getRestaurantName() {
        return restaurantMapActivity.getIntent().getStringExtra(Constants.getName());
    }

    private boolean isRestaurantFilterNull() {
        return restaurantFilter == null;
    }

    private String getRestaurantFilterNameValue() {
        return restaurantFilter.getName();
    }

    private String getRestaurantFilterCityValue() {
        return restaurantFilter.getCity();
    }

    private Integer getRestaurantFilterAvaragePriceValue() {
        return restaurantFilter.getAvaragePrice();
    }

    private Integer getRestaurantFilterAvarageRatingValue() {
        return restaurantFilter.getAvarageRating();
    }

    private Double getRestaurantFilterDistanceValue() {
        return restaurantFilter.getDistance();
    }

    private List<String> getRestaurantFilterTypesOfCuisine() {
        return restaurantFilter.getTypesOfCuisine();
    }

    private boolean getRestaurantFilterHasCertificateOfExcellenceValue() {
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
        return !getRestaurantFilterNameValue().equals("");
    }

    private boolean isSearchingForCity() {
        return !getRestaurantFilterCityValue().equals("");
    }

    private boolean isRestaurantFilterAvaragePriceEqualsToZero() {
        return getRestaurantFilterAvaragePriceValue().equals(0);
    }

    private boolean isRestaurantFilterAvaragePriceGreaterEqualsThan150() {
        return getRestaurantFilterAvaragePriceValue() >= 150;
    }

    private boolean isRestaurantFilterAvarageRatingEqualsToZero() {
        return getRestaurantFilterAvarageRatingValue().equals(0);
    }

    private boolean isRestaurantFilterDistanceEqualsToZero() {
        return getRestaurantFilterDistanceValue().equals(0d);
    }

    private boolean isTypesOfCuisineListSelected() {
        return restaurantFilter != null && getRestaurantFilterTypesOfCuisine() != null && getRestaurantFilterTypesOfCuisine().size() != 0;
    }

    private boolean isRestaurantFilterHasCertificateOfExcellence() {
        return getRestaurantFilterHasCertificateOfExcellenceValue();
    }

    private boolean isBottomSheetFilterRestaurantsVisible() {
        return bottomSheetFilterRestaurants.isAdded();
    }

    private boolean isTypesOfCuisineEmpty() {
        return typesOfCuisine.isEmpty();
    }

}
