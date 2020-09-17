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
import com.quiriletelese.troppadvisorproject.adapters.BottomSheetAttractionMap;
import com.quiriletelese.troppadvisorproject.dao_interfaces.AttractionDAO;
import com.quiriletelese.troppadvisorproject.dao_interfaces.CityDAO;
import com.quiriletelese.troppadvisorproject.factories.DAOFactory;
import com.quiriletelese.troppadvisorproject.interfaces.AutoCompleteTextViewsAccomodationMapTextChangeListener;
import com.quiriletelese.troppadvisorproject.interfaces.Constants;
import com.quiriletelese.troppadvisorproject.interfaces.OnImageViewSearchMapFilterClick;
import com.quiriletelese.troppadvisorproject.model_helpers.Address;
import com.quiriletelese.troppadvisorproject.model_helpers.PointSearch;
import com.quiriletelese.troppadvisorproject.models.Attraction;
import com.quiriletelese.troppadvisorproject.utils.ConfigFileReader;
import com.quiriletelese.troppadvisorproject.views.AttractionMapActivity;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBack;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBackCity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AttractionMapActivityController implements GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener,
        View.OnClickListener, OnImageViewSearchMapFilterClick,
        AutoCompleteTextViewsAccomodationMapTextChangeListener, Constants {

    private AttractionMapActivity attractionMapActivity;
    private BottomSheetAttractionMap bottomSheetAttractionMap;
    private DAOFactory daoFactory;
    private Attraction attraction = null;
    private List<Attraction> attractions = new ArrayList<>();
    private ArrayList<Marker> markers = new ArrayList<>();
    boolean isRelativeLayoutAttractionInformationVisible = false, isLinearLayoutSearchAttractionVisible = true,
            isFloatingActionButtonCenterPositionOnAttractionsVisible = true;

    public AttractionMapActivityController(AttractionMapActivity attractionMapActivity) {
        this.attractionMapActivity = attractionMapActivity;
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
            case R.id.text_view_search_attractions_on_map:
                showBottomSheetMapFilters();
                break;
            case R.id.image_view_attraction_map_go_back:
                attractionMapActivity.onBackPressed();
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
                            int page, int size) {
        daoFactory = DAOFactory.getInstance();
        AttractionDAO attractionDAO = daoFactory.getAttractionDAO(ConfigFileReader.getProperty(HOTEL_STORAGE_TECHNOLOGY,
                attractionMapActivity.getApplicationContext()));
        attractionDAO.findByRsql(volleyCallBack, pointSearch, rsqlQuery, attractionMapActivity.getApplicationContext(), page, size);
    }

    private void findByRsqlNoPoint(VolleyCallBack volleyCallBack, String rsqlQuery, int page, int size) {
        daoFactory = DAOFactory.getInstance();
        AttractionDAO attractionDAO = daoFactory.getAttractionDAO(ConfigFileReader.getProperty(HOTEL_STORAGE_TECHNOLOGY,
                attractionMapActivity.getApplicationContext()));
        attractionDAO.findByRsqlNoPoint(volleyCallBack, rsqlQuery, attractionMapActivity.getApplicationContext(), page, size);
    }

    private void findByNameLikeIgnoreCase(VolleyCallBack volleyCallBack, String name, int page, int size) {
        daoFactory = DAOFactory.getInstance();
        AttractionDAO attractionDAO = daoFactory.getAttractionDAO(ConfigFileReader.getProperty(HOTEL_STORAGE_TECHNOLOGY,
                attractionMapActivity.getApplicationContext()));
        attractionDAO.findByNameLikeIgnoreCase(volleyCallBack, name, attractionMapActivity.getApplicationContext(), page, size);
    }

    public void findAllByPointNear(VolleyCallBack volleyCallBack, PointSearch pointSearch) {
        daoFactory = DAOFactory.getInstance();
        AttractionDAO attractionDAO = daoFactory.getAttractionDAO(ConfigFileReader.getProperty(HOTEL_STORAGE_TECHNOLOGY,
                attractionMapActivity.getApplicationContext()));
        attractionDAO.findAllByPointNear(volleyCallBack, pointSearch, attractionMapActivity.getApplicationContext());
    }

    public void findAttractionsName(VolleyCallBack volleyCallBack, String name) {
        daoFactory = DAOFactory.getInstance();
        AttractionDAO attractionDAO = daoFactory.getAttractionDAO(ConfigFileReader.getProperty(HOTEL_STORAGE_TECHNOLOGY,
                attractionMapActivity.getApplicationContext()));
        attractionDAO.findHotelsName(volleyCallBack, name, attractionMapActivity.getApplicationContext());
    }

    public void findCitiesName(VolleyCallBackCity volleyCallBackCity, String name) {
        daoFactory = DAOFactory.getInstance();
        CityDAO cityDAO = daoFactory.getCityDAO(ConfigFileReader.getProperty(CITY_STORAGE_TECHNOLOGY,
                attractionMapActivity.getApplicationContext()));
        cityDAO.findCitiesByName(volleyCallBackCity, name, attractionMapActivity.getApplicationContext());
    }

    private void onImageViewSearchHotelMapFilterClickHelper() {
        if (!isSearchingForName()) {
            if (isSearchingForCity()) {
                findByRsqlNoPointHelper();
            } else {
                if (isRsqlValuesCorrectlySetted()) {
                    System.out.println(createRsqlString());
                    findByRsqlHelper();
                } else
                    findAllByPointNearHelper(checkDistanceValue());
            }
        } else
            findByNameLikeIgnoreCaseHelper();
    }

    private void findByRsqlHelper() {
        PointSearch pointSearch = (PointSearch) attractionMapActivity.getIntent().
                getSerializableExtra(POINT_SEARCH);
        pointSearch.setDistance(checkDistanceValue());
        findByRsql(new VolleyCallBack() {
            @Override
            public void onSuccess(Object object) {
                attractions = (List<Attraction>) object;
                bottomSheetAttractionMap.dismiss();
                addMarkersOnSuccess(attractions);
                zoomOnMap();
            }

            @Override
            public void onError(String errorCode) {
                if (errorCode.equals("204"))
                    showToastNoResults();
            }

        }, pointSearch, createRsqlString(), 0, 10000);
    }

    private void findByRsqlNoPointHelper() {
        findByRsqlNoPoint(new VolleyCallBack() {
            @Override
            public void onSuccess(Object object) {
                attractions = (List<Attraction>) object;
                bottomSheetAttractionMap.dismiss();
                addMarkersOnSuccess(attractions);
                zoomOnMap();
            }

            @Override
            public void onError(String errorCode) {
                if (errorCode.equals("204"))
                    showToastNoResults();
            }
        }, createRsqlString(), 0, 10000);
    }

    private void findAllByPointNearHelper(Double distance) {
        PointSearch pointSearch = getPointSearch();
        pointSearch.setDistance(distance);
        findAllByPointNear(new VolleyCallBack() {
            @Override
            public void onSuccess(Object object) {
                attractions = (List<Attraction>) object;
                if (bottomSheetAttractionMap != null && bottomSheetAttractionMap.isAdded())
                    bottomSheetAttractionMap.dismiss();
                addMarkersOnSuccess(attractions);
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
                attractions = (List<Attraction>) object;
                bottomSheetAttractionMap.dismiss();
                addMarkersOnSuccess(attractions);
                zoomOnMap();
            }

            @Override
            public void onError(String errorCode) {

            }
        }, bottomSheetAttractionMap.getAutoCompleteTextViewMapAttractionNameValue(), 0, 10000);
    }

    private void findHotelsNameHelper(String newText) {
        if (!newText.equals(""))
            findAttractionsName(new VolleyCallBack() {
                @Override
                public void onSuccess(Object object) {
                    setAutoCompleteTextViewAttractionNameAdapter((List<String>) object);
                }

                @Override
                public void onError(String errorCode) {

                }
            }, newText);
    }

    private void findCitiesNameHelper(String newText) {
        if (!newText.equals("")) {
            bottomSheetAttractionMap.getSeekBarAttractionMapDistance().setEnabled(false);
            findCitiesName(new VolleyCallBackCity() {
                @Override
                public void onSuccess(Object object) {
                    setAutoCompleteTextViewAttractionCityAdapter((List<String>) object);
                }

                @Override
                public void onError(String error) {

                }
            }, newText);
        } else
            bottomSheetAttractionMap.getSeekBarAttractionMapDistance().setEnabled(true);
    }

    private String checkCityNameValue(String rsqlString) {
        if (!bottomSheetAttractionMap.getAutoCompleteTextViewMapAttractionCityValue().equals("")) {
            String cityName = extractCityName(bottomSheetAttractionMap.getAutoCompleteTextViewMapAttractionCityValue());
            rsqlString = rsqlString.concat("address.city==" + cityName + ";");
        }
        return rsqlString;
    }

    private String extractCityName(String city) {
        return city.substring(0, city.lastIndexOf(","));
    }

    private String checkPriceValue(String rsqlString) {
        if (!(bottomSheetAttractionMap.getSeekBarAttractionMapPriceValue() == 0)) {
            if (bottomSheetAttractionMap.getSeekBarAttractionMapPriceValue() == 150)
                rsqlString = rsqlString.concat("avaragePrice=ge=" + bottomSheetAttractionMap.getSeekBarAttractionMapPriceValue() + ";");
            else
                rsqlString = rsqlString.concat("avaragePrice=le=" + bottomSheetAttractionMap.getSeekBarAttractionMapPriceValue() + ";");
        }
        return rsqlString;
    }

    private String checkRatingValue(String rsqlString) {
        if (!(bottomSheetAttractionMap.getSeekBarAttractionMapRatingValue() == 0))
            rsqlString = rsqlString.concat("avarageRating=ge=" + bottomSheetAttractionMap.getSeekBarAttractionMapRatingValue() + ";");
        return rsqlString;
    }

    private Double checkDistanceValue() {
        double distance = 1.0;
        if ((bottomSheetAttractionMap.getSeekBarAttractionMapDistanceValue() != 0))
            distance = bottomSheetAttractionMap.getSeekBarAttractionMapDistanceValue();
        return distance;
    }

    private String checkCertificateOfExcellence(String rsqlString) {
        if (bottomSheetAttractionMap.getSwitchCompatAttractionMapCertificateOfExcellenceIsSelected())
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

    private void setAutoCompleteTextViewAttractionNameAdapter(List<String> hotelsName) {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(attractionMapActivity.getApplicationContext(),
                android.R.layout.select_dialog_item, hotelsName);
        bottomSheetAttractionMap.getAutoCompleteTextViewMapAttractionName().setAdapter(arrayAdapter);
    }

    private void setAutoCompleteTextViewAttractionCityAdapter(List<String> citiesName) {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(attractionMapActivity.getApplicationContext(),
                android.R.layout.select_dialog_item, citiesName);
        bottomSheetAttractionMap.getAutoCompleteTextViewMapAttractionCity().setAdapter(arrayAdapter);
    }

    public void addMarkersOnMap() {
        findAllByPointNearHelper(2.0);
    }

    private void addMarkersOnSuccess(List<Attraction> attractions) {
        clearAllMarkerOnMap();
        for (Attraction attraction : attractions)
            markers.add(attractionMapActivity.getGoogleMap().addMarker(createMarkerOptions(attraction)));
    }

    private void clearAllMarkerOnMap() {
        attractionMapActivity.getGoogleMap().clear();
    }

    private MarkerOptions createMarkerOptions(Attraction attraction) {
        return new MarkerOptions()
                .position(new LatLng(attraction.getPoint().getX(), attraction.getPoint().getY()))
                .icon(setCustomMarker(attractionMapActivity.getApplicationContext(), R.drawable.hotel_marker))
                .title(attraction.getId());
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
        attractionMapActivity.getGoogleMap().animateCamera(cu);
    }

    @SuppressLint("MissingPermission")
    public void setMapProperties() {
        attractionMapActivity.getGoogleMap().getUiSettings().setMyLocationButtonEnabled(false);
        attractionMapActivity.getGoogleMap().getUiSettings().setMapToolbarEnabled(false);
        attractionMapActivity.getGoogleMap().setMyLocationEnabled(true);
        attractionMapActivity.getGoogleMap().setOnMarkerClickListener(this);
        attractionMapActivity.getGoogleMap().setOnMapClickListener(this);
    }

    private void onMapClickHelper() {
        if (attraction != null) {
            if (isRelativeLayoutAttractionInformationVisible)
                setRelativeLayoutAttractionInformationInvisible();
            else
                setRelativeLayoutAttractionInformationVisible();
        }
        if (isLinearLayoutSearchAttractionVisible)
            setLinearLayoutSearchAttractionsInvisible();
        else
            setLinearLayoutSearchAttractionsVisible();
        if (isFloatingActionButtonCenterPositionOnAttractionsVisible)
            setFloatingActionButtonCenterPositionOnAttractionsInvisible();
        else
            setFloatingActionButtonCenterPositionOnAttractionsVisible();
    }

    private void onMarkerClickHelper(Marker marker) {
        if (!isRelativeLayoutAttractionInformationVisible)
            setRelativeLayoutAttractionInformationVisible();
        if (!isLinearLayoutSearchAttractionVisible)
            setLinearLayoutSearchAttractionsVisible();
        if (!isFloatingActionButtonCenterPositionOnAttractionsVisible)
            setFloatingActionButtonCenterPositionOnAttractionsVisible();
        setRelativeLayoutAttractionInformationHotelFields(marker);
    }

    private void setRelativeLayoutAttractionInformationVisible() {
        isRelativeLayoutAttractionInformationVisible = true;
        attractionMapActivity.getRelativeLayoutAttractionInformation().setVisibility(View.VISIBLE);
        attractionMapActivity.getRelativeLayoutAttractionInformation().animate().translationY(0);
    }

    private void setRelativeLayoutAttractionInformationInvisible() {
        isRelativeLayoutAttractionInformationVisible = false;
        attractionMapActivity.getRelativeLayoutAttractionInformation().animate().translationY(attractionMapActivity.
                getRelativeLayoutAttractionInformation().getHeight() + 100);
    }

    private void setRelativeLayoutAttractionInformationHotelFields(Marker marker) {
        attraction = getAttractionFromMarkerClick(marker.getTitle());
        //setHotelImage(hotel);
        attractionMapActivity.getTextViewAttractionName().setText(attraction.getName());
        attractionMapActivity.getTextViewAttractionRating().setText(createReviewString(attraction.getAvarageRating()));
        attractionMapActivity.getTextViewAttractionAddress().setText(createAddressString(attraction.getAddress()));
    }

    private void setHotelImage(Attraction attraction) {
        if (hasImage(attraction))
            Picasso.with(attractionMapActivity.getApplicationContext())
                    .load(attraction.getImages().get(0))
                    .placeholder(R.drawable.troppadvisor_logo)
                    .into(attractionMapActivity.getImageViewAttraction());
        else
            attractionMapActivity.getImageViewAttraction().setImageDrawable(null);
    }

    private boolean hasImage(Attraction attraction) {
        return attraction.getImages().size() > 0;
    }

    private String createAddressString(Address address) {
        String attractionAddress = "";
        attractionAddress = attractionAddress.concat(address.getType() + " ");
        attractionAddress = attractionAddress.concat(address.getStreet() + ", ");
        attractionAddress = attractionAddress.concat(address.getHouseNumber() + ", ");
        attractionAddress = attractionAddress.concat(address.getCity() + ", ");
        attractionAddress = attractionAddress.concat(address.getProvince() + ", ");
        attractionAddress = attractionAddress.concat(address.getPostalCode());
        return attractionAddress;
    }

    private String createReviewString(Integer review) {
        if (review.equals(0))
            return attractionMapActivity.getResources().getString(R.string.no_review);
        else {
            String rating = "";
            rating = rating.concat(review + "/5");
            return rating;
        }
    }

    private Attraction getAttractionFromMarkerClick(String attractionId) {
        Attraction attractionToReturn = null;
        for (Attraction attraction : attractions)
            if (attraction.getId().equals(attractionId)) {
                attractionToReturn = attraction;
                break;
            }
        return attractionToReturn;
    }

    private void setLinearLayoutSearchAttractionsVisible() {
        isLinearLayoutSearchAttractionVisible = true;
        attractionMapActivity.getLinearLayoutSearchAttractions().animate().translationY(0);
    }

    private void setLinearLayoutSearchAttractionsInvisible() {
        isLinearLayoutSearchAttractionVisible = false;
        attractionMapActivity.getLinearLayoutSearchAttractions().animate().translationY(-attractionMapActivity.
                getLinearLayoutSearchAttractions().getHeight() - 100);
    }

    private void setFloatingActionButtonCenterPositionOnAttractionsVisible() {
        isFloatingActionButtonCenterPositionOnAttractionsVisible = true;
        attractionMapActivity.getFloatingActionButtonCenterPositionOnAttractions().show();
    }

    private void setFloatingActionButtonCenterPositionOnAttractionsInvisible() {
        isFloatingActionButtonCenterPositionOnAttractionsVisible = false;
        attractionMapActivity.getFloatingActionButtonCenterPositionOnAttractions().hide();
    }

    private void showBottomSheetMapFilters() {
        bottomSheetAttractionMap = new BottomSheetAttractionMap();
        bottomSheetAttractionMap.show(attractionMapActivity.getSupportFragmentManager(), bottomSheetAttractionMap.getTag());
        bottomSheetAttractionMap.setOnImageViewSearchMapFilterClick(this);
        bottomSheetAttractionMap.setAutoCompleteTextViewsAccomodationMapTextChangeListener(this);
    }

    private void showToastNoResults() {
        attractionMapActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(attractionMapActivity, "Nessun attrazione trovata in base ai criteri di ricerca", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setComponentProperties() {
        attractionMapActivity.getRelativeLayoutAttractionInformation().animate().translationY(attractionMapActivity.
                getRelativeLayoutAttractionInformation().getHeight() + 100);
    }

    public void setListenerOnViewComponents() {
        attractionMapActivity.getTextViewSearchAttractionsOnMap().setOnClickListener(this);
        attractionMapActivity.getImageViewAttractionMapGoBack().setOnClickListener(this);
        attractionMapActivity.getFloatingActionButtonCenterPositionOnAttractions().setOnClickListener(this);
    }

    private PointSearch getPointSearch() {
        return (PointSearch) attractionMapActivity.getIntent().getSerializableExtra(POINT_SEARCH);
    }

    private boolean isSearchingForName() {
        return !bottomSheetAttractionMap.getAutoCompleteTextViewMapAttractionNameValue().equals("");
    }

    private boolean isSearchingForCity() {
        return !bottomSheetAttractionMap.getAutoCompleteTextViewMapAttractionCityValue().equals("");
    }

    private boolean isRsqlValuesCorrectlySetted() {
        return !createRsqlString().equals("");
    }

}
