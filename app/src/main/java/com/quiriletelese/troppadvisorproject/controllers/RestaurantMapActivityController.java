package com.quiriletelese.troppadvisorproject.controllers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
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
import com.quiriletelese.troppadvisorproject.adapters.BottomSheetRestaurantMap;
import com.quiriletelese.troppadvisorproject.dao_interfaces.CityDAO;
import com.quiriletelese.troppadvisorproject.dao_interfaces.RestaurantDAO;
import com.quiriletelese.troppadvisorproject.dao_interfaces.TypeOfCuisineDAO;
import com.quiriletelese.troppadvisorproject.factories.DAOFactory;
import com.quiriletelese.troppadvisorproject.interfaces.AutoCompleteTextViewsAccomodationMapTextChangeListener;
import com.quiriletelese.troppadvisorproject.interfaces.Constants;
import com.quiriletelese.troppadvisorproject.interfaces.OnImageViewSearchMapFilterClick;
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
        View.OnClickListener, OnImageViewSearchMapFilterClick, AutoCompleteTextViewsAccomodationMapTextChangeListener, Constants {

    private RestaurantMapActivity restaurantMapActivity;
    private BottomSheetRestaurantMap bottomSheetRestaurantMap;
    private DAOFactory daoFactory;
    private Restaurant restaurant = null;
    private List<Restaurant> restaurants = new ArrayList<>();
    private List<String> typeOfCuisine = new ArrayList<>();
    private ArrayList<Marker> markers = new ArrayList<>();
    boolean isRelativeLayoutRestaurantInformationVisible = false, isLinearLayoutSearchRestaurantsVisible = true,
            isFloatingActionButtonCenterPositionOnRestaurantsVisible = true;

    public RestaurantMapActivityController(RestaurantMapActivity restaurantMapActivity) {
        this.restaurantMapActivity = restaurantMapActivity;
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
    public void onImageViewSearchHotelMapFilterClick() {
        onImageViewSearchHotelMapFilterClickHelper();
    }

    @Override
    public void onAutoCompleteTextViewAccomodationNameTextChanged(String newText) {
        findHotelsNameHelper(newText);
    }

    @Override
    public void onAutoCompleteTextViewAccomodtionCityTextChanged(final String newText) {
        findCitiesNameHelper(newText);
    }

    private void findByRsql(VolleyCallBack volleyCallBack, PointSearch pointSearch, String rsqlQuery,
                            Context context, int page, int size) {
        daoFactory = DAOFactory.getInstance();
        RestaurantDAO restaurantDAO = daoFactory.getRestaurantDAO(ConfigFileReader.getProperty(HOTEL_STORAGE_TECHNOLOGY,
                restaurantMapActivity.getApplicationContext()));
        restaurantDAO.findByRsql(volleyCallBack, pointSearch, rsqlQuery, context, page, size);
    }

    private void findByRsqlNoPoint(VolleyCallBack volleyCallBack, String rsqlQuery, Context context,
                                   int page, int size) {
        daoFactory = DAOFactory.getInstance();
        RestaurantDAO restaurantDAO = daoFactory.getRestaurantDAO(ConfigFileReader.getProperty(HOTEL_STORAGE_TECHNOLOGY,
                restaurantMapActivity.getApplicationContext()));
        restaurantDAO.findByRsqlNoPoint(volleyCallBack, rsqlQuery, context, page, size);
    }

    private void findByNameLikeIgnoreCase(VolleyCallBack volleyCallBack, String name, Context context,
                                          int page, int size) {
        daoFactory = DAOFactory.getInstance();
        RestaurantDAO restaurantDAO = daoFactory.getRestaurantDAO(ConfigFileReader.getProperty(HOTEL_STORAGE_TECHNOLOGY,
                restaurantMapActivity.getApplicationContext()));
        restaurantDAO.findByNameLikeIgnoreCase(volleyCallBack, name, context, page, size);
    }

    public void findAllByPointNear(VolleyCallBack volleyCallBack, PointSearch pointSearch) {
        daoFactory = DAOFactory.getInstance();
        RestaurantDAO restaurantDAO = daoFactory.getRestaurantDAO(ConfigFileReader.getProperty(HOTEL_STORAGE_TECHNOLOGY,
                restaurantMapActivity.getApplicationContext()));
        restaurantDAO.findAllByPointNear(volleyCallBack, pointSearch, restaurantMapActivity.getApplicationContext());
    }

    public void findHotelsName(VolleyCallBack volleyCallBack, String name) {
        daoFactory = DAOFactory.getInstance();
        RestaurantDAO restaurantDAO = daoFactory.getRestaurantDAO(ConfigFileReader.getProperty(HOTEL_STORAGE_TECHNOLOGY,
                restaurantMapActivity.getApplicationContext()));
        restaurantDAO.findRestaurantsName(volleyCallBack, name, restaurantMapActivity.getApplicationContext());
    }

    public void findCitiesName(VolleyCallBackCity volleyCallBackCity, String name) {
        daoFactory = DAOFactory.getInstance();
        CityDAO cityDAO = daoFactory.getCityDAO(ConfigFileReader.getProperty(CITY_STORAGE_TECHNOLOGY,
                restaurantMapActivity.getApplicationContext()));
        cityDAO.findCitiesByName(volleyCallBackCity, name, restaurantMapActivity.getApplicationContext());
    }

    public void findTypeOfCuisine(VolleyCallBack volleyCallBack) {
        daoFactory = DAOFactory.getInstance();
        TypeOfCuisineDAO typeOfCuisineDAO = daoFactory.getTypeOfCuisineDAO(ConfigFileReader.getProperty(CITY_STORAGE_TECHNOLOGY,
                restaurantMapActivity.getApplicationContext()));
        typeOfCuisineDAO.getAll(volleyCallBack, restaurantMapActivity.getApplicationContext());
    }

    private void onImageViewSearchHotelMapFilterClickHelper() {
        if (!isSearchingForName()) {
            if (isSearchingForCity()) {
                findByRsqlNoPointHelper();
            } else {
                if (isRsqlValuesCorrectlySetted())
                    findByRsqlHelper();
                else
                    findAllByPointNearHelper(checkDistanceValue());
            }
        } else
            findByNameLikeIgnoreCaseHelper();
    }

    private void findByRsqlHelper() {
        PointSearch pointSearch = (PointSearch) restaurantMapActivity.getIntent().getSerializableExtra(POINT_SEARCH);
        pointSearch.setDistance(checkDistanceValue());
        findByRsql(new VolleyCallBack() {
            @Override
            public void onSuccess(Object object) {
                setMapOnFindByRsqlSuccess((List<Restaurant>) object);
            }

            @Override
            public void onError(String errorCode) {
                if (errorCode.equals("204"))
                    showToastNoResults();
            }

        }, pointSearch, createRsqlString(), restaurantMapActivity.getApplicationContext(), 0, 10000);
    }

    private void findByRsqlNoPointHelper() {
        findByRsqlNoPoint(new VolleyCallBack() {
            @Override
            public void onSuccess(Object object) {
                setMapOnFindByRsqlSuccess((List<Restaurant>) object);
            }

            @Override
            public void onError(String errorCode) {
                if (errorCode.equals("204"))
                    showToastNoResults();
            }
        }, createRsqlString(), restaurantMapActivity.getApplicationContext(), 0, 10000);
    }

    private void findAllByPointNearHelper(Double distance) {
        PointSearch pointSearch = getPointSearch();
        pointSearch.setDistance(distance);
        findAllByPointNear(new VolleyCallBack() {
            @Override
            public void onSuccess(Object object) {
                setMapOnFindByRsqlSuccess((List<Restaurant>) object);
                /*restaurants = (List<Restaurant>) object;
                if (bottomSheetRestaurantMap != null && bottomSheetRestaurantMap.isAdded())
                    bottomSheetRestaurantMap.dismiss();
                addMarkersOnSuccess(restaurants);
                zoomOnMap();*/
            }

            @Override
            public void onError(String errorCode) {
                if (errorCode.equals("204"))
                    showToastNoResults();
                showToastNoResults();
            }
        }, pointSearch);
    }

    private void findByNameLikeIgnoreCaseHelper() {
        findByNameLikeIgnoreCase(new VolleyCallBack() {
            @Override
            public void onSuccess(Object object) {
                restaurants = (List<Restaurant>) object;
                bottomSheetRestaurantMap.dismiss();
                addMarkersOnSuccess(restaurants);
                zoomOnMap();
            }

            @Override
            public void onError(String errorCode) {

            }
        }, bottomSheetRestaurantMap.getAutoCompleteTextViewMapRestaurantNameValue(), restaurantMapActivity
                .getApplicationContext(), 0, 10000);
    }

    private void findHotelsNameHelper(String newText) {
        if (!newText.equals(""))
            findHotelsName(new VolleyCallBack() {
                @Override
                public void onSuccess(Object object) {
                    setAutoCompleteTextViewHotelNameAdapter((List<String>) object);
                }

                @Override
                public void onError(String errorCode) {

                }
            }, newText);
    }

    private void findCitiesNameHelper(String newText) {
        if (!newText.equals("")) {
            bottomSheetRestaurantMap.getSeekBarRestaurantMapDistance().setEnabled(false);
            findCitiesName(new VolleyCallBackCity() {
                @Override
                public void onSuccess(Object object) {
                    setAutoCompleteTextViewHotelCityAdapter((List<String>) object);
                }

                @Override
                public void onError(String error) {

                }
            }, newText);
        } else
            bottomSheetRestaurantMap.getSeekBarRestaurantMapDistance().setEnabled(true);
    }

    private void setMapOnFindByRsqlSuccess(List<Restaurant> restaurants) {
        this.restaurants = restaurants;
        if (bottomSheetRestaurantMap != null && bottomSheetRestaurantMap.isAdded())
            if (getMultiSpinnerSearchSelectedItems().size() != 0)
                filterByTypeOfCuisine();
        if (this.restaurants.size() != 0) {
            if (bottomSheetRestaurantMap != null && bottomSheetRestaurantMap.isAdded())
                bottomSheetRestaurantMap.dismiss();
            addMarkersOnSuccess(restaurants);
            zoomOnMap();
        } else
            showToastNoResults();
    }

    private void filterByTypeOfCuisine() {
        List<String> typeOfCuisine = getMultiSpinnerSearchSelectedItems();
        List<Restaurant> restaurantsWithSelectedTypeOfCuisine = new ArrayList<>();
        for (Restaurant restaurant : restaurants)
            if (!restaurant.getTypeOfCuisine().containsAll(typeOfCuisine))
                restaurantsWithSelectedTypeOfCuisine.add(restaurant);
        //restaurants = restaurantsWithSelectedTypeOfCuisine;
        restaurants.removeAll(restaurantsWithSelectedTypeOfCuisine);
    }

    private String checkCityNameValue(String rsqlString) {
        if (!bottomSheetRestaurantMap.getAutoCompleteTextViewMapRestaurantCityValue().equals("")) {
            String cityName = extractCityName(bottomSheetRestaurantMap.getAutoCompleteTextViewMapRestaurantCityValue());
            rsqlString = rsqlString.concat("address.city==" + cityName + ";");
        }
        return rsqlString;
    }

    private String extractCityName(String city) {
        return city.substring(0, city.lastIndexOf(","));
    }

    private String checkPriceValue(String rsqlString) {
        if (bottomSheetRestaurantMap.getSeekBarRestaurantMapPriceValue() != 0) {
            if (bottomSheetRestaurantMap.getSeekBarRestaurantMapPriceValue() == 150)
                rsqlString = rsqlString.concat("avaragePrice=ge=" + bottomSheetRestaurantMap.getSeekBarRestaurantMapPriceValue() + ";");
            else
                rsqlString = rsqlString.concat("avaragePrice=le=" + bottomSheetRestaurantMap.getSeekBarRestaurantMapPriceValue() + ";");
        }
        return rsqlString;
    }

    private String checkRatingValue(String rsqlString) {
        if (bottomSheetRestaurantMap.getSeekBarRestaurantMapRatingValue() != 0)
            rsqlString = rsqlString.concat("avarageRating=ge=" + bottomSheetRestaurantMap.getSeekBarRestaurantMapRatingValue() + ";");
        return rsqlString;
    }

    private Double checkDistanceValue() {
        double distance = 1.0;
        if (bottomSheetRestaurantMap.getSeekBarRestaurantMapDistanceValue() != 0)
            distance = bottomSheetRestaurantMap.getSeekBarRestaurantMapDistanceValue();
        return distance;
    }

    private String checkCertificateOfExcellence(String rsqlString) {
        if (bottomSheetRestaurantMap.getSwitchCompatRestaurantMapCertificateOfExcellenceIsSelected())
            rsqlString = rsqlString.concat("certificateOfExcellence==" + "true;");
        return rsqlString;
    }

    private List<String> getMultiSpinnerSearchSelectedItems() {
        List<String> typeOfCuisine = new ArrayList<>();
        List<KeyPairBoolData> keyPairBoolDataList = bottomSheetRestaurantMap.getMultiSpinnerSearchSelectedItems();
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
        return rsqlString;
    }

    private void setAutoCompleteTextViewHotelNameAdapter(List<String> hotelsName) {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(restaurantMapActivity.getApplicationContext(),
                android.R.layout.select_dialog_item, hotelsName);
        bottomSheetRestaurantMap.getAutoCompleteTextViewMapRestaurantName().setAdapter(arrayAdapter);
    }

    private void setAutoCompleteTextViewHotelCityAdapter(List<String> citiesName) {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(restaurantMapActivity.getApplicationContext(),
                android.R.layout.select_dialog_item, citiesName);
        bottomSheetRestaurantMap.getAutoCompleteTextViewMapRestaurantCity().setAdapter(arrayAdapter);
    }

    public void addMarkersOnMap() {
        findAllByPointNearHelper(2.0);
    }

    private void addMarkersOnSuccess(List<Restaurant> restaurants) {
        clearAllMarkerOnMap();
        for (Restaurant restaurant : restaurants)
            markers.add(restaurantMapActivity.getGoogleMap().addMarker(createMarkerOptions(restaurant)));
    }

    private void clearAllMarkerOnMap() {
        restaurantMapActivity.getGoogleMap().clear();
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
        bottomSheetRestaurantMap = new BottomSheetRestaurantMap();
        bottomSheetRestaurantMap.show(restaurantMapActivity.getSupportFragmentManager(), bottomSheetRestaurantMap.getTag());
        bottomSheetRestaurantMap.setOnImageViewSearchMapFilterClick(this);
        bottomSheetRestaurantMap.setAutoCompleteTextViewsAccomodationMapTextChangeListener(this);
        findTypeOfCuisine();
    }

    private void findTypeOfCuisine() {
        findTypeOfCuisine(new VolleyCallBack() {
            @Override
            public void onSuccess(Object object) {
                bottomSheetRestaurantMap.setTypeOfCuisineList((List<String>) object);
            }

            @Override
            public void onError(String errorCode) {

            }
        });
    }

    private void showToastNoResults() {
        restaurantMapActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(restaurantMapActivity, restaurantMapActivity.getResources().getString(R.string.no_hotels_found_by_filter), Toast.LENGTH_SHORT).show();
            }
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

    private PointSearch getPointSearch() {
        return (PointSearch) restaurantMapActivity.getIntent().getSerializableExtra(POINT_SEARCH);
    }

    private boolean isSearchingForName() {
        return !bottomSheetRestaurantMap.getAutoCompleteTextViewMapRestaurantNameValue().equals("");
    }

    private boolean isSearchingForCity() {
        return !bottomSheetRestaurantMap.getAutoCompleteTextViewMapRestaurantCityValue().equals("");
    }

    private boolean isRsqlValuesCorrectlySetted() {
        return !createRsqlString().equals("");
    }


}
