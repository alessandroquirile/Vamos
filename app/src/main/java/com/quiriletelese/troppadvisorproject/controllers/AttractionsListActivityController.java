package com.quiriletelese.troppadvisorproject.controllers;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.adapters.BottomSheetFilterAttractions;
import com.quiriletelese.troppadvisorproject.adapters.RecyclerViewAttractionsListAdapter;
import com.quiriletelese.troppadvisorproject.dao_interfaces.AttractionDAO;
import com.quiriletelese.troppadvisorproject.dao_interfaces.CityDAO;
import com.quiriletelese.troppadvisorproject.factories.DAOFactory;
import com.quiriletelese.troppadvisorproject.model_helpers.AttractionFilter;
import com.quiriletelese.troppadvisorproject.model_helpers.Constants;
import com.quiriletelese.troppadvisorproject.model_helpers.PointSearch;
import com.quiriletelese.troppadvisorproject.models.Attraction;
import com.quiriletelese.troppadvisorproject.util_interfaces.AutoCompleteTextViewsAccomodationFilterTextChangeListener;
import com.quiriletelese.troppadvisorproject.util_interfaces.BottomSheetFilterSearchButtonClick;
import com.quiriletelese.troppadvisorproject.utils.ConfigFileReader;
import com.quiriletelese.troppadvisorproject.utils.UserSharedPreferences;
import com.quiriletelese.troppadvisorproject.views.AttractionMapActivity;
import com.quiriletelese.troppadvisorproject.views.AttractionsListActivity;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBack;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author Alessandro Quirile, Mauro Telese
 */

public class AttractionsListActivityController implements BottomSheetFilterSearchButtonClick,
        AutoCompleteTextViewsAccomodationFilterTextChangeListener {

    private final AttractionsListActivity attractionsListActivity;
    private final BottomSheetFilterAttractions bottomSheetFilterAttractions = new BottomSheetFilterAttractions();
    private AttractionFilter attractionFilter;
    private RecyclerViewAttractionsListAdapter recyclerViewAttractionsListAdapter;
    private final DAOFactory daoFactory = DAOFactory.getInstance();
    private final int size = 50;
    private int page = 0;
    private boolean isLoadingData = false;
    private boolean isPointSearchNull = false;
    private boolean hasToShowBottomSheetFilter = true;

    public AttractionsListActivityController(AttractionsListActivity attractionsListActivity) {
        this.attractionsListActivity = attractionsListActivity;
    }

    @Override
    public void onBottomSheetFilterSearchButtonClick() {
        onBottomSheetFilterSearchButtonClickHelper();
    }

    @Override
    public void onAutoCompleteTextViewAccomodationNameTextChanged(String newText) {
        findAttractionsName(newText);
    }

    @Override
    public void onAutoCompleteTextViewAccomodationCityTextChanged(String newText) {
        findCitiesName(newText);
    }

    private void findByRsqlHelper(VolleyCallBack volleyCallBack, PointSearch pointSearch, String rsqlQuery) {
        getAttractionDAO().findByRsql(volleyCallBack, pointSearch, rsqlQuery, getContext(), page, size);
    }

    private void findByNameLikeIgnoreCaseHelper(VolleyCallBack volleyCallBack, String name) {
        AttractionDAO attractionDAO = getAttractionDAO();
        attractionDAO.findByNameLikeIgnoreCase(volleyCallBack, name, getContext(), page, size);
    }

    public void findAttractionsNameHelper(VolleyCallBack volleyCallBack, String name) {
        AttractionDAO attractionDAO = getAttractionDAO();
        attractionDAO.findAttractionsName(volleyCallBack, name, getContext());
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
                initializeRecyclerViewOnSuccess((List<Attraction>) object);
            }

            @Override
            public void onError(String errorCode) {
                volleyCallbackOnError(errorCode);
            }
        }, getAttractionFilterNameValue());
    }

    private void findAttractionsName(@NotNull String newText) {
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

    private void findCitiesName(@NotNull String newText) {
        if (!newText.equals("")) {
            disableFieldsOnAutoCompleteTextViewCityChanged();
            findCitiesNameHelper(new VolleyCallBack() {
                @Override
                public void onSuccess(Object object) {
                    setAutoCompleteTextViewHotelCityAdapter((List<String>) object);
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
                    if (hasScrolledToLastItem(recyclerView))
                        loadMoreAttractions();
            }
        });
    }

    private boolean isScrollingDown(int dy) {
        return dy > 0;
    }

    private boolean hasScrolledToLastItem(@NotNull RecyclerView recyclerView) {
        return !recyclerView.canScrollVertically(1);
    }

    private void loadMoreAttractions() {
        page++;
        setIsLoadingData(true);
        setProgressBarVisibilityOnUiThred(View.VISIBLE);
        detectSearchType();
    }

    private void onBottomSheetFilterSearchButtonClickHelper() {
        setIsLoadingData(false);
        page = 0;
        createAttractionFilter();
        detectSearchType();
    }

    private void createAttractionFilter() {
        attractionFilter = new AttractionFilter();
        attractionFilter.setName(getAttractionNameValueFromBottomSheetFilter());
        attractionFilter.setCity(getCityNameValueFromBottomSheetFilter());
        attractionFilter.setAvaragePrice(getPriceValueFromBottomSheetFilter());
        attractionFilter.setAvarageRating(getRatingValueFromBottomSheetFilter());
        attractionFilter.setDistance(getDistanceValueFromBottomSheetFilter());
        attractionFilter.setHasCertificateOfExcellence(getCertificateOfExcellenceFromBottomSheetFilter());
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
        if (!isAttractionFilterNull())
            new Handler().postDelayed(this::setFields, 100);
    }

    private void setFields() {
        bottomSheetFilterAttractions.setAutoCompleteTextViewNameText(getAttractionFilterNameValue());
        bottomSheetFilterAttractions.setAutoCompleteTextViewCityText(getAttractionFilterCityValue());
        bottomSheetFilterAttractions.setSeekBarPriceProgress(getAttractionFilterAvaragePriceValue());
        bottomSheetFilterAttractions.setSeekBarRatingProgress(getAttractionFilterAvarageRatingValue());
        bottomSheetFilterAttractions.setSeekBarDistanceProgress(getAttractionFilterDistanceValue().intValue());
        if (isSearchingForCity() || isSearchingForName())
            bottomSheetFilterAttractions.setSeekBarDistanceEnabled(false);
        bottomSheetFilterAttractions.setSwitchCompatCertificateOfExcellenceChecked(isAttractionFilterHasCertificateOfExcellence());
    }

    private void detectSearchType() {
        if (!isAttractionFilterNull()) {
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
        if (!checkTapTargetBooleanPreferences())
            setTapTargetSequence();
        else {
            if (hasToShowBottomSheetFilter) {
                hasToShowBottomSheetFilter = false;
                final Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(this::showBottomSheetFilters, 200);
            }
        }
    }

    private void volleyCallbackOnError(@NotNull String errorCode) {
        setProgressBarVisibilityOnUiThred(View.INVISIBLE);
        switch (errorCode) {
            case "204":
                handle204VolleyError();
                break;
            default:
                handleOtherVolleyError();
                break;
        }
        if (hasToShowBottomSheetFilter) {
            hasToShowBottomSheetFilter = false;
            final Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(this::showBottomSheetFilters, 200);
        }
    }

    private void handle204VolleyError() {
        if (!isLoadingData)
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

    @NotNull
    @Contract("_ -> new")
    private RecyclerViewAttractionsListAdapter createRecyclerViewAdapter(List<Attraction> attractions) {
        return new RecyclerViewAttractionsListAdapter(getContext(), attractions);
    }

    @NotNull
    @Contract(" -> new")
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
        bottomSheetFilterAttractions.setBottomSheetFilterSearchButtonClick(this);
        bottomSheetFilterAttractions.setAutoCompleteTextViewsAccomodationFilterTextChangeListener(this);
    }

    @NotNull
    private PointSearch createPointSearch() {
        isPointSearchNull = false;
        PointSearch pointSearch = getPointSearch();
        pointSearch.setDistance(isAttractionFilterNull() ? 5d : checkDistanceValue());
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

    @NotNull
    @Contract("_ -> new")
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

    private void showToastVolleyError(int stringId) {
        setIsLoadingData(false);
        showToastOnUiThread(stringId);
    }

    private void showToastOnUiThread(int stringId) {
        attractionsListActivity.runOnUiThread(() ->
                Toast.makeText(attractionsListActivity, getString(stringId), Toast.LENGTH_SHORT).show());
    }

    private void setProgressBarVisibilityOnUiThred(int visibility) {
        attractionsListActivity.runOnUiThread(() -> getProgressBarLoadMore().setVisibility(visibility));
    }

    public void startMapsActivity() {
        if (!(recyclerViewAttractionsListAdapter == null)) {
            Intent intentAttractionsMapActivity = new Intent(getContext(), AttractionMapActivity.class);
            putPointSearch(intentAttractionsMapActivity);
            putRsqlQuery(intentAttractionsMapActivity);
            putAttractionName(intentAttractionsMapActivity);
            putAttractionFilter(intentAttractionsMapActivity);
            intentAttractionsMapActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            attractionsListActivity.startActivity(intentAttractionsMapActivity);
        } else
            showToastOnUiThread(R.string.no_attractions_to_show_on_map);
    }

    private void putPointSearch(@NotNull Intent attractionMapsActivityIntent) {
        attractionMapsActivityIntent.putExtra(Constants.getPointSearch(), isPointSearchNull ? null : createPointSearch());
    }

    private void putRsqlQuery(Intent attractionMapsActivityIntent) {
        if (!isAttractionFilterNull())
            attractionMapsActivityIntent.putExtra(Constants.getRsqlQuery(), isRsqlEmpty() ? "0" : createRsqlString());
        else
            attractionMapsActivityIntent.putExtra(Constants.getRsqlQuery(), "0");
    }

    private void putAttractionName(Intent attractionMapsActivityIntent) {
        if (!isAttractionFilterNull())
            if (isSearchingForName())
                attractionMapsActivityIntent.putExtra(Constants.getName(), getAttractionFilterNameValue());
    }

    private void putAttractionFilter(@NotNull Intent attractionMapsActivityIntent) {
        attractionMapsActivityIntent.putExtra(Constants.getAccomodationFilter(), isAttractionFilterNull() ? null
                : attractionFilter);
    }

    private String getAttractionNameValueFromBottomSheetFilter() {
        return bottomSheetFilterAttractions.getAutoCompleteTextViewNameValue();
    }

    private String getCityNameValueFromBottomSheetFilter() {
        return extractCityName(bottomSheetFilterAttractions.getAutoCompleteTextViewCityValue());
    }

    @NotNull
    private Integer getPriceValueFromBottomSheetFilter() {
        return bottomSheetFilterAttractions.getSeekBarPriceValue();
    }

    @NotNull
    private Integer getRatingValueFromBottomSheetFilter() {
        return bottomSheetFilterAttractions.getSeekBarRatingValue();
    }

    @NotNull
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
            String cityName = extractCityName(getAttractionFilterCityValue());
            rsqlString = rsqlString.concat("address.city==" + cityName + ";");
        }
        return rsqlString;
    }

    private String extractCityName(@NotNull String city) {
        return city.contains(",") ? city.substring(0, city.lastIndexOf(",")) : city.trim();
    }

    private String checkPriceValue(String rsqlString) {
        if (!isAttractionFilterAvaragePriceEqualsToZero()) {
            if (isAttractionFilterAvaragePriceGreaterEqualsThan150())
                rsqlString = rsqlString.concat("avaragePrice=ge=" + getAttractionFilterAvaragePriceValue() + ";");
            else
                rsqlString = rsqlString.concat("avaragePrice=le=" + getAttractionFilterAvaragePriceValue() + ";");
        }
        return rsqlString;
    }

    private String checkRatingValue(String rsqlString) {
        if (!isAttractionFilterAvarageRatingEqualsToZero())
            rsqlString = rsqlString.concat("avarageRating=ge=" + getAttractionFilterAvarageRatingValue() + ";");
        return rsqlString;
    }

    private Double checkDistanceValue() {
        double distance = 1.0;
        if (!isAttractionFilterDistanceEqualsToZero())
            distance = getAttractionFilterDistanceValue();
        return distance;
    }

    private String checkCertificateOfExcellence(String rsqlString) {
        if (isAttractionFilterHasCertificateOfExcellence())
            rsqlString = rsqlString.concat("certificateOfExcellence==" + "true;");
        return rsqlString;
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
        return rsqlString;
    }

    private ProgressBar getProgressBarLoadMore() {
        return attractionsListActivity.getProgressBarAttractionLoadMore();
    }

    private Context getContext() {
        return attractionsListActivity.getApplicationContext();
    }

    public Toolbar getToolbar() {
        return attractionsListActivity.getToolbar();
    }

    private RecyclerView getRecyclerView() {
        return attractionsListActivity.getRecyclerViewAttractionsList();
    }

    private boolean isRsqlEmpty() {
        return createRsqlString().equals("");
    }

    private FragmentManager getSupportFragmentManager() {
        return attractionsListActivity.getSupportFragmentManager();
    }

    private AttractionDAO getAttractionDAO() {
        return daoFactory.getAttractionDAO(getStorageTechnology(Constants.getAttractionStorageTechnology()));
    }

    private CityDAO getCityDAO() {
        return daoFactory.getCityDAO(getStorageTechnology(Constants.getCityStorageTechnology()));
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

    @NotNull
    private String getString(int string) {
        return getResources().getString(string);
    }

    private Intent getIntent() {
        return attractionsListActivity.getIntent();
    }

    public PointSearch getPointSearch() {
        return (PointSearch) getIntent().getSerializableExtra(Constants.getPointSearch());
    }

    private boolean isBottomSheetFilterHotelsVisible() {
        return bottomSheetFilterAttractions.isAdded();
    }

    private void setIsLoadingData(boolean value) {
        isLoadingData = value;
    }

    private String getAttractionFilterNameValue() {
        return attractionFilter.getName();
    }

    private String getAttractionFilterCityValue() {
        return attractionFilter.getCity();
    }

    private Integer getAttractionFilterAvaragePriceValue() {
        return attractionFilter.getAvaragePrice();
    }

    private Integer getAttractionFilterAvarageRatingValue() {
        return attractionFilter.getAvarageRating();
    }

    private Double getAttractionFilterDistanceValue() {
        return attractionFilter.getDistance();
    }

    private boolean getAttractionFilterHasCertificateOfExcellenceValue() {
        return attractionFilter.isHasCertificateOfExcellence();
    }

    private boolean isSearchingForName() {
        return !getAttractionFilterNameValue().equals("");
    }

    private boolean isSearchingForCity() {
        return !getAttractionFilterCityValue().equals("");
    }

    private boolean isBottomSheetFilterHotelsNull() {
        return bottomSheetFilterAttractions == null;
    }

    private boolean isAttractionFilterNull() {
        return attractionFilter == null;
    }

    private boolean isAttractionFilterAvaragePriceEqualsToZero() {
        return getAttractionFilterAvaragePriceValue().equals(0);
    }

    private boolean isAttractionFilterAvaragePriceGreaterEqualsThan150() {
        return getAttractionFilterAvaragePriceValue() >= 150;
    }

    private boolean isAttractionFilterAvarageRatingEqualsToZero() {
        return getAttractionFilterAvarageRatingValue().equals(0);
    }

    private boolean isAttractionFilterDistanceEqualsToZero() {
        return getAttractionFilterDistanceValue().equals(0d);
    }

    private boolean isAttractionFilterHasCertificateOfExcellence() {
        return getAttractionFilterHasCertificateOfExcellenceValue();
    }

    private boolean checkTapTargetBooleanPreferences() {
        return new UserSharedPreferences(getContext()).constains(Constants.getTapTargetAttractionsList());
    }

    private void writeTapTargetBooleanPreferences() {
        new UserSharedPreferences(getContext()).putBooleanSharedPreferences(Constants.getTapTargetAttractionsList(), true);
    }

    public void setTapTargetSequence() {
        new TapTargetSequence(attractionsListActivity).targets(
                createTapTargetForToolbar(R.id.button_see_attractions_on_map, getString(R.string.see_on_map),
                        getString(R.string.see_on_map_tap_description), 50),
                createTapTargetForToolbar(R.id.search_button_menu_attractions_list, getString(R.string.search_attractions_list_tap_title),
                        getString(R.string.search_attractions_list_tap_description), 50))
                .listener(new TapTargetSequence.Listener() {
                    // This listener will tell us when interesting(tm) events happen in regards
                    // to the sequence
                    @Override
                    public void onSequenceFinish() {
                        writeTapTargetBooleanPreferences();
                        showBottomSheetFilters();
                        hasToShowBottomSheetFilter = false;
                    }

                    @Override
                    public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {
                        // Perform action for the current target
                    }

                    @Override
                    public void onSequenceCanceled(TapTarget lastTarget) {
                        // Boo
                    }
                }).start();

    }

    private TapTarget createTapTargetForToolbar(int menuItemId, String title, String body, int radius) {
        return TapTarget.forToolbarMenuItem(getToolbar(), menuItemId, title, body)
                .dimColor(android.R.color.black)
                .outerCircleColor(R.color.colorPrimary)
                .textColor(android.R.color.white)
                .targetCircleColor(R.color.white)
                .drawShadow(true)
                .cancelable(false)
                .tintTarget(true)
                .targetRadius(radius);
    }

}
