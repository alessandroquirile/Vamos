package com.quiriletelese.troppadvisorproject.controllers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

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
import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.adapters.BottomSheetFilterRestaurants;
import com.quiriletelese.troppadvisorproject.dao_interfaces.CityDAO;
import com.quiriletelese.troppadvisorproject.dao_interfaces.RestaurantDAO;
import com.quiriletelese.troppadvisorproject.dao_interfaces.TypeOfCuisineDAO;
import com.quiriletelese.troppadvisorproject.factories.DAOFactory;
import com.quiriletelese.troppadvisorproject.interfaces.AutoCompleteTextViewsAccomodationFilterTextChangeListener;
import com.quiriletelese.troppadvisorproject.interfaces.Constants;
import com.quiriletelese.troppadvisorproject.interfaces.OnBottomSheetFilterSearchButtonClick;
import com.quiriletelese.troppadvisorproject.model_helpers.AccomodationRestaurantFilter;
import com.quiriletelese.troppadvisorproject.model_helpers.Address;
import com.quiriletelese.troppadvisorproject.model_helpers.PointSearch;
import com.quiriletelese.troppadvisorproject.models.Hotel;
import com.quiriletelese.troppadvisorproject.models.Restaurant;
import com.quiriletelese.troppadvisorproject.utils.ConfigFileReader;
import com.quiriletelese.troppadvisorproject.views.RestaurantMapActivity;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBack;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBackCity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RestaurantMapActivityController implements GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener,
        View.OnClickListener, OnBottomSheetFilterSearchButtonClick, AutoCompleteTextViewsAccomodationFilterTextChangeListener,
        Constants {

    private RestaurantMapActivity restaurantMapActivity;
    private BottomSheetFilterRestaurants bottomSheetFilterRestaurants;
    private AccomodationRestaurantFilter accomodationRestaurantFilter;
    private DAOFactory daoFactory = DAOFactory.getInstance();
    private Restaurant restaurant = null;
    private List<Restaurant> restaurants = new ArrayList<>();
    private List<String> typesOfCuisine = new ArrayList<>();
    private ArrayList<Marker> markers = new ArrayList<>();
    boolean isRelativeLayoutRestaurantInformationVisible = false, isLinearLayoutSearchRestaurantsVisible = true,
            isFloatingActionButtonCenterPositionOnRestaurantsVisible = true;

    public RestaurantMapActivityController(RestaurantMapActivity restaurantMapActivity) {
        this.restaurantMapActivity = restaurantMapActivity;
        accomodationRestaurantFilter = getAccomodationRestaurantFilter();
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
                restaurantMapActivity.onBackPressed();
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
                                  String rsqlQuery, Context context, int page, int size) {
        RestaurantDAO restaurantDAO = daoFactory.getRestaurantDAO(ConfigFileReader.getProperty(HOTEL_STORAGE_TECHNOLOGY,
                restaurantMapActivity.getApplicationContext()));
        restaurantDAO.findByRsql(volleyCallBack, typesOfCuisine, pointSearch, rsqlQuery, context, page, size);
    }

    private void findByNameLikeIgnoreCaseHelper(VolleyCallBack volleyCallBack, String name, Context context,
                                                int page, int size) {
        RestaurantDAO restaurantDAO = daoFactory.getRestaurantDAO(ConfigFileReader.getProperty(HOTEL_STORAGE_TECHNOLOGY,
                restaurantMapActivity.getApplicationContext()));
        restaurantDAO.findByNameLikeIgnoreCase(volleyCallBack, name, context, page, size);
    }

    public void findRestaurantsNameHelper(VolleyCallBack volleyCallBack, String name) {
        RestaurantDAO restaurantDAO = daoFactory.getRestaurantDAO(ConfigFileReader.getProperty(HOTEL_STORAGE_TECHNOLOGY,
                restaurantMapActivity.getApplicationContext()));
        restaurantDAO.findRestaurantsName(volleyCallBack, name, restaurantMapActivity.getApplicationContext());
    }

    public void findCitiesNameHelper(VolleyCallBackCity volleyCallBackCity, String name) {
        CityDAO cityDAO = daoFactory.getCityDAO(ConfigFileReader.getProperty(CITY_STORAGE_TECHNOLOGY,
                restaurantMapActivity.getApplicationContext()));
        cityDAO.findCitiesByName(volleyCallBackCity, name, restaurantMapActivity.getApplicationContext());
    }

    public void findTypeOfCuisineHelper(VolleyCallBack volleyCallBack) {
        TypeOfCuisineDAO typeOfCuisineDAO = daoFactory.getTypeOfCuisineDAO(ConfigFileReader.getProperty(CITY_STORAGE_TECHNOLOGY,
                restaurantMapActivity.getApplicationContext()));
        typeOfCuisineDAO.getAll(volleyCallBack, restaurantMapActivity.getApplicationContext());
    }

    private void findByRsql(List<String> typesOfCuisine, PointSearch pointSearch, String rsqlQuery) {
        findByRsqlHelper(new VolleyCallBack() {
            @Override
            public void onSuccess(Object object) {
                setMapOnSuccess((List<Restaurant>) object);
            }

            @Override
            public void onError(String errorCode) {
                if (errorCode.equals("204"))
                    showToastNoResults();
            }

        }, typesOfCuisine, pointSearch, rsqlQuery, getContext(), 0, 10000);
    }

    private void findByNameLikeIgnoreCase(String name) {
        findByNameLikeIgnoreCaseHelper(new VolleyCallBack() {
            @Override
            public void onSuccess(Object object) {
                setMapOnSuccess((List<Restaurant>) object);
            }

            @Override
            public void onError(String errorCode) {

            }
        }, name, getContext(), 0, 10000);
    }

    private void findRestarantsName(String newText) {
        if (!newText.equals("")) {
            disableFieldsOnAutoCompleteTextViewNameChanged();
            findRestaurantsNameHelper(new VolleyCallBack() {
                @Override
                public void onSuccess(Object object) {
                    setAutoCompleteTextViewHotelNameAdapter((List<String>) object);
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
                    setAutoCompleteTextViewHotelCityAdapter((List<String>) object);
                }

                @Override
                public void onError(String error) {

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
        if (isBottomSheetFilterRestaurantsVisible())
            bottomSheetFilterRestaurants.dismiss();
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
        accomodationRestaurantFilter = new AccomodationRestaurantFilter();
        accomodationRestaurantFilter.setName(getRestaurantNameValue());
        accomodationRestaurantFilter.setCity(getCityNameValue());
        accomodationRestaurantFilter.setAvaragePrice(getPriceValue());
        accomodationRestaurantFilter.setAvarageRating(getRatingValue());
        accomodationRestaurantFilter.setDistance(getDistanceValue());
        accomodationRestaurantFilter.setTypesOfCuisine(getMultiSpinnerSearchSelectedItems());
        accomodationRestaurantFilter.setHasCertificateOfExcellence(getCertificateOfExcellence());
    }

    private void detectSearchType() {
        if (!isSearchingForName())
            findByRsql(isTypesOfCuisineListSelected() ? getAccomodationFilterTypesOfCuisine() : null,
                    isSearchingForCity() ? createNullPointSearch() : createPointSearch(),
                    !createRsqlString().equals("") ? createRsqlString() : "0");
        else
            findByNameLikeIgnoreCase(bottomSheetFilterRestaurants.getAutoCompleteTextViewNameValue());
    }

    private String getRestaurantNameValue() {
        return bottomSheetFilterRestaurants.getAutoCompleteTextViewNameValue();
    }

    private String getCityNameValue() {
        return extractCityName(bottomSheetFilterRestaurants.getAutoCompleteTextViewCityValue());
    }

    private Integer getPriceValue() {
        return bottomSheetFilterRestaurants.getSeekBarPriceValue();
    }

    private Integer getRatingValue() {
        return bottomSheetFilterRestaurants.getSeekBarRatingValue();
    }

    private Double getDistanceValue() {
        if (bottomSheetFilterRestaurants.getSeekBarDistanceValue() != 0)
            return (double) bottomSheetFilterRestaurants.getSeekBarDistanceValue();
        else
            return 1d;
    }

    private boolean getCertificateOfExcellence() {
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

    private List<String> getMultiSpinnerSearchSelectedItems() {
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
        System.out.println("RSQLSTRING = " + rsqlString);
        return rsqlString;
    }

    private PointSearch createPointSearch() {
        PointSearch pointSearch = getPointSearch();
        pointSearch.setDistance(isAccomodationFilterNull() ? 5d : checkDistanceValue());
        return pointSearch;
    }

    private PointSearch createNullPointSearch() {
        PointSearch pointSearch = new PointSearch();
        pointSearch.setLatitude(0d);
        pointSearch.setLongitude(0d);
        pointSearch.setDistance(0d);
        return pointSearch;
    }

    private void setAutoCompleteTextViewHotelNameAdapter(List<String> hotelsName) {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(restaurantMapActivity.getApplicationContext(),
                android.R.layout.select_dialog_item, hotelsName);
        bottomSheetFilterRestaurants.getAutoCompleteTextViewName().setAdapter(arrayAdapter);
    }

    private void setAutoCompleteTextViewHotelCityAdapter(List<String> citiesName) {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(restaurantMapActivity.getApplicationContext(),
                android.R.layout.select_dialog_item, citiesName);
        bottomSheetFilterRestaurants.getAutoCompleteTextViewCity().setAdapter(arrayAdapter);
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
            markers.add(restaurantMapActivity.getGoogleMap().addMarker(createMarkerOptions(restaurant)));
    }

    private void clearAllMarkersOnMap() {
        restaurantMapActivity.getGoogleMap().clear();
        markers = new ArrayList<>();
    }

    private MarkerOptions createMarkerOptions(Restaurant restaurant) {
        return new MarkerOptions()
                .position(new LatLng(restaurant.getPoint().getX(), restaurant.getPoint().getY()))
                .icon(setCustomMarker(restaurantMapActivity.getApplicationContext(), R.drawable.restaurant_marker))
                .title(restaurant.getName());
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
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        restaurantMapActivity.getGoogleMap().animateCamera(cu);
    }

    @SuppressLint("MissingPermission")
    public void setMapProperties() {
        restaurantMapActivity.getGoogleMap().getUiSettings().setMyLocationButtonEnabled(false);
        restaurantMapActivity.getGoogleMap().getUiSettings().setMapToolbarEnabled(false);
        restaurantMapActivity.getGoogleMap().setMyLocationEnabled(true);
        restaurantMapActivity.getGoogleMap().setOnMarkerClickListener(this);
        restaurantMapActivity.getGoogleMap().setOnMapClickListener(this);
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
        setRelativeLayoutHotelInformationHotelFields(marker);
    }

    private void setRelativeLayoutHotelInformationVisible() {
        isRelativeLayoutRestaurantInformationVisible = true;
        restaurantMapActivity.getRelativeLayoutRestaurantInformation().setVisibility(View.VISIBLE);
        restaurantMapActivity.getRelativeLayoutRestaurantInformation().animate().translationY(0);
    }

    private void setRelativeLayoutHotelInformationInvisible() {
        isRelativeLayoutRestaurantInformationVisible = false;
        restaurantMapActivity.getRelativeLayoutRestaurantInformation().animate().translationY(restaurantMapActivity.
                getRelativeLayoutRestaurantInformation().getHeight() + 100);
    }

    private void setRelativeLayoutHotelInformationHotelFields(Marker marker) {
        restaurant = getRestaurantFromMarkerClick(marker.getTitle());
        //setHotelImage(hotel);
        restaurantMapActivity.getTextViewRestaurantName().setText(restaurant.getName());
        restaurantMapActivity.getTextViewRestaurantRating().setText(createReviewString(restaurant.getAvarageRating()));
        restaurantMapActivity.getTextViewRestaurantAddress().setText(createAddressString(restaurant.getAddress()));
    }

    private void setRestaurantImage(Hotel hotel) {
        if (hasImage(hotel))
            Picasso.with(restaurantMapActivity.getApplicationContext())
                    .load(hotel.getImages().get(0))
                    .placeholder(R.drawable.troppadvisor_logo)
                    .into(restaurantMapActivity.getImageViewRestaurant());
        else
            restaurantMapActivity.getImageViewRestaurant().setImageDrawable(null);
    }

    private boolean hasImage(Hotel hotel) {
        return hotel.getImages().size() > 0;
    }

    private String createAddressString(Address address) {
        String hotelAddress = "";
        hotelAddress = hotelAddress.concat(address.getType() + " ");
        hotelAddress = hotelAddress.concat(address.getStreet() + ", ");
        hotelAddress = hotelAddress.concat(address.getHouseNumber() + ", ");
        hotelAddress = hotelAddress.concat(address.getCity() + ", ");
        hotelAddress = hotelAddress.concat(address.getProvince() + ", ");
        hotelAddress = hotelAddress.concat(address.getPostalCode());
        return hotelAddress;
    }

    private String createReviewString(Integer review) {
        if (review.equals(0))
            return restaurantMapActivity.getResources().getString(R.string.no_review);
        else {
            String rating = "";
            rating = rating.concat(review + "/5");
            return rating;
        }
    }

    private Restaurant getRestaurantFromMarkerClick(String restaurantId) {
        Restaurant restaurantToReturn = null;
        for (Restaurant restaurant : restaurants)
            if (restaurant.getName().equals(restaurantId)) {
                restaurantToReturn = restaurant;
                break;
            }
        return restaurantToReturn;
    }

    private void setLinearLayoutSearchRestaurantsVisible() {
        isLinearLayoutSearchRestaurantsVisible = true;
        restaurantMapActivity.getLinearLayoutSearchRestaurants().animate().translationY(0);
    }

    private void setLinearLayoutSearchRestaurantsInvisible() {
        isLinearLayoutSearchRestaurantsVisible = false;
        restaurantMapActivity.getLinearLayoutSearchRestaurants().animate().translationY(-restaurantMapActivity.
                getLinearLayoutSearchRestaurants().getHeight() - 100);
    }

    private void setFloatingActionButtonCenterPositionOnRestaurantsVisible() {
        isFloatingActionButtonCenterPositionOnRestaurantsVisible = true;
        restaurantMapActivity.getFloatingActionButtonCenterPositionOnRestaurants().show();
    }

    private void setFloatingActionButtonCenterPositionOnRestaurantsInvisible() {
        isFloatingActionButtonCenterPositionOnRestaurantsVisible = false;
        restaurantMapActivity.getFloatingActionButtonCenterPositionOnRestaurants().hide();
    }

    private void showBottomSheetMapFilters() {
        bottomSheetFilterRestaurants = new BottomSheetFilterRestaurants();
        bottomSheetFilterRestaurants.show(restaurantMapActivity.getSupportFragmentManager(), bottomSheetFilterRestaurants.getTag());
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
        bottomSheetFilterRestaurants.getAutoCompleteTextViewName().setText(getAccomodationFilterNameValue());
        bottomSheetFilterRestaurants.getAutoCompleteTextViewCity().setText(getAccomodationFilterCityValue());
        bottomSheetFilterRestaurants.getSeekBarPrice().setProgress(getAccomodationFilterAvaragePriceValue());
        bottomSheetFilterRestaurants.getSeekBarRating().setProgress(getAccomodationFilterAvarageRatingValue());
        bottomSheetFilterRestaurants.getSeekBarDistance().setProgress(getAccomodationFilterDistanceValue().intValue());
        if (isSearchingForCity() || isSearchingForName())
            bottomSheetFilterRestaurants.setSeekBarDistanceEnabled(false);
        bottomSheetFilterRestaurants.getSwitchCompatCertificateOfExcellence().setChecked(accomodationRestaurantFilter.isHasCertificateOfExcellence());
    }

    private void showToastNoResults() {
        restaurantMapActivity.runOnUiThread(() -> {
            Toast.makeText(restaurantMapActivity, restaurantMapActivity.getResources().getString(R.string.no_hotels_found_by_filter), Toast.LENGTH_SHORT).show();
        });
    }

    public void setComponentProperties() {
        restaurantMapActivity.getRelativeLayoutRestaurantInformation().animate().translationY(restaurantMapActivity.
                getRelativeLayoutRestaurantInformation().getHeight() + 100);
    }

    public void setListenerOnViewComponents() {
        restaurantMapActivity.getTextViewSearchRestaurantsOnMap().setOnClickListener(this);
        restaurantMapActivity.getImageViewRestaurantMapGoBack().setOnClickListener(this);
        restaurantMapActivity.getFloatingActionButtonCenterPositionOnRestaurants().setOnClickListener(this);
    }

    private Context getContext() {
        return restaurantMapActivity.getApplicationContext();
    }

    private Intent getIntent(){
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

    private AccomodationRestaurantFilter getAccomodationRestaurantFilter(){
        return (AccomodationRestaurantFilter) getIntent().getSerializableExtra(ACCOMODATION_FILTER);
    }

    private String getRestaurantName() {
        return restaurantMapActivity.getIntent().getStringExtra(NAME);
    }

    private boolean isAccomodationFilterNull() {
        return accomodationRestaurantFilter == null;
    }

    private String getAccomodationFilterNameValue() {
        return accomodationRestaurantFilter.getName();
    }

    private String getAccomodationFilterCityValue() {
        return accomodationRestaurantFilter.getCity();
    }

    private Integer getAccomodationFilterAvaragePriceValue() {
        return accomodationRestaurantFilter.getAvaragePrice();
    }

    private Integer getAccomodationFilterAvarageRatingValue() {
        return accomodationRestaurantFilter.getAvarageRating();
    }

    private Double getAccomodationFilterDistanceValue() {
        return accomodationRestaurantFilter.getDistance();
    }

    private List<String> getAccomodationFilterTypesOfCuisine() {
        return accomodationRestaurantFilter.getTypesOfCuisine();
    }

    private boolean getAccomodationFilterHasCertificateOfExcellenceValue() {
        return accomodationRestaurantFilter.isHasCertificateOfExcellence();
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
        return accomodationRestaurantFilter != null && getAccomodationFilterTypesOfCuisine() != null && getAccomodationFilterTypesOfCuisine().size() != 0;
    }

    private boolean isAccomodationFilterHasCertificateOfExcellence() {
        return getAccomodationFilterHasCertificateOfExcellenceValue();
    }

    private boolean isBottomSheetFilterRestaurantsVisible() {
        return bottomSheetFilterRestaurants != null && bottomSheetFilterRestaurants.isAdded();
    }

    private boolean isTypesOfCuisineEmpty() {
        return typesOfCuisine.isEmpty();
    }

}
