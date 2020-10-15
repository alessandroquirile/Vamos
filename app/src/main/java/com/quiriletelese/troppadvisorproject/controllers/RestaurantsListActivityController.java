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
import com.quiriletelese.troppadvisorproject.interfaces.BottomSheetFilterSearchButtonClick;
import com.quiriletelese.troppadvisorproject.interfaces.Constants;
import com.quiriletelese.troppadvisorproject.model_helpers.PointSearch;
import com.quiriletelese.troppadvisorproject.model_helpers.RestaurantFilter;
import com.quiriletelese.troppadvisorproject.models.Restaurant;
import com.quiriletelese.troppadvisorproject.utils.ConfigFileReader;
import com.quiriletelese.troppadvisorproject.views.RestaurantMapActivity;
import com.quiriletelese.troppadvisorproject.views.RestaurantsListActivity;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBack;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alessandro Quirile, Mauro Telese
 */

public class RestaurantsListActivityController implements BottomSheetFilterSearchButtonClick,
        AutoCompleteTextViewsAccomodationFilterTextChangeListener, Constants {

    private RestaurantsListActivity restaurantsListActivity;
    private BottomSheetFilterRestaurants bottomSheetFilterRestaurants = new BottomSheetFilterRestaurants();
    private RestaurantFilter restaurantFilter;
    private RecyclerViewRestaurantsListAdapter recyclerViewRestaurantsListAdapter;
    private DAOFactory daoFactory = DAOFactory.getInstance();
    private List<String> typesOfCuisine = new ArrayList<>();
    private int page = 0, size = 30;
    private boolean isLoadingData = false;
    private boolean isPointSearchNull = false;

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
        }, getRestaurantFilterNameValue());
    }

    private void findRestaurantsName(@NotNull String newText) {
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

    private void findCitiesName(@NotNull String newText) {
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
        createRestaurantFilter();
        detectSearchType();
    }

    private void createRestaurantFilter() {
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
        if (!isRestaurantFilterNull())
            bottomSheetFilterRestaurants.setSelectedItems(getRestaurantFilterTypesOfCuisine());
    }

    private void setBottomSheetFiltersFields() {
        if (!isBottomSheetFilterRestaurantsNull() && !isRestaurantFilterNull()) {
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
        bottomSheetFilterRestaurants.setSwitchCompatCertificateOfExcellenceChecked(isRestaurantFilterHasCertificateOfExcellence());
    }

    private void detectSearchType() {
        if (!isRestaurantFilterNull()) {
            if (!isSearchingForName()) {
                findByRsql(isTypesOfCuisineListSelected() ? getRestaurantFilterTypesOfCuisine() : null,
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

    @NotNull
    @Contract("_ -> new")
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
        bottomSheetFilterRestaurants.setBottomSheetFilterSearchButtonClick(this);
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

    @NotNull
    @Contract("_ -> new")
    private RecyclerViewRestaurantsListAdapter createRecyclerViewAdapter(List<Restaurant> restaurants) {
        return new RecyclerViewRestaurantsListAdapter(getContext(), restaurants);
    }

    @NotNull
    @Contract(" -> new")
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
        return bottomSheetFilterRestaurants.getSeekBarDistanceValue() != 0 ? (double) bottomSheetFilterRestaurants.
                getSeekBarDistanceValue() : 1d;
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
        return city.contains(",") ? city.substring(0, city.lastIndexOf(",")) : city;
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
    private List<String> getMultiSpinnerSearchSelectedItems() {
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
        System.out.println("RSQLSTRING = " + rsqlString);
        return rsqlString;
    }

    @NotNull
    private PointSearch createPointSearch() {
        isPointSearchNull = false;
        PointSearch pointSearch = getPointSearch();
        pointSearch.setDistance(isRestaurantFilterNull() ? 5d : checkDistanceValue());
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

    private void showToastOnUiThred(int string) {
        restaurantsListActivity.runOnUiThread(() ->
                Toast.makeText(restaurantsListActivity, getString(string), Toast.LENGTH_SHORT).show());
    }

    private void setProgressBarVisibilityOnUiThred(int visibility){
        restaurantsListActivity.runOnUiThread(() -> getProgressBarLoadMore().setVisibility(visibility));
    }

    public void startRestaurantMapActivity() {
        Intent restaurantMapActivityIntent = new Intent(getContext(), RestaurantMapActivity.class);
        putPointSearch(restaurantMapActivityIntent);
        putRsqlQuery(restaurantMapActivityIntent);
        putResturantName(restaurantMapActivityIntent);
        putRestaurantFilter(restaurantMapActivityIntent);
        restaurantsListActivity.startActivity(restaurantMapActivityIntent);
    }

    private void putPointSearch(@NotNull Intent restaurantMapActivityIntent) {
        restaurantMapActivityIntent.putExtra(POINT_SEARCH, isPointSearchNull ? null : createPointSearch());
    }

    private void putRsqlQuery(Intent restaurantMapActivityIntent) {
        if (!isRestaurantFilterNull())
            restaurantMapActivityIntent.putExtra(RSQL_QUERY, isRsqlEmpty() ? "0" : createRsqlString());
        else
            restaurantMapActivityIntent.putExtra(RSQL_QUERY, "0");
    }

    private void putResturantName(Intent restaurantMapActivityIntent) {
        if (!isRestaurantFilterNull()) {
            if (isSearchingForName()) {
                restaurantMapActivityIntent.putExtra(SEARCH_FOR_NAME, true);
                restaurantMapActivityIntent.putExtra(NAME, getRestaurantFilterNameValue());
            } else
                restaurantMapActivityIntent.putExtra(SEARCH_FOR_NAME, false);
        }
    }

    private void putRestaurantFilter(@NotNull Intent restaurantMapActivityIntent) {
        restaurantMapActivityIntent.putExtra(ACCOMODATION_FILTER, isRestaurantFilterNull() ? null
                : restaurantFilter);
    }

    private ProgressBar getProgressBarLoadMore(){
        return restaurantsListActivity.getProgressBarRestaurantLoadMore();
    }

    private Resources getResources() {
        return restaurantsListActivity.getResources();
    }

    @NotNull
    private String getString(int string){
        return getResources().getString(string);
    }

    public PointSearch getPointSearch() {
        return (PointSearch) restaurantsListActivity.getIntent().getSerializableExtra(POINT_SEARCH);
    }

    @NotNull
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

    private boolean isScrolledToLastItem(@NotNull RecyclerView recyclerView) {
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

    private boolean isSearchingForName() {
        return !getRestaurantFilterNameValue().equals("");
    }

    private boolean isSearchingForCity() {
        return !getRestaurantFilterCityValue().equals("");
    }

    private boolean isBottomSheetFilterRestaurantsNull() {
        return bottomSheetFilterRestaurants == null;
    }

    private boolean isRestaurantFilterNull() {
        return restaurantFilter == null;
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

    private boolean isRestaurantFilterTypesOfCuisineNull() {
        return getRestaurantFilterTypesOfCuisine() == null;
    }

    private boolean isRestaurantFilterTypesOfCuisineEmpty() {
        return getRestaurantFilterTypesOfCuisine().size() == 0;
    }

    private boolean isTypesOfCuisineListSelected() {
        return !isRestaurantFilterNull() && !isRestaurantFilterTypesOfCuisineNull() &&
                !isRestaurantFilterTypesOfCuisineEmpty();
    }

    private boolean isRestaurantFilterHasCertificateOfExcellence() {
        return getRestaurantFilterHasCertificateOfExcellenceValue();
    }

    private boolean isTypesOfCuisineEmpty() {
        return typesOfCuisine.isEmpty();
    }

}
