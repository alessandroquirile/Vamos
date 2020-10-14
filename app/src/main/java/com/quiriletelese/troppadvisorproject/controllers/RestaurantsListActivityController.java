package com.quiriletelese.troppadvisorproject.controllers;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
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
import com.quiriletelese.troppadvisorproject.model_helpers.RestaurantFilter;
import com.quiriletelese.troppadvisorproject.model_helpers.PointSearch;
import com.quiriletelese.troppadvisorproject.models.Restaurant;
import com.quiriletelese.troppadvisorproject.utils.ConfigFileReader;
import com.quiriletelese.troppadvisorproject.views.RestaurantMapActivity;
import com.quiriletelese.troppadvisorproject.views.RestaurantsListActivity;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBack;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alessandro Quirile, Mauro Telese
 */

public class RestaurantsListActivityController implements OnBottomSheetFilterSearchButtonClick,
        AutoCompleteTextViewsAccomodationFilterTextChangeListener, Constants {

    private RestaurantsListActivity restaurantsListActivity;
    private BottomSheetFilterRestaurants bottomSheetFilterRestaurants = new BottomSheetFilterRestaurants();
    private RestaurantFilter restaurantFilter;
    private RecyclerViewRestaurantsListAdapter recyclerViewRestaurantsListAdapter;
    private DAOFactory daoFactory = DAOFactory.getInstance();
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
        RestaurantDAO restaurantDAO = getRestaurantDAO();
        restaurantDAO.findByRsql(volleyCallBack, typesOfCuisine, pointSearch, rsqlQuery, getContext(), page, size);
    }

    private void findByNameLikeIgnoreCaseHelper(VolleyCallBack volleyCallBack, String name) {
        RestaurantDAO restaurantDAO = getRestaurantDAO();
        restaurantDAO.findByNameLikeIgnoreCase(volleyCallBack, name, getContext(), page, size);
    }

    public void findRestaurantsNameHelper(VolleyCallBack volleyCallBack, String name) {
        RestaurantDAO restaurantDAO = getRestaurantDAO();
        restaurantDAO.findRestaurantsName(volleyCallBack, name, getContext());
    }

    public void findCitiesNameHelper(VolleyCallBack volleyCallBack, String name) {
        CityDAO cityDAO = getCityDAO();
        cityDAO.findCitiesByName(volleyCallBack, name, getContext());
    }

    public void findTypeOfCuisineHelper(VolleyCallBack volleyCallBack) {
        TypeOfCuisineDAO typeOfCuisineDAO = getTypeOfCuisineDAO();
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
                    volleyCallbackOnError(errorCode);
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
        if (isLoadingData)
            addNewRestaurantsToList(restaurants);
        else
            initializeRecyclerViewOnSuccess(restaurants);
        setProgressBarVisibilityOnUiThred(View.INVISIBLE);
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
        if (isLoadingData)
            showToastVolleyError(R.string.end_of_results);
        else
            showToastVolleyError(R.string.no_restaurants_found_by_filter);
    }

    private void handleOtherVolleyError(){
        showToastVolleyError(R.string.unexpected_error_while_fetch_data);
    }

    private void loadMoreRestaurants() {
        page += 1;
        setIsLoadingData(true);
        setProgressBarVisibilityOnUiThred(View.VISIBLE);
        detectSearchType();
    }

    private void onBottomSheetFilterSearchButtonClickHelper() {
        page = 0;
        setIsLoadingData(false);
        createAccomodationFilter();
        detectSearchType();
    }

    private void createAccomodationFilter() {
        restaurantFilter = new RestaurantFilter();
        restaurantFilter.setName(getRestaurantNameValueFromBottomSheetFilter());
        restaurantFilter.setCity(getCityNameValueFromBottomSheetFilter());
        restaurantFilter.setAvaragePrice(getPriceValueFromBottomSheetFilter());
        restaurantFilter.setAvarageRating(getRatingValueFromBottomSheetFilter());
        restaurantFilter.setDistance(getDistanceValueFromBottomSheetFilter());
        restaurantFilter.setTypesOfCuisine(getMultiSpinnerSearchSelectedItems());
        restaurantFilter.setHasCertificateOfExcellence(getCertificateOfExcellenceFromBottomSheetFilter());
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
                        isSearchingForCity() ? null : createPointSearch(),
                        isRsqlEmpty() ? "0" : createRsqlString());
            } else
                findByNameLikeIgnoreCase();
        } else
            findByRsql(null,
                    createPointSearch(), "0");
    }

    private void setAutoCompleteTextViewNameAdapter(List<String> restaurantsNames) {
        ArrayAdapter<String> arrayAdapter = createAutoCompleteTextViewAdapter(restaurantsNames);
        bottomSheetFilterRestaurants.setAutoCompleteTextViewNameAdapter(arrayAdapter);
    }

    private void setAutoCompleteTextViewCityAdapter(List<String> citiesNames) {
        ArrayAdapter<String> arrayAdapter = createAutoCompleteTextViewAdapter(citiesNames);
        bottomSheetFilterRestaurants.setAutoCompleteTextViewCityAdapter(arrayAdapter);
    }

    private ArrayAdapter<String> createAutoCompleteTextViewAdapter(List<String> content) {
        return new ArrayAdapter<>(getContext(), getAutoCompleteTextViewAdapterLayout(), content);
    }

    private int getAutoCompleteTextViewAdapterLayout() {
        return android.R.layout.select_dialog_item;
    }

    public void showBottomSheetFilters() {
        bottomSheetFilterRestaurants.show(getSupportFragmentManager(), getBottomSheetFilterTag());
        setBottomSheetFiltersFields();
        setTypesOfCuisine();
        bottomSheetFilterRestaurants.setOnBottomSheetFilterSearchButtonClick(this);
        bottomSheetFilterRestaurants.setAutoCompleteTextViewsAccomodationFilterTextChangeListener(this);
    }

    private void initializeRecyclerViewOnSuccess(List<Restaurant> restaurants) {
        dismissBottomSheetFilterRestaurants();
        recyclerViewRestaurantsListAdapter = createRecyclerViewAdapter(restaurants);
        getRecyclerView().setLayoutManager(createLinearLayoutManager());
        getRecyclerView().setAdapter(recyclerViewRestaurantsListAdapter);
        getRecyclerView().smoothScrollToPosition(0);
        recyclerViewRestaurantsListAdapter.notifyDataSetChanged();
    }

    private RecyclerViewRestaurantsListAdapter createRecyclerViewAdapter(List<Restaurant> restaurants) {
        return new RecyclerViewRestaurantsListAdapter(getContext(), restaurants);
    }

    private LinearLayoutManager createLinearLayoutManager() {
        return new LinearLayoutManager(getContext(), setRecyclerViewVerticalOrientation(), false);
    }

    private int setRecyclerViewVerticalOrientation() {
        return RecyclerView.VERTICAL;
    }

    private void addNewRestaurantsToList(List<Restaurant> restaurants) {
        recyclerViewRestaurantsListAdapter.addListItems(restaurants);
        recyclerViewRestaurantsListAdapter.notifyDataSetChanged();
        setProgressBarVisibilityOnUiThred(View.INVISIBLE);
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
        return bottomSheetFilterRestaurants.getSeekBarDistanceValue() != 0 ? (double) bottomSheetFilterRestaurants.
                getSeekBarDistanceValue() : 1d;
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

    private void dismissBottomSheetFilterRestaurants() {
        if (isBottomSheetFilterRestaurantsVisible())
            bottomSheetFilterRestaurants.dismiss();
    }

    private void showToastVolleyError(int string) {
        setIsLoadingData(false);
        setProgressBarVisibilityOnUiThred(View.INVISIBLE);
        showToastOnUiThred(string);
    }

    private void showToastOnUiThred(int string){
        restaurantsListActivity.runOnUiThread(() -> {
            Toast.makeText(restaurantsListActivity, getString(string), Toast.LENGTH_SHORT).show();
        });
    }

    private void setProgressBarVisibilityOnUiThred(int visibility){
        restaurantsListActivity.runOnUiThread(() -> {
            getProgressBarLoadMore().setVisibility(visibility);
        });
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
        restaurantMapActivityIntent.putExtra(POINT_SEARCH, isPointSearchNull ? null : createPointSearch());
    }

    private void putRsqlQuery(Intent restaurantMapActivityIntent) {
        if (!isAccomodationFilterNull())
            restaurantMapActivityIntent.putExtra(RSQL_QUERY, isRsqlEmpty() ? "0" : createRsqlString());
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
                : restaurantFilter);
    }

    private ProgressBar getProgressBarLoadMore(){
        return restaurantsListActivity.getProgressBarRestaurantLoadMore();
    }

    private Resources getResources() {
        return restaurantsListActivity.getResources();
    }

    private String getString(int string){
        return getResources().getString(string);
    }

    public PointSearch getPointSearch() {
        return (PointSearch) restaurantsListActivity.getIntent().getSerializableExtra(POINT_SEARCH);
    }

    private FragmentManager getSupportFragmentManager() {
        return restaurantsListActivity.getSupportFragmentManager();
    }

    private boolean isRsqlEmpty(){
        return createRsqlString().equals("");
    }

    private String getBottomSheetFilterTag() {
        return bottomSheetFilterRestaurants.getTag();
    }

    private boolean isBottomSheetFilterRestaurantsVisible() {
        return bottomSheetFilterRestaurants.isAdded();
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

    private RestaurantDAO getRestaurantDAO(){
        return daoFactory.getRestaurantDAO(getStorageTechnology(RESTAURANT_STORAGE_TECHNOLOGY));
    }

    private CityDAO getCityDAO() {
        return daoFactory.getCityDAO(getStorageTechnology(CITY_STORAGE_TECHNOLOGY));
    }

    private TypeOfCuisineDAO getTypeOfCuisineDAO(){
        return  daoFactory.getTypeOfCuisineDAO(getStorageTechnology(TYPES_OF_CUISINE_STORAGE_TECHNOLOGY));
    }

    private String getStorageTechnology(String storageTechnology) {
        return ConfigFileReader.getProperty(storageTechnology, getContext());
    }

    private Context getContext() {
        return restaurantsListActivity.getApplicationContext();
    }

    private RecyclerView getRecyclerView() {
        return restaurantsListActivity.getRecyclerViewRestaurantsList();
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

    private boolean isSearchingForName() {
        return !getAccomodationFilterNameValue().equals("");
    }

    private boolean isSearchingForCity() {
        return !getAccomodationFilterCityValue().equals("");
    }

    private boolean isBottomSheetFilterRestaurantsNull() {
        return bottomSheetFilterRestaurants == null;
    }

    private boolean isAccomodationFilterNull() {
        return restaurantFilter == null;
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

    private boolean isBottomSheetFilterDistanceEqualsToZero() {
        return getAccomodationFilterDistanceValue().equals(0d);
    }

    private boolean isAccomodationFilterTypesOfCuisineNull() {
        return getAccomodationFilterTypesOfCuisine() == null;
    }

    private boolean isAccomodationFilterTypesOfCuisineEmpty() {
        return getAccomodationFilterTypesOfCuisine().size() == 0;
    }

    private boolean isTypesOfCuisineListSelected() {
        return !isAccomodationFilterNull() && !isAccomodationFilterTypesOfCuisineNull() &&
                !isAccomodationFilterTypesOfCuisineEmpty();
    }

    private boolean isAccomodationFilterHasCertificateOfExcellence() {
        return getAccomodationFilterHasCertificateOfExcellenceValue();
    }

    private boolean isTypesOfCuisineEmpty() {
        return typesOfCuisine.isEmpty();
    }

}
