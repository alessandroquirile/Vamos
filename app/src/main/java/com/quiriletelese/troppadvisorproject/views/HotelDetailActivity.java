package com.quiriletelese.troppadvisorproject.views;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.controllers.HotelDetailActivityController;

public class HotelDetailActivity extends AppCompatActivity {

    private HotelDetailActivityController hotelDetailActivityController;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private ViewPager viewPager;
    private FloatingActionButton floatingActionButtonWriteReview;
    private TextView textViewAvarageRating, textViewCertificateOfExcellence, textViewAddress,
            textViewPhoneNumber, textViewStars, textViewAvaragePrice;
    private Button buttonReadReviews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_detail);

        initializeViewComponents();
        initializeController();
        setListenerOnViewComponents();
        initializeActivityFields();

    }

    private void initializeViewComponents() {
        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar_layout_hotel_detail_activity);
        viewPager = findViewById(R.id.view_pager_hotel_detail);
        floatingActionButtonWriteReview = findViewById(R.id.floating_action_button_hotel_write_review);
        textViewAvarageRating = findViewById(R.id.text_view_hotel_avarage_rating);
        textViewCertificateOfExcellence = findViewById(R.id.text_view_hotel_certificate_of_excellence);
        textViewAddress = findViewById(R.id.text_view_accomodation_address);
        textViewPhoneNumber = findViewById(R.id.text_view_hotel_phone_number);
        textViewStars = findViewById(R.id.text_view_hotel_stars);
        textViewAvaragePrice = findViewById(R.id.text_view_hotel_avarage_price);
        buttonReadReviews = findViewById(R.id.button_hotel_read_reviews);
    }

    private void initializeController() {
        hotelDetailActivityController = new HotelDetailActivityController(this);
    }
    private void setListenerOnViewComponents(){
        hotelDetailActivityController.setListenerOnViewComponents();
    }

    private void initializeActivityFields() {
        hotelDetailActivityController.findById();
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

    public TextView getTextViewStars() {
        return textViewStars;
    }

    public TextView getTextViewAvaragePrice() {
        return textViewAvaragePrice;
    }

    public Button getButtonReadReviews() {
        return buttonReadReviews;
    }
}

