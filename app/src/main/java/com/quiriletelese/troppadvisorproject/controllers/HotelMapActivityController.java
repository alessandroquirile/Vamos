package com.quiriletelese.troppadvisorproject.controllers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.adapters.BottomSheetFilterHotels;
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
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBack;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class HotelMapActivityController implements GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener,
        View.OnClickListener, OnBottomSheetFilterSearchButtonClick, AutoCompleteTextViewsAccomodationFilterTextChangeListener,
        Constants {

    private HotelMapActivity hotelMapActivity;
    private BottomSheetFilterHotels bottomSheetFilterHotels = new BottomSheetFilterHotels();
    private AccomodationHotelFilter accomodationHotelFilter;
    private DAOFactory daoFactory = DAOFactory.getInstance();
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
                onBackPressed();
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

    private void findByRsqlHelper(VolleyCallBack volleyCallBack, PointSearch pointSearch, String rsqlQuery) {
        getHotelDAO().findByRsql(volleyCallBack, pointSearch, rsqlQuery, getContext(), 0, 10000);
    }

    private void findByNameLikeIgnoreCaseHelper(VolleyCallBack volleyCallBack, String name) {
        getHotelDAO().findByNameLikeIgnoreCase(volleyCallBack, name, getContext(), 0, 10000);
    }

    public void findHotelsNameHelper(VolleyCallBack volleyCallBack, String name) {
        getHotelDAO().findHotelsName(volleyCallBack, name, getContext());
    }

    public void findCitiesNameHelper(VolleyCallBack volleyCallBack, String name) {
        getCityDAO().findCitiesByName(volleyCallBack, name, getContext());
    }

    public void findByRsql(PointSearch pointSearch, String rsqlQuery) {
        findByRsqlHelper(new VolleyCallBack() {
            @Override
            public void onSuccess(Object object) {
                setMapOnSuccess((List<Hotel>) object);
            }

            @Override
            public void onError(String errorCode) {
                volleyCallbackOnError(errorCode);
            }

        }, pointSearch, rsqlQuery);
    }

    private void findByNameLikeIgnoreCase(String name) {
        findByNameLikeIgnoreCaseHelper(new VolleyCallBack() {
            @Override
            public void onSuccess(Object object) {
                setMapOnSuccess((List<Hotel>) object);
            }

            @Override
            public void onError(String errorCode) {
                volleyCallbackOnError(errorCode);
            }
        }, name);
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

    private void setMapOnSuccess(List<Hotel> hotels) {
        this.hotels = hotels;
        dismissBottomSheetFilterHotels();
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
        showToastVolleyError(R.string.no_restaurants_found_by_filter);
    }

    private void handleOtherVolleyError() {
        showToastVolleyError(R.string.unexpected_error_while_fetch_data);
    }

    private void showToastVolleyError(int string) {
        showToastOnUiThred(string);
    }

    private void showToastOnUiThred(int string) {
        hotelMapActivity.runOnUiThread(() -> {
            Toast.makeText(hotelMapActivity, getString(string), Toast.LENGTH_SHORT).show();
        });
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
            findByRsql(isSearchingForCity() ? null : createPointSearch(),
                    !createRsqlString().equals("") ? createRsqlString() : "0");
        } else
            findByNameLikeIgnoreCase(getHotelNameValueFromBottomSheetFilter());
    }

    private String getHotelNameValueFromBottomSheetFilter() {
        return bottomSheetFilterHotels.getAutoCompleteTextViewNameValue();
    }

    private String getCityNameValueFromBottomSheetFilter() {
        return extractCityName(bottomSheetFilterHotels.getAutoCompleteTextViewCityValue());
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
        return isDistanceSeekbarEnabled() && isDistanceDifferentFromZero() ?
                (double) bottomSheetFilterHotels.getSeekBarDistanceValue() : 1d;
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

    private void setAutoCompleteTextViewHotelNameAdapter(List<String> hotelsName) {
        getAutoCompleteTextViewName().setAdapter(createAutoCompleteTextViewAdapter(hotelsName));
    }

    private void setAutoCompleteTextViewHotelCityAdapter(List<String> citiesName) {
        getAutoCompleteTextViewCity().setAdapter(createAutoCompleteTextViewAdapter(citiesName));
    }

    private ArrayAdapter<String> createAutoCompleteTextViewAdapter(List<String> values) {
        return new ArrayAdapter<>(getContext(), getArrayAdapterLayout(), values);
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
            markers.add(addMarker(hotel));
    }

    private Marker addMarker(Hotel hotel) {
        return getGoogleMap().addMarker(createMarkerOptions(hotel));
    }

    private void clearAllMarkerOnMap() {
        getGoogleMap().clear();
        markers = new ArrayList<>();
    }

    private MarkerOptions createMarkerOptions(Hotel hotel) {
        return new MarkerOptions()
                .position(new LatLng(hotel.getPoint().getX(), hotel.getPoint().getY()))
                .icon(setCustomMarker(getContext(), getHotelMarker()))
                .title(hotel.getId());
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
        getGoogleMap().getUiSettings().setMyLocationButtonEnabled(false);
        getGoogleMap().getUiSettings().setMapToolbarEnabled(false);
        getGoogleMap().setMyLocationEnabled(true);
        getGoogleMap().setOnMarkerClickListener(this);
        getGoogleMap().setOnMapClickListener(this);
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
        getRelativeLayoutDetails().setVisibility(View.VISIBLE);
        getRelativeLayoutDetails().animate().translationY(0);
    }

    private void setRelativeLayoutHotelInformationInvisible() {
        isRelativeLayoutHotelInformationVisible = false;
        getRelativeLayoutDetails().animate().translationY(getRelativeLayoutDetails().getHeight() + 100);
    }

    private void setRelativeLayoutHotelInformationHotelFields(Marker marker) {
        hotel = getHotelFromMarkerClick(marker.getTitle());
        //setHotelImage(hotel);
        getTextViewName().setText(hotel.getName());
        getTextViewRating().setText(createAvarageRatingString(hotel));
        getTextViewAddress().setText(createAddressString(hotel));
    }

    private void setHotelImage(Hotel hotel) {
        if (hasImage(hotel))
            Picasso.with(getContext())
                    .load(hotel.getImages().get(0))
                    .placeholder(R.drawable.troppadvisor_logo)
                    .into(getImageViewHotel());
        else
            getImageViewHotel().setImageDrawable(null);
    }

    private boolean hasImage(Hotel hotel) {
        return hotel.isImagesGraterThanZero();
    }

    private String createAddressString(Hotel hotel) {
        String hotelAddress = "";
        hotelAddress = hotelAddress.concat(hotel.getTypeOfAddress() + " ");
        hotelAddress = hotelAddress.concat(hotel.getStreet() + ", ");
        hotelAddress = hotelAddress.concat(hotel.getHouseNumber() + ", ");
        hotelAddress = hotelAddress.concat(hotel.getCity() + ", ");
        hotelAddress = hotelAddress.concat(hotel.getProvince() + ", ");
        hotelAddress = hotelAddress.concat(hotel.getPostalCode());
        return hotelAddress;
    }

    private String createAvarageRatingString(Hotel hotel) {
        return !hasReviews(hotel) ? getString(R.string.no_reviews) : createAvarageRatingStringHelper(hotel);
    }

    private String createAvarageRatingStringHelper(Hotel hotel) {
        return hotel.getAvarageRating() + "/5 (" + hotel.getTotalReviews() + " " + getString(R.string.reviews) + ")";
    }

    private boolean hasReviews(Hotel hotel) {
        return !hotel.getAvarageRating().equals(0);
    }

    private Hotel getHotelFromMarkerClick(String hotelId) {
        Hotel hotelToReturn = null;
        for (Hotel hotel : hotels)
            if (hotel.getId().equals(hotelId)) {
                hotelToReturn = hotel;
                break;
            }
        return hotelToReturn;
    }

    private void setLinearLayoutSearchHotelsVisible() {
        isLinearLayoutSearchHotelsVisible = true;
        getLinearLayoutSearchHotels().animate().translationY(0);
    }

    private void setLinearLayoutSearchHotelsInvisible() {
        isLinearLayoutSearchHotelsVisible = false;
        getLinearLayoutSearchHotels().animate().translationY(-getLinearLayoutSearchHotels().getHeight() - 100);
    }

    private void setFloatingActionButtonCenterPositionOnHotelsVisible() {
        isFloatingActionButtonCenterPositionOnHotelsVisible = true;
        getFloatingActionButtonCenterPositionOnHotels().show();
    }

    private void setFloatingActionButtonCenterPositionOnHotelsInvisible() {
        isFloatingActionButtonCenterPositionOnHotelsVisible = false;
        getFloatingActionButtonCenterPositionOnHotels().hide();
    }

    private void showBottomSheetMapFilters() {
        bottomSheetFilterHotels.show(getSupportFragmentManager(), getTag());
        setBottomSheetFiltersFields();
        bottomSheetFilterHotels.setOnBottomSheetFilterSearchButtonClick(this);
        bottomSheetFilterHotels.setAutoCompleteTextViewsAccomodationFilterTextChangeListener(this);
    }

    public void setComponentProperties() {
        getRelativeLayoutDetails().animate().translationY(getRelativeLayoutDetails().getHeight() + 100);
    }

    public void setListenerOnViewComponents() {
        getTextViewSearchOnMap().setOnClickListener(this);
        getImageViewMapGoBack().setOnClickListener(this);
        getFloatingActionButtonCenterPositionOnHotels().setOnClickListener(this);
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

    private void dismissBottomSheetFilterHotels() {
        if (isBottomSheetFilterHotelsVisible())
            bottomSheetFilterHotels.dismiss();
    }

    private void onBackPressed() {
        hotelMapActivity.onBackPressed();
    }

    private FragmentManager getSupportFragmentManager() {
        return hotelMapActivity.getSupportFragmentManager();
    }

    private String getTag() {
        return bottomSheetFilterHotels.getTag();
    }

    private Context getContext() {
        return hotelMapActivity.getApplicationContext();
    }

    private Resources getResources() {
        return hotelMapActivity.getResources();
    }

    private String getString(int string) {
        return getResources().getString(string);
    }

    private Intent getIntent() {
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

    private AccomodationHotelFilter getAccomodationFilter() {
        return (AccomodationHotelFilter) getIntent().getSerializableExtra(ACCOMODATION_FILTER);
    }

    private AutoCompleteTextView getAutoCompleteTextViewName() {
        return bottomSheetFilterHotels.getAutoCompleteTextViewName();
    }

    private AutoCompleteTextView getAutoCompleteTextViewCity() {
        return bottomSheetFilterHotels.getAutoCompleteTextViewCity();
    }

    private int getArrayAdapterLayout() {
        return android.R.layout.select_dialog_item;
    }

    private GoogleMap getGoogleMap() {
        return hotelMapActivity.getGoogleMap();
    }

    private int getHotelMarker() {
        return R.drawable.hotel_marker;
    }

    private String getHotelName() {
        return getIntent().getStringExtra(NAME);
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

    private TextView getTextViewSearchOnMap() {
        return hotelMapActivity.getTextViewSearchOnMap();
    }

    private ImageView getImageViewMapGoBack() {
        return hotelMapActivity.getImageViewMapGoBack();
    }

    private RelativeLayout getRelativeLayoutDetails() {
        return hotelMapActivity.getRelativeLayoutDetails();
    }

    private TextView getTextViewName() {
        return hotelMapActivity.getTextViewName();
    }

    private TextView getTextViewRating() {
        return hotelMapActivity.getTextViewRating();
    }

    private TextView getTextViewAddress() {
        return hotelMapActivity.getTextViewAddress();
    }

    private ImageView getImageViewHotel() {
        return hotelMapActivity.getImageViewHotel();
    }

    private LinearLayout getLinearLayoutSearchHotels() {
        return hotelMapActivity.getLinearLayoutSearchHotels();
    }

    private FloatingActionButton getFloatingActionButtonCenterPositionOnHotels() {
        return hotelMapActivity.getFloatingActionButtonCenterPositionOnHotels();
    }

    private boolean isDistanceSeekbarEnabled() {
        return bottomSheetFilterHotels.getSeekBarDistance().isEnabled();
    }

    private boolean isDistanceDifferentFromZero() {
        return bottomSheetFilterHotels.getSeekBarDistanceValue() != 0;
    }

    private boolean isSearchingForName() {
        return !getAccomodationFilterNameValue().equals("");
    }

    private boolean isSearchingForCity() {
        return !getAccomodationFilterCityValue().equals("");
    }

    private boolean isBottomSheetFilterHotelsNull() {
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

    private boolean isBottomSheetFilterHotelsVisible() {
        return bottomSheetFilterHotels.isAdded();
    }

}
