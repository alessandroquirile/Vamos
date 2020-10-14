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
import com.quiriletelese.troppadvisorproject.dao_interfaces.HotelDAO;
import com.quiriletelese.troppadvisorproject.factories.DAOFactory;
import com.quiriletelese.troppadvisorproject.interfaces.Constants;
import com.quiriletelese.troppadvisorproject.model_helpers.Address;
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
        switch (view.getId()) {
            case R.id.floating_action_button_hotel_write_review:
                startWriteReviewActivity();
                break;
            case R.id.button_hotel_read_reviews:
                seeReviews();
                break;
        }
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

    public void setListenerOnViewComponents() {
        getFloatingActionButtonWriteReview().setOnClickListener(this);
        getButtonReadReviews().setOnClickListener(this);
    }

    private void initializeActivityFields() {
        setCollapsingToolbarLayoutTitle(getName());
        setAvaragePrice(getAvaragePrice());
        setAvarageRating(hotel);
        setCertificateOfExcellence(isHasCertificateOfExcellence());
        setAddress(getAddress());
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

    private void setAvarageRating(@NotNull Hotel hotel) {
        if (!hasAvarageRating(hotel.getAvarageRating()))
            getTextViewAvarageRating().setText(R.string.no_reviews);
        else
            getTextViewAvarageRating().setText(createAvarageRatingString(hotel));
    }

    @NotNull
    private String createAvarageRatingString(@NotNull Hotel hotel) {
        String avarageRating = "";
        avarageRating = avarageRating.concat(hotel.getAvarageRating() + "/5 (");
        avarageRating = avarageRating.concat(hotel.getTotalReviews() + " ");
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

    @NotNull
    private String createAddressString(@NotNull Address address) {
        String hotelAddress = "";
        hotelAddress = hotelAddress.concat(address.getType() + " ");
        hotelAddress = hotelAddress.concat(address.getStreet() + ", ");
        hotelAddress = hotelAddress.concat(address.getHouseNumber() + ", ");
        hotelAddress = hotelAddress.concat(address.getCity() + ", ");
        hotelAddress = hotelAddress.concat(address.getProvince() + ", ");
        hotelAddress = hotelAddress.concat(address.getPostalCode());
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

    private void setAvaragePrice(Integer price) {
        getTextViewAvaragePrice().setText(createAvaragePriceString(price));
    }

    @NotNull
    private String createAvaragePriceString(Integer price) {
        String avaragePrice = "";
        avaragePrice = avaragePrice.concat(getString(R.string.avarage_price) + " ");
        avaragePrice = avaragePrice.concat(price + " ");
        avaragePrice = avaragePrice.concat(getString(R.string.currency));
        return avaragePrice;
    }

    private void startWriteReviewActivity() {
        hotelDetailActivity.startActivity(createWriteReviewActivityIntent());
    }

    private void startSeeReviewsActivity() {
        hotelDetailActivity.startActivity(createSeeReviewsActivityIntent());
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

    @Contract(pure = true)
    private boolean hasAvarageRating(@NotNull Integer avarageRating) {
        return !avarageRating.equals(0);
    }

    private boolean hasReviews() {
        return getReviews().size() > 0;
    }

    private ViewPager getViewPager() {
        return hotelDetailActivity.getViewPager();
    }

    private List<String> getImages(){
        return hotel.getImages();
    }

    private List<Review> getReviews(){
        return hotel.getReviews();
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

    private Integer getAvaragePrice(){
        return hotel.getAvaragePrice();
    }

    private boolean isHasCertificateOfExcellence(){
        return hotel.isHasCertificateOfExcellence();
    }

    private Address getAddress(){
        return hotel.getAddress();
    }

    private String getPhoneNumber(){
        return hotel.getPhoneNumber();
    }

    private Integer getStars(){
        return hotel.getStars();
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
