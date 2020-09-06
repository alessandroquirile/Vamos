package com.quiriletelese.troppadvisorproject.controllers;

import android.content.Intent;
import android.view.View;

import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.adapters.ViewPagerOverViewActivityAdapter;
import com.quiriletelese.troppadvisorproject.interfaces.Constants;
import com.quiriletelese.troppadvisorproject.model_helpers.Address;
import com.quiriletelese.troppadvisorproject.models.Attraction;
import com.quiriletelese.troppadvisorproject.views.AttractionDetailActivity;
import com.quiriletelese.troppadvisorproject.views.WriteReviewActivity;

public class AttractionDetailActivityController implements View.OnClickListener, Constants {

    private AttractionDetailActivity attractionDetailActivity;

    public AttractionDetailActivityController(AttractionDetailActivity attractionDetailActivity) {
        this.attractionDetailActivity = attractionDetailActivity;
    }

    @Override
    public void onClick(View view) {

    }

    public void setListenerOnViewComponents() {
        attractionDetailActivity.getFloatingActionButtonAttractionWriteReview().setOnClickListener(this);
        attractionDetailActivity.getButtonAttractionReadReviews().setOnClickListener(this);
    }

    public void initializaViewPager() {
        Attraction attraction = (Attraction) attractionDetailActivity.getIntent().getSerializableExtra(ATTRACTION);
        ViewPagerOverViewActivityAdapter viewPagerOverViewActivityAdapter = new ViewPagerOverViewActivityAdapter(attraction.getImages(), attractionDetailActivity.getApplicationContext());
        attractionDetailActivity.getViewPagerAttractionDetail().setAdapter(viewPagerOverViewActivityAdapter);
    }

    public void initializeActivityFields() {
        Attraction attraction = (Attraction) attractionDetailActivity.getIntent().getSerializableExtra(ATTRACTION);
        setCollapsingToolbarLayoutTitle(attraction.getName());
        setAvarageRating(attraction.getAvarageRating());
        setCertificateOfExcellence(attraction.isHasCertificateOfExcellence());
        setAddress(attraction.getAddress());
        setOpeningTime(attraction.getOpeningTime());
        setPhoneNunmber(attraction.getPhoneNumber());
        setAvaragePrice(attraction.getAvaragePrice());
    }

    private void setCollapsingToolbarLayoutTitle(String title) {
        attractionDetailActivity.getCollapsingToolbarLayoutAttractionDetailActivity().setTitle(title);
    }

    private void setAvarageRating(Integer avarageRating) {
        if (avarageRating.equals(0))
            attractionDetailActivity.getTextViewAttractionAvarageRating().setText(R.string.no_review);
        else
            attractionDetailActivity.getTextViewAttractionAvarageRating().setText(avarageRating + "/5");
    }

    private void setCertificateOfExcellence(boolean certificateOfExcellence) {
        if (!certificateOfExcellence)
            attractionDetailActivity.getTextViewAttractionCertificateOfExcellence().setVisibility(View.GONE);
    }

    private void setAddress(Address address) {
        String restaurantAddress = createAddressString(address);
        attractionDetailActivity.getTextViewAttractionAddress().setText(restaurantAddress);
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
            attractionDetailActivity.getTextViewAttractionOpeningTime().setText(openingTime);
        else
            attractionDetailActivity.getTextViewAttractionOpeningTime().setText(attractionDetailActivity
            .getResources().getString(R.string.no_information_available));
    }

    private void setPhoneNunmber(String phoneNumber) {
        if (!phoneNumber.equals(""))
            attractionDetailActivity.getTextViewAttractionPhoneNumber().setText(phoneNumber);
        else
            attractionDetailActivity.getTextViewAttractionPhoneNumber().setText(attractionDetailActivity
                    .getResources().getString(R.string.no_phone_number));
    }

    private void setAvaragePrice(Integer price) {
        if (!price.equals(0))
            attractionDetailActivity.getTextViewAttractionAvaragePrice().setText(createAvaragePriceString(price));
        else
            attractionDetailActivity.getTextViewAttractionAvaragePrice().setText(attractionDetailActivity
                    .getResources().getString(R.string.gratis));
    }

    private String createAvaragePriceString(Integer price) {
        String avaragePrice = "";
        avaragePrice = avaragePrice.concat(attractionDetailActivity.getResources().getString(R.string.avarage_price) + " ");
        avaragePrice = avaragePrice.concat(price + " ");
        avaragePrice = avaragePrice.concat(attractionDetailActivity.getResources().getString(R.string.currency));
        return avaragePrice;
    }

    private void startWriteReviewActivity() {
        Intent writeReviewActivityIntent = new Intent(attractionDetailActivity.getApplicationContext(), WriteReviewActivity.class);
        writeReviewActivityIntent.putExtra(ID, getRestaurantId());
        writeReviewActivityIntent.putExtra(ACCOMODATION_TYPE, ATTRACTION);
        attractionDetailActivity.startActivity(writeReviewActivityIntent);

    }

    private String getRestaurantId() {
        Attraction attraction = (Attraction) attractionDetailActivity.getIntent().getSerializableExtra(ATTRACTION);
        return attraction.getId();
    }

}
