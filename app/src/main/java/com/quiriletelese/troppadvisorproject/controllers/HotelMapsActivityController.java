package com.quiriletelese.troppadvisorproject.controllers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewTreeObserver;

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
import com.quiriletelese.troppadvisorproject.dao_interfaces.HotelDAO;
import com.quiriletelese.troppadvisorproject.factories.DAOFactory;
import com.quiriletelese.troppadvisorproject.interfaces.Constants;
import com.quiriletelese.troppadvisorproject.model_helpers.Address;
import com.quiriletelese.troppadvisorproject.model_helpers.PointSearch;
import com.quiriletelese.troppadvisorproject.models.Hotel;
import com.quiriletelese.troppadvisorproject.utils.ConfigFileReader;
import com.quiriletelese.troppadvisorproject.views.HotelMapsActivity;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBack;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class HotelMapsActivityController implements GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener,
        View.OnClickListener, Constants {

    private HotelMapsActivity hotelMapsActivity;
    private DAOFactory daoFactory;
    private Hotel hotel = null;
    private List<Hotel> hotels = new ArrayList<>();
    private ArrayList<Marker> markers = new ArrayList<>();
    boolean isKeyboardVisible = false;
    boolean isRelativeLayoutHotelInformationVisible = false, isLinearLayoutSearchHotelsVisible = false,
            isFloatingActionButtonCenterPositionOnHotelsVisible = false;

    public HotelMapsActivityController(HotelMapsActivity hotelMapsActivity) {
        this.hotelMapsActivity = hotelMapsActivity;
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
            case R.id.floating_action_button_center_position_on_hotels:
                zoomOnMap();
                break;
        }
    }

    public void findHotelsByPointNear(VolleyCallBack volleyCallBack, PointSearch pointSearch) {
        daoFactory = DAOFactory.getInstance();
        HotelDAO hotelDAO = daoFactory.getHotelDAO(ConfigFileReader.getProperty(HOTEL_STORAGE_TECHNOLOGY,
                hotelMapsActivity.getApplicationContext()));
        hotelDAO.findAllByPointNear(volleyCallBack, pointSearch, hotelMapsActivity.getApplicationContext());
    }

    public void addMarkersOnMap(PointSearch pointSearch) {
        findHotelsByPointNear(new VolleyCallBack() {
            @Override
            public void onSuccess(final List<?> accomodation) {
                hotels = (List<Hotel>) accomodation;
                addMarkersOnSuccess((List<Hotel>) accomodation);
                zoomOnMap();
            }

            @Override
            public void onError(List<?> accomodation, String error) {
            }
        }, pointSearch);
    }

    private void addMarkersOnSuccess(List<Hotel> hotels) {
        for (Hotel hotel : hotels)
            markers.add(hotelMapsActivity.getGoogleMap().addMarker(createMarkerOptions(hotel)));
    }

    private MarkerOptions createMarkerOptions(Hotel hotel) {
        return new MarkerOptions()
                .position(new LatLng(hotel.getPoint().getX(), hotel.getPoint().getY()))
                .icon(setCustomMarker(hotelMapsActivity.getApplicationContext(), R.drawable.hotel_marker))
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
        hotelMapsActivity.getGoogleMap().animateCamera(cu);
    }

    @SuppressLint("MissingPermission")
    public void setMapProperties() {
        hotelMapsActivity.getGoogleMap().getUiSettings().setMyLocationButtonEnabled(false);
        hotelMapsActivity.getGoogleMap().getUiSettings().setMapToolbarEnabled(false);
        hotelMapsActivity.getGoogleMap().setMyLocationEnabled(true);
        hotelMapsActivity.getGoogleMap().setOnMarkerClickListener(this);
        hotelMapsActivity.getGoogleMap().setOnMapClickListener(this);
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
        if (!isRelativeLayoutHotelInformationVisible)
            setRelativeLayoutHotelInformationVisible();
        if (!isLinearLayoutSearchHotelsVisible)
            setLinearLayoutSearchHotelsVisible();
        if (!isFloatingActionButtonCenterPositionOnHotelsVisible)
            setFloatingActionButtonCenterPositionOnHotelsVisible();
        setRelativeLayoutHotelInformationHotelFields(marker);
    }

    public boolean isKeyboardVisible() {
        ViewTreeObserver viewTreeObserver = hotelMapsActivity.getRelativeLayoutMain().getViewTreeObserver();
        viewTreeObserver.addOnGlobalFocusChangeListener(new ViewTreeObserver.OnGlobalFocusChangeListener() {
            @Override
            public void onGlobalFocusChanged(View view, View view1) {
                Rect rect = new Rect();
                hotelMapsActivity.getRelativeLayoutMain().getWindowVisibleDisplayFrame(rect);
                int screenHeight = hotelMapsActivity.getRelativeLayoutMain().getRootView().getHeight();
                int keyboardHeight = screenHeight - rect.bottom;
                if (keyboardHeight > screenHeight * 0.15)
                    isKeyboardVisible = true;
                else
                    isKeyboardVisible = false;
            }
        });
        return isKeyboardVisible;
    }

    private void setRelativeLayoutHotelInformationVisible() {
        isRelativeLayoutHotelInformationVisible = true;
        hotelMapsActivity.getRelativeLayoutHotelInformation().setVisibility(View.VISIBLE);
        hotelMapsActivity.getRelativeLayoutHotelInformation().animate().translationY(0);
    }

    private void setRelativeLayoutHotelInformationInvisible() {
        isRelativeLayoutHotelInformationVisible = false;
        hotelMapsActivity.getRelativeLayoutHotelInformation().animate().translationY(hotelMapsActivity.
                getRelativeLayoutHotelInformation().getHeight() + 100);
    }

    private void setRelativeLayoutHotelInformationHotelFields(Marker marker) {
        hotel = getHotelFromMarkerClick(marker.getTitle());
        setHotelImage(hotel);
        hotelMapsActivity.getTextViewHotelName().setText(hotel.getName());
        hotelMapsActivity.getTextViewHotelRating().setText(createReviewString(hotel.getAvarageRating()));
        hotelMapsActivity.getTextViewHotelAddress().setText(createAddressString(hotel.getAddress()));
    }

    private void setHotelImage(Hotel hotel) {
        if (hasImage(hotel))
            Picasso.with(hotelMapsActivity.getApplicationContext())
                    .load(hotel.getImages().get(0))
                    .placeholder(R.drawable.troppadvisor_logo)
                    .into(hotelMapsActivity.getImageViewHotel());
        else
            hotelMapsActivity.getImageViewHotel().setImageDrawable(null);
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
            return hotelMapsActivity.getResources().getString(R.string.no_review);
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
        hotelMapsActivity.getLinearLayoutSearchHotels().setVisibility(View.VISIBLE);
        hotelMapsActivity.getLinearLayoutSearchHotels().animate().translationY(0);
    }

    private void setLinearLayoutSearchHotelsInvisible() {
        isLinearLayoutSearchHotelsVisible = false;
        hotelMapsActivity.getLinearLayoutSearchHotels().animate().translationY(-hotelMapsActivity.
                getLinearLayoutSearchHotels().getHeight() - 100);
    }

    private void setFloatingActionButtonCenterPositionOnHotelsVisible() {
        isFloatingActionButtonCenterPositionOnHotelsVisible = true;
        hotelMapsActivity.getLinearLayoutSearchHotels().setVisibility(View.VISIBLE);
        hotelMapsActivity.getFloatingActionButtonCenterPositionOnHotels().show();
    }

    private void setFloatingActionButtonCenterPositionOnHotelsInvisible() {
        isFloatingActionButtonCenterPositionOnHotelsVisible = false;
        hotelMapsActivity.getFloatingActionButtonCenterPositionOnHotels().hide();
    }

    public void setComponentProperties() {
        hotelMapsActivity.getRelativeLayoutHotelInformation().animate().translationY(hotelMapsActivity.
                getRelativeLayoutHotelInformation().getHeight() + 100);
        hotelMapsActivity.getLinearLayoutSearchHotels().animate().translationY(-hotelMapsActivity.
                getLinearLayoutSearchHotels().getHeight() - 100);
        hotelMapsActivity.getFloatingActionButtonCenterPositionOnHotels().hide();
    }

    public void setListenerOnViewComponents() {
        hotelMapsActivity.getFloatingActionButtonCenterPositionOnHotels().setOnClickListener(this);
    }

}
