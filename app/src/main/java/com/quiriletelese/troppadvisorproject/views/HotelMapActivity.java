package com.quiriletelese.troppadvisorproject.views;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.controllers.HotelMapActivityController;
import com.quiriletelese.troppadvisorproject.interfaces.Constants;

public class HotelMapActivity extends FragmentActivity implements OnMapReadyCallback, Constants {

    private HotelMapActivityController hotelMapsActivityController;
    private GoogleMap googleMap;
    private LinearLayout linearLayoutSearchHotels;
    private RelativeLayout relativeLayoutHotelInformation;
    private ImageView imageViewHotelMapGoBack, imageViewHotel;
    private TextView textViewSearchHotelsOnMap, textViewHotelName, textViewHotelRating, textViewHotelAddress;
    private FloatingActionButton floatingActionButtonCenterPositionOnHotels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.hotel_map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        intializeViewComponents(googleMap);
        initializeController();
        setListenerOnViewComponents();
        setComponentProperties();
        setMapProperties();
        addMarkersOnMap();
    }

    private void intializeViewComponents(GoogleMap googleMap) {
        this.googleMap = googleMap;
        linearLayoutSearchHotels = findViewById(R.id.linear_layout_search_hotels);
        relativeLayoutHotelInformation = findViewById(R.id.relative_layout_hotel_information);
        imageViewHotelMapGoBack = findViewById(R.id.image_view_hotel_map_go_back);
        imageViewHotel = findViewById(R.id.image_view_hotel);
        textViewSearchHotelsOnMap = findViewById(R.id.text_view_search_hotels_on_map);
        textViewHotelName = findViewById(R.id.text_view_hotel_name);
        textViewHotelRating = findViewById(R.id.text_view_hotel_rating);
        textViewHotelAddress = findViewById(R.id.text_view_hotel_address);
        floatingActionButtonCenterPositionOnHotels = findViewById(R.id.floating_action_button_center_position_on_hotels);
    }

    private void initializeController() {
        hotelMapsActivityController = new HotelMapActivityController(this);
    }

    public void setListenerOnViewComponents() {
        hotelMapsActivityController.setListenerOnViewComponents();
    }

    private void setComponentProperties() {
        hotelMapsActivityController.setComponentProperties();
    }

    private void addMarkersOnMap() {
        hotelMapsActivityController.addMarkersOnMap();
    }

    private void setMapProperties() {
        hotelMapsActivityController.setMapProperties();
    }

    public GoogleMap getGoogleMap() {
        return googleMap;
    }

    public LinearLayout getLinearLayoutSearchHotels() {
        return linearLayoutSearchHotels;
    }

    public RelativeLayout getRelativeLayoutHotelInformation() {
        return relativeLayoutHotelInformation;
    }

    public ImageView getImageViewHotelMapGoBack() {
        return imageViewHotelMapGoBack;
    }

    public ImageView getImageViewHotel() {
        return imageViewHotel;
    }

    public TextView getTextViewSearchHotelsOnMap() {
        return textViewSearchHotelsOnMap;
    }

    public TextView getTextViewHotelName() {
        return textViewHotelName;
    }

    public TextView getTextViewHotelRating() {
        return textViewHotelRating;
    }

    public TextView getTextViewHotelAddress() {
        return textViewHotelAddress;
    }

    public FloatingActionButton getFloatingActionButtonCenterPositionOnHotels() {
        return floatingActionButtonCenterPositionOnHotels;
    }
}
