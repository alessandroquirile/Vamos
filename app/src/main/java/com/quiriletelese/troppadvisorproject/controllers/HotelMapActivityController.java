package com.quiriletelese.troppadvisorproject.controllers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.androidbuts.multispinnerfilter.KeyPairBoolData;
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
import com.quiriletelese.troppadvisorproject.adapters.BottomSheetHotelMap;
import com.quiriletelese.troppadvisorproject.dao_interfaces.CityDAO;
import com.quiriletelese.troppadvisorproject.dao_interfaces.HotelDAO;
import com.quiriletelese.troppadvisorproject.dao_interfaces.TypeOfCuisineDAO;
import com.quiriletelese.troppadvisorproject.factories.DAOFactory;
import com.quiriletelese.troppadvisorproject.interfaces.AutoCompleteTextViewsAccomodationMapTextChangeListener;
import com.quiriletelese.troppadvisorproject.interfaces.Constants;
import com.quiriletelese.troppadvisorproject.interfaces.OnImageViewSearchMapFilterClick;
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
        View.OnClickListener, OnImageViewSearchMapFilterClick, AutoCompleteTextViewsAccomodationMapTextChangeListener, Constants {

    private HotelMapActivity hotelMapActivity;
    private BottomSheetHotelMap bottomSheetHotelMap;
    private DAOFactory daoFactory;
    private Hotel hotel = null;
    private List<Hotel> hotels = new ArrayList<>();
    private ArrayList<Marker> markers = new ArrayList<>();
    boolean isRelativeLayoutHotelInformationVisible = false, isLinearLayoutSearchHotelsVisible = true,
            isFloatingActionButtonCenterPositionOnHotelsVisible = true;

    public HotelMapActivityController(HotelMapActivity hotelMapActivity) {
        this.hotelMapActivity = hotelMapActivity;
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
    public void onImageViewSearchHotelMapFilterClick() {
        onImageViewSearchHotelMapFilterClickHelper();
    }

    @Override
    public void onAutoCompleteTextViewAccomodationNameTextChanged(String newText) {
        findHotelsNameHelper(newText);
    }

    @Override
    public void onAutoCompleteTextViewAccomodtionCityTextChanged(final String newText) {
        findCitiesNameHelper(newText);
    }

    private void findByRsql(VolleyCallBack volleyCallBack, PointSearch pointSearch, String rsqlQuery,
                            Context context, int page, int size) {
        daoFactory = DAOFactory.getInstance();
        HotelDAO hotelDAO = daoFactory.getHotelDAO(ConfigFileReader.getProperty(HOTEL_STORAGE_TECHNOLOGY,
                hotelMapActivity.getApplicationContext()));
        hotelDAO.findByRsql(volleyCallBack, pointSearch, rsqlQuery, context, page, size);
    }

    private void findByRsqlNoPoint(VolleyCallBack volleyCallBack, String rsqlQuery, Context context,
                                   int page, int size) {
        daoFactory = DAOFactory.getInstance();
        HotelDAO hotelDAO = daoFactory.getHotelDAO(ConfigFileReader.getProperty(HOTEL_STORAGE_TECHNOLOGY,
                hotelMapActivity.getApplicationContext()));
        hotelDAO.findByRsqlNoPoint(volleyCallBack, rsqlQuery, context, page, size);
    }

    private void findByNameLikeIgnoreCase(VolleyCallBack volleyCallBack, String name, Context context,
                                          int page, int size) {
        daoFactory = DAOFactory.getInstance();
        HotelDAO hotelDAO = daoFactory.getHotelDAO(ConfigFileReader.getProperty(HOTEL_STORAGE_TECHNOLOGY,
                hotelMapActivity.getApplicationContext()));
        hotelDAO.findByNameLikeIgnoreCase(volleyCallBack, name, context, page, size);
    }

    public void findAllByPointNear(VolleyCallBack volleyCallBack, PointSearch pointSearch) {
        daoFactory = DAOFactory.getInstance();
        HotelDAO hotelDAO = daoFactory.getHotelDAO(ConfigFileReader.getProperty(HOTEL_STORAGE_TECHNOLOGY,
                hotelMapActivity.getApplicationContext()));
        hotelDAO.findAllByPointNear(volleyCallBack, pointSearch, hotelMapActivity.getApplicationContext());
    }

    public void findHotelsName(VolleyCallBack volleyCallBack, String name) {
        daoFactory = DAOFactory.getInstance();
        HotelDAO hotelDAO = daoFactory.getHotelDAO(ConfigFileReader.getProperty(HOTEL_STORAGE_TECHNOLOGY,
                hotelMapActivity.getApplicationContext()));
        hotelDAO.findHotelsName(volleyCallBack, name, hotelMapActivity.getApplicationContext());
    }

    public void findCitiesName(VolleyCallBackCity volleyCallBackCity, String name) {
        daoFactory = DAOFactory.getInstance();
        CityDAO cityDAO = daoFactory.getCityDAO(ConfigFileReader.getProperty(CITY_STORAGE_TECHNOLOGY,
                hotelMapActivity.getApplicationContext()));
        cityDAO.findCitiesByName(volleyCallBackCity, name, hotelMapActivity.getApplicationContext());
    }

    private void onImageViewSearchHotelMapFilterClickHelper() {
        if (!isSearchingForName()) {
            if (isSearchingForCity()) {
                findByRsqlNoPointHelper();
            } else {
                if (isRsqlValuesCorrectlySetted()) {
                    findByRsqlHelper();
                } else
                    findAllByPointNearHelper(checkDistanceValue());
            }
        } else
            findByNameLikeIgnoreCaseHelper();
    }

    private void findByRsqlHelper() {
        PointSearch pointSearch = (PointSearch) hotelMapActivity.getIntent().
                getSerializableExtra(POINT_SEARCH);
        pointSearch.setDistance(checkDistanceValue());
        findByRsql(new VolleyCallBack() {
            @Override
            public void onSuccess(Object object) {
                hotels = (List<Hotel>) object;
                bottomSheetHotelMap.dismiss();
                addMarkersOnSuccess(hotels);
                zoomOnMap();

            }

            @Override
            public void onError(String errorCode) {
                if (errorCode.equals("204"))
                    showToastNoResults();
            }

        }, pointSearch, createRsqlString(), hotelMapActivity.getApplicationContext(), 0, 10000);
    }

    private void findByRsqlNoPointHelper() {
        findByRsqlNoPoint(new VolleyCallBack() {
            @Override
            public void onSuccess(Object object) {
                hotels = (List<Hotel>) object;
                bottomSheetHotelMap.dismiss();
                addMarkersOnSuccess(hotels);
                zoomOnMap();
            }

            @Override
            public void onError(String errorCode) {
                if (errorCode.equals("204"))
                    showToastNoResults();
            }
        }, createRsqlString(), hotelMapActivity.getApplicationContext(), 0, 10000);
    }

    private void findAllByPointNearHelper(Double distance) {
        PointSearch pointSearch = getPointSearch();
        pointSearch.setDistance(distance);
        findAllByPointNear(new VolleyCallBack() {
            @Override
            public void onSuccess(Object object) {
                hotels = (List<Hotel>) object;
                if (bottomSheetHotelMap != null && bottomSheetHotelMap.isAdded())
                    bottomSheetHotelMap.dismiss();
                addMarkersOnSuccess(hotels);
                zoomOnMap();
            }

            @Override
            public void onError(String errorCode) {
                if (errorCode.equals("204"))
                    showToastNoResults();
                showToastNoResults();
            }
        }, pointSearch);
    }

    private void findByNameLikeIgnoreCaseHelper() {
        findByNameLikeIgnoreCase(new VolleyCallBack() {
            @Override
            public void onSuccess(Object object) {
                List<Hotel> hotels = (List<Hotel>) object;
                bottomSheetHotelMap.dismiss();
                addMarkersOnSuccess(hotels);
                zoomOnMap();
            }

            @Override
            public void onError(String errorCode) {

            }
        }, bottomSheetHotelMap.getAutoCompleteTextViewMapHotelNameValue(), hotelMapActivity
                .getApplicationContext(), 0, 10000);
    }

    private void findHotelsNameHelper(String newText) {
        if (!newText.equals(""))
            findHotelsName(new VolleyCallBack() {
                @Override
                public void onSuccess(Object object) {
                    setAutoCompleteTextViewHotelNameAdapter((List<String>) object);
                }

                @Override
                public void onError(String errorCode) {

                }
            }, newText);
    }

    private void findCitiesNameHelper(String newText) {
        if (!newText.equals("")) {
            bottomSheetHotelMap.getSeekBarHotelMapDistance().setEnabled(false);
            findCitiesName(new VolleyCallBackCity() {
                @Override
                public void onSuccess(Object object) {
                    setAutoCompleteTextViewHotelCityAdapter((List<String>) object);
                }

                @Override
                public void onError(String error) {

                }
            }, newText);
        } else
            bottomSheetHotelMap.getSeekBarHotelMapDistance().setEnabled(true);
    }

    private String checkCityNameValue(String rsqlString) {
        if (!bottomSheetHotelMap.getAutoCompleteTextViewMapHotelCityValue().equals("")) {
            String cityName = extractCityName(bottomSheetHotelMap.getAutoCompleteTextViewMapHotelCityValue());
            rsqlString = rsqlString.concat("address.city==" + cityName + ";");
        }
        return rsqlString;
    }

    private String extractCityName(String city) {
        return city.substring(0, city.lastIndexOf(","));
    }

    private String checkPriceValue(String rsqlString) {
        if (!(bottomSheetHotelMap.getSeekBarHotelMapPriceValue() == 0)) {
            if (bottomSheetHotelMap.getSeekBarHotelMapPriceValue() == 150)
                rsqlString = rsqlString.concat("avaragePrice=ge=" + bottomSheetHotelMap.getSeekBarHotelMapPriceValue() + ";");
            else
                rsqlString = rsqlString.concat("avaragePrice=le=" + bottomSheetHotelMap.getSeekBarHotelMapPriceValue() + ";");
        }
        return rsqlString;
    }

    private String checkRatingValue(String rsqlString) {
        if (!(bottomSheetHotelMap.getSeekBarHotelMapRatingValue() == 0))
            rsqlString = rsqlString.concat("avarageRating=ge=" + bottomSheetHotelMap.getSeekBarHotelMapRatingValue() + ";");
        return rsqlString;
    }

    private String checkHotelStarsValue(String rsqlString) {
        if (!(bottomSheetHotelMap.getSeekBarHotelMapStarsValue() == 0))
            rsqlString = rsqlString.concat("stars=le=" + bottomSheetHotelMap.getSeekBarHotelMapStarsValue() + ";");
        return rsqlString;
    }

    private Double checkDistanceValue() {
        double distance = 1.0;
        if ((bottomSheetHotelMap.getSeekBarHotelMapDistanceValue() != 0))
            distance = bottomSheetHotelMap.getSeekBarHotelMapDistanceValue();
        return distance;
    }

    private String checkCertificateOfExcellence(String rsqlString) {
        if (bottomSheetHotelMap.getSwitchCompatHotelMapCertificateOfExcellenceIsSelected())
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

    private void setAutoCompleteTextViewHotelNameAdapter(List<String> hotelsName) {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(hotelMapActivity.getApplicationContext(),
                android.R.layout.select_dialog_item, hotelsName);
        bottomSheetHotelMap.getAutoCompleteTextViewMapHotelName().setAdapter(arrayAdapter);
    }

    private void setAutoCompleteTextViewHotelCityAdapter(List<String> citiesName) {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(hotelMapActivity.getApplicationContext(),
                android.R.layout.select_dialog_item, citiesName);
        bottomSheetHotelMap.getAutoCompleteTextViewMapHotelCity().setAdapter(arrayAdapter);
    }

    public void addMarkersOnMap() {
        findAllByPointNearHelper(2.0);
    }

    private void addMarkersOnSuccess(List<Hotel> hotels) {
        clearAllMarkerOnMap();
        for (Hotel hotel : hotels)
            markers.add(hotelMapActivity.getGoogleMap().addMarker(createMarkerOptions(hotel)));
    }

    private void clearAllMarkerOnMap() {
        hotelMapActivity.getGoogleMap().clear();
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
        isFloatingActionButtonCenterPositionOnHotelsVisible = true;
        hotelMapActivity.getFloatingActionButtonCenterPositionOnHotels().show();
    }

    private void setFloatingActionButtonCenterPositionOnHotelsInvisible() {
        isFloatingActionButtonCenterPositionOnHotelsVisible = false;
        hotelMapActivity.getFloatingActionButtonCenterPositionOnHotels().hide();
    }

    private void showBottomSheetMapFilters() {
        bottomSheetHotelMap = new BottomSheetHotelMap();
        bottomSheetHotelMap.show(hotelMapActivity.getSupportFragmentManager(), bottomSheetHotelMap.getTag());
        bottomSheetHotelMap.setOnImageViewSearchMapFilterClick(this);
        bottomSheetHotelMap.setAutoCompleteTextViewsAccomodationMapTextChangeListener(this);
    }

    private void showToastNoResults() {
        hotelMapActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(hotelMapActivity, hotelMapActivity.getResources().getString(R.string.no_hotels_found_by_filter), Toast.LENGTH_SHORT).show();
            }
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

    private PointSearch getPointSearch() {
        return (PointSearch) hotelMapActivity.getIntent().getSerializableExtra(POINT_SEARCH);
    }

    private boolean isSearchingForName() {
        return !bottomSheetHotelMap.getAutoCompleteTextViewMapHotelNameValue().equals("");
    }

    private boolean isSearchingForCity() {
        return !bottomSheetHotelMap.getAutoCompleteTextViewMapHotelCityValue().equals("");
    }

    private boolean isRsqlValuesCorrectlySetted() {
        return !createRsqlString().equals("");
    }

}
