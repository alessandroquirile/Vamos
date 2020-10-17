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
import com.quiriletelese.troppadvisorproject.dao_interfaces.HotelDAO;
import com.quiriletelese.troppadvisorproject.factories.DAOFactory;
import com.quiriletelese.troppadvisorproject.interfaces.Constants;
import com.quiriletelese.troppadvisorproject.models.Hotel;
import com.quiriletelese.troppadvisorproject.models.Review;
import com.quiriletelese.troppadvisorproject.utils.ConfigFileReader;
import com.quiriletelese.troppadvisorproject.views.HotelDetailActivity;
import com.quiriletelese.troppadvisorproject.views.SeeReviewsActivity;
import com.quiriletelese.troppadvisorproject.views.WriteReviewActivity;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBack;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

/**
 * @author Alessandro Quirile, Mauro Telese
 */

public class HotelDetailActivityController implements View.OnClickListener, Constants {

    private HotelDetailActivity hotelDetailActivity;
    private DAOFactory daoFactory = DAOFactory.getInstance();
    private Hotel hotel;
    private AlertDialog alertDialogLoadingInProgress;

    public HotelDetailActivityController(HotelDetailActivity hotelDetailActivity) {
        this.hotelDetailActivity = hotelDetailActivity;
    }

    @Override
    public void onClick(@NotNull View view) {
        onClickHelper(view);
    }

    private void findHotelByIdHelper(VolleyCallBack volleyCallBack, String id) {
        getHotelDAO().findById(volleyCallBack, id, getContext());
    }

    public void findById() {
        findHotelByIdHelper(new VolleyCallBack() {
            @Override
            public void onSuccess(Object object) {
                hotel = (Hotel) object;
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
            case R.id.text_view_hotel_phone_number:
                startCallActivity();
                break;
            case R.id.text_view_hotel_address:
                startMapsActivity();
                break;
            case R.id.floating_action_button_hotel_write_review:
                startWriteReviewActivity();
                break;
            case R.id.button_hotel_read_reviews:
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

    private void initializeActivityFields() {
        setCollapsingToolbarLayoutTitle(getName());
        setAvaragePrice(getAvaragePrice());
        setAvarageRating();
        setCertificateOfExcellence(isHasCertificateOfExcellence());
        setAddress();
        setPhoneNunmber(getPhoneNumber());
        setHotelStars(getStars());
    }

    private void initializeViewPager() {
        getViewPager().setAdapter(createViewPagerOverViewActivityAdapter());
    }

    @NotNull
    @Contract(" -> new")
    private ViewPagerOverViewActivityAdapter createViewPagerOverViewActivityAdapter() {
        return new ViewPagerOverViewActivityAdapter(getImages(), getContext());
    }

    private void detectVolleyError(@NotNull String errorCode) {
        switch (errorCode) {
            case NO_CONTENT:
                showToastOnUiThread(R.string.no_content_error_hotel_detail);
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
            getTextViewAvarageRating().setText(createAvarageRatingString(hotel));
    }

    @NotNull
    private String createAvarageRatingString(@NotNull Hotel hotel) {
        String avarageRating = "";
        avarageRating = avarageRating.concat(hotel.getAvarageRating().intValue() + "/5 (");
        avarageRating = avarageRating.concat(hotel.getTotalReviews() + " ");
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

    @NotNull
    private String createAddressString() {
        String hotelAddress = "";
        hotelAddress = hotelAddress.concat(getTypeOfAddress() + " ");
        hotelAddress = hotelAddress.concat(getStreet() + ", ");
        hotelAddress = hotelAddress.concat(getHouseNumber() + ", ");
        hotelAddress = hotelAddress.concat(getCity() + ", ");
        hotelAddress = hotelAddress.concat(getProvince() + ", ");
        hotelAddress = hotelAddress.concat(getPostalCode());
        return hotelAddress;
    }

    private void setPhoneNunmber(@NotNull String phoneNumber) {
        if (!phoneNumber.equals(""))
            getTextViewPhoneNumber().setText(phoneNumber);
        else
            getTextViewPhoneNumber().setText(getString(R.string.no_phone_number));
    }

    private void setHotelStars(Integer stars) {
        getTextViewStars().setText(createHotelStarsString(stars));
    }

    @NotNull
    private String createHotelStarsString(@NotNull Integer stars) {
        String hotelStars = "";
        hotelStars = hotelStars.concat(getString(R.string.hotel_stars) + " ");
        hotelStars = hotelStars.concat(stars.toString());
        return hotelStars;
    }

    private void setAvaragePrice(Double price) {
        getTextViewAvaragePrice().setText(createAvaragePriceString(price));
    }

    @NotNull
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
        callActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        callActivityIntent.setData(Uri.parse("tel:" + getTextViewPhoneNumber().getText().toString()));
        return callActivityIntent;
    }

    @NotNull
    private Intent createMapsActivityIntent() {
        String uri = String.format(Locale.ENGLISH, "geo:0,0?q=" + createAddressString());
        Intent mapsActivityIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        mapsActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return mapsActivityIntent;
    }

    @NotNull
    private Intent createWriteReviewActivityIntent() {
        Intent writeReviewActivityIntent = new Intent(getContext(), WriteReviewActivity.class);
        writeReviewActivityIntent.putExtra(ID, getId());
        writeReviewActivityIntent.putExtra(ACCOMODATION_TYPE, HOTEL);
        return writeReviewActivityIntent;
    }

    @NotNull
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
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(hotelDetailActivity);
        LayoutInflater layoutInflater = hotelDetailActivity.getLayoutInflater();
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
        hotelDetailActivity.runOnUiThread(() -> {
            Toast.makeText(hotelDetailActivity, getString(string), Toast.LENGTH_SHORT).show();
        });
    }

    private CollapsingToolbarLayout getCollapsingToolbarLayout(){
        return hotelDetailActivity.getCollapsingToolbarLayout();
    }

    private TextView getTextViewPhoneNumber(){
        return hotelDetailActivity.getTextViewPhoneNumber();
    }

    private TextView getTextViewAddress(){
        return hotelDetailActivity.getTextViewAddress();
    }

    private TextView getTextViewCertificateOfExcellence(){
        return hotelDetailActivity.getTextViewCertificateOfExcellence();
    }

    private TextView getTextViewAvarageRating(){
        return hotelDetailActivity.getTextViewAvarageRating();
    }

    private TextView getTextViewAvaragePrice(){
        return hotelDetailActivity.getTextViewAvaragePrice();
    }

    private TextView getTextViewStars(){
        return hotelDetailActivity.getTextViewStars();
    }

    private FloatingActionButton getFloatingActionButtonWriteReview() {
        return hotelDetailActivity.getFloatingActionButtonWriteReview();
    }

    private Button getButtonReadReviews() {
        return hotelDetailActivity.getButtonReadReviews();
    }

    private boolean hasAvarageRating() {
        return hotel.hasAvarageRating();
    }

    private boolean hasReviews() {
        return hotel.hasReviews();
    }

    private ViewPager getViewPager() {
        return hotelDetailActivity.getViewPager();
    }

    private List<String> getImages(){
        return hotel.getImages();
    }

    private Intent getIntent(){
        return hotelDetailActivity.getIntent();
    }

    private String getId() {
        return getIntent().getStringExtra(ID);
    }

    private String getName(){
        return hotel.getName();
    }

    private Double getAvaragePrice(){
        return hotel.getAvaragePrice();
    }

    private boolean isHasCertificateOfExcellence(){
        return hotel.isHasCertificateOfExcellence();
    }

    private String getPhoneNumber(){
        return hotel.getPhoneNumber();
    }

    private Integer getStars(){
        return hotel.getStars();
    }

    private String getTypeOfAddress(){
        return hotel.getTypeOfAddress();
    }

    private String getStreet(){
        return hotel.getStreet();
    }

    private String getHouseNumber(){
        return hotel.getHouseNumber();
    }

    private String getCity(){
        return hotel.getCity();
    }

    private String getProvince(){
        return hotel.getProvince();
    }

    private String getPostalCode(){
        return hotel.getPostalCode();
    }

    private Context getContext() {
        return hotelDetailActivity.getApplicationContext();
    }

    private Resources getResources(){
        return hotelDetailActivity.getResources();
    }

    @NotNull
    private String getString(int id){
        return getResources().getString(id);
    }

    private HotelDAO getHotelDAO() {
        return daoFactory.getHotelDAO(getStorageTechnology(HOTEL_STORAGE_TECHNOLOGY));
    }

    private String getStorageTechnology(String storageTechnology) {
        return ConfigFileReader.getProperty(storageTechnology, getContext());
    }

}
