package com.quiriletelese.troppadvisorproject.views;

import android.content.Intent;
import android.media.Rating;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.controllers.AttractionDetailActivityController;
import com.quiriletelese.troppadvisorproject.utils.OnMapAndViewReadyListener;

/**
 * @author Alessandro Quirile, Mauro Telese
 */

public class AttractionDetailActivity extends AppCompatActivity implements
        OnMapAndViewReadyListener.OnGlobalLayoutAndMapReadyListener, OnMapReadyCallback {

    private AttractionDetailActivityController attractionDetailActivityController;
    private Toolbar toolbar;
    private ViewPager viewPager;
    private FloatingActionButton floatingActionButtonWriteReview;
    private RatingBar ratingBarActivityDetail;
    private TextView textViewAttractionName;
    private TextView textViewAvarageRating;
    private LinearLayoutCompat linearLayoutCompatReviewsPreview, linearLayoutCompatCertificateOfExcellence;
    private TextView textViewCertificateOfExcellence;
    private TextView textViewAddress;
    private TextView textViewPhoneNumber;
    private TextView textViewOpeningTime;
    private TextView textViewAvaragePrice;
    private TextView textViewAttractionWebsite;
    private TextView textViewImagePosition;
    private GoogleMap map;
    private SupportMapFragment mapFragment;
    private View mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attraction_detail);

        toolbar = findViewById(R.id.tool_bar_attraction_detail);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        setAppBarLayout();

        initializeViewComponents();
        initializeController();
        setListenerOnViewComponents();
        showLoadingInProgressDialog();
        initializeActivityFields();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        setMapProperties();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_attraction_detail_activity, menu);
        menu.findItem(R.id.review_attraction).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.review_attraction:
                attractionDetailActivityController.startWriteReviewActivity();
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        attractionDetailActivityController.handleOnActivityResult(requestCode, resultCode, data);
    }

    private void initializeViewComponents() {
        viewPager = findViewById(R.id.view_pager_attraction_detail);
        floatingActionButtonWriteReview = findViewById(R.id.floating_action_button_attraction_write_review);
        ratingBarActivityDetail = findViewById(R.id.rating_bar_activity_detail);
        textViewAttractionName = findViewById(R.id.text_view_attraction_name);
        textViewAvarageRating = findViewById(R.id.text_view_attraction_avarage_rating);
        linearLayoutCompatReviewsPreview = findViewById(R.id.linear_layout_compat_reviews_preview);
        linearLayoutCompatCertificateOfExcellence = findViewById(R.id.linear_layout_compat_certificate_of_excellence);
        textViewCertificateOfExcellence = findViewById(R.id.text_view_attraction_certificate_of_excellence);
        textViewAddress = findViewById(R.id.text_view_attraction_address);
        textViewOpeningTime = findViewById(R.id.text_view_attraction_opening_time);
        textViewPhoneNumber = findViewById(R.id.text_view_attraction_phone_number);
        textViewAvaragePrice = findViewById(R.id.text_view_attraction_avarage_price);
        textViewAttractionWebsite = findViewById(R.id.text_view_attraction_website);
        textViewImagePosition = findViewById(R.id.text_view_image_position);
        mapView = findViewById(R.id.map);
    }

    private void initializeController() {
        attractionDetailActivityController = new AttractionDetailActivityController(this);
    }

    private void setListenerOnViewComponents() {
        attractionDetailActivityController.setListenerOnViewComponents();
    }

    private void setAppBarLayout() {
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            int scrollRange = -1;
            boolean isCollapsed = false;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    toolbar.getMenu().findItem(R.id.review_attraction).setVisible(true);
                    isCollapsed = true;
                } else if (isCollapsed) {
                    toolbar.getMenu().findItem(R.id.review_attraction).setVisible(false);
                    isCollapsed = false;
                }
            }
        });
    }

    public void showLoadingInProgressDialog() {
        attractionDetailActivityController.showLoadingInProgressDialog();
    }

    private void setMapProperties() {
        attractionDetailActivityController.setMapProperties();
    }

    private void initializeActivityFields() {
        attractionDetailActivityController.findById();
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

    public TextView getTextViewAttractionName() {
        return textViewAttractionName;
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

    public TextView getTextViewAttractionWebsite() {
        return textViewAttractionWebsite;
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public TextView getTextViewImagePosition() {
        return textViewImagePosition;
    }

    public GoogleMap getMap() {
        return map;
    }

    public View getMapView() {
        return mapView;
    }
}