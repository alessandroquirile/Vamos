package com.quiriletelese.troppadvisorproject.views;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.controllers.AttractionDetailActivityController;

/**
 * @author Alessandro Quirile, Mauro Telese
 */

public class AttractionDetailActivity extends AppCompatActivity {

    private AttractionDetailActivityController attractionDetailActivityController;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private ViewPager viewPager;
    private FloatingActionButton floatingActionButtonWriteReview;
    private TextView textViewAvarageRating;
    private TextView textViewCertificateOfExcellence;
    private TextView textViewAddress;
    private TextView textViewPhoneNumber;
    private TextView textViewOpeningTime;
    private TextView textViewAvaragePrice;
    private Button buttonReadReviews;

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

    private void initializeViewComponents() {
        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar_layout_attraction_detail_activity);
        viewPager = findViewById(R.id.view_pager_attraction_detail);
        floatingActionButtonWriteReview = findViewById(R.id.floating_action_button_attraction_write_review);
        textViewAvarageRating = findViewById(R.id.text_view_attraction_avarage_rating);
        textViewCertificateOfExcellence = findViewById(R.id.text_view_attraction_certificate_of_excellence);
        textViewAddress = findViewById(R.id.text_view_attraction_address);
        textViewOpeningTime = findViewById(R.id.text_view_attraction_opening_time);
        textViewPhoneNumber = findViewById(R.id.text_view_attraction_phone_number);
        textViewAvaragePrice = findViewById(R.id.text_view_attraction_avarage_price);
        buttonReadReviews = findViewById(R.id.button_attraction_read_reviews);
    }

    private void initializeController() {
        attractionDetailActivityController = new AttractionDetailActivityController(this);
    }

    private void setListenerOnViewComponents(){
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

    public Button getButtonReadReviews() {
        return buttonReadReviews;
    }
}