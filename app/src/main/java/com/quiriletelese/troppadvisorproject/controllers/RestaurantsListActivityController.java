package com.quiriletelese.troppadvisorproject.controllers;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidbuts.multispinnerfilter.KeyPairBoolData;
import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.adapters.BottomSheetFilterRestaurants;
import com.quiriletelese.troppadvisorproject.adapters.RecyclerViewRestaurantsListAdapter;
import com.quiriletelese.troppadvisorproject.dao_interfaces.CityDAO;
import com.quiriletelese.troppadvisorproject.dao_interfaces.RestaurantDAO;
import com.quiriletelese.troppadvisorproject.dao_interfaces.TypeOfCuisineDAO;
import com.quiriletelese.troppadvisorproject.factories.DAOFactory;
import com.quiriletelese.troppadvisorproject.interfaces.AutoCompleteTextViewsAccomodationFilterTextChangeListener;
import com.quiriletelese.troppadvisorproject.interfaces.Constants;
import com.quiriletelese.troppadvisorproject.interfaces.OnBottomSheetFilterSearchButtonClick;
import com.quiriletelese.troppadvisorproject.model_helpers.AccomodationRestaurantFilter;
import com.quiriletelese.troppadvisorproject.model_helpers.PointSearch;
import com.quiriletelese.troppadvisorproject.models.Restaurant;
import com.quiriletelese.troppadvisorproject.utils.ConfigFileReader;
import com.quiriletelese.troppadvisorproject.views.RestaurantMapActivity;
import com.quiriletelese.troppadvisorproject.views.RestaurantsListActivity;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBack;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBackCity;

import java.util.ArrayList;
import java.util.List;

public class RestaurantsListActivityController implements OnBottomSheetFilterSearchButtonClick,
        AutoCompleteTextViewsAccomodationFilterTextChangeListener, Constants {

    private RestaurantsListActivity restaurantsListActivity;
    private BottomSheetFilterRestaurants bottomSheetFilterRestaurants;
    private AccomodationRestaurantFilter accomodationRestaurantFilter;
    private DAOFactory daoFactory = DAOFactory.getInstance();
    private RecyclerViewRestaurantsListAdapter recyclerViewRestaurantsListAdapter;
    private List<String> typesOfCuisine = new ArrayList<>();
    private int page = 0, size = 3;
    private boolean isLoadingData = false, isPointSearchNull = false;

    public RestaurantsListActivityController(RestaurantsListActivity restaurantsListActivity) {
        this.restaurantsListActivity = restaurantsListActivity;
    }

    @Override
    public void OnBottomSheetFilterSearchButtonClick() {
        onBottomSheetFilterSearchButtonClickHelper();
    }

    @Override
    public void onAutoCompleteTextViewAccomodationNameTextChanged(String newText) {
        findRestaurantsName(newText);
    }

    @Override
    public void onAutoCompleteTextViewAccomodtionCityTextChanged(String newText) {
        findCitiesName(newText);
    }

    private void findByRsqlHelper(VolleyCallBack volleyCallBack, List<String> typesOfCuisine, PointSearch pointSearch,
                                  String rsqlQuery) {
        RestaurantDAO restaurantDAO = daoFactory.getRestaurantDAO(getStorageTechnology(RESTAURANT_STORAGE_TECHNOLOGY));
        restaurantDAO.findByRsql(volleyCallBack, typesOfCuisine, pointSearch, rsqlQuery, getContext(), page, size);
    }

    private void findByNameLikeIgnoreCaseHelper(VolleyCallBack volleyCallBack, String name) {
        RestaurantDAO restaurantDAO = daoFactory.getRestaurantDAO(getStorageTechnology(RESTAURANT_STORAGE_TECHNOLOGY));
        restaurantDAO.findByNameLikeIgnoreCase(volleyCallBack, name, getContext(), page, size);
    }

    public void findRestaurantsNameHelper(VolleyCallBack volleyCallBack, String name) {
        RestaurantDAO restaurantDAO = daoFactory.getRestaurantDAO(getStorageTechnology(RESTAURANT_STORAGE_TECHNOLOGY));
        restaurantDAO.findRestaurantsName(volleyCallBack, name, getContext());
    }

    public void findCitiesNameHelper(VolleyCallBackCity volleyCallBackCity, String name) {
        CityDAO cityDAO = daoFactory.getCityDAO(getStorageTechnology(CITY_STORAGE_TECHNOLOGY));
        cityDAO.findCitiesByName(volleyCallBackCity, name, getContext());
    }

    public void findTypeOfCuisineHelper(VolleyCallBack volleyCallBack) {
        TypeOfCuisineDAO typeOfCuisineDAO = daoFactory.getTypeOfCuisineDAO(getStorageTechnology(TYPES_OF_CUISINE_TECHNOLOGY));
        typeOfCuisineDAO.getAll(volleyCallBack, getContext());
    }

    public void findByRsql(List<String> typesOfCuisine, PointSearch pointSearch, String rsqlQuery) {
        findByRsqlHelper(new VolleyCallBack() {
            @Override
            public void onSuccess(Object object) {
                volleyCallbackOnSuccess(object);
            }

            @Override
            public void onError(String errorCode) {
                if (errorCode.equals("204"))
                    showToastNoResults();
            }

        }, typesOfCuisine, pointSearch, rsqlQuery);
    }

    private void findByNameLikeIgnoreCase() {
        findByNameLikeIgnoreCaseHelper(new VolleyCallBack() {
            @Override
            public void onSuccess(Object object) {
                volleyCallbackOnSuccess(object);
            }

            @Override
            public void onError(String errorCode) {
                volleyCallbackOnError(errorCode);
            }
        }, getAccomodationFilterNameValue());
    }

    private void findRestaurantsName(String newText) {
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
                    volleyCallbackOnError(error);
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

    public void addRecyclerViewOnScrollListener() {
        getRecyclerView().addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isScrollingDown(dy))
                    if (isScrolledToLastItem(recyclerView))
                        loadMoreRestaurants();
            }
        });
    }

    private void volleyCallbackOnSuccess(Object object) {
        List<Restaurant> restaurants = (List<Restaurant>) object;
        if (isLoadingData) {
            addNewRestaurantsToList(restaurants);
        } else {
            initializeRecyclerViewOnSuccess(restaurants);
        }
        setProgressBarLoadMoreInvisible();
    }

    private void volleyCallbackOnError(String errorCode) {
        if (errorCode.equals("204")) {
            if (isLoadingData)
                showToastNoMoreResults();
            else
                showToastNoResults();
        }
    }

    private void loadMoreRestaurants() {
        page += 1;
        setIsLoadingData(true);
        setProgressBarLoadMoreVisible();
        detectSearchType();
    }

    private void onBottomSheetFilterSearchButtonClickHelper() {
        page = 0;
        setIsLoadingData(false);
        createAccomodationFilter();
        detectSearchType();
    }

    private void createAccomodationFilter() {
        accomodationRestaurantFilter = new AccomodationRestaurantFilter();
        accomodationRestaurantFilter.setName(getRestaurantNameValueFromBottomSheetFilter());
        accomodationRestaurantFilter.setCity(getCityNameValueFromBottomSheetFilter());
        accomodationRestaurantFilter.setAvaragePrice(getPriceValueFromBottomSheetFilter());
        accomodationRestaurantFilter.setAvarageRating(getRatingValueFromBottomSheetFilter());
        accomodationRestaurantFilter.setDistance(getDistanceValueFromBottomSheetFilter());
        accomodationRestaurantFilter.setTypesOfCuisine(getMultiSpinnerSearchSelectedItems());
        accomodationRestaurantFilter.setHasCertificateOfExcellence(getCertificateOfExcellenceFromBottomSheetFilter());
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
        if (!isBottomSheetFilterRestaurantsNull() && !isAccomodationFilterNull()) {
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
        bottomSheetFilterRestaurants.setSwitchCompatCertificateOfExcellenceChecked(isAccomodationFilterHasCertificateOfExcellence());
    }

    private void detectSearchType() {
        if (!isAccomodationFilterNull()) {
            if (!isSearchingForName()) {
                findByRsql(isTypesOfCuisineListSelected() ? getAccomodationFilterTypesOfCuisine() : null,
                        isSearchingForCity() ? createNullPointSearch() : createPointSearch(), !createRsqlString().equals("") ? createRsqlString() : "0");
            } else
                findByNameLikeIgnoreCase();
        } else
            findByRsql(null,
                    createPointSearch(), "0");
    }

    private void setAutoCompleteTextViewHotelNameAdapter(List<String> hotelsName) {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(restaurantsListActivity.getApplicationContext(),
                android.R.layout.select_dialog_item, hotelsName);
        bottomSheetFilterRestaurants.getAutoCompleteTextViewName().setAdapter(arrayAdapter);
    }

    private void setAutoCompleteTextViewHotelCityAdapter(List<String> citiesName) {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(restaurantsListActivity.getApplicationContext(),
                android.R.layout.select_dialog_item, citiesName);
        bottomSheetFilterRestaurants.getAutoCompleteTextViewCity().setAdapter(arrayAdapter);
    }

    public void showBottomSheetFilters() {
        bottomSheetFilterRestaurants = new BottomSheetFilterRestaurants();
        bottomSheetFilterRestaurants.show(restaurantsListActivity.getSupportFragmentManager(), bottomSheetFilterRestaurants.getTag());
        setBottomSheetFiltersFields();
        setTypesOfCuisine();
        bottomSheetFilterRestaurants.setOnBottomSheetFilterSearchButtonClick(this);
        bottomSheetFilterRestaurants.setAutoCompleteTextViewsAccomodationFilterTextChangeListener(this);
    }

    private void initializeRecyclerViewOnSuccess(List<Restaurant> restaurants) {
        dismissBottomSheetFilterRestaurants();
        LinearLayoutManager linearLayoutManager = createLinearLayoutManager();
        recyclerViewRestaurantsListAdapter = createRecyclerViewHotelsListAdapter(restaurants);
        getRecyclerView().setLayoutManager(linearLayoutManager);
        getRecyclerView().setAdapter(recyclerViewRestaurantsListAdapter);
        getRecyclerView().smoothScrollToPosition(0);
        recyclerViewRestaurantsListAdapter.notifyDataSetChanged();
    }

    private LinearLayoutManager createLinearLayoutManager(){
        return new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
    }

    private RecyclerViewRestaurantsListAdapter createRecyclerViewHotelsListAdapter(List<Restaurant> restaurants){
        return new RecyclerViewRestaurantsListAdapter(getContext(), restaurants);
    }

    private void addNewRestaurantsToList(List<Restaurant> restaurants) {
        recyclerViewRestaurantsListAdapter.addListItems(restaurants);
        recyclerViewRestaurantsListAdapter.notifyDataSetChanged();
        setProgressBarLoadMoreInvisible();
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
        if (bottomSheetFilterRestaurants.getSeekBarDistanceValue() != 0)
            return (double) bottomSheetFilterRestaurants.getSeekBarDistanceValue();
        else
            return 1d;
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
        isPointSearchNull = false;
        PointSearch pointSearch = getPointSearch();
        pointSearch.setDistance(isAccomodationFilterNull() ? 5d : checkDistanceValue());
        return pointSearch;
    }

    private PointSearch createNullPointSearch() {
        isPointSearchNull = true;
        PointSearch pointSearch = new PointSearch();
        pointSearch.setLatitude(0d);
        pointSearch.setLongitude(0d);
        pointSearch.setDistance(0d);
        return pointSearch;
    }

    private void dismissBottomSheetFilterRestaurants() {
        if (isBottomSheetFilterRestaurantsVisible())
            bottomSheetFilterRestaurants.dismiss();
    }

    private void showToastNoMoreResults() {
        setIsLoadingData(false);
        setProgressBarLoadMoreInvisible();
        Toast.makeText(restaurantsListActivity, restaurantsListActivity.getResources().getString(R.string.end_of_results), Toast.LENGTH_SHORT).show();
    }

    private void showToastNoResults() {
        setProgressBarLoadMoreInvisible();
        restaurantsListActivity.runOnUiThread(() -> {
            Toast.makeText(restaurantsListActivity, restaurantsListActivity.getResources().getString(R.string.no_restaurants_found_by_filter), Toast.LENGTH_SHORT).show();
        });
    }

    private void setProgressBarLoadMoreVisible() {
        ProgressBar progressBar = restaurantsListActivity.getProgressBarRestaurantLoadMore();
        progressBar.setVisibility(View.VISIBLE);
    }

    private void setProgressBarLoadMoreInvisible() {
        ProgressBar progressBar = restaurantsListActivity.getProgressBarRestaurantLoadMore();
        progressBar.setVisibility(View.GONE);
    }

    public void startRestaurantMapActivity() {
        Intent restaurantMapActivityIntent = new Intent(getContext(), RestaurantMapActivity.class);
        putPointSearch(restaurantMapActivityIntent);
        putRsqlQuery(restaurantMapActivityIntent);
        putResturantName(restaurantMapActivityIntent);
        putAccomodationRestaurantFilter(restaurantMapActivityIntent);
        restaurantsListActivity.startActivity(restaurantMapActivityIntent);
    }

    private void putPointSearch(Intent restaurantMapActivityIntent) {
        restaurantMapActivityIntent.putExtra(POINT_SEARCH, isPointSearchNull ? createNullPointSearch()
                : createPointSearch());
    }

    private void putRsqlQuery(Intent restaurantMapActivityIntent) {
        if (!isAccomodationFilterNull())
            restaurantMapActivityIntent.putExtra(RSQL_QUERY, createRsqlString().equals("") ? "0"
                    : createRsqlString());
        else
            restaurantMapActivityIntent.putExtra(RSQL_QUERY, "0");
    }

    private void putResturantName(Intent restaurantMapActivityIntent) {
        if (!isAccomodationFilterNull()) {
            if (isSearchingForName()) {
                restaurantMapActivityIntent.putExtra(SEARCH_FOR_NAME, true);
                restaurantMapActivityIntent.putExtra(NAME, getAccomodationFilterNameValue());
            } else
                restaurantMapActivityIntent.putExtra(SEARCH_FOR_NAME, false);
        }
    }

    private void putAccomodationRestaurantFilter(Intent restaurantMapActivityIntent) {
            restaurantMapActivityIntent.putExtra(ACCOMODATION_FILTER, isAccomodationFilterNull() ? null
                    : accomodationRestaurantFilter);
    }

    public PointSearch getPointSearch() {
        return (PointSearch) restaurantsListActivity.getIntent().getSerializableExtra(POINT_SEARCH);
    }

    private boolean isBottomSheetFilterRestaurantsVisible() {
        return bottomSheetFilterRestaurants != null && bottomSheetFilterRestaurants.isAdded();
    }

    private void setIsLoadingData(boolean value) {
        isLoadingData = value;
    }

    private boolean isScrollingDown(int dy) {
        return dy > 0;
    }

    private boolean isScrolledToLastItem(RecyclerView recyclerView) {
        return !recyclerView.canScrollVertically(1);
    }

    private String getStorageTechnology(String storageTechnology){
        return ConfigFileReader.getProperty(storageTechnology, getContext());
    }

    private Context getContext(){
        return restaurantsListActivity.getApplicationContext();
    }

    private RecyclerView getRecyclerView(){
        return restaurantsListActivity.getRecyclerViewRestaurantsList();
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

    private boolean isBottomSheetFilterRestaurantsNull(){
        return bottomSheetFilterRestaurants == null;
    }

    private boolean isAccomodationFilterNull() {
        return accomodationRestaurantFilter == null;
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

    private boolean isAccomodationFilterTypesOfCuisineNull() {
        return getAccomodationFilterTypesOfCuisine() == null;
    }

    private boolean isAccomodationFilterTypesOfCuisineEmpty() {
        return getAccomodationFilterTypesOfCuisine().size() == 0;
    }

    private boolean isTypesOfCuisineListSelected() {
        return !isAccomodationFilterNull() && !isAccomodationFilterTypesOfCuisineNull() && !isAccomodationFilterTypesOfCuisineEmpty();
    }

    private boolean isAccomodationFilterHasCertificateOfExcellence() {
        return getAccomodationFilterHasCertificateOfExcellenceValue();
    }

    private boolean isTypesOfCuisineEmpty() {
        return typesOfCuisine.isEmpty();
    }

}
