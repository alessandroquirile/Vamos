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
import com.quiriletelese.troppadvisorproject.dao_interfaces.AttractionDAO;
import com.quiriletelese.troppadvisorproject.dao_interfaces.CityDAO;
import com.quiriletelese.troppadvisorproject.factories.DAOFactory;
import com.quiriletelese.troppadvisorproject.interfaces.AutoCompleteTextViewsAccomodationFilterTextChangeListener;
import com.quiriletelese.troppadvisorproject.interfaces.Constants;
import com.quiriletelese.troppadvisorproject.interfaces.OnBottomSheetFilterSearchButtonClick;
import com.quiriletelese.troppadvisorproject.model_helpers.AccomodationAttractionFilter;
import com.quiriletelese.troppadvisorproject.model_helpers.PointSearch;
import com.quiriletelese.troppadvisorproject.models.Attraction;
import com.quiriletelese.troppadvisorproject.utils.ConfigFileReader;
import com.quiriletelese.troppadvisorproject.views.AttractionMapActivity;
import com.quiriletelese.troppadvisorproject.views.AttractionsListActivity;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBack;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBackCity;

import java.util.List;

public class AttractionsListActivityController implements OnBottomSheetFilterSearchButtonClick,
        AutoCompleteTextViewsAccomodationFilterTextChangeListener, Constants {

    private AttractionsListActivity attractionsListActivity;
    private DAOFactory daoFactory = DAOFactory.getInstance();
    private BottomSheetFilterAttractions bottomSheetFilterAttractions = new BottomSheetFilterAttractions();
    private AccomodationAttractionFilter accomodationAttractionFilter;
    private RecyclerViewAttractionsListAdapter recyclerViewAttractionsListAdapter;
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

    private void findByRsqlHelper(VolleyCallBack volleyCallBack, PointSearch pointSearch, String rsqlQuery,
                                  Context context, int page, int size) {
        AttractionDAO attractionDAO = daoFactory.getAttractionDAO(ConfigFileReader.getProperty(ATTRACTION_STORAGE_TECHNOLOGY,
                getContext()));
        attractionDAO.findByRsql(volleyCallBack, pointSearch, rsqlQuery, context, page, size);
    }

    private void findByNameLikeIgnoreCaseHelper(VolleyCallBack volleyCallBack, String name, Context context,
                                                int page, int size) {
        AttractionDAO attractionDAO = daoFactory.getAttractionDAO(ConfigFileReader.getProperty(ATTRACTION_STORAGE_TECHNOLOGY,
                getContext()));
        attractionDAO.findByNameLikeIgnoreCase(volleyCallBack, name, context, page, size);
    }

    public void findAttractionsNameHelper(VolleyCallBack volleyCallBack, String name) {
        AttractionDAO attractionDAO = daoFactory.getAttractionDAO(ConfigFileReader.getProperty(ATTRACTION_STORAGE_TECHNOLOGY,
                getContext()));
        attractionDAO.findHotelsName(volleyCallBack, name, getContext());
    }

    public void findCitiesNameHelper(VolleyCallBackCity volleyCallBackCity, String name) {
        CityDAO cityDAO = daoFactory.getCityDAO(ConfigFileReader.getProperty(CITY_STORAGE_TECHNOLOGY,
                getContext()));
        cityDAO.findCitiesByName(volleyCallBackCity, name, getContext());
    }

    public void findByRsql(PointSearch pointSearch, String rsqlQuery) {
        findByRsqlHelper(new VolleyCallBack() {
            @Override
            public void onSuccess(Object object) {
                volleyCallbackOnSuccess(object);
                dismissBottomSheetFilterAttractions();
            }

            @Override
            public void onError(String errorCode) {
                volleyCallbackOnError(errorCode);
            }

        }, pointSearch, rsqlQuery, getContext(), page, size);
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
        }, bottomSheetFilterAttractions.getAutoCompleteTextViewNameValue(), getContext(), page, size);
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
        attractionsListActivity.getRecyclerViewAttractionsList().addOnScrollListener(new RecyclerView.OnScrollListener() {
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

    private void showToastNoMoreResults() {
        isLoadingData = false;
        setProgressBarLoadMoreInvisible();
        attractionsListActivity.runOnUiThread(() -> {
            Toast.makeText(attractionsListActivity, getResources().getString(R.string.end_of_results), Toast.LENGTH_SHORT).show();
        });
    }

    private void loadMoreAttractions() {
        page += 1;
        isLoadingData = true;
        setProgressBarLoadMoreVisible();
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
                findByRsql(isSearchingForCity() ? createNullPointSearch() : createPointSearch(),
                        !createRsqlString().equals("") ? createRsqlString() : "0");
            } else
                findByNameLikeIgnoreCase();
        } else
            findByRsql(createPointSearch(), "0");
    }

    private void volleyCallbackOnSuccess(Object object) {
        if (isLoadingData)
            addNewAttractionsToList((List<Attraction>) object);
        else
            initializeRecyclerViewOnSuccess((List<Attraction>) object);
    }

    private void volleyCallbackOnError(String errorCode) {
        if (errorCode.equals("204")) {
            if (isLoadingData)
                showToastNoMoreResults();
            else
                showToastNoResults();
        }
    }

    private void initializeRecyclerViewOnSuccess(List<Attraction> attractions) {
        dismissBottomSheetFilterAttractions();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerViewAttractionsListAdapter = new RecyclerViewAttractionsListAdapter(getContext(), attractions);
        attractionsListActivity.getRecyclerViewAttractionsList().setLayoutManager(linearLayoutManager);
        attractionsListActivity.getRecyclerViewAttractionsList().setAdapter(recyclerViewAttractionsListAdapter);
        attractionsListActivity.getRecyclerViewAttractionsList().smoothScrollToPosition(0);
        recyclerViewAttractionsListAdapter.notifyDataSetChanged();
    }

    private void addNewAttractionsToList(List<Attraction> attractions) {
        recyclerViewAttractionsListAdapter.addListItems(attractions);
        recyclerViewAttractionsListAdapter.notifyDataSetChanged();
        setProgressBarLoadMoreInvisible();
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

    private PointSearch createNullPointSearch() {
        isPointSearchNull = true;
        PointSearch pointSearch = new PointSearch();
        pointSearch.setLatitude(0d);
        pointSearch.setLongitude(0d);
        pointSearch.setDistance(0d);
        return pointSearch;
    }

    private void setAutoCompleteTextViewHotelNameAdapter(List<String> hotelsName) {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.select_dialog_item, hotelsName);
        bottomSheetFilterAttractions.getAutoCompleteTextViewName().setAdapter(arrayAdapter);
    }

    private void setAutoCompleteTextViewHotelCityAdapter(List<String> citiesName) {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.select_dialog_item, citiesName);
        bottomSheetFilterAttractions.getAutoCompleteTextViewCity().setAdapter(arrayAdapter);
    }

    private void dismissBottomSheetFilterAttractions() {
        if (isBottomSheetFilterHotelsVisible())
            bottomSheetFilterAttractions.dismiss();
    }

    private void showToastNoMoreAttractions() {
        setProgressBarLoadMoreInvisible();
        Toast.makeText(attractionsListActivity, attractionsListActivity.getResources().getString(R.string.end_of_results), Toast.LENGTH_SHORT).show();
    }

    private void setProgressBarLoadMoreVisible() {
        ProgressBar progressBar = attractionsListActivity.getProgressBarAttractionLoadMore();
        progressBar.setVisibility(View.VISIBLE);
    }

    private void setProgressBarLoadMoreInvisible() {
        ProgressBar progressBar = attractionsListActivity.getProgressBarAttractionLoadMore();
        progressBar.setVisibility(View.GONE);
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
        attractionMapsActivityIntent.putExtra(POINT_SEARCH, isPointSearchNull ? createNullPointSearch() : createPointSearch());
    }

    private void putRsqlQuery(Intent attractionMapsActivityIntent) {
        if (!isAccomodationFilterNull())
            attractionMapsActivityIntent.putExtra(RSQL_QUERY, createRsqlString().equals("") ? "0" : createRsqlString());
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

    private void showToastNoResults() {
        attractionsListActivity.runOnUiThread(() -> {
            Toast.makeText(attractionsListActivity, getResources().getString(R.string.no_hotels_found_by_filter), Toast.LENGTH_SHORT).show();
        });
    }

    private Context getContext() {
        return attractionsListActivity.getApplicationContext();
    }

    private FragmentManager getSupportFragmentManager() {
        return attractionsListActivity.getSupportFragmentManager();
    }

    private String getBottomSheetFilterTag() {
        return bottomSheetFilterAttractions.getTag();
    }

    private Resources getResources() {
        return attractionsListActivity.getResources();
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
