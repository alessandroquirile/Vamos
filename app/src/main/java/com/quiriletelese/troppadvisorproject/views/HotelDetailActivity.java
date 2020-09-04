package com.quiriletelese.troppadvisorproject.views;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.controllers.HotelDetailActivityController;

public class HotelDetailActivity extends AppCompatActivity {

    private HotelDetailActivityController hotelDetailActivityController;
    private CollapsingToolbarLayout collapsingToolbarLayoutHotelDetailActivity;
    private ViewPager viewPagerOverview;
    private FloatingActionButton floatingActionButtonHotelWriteReview;
    private TextView textViewHotelAvarageRating, textViewHotelCertificateOfExcellence, textViewHotelAddress,
            textViewHotelPhoneNumber, textViewHotelStars, textViewHotelAvaragePrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_detail);

        initializeViewComponents();
        initializeController();
        initializaViewPager();
        initializeActivityFields();
    }

    private void initializeViewComponents() {
        collapsingToolbarLayoutHotelDetailActivity = findViewById(R.id.collapsing_toolbar_layout_hotel_detail_activity);
        viewPagerOverview = findViewById(R.id.view_pager_overview);
        floatingActionButtonHotelWriteReview = findViewById(R.id.floating_action_button_hotel_write_review);
        textViewHotelAvarageRating = findViewById(R.id.text_view_hotel_avarage_rating);
        textViewHotelCertificateOfExcellence = findViewById(R.id.text_view_hotel_certificate_of_excellence);
        textViewHotelAddress = findViewById(R.id.text_view_hotel_address);
        textViewHotelPhoneNumber = findViewById(R.id.text_view_hotel_phone_number);
        textViewHotelStars = findViewById(R.id.text_view_hotel_stars);
        textViewHotelAvaragePrice = findViewById(R.id.text_view_hotel_avarage_price);
    }

    private void initializeController() {
        hotelDetailActivityController = new HotelDetailActivityController(this);
        /*WriteReviewController writeReviewController = new WriteReviewController(this);
        writeReviewController.setListenersOnOverviewActiviyComponents();*/
    }

    private void initializaViewPager() {
        hotelDetailActivityController.initializaViewPager();
        /*Hotel hotel = (Hotel) getIntent().getSerializableExtra("hotel");
        ViewPagerOverViewActivityAdapter viewPagerOverViewActivityAdapter = new ViewPagerOverViewActivityAdapter(hotel.getImages(), this);
        viewPagerOverview.setAdapter(viewPagerOverViewActivityAdapter);*/
    }

    private void initializeActivityFields() {
        hotelDetailActivityController.initializeActivityFields();
    }

    public CollapsingToolbarLayout getCollapsingToolbarLayoutHotelDetailActivity() {
        return collapsingToolbarLayoutHotelDetailActivity;
    }

    public ViewPager getViewPagerOverview() {
        return viewPagerOverview;
    }

    public FloatingActionButton getFloatingActionButtonHotelWriteReview() {
        return floatingActionButtonHotelWriteReview;
    }

    public TextView getTextViewHotelAvarageRating() {
        return textViewHotelAvarageRating;
    }

    public TextView getTextViewHotelCertificateOfExcellence() {
        return textViewHotelCertificateOfExcellence;
    }

    public TextView getTextViewHotelAddress() {
        return textViewHotelAddress;
    }

    public TextView getTextViewHotelPhoneNumber() {
        return textViewHotelPhoneNumber;
    }

    public TextView getTextViewHotelStars() {
        return textViewHotelStars;
    }

    public TextView getTextViewHotelAvaragePrice() {
        return textViewHotelAvaragePrice;
    }

}

