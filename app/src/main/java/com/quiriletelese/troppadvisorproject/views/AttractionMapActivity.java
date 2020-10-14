package com.quiriletelese.troppadvisorproject.views;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.controllers.AttractionMapActivityController;
import com.quiriletelese.troppadvisorproject.interfaces.Constants;

/**
 * @author Alessandro Quirile, Mauro Telese
 */

public class AttractionMapActivity extends FragmentActivity implements OnMapReadyCallback, Constants {

    private AttractionMapActivityController attractionMapActivityController;
    private GoogleMap googleMap;
    private LinearLayout linearLayoutSearchAttractions;
    private RelativeLayout relativeLayoutDetails;
    private ImageView imageViewMapGoBack, imageViewAttraction;
    private TextView textViewSearchOnMap, textViewName, textViewRating, textViewAddress;
    private FloatingActionButton floatingActionButtonCenterPositionOnAttractions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attractions_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.attractions_map);
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
        linearLayoutSearchAttractions = findViewById(R.id.linear_layout_search_attractions);
        relativeLayoutDetails = findViewById(R.id.relative_layout_attraction_information);
        imageViewMapGoBack = findViewById(R.id.image_view_attraction_map_go_back);
        imageViewAttraction = findViewById(R.id.image_view_attraction);
        textViewSearchOnMap = findViewById(R.id.text_view_search_attractions_on_map);
        textViewName = findViewById(R.id.text_view_attraction_name);
        textViewRating = findViewById(R.id.text_view_attraction_rating);
        textViewAddress = findViewById(R.id.text_view_attraction_address);
        floatingActionButtonCenterPositionOnAttractions = findViewById(R.id.floating_action_button_center_position_on_attractions);
    }

    private void setGoogleMapStyle(){
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getApplicationContext(), R.raw.map_style));
    }

    private void initializeController() {
        attractionMapActivityController = new AttractionMapActivityController(this);
    }

    public void setListenerOnViewComponents() {
        attractionMapActivityController.setListenerOnViewComponents();
    }

    private void setComponentProperties() {
        attractionMapActivityController.setComponentProperties();
    }

    private void addMarkersOnMap() {
        attractionMapActivityController.addMarkersOnMap();
    }

    private void setMapProperties() {
        attractionMapActivityController.setMapProperties();
    }

    public GoogleMap getGoogleMap() {
        return googleMap;
    }

    public LinearLayout getLinearLayoutSearchAttractions() {
        return linearLayoutSearchAttractions;
    }

    public RelativeLayout getRelativeLayoutDetails() {
        return relativeLayoutDetails;
    }

    public ImageView getImageViewMapGoBack() {
        return imageViewMapGoBack;
    }

    public ImageView getImageViewAttraction() {
        return imageViewAttraction;
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

    public FloatingActionButton getFloatingActionButtonCenterPositionOnAttractions() {
        return floatingActionButtonCenterPositionOnAttractions;
    }
}