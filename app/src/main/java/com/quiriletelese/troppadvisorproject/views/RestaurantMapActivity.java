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
import com.quiriletelese.troppadvisorproject.controllers.RestaurantMapActivityController;
import com.quiriletelese.troppadvisorproject.interfaces.Constants;

public class RestaurantMapActivity extends FragmentActivity implements OnMapReadyCallback, Constants {

    private RestaurantMapActivityController restaurantMapActivityController;
    private GoogleMap googleMap;
    private LinearLayout linearLayoutSearchRestaurants;
    private RelativeLayout relativeLayoutDetails;
    private ImageView imageViewMapGoBack, imageViewRestaurant;
    private TextView textViewSearchOnMap, textViewName, textViewRating, textViewAddress;
    private FloatingActionButton floatingActionButtonCenterPositionOnRestaurants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.restaurant_map);
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
        linearLayoutSearchRestaurants = findViewById(R.id.linear_layout_search_restaurants);
        relativeLayoutDetails = findViewById(R.id.relative_layout_restaurant_details);
        imageViewMapGoBack = findViewById(R.id.image_view_restaurant_map_go_back);
        imageViewRestaurant = findViewById(R.id.image_view_restaurant);
        textViewSearchOnMap = findViewById(R.id.text_view_search_restaurants_on_map);
        textViewName = findViewById(R.id.text_view_restaurant_name);
        textViewRating = findViewById(R.id.text_view_restaurant_rating);
        textViewAddress = findViewById(R.id.text_view_restaurant_address);
        floatingActionButtonCenterPositionOnRestaurants = findViewById(R.id.floating_action_button_center_position_on_restaurants);
    }

    private void setGoogleMapStyle(){
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getApplicationContext(), R.raw.map_style));
    }

    private void initializeController() {
        restaurantMapActivityController = new RestaurantMapActivityController(this);
    }

    public void setListenerOnViewComponents() {
        restaurantMapActivityController.setListenerOnViewComponents();
    }

    private void setComponentProperties() {
        restaurantMapActivityController.setComponentProperties();
    }

    private void addMarkersOnMap() {
        restaurantMapActivityController.addMarkersOnMap();
    }

    private void setMapProperties() {
        restaurantMapActivityController.setMapProperties();
    }

    public GoogleMap getGoogleMap() {
        return googleMap;
    }

    public LinearLayout getLinearLayoutSearchRestaurants() {
        return linearLayoutSearchRestaurants;
    }

    public RelativeLayout getRelativeLayoutDetails() {
        return relativeLayoutDetails;
    }

    public ImageView getImageViewMapGoBack() {
        return imageViewMapGoBack;
    }

    public ImageView getImageViewRestaurant() {
        return imageViewRestaurant;
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

    public FloatingActionButton getFloatingActionButtonCenterPositionOnRestaurants() {
        return floatingActionButtonCenterPositionOnRestaurants;
    }
}