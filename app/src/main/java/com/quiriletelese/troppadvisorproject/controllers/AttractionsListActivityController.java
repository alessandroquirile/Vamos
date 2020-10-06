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

import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.adapters.BottomSheetFilterAttractions;
import com.quiriletelese.troppadvisorproject.adapters.RecyclerViewAttractionsListAdapter;
import com.quiriletelese.troppadvisorproject.adapters.RecyclerViewHotelsListAdapter;
import com.quiriletelese.troppadvisorproject.dao_interfaces.AttractionDAO;
import com.quiriletelese.troppadvisorproject.dao_interfaces.CityDAO;
import com.quiriletelese.troppadvisorproject.factories.DAOFactory;
import com.quiriletelese.troppadvisorproject.interfaces.AutoCompleteTextViewsAccomodationFilterTextChangeListener;
import com.quiriletelese.troppadvisorproject.interfaces.Constants;
import com.quiriletelese.troppadvisorproject.interfaces.OnBottomSheetFilterSearchButtonClick;
import com.quiriletelese.troppadvisorproject.model_helpers.AccomodationAttractionFilter;
import com.quiriletelese.troppadvisorproject.model_helpers.PointSearch;
import com.quiriletelese.troppadvisorproject.models.Attraction;
import com.quiriletelese.troppadvisorproject.models.Hotel;
import com.quiriletelese.troppadvisorproject.utils.ConfigFileReader;
import com.quiriletelese.troppadvisorproject.views.AttractionMapActivity;
import com.quiriletelese.troppadvisorproject.views.AttractionsListActivity;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBack;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBackCity;

import java.util.List;

public class AttractionsListActivityController implements OnBottomSheetFilterSearchButtonClick,
        AutoCompleteTextViewsAccomodationFilterTextChangeListener, Constants {

    private AttractionsListActivity attractionsListActivity;
    private BottomSheetFilterAttractions bottomSheetFilterAttractions = new BottomSheetFilterAttractions();
    private AccomodationAttractionFilter accomodationAttractionFilter;
    private RecyclerViewAttractionsListAdapter recyclerViewAttractionsListAdapter;
    private DAOFactory daoFactory = DAOFactory.getInstance();
    private int page = 0, size = 3;
    private boolean isLoadingData = false, isPointSearchNull = false;

    public AttractionsListActivityController(AttractionsListActivity attractionsListActivity) {
        this.attractionsListActivity = attractionsListActivity;
    }

    @Override
    public void OnBottomSheetFilterSearchButtonClick() {
        onBottomSheetFilterSearchButtonClickHelper();
    }

    @Override
    public void onAutoCompleteTextViewAccomodationNameTextChanged(String newText) {
        findHotelsName(newText);
    }

    @Override
    public void onAutoCompleteTextViewAccomodtionCityTextChanged(String newText) {
        findCitiesName(newText);
    }

    private void findByRsqlHelper(VolleyCallBack volleyCallBack, PointSearch pointSearch, String rsqlQuery) {
        AttractionDAO attractionDAO = getAttractionDAO();
        attractionDAO.findByRsql(volleyCallBack, pointSearch, rsqlQuery, getContext(), page, size);
    }

    private void findByNameLikeIgnoreCaseHelper(VolleyCallBack volleyCallBack, String name) {
        AttractionDAO attractionDAO = getAttractionDAO();
        attractionDAO.findByNameLikeIgnoreCase(volleyCallBack, name, getContext(), page, size);
    }

    public void findAttractionsNameHelper(VolleyCallBack volleyCallBack, String name) {
        AttractionDAO attractionDAO = getAttractionDAO();
        attractionDAO.findHotelsName(volleyCallBack, name, getContext());
    }

    public void findCitiesNameHelper(VolleyCallBackCity volleyCallBackCity, String name) {
        CityDAO cityDAO = getCityDAO();
        cityDAO.findCitiesByName(volleyCallBackCity, name, getContext());
    }

    public void findByRsql(PointSearch pointSearch, String rsqlQuery) {
        findByRsqlHelper(new VolleyCallBack() {
            @Override
            public void onSuccess(Object object) {
                volleyCallbackOnSuccess(object);
            }

            @Override
            public void onError(String errorCode) {
                volleyCallbackOnError(errorCode);
            }

        }, pointSearch, rsqlQuery);
    }

    private void findByNameLikeIgnoreCase() {
        findByNameLikeIgnoreCaseHelper(new VolleyCallBack() {
            @Override
            public void onSuccess(Object object) {
                initializeRecyclerViewOnSuccess((List<Attraction>) object);
            }

            @Override
            public void onError(String errorCode) {
                volleyCallbackOnError(errorCode);
            }
        }, getAccomodationFilterNameValue());
    }

    private void findHotelsName(String newText) {
        if (!newText.equals("")) {
            disableFieldsOnAutoCompleteTextViewNameChanged();
            findAttractionsNameHelper(new VolleyCallBack() {
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
                        loadMoreAttractions();
            }
        });
    }

    private boolean isScrollingDown(int dy) {
        return dy > 0;
    }

    private boolean isScrolledToLastItem(RecyclerView recyclerView) {
        return !recyclerView.canScrollVertically(1);
    }

    private void loadMoreAttractions() {
        page += 1;
        isLoadingData = true;
        setProgressBarVisibilityOnUiThred(View.VISIBLE);
        detectSearchType();
    }

    private void onBottomSheetFilterSearchButtonClickHelper() {
        isLoadingData = false;
        page = 0;
        createAccomodationFilter();
        detectSearchType();
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
        if (isSearchingForCity() || isSearchingForName())
            bottomSheetFilterAttractions.setSeekBarDistanceEnabled(false);
        bottomSheetFilterAttractions.setSwitchCompatCertificateOfExcellenceChecked(isAccomodationFilterHasCertificateOfExcellence());
    }

    private void detectSearchType() {
        if (!isAccomodationFilterNull()) {
            if (!isSearchingForName()) {
                findByRsql(isSearchingForCity() ? null : createPointSearch(),
                        isRsqlEmpty() ? "0" : createRsqlString());
            } else
                findByNameLikeIgnoreCase();
        } else
            findByRsql(createPointSearch(), "0");
    }

    private void volleyCallbackOnSuccess(Object object) {
        List<Attraction> attractions = (List<Attraction>) object;
        if (isLoadingData)
            addNewAttractionsToList(attractions);
        else
            initializeRecyclerViewOnSuccess(attractions);
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
            showToastVolleyError(R.string.no_attractions_found_by_filter);
    }

    private void handleOtherVolleyError() {
        showToastVolleyError(R.string.unexpected_error_while_fetch_data);
    }

    private void initializeRecyclerViewOnSuccess(List<Attraction> attractions) {
        dismissBottomSheetFilterAttractions();
        recyclerViewAttractionsListAdapter = createRecyclerViewAdapter(attractions);
        attractionsListActivity.getRecyclerViewAttractionsList().setLayoutManager(createLinearLayoutManager());
        attractionsListActivity.getRecyclerViewAttractionsList().setAdapter(recyclerViewAttractionsListAdapter);
        attractionsListActivity.getRecyclerViewAttractionsList().smoothScrollToPosition(0);
        recyclerViewAttractionsListAdapter.notifyDataSetChanged();
    }

    private RecyclerViewAttractionsListAdapter createRecyclerViewAdapter(List<Attraction> attractions) {
        return new RecyclerViewAttractionsListAdapter(getContext(), attractions);
    }

    private LinearLayoutManager createLinearLayoutManager() {
        return new LinearLayoutManager(getContext(), setRecyclerViewVerticalOrientation(), false);
    }

    private int setRecyclerViewVerticalOrientation() {
        return RecyclerView.VERTICAL;
    }

    private void addNewAttractionsToList(List<Attraction> attractions) {
        recyclerViewAttractionsListAdapter.addListItems(attractions);
        recyclerViewAttractionsListAdapter.notifyDataSetChanged();
        setProgressBarVisibilityOnUiThred(View.INVISIBLE);
    }

    public void showBottomSheetFilters() {
        bottomSheetFilterAttractions.show(getSupportFragmentManager(), getBottomSheetFilterTag());
        setBottomSheetFiltersFields();
        bottomSheetFilterAttractions.setOnBottomSheetFilterSearchButtonClick(this);
        bottomSheetFilterAttractions.setAutoCompleteTextViewsAccomodationFilterTextChangeListener(this);
    }

    private PointSearch createPointSearch() {
        isPointSearchNull = false;
        PointSearch pointSearch = getPointSearch();
        pointSearch.setDistance(isAccomodationFilterNull() ? 5d : checkDistanceValue());
        return pointSearch;
    }

    private void setAutoCompleteTextViewHotelNameAdapter(List<String> attractionsNames) {
        ArrayAdapter<String> arrayAdapter = createAutoCompleteTextViewAdapter(attractionsNames);
        bottomSheetFilterAttractions.getAutoCompleteTextViewName().setAdapter(arrayAdapter);
    }

    private void setAutoCompleteTextViewHotelCityAdapter(List<String> citiesNames) {
        ArrayAdapter<String> arrayAdapter = createAutoCompleteTextViewAdapter(citiesNames);
        bottomSheetFilterAttractions.getAutoCompleteTextViewCity().setAdapter(arrayAdapter);
    }

    private ArrayAdapter<String> createAutoCompleteTextViewAdapter(List<String> content) {
        return new ArrayAdapter<>(getContext(), getAutoCompleteTextViewAdapterLayout(), content);
    }

    private int getAutoCompleteTextViewAdapterLayout() {
        return android.R.layout.select_dialog_item;
    }

    private void dismissBottomSheetFilterAttractions() {
        if (isBottomSheetFilterHotelsVisible())
            bottomSheetFilterAttractions.dismiss();
    }

    private void showToastVolleyError(int string) {
        setIsLoadingData(false);
        setProgressBarVisibilityOnUiThred(View.INVISIBLE);
        runToastOnUiThred(string);
    }

    private void runToastOnUiThred(int string) {
        attractionsListActivity.runOnUiThread(() -> {
            Toast.makeText(attractionsListActivity, getString(string), Toast.LENGTH_SHORT).show();
        });
    }

    private void setProgressBarVisibilityOnUiThred(int visibility) {
        attractionsListActivity.runOnUiThread(() -> {
            getProgressBarLoadMore().setVisibility(visibility);
        });
    }

    public void startMapsActivity() {
        Intent attractionsMapActivityIntent = new Intent(getContext(), AttractionMapActivity.class);
        putPointSearch(attractionsMapActivityIntent);
        putRsqlQuery(attractionsMapActivityIntent);
        putAttractionName(attractionsMapActivityIntent);
        putAccomodationHotelFilter(attractionsMapActivityIntent);
        attractionsListActivity.startActivity(attractionsMapActivityIntent);
    }

    private void putPointSearch(Intent attractionMapsActivityIntent) {
        attractionMapsActivityIntent.putExtra(POINT_SEARCH, isPointSearchNull ? null : createPointSearch());
    }

    private void putRsqlQuery(Intent attractionMapsActivityIntent) {
        if (!isAccomodationFilterNull())
            attractionMapsActivityIntent.putExtra(RSQL_QUERY, isRsqlEmpty() ? "0" : createRsqlString());
        else
            attractionMapsActivityIntent.putExtra(RSQL_QUERY, "0");
    }

    private void putAttractionName(Intent attractionMapsActivityIntent) {
        if (!isAccomodationFilterNull())
            if (isSearchingForName())
                attractionMapsActivityIntent.putExtra(NAME, getAccomodationFilterNameValue());
    }

    private void putAccomodationHotelFilter(Intent attractionMapsActivityIntent) {
        attractionMapsActivityIntent.putExtra(ACCOMODATION_FILTER, isAccomodationFilterNull() ? null
                : accomodationAttractionFilter);
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
        if (bottomSheetFilterAttractions.getSeekBarDistanceValue() != 0)
            return (double) bottomSheetFilterAttractions.getSeekBarDistanceValue();
        else
            return 1d;
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
        return rsqlString;
    }

    private ProgressBar getProgressBarLoadMore() {
        return attractionsListActivity.getProgressBarAttractionLoadMore();
    }

    private Context getContext() {
        return attractionsListActivity.getApplicationContext();
    }

    private RecyclerView getRecyclerView() {
        return attractionsListActivity.getRecyclerViewAttractionsList();
    }

    private boolean isRsqlEmpty(){
        return createRsqlString().equals("");
    }

    private FragmentManager getSupportFragmentManager() {
        return attractionsListActivity.getSupportFragmentManager();
    }

    private AttractionDAO getAttractionDAO(){
        return daoFactory.getAttractionDAO(getStorageTechnology(ATTRACTION_STORAGE_TECHNOLOGY));
    }

    private CityDAO getCityDAO() {
        return daoFactory.getCityDAO(getStorageTechnology(CITY_STORAGE_TECHNOLOGY));
    }

    private String getStorageTechnology(String storageTechnology) {
        return ConfigFileReader.getProperty(storageTechnology, getContext());
    }

    private String getBottomSheetFilterTag() {
        return bottomSheetFilterAttractions.getTag();
    }

    private Resources getResources() {
        return attractionsListActivity.getResources();
    }

    private String getString(int string) {
        return getResources().getString(string);
    }

    private Intent getIntent() {
        return attractionsListActivity.getIntent();
    }

    public PointSearch getPointSearch() {
        return (PointSearch) getIntent().getSerializableExtra(POINT_SEARCH);
    }

    private boolean isBottomSheetFilterHotelsVisible() {
        return bottomSheetFilterAttractions.isAdded();
    }

    private void setIsLoadingData(boolean value) {
        isLoadingData = value;
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

    private boolean isSearchingForName() {
        return !getAccomodationFilterNameValue().equals("");
    }

    private boolean isSearchingForCity() {
        return !getAccomodationFilterCityValue().equals("");
    }

    private boolean isBottomSheetFilterHotelsNull() {
        return bottomSheetFilterAttractions == null;
    }

    private boolean isAccomodationFilterNull() {
        return accomodationAttractionFilter == null;
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

}
