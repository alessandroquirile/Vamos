package com.quiriletelese.troppadvisorproject.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.controllers.AttractionDetailActivityController;
import com.quiriletelese.troppadvisorproject.controllers.RestaurantDetailActivityController;

public class AttractionDetailActivity extends AppCompatActivity {

    private AttractionDetailActivityController attractionDetailActivityController;
    private CollapsingToolbarLayout collapsingToolbarLayoutAttractionDetailActivity;
    private ViewPager viewPagerAttractionDetail;
    private FloatingActionButton floatingActionButtonAttractionWriteReview;
    private TextView textViewAttractionAvarageRating, textViewAttractionCertificateOfExcellence,
            textViewAttractionAddress, textViewAttractionPhoneNumber, textViewAttractionOpeningTime,
            textViewAttractionAvaragePrice;
    private Button buttonAttractionReadReviews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attraction_detail);

        initializeViewComponents();
        initializeController();
        setListenerOnViewComponents();
        initializeActivityFields();
    }

    private void initializeViewComponents() {
        collapsingToolbarLayoutAttractionDetailActivity = findViewById(R.id.collapsing_toolbar_layout_attraction_detail_activity);
        viewPagerAttractionDetail = findViewById(R.id.view_pager_attraction_detail);
        floatingActionButtonAttractionWriteReview = findViewById(R.id.floating_action_button_attraction_write_review);
        textViewAttractionAvarageRating = findViewById(R.id.text_view_attraction_avarage_rating);
        textViewAttractionCertificateOfExcellence = findViewById(R.id.text_view_attraction_certificate_of_excellence);
        textViewAttractionAddress = findViewById(R.id.text_view_attraction_address);
        textViewAttractionOpeningTime = findViewById(R.id.text_view_attraction_opening_time);
        textViewAttractionPhoneNumber = findViewById(R.id.text_view_attraction_phone_number);
        textViewAttractionAvaragePrice = findViewById(R.id.text_view_attraction_avarage_price);
        buttonAttractionReadReviews = findViewById(R.id.button_attraction_read_reviews);
    }

    private void initializeController() {
        attractionDetailActivityController = new AttractionDetailActivityController(this);
    }

    private void setListenerOnViewComponents(){
        attractionDetailActivityController.setListenerOnViewComponents();
    }

    private void initializeActivityFields() {
        attractionDetailActivityController.initializeActivityFields();
    }

    public CollapsingToolbarLayout getCollapsingToolbarLayoutAttractionDetailActivity() {
        return collapsingToolbarLayoutAttractionDetailActivity;
    }

    public ViewPager getViewPagerAttractionDetail() {
        return viewPagerAttractionDetail;
    }

    public FloatingActionButton getFloatingActionButtonAttractionWriteReview() {
        return floatingActionButtonAttractionWriteReview;
    }

    public TextView getTextViewAttractionAvarageRating() {
        return textViewAttractionAvarageRating;
    }

    public TextView getTextViewAttractionCertificateOfExcellence() {
        return textViewAttractionCertificateOfExcellence;
    }

    public TextView getTextViewAttractionAddress() {
        return textViewAttractionAddress;
    }

    public TextView getTextViewAttractionPhoneNumber() {
        return textViewAttractionPhoneNumber;
    }

    public TextView getTextViewAttractionOpeningTime() {
        return textViewAttractionOpeningTime;
    }

    public TextView getTextViewAttractionAvaragePrice() {
        return textViewAttractionAvaragePrice;
    }

    public Button getButtonAttractionReadReviews() {
        return buttonAttractionReadReviews;
    }
}