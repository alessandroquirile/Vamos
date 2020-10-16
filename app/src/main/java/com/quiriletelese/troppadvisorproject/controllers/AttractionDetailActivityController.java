package com.quiriletelese.troppadvisorproject.controllers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
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
import com.quiriletelese.troppadvisorproject.models.Attraction;
import com.quiriletelese.troppadvisorproject.utils.ConfigFileReader;
import com.quiriletelese.troppadvisorproject.views.AttractionDetailActivity;
import com.quiriletelese.troppadvisorproject.views.SeeReviewsActivity;
import com.quiriletelese.troppadvisorproject.views.WriteReviewActivity;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBack;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

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
        onClickHelper(view);
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

    private void onClickHelper(View view){
        switch (view.getId()) {
            case R.id.text_view_attraction_phone_number:
                startCallActivity();
                break;
            case R.id.text_view_attraction_address:
                startMapsActivity();
                break;
            case R.id.floating_action_button_attraction_write_review:
                startWriteReviewActivity();
                break;
            case R.id.button_attraction_read_reviews:
                seeReviews();
                break;
        }
    }

    public void setListenerOnViewComponents() {
        getTextViewPhoneNumber().setOnClickListener(this);
        getTextViewAddress().setOnClickListener(this);
        getFloatingActionButtonWriteReview().setOnClickListener(this);
        getButtonReadReviews().setOnClickListener(this);
    }

    public void initializeActivityFields() {
        setCollapsingToolbarLayoutTitle(getName());
        setAvaragePrice(getAvaragePrice());
        setAvarageRating();
        setCertificateOfExcellence(isHasCertificateOfExcellence());
        setAddress();
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

    private void setAvarageRating() {
        if (!hasAvarageRating())
            getTextViewAvarageRating().setText(R.string.no_reviews);
        else
            getTextViewAvarageRating().setText(createAvarageRatingString());
    }

    private String createAvarageRatingString() {
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

    private void setAddress() {
        getTextViewAddress().setText(createAddressString());
    }

    private String createAddressString() {
        String restaurantAddress = "";
        restaurantAddress = restaurantAddress.concat(getTypeOfAddress() + " ");
        restaurantAddress = restaurantAddress.concat(getStreet() + ", ");
        restaurantAddress = restaurantAddress.concat(getHouseNumber() + ", ");
        restaurantAddress = restaurantAddress.concat(getCity() + ", ");
        restaurantAddress = restaurantAddress.concat(getProvince() + ", ");
        restaurantAddress = restaurantAddress.concat(getPostalCode());
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

    private void setAvaragePrice(Double price) {
        if (!price.equals(0d))
            getTextViewAvaragePrice().setText(createAvaragePriceString(price));
        else
            getTextViewAvaragePrice().setText(getString(R.string.gratis));
    }

    private String createAvaragePriceString(Double price) {
        String avaragePrice = "";
        avaragePrice = avaragePrice.concat(getString(R.string.avarage_price) + " ");
        avaragePrice = avaragePrice.concat(price + " ");
        avaragePrice = avaragePrice.concat(getString(R.string.currency));
        return avaragePrice;
    }

    private void startCallActivity(){
        getContext().startActivity(createCallActivityIntent());
    }

    private void startMapsActivity(){
        getContext().startActivity(createMapsActivityIntent());
    }

    private void startWriteReviewActivity() {
        getContext().startActivity(createWriteReviewActivityIntent());
    }

    private void startSeeReviewsActivity() {
        getContext().startActivity(createSeeReviewsActivityIntent());
    }

    @NotNull
    private Intent createCallActivityIntent() {
        Intent callActivityIntent = new Intent(Intent.ACTION_DIAL);
        callActivityIntent.setData(Uri.parse("tel:" + getTextViewPhoneNumber().getText().toString()));
        callActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return callActivityIntent;
    }

    @NotNull
    private Intent createMapsActivityIntent() {
        String uri = String.format(Locale.ENGLISH, "geo:0,0?q=" + createAddressString());
        Intent mapsActivityIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        mapsActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return mapsActivityIntent;
    }

    private Intent createWriteReviewActivityIntent() {
        Intent writeReviewActivityIntent = new Intent(getContext(), WriteReviewActivity.class);
        writeReviewActivityIntent.putExtra(ID, getId());
        writeReviewActivityIntent.putExtra(ACCOMODATION_TYPE, ATTRACTION);
        writeReviewActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return writeReviewActivityIntent;
    }

    private Intent createSeeReviewsActivityIntent() {
        Intent seeReviewsActivityIntent = new Intent(getContext(), SeeReviewsActivity.class);
        seeReviewsActivityIntent.putExtra(ACCOMODATION_NAME, getName());
        seeReviewsActivityIntent.putExtra(ID, getId());
        seeReviewsActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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

    private boolean hasAvarageRating() {
        return attraction.hasAvarageRating();
    }

    private boolean hasReviews() {
        return attraction.hasReviews();
    }

    private ViewPager getViewPager(){
        return attractionDetailActivity.getViewPager();
    }

    private List<String> getImages(){
        return attraction.getImages();
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

    private Double getAvaragePrice(){
        return attraction.getAvaragePrice();
    }

    private boolean isHasCertificateOfExcellence(){
        return attraction.isHasCertificateOfExcellence();
    }

    private String getTypeOfAddress(){
        return attraction.getTypeOfAddress();
    }

    private String getStreet(){
        return attraction.getStreet();
    }

    private String getHouseNumber(){
        return attraction.getHouseNumber();
    }

    private String getCity(){
        return attraction.getCity();
    }

    private String getProvince(){
        return attraction.getProvince();
    }

    private String getPostalCode(){
        return attraction.getPostalCode();
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

    @NotNull
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
