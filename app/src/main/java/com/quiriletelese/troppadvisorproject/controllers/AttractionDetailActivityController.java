package com.quiriletelese.troppadvisorproject.controllers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.adapters.ViewPagerOverViewActivityAdapter;
import com.quiriletelese.troppadvisorproject.dao_interfaces.AttractionDAO;
import com.quiriletelese.troppadvisorproject.factories.DAOFactory;
import com.quiriletelese.troppadvisorproject.interfaces.Constants;
import com.quiriletelese.troppadvisorproject.model_helpers.Address;
import com.quiriletelese.troppadvisorproject.models.Attraction;
import com.quiriletelese.troppadvisorproject.models.Review;
import com.quiriletelese.troppadvisorproject.utils.ConfigFileReader;
import com.quiriletelese.troppadvisorproject.views.AttractionDetailActivity;
import com.quiriletelese.troppadvisorproject.views.SeeReviewsActivity;
import com.quiriletelese.troppadvisorproject.views.WriteReviewActivity;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBack;

import java.util.List;
/**
 * @author Alessandro Quirile, Mauro Telese
 */

public class AttractionDetailActivityController implements View.OnClickListener, Constants {

    private AttractionDetailActivity attractionDetailActivity;
    private DAOFactory daoFactory = DAOFactory.getInstance();
    private Attraction attraction;
    private AlertDialog alertDialogLoadingInProgress;

    public AttractionDetailActivityController(AttractionDetailActivity attractionDetailActivity) {
        this.attractionDetailActivity = attractionDetailActivity;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.floating_action_button_attraction_write_review:
                startWriteReviewActivity();
                break;
            case R.id.button_attraction_read_reviews:
                seeReviews();
                break;
        }
    }

    private void findHotelByIdHelper(VolleyCallBack volleyCallBack, String id) {
        getAttractionDAO().findById(volleyCallBack, id, getContext());
    }

    public void findById() {
        findHotelByIdHelper(new VolleyCallBack() {
            @Override
            public void onSuccess(Object object) {
                attraction = (Attraction) object;
                initializeActivityFields();
                initializeViewPager();
                dismissLoadingInProgressDialog();
            }

            @Override
            public void onError(String errorCode) {
                detectVolleyError(errorCode);
            }
        }, getId());
    }

    public void setListenerOnViewComponents() {
        getFloatingActionButtonWriteReview().setOnClickListener(this);
        getButtonReadReviews().setOnClickListener(this);
    }

    public void initializeActivityFields() {
        setCollapsingToolbarLayoutTitle(getName());
        setAvaragePrice(getAvaragePrice());
        setAvarageRating(attraction);
        setCertificateOfExcellence(isHasCertificateOfExcellence());
        setAddress(getAddress());
        setOpeningTime(getOpeningTime());
        setPhoneNunmber(getPhoneNumber());
    }

    private void initializeViewPager() {
        getViewPager().setAdapter(createViewPagerOverViewActivityAdapter());
    }

    private ViewPagerOverViewActivityAdapter createViewPagerOverViewActivityAdapter(){
        return new ViewPagerOverViewActivityAdapter(getImages(), getContext());
    }

    private void detectVolleyError(String errorCode) {
        switch (errorCode) {
            case NO_CONTENT:
                showToastOnUiThread(R.string.no_content_error_attraction_detail);
                break;
        }
    }

    private void setCollapsingToolbarLayoutTitle(String title) {
        getCollapsingToolbarLayout().setTitle(title);
    }

    private void setAvarageRating(Attraction attraction) {
        if (!hasAvarageRating(attraction.getAvarageRating()))
            getTextViewAvarageRating().setText(R.string.no_reviews);
        else
            getTextViewAvarageRating().setText(createAvarageRatingString(attraction));
    }

    private String createAvarageRatingString(Attraction attraction) {
        String avarageRating = "";
        avarageRating = avarageRating.concat(attraction.getAvarageRating() + "/5 (");
        avarageRating = avarageRating.concat(attraction.getTotalReviews() + " ");
        avarageRating = avarageRating.concat(getString(R.string.reviews) + ")");
        return avarageRating;
    }

    private void setCertificateOfExcellence(boolean certificateOfExcellence) {
        if (!certificateOfExcellence)
            getTextViewCertificateOfExcellence().setVisibility(View.GONE);
    }

    private void setAddress(Address address) {
        getTextViewAddress().setText(createAddressString(address));
    }

    private String createAddressString(Address address) {
        String restaurantAddress = "";
        restaurantAddress = restaurantAddress.concat(address.getType() + " ");
        restaurantAddress = restaurantAddress.concat(address.getStreet() + ", ");
        restaurantAddress = restaurantAddress.concat(address.getHouseNumber() + ", ");
        restaurantAddress = restaurantAddress.concat(address.getCity() + ", ");
        restaurantAddress = restaurantAddress.concat(address.getProvince() + ", ");
        restaurantAddress = restaurantAddress.concat(address.getPostalCode());
        return restaurantAddress;
    }

    private void setOpeningTime(String openingTime) {
        if (!openingTime.equals(""))
            getTextViewOpeningTime().setText(openingTime);
        else
            getTextViewOpeningTime().setText(getString(R.string.no_information_available));
    }

    private void setPhoneNunmber(String phoneNumber) {
        if (!phoneNumber.equals(""))
            getTextViewPhoneNumber().setText(phoneNumber);
        else
            getTextViewPhoneNumber().setText(getString(R.string.no_phone_number));
    }

    private void setAvaragePrice(Integer price) {
        if (!price.equals(0))
            getTextViewAvaragePrice().setText(createAvaragePriceString(price));
        else
            getTextViewAvaragePrice().setText(getString(R.string.gratis));
    }

    private String createAvaragePriceString(Integer price) {
        String avaragePrice = "";
        avaragePrice = avaragePrice.concat(getString(R.string.avarage_price) + " ");
        avaragePrice = avaragePrice.concat(price + " ");
        avaragePrice = avaragePrice.concat(getString(R.string.currency));
        return avaragePrice;
    }

    private void startWriteReviewActivity() {
        attractionDetailActivity.startActivity(createWriteReviewActivityIntent());
    }

    private void startSeeReviewsActivity() {
        attractionDetailActivity.startActivity(createSeeReviewsActivityIntent());
    }

    private Intent createWriteReviewActivityIntent() {
        Intent writeReviewActivityIntent = new Intent(getContext(), WriteReviewActivity.class);
        writeReviewActivityIntent.putExtra(ID, getId());
        writeReviewActivityIntent.putExtra(ACCOMODATION_TYPE, ATTRACTION);
        return writeReviewActivityIntent;
    }

    private Intent createSeeReviewsActivityIntent() {
        Intent seeReviewsActivityIntent = new Intent(getContext(), SeeReviewsActivity.class);
        seeReviewsActivityIntent.putExtra(ACCOMODATION_NAME, getName());
        seeReviewsActivityIntent.putExtra(ID, getId());
        return seeReviewsActivityIntent;
    }

    private void seeReviews() {
        if (hasReviews())
            startSeeReviewsActivity();
        else
            showToastOnUiThread(R.string.no_reviews);
    }

    public void showLoadingInProgressDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(attractionDetailActivity);
        LayoutInflater layoutInflater = attractionDetailActivity.getLayoutInflater();
        View dialogView = layoutInflater.inflate(R.layout.dialog_loading_in_progress, null);
        alertDialogBuilder.setView(dialogView);
        alertDialogBuilder.setCancelable(false);
        alertDialogLoadingInProgress = alertDialogBuilder.create();
        alertDialogLoadingInProgress.show();
    }

    private void dismissLoadingInProgressDialog(){
        alertDialogLoadingInProgress.dismiss();
    }

    private void showToastOnUiThread(int string) {
        attractionDetailActivity.runOnUiThread(() -> {
            Toast.makeText(attractionDetailActivity, getString(string), Toast.LENGTH_SHORT).show();
        });
    }

    private CollapsingToolbarLayout getCollapsingToolbarLayout(){
        return attractionDetailActivity.getCollapsingToolbarLayout();
    }

    private TextView getTextViewOpeningTime(){
        return attractionDetailActivity.getTextViewOpeningTime();
    }

    private TextView getTextViewPhoneNumber(){
        return attractionDetailActivity.getTextViewPhoneNumber();
    }

    private TextView getTextViewAddress(){
        return attractionDetailActivity.getTextViewAddress();
    }

    private TextView getTextViewCertificateOfExcellence(){
        return attractionDetailActivity.getTextViewCertificateOfExcellence();
    }

    private TextView getTextViewAvarageRating(){
        return attractionDetailActivity.getTextViewAvarageRating();
    }

    private TextView getTextViewAvaragePrice(){
        return attractionDetailActivity.getTextViewAvaragePrice();
    }

    private FloatingActionButton getFloatingActionButtonWriteReview(){
        return attractionDetailActivity.getFloatingActionButtonWriteReview();
    }

    private Button getButtonReadReviews(){
        return attractionDetailActivity.getButtonReadReviews();
    }

    private boolean hasAvarageRating(Integer avarageRating) {
        return !avarageRating.equals(0);
    }

    private boolean hasReviews() {
        return getReviews().size() > 0;
    }

    private ViewPager getViewPager(){
        return attractionDetailActivity.getViewPager();
    }

    private List<String> getImages(){
        return attraction.getImages();
    }

    private List<Review> getReviews(){
        return attraction.getReviews();
    }

    private Intent getIntent(){
        return attractionDetailActivity.getIntent();
    }

    private String getId() {
        return getIntent().getStringExtra(ID);
    }

    private String getName(){
        return attraction.getName();
    }

    private Integer getAvaragePrice(){
        return attraction.getAvaragePrice();
    }

    private boolean isHasCertificateOfExcellence(){
        return attraction.isHasCertificateOfExcellence();
    }

    private Address getAddress(){
        return attraction.getAddress();
    }

    private String getOpeningTime() {
        return attraction.getOpeningTime();
    }

    private String getPhoneNumber(){
        return attraction.getPhoneNumber();
    }

    private Context getContext() {
        return attractionDetailActivity.getApplicationContext();
    }

    private Resources getResources(){
        return attractionDetailActivity.getResources();
    }

    private String getString(int id){
        return getResources().getString(id);
    }

    private AttractionDAO getAttractionDAO() {
        return daoFactory.getAttractionDAO(getStorageTechnology(ATTRACTION_STORAGE_TECHNOLOGY));
    }

    private String getStorageTechnology(String storageTechnology) {
        return ConfigFileReader.getProperty(storageTechnology, getContext());
    }

}
