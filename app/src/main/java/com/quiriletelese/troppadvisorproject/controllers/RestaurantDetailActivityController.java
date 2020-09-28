package com.quiriletelese.troppadvisorproject.controllers;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.adapters.ViewPagerOverViewActivityAdapter;
import com.quiriletelese.troppadvisorproject.dao_interfaces.RestaurantDAO;
import com.quiriletelese.troppadvisorproject.factories.DAOFactory;
import com.quiriletelese.troppadvisorproject.interfaces.Constants;
import com.quiriletelese.troppadvisorproject.model_helpers.Address;
import com.quiriletelese.troppadvisorproject.models.Restaurant;
import com.quiriletelese.troppadvisorproject.utils.ConfigFileReader;
import com.quiriletelese.troppadvisorproject.views.RestaurantDetailActivity;
import com.quiriletelese.troppadvisorproject.views.SeeReviewsActivity;
import com.quiriletelese.troppadvisorproject.views.WriteReviewActivity;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBack;

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

    public void setListenerOnViewComponents() {
        restaurantDetailActivity.getFloatingActionButtonRestaurantWriteReview().setOnClickListener(this);
        restaurantDetailActivity.getButtonRestaurantReadReviews().setOnClickListener(this);
    }

    private void findHotelByIdHelper(VolleyCallBack volleyCallBack, String id, Context context) {
        RestaurantDAO restaurantDAO = daoFactory.getRestaurantDAO(ConfigFileReader.getProperty(HOTEL_STORAGE_TECHNOLOGY,
                restaurantDetailActivity.getApplicationContext()));
        restaurantDAO.findById(volleyCallBack, id, context);
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
        }, getRestaurantId(), restaurantDetailActivity.getApplicationContext());
    }


    private void initializeActivityFields() {
        setCollapsingToolbarLayoutTitle(restaurant.getName());
        setAvarageRating(restaurant);
        setCertificateOfExcellence(restaurant.isHasCertificateOfExcellence());
        setAddress(restaurant.getAddress());
        setOpeningTime(restaurant.getOpeningTime());
        setPhoneNunmber(restaurant.getPhoneNumber());
        setAvaragePrice(restaurant.getAvaragePrice());
        setTypeOfCuisineList(restaurant.getTypeOfCuisine());
    }

    private void initializeViewPager() {
        ViewPagerOverViewActivityAdapter viewPagerOverViewActivityAdapter = new ViewPagerOverViewActivityAdapter(
                restaurant.getImages(), restaurantDetailActivity.getApplicationContext());
        restaurantDetailActivity.getViewPagerRestaurantDetail().setAdapter(viewPagerOverViewActivityAdapter);
    }

    private void detectVolleyError(String errorCode) {
        switch (errorCode) {
            case NO_CONTENT:
                showToastNoContentError();
                break;
        }
    }

    private void setCollapsingToolbarLayoutTitle(String title) {
        restaurantDetailActivity.getCollapsingToolbarLayoutRestaurantDetailActivity().setTitle(title);
    }

    private void setAvarageRating(Restaurant restaurant) {
        if (!hasAvarageRating(restaurant.getAvarageRating()))
            restaurantDetailActivity.getTextViewRestaurantAvarageRating().setText(R.string.no_review);
        else
            restaurantDetailActivity.getTextViewRestaurantAvarageRating().setText(createAvarageRatingString(restaurant));
    }

    private boolean hasAvarageRating(Integer avarageRating) {
        return !avarageRating.equals(0);
    }

    private String createAvarageRatingString(Restaurant restaurant) {
        String avarageRating = "";
        avarageRating = avarageRating.concat(restaurant.getAvarageRating() + "/5 (");
        avarageRating = avarageRating.concat(restaurant.getTotalReviews() + " ");
        avarageRating = avarageRating.concat(restaurantDetailActivity.getResources().getString(R.string.reviews) + ")");
        return avarageRating;
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

    private void setTypeOfCuisineListEmpty() {
        restaurantDetailActivity.getTextViewTypeOfCuisineList().setText(restaurantDetailActivity
                .getResources().getString(R.string.no_information_available));
    }

    private void startWriteReviewActivity() {
        Intent writeReviewActivityIntent = new Intent(restaurantDetailActivity.getApplicationContext(), WriteReviewActivity.class);
        writeReviewActivityIntent.putExtra(ID, getRestaurantId());
        writeReviewActivityIntent.putExtra(ACCOMODATION_TYPE, RESTAURANT);
        restaurantDetailActivity.startActivity(writeReviewActivityIntent);
    }

    private void seeReviews() {
        if (hasReviews())
            startSeeReviewsActivity();
        else
            showToastNoReviewsError();
    }

    private void startSeeReviewsActivity() {
        restaurantDetailActivity.startActivity(createSeeReviewsActivityIntent());
    }

    private Intent createSeeReviewsActivityIntent() {
        Intent seeReviewsActivityIntent = new Intent(restaurantDetailActivity.getApplicationContext(), SeeReviewsActivity.class);
        seeReviewsActivityIntent.putExtra(ACCOMODATION_NAME, getRestaurantName());
        seeReviewsActivityIntent.putExtra(ID, getRestaurantId());
        return seeReviewsActivityIntent;
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

    private boolean hasReviews() {
        return restaurant.getReviews().size() > 0;
    }

    private String getRestaurantId() {
        return restaurantDetailActivity.getIntent().getStringExtra(ID);
    }

    private String getRestaurantName() {
        return restaurant.getName();
    }

    private String getNoContentErrorMessage(){
        return restaurantDetailActivity.getResources().getString(R.string.no_content_error_hotel_detail);
    }

    private String getNoReviewsErrorMessage(){
        return restaurantDetailActivity.getResources().getString(R.string.no_review);
    }

}
