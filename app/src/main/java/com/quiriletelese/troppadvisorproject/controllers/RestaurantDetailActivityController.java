package com.quiriletelese.troppadvisorproject.controllers;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
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
import com.quiriletelese.troppadvisorproject.dao_interfaces.RestaurantDAO;
import com.quiriletelese.troppadvisorproject.factories.DAOFactory;
import com.quiriletelese.troppadvisorproject.interfaces.Constants;
import com.quiriletelese.troppadvisorproject.model_helpers.Address;
import com.quiriletelese.troppadvisorproject.models.Restaurant;
import com.quiriletelese.troppadvisorproject.models.Review;
import com.quiriletelese.troppadvisorproject.utils.ConfigFileReader;
import com.quiriletelese.troppadvisorproject.views.RestaurantDetailActivity;
import com.quiriletelese.troppadvisorproject.views.SeeReviewsActivity;
import com.quiriletelese.troppadvisorproject.views.WriteReviewActivity;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBack;

import java.util.ArrayList;
import java.util.List;

public class RestaurantDetailActivityController implements View.OnClickListener, Constants {

    private RestaurantDetailActivity restaurantDetailActivity;
    private DAOFactory daoFactory = DAOFactory.getInstance();
    private Restaurant restaurant;

    public RestaurantDetailActivityController(RestaurantDetailActivity restaurantDetailActivity) {
        this.restaurantDetailActivity = restaurantDetailActivity;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.floating_action_button_restaurant_write_review:
                startWriteReviewActivity();
                break;
            case R.id.button_restaurant_read_reviews:
                seeReviews();
                break;
        }
    }

    private void findHotelByIdHelper(VolleyCallBack volleyCallBack, String id) {
        getResaurantDAO().findById(volleyCallBack, id, getContext());
    }

    public void findById() {
        findHotelByIdHelper(new VolleyCallBack() {
            @Override
            public void onSuccess(Object object) {
                restaurant = (Restaurant) object;
                initializeActivityFields();
                initializeViewPager();
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
        setAvarageRating(restaurant);
        setCertificateOfExcellence(isHasCertificateOfExcellence());
        setAddress(getAddress());
        setOpeningTime(getOpeningTime());
        setPhoneNunmber(getPhoneNumber());
        setAvaragePrice(getAvaragePrice());
        setTypeOfCuisineList(getTypeOfCuisine());
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
                showToastNoContentError();
                break;
        }
    }

    private void setCollapsingToolbarLayoutTitle(String title) {
        getCollapsingToolbarLayout().setTitle(title);
    }

    private void setAvarageRating(Restaurant restaurant) {
        if (!hasAvarageRating(restaurant.getAvarageRating()))
            getTextViewAvarageRating().setText(R.string.no_review);
        else
            getTextViewAvarageRating().setText(createAvarageRatingString(restaurant));
    }

    private String createAvarageRatingString(Restaurant restaurant) {
        String avarageRating = "";
        avarageRating = avarageRating.concat(restaurant.getAvarageRating() + "/5 (");
        avarageRating = avarageRating.concat(restaurant.getTotalReviews() + " ");
        avarageRating = avarageRating.concat(getString(R.string.reviews) + ")");
        return avarageRating;
    }

    private void setCertificateOfExcellence(boolean certificateOfExcellence) {
        if (!certificateOfExcellence)
            getTextViewCertificateOfExcellence().setVisibility(View.GONE);
    }

    private void setAddress(Address address) {
        String restaurantAddress = createAddressString(address);
        getTextViewAddress().setText(restaurantAddress);
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
        getTextViewAvaragePrice().setText(createAvaragePriceString(price));
    }

    private String createAvaragePriceString(Integer price) {
        String avaragePrice = "";
        avaragePrice = avaragePrice.concat(getString(R.string.avarage_price) + " ");
        avaragePrice = avaragePrice.concat(price + " ");
        avaragePrice = avaragePrice.concat(getString(R.string.currency));
        return avaragePrice;
    }

    private void setTypeOfCuisineList(List<String> typeOfCuisine) {
        if (!typeOfCuisine.isEmpty())
            for (String cuisine : typeOfCuisine)
                setTypeOfCuisineListHelper(cuisine);
        else
            setTypeOfCuisineListEmpty();
    }

    private void setTypeOfCuisineListHelper(String cuisine) {
        String typeOfCuisine = getTextViewTypeOfCuisineListText();
        typeOfCuisine = typeOfCuisine.concat("- " + cuisine + "\n");
        getTextViewTypeOfCuisineList().setText(typeOfCuisine);
    }

    private void setTypeOfCuisineListEmpty() {
        getTextViewTypeOfCuisineList().setText(getString(R.string.no_information_available));
    }

    private void startWriteReviewActivity() {
        restaurantDetailActivity.startActivity(createWriteReviewActivityIntent());
    }

    private void startSeeReviewsActivity() {
        restaurantDetailActivity.startActivity(createSeeReviewsActivityIntent());
    }

    private Intent createWriteReviewActivityIntent() {
        Intent writeReviewActivityIntent = new Intent(getContext(), WriteReviewActivity.class);
        writeReviewActivityIntent.putExtra(ID, getId());
        writeReviewActivityIntent.putExtra(ACCOMODATION_TYPE, RESTAURANT);
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
            showToastNoReviewsError();
    }

    private void showToastNoContentError() {
        restaurantDetailActivity.runOnUiThread(() -> {
            Toast.makeText(restaurantDetailActivity, getNoContentErrorMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void showToastNoReviewsError(){
        restaurantDetailActivity.runOnUiThread(() -> {
            Toast.makeText(restaurantDetailActivity, getNoReviewsErrorMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private CollapsingToolbarLayout getCollapsingToolbarLayout(){
        return restaurantDetailActivity.getCollapsingToolbarLayout();
    }

    private TextView getTextViewOpeningTime(){
        return restaurantDetailActivity.getTextViewOpeningTime();
    }

    private TextView getTextViewPhoneNumber(){
        return restaurantDetailActivity.getTextViewPhoneNumber();
    }

    private TextView getTextViewAddress(){
        return restaurantDetailActivity.getTextViewAddress();
    }

    private TextView getTextViewCertificateOfExcellence(){
        return restaurantDetailActivity.getTextViewCertificateOfExcellence();
    }

    private TextView getTextViewAvarageRating(){
        return restaurantDetailActivity.getTextViewAvarageRating();
    }

    private TextView getTextViewAvaragePrice(){
        return restaurantDetailActivity.getTextViewAvaragePrice();
    }

    private TextView getTextViewTypeOfCuisineList() {
        return restaurantDetailActivity.getTextViewTypeOfCuisineList();
    }

    private String getTextViewTypeOfCuisineListText(){
        return getTextViewTypeOfCuisineList().getText().toString();
    }

    private FloatingActionButton getFloatingActionButtonWriteReview(){
        return restaurantDetailActivity.getFloatingActionButtonWriteReview();
    }

    private Button getButtonReadReviews(){
        return restaurantDetailActivity.getButtonReadReviews();
    }

    private boolean hasAvarageRating(Integer avarageRating) {
        return !avarageRating.equals(0);
    }

    private boolean hasReviews() {
        return getReviews().size() > 0;
    }

    private ViewPager getViewPager(){
        return restaurantDetailActivity.getViewPager();
    }

    private List<String> getImages(){
        return restaurant.getImages();
    }

    private List<Review> getReviews(){
        return restaurant.getReviews();
    }

    private Intent getIntent(){
        return restaurantDetailActivity.getIntent();
    }

    private String getId() {
        return getIntent().getStringExtra(ID);
    }

    private String getName(){
        return restaurant.getName();
    }

    private Integer getAvaragePrice(){
        return restaurant.getAvaragePrice();
    }

    private boolean isHasCertificateOfExcellence(){
        return restaurant.isHasCertificateOfExcellence();
    }

    private Address getAddress(){
        return restaurant.getAddress();
    }

    private String getOpeningTime() {
        return restaurant.getOpeningTime();
    }

    private String getPhoneNumber(){
        return restaurant.getPhoneNumber();
    }

    private List<String> getTypeOfCuisine(){
        return restaurant.getTypeOfCuisine();
    }

    private Context getContext() {
        return restaurantDetailActivity.getApplicationContext();
    }

    private Resources getResources(){
        return restaurantDetailActivity.getResources();
    }

    private String getString(int id){
        return getResources().getString(id);
    }

    private RestaurantDAO getResaurantDAO() {
        return daoFactory.getRestaurantDAO(getStorageTechnology(RESTAURANT_STORAGE_TECHNOLOGY));
    }

    private String getStorageTechnology(String storageTechnology) {
        return ConfigFileReader.getProperty(storageTechnology, getContext());
    }

    private String getNoContentErrorMessage(){
        return getString(R.string.no_content_error_hotel_detail);
    }

    private String getNoReviewsErrorMessage(){
        return getString(R.string.no_review);
    }

}
