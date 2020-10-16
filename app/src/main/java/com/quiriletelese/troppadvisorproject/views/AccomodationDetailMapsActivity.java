package com.quiriletelese.troppadvisorproject.views;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.controllers.AccomodationDetailMapsActivityController;

import java.util.Objects;

/**
 * @author Alessandro Quirile, Mauro Telese
 */

public class AccomodationDetailMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private AccomodationDetailMapsActivityController accomodationDetailMapsActivityController;
    private GoogleMap googleMap;
    private RelativeLayout relativeLayoutDetails;
    private ImageView imageViewAccomodation;
    private FloatingActionButton floatingActionButtonGoBack;
    private TextView textViewName;
    private TextView textViewRating;
    private TextView textViewAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accomodation_detail_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.accomodation_detail_map);
        Objects.requireNonNull(mapFragment).getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        intializeViewComponents(googleMap);
        setGoogleMapStyle();
        initializeController();
        setRelativeLayoutDetailsFields();
        setListenerOnViewComponents();
        setMapProperties();
        addMarker();
    }

    private void intializeViewComponents(GoogleMap googleMap) {
        this.googleMap = googleMap;
        relativeLayoutDetails = findViewById(R.id.relative_layout_accomodation_detail);
        imageViewAccomodation = findViewById(R.id.image_view_accomodation);
        textViewName = findViewById(R.id.text_view_accomodation_map_name);
        textViewRating = findViewById(R.id.text_view_accomodation_map_rating);
        textViewAddress = findViewById(R.id.text_view_accomodation_map_address);
        floatingActionButtonGoBack = findViewById(R.id.floating_action_button_go_back_accomodation_map_detail_activity);
    }

    private void setGoogleMapStyle(){
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getApplicationContext(), R.raw.map_style));
    }

    private void initializeController() {
        accomodationDetailMapsActivityController = new AccomodationDetailMapsActivityController(this);
    }

    private void setRelativeLayoutDetailsFields(){
        accomodationDetailMapsActivityController.setRelativeLayoutDetailsFields();
    }

    public void setListenerOnViewComponents() {
        accomodationDetailMapsActivityController.setListenerOnViewComponents();
    }

    private void addMarker() {
        accomodationDetailMapsActivityController.addMarker();
    }

    private void setMapProperties() {
        accomodationDetailMapsActivityController.setMapProperties();
    }

    public GoogleMap getGoogleMap() {
        return googleMap;
    }

    public RelativeLayout getRelativeLayoutDetails() {
        return relativeLayoutDetails;
    }

    public ImageView getImageViewAccomodation() {
        return imageViewAccomodation;
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

    public FloatingActionButton getFloatingActionButtonGoBack() {
        return floatingActionButtonGoBack;
    }
}