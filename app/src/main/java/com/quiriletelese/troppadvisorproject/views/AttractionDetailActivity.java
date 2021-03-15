package com.quiriletelese.troppadvisorproject.views;

import android.media.Rating;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.controllers.AttractionDetailActivityController;
import com.quiriletelese.troppadvisorproject.utils.OnMapAndViewReadyListener;

/**
 * @author Alessandro Quirile, Mauro Telese
 */

public class AttractionDetailActivity extends AppCompatActivity implements
        OnMapAndViewReadyListener.OnGlobalLayoutAndMapReadyListener {

    private AttractionDetailActivityController attractionDetailActivityController;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private ViewPager viewPager;
    private FloatingActionButton floatingActionButtonWriteReview;
    private RatingBar ratingBarActivityDetail;
    private TextView textViewAvarageRating;
    private LinearLayoutCompat linearLayoutCompatReviewsPreview, linearLayoutCompatCertificateOfExcellence;
    private TextView textViewCertificateOfExcellence;
    private TextView textViewAddress;
    private TextView textViewPhoneNumber;
    private TextView textViewOpeningTime;
    private TextView textViewAvaragePrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attraction_detail);

        initializeViewComponents();
        initializeController();
        setListenerOnViewComponents();
        showLoadingInProgressDialog();
        initializeActivityFields();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initializeActivityFields();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        attractionDetailActivityController.handleOnMapReady(googleMap);
    }

    private void initializeViewComponents() {
        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar_layout_attraction_detail_activity);
        viewPager = findViewById(R.id.view_pager_attraction_detail);
        floatingActionButtonWriteReview = findViewById(R.id.floating_action_button_attraction_write_review);
        ratingBarActivityDetail = findViewById(R.id.rating_bar_activity_detail);
        textViewAvarageRating = findViewById(R.id.text_view_attraction_avarage_rating);
        linearLayoutCompatReviewsPreview = findViewById(R.id.linear_layout_compat_reviews_preview);
        linearLayoutCompatCertificateOfExcellence = findViewById(R.id.linear_layout_compat_certificate_of_excellence);
        textViewCertificateOfExcellence = findViewById(R.id.text_view_attraction_certificate_of_excellence);
        textViewAddress = findViewById(R.id.text_view_attraction_address);
        textViewOpeningTime = findViewById(R.id.text_view_attraction_opening_time);
        textViewPhoneNumber = findViewById(R.id.text_view_attraction_phone_number);
        textViewAvaragePrice = findViewById(R.id.text_view_attraction_avarage_price);
    }

    private void initializeController() {
        attractionDetailActivityController = new AttractionDetailActivityController(this);
    }

    private void setListenerOnViewComponents() {
        attractionDetailActivityController.setListenerOnViewComponents();
    }

    public void showLoadingInProgressDialog() {
        attractionDetailActivityController.showLoadingInProgressDialog();
    }

    private void initializeActivityFields() {
        attractionDetailActivityController.findById();
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

    public RatingBar getRatingBarActivityDetail() {
        return ratingBarActivityDetail;
    }

    public TextView getTextViewAvarageRating() {
        return textViewAvarageRating;
    }

    public LinearLayoutCompat getLinearLayoutCompatReviewsPreview() {
        return linearLayoutCompatReviewsPreview;
    }

    public LinearLayoutCompat getLinearLayoutCompatCertificateOfExcellence() {
        return linearLayoutCompatCertificateOfExcellence;
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

}