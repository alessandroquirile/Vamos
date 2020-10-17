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
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.controllers.HotelMapActivityController;
import com.quiriletelese.troppadvisorproject.util_interfaces.Constants;

/**
 * @author Alessandro Quirile, Mauro Telese
 */

public class HotelMapActivity extends FragmentActivity implements OnMapReadyCallback, Constants {

    private HotelMapActivityController hotelMapsActivityController;
    private GoogleMap googleMap;
    private LinearLayout linearLayoutSearchHotels;
    private RelativeLayout relativeLayoutDetails;
    private ImageView imageViewMapGoBack, imageViewHotel;
    private TextView textViewSearchOnMap, textViewName, textViewRating, textViewAddress;
    private FloatingActionButton floatingActionButtonCenterPositionOnHotels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.hotel_map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        intializeViewComponents(googleMap);
        setGoogleMapStyle();
        initializeController();
        setListenerOnViewComponents();
        setComponentProperties();
        setMapProperties();
        addMarkersOnMap();
    }

    private void intializeViewComponents(GoogleMap googleMap) {
        this.googleMap = googleMap;
        linearLayoutSearchHotels = findViewById(R.id.linear_layout_search_hotels);
        relativeLayoutDetails = findViewById(R.id.relative_layout_hotel_information);
        imageViewMapGoBack = findViewById(R.id.image_view_hotel_map_go_back);
        imageViewHotel = findViewById(R.id.image_view_hotel);
        textViewSearchOnMap = findViewById(R.id.text_view_search_hotels_on_map);
        textViewName = findViewById(R.id.text_view_hotel_map_name);
        textViewRating = findViewById(R.id.text_view_hotel_map_rating);
        textViewAddress = findViewById(R.id.text_view_hotel_map_address);
        floatingActionButtonCenterPositionOnHotels = findViewById(R.id.floating_action_button_center_position_on_hotels);
    }

    private void setGoogleMapStyle(){
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getApplicationContext(), R.raw.map_style));
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

    public RelativeLayout getRelativeLayoutDetails() {
        return relativeLayoutDetails;
    }

    public ImageView getImageViewMapGoBack() {
        return imageViewMapGoBack;
    }

    public ImageView getImageViewHotel() {
        return imageViewHotel;
    }

    public TextView getTextViewSearchOnMap() {
        return textViewSearchOnMap;
    }

    public TextView getTextViewName() {
        return textViewName;
    }

    public TextView getTextViewRating() {
        return textViewRating;
    }

    public TextView getTextViewAddress() {
        return textViewAddress;
    }

    public FloatingActionButton getFloatingActionButtonCenterPositionOnHotels() {
        return floatingActionButtonCenterPositionOnHotels;
    }
}
