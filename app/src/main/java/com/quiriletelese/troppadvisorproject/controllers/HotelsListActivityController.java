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

import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.adapters.BottomSheetFilterHotels;
import com.quiriletelese.troppadvisorproject.adapters.RecyclerViewHotelsListAdapter;
import com.quiriletelese.troppadvisorproject.dao_interfaces.CityDAO;
import com.quiriletelese.troppadvisorproject.dao_interfaces.HotelDAO;
import com.quiriletelese.troppadvisorproject.factories.DAOFactory;
import com.quiriletelese.troppadvisorproject.interfaces.AutoCompleteTextViewsAccomodationFilterTextChangeListener;
import com.quiriletelese.troppadvisorproject.interfaces.Constants;
import com.quiriletelese.troppadvisorproject.interfaces.OnBottomSheetFilterSearchButtonClick;
import com.quiriletelese.troppadvisorproject.model_helpers.AccomodationHotelFilter;
import com.quiriletelese.troppadvisorproject.model_helpers.PointSearch;
import com.quiriletelese.troppadvisorproject.models.Hotel;
import com.quiriletelese.troppadvisorproject.utils.ConfigFileReader;
import com.quiriletelese.troppadvisorproject.views.HotelMapActivity;
import com.quiriletelese.troppadvisorproject.views.HotelsListActivity;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBack;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBackCity;

import java.util.List;

public class HotelsListActivityController implements OnBottomSheetFilterSearchButtonClick,
        AutoCompleteTextViewsAccomodationFilterTextChangeListener, Constants {

    private HotelsListActivity hotelsListActivity;
    private BottomSheetFilterHotels bottomSheetFilterHotels = new BottomSheetFilterHotels();
    private AccomodationHotelFilter accomodationHotelFilter;
    private DAOFactory daoFactory = DAOFactory.getInstance();
    private RecyclerViewHotelsListAdapter recyclerViewHotelsListAdapter;
    private int page = 0, size = 3;
    private boolean isLoadingData = false, isPointSearchNull = false;

    public HotelsListActivityController(HotelsListActivity hotelsListActivity) {
        this.hotelsListActivity = hotelsListActivity;
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
        HotelDAO hotelDAO = daoFactory.getHotelDAO(ConfigFileReader.getProperty(HOTEL_STORAGE_TECHNOLOGY,
                hotelsListActivity.getApplicationContext()));
        hotelDAO.findByRsql(volleyCallBack, pointSearch, rsqlQuery, context, page, size);
    }

    private void findByNameLikeIgnoreCaseHelper(VolleyCallBack volleyCallBack, String name, Context context,
                                                int page, int size) {
        HotelDAO hotelDAO = daoFactory.getHotelDAO(ConfigFileReader.getProperty(HOTEL_STORAGE_TECHNOLOGY,
                hotelsListActivity.getApplicationContext()));
        hotelDAO.findByNameLikeIgnoreCase(volleyCallBack, name, context, page, size);
    }

    public void findHotelsNameHelper(VolleyCallBack volleyCallBack, String name) {
        HotelDAO hotelDAO = daoFactory.getHotelDAO(ConfigFileReader.getProperty(HOTEL_STORAGE_TECHNOLOGY,
                hotelsListActivity.getApplicationContext()));
        hotelDAO.findHotelsName(volleyCallBack, name, hotelsListActivity.getApplicationContext());
    }

    public void findCitiesNameHelper(VolleyCallBackCity volleyCallBackCity, String name) {
        CityDAO cityDAO = daoFactory.getCityDAO(ConfigFileReader.getProperty(CITY_STORAGE_TECHNOLOGY,
                hotelsListActivity.getApplicationContext()));
        cityDAO.findCitiesByName(volleyCallBackCity, name, hotelsListActivity.getApplicationContext());
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

        }, pointSearch, rsqlQuery, hotelsListActivity.getApplicationContext(), page, size);
    }

    private void findByNameLikeIgnoreCase() {
        findByNameLikeIgnoreCaseHelper(new VolleyCallBack() {
            @Override
            public void onSuccess(Object object) {
                initializeRecyclerViewOnSuccess((List<Hotel>) object);
                bottomSheetFilterHotels.dismiss();
            }

            @Override
            public void onError(String errorCode) {
                volleyCallbackOnError(errorCode);
            }
        }, bottomSheetFilterHotels.getAutoCompleteTextViewMapHotelNameValue(), hotelsListActivity
                .getApplicationContext(), page, size);
    }

    private void findHotelsName(String newText) {
        if (!newText.equals("")) {
            disableFieldsOnAutoCompleteTextViewNameChanged();
            findHotelsNameHelper(new VolleyCallBack() {
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
        hotelsListActivity.getRecyclerViewHotelsList().addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isScrollingDown(dy))
                    if (isScrolledToLastItem(recyclerView))
                        loadMoreHotels();
            }
        });
    }

    private void loadMoreHotels() {
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
        accomodationHotelFilter = new AccomodationHotelFilter();
        accomodationHotelFilter.setName(getHotelNameValueFromBottomSheetFilter());
        accomodationHotelFilter.setCity(getCityNameValueFromBottomSheetFilter());
        accomodationHotelFilter.setAvaragePrice(getPriceValueFromBottomSheetFilter());
        accomodationHotelFilter.setAvarageRating(getRatingValueFromBottomSheetFilter());
        accomodationHotelFilter.setStars(getStarsValueFromBottomSheetFilter());
        accomodationHotelFilter.setDistance(getDistanceValueFromBottomSheetFilter());
        accomodationHotelFilter.setHasCertificateOfExcellence(getCertificateOfExcellenceFromBottomSheetFilter());
    }

    private void disableFieldsOnAutoCompleteTextViewNameChanged() {
        bottomSheetFilterHotels.setAutoCompleteTextViewCityText("");
        bottomSheetFilterHotels.setSeekBarPriceEnabled(false);
        bottomSheetFilterHotels.setSeekBarRatingEnabled(false);
        bottomSheetFilterHotels.setSeekBarStarsEnabled(false);
        bottomSheetFilterHotels.setSeekBarDistanceEnabled(false);
        bottomSheetFilterHotels.setSwitchCompatCertificateOfExcellenceEnabled(false);
    }

    private void enableFieldsOnAutoCompleteTextViewNameChanged() {
        bottomSheetFilterHotels.setSeekBarPriceEnabled(true);
        bottomSheetFilterHotels.setSeekBarRatingEnabled(true);
        bottomSheetFilterHotels.setSeekBarStarsEnabled(true);
        bottomSheetFilterHotels.setSeekBarDistanceEnabled(true);
        bottomSheetFilterHotels.setSwitchCompatCertificateOfExcellenceEnabled(true);
    }

    private void disableFieldsOnAutoCompleteTextViewCityChanged() {
        bottomSheetFilterHotels.setAutoCompleteTextViewNameText("");
        bottomSheetFilterHotels.setSeekBarDistanceEnabled(false);
    }

    private void enableFieldsOnAutoCompleteTextViewCityChanged() {
        bottomSheetFilterHotels.setSeekBarDistanceEnabled(true);
    }

    private void setBottomSheetFiltersFields() {
        if (!isBottomSheetFilterHotelsNull() && !isAccomodationFilterNull())
            new Handler().postDelayed(this::setFields, 100);
    }

    private void setFields() {
        bottomSheetFilterHotels.getAutoCompleteTextViewName().setText(getAccomodationFilterNameValue());
        bottomSheetFilterHotels.getAutoCompleteTextViewCity().setText(getAccomodationFilterCityValue());
        bottomSheetFilterHotels.setSeekBarPriceProgress(getAccomodationFilterAvaragePriceValue());
        bottomSheetFilterHotels.setSeekBarRatingProgress(getAccomodationFilterAvarageRatingValue());
        bottomSheetFilterHotels.setSeekBarStarsProgress(getAccomodationFilterStarsValue());
        bottomSheetFilterHotels.setSeekBarDistanceProgress(getAccomodationFilterDistanceValue().intValue());
        if (isSearchingForCity() || isSearchingForName())
            bottomSheetFilterHotels.setSeekBarDistanceEnabled(false);
        bottomSheetFilterHotels.setSwitchCompatCertificateOfExcellenceChecked(isAccomodationFilterHasCertificateOfExcellence());
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
            addNewHotelsToList((List<Hotel>) object);
        else
            initializeRecyclerViewOnSuccess((List<Hotel>) object);
    }

    private void volleyCallbackOnError(String errorCode) {
        if (errorCode.equals("204")) {
            if (isLoadingData)
                showToastNoMoreResults();
            else
                showToastNoResults();
        }
    }

    private void initializeRecyclerViewOnSuccess(List<Hotel> hotels) {
        dismissBottomSheetFilterHotels();
        LinearLayoutManager linearLayoutManager = createLinearLayoutManager();
        recyclerViewHotelsListAdapter = createRecyclerViewHotelsListAdapter(hotels);
        hotelsListActivity.getRecyclerViewHotelsList().setLayoutManager(linearLayoutManager);
        hotelsListActivity.getRecyclerViewHotelsList().setAdapter(recyclerViewHotelsListAdapter);
        hotelsListActivity.getRecyclerViewHotelsList().smoothScrollToPosition(0);
        recyclerViewHotelsListAdapter.notifyDataSetChanged();
    }

    private LinearLayoutManager createLinearLayoutManager(){
        return new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
    }

    private RecyclerViewHotelsListAdapter createRecyclerViewHotelsListAdapter(List<Hotel> hotels){
        return new RecyclerViewHotelsListAdapter(getContext(), hotels);
    }

    private void addNewHotelsToList(List<Hotel> hotels) {
        recyclerViewHotelsListAdapter.addListItems(hotels);
        recyclerViewHotelsListAdapter.notifyDataSetChanged();
        setProgressBarLoadMoreInvisible();
    }

    public void showBottomSheetFilters() {
        bottomSheetFilterHotels.show(hotelsListActivity.getSupportFragmentManager(), bottomSheetFilterHotels.getTag());
        setBottomSheetFiltersFields();
        bottomSheetFilterHotels.setOnBottomSheetFilterSearchButtonClick(this);
        bottomSheetFilterHotels.setAutoCompleteTextViewsAccomodationFilterTextChangeListener(this);
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
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(hotelsListActivity.getApplicationContext(),
                android.R.layout.select_dialog_item, hotelsName);
        bottomSheetFilterHotels.getAutoCompleteTextViewName().setAdapter(arrayAdapter);
    }

    private void setAutoCompleteTextViewHotelCityAdapter(List<String> citiesName) {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(hotelsListActivity.getApplicationContext(),
                android.R.layout.select_dialog_item, citiesName);
        bottomSheetFilterHotels.getAutoCompleteTextViewCity().setAdapter(arrayAdapter);
    }

    private void dismissBottomSheetFilterHotels() {
        if (isBottomSheetFilterHotelsVisible())
            bottomSheetFilterHotels.dismiss();
    }

    private Context getContext(){
        return hotelsListActivity.getApplicationContext();
    }

    private void setProgressBarLoadMoreVisible() {
        ProgressBar progressBar = hotelsListActivity.getProgressBarHotelLoadMore();
        progressBar.setVisibility(View.VISIBLE);
    }

    private void setProgressBarLoadMoreInvisible() {
        ProgressBar progressBar = hotelsListActivity.getProgressBarHotelLoadMore();
        progressBar.setVisibility(View.GONE);
    }

    public void startMapsActivity() {
        Intent hotelMapsActivityIntent = new Intent(getContext(), HotelMapActivity.class);
        putPointSearch(hotelMapsActivityIntent);
        putRsqlQuery(hotelMapsActivityIntent);
        putHotelName(hotelMapsActivityIntent);
        putAccomodationHotelFilter(hotelMapsActivityIntent);
        hotelsListActivity.startActivity(hotelMapsActivityIntent);
    }

    private void putPointSearch(Intent hotelMapsActivityIntent) {
        hotelMapsActivityIntent.putExtra(POINT_SEARCH, isPointSearchNull ? createNullPointSearch() : createPointSearch());
    }

    private void putRsqlQuery(Intent hotelMapsActivityIntent) {
        if (!isAccomodationFilterNull())
            hotelMapsActivityIntent.putExtra(RSQL_QUERY, createRsqlString().equals("") ? "0" : createRsqlString());
        else
            hotelMapsActivityIntent.putExtra(RSQL_QUERY, "0");
    }

    private void putHotelName(Intent hotelMapsActivityIntent) {
        if (!isAccomodationFilterNull()) {
            if (isSearchingForName()) {
                hotelMapsActivityIntent.putExtra(SEARCH_FOR_NAME, true);
                hotelMapsActivityIntent.putExtra(NAME, getAccomodationFilterNameValue());
            } else
                hotelMapsActivityIntent.putExtra(SEARCH_FOR_NAME, false);
        }
    }

    private void putAccomodationHotelFilter(Intent hotelMapsActivityIntent) {
        hotelMapsActivityIntent.putExtra(ACCOMODATION_FILTER, isAccomodationFilterNull() ? null
                : accomodationHotelFilter);
    }

    private String getHotelNameValueFromBottomSheetFilter() {
        return bottomSheetFilterHotels.getAutoCompleteTextViewMapHotelNameValue();
    }

    private String getCityNameValueFromBottomSheetFilter() {
        return extractCityName(bottomSheetFilterHotels.getAutoCompleteTextViewMapHotelCityValue());
    }

    private Integer getPriceValueFromBottomSheetFilter() {
        return bottomSheetFilterHotels.getSeekBarPriceValue();
    }

    private Integer getRatingValueFromBottomSheetFilter() {
        return bottomSheetFilterHotels.getSeekBarRatingValue();
    }

    private Integer getStarsValueFromBottomSheetFilter() {
        return bottomSheetFilterHotels.getSeekBarStarsValue();
    }

    private Double getDistanceValueFromBottomSheetFilter() {
        if (bottomSheetFilterHotels.getSeekBarDistanceValue() != 0)
            return (double) bottomSheetFilterHotels.getSeekBarDistanceValue();
        else
            return 1d;
    }

    private boolean getCertificateOfExcellenceFromBottomSheetFilter() {
        return bottomSheetFilterHotels.getSwitchCompatCertificateOfExcellenceIsSelected();
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

    private String checkHotelStarsValue(String rsqlString) {
        if (!(bottomSheetFilterHotels.getSeekBarStarsValue() == 0))
            rsqlString = rsqlString.concat("stars=le=" + bottomSheetFilterHotels.getSeekBarStarsValue() + ";");
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
        rsqlString = checkHotelStarsValue(rsqlString);
        rsqlString = checkCertificateOfExcellence(rsqlString);
        if (!rsqlString.equals(""))
            rsqlString = rsqlString.substring(0, rsqlString.lastIndexOf(";"));
        return rsqlString;
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
        hotelsListActivity.runOnUiThread(() -> {
            Toast.makeText(hotelsListActivity, hotelsListActivity.getResources().getString(R.string.end_of_results), Toast.LENGTH_SHORT).show();
        });
    }

    private void showToastNoResults() {
        hotelsListActivity.runOnUiThread(() -> {
            Toast.makeText(hotelsListActivity, hotelsListActivity.getResources().getString(R.string.no_hotels_found_by_filter), Toast.LENGTH_SHORT).show();
        });
    }

    private Intent getIntent(){
        return hotelsListActivity.getIntent();
    }

    public PointSearch getPointSearch() {
        return (PointSearch) getIntent().getSerializableExtra(POINT_SEARCH);
    }

    private boolean isBottomSheetFilterHotelsVisible() {
        return bottomSheetFilterHotels.isAdded();
    }

    private String getAccomodationFilterNameValue() {
        return accomodationHotelFilter.getName();
    }

    private String getAccomodationFilterCityValue() {
        return accomodationHotelFilter.getCity();
    }

    private Integer getAccomodationFilterAvaragePriceValue() {
        return accomodationHotelFilter.getAvaragePrice();
    }

    private Integer getAccomodationFilterAvarageRatingValue() {
        return accomodationHotelFilter.getAvarageRating();
    }

    private Integer getAccomodationFilterStarsValue() {
        return accomodationHotelFilter.getStars();
    }

    private Double getAccomodationFilterDistanceValue() {
        return accomodationHotelFilter.getDistance();
    }

    private boolean getAccomodationFilterHasCertificateOfExcellenceValue() {
        return accomodationHotelFilter.isHasCertificateOfExcellence();
    }

    private boolean isSearchingForName() {
        return !getAccomodationFilterNameValue().equals("");
    }

    private boolean isSearchingForCity() {
        return !getAccomodationFilterCityValue().equals("");
    }

    private boolean isBottomSheetFilterHotelsNull(){
        return bottomSheetFilterHotels == null;
    }

    private boolean isAccomodationFilterNull() {
        return accomodationHotelFilter == null;
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
