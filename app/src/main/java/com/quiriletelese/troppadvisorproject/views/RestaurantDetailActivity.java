package com.quiriletelese.troppadvisorproject.views;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.controllers.RestaurantDetailActivityController;

/**
 * @author Alessandro Quirile, Mauro Telese
 */

public class RestaurantDetailActivity extends AppCompatActivity {

    private RestaurantDetailActivityController restaurantDetailActivityController;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private ViewPager viewPager;
    private FloatingActionButton floatingActionButtonWriteReview;
    private TextView textViewAvarageRating, textViewCertificateOfExcellence, textViewAddress,
            textViewPhoneNumber, textViewOpeningTime, textViewAvaragePrice, textViewTypeOfCuisineList;
    private Button buttonReadReviews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail);

        initializeViewComponents();
        initializeController();
        setListenerOnViewComponents();
        showLoadingInProgressDialog();
        initializeActivityFields();

    }

    private void initializeViewComponents() {
        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar_layout_restaurant_detail_activity);
        viewPager = findViewById(R.id.view_pager_restaurant_detail);
        floatingActionButtonWriteReview = findViewById(R.id.floating_action_button_restaurant_write_review);
        textViewAvarageRating = findViewById(R.id.text_view_restaurant_avarage_rating);
        textViewCertificateOfExcellence = findViewById(R.id.text_view_restaurant_certificate_of_excellence);
        textViewAddress = findViewById(R.id.text_view_restaurant_address);
        textViewOpeningTime = findViewById(R.id.text_view_restaurant_opening_time);
        textViewPhoneNumber = findViewById(R.id.text_view_restaurant_phone_number);
        textViewAvaragePrice = findViewById(R.id.text_view_restaurant_avarage_price);
        textViewTypeOfCuisineList = findViewById(R.id.text_view_restaurant_type_of_cuisine_list);
        buttonReadReviews = findViewById(R.id.button_restaurant_read_reviews);
    }

    private void initializeController() {
        restaurantDetailActivityController = new RestaurantDetailActivityController(this);
    }

    private void setListenerOnViewComponents(){
        restaurantDetailActivityController.setListenerOnViewComponents();
    }

    public void showLoadingInProgressDialog() {
        restaurantDetailActivityController.showLoadingInProgressDialog();
    }

    private void initializeActivityFields() {
        restaurantDetailActivityController.findById();
    }

    public CollapsingToolbarLayout getCollapsingToolbarLayout() {
        return collapsingToolbarLayout;
    }

    public ViewPager getViewPager() {
        return viewPager;
    }

    public FloatingActionButton getFloatingActionButtonWriteReview() {
        return floatingActionButtonWriteReview;
    }

    public TextView getTextViewAvarageRating() {
        return textViewAvarageRating;
    }

    public TextView getTextViewCertificateOfExcellence() {
        return textViewCertificateOfExcellence;
    }

    public TextView getTextViewAddress() {
        return textViewAddress;
    }

    public TextView getTextViewPhoneNumber() {
        return textViewPhoneNumber;
    }

    public TextView getTextViewOpeningTime() {
        return textViewOpeningTime;
    }

    public TextView getTextViewAvaragePrice() {
        return textViewAvaragePrice;
    }

    public TextView getTextViewTypeOfCuisineList() {
        return textViewTypeOfCuisineList;
    }

    public Button getButtonReadReviews() {
        return buttonReadReviews;
    }

}