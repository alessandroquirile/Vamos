package com.quiriletelese.troppadvisorproject.views;

import android.os.Bundle;
import android.widget.AutoCompleteTextView;
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
import com.quiriletelese.troppadvisorproject.controllers.HotelMapsActivityController;
import com.quiriletelese.troppadvisorproject.interfaces.Constants;
import com.quiriletelese.troppadvisorproject.model_helpers.PointSearch;
import com.quiriletelese.troppadvisorproject.models.Hotel;

import java.util.List;

public class HotelMapsActivity extends FragmentActivity implements OnMapReadyCallback, Constants {

    private HotelMapsActivityController hotelMapsActivityController;
    private GoogleMap googleMap;
    private LinearLayout linearLayoutSearchHotels;
    private RelativeLayout relativeLayoutHotelInformation, relativeLayoutMain;
    private ImageView imageViewHotelMapGoBack, imageViewHotelMapClearText, imageViewHotel;
    private TextView textViewHotelName, textViewHotelRating, textViewHotelAddress;
    private AutoCompleteTextView autoCompleteTextViewSearchHotelsOnMap;
    private FloatingActionButton floatingActionButtonCenterPositionOnHotels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
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
        relativeLayoutMain = findViewById(R.id.relative_layout_main);
        imageViewHotelMapGoBack = findViewById(R.id.image_view_hotel_map_go_back);
        imageViewHotelMapClearText = findViewById(R.id.image_view_hotel_map_clear_text);
        imageViewHotel = findViewById(R.id.image_view_hotel);
        textViewHotelName = findViewById(R.id.text_view_hotel_name);
        textViewHotelRating = findViewById(R.id.text_view_hotel_rating);
        textViewHotelAddress = findViewById(R.id.text_view_hotel_address);
        autoCompleteTextViewSearchHotelsOnMap = findViewById(R.id.auto_complete_text_view_search_hotels_on_map);
        floatingActionButtonCenterPositionOnHotels = findViewById(R.id.floating_action_button_center_position_on_hotels);
    }

    private void initializeController() {
        hotelMapsActivityController = new HotelMapsActivityController(this);
    }

    public void setListenerOnViewComponents() {
        hotelMapsActivityController.setListenerOnViewComponents();
    }

    private void setComponentProperties() {
        hotelMapsActivityController.setComponentProperties();
    }

    private void addMarkersOnMap() {
        PointSearch pointSearch = (PointSearch) getIntent().getSerializableExtra(POINT_SEARCH);
        hotelMapsActivityController.addMarkersOnMap(pointSearch);
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

    public RelativeLayout getRelativeLayoutMain() {
        return relativeLayoutMain;
    }

    public ImageView getImageViewHotelMapGoBack() {
        return imageViewHotelMapGoBack;
    }

    public ImageView getImageViewHotelMapClearText() {
        return imageViewHotelMapClearText;
    }

    public ImageView getImageViewHotel() {
        return imageViewHotel;
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

    public AutoCompleteTextView getAutoCompleteTextViewSearchHotelsOnMap() {
        return autoCompleteTextViewSearchHotelsOnMap;
    }

    public FloatingActionButton getFloatingActionButtonCenterPositionOnHotels() {
        return floatingActionButtonCenterPositionOnHotels;
    }
}
