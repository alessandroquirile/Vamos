package com.quiriletelese.troppadvisorproject.views;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.controllers.AttractionMapActivityController;
import com.quiriletelese.troppadvisorproject.interfaces.Constants;

public class AttractionMapActivity extends FragmentActivity implements OnMapReadyCallback, Constants {

    private AttractionMapActivityController attractionMapActivityController;
    private GoogleMap googleMap;
    private LinearLayout linearLayoutSearchAttractions;
    private RelativeLayout relativeLayoutAttractionInformation;
    private ImageView imageViewAttractionMapGoBack, imageViewAttraction;
    private TextView textViewSearchAttractionsOnMap, textViewAttractionName, textViewAttractionRating,
            textViewAttractionAddress;
    private FloatingActionButton floatingActionButtonCenterPositionOnAttractions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attraction_map);
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
        linearLayoutSearchAttractions = findViewById(R.id.linear_layout_search_attractions);
        relativeLayoutAttractionInformation = findViewById(R.id.relative_layout_attraction_information);
        imageViewAttractionMapGoBack = findViewById(R.id.image_view_attraction_map_go_back);
        imageViewAttraction = findViewById(R.id.image_view_attraction);
        textViewSearchAttractionsOnMap = findViewById(R.id.text_view_search_attractions_on_map);
        textViewAttractionName = findViewById(R.id.text_view_attraction_name);
        textViewAttractionRating = findViewById(R.id.text_view_attraction_rating);
        textViewAttractionAddress = findViewById(R.id.text_view_attraction_address);
        floatingActionButtonCenterPositionOnAttractions = findViewById(R.id.floating_action_button_center_position_on_attractions);
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

    public RelativeLayout getRelativeLayoutAttractionInformation() {
        return relativeLayoutAttractionInformation;
    }

    public ImageView getImageViewAttractionMapGoBack() {
        return imageViewAttractionMapGoBack;
    }

    public ImageView getImageViewAttraction() {
        return imageViewAttraction;
    }

    public TextView getTextViewSearchAttractionsOnMap() {
        return textViewSearchAttractionsOnMap;
    }

    public TextView getTextViewAttractionName() {
        return textViewAttractionName;
    }

    public TextView getTextViewAttractionRating() {
        return textViewAttractionRating;
    }

    public TextView getTextViewAttractionAddress() {
        return textViewAttractionAddress;
    }

    public FloatingActionButton getFloatingActionButtonCenterPositionOnAttractions() {
        return floatingActionButtonCenterPositionOnAttractions;
    }
}