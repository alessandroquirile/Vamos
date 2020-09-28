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
import com.quiriletelese.troppadvisorproject.adapters.BottomSheetFilterHotels;
import com.quiriletelese.troppadvisorproject.dao_interfaces.CityDAO;
import com.quiriletelese.troppadvisorproject.dao_interfaces.HotelDAO;
import com.quiriletelese.troppadvisorproject.factories.DAOFactory;
import com.quiriletelese.troppadvisorproject.interfaces.AutoCompleteTextViewsAccomodationFilterTextChangeListener;
import com.quiriletelese.troppadvisorproject.interfaces.Constants;
import com.quiriletelese.troppadvisorproject.interfaces.OnBottomSheetFilterSearchButtonClick;
import com.quiriletelese.troppadvisorproject.model_helpers.AccomodationHotelFilter;
import com.quiriletelese.troppadvisorproject.model_helpers.Address;
import com.quiriletelese.troppadvisorproject.model_helpers.PointSearch;
import com.quiriletelese.troppadvisorproject.models.Hotel;
import com.quiriletelese.troppadvisorproject.utils.ConfigFileReader;
import com.quiriletelese.troppadvisorproject.views.HotelMapActivity;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBack;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBackCity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class HotelMapActivityController implements GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener,
        View.OnClickListener, OnBottomSheetFilterSearchButtonClick,
        AutoCompleteTextViewsAccomodationFilterTextChangeListener, Constants {

    private HotelMapActivity hotelMapActivity;
    private BottomSheetFilterHotels bottomSheetFilterHotels;
    private AccomodationHotelFilter accomodationHotelFilter;
    private DAOFactory daoFactory = DAOFactory.getInstance();;
    private Hotel hotel = null;
    private List<Hotel> hotels = new ArrayList<>();
    private ArrayList<Marker> markers = new ArrayList<>();
    boolean isRelativeLayoutHotelInformationVisible = false, isLinearLayoutSearchHotelsVisible = true,
            isFloatingActionButtonCenterPositionOnHotelsVisible = true;

    public HotelMapActivityController(HotelMapActivity hotelMapActivity) {
        this.hotelMapActivity = hotelMapActivity;
        accomodationHotelFilter = getAccomodationFilter();
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
            case R.id.text_view_search_hotels_on_map:
                showBottomSheetMapFilters();
                break;
            case R.id.image_view_hotel_map_go_back:
                hotelMapActivity.onBackPressed();
                break;
            case R.id.floating_action_button_center_position_on_hotels:
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
        findHotelsName(newText);
    }

    @Override
    public void onAutoCompleteTextViewAccomodtionCityTextChanged(final String newText) {
        findCitiesName(newText);
    }

    private void findByRsqlHelper(VolleyCallBack volleyCallBack, PointSearch pointSearch, String rsqlQuery,
                                  Context context, int page, int size) {
        HotelDAO hotelDAO = daoFactory.getHotelDAO(ConfigFileReader.getProperty(HOTEL_STORAGE_TECHNOLOGY,
                hotelMapActivity.getApplicationContext()));
        hotelDAO.findByRsql(volleyCallBack, pointSearch, rsqlQuery, context, page, size);
    }

    private void findByNameLikeIgnoreCaseHelper(VolleyCallBack volleyCallBack, String name, Context context,
                                                int page, int size) {
        HotelDAO hotelDAO = daoFactory.getHotelDAO(ConfigFileReader.getProperty(HOTEL_STORAGE_TECHNOLOGY,
                hotelMapActivity.getApplicationContext()));
        hotelDAO.findByNameLikeIgnoreCase(volleyCallBack, name, context, page, size);
    }

    public void findHotelsNameHelper(VolleyCallBack volleyCallBack, String name) {
        HotelDAO hotelDAO = daoFactory.getHotelDAO(ConfigFileReader.getProperty(HOTEL_STORAGE_TECHNOLOGY,
                hotelMapActivity.getApplicationContext()));
        hotelDAO.findHotelsName(volleyCallBack, name, hotelMapActivity.getApplicationContext());
    }

    public void findCitiesNameHelper(VolleyCallBackCity volleyCallBackCity, String name) {
        CityDAO cityDAO = daoFactory.getCityDAO(ConfigFileReader.getProperty(CITY_STORAGE_TECHNOLOGY,
                hotelMapActivity.getApplicationContext()));
        cityDAO.findCitiesByName(volleyCallBackCity, name, hotelMapActivity.getApplicationContext());
    }

    public void findByRsql(PointSearch pointSearch, String rsqlQuery) {
        findByRsqlHelper(new VolleyCallBack() {
            @Override
            public void onSuccess(Object object) {
                setMapOnSuccess((List<Hotel>) object);

            }

            @Override
            public void onError(String errorCode) {
                if (errorCode.equals("204"))
                    showToastNoResults();
            }

        }, pointSearch, rsqlQuery, getContext(), 0, 10000);
    }

    private void findByNameLikeIgnoreCase(String name) {
        findByNameLikeIgnoreCaseHelper(new VolleyCallBack() {
            @Override
            public void onSuccess(Object object) {
                setMapOnSuccess((List<Hotel>) object);
            }

            @Override
            public void onError(String errorCode) {

            }
        }, name, getContext(), 0, 10000);
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

    private void setMapOnSuccess(List<Hotel> hotels) {
        this.hotels = hotels;
        if (isBottomSheetFilterHotelsVisible())
            bottomSheetFilterHotels.dismiss();
        addMarkersOnSuccess(hotels);
        zoomOnMap();
    }

    private void onBottomSheetFilterSearchButtonClickHelper() {
        createAccomodationFilter();
        detectSearchType();
    }

    private void disableFieldsOnAutoCompleteTextViewNameChanged() {
        bottomSheetFilterHotels.setAutoCompleteTextViewCityText("");
        bottomSheetFilterHotels.setSeekBarPriceEnabled(false);
        bottomSheetFilterHotels.setSeekBarRatingEnabled(false);
        bottomSheetFilterHotels.setSeekBarDistanceEnabled(false);
        bottomSheetFilterHotels.setSwitchCompatCertificateOfExcellenceEnabled(false);
    }

    private void enableFieldsOnAutoCompleteTextViewNameChanged() {
        bottomSheetFilterHotels.setSeekBarPriceEnabled(true);
        bottomSheetFilterHotels.setSeekBarRatingEnabled(true);
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
        bottomSheetFilterHotels.setAutoCompleteTextViewNameText(getAccomodationFilterNameValue());
        bottomSheetFilterHotels.setAutoCompleteTextViewCityText(getAccomodationFilterCityValue());
        bottomSheetFilterHotels.setSeekBarPriceProgress(getAccomodationFilterAvaragePriceValue());
        bottomSheetFilterHotels.setSeekBarRatingProgress(getAccomodationFilterAvarageRatingValue());
        bottomSheetFilterHotels.setSeekBarStarsProgress(getAccomodationFilterStarsValue());
        bottomSheetFilterHotels.setSeekBarDistanceProgress(getAccomodationFilterDistanceValue().intValue());
        if (isSearchingForCity() || isSearchingForName())
            bottomSheetFilterHotels.setSeekBarDistanceEnabled(false);
        bottomSheetFilterHotels.setSwitchCompatCertificateOfExcellenceChecked(isAccomodationFilterHasCertificateOfExcellence());
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

    private void detectSearchType() {
        if (!isSearchingForName()) {
            findByRsql(isSearchingForCity() ? createNullPointSearch() : createPointSearch(),
                    !createRsqlString().equals("") ? createRsqlString() : "0");
        } else
            findByNameLikeIgnoreCase(getHotelNameValueFromBottomSheetFilter());
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
        Double distance = 0d;
        if (bottomSheetFilterHotels.getSeekBarDistance().isEnabled()) {
            if (bottomSheetFilterHotels.getSeekBarDistanceValue() != 0)
                distance = (double) bottomSheetFilterHotels.getSeekBarDistanceValue();
            else
                distance = 1d;
        }
        return distance;
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
        if (!isAccomodationFilterStarsEqualsToZero())
            rsqlString = rsqlString.concat("stars=le=" + getAccomodationFilterStarsValue() + ";");
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
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(hotelMapActivity.getApplicationContext(),
                android.R.layout.select_dialog_item, hotelsName);
        bottomSheetFilterHotels.getAutoCompleteTextViewName().setAdapter(arrayAdapter);
    }

    private void setAutoCompleteTextViewHotelCityAdapter(List<String> citiesName) {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(hotelMapActivity.getApplicationContext(),
                android.R.layout.select_dialog_item, citiesName);
        bottomSheetFilterHotels.getAutoCompleteTextViewCity().setAdapter(arrayAdapter);
    }

    public void addMarkersOnMap() {
        if (isIntentSearchingForName())
            findByNameLikeIgnoreCase(getHotelName());
        else
            findByRsql(getPointSearch(), getRsqlQuery());
    }

    private void addMarkersOnSuccess(List<Hotel> hotels) {
        clearAllMarkerOnMap();
        for (Hotel hotel : hotels)
            markers.add(hotelMapActivity.getGoogleMap().addMarker(createMarkerOptions(hotel)));
    }

    private void clearAllMarkerOnMap() {
        hotelMapActivity.getGoogleMap().clear();
        markers = new ArrayList<>();
    }

    private MarkerOptions createMarkerOptions(Hotel hotel) {
        return new MarkerOptions()
                .position(new LatLng(hotel.getPoint().getX(), hotel.getPoint().getY()))
                .icon(setCustomMarker(hotelMapActivity.getApplicationContext(), R.drawable.hotel_marker))
                .title(hotel.getName());
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
        hotelMapActivity.getGoogleMap().animateCamera(cu);
    }

    @SuppressLint("MissingPermission")
    public void setMapProperties() {
        hotelMapActivity.getGoogleMap().getUiSettings().setMyLocationButtonEnabled(false);
        hotelMapActivity.getGoogleMap().getUiSettings().setMapToolbarEnabled(false);
        hotelMapActivity.getGoogleMap().setMyLocationEnabled(true);
        hotelMapActivity.getGoogleMap().setOnMarkerClickListener(this);
        hotelMapActivity.getGoogleMap().setOnMapClickListener(this);
    }

    private void onMapClickHelper() {
        if (hotel != null) {
            if (isRelativeLayoutHotelInformationVisible)
                setRelativeLayoutHotelInformationInvisible();
            else
                setRelativeLayoutHotelInformationVisible();
        }
        if (isLinearLayoutSearchHotelsVisible)
            setLinearLayoutSearchHotelsInvisible();
        else
            setLinearLayoutSearchHotelsVisible();
        if (isFloatingActionButtonCenterPositionOnHotelsVisible)
            setFloatingActionButtonCenterPositionOnHotelsInvisible();
        else
            setFloatingActionButtonCenterPositionOnHotelsVisible();
    }

    private void onMarkerClickHelper(Marker marker) {
        marker.showInfoWindow();
        if (!isRelativeLayoutHotelInformationVisible)
            setRelativeLayoutHotelInformationVisible();
        if (!isLinearLayoutSearchHotelsVisible)
            setLinearLayoutSearchHotelsVisible();
        if (!isFloatingActionButtonCenterPositionOnHotelsVisible)
            setFloatingActionButtonCenterPositionOnHotelsVisible();
        setRelativeLayoutHotelInformationHotelFields(marker);
    }

    private void setRelativeLayoutHotelInformationVisible() {
        isRelativeLayoutHotelInformationVisible = true;
        hotelMapActivity.getRelativeLayoutHotelInformation().setVisibility(View.VISIBLE);
        hotelMapActivity.getRelativeLayoutHotelInformation().animate().translationY(0);
    }

    private void setRelativeLayoutHotelInformationInvisible() {
        isRelativeLayoutHotelInformationVisible = false;
        hotelMapActivity.getRelativeLayoutHotelInformation().animate().translationY(hotelMapActivity.
                getRelativeLayoutHotelInformation().getHeight() + 100);
    }

    private void setRelativeLayoutHotelInformationHotelFields(Marker marker) {
        hotel = getHotelFromMarkerClick(marker.getTitle());
        //setHotelImage(hotel);
        hotelMapActivity.getTextViewHotelName().setText(hotel.getName());
        hotelMapActivity.getTextViewHotelRating().setText(createReviewString(hotel.getAvarageRating()));
        hotelMapActivity.getTextViewHotelAddress().setText(createAddressString(hotel.getAddress()));
    }

    private void setHotelImage(Hotel hotel) {
        if (hasImage(hotel))
            Picasso.with(hotelMapActivity.getApplicationContext())
                    .load(hotel.getImages().get(0))
                    .placeholder(R.drawable.troppadvisor_logo)
                    .into(hotelMapActivity.getImageViewHotel());
        else
            hotelMapActivity.getImageViewHotel().setImageDrawable(null);
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
            return hotelMapActivity.getResources().getString(R.string.no_review);
        else {
            String rating = "";
            rating = rating.concat(review + "/5");
            return rating;
        }
    }

    private Hotel getHotelFromMarkerClick(String hotelName) {
        Hotel hotelToReturn = null;
        for (Hotel hotel : hotels)
            if (hotel.getName().equals(hotelName)) {
                hotelToReturn = hotel;
                break;
            }
        return hotelToReturn;
    }

    private void setLinearLayoutSearchHotelsVisible() {
        isLinearLayoutSearchHotelsVisible = true;
        hotelMapActivity.getLinearLayoutSearchHotels().animate().translationY(0);
    }

    private void setLinearLayoutSearchHotelsInvisible() {
        isLinearLayoutSearchHotelsVisible = false;
        hotelMapActivity.getLinearLayoutSearchHotels().animate().translationY(-hotelMapActivity.
                getLinearLayoutSearchHotels().getHeight() - 100);
    }

    private void setFloatingActionButtonCenterPositionOnHotelsVisible() {
        isFloatingActionButtonCenterPositionOnHotelsVisible = true;
        hotelMapActivity.getFloatingActionButtonCenterPositionOnHotels().show();
    }

    private void setFloatingActionButtonCenterPositionOnHotelsInvisible() {
        isFloatingActionButtonCenterPositionOnHotelsVisible = false;
        hotelMapActivity.getFloatingActionButtonCenterPositionOnHotels().hide();
    }

    private void showBottomSheetMapFilters() {
        bottomSheetFilterHotels = new BottomSheetFilterHotels();
        bottomSheetFilterHotels.show(hotelMapActivity.getSupportFragmentManager(), bottomSheetFilterHotels.getTag());
        setBottomSheetFiltersFields();
        bottomSheetFilterHotels.setOnBottomSheetFilterSearchButtonClick(this);
        bottomSheetFilterHotels.setAutoCompleteTextViewsAccomodationFilterTextChangeListener(this);
    }

    private void showToastNoResults() {
        hotelMapActivity.runOnUiThread(() -> {
            Toast.makeText(hotelMapActivity, hotelMapActivity.getResources().getString(R.string.no_hotels_found_by_filter), Toast.LENGTH_SHORT).show();
        });
    }

    public void setComponentProperties() {
        hotelMapActivity.getRelativeLayoutHotelInformation().animate().translationY(hotelMapActivity.
                getRelativeLayoutHotelInformation().getHeight() + 100);
    }

    public void setListenerOnViewComponents() {
        hotelMapActivity.getTextViewSearchHotelsOnMap().setOnClickListener(this);
        hotelMapActivity.getImageViewHotelMapGoBack().setOnClickListener(this);
        hotelMapActivity.getFloatingActionButtonCenterPositionOnHotels().setOnClickListener(this);
    }

    private Context getContext() {
        return hotelMapActivity.getApplicationContext();
    }

    private Intent getIntent(){
        return hotelMapActivity.getIntent();
    }

    public PointSearch getPointSearch() {
        return (PointSearch) getIntent().getSerializableExtra(POINT_SEARCH);
    }

    private String getRsqlQuery() {
        return getIntent().getStringExtra(RSQL_QUERY);
    }

    private boolean isIntentSearchingForName() {
        return getIntent().getBooleanExtra(SEARCH_FOR_NAME, false);
    }

    private AccomodationHotelFilter getAccomodationFilter(){
        return (AccomodationHotelFilter) getIntent().getSerializableExtra(ACCOMODATION_FILTER);
    }

    private String getHotelName() {
        return getIntent().getStringExtra(NAME);
    }

    private boolean isBottomSheetFilterHotelsVisible() {
        return bottomSheetFilterHotels != null && bottomSheetFilterHotels.isAdded();
    }

    private boolean isAccomodationFilterNull() {
        return accomodationHotelFilter == null;
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

    private boolean isAccomodationFilterAvaragePriceEqualsToZero() {
        return getAccomodationFilterAvaragePriceValue().equals(0);
    }

    private boolean isAccomodationFilterAvaragePriceGreaterEqualsThan150() {
        return getAccomodationFilterAvaragePriceValue() >= 150;
    }

    private boolean isAccomodationFilterAvarageRatingEqualsToZero() {
        return getAccomodationFilterAvarageRatingValue().equals(0);
    }

    private boolean isAccomodationFilterStarsEqualsToZero() {
        return getAccomodationFilterStarsValue().equals(0);
    }

    private boolean isAccomodationFilterDistanceEqualsToZero() {
        return getAccomodationFilterDistanceValue().equals(0d);
    }

    private boolean isAccomodationFilterHasCertificateOfExcellence() {
        return getAccomodationFilterHasCertificateOfExcellenceValue();
    }

}
