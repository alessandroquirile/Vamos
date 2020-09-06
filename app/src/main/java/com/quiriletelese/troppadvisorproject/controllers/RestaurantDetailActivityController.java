package com.quiriletelese.troppadvisorproject.controllers;

import android.content.Intent;
import android.view.View;

import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.adapters.ViewPagerOverViewActivityAdapter;
import com.quiriletelese.troppadvisorproject.interfaces.Constants;
import com.quiriletelese.troppadvisorproject.model_helpers.Address;
import com.quiriletelese.troppadvisorproject.models.Restaurant;
import com.quiriletelese.troppadvisorproject.views.RestaurantDetailActivity;
import com.quiriletelese.troppadvisorproject.views.WriteReviewActivity;

import java.util.List;

public class RestaurantDetailActivityController implements View.OnClickListener, Constants {

    private RestaurantDetailActivity restaurantDetailActivity;

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
        }
    }

    public void setListenerOnViewComponents(){
        restaurantDetailActivity.getFloatingActionButtonRestaurantWriteReview().setOnClickListener(this);
        restaurantDetailActivity.getButtonRestaurantReadReviews().setOnClickListener(this);
    }

    public void initializaViewPager() {
        Restaurant restaurant = (Restaurant) restaurantDetailActivity.getIntent().getSerializableExtra(RESTAURANT);
        ViewPagerOverViewActivityAdapter viewPagerOverViewActivityAdapter = new ViewPagerOverViewActivityAdapter(restaurant.getImages(), restaurantDetailActivity.getApplicationContext());
        restaurantDetailActivity.getViewPagerRestaurantDetail().setAdapter(viewPagerOverViewActivityAdapter);
    }

    public void initializeActivityFields() {
        Restaurant restaurant = (Restaurant) restaurantDetailActivity.getIntent().getSerializableExtra(RESTAURANT);
        setCollapsingToolbarLayoutTitle(restaurant.getName());
        setAvarageRating(restaurant.getAvarageRating());
        setCertificateOfExcellence(restaurant.isHasCertificateOfExcellence());
        setAddress(restaurant.getAddress());
        setOpeningTime(restaurant.getOpeningTime());
        setPhoneNunmber(restaurant.getPhoneNumber());
        setAvaragePrice(restaurant.getAvaragePrice());
        setTypeOfCuisineList(restaurant.getTypeOfCuisine());
    }

    private void setCollapsingToolbarLayoutTitle(String title) {
        restaurantDetailActivity.getCollapsingToolbarLayoutRestaurantDetailActivity().setTitle(title);
    }

    private void setAvarageRating(Integer avarageRating) {
        if (avarageRating.equals(0))
            restaurantDetailActivity.getTextViewRestaurantAvarageRating().setText(R.string.no_review);
        else
            restaurantDetailActivity.getTextViewRestaurantAvarageRating().setText(avarageRating + "/5");
    }

    private void setCertificateOfExcellence(boolean certificateOfExcellence) {
        if (!certificateOfExcellence)
            restaurantDetailActivity.getTextViewRestaurantCertificateOfExcellence().setVisibility(View.GONE);
    }

    private void setAddress(Address address) {
        String restaurantAddress = createAddressString(address);
        restaurantDetailActivity.getTextViewRestaurantAddress().setText(restaurantAddress);
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
            restaurantDetailActivity.getTextViewRestaurantOpeningTime().setText(openingTime);
        else
            restaurantDetailActivity.getTextViewRestaurantOpeningTime().setText(restaurantDetailActivity
                    .getResources().getString(R.string.no_information_available));
    }

    private void setPhoneNunmber(String phoneNumber) {
        if (!phoneNumber.equals(""))
            restaurantDetailActivity.getTextViewRestaurantPhoneNumber().setText(phoneNumber);
        else
            restaurantDetailActivity.getTextViewRestaurantPhoneNumber().setText(restaurantDetailActivity
                    .getResources().getString(R.string.no_phone_number));
    }

    private void setAvaragePrice(Integer price) {
        restaurantDetailActivity.getTextViewRestaurantAvaragePrice().setText(createAvaragePriceString(price));
    }

    private String createAvaragePriceString(Integer price) {
        String avaragePrice = "";
        avaragePrice = avaragePrice.concat(restaurantDetailActivity.getResources().getString(R.string.avarage_price) + " ");
        avaragePrice = avaragePrice.concat(price + " ");
        avaragePrice = avaragePrice.concat(restaurantDetailActivity.getResources().getString(R.string.currency));
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
        String typeOfCuisine = restaurantDetailActivity.getTextViewTypeOfCuisineList().getText().toString();
        typeOfCuisine = typeOfCuisine.concat("- " + cuisine + "\n");
        restaurantDetailActivity.getTextViewTypeOfCuisineList().setText(typeOfCuisine);
    }

    private void setTypeOfCuisineListEmpty(){
        restaurantDetailActivity.getTextViewTypeOfCuisineList().setText(restaurantDetailActivity
                .getResources().getString(R.string.no_information_available));
    }

    private void startWriteReviewActivity() {
        Intent writeReviewActivityIntent = new Intent(restaurantDetailActivity.getApplicationContext(), WriteReviewActivity.class);
        writeReviewActivityIntent.putExtra(ID, getRestaurantId());
        writeReviewActivityIntent.putExtra(ACCOMODATION_TYPE, RESTAURANT);
        restaurantDetailActivity.startActivity(writeReviewActivityIntent);

    }

    private String getRestaurantId() {
        Restaurant restaurant = (Restaurant) restaurantDetailActivity.getIntent().getSerializableExtra(RESTAURANT);
        return restaurant.getId();
    }

}
