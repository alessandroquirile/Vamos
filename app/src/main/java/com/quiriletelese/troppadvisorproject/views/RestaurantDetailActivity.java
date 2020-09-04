package com.quiriletelese.troppadvisorproject.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.controllers.HotelDetailActivityController;
import com.quiriletelese.troppadvisorproject.controllers.RestaurantDetailActivityController;

public class RestaurantDetailActivity extends AppCompatActivity {

    private RestaurantDetailActivityController restaurantDetailActivityController;
    private CollapsingToolbarLayout collapsingToolbarLayoutRestaurantDetailActivity;
    private ViewPager viewPagerOverview;
    private FloatingActionButton floatingActionButtonRestaurantWriteReview;
    private TextView textViewRestaurantAvarageRating, textViewRestaurantCertificateOfExcellence,
            textViewRestaurantAddress, textViewRestaurantPhoneNumber, textViewRestaurantOpeningTime,
            textViewRestaurantAvaragePrice, textViewTypeOfCuisineList;
    private Button buttonRestaurantRedReviews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail);

        initializeViewComponents();
        initializeController();
        setListenerOnViewComponents();
        initializaViewPager();
        initializeActivityFields();
    }

    private void initializeViewComponents() {
        collapsingToolbarLayoutRestaurantDetailActivity = findViewById(R.id.collapsing_toolbar_layout_restaurant_detail_activity);
        viewPagerOverview = findViewById(R.id.view_pager_overview);
        floatingActionButtonRestaurantWriteReview = findViewById(R.id.floating_action_button_hotel_write_review);
        textViewRestaurantAvarageRating = findViewById(R.id.text_view_restaurant_avarage_rating);
        textViewRestaurantCertificateOfExcellence = findViewById(R.id.text_view_restaurant_certificate_of_excellence);
        textViewRestaurantAddress = findViewById(R.id.text_view_restaurant_address);
        textViewRestaurantOpeningTime = findViewById(R.id.text_view_restaurant_opening_time);
        textViewRestaurantPhoneNumber = findViewById(R.id.text_view_restaurant_phone_number);
        textViewRestaurantAvaragePrice = findViewById(R.id.text_view_restaurant_avarage_price);
        textViewTypeOfCuisineList = findViewById(R.id.text_view_restaurant_type_of_cuisine_list);
        buttonRestaurantRedReviews = findViewById(R.id.button_restaurant_read_reviews);
    }

    private void initializeController() {
        restaurantDetailActivityController = new RestaurantDetailActivityController(this);
    }

    private void setListenerOnViewComponents(){
        restaurantDetailActivityController.setListenerOnViewComponents();
    }

    private void initializaViewPager() {
        restaurantDetailActivityController.initializaViewPager();
    }

    private void initializeActivityFields() {
        restaurantDetailActivityController.initializeActivityFields();
    }

    public CollapsingToolbarLayout getCollapsingToolbarLayoutRestaurantDetailActivity() {
        return collapsingToolbarLayoutRestaurantDetailActivity;
    }

    public ViewPager getViewPagerOverview() {
        return viewPagerOverview;
    }

    public FloatingActionButton getFloatingActionButtonRestaurantWriteReview() {
        return floatingActionButtonRestaurantWriteReview;
    }

    public TextView getTextViewRestaurantAvarageRating() {
        return textViewRestaurantAvarageRating;
    }

    public TextView getTextViewRestaurantCertificateOfExcellence() {
        return textViewRestaurantCertificateOfExcellence;
    }

    public TextView getTextViewRestaurantAddress() {
        return textViewRestaurantAddress;
    }

    public TextView getTextViewRestaurantPhoneNumber() {
        return textViewRestaurantPhoneNumber;
    }

    public TextView getTextViewRestaurantOpeningTime() {
        return textViewRestaurantOpeningTime;
    }

    public TextView getTextViewRestaurantAvaragePrice() {
        return textViewRestaurantAvaragePrice;
    }

    public TextView getTextViewTypeOfCuisineList() {
        return textViewTypeOfCuisineList;
    }

    public Button getButtonRestaurantRedReviews() {
        return buttonRestaurantRedReviews;
    }

}