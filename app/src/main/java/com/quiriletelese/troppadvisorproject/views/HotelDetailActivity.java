package com.quiriletelese.troppadvisorproject.views;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.adapters.ViewPagerOverViewActivityAdapter;
import com.quiriletelese.troppadvisorproject.controllers.HotelDetailActivityController;
import com.quiriletelese.troppadvisorproject.controllers.WriteReviewController;
import com.quiriletelese.troppadvisorproject.models.Hotel;

public class HotelDetailActivity extends AppCompatActivity {

    private HotelDetailActivityController hotelDetailActivityController;
    private CollapsingToolbarLayout collapsingToolbarLayoutHotelDetailActivity;
    private ViewPager viewPagerOverview;
    private FloatingActionButton floatingActionButtonWriteReview;
    private TextView textViewAvarageRating, textViewCertificateOfExcellence, textViewAddress,
            textViewPhoneNumber, textViewStars, textViewAvaragePrice;

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
        floatingActionButtonWriteReview = findViewById(R.id.floating_action_button_write_review);
        textViewAvarageRating = findViewById(R.id.text_view_avarage_rating);
        textViewCertificateOfExcellence = findViewById(R.id.text_view_certificate_of_excellence);
        textViewAddress = findViewById(R.id.text_view_address);
        textViewPhoneNumber = findViewById(R.id.text_view_phone_number);
        textViewStars = findViewById(R.id.text_view_stars);
        textViewAvaragePrice = findViewById(R.id.text_view_avarage_price);
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

}

