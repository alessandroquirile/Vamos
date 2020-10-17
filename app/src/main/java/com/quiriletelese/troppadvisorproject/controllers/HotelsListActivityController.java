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
import com.quiriletelese.troppadvisorproject.adapters.BottomSheetFilterHotels;
import com.quiriletelese.troppadvisorproject.adapters.RecyclerViewHotelsListAdapter;
import com.quiriletelese.troppadvisorproject.dao_interfaces.CityDAO;
import com.quiriletelese.troppadvisorproject.dao_interfaces.HotelDAO;
import com.quiriletelese.troppadvisorproject.factories.DAOFactory;
import com.quiriletelese.troppadvisorproject.model_helpers.HotelFilter;
import com.quiriletelese.troppadvisorproject.model_helpers.PointSearch;
import com.quiriletelese.troppadvisorproject.models.Hotel;
import com.quiriletelese.troppadvisorproject.util_interfaces.AutoCompleteTextViewsAccomodationFilterTextChangeListener;
import com.quiriletelese.troppadvisorproject.util_interfaces.BottomSheetFilterSearchButtonClick;
import com.quiriletelese.troppadvisorproject.util_interfaces.Constants;
import com.quiriletelese.troppadvisorproject.utils.ConfigFileReader;
import com.quiriletelese.troppadvisorproject.views.HotelMapActivity;
import com.quiriletelese.troppadvisorproject.views.HotelsListActivity;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBack;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author Alessandro Quirile, Mauro Telese
 */

public class HotelsListActivityController implements BottomSheetFilterSearchButtonClick,
        AutoCompleteTextViewsAccomodationFilterTextChangeListener, Constants {

    private final HotelsListActivity hotelsListActivity;
    private final BottomSheetFilterHotels bottomSheetFilterHotels = new BottomSheetFilterHotels();
    private HotelFilter hotelFilter;
    private RecyclerViewHotelsListAdapter recyclerViewHotelsListAdapter;
    private final DAOFactory daoFactory = DAOFactory.getInstance();
    private final int size = 30;
    private int page = 0;
    private boolean isLoadingData = false;
    private boolean isPointSearchNull = false;

    public HotelsListActivityController(HotelsListActivity hotelsListActivity) {
        this.hotelsListActivity = hotelsListActivity;
    }

    @Override
    public void onBottomSheetFilterSearchButtonClick() {
        onBottomSheetFilterSearchButtonClickHelper();
    }

    @Override
    public void onAutoCompleteTextViewAccomodationNameTextChanged(String newText) {
        findHotelsName(newText);
    }

    @Override
    public void onAutoCompleteTextViewAccomodationCityTextChanged(String newText) {
        findCitiesName(newText);
    }

    private void findByRsqlHelper(VolleyCallBack volleyCallBack, PointSearch pointSearch, String rsqlQuery) {
        HotelDAO hotelDAO = getHotelDAO();
        hotelDAO.findByRsql(volleyCallBack, pointSearch, rsqlQuery, getContext(), page, size);
    }

    private void findByNameLikeIgnoreCaseHelper(VolleyCallBack volleyCallBack, String name) {
        HotelDAO hotelDAO = getHotelDAO();
        hotelDAO.findByNameLikeIgnoreCase(volleyCallBack, name, getContext(), page, size);
    }

    public void findHotelsNameHelper(VolleyCallBack volleyCallBack, String name) {
        HotelDAO hotelDAO = getHotelDAO();
        hotelDAO.findHotelsName(volleyCallBack, name, getContext());
    }

    public void findCitiesNameHelper(VolleyCallBack volleyCallBack, String name) {
        CityDAO cityDAO = getCityDAO();
        cityDAO.findCitiesByName(volleyCallBack, name, getContext());
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
                volleyCallbackOnSuccess(object);
            }

            @Override
            public void onError(String errorCode) {
                volleyCallbackOnError(errorCode);
            }
        }, getHotelFilterNameValue());
    }

    private void findHotelsName(@NotNull String newText) {
        if (!newText.equals("")) {
            disableFieldsOnAutoCompleteTextViewNameChanged();
            findHotelsNameHelper(new VolleyCallBack() {
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
                        loadMoreHotels();
            }
        });
    }

    private void loadMoreHotels() {
        page++;
        setIsLoadingData(true);
        setProgressBarVisibilityOnUiThred(View.VISIBLE);
        detectSearchType();
    }

    private void onBottomSheetFilterSearchButtonClickHelper() {
        page = 0;
        setIsLoadingData(false);
        createHotelFilter();
        detectSearchType();
    }

    private void createHotelFilter() {
        hotelFilter = new HotelFilter();
        hotelFilter.setName(getHotelNameValueFromBottomSheetFilter());
        hotelFilter.setCity(getCityNameValueFromBottomSheetFilter());
        hotelFilter.setAvaragePrice(getPriceValueFromBottomSheetFilter());
        hotelFilter.setAvarageRating(getRatingValueFromBottomSheetFilter());
        hotelFilter.setStars(getStarsValueFromBottomSheetFilter());
        hotelFilter.setDistance(getDistanceValueFromBottomSheetFilter());
        hotelFilter.setHasCertificateOfExcellence(getCertificateOfExcellenceFromBottomSheetFilter());
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
        if (!isBottomSheetFilterHotelsNull() && !isHotelFilterNull())
            new Handler().postDelayed(this::setFields, 100);
    }

    private void setFields() {
        bottomSheetFilterHotels.setAutoCompleteTextViewNameText(getHotelFilterNameValue());
        bottomSheetFilterHotels.setAutoCompleteTextViewCityText(getHotelFilterCityValue());
        bottomSheetFilterHotels.setSeekBarPriceProgress(getHotelFilterAvaragePriceValue());
        bottomSheetFilterHotels.setSeekBarRatingProgress(getHotelFilterAvarageRatingValue());
        bottomSheetFilterHotels.setSeekBarStarsProgress(getHotelFilterStarsValue());
        bottomSheetFilterHotels.setSeekBarDistanceProgress(getHotelFilterDistanceValue().intValue());
        if (isSearchingForCity() || isSearchingForName())
            bottomSheetFilterHotels.setSeekBarDistanceEnabled(false);
        bottomSheetFilterHotels.setSwitchCompatCertificateOfExcellenceChecked(isHotelFilterHasCertificateOfExcellence());
    }

    private void detectSearchType() {
        if (!isHotelFilterNull()) {
            if (!isSearchingForName()) {
                findByRsql(isSearchingForCity() ? null : createPointSearch(),
                        isRsqlEmpty() ? "0" : createRsqlString());
            } else
                findByNameLikeIgnoreCase();
        } else
            findByRsql(createPointSearch(), "0");
    }

    private void volleyCallbackOnSuccess(Object object) {
        List<Hotel> hotels = (List<Hotel>) object;
        if (isLoadingData)
            addNewHotelsToList(hotels);
        else
            initializeRecyclerViewOnSuccess(hotels);
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
            showToastVolleyError(R.string.no_hotels_found_by_filter);
    }

    private void handleOtherVolleyError() {
        showToastVolleyError(R.string.unexpected_error_while_fetch_data);
    }

    private void initializeRecyclerViewOnSuccess(List<Hotel> hotels) {
        dismissBottomSheetFilterHotels();
        recyclerViewHotelsListAdapter = createRecyclerViewAdapter(hotels);
        hotelsListActivity.getRecyclerViewHotelsList().setLayoutManager(createLinearLayoutManager());
        hotelsListActivity.getRecyclerViewHotelsList().setAdapter(recyclerViewHotelsListAdapter);
        hotelsListActivity.getRecyclerViewHotelsList().smoothScrollToPosition(0);
        recyclerViewHotelsListAdapter.notifyDataSetChanged();
    }

    @NotNull
    @Contract("_ -> new")
    private RecyclerViewHotelsListAdapter createRecyclerViewAdapter(List<Hotel> hotels) {
        return new RecyclerViewHotelsListAdapter(getContext(), hotels);
    }

    @NotNull
    @Contract(" -> new")
    private LinearLayoutManager createLinearLayoutManager() {
        return new LinearLayoutManager(getContext(), setRecyclerViewVerticalOrientation(), false);
    }

    private int setRecyclerViewVerticalOrientation() {
        return RecyclerView.VERTICAL;
    }

    private void addNewHotelsToList(List<Hotel> hotels) {
        recyclerViewHotelsListAdapter.addListItems(hotels);
        recyclerViewHotelsListAdapter.notifyDataSetChanged();
        setProgressBarVisibilityOnUiThred(View.INVISIBLE);
    }

    public void showBottomSheetFilters() {
        bottomSheetFilterHotels.show(getSupportFragmentManager(), bottomSheetFilterHotels.getTag());
        setBottomSheetFiltersFields();
        bottomSheetFilterHotels.setBottomSheetFilterSearchButtonClick(this);
        bottomSheetFilterHotels.setAutoCompleteTextViewsAccomodationFilterTextChangeListener(this);
    }

    @NotNull
    private PointSearch createPointSearch() {
        isPointSearchNull = false;
        PointSearch pointSearch = getPointSearch();
        pointSearch.setDistance(isHotelFilterNull() ? 5d : checkDistanceValue());
        return pointSearch;
    }

    private void setAutoCompleteTextViewNameAdapter(List<String> hotelsNames) {
        ArrayAdapter<String> arrayAdapter = createAutoCompleteTextViewAdapter(hotelsNames);
        bottomSheetFilterHotels.setAutoCompleteTextViewNameAdapter(arrayAdapter);
    }

    private void setAutoCompleteTextViewCityAdapter(List<String> citiesNames) {
        ArrayAdapter<String> arrayAdapter = createAutoCompleteTextViewAdapter(citiesNames);
        bottomSheetFilterHotels.setAutoCompleteTextViewCityAdapter(arrayAdapter);
    }

    @NotNull
    @Contract("_ -> new")
    private ArrayAdapter<String> createAutoCompleteTextViewAdapter(List<String> content) {
        return new ArrayAdapter<>(getContext(), getAutoCompleteTextViewAdapterLayout(), content);
    }

    private int getAutoCompleteTextViewAdapterLayout() {
        return android.R.layout.select_dialog_item;
    }

    private void dismissBottomSheetFilterHotels() {
        if (isBottomSheetFilterHotelsVisible())
            bottomSheetFilterHotels.dismiss();
    }

    private void showToastVolleyError(int string) {
        setIsLoadingData(false);
        setProgressBarVisibilityOnUiThred(View.INVISIBLE);
        showToastOnUiThred(string);
    }

    private void showToastOnUiThred(int string) {
        hotelsListActivity.runOnUiThread(() ->
                Toast.makeText(hotelsListActivity, getString(string), Toast.LENGTH_SHORT).show());
    }

    private void setProgressBarVisibilityOnUiThred(int visibility) {
        hotelsListActivity.runOnUiThread(() -> getProgressBarLoadMore().setVisibility(visibility));
    }

    public void startMapsActivity() {
        Intent intentHotelMapsActivity = new Intent(getContext(), HotelMapActivity.class);
        putPointSearch(intentHotelMapsActivity);
        putRsqlQuery(intentHotelMapsActivity);
        putHotelName(intentHotelMapsActivity);
        putHotelFilter(intentHotelMapsActivity);
        intentHotelMapsActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        hotelsListActivity.startActivity(intentHotelMapsActivity);
    }

    private void putPointSearch(@NotNull Intent hotelMapsActivityIntent) {
        hotelMapsActivityIntent.putExtra(POINT_SEARCH, isPointSearchNull ? null : createPointSearch());
    }

    private void putRsqlQuery(Intent hotelMapsActivityIntent) {
        if (!isHotelFilterNull())
            hotelMapsActivityIntent.putExtra(RSQL_QUERY, isRsqlEmpty() ? "0" : createRsqlString());
        else
            hotelMapsActivityIntent.putExtra(RSQL_QUERY, "0");
    }

    private void putHotelName(Intent hotelMapsActivityIntent) {
        if (!isHotelFilterNull()) {
            if (isSearchingForName()) {
                hotelMapsActivityIntent.putExtra(SEARCH_FOR_NAME, true);
                hotelMapsActivityIntent.putExtra(NAME, getHotelFilterNameValue());
            } else
                hotelMapsActivityIntent.putExtra(SEARCH_FOR_NAME, false);
        }
    }

    private void putHotelFilter(@NotNull Intent hotelMapsActivityIntent) {
        hotelMapsActivityIntent.putExtra(ACCOMODATION_FILTER, isHotelFilterNull() ? null
                : hotelFilter);
    }

    private String getHotelNameValueFromBottomSheetFilter() {
        return bottomSheetFilterHotels.getAutoCompleteTextViewNameValue();
    }

    private String getCityNameValueFromBottomSheetFilter() {
        return extractCityName(bottomSheetFilterHotels.getAutoCompleteTextViewCityValue());
    }

    @NotNull
    private Integer getPriceValueFromBottomSheetFilter() {
        return bottomSheetFilterHotels.getSeekBarPriceValue();
    }

    @NotNull
    private Integer getRatingValueFromBottomSheetFilter() {
        return bottomSheetFilterHotels.getSeekBarRatingValue();
    }

    @NotNull
    private Integer getStarsValueFromBottomSheetFilter() {
        return bottomSheetFilterHotels.getSeekBarStarsValue();
    }

    @NotNull
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
            String cityName = extractCityName(getHotelFilterCityValue());
            rsqlString = rsqlString.concat("address.city==" + cityName + ";");
        }
        return rsqlString;
    }

    private String extractCityName(@NotNull String city) {
        return city.contains(",") ? city.substring(0, city.lastIndexOf(",")) : city;
    }

    private String checkPriceValue(String rsqlString) {
        if (!isHotelFilterAvaragePriceEqualsToZero()) {
            if (isHotelFilterAvaragePriceGreaterEqualsThan150())
                rsqlString = rsqlString.concat("avaragePrice=ge=" + getHotelFilterAvaragePriceValue() + ";");
            else
                rsqlString = rsqlString.concat("avaragePrice=le=" + getHotelFilterAvaragePriceValue() + ";");
        }
        return rsqlString;
    }

    private String checkRatingValue(String rsqlString) {
        if (!isHotelFilterAvarageRatingEqualsToZero())
            rsqlString = rsqlString.concat("avarageRating=ge=" + getHotelFilterAvarageRatingValue() + ";");
        return rsqlString;
    }

    private String checkHotelStarsValue(String rsqlString) {
        if (!(bottomSheetFilterHotels.getSeekBarStarsValue() == 0))
            rsqlString = rsqlString.concat("stars=le=" + bottomSheetFilterHotels.getSeekBarStarsValue() + ";");
        return rsqlString;
    }

    private Double checkDistanceValue() {
        double distance = 1.0;
        if (!isHotelFilterDistanceEqualsToZero())
            distance = getHotelFilterDistanceValue();
        return distance;
    }

    private String checkCertificateOfExcellence(String rsqlString) {
        if (isHotelFilterHasCertificateOfExcellence())
            rsqlString = rsqlString.concat("certificateOfExcellence==" + "true;");
        return rsqlString;
    }

    @NotNull
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

    private boolean isScrolledToLastItem(@NotNull RecyclerView recyclerView) {
        return !recyclerView.canScrollVertically(1);
    }

    private HotelDAO getHotelDAO() {
        return daoFactory.getHotelDAO(getStorageTechnology(HOTEL_STORAGE_TECHNOLOGY));
    }

    private CityDAO getCityDAO() {
        return daoFactory.getCityDAO(getStorageTechnology(CITY_STORAGE_TECHNOLOGY));
    }

    private String getStorageTechnology(String storageTechnology) {
        return ConfigFileReader.getProperty(storageTechnology, getContext());
    }

    private ProgressBar getProgressBarLoadMore() {
        return hotelsListActivity.getProgressBarHotelLoadMore();
    }

    private Context getContext() {
        return hotelsListActivity.getApplicationContext();
    }

    private RecyclerView getRecyclerView() {
        return hotelsListActivity.getRecyclerViewHotelsList();
    }

    private Resources getResources() {
        return hotelsListActivity.getResources();
    }

    @NotNull
    private String getString(int string) {
        return getResources().getString(string);
    }

    private Intent getIntent() {
        return hotelsListActivity.getIntent();
    }

    public PointSearch getPointSearch() {
        return (PointSearch) getIntent().getSerializableExtra(POINT_SEARCH);
    }

    @NotNull
    private FragmentManager getSupportFragmentManager() {
        return hotelsListActivity.getSupportFragmentManager();
    }

    private boolean isRsqlEmpty() {
        return createRsqlString().equals("");
    }

    private boolean isBottomSheetFilterHotelsVisible() {
        return bottomSheetFilterHotels.isAdded();
    }

    private void setIsLoadingData(boolean value) {
        isLoadingData = value;
    }

    private String getHotelFilterNameValue() {
        return hotelFilter.getName();
    }

    private String getHotelFilterCityValue() {
        return hotelFilter.getCity();
    }

    private Integer getHotelFilterAvaragePriceValue() {
        return hotelFilter.getAvaragePrice();
    }

    private Integer getHotelFilterAvarageRatingValue() {
        return hotelFilter.getAvarageRating();
    }

    private Integer getHotelFilterStarsValue() {
        return hotelFilter.getStars();
    }

    private Double getHotelFilterDistanceValue() {
        return hotelFilter.getDistance();
    }

    private boolean getHotelFilterHasCertificateOfExcellenceValue() {
        return hotelFilter.isHasCertificateOfExcellence();
    }

    private boolean isSearchingForName() {
        return !getHotelFilterNameValue().equals("");
    }

    private boolean isSearchingForCity() {
        return !getHotelFilterCityValue().equals("");
    }

    private boolean isBottomSheetFilterHotelsNull() {
        return bottomSheetFilterHotels == null;
    }

    private boolean isHotelFilterNull() {
        return hotelFilter == null;
    }

    private boolean isHotelFilterAvaragePriceEqualsToZero() {
        return getHotelFilterAvaragePriceValue().equals(0);
    }

    private boolean isHotelFilterAvaragePriceGreaterEqualsThan150() {
        return getHotelFilterAvaragePriceValue() >= 150;
    }

    private boolean isHotelFilterAvarageRatingEqualsToZero() {
        return getHotelFilterAvarageRatingValue().equals(0);
    }

    private boolean isHotelFilterDistanceEqualsToZero() {
        return getHotelFilterDistanceValue().equals(0d);
    }

    private boolean isHotelFilterHasCertificateOfExcellence() {
        return getHotelFilterHasCertificateOfExcellenceValue();
    }

}
