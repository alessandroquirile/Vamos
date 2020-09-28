package com.quiriletelese.troppadvisorproject.controllers;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.adapters.ViewPagerOverViewActivityAdapter;
import com.quiriletelese.troppadvisorproject.dao_interfaces.AttractionDAO;
import com.quiriletelese.troppadvisorproject.dao_interfaces.HotelDAO;
import com.quiriletelese.troppadvisorproject.factories.DAOFactory;
import com.quiriletelese.troppadvisorproject.interfaces.Constants;
import com.quiriletelese.troppadvisorproject.model_helpers.Address;
import com.quiriletelese.troppadvisorproject.models.Attraction;
import com.quiriletelese.troppadvisorproject.models.Hotel;
import com.quiriletelese.troppadvisorproject.utils.ConfigFileReader;
import com.quiriletelese.troppadvisorproject.views.AttractionDetailActivity;
import com.quiriletelese.troppadvisorproject.views.SeeReviewsActivity;
import com.quiriletelese.troppadvisorproject.views.WriteReviewActivity;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBack;

public class AttractionDetailActivityController implements View.OnClickListener, Constants {

    private AttractionDetailActivity attractionDetailActivity;
    private DAOFactory daoFactory = DAOFactory.getInstance();
    private Attraction attraction;

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

    public void setListenerOnViewComponents() {
        attractionDetailActivity.getFloatingActionButtonAttractionWriteReview().setOnClickListener(this);
        attractionDetailActivity.getButtonAttractionReadReviews().setOnClickListener(this);
    }

    private void findHotelByIdHelper(VolleyCallBack volleyCallBack, String id, Context context) {
        AttractionDAO attractionDAO = daoFactory.getAttractionDAO(ConfigFileReader.getProperty(HOTEL_STORAGE_TECHNOLOGY,
                attractionDetailActivity.getApplicationContext()));
        attractionDAO.findById(volleyCallBack, id, context);
    }

    public void findById() {
        findHotelByIdHelper(new VolleyCallBack() {
            @Override
            public void onSuccess(Object object) {
                attraction = (Attraction) object;
                initializeActivityFields();
                initializeViewPager();
            }

            @Override
            public void onError(String errorCode) {
                detectVolleyError(errorCode);
            }
        }, getAttractionId(), attractionDetailActivity.getApplicationContext());
    }

    public void initializeActivityFields() {
        setCollapsingToolbarLayoutTitle(attraction.getName());
        setAvarageRating(attraction.getAvarageRating());
        setCertificateOfExcellence(attraction.isHasCertificateOfExcellence());
        setAddress(attraction.getAddress());
        setOpeningTime(attraction.getOpeningTime());
        setPhoneNunmber(attraction.getPhoneNumber());
        setAvaragePrice(attraction.getAvaragePrice());
    }

    private void initializeViewPager() {
        ViewPagerOverViewActivityAdapter viewPagerOverViewActivityAdapter = new ViewPagerOverViewActivityAdapter(attraction.getImages(), attractionDetailActivity.getApplicationContext());
        attractionDetailActivity.getViewPagerAttractionDetail().setAdapter(viewPagerOverViewActivityAdapter);
    }

    private void detectVolleyError(String errorCode) {
        switch (errorCode) {
            case NO_CONTENT:
                showToastNoContentError();
                break;
        }
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
        attractionDetailActivity.startActivity(createWriteReviewActivityIntent());
    }

    private Intent createWriteReviewActivityIntent() {
        Intent writeReviewActivityIntent = new Intent(attractionDetailActivity.getApplicationContext(), WriteReviewActivity.class);
        writeReviewActivityIntent.putExtra(ID, getAttractionId());
        writeReviewActivityIntent.putExtra(ACCOMODATION_TYPE, ATTRACTION);
        return writeReviewActivityIntent;
    }

    private void seeReviews() {
        if (hasReviews())
            startSeeReviewsActivity();
        else
            showToastNoReviewsError();
    }

    private void startSeeReviewsActivity() {
        attractionDetailActivity.startActivity(createSeeReviewsActivityIntent());
    }

    private Intent createSeeReviewsActivityIntent() {
        Intent seeReviewsActivityIntent = new Intent(attractionDetailActivity.getApplicationContext(), SeeReviewsActivity.class);
        seeReviewsActivityIntent.putExtra(ACCOMODATION_NAME, getAttractionName());
        seeReviewsActivityIntent.putExtra(ID, getAttractionId());
        return seeReviewsActivityIntent;
    }

    private void showToastNoContentError() {
        attractionDetailActivity.runOnUiThread(() -> {
            Toast.makeText(attractionDetailActivity, getNoContentErrorMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void showToastNoReviewsError(){
        attractionDetailActivity.runOnUiThread(() -> {
            Toast.makeText(attractionDetailActivity, getNoReviewsErrorMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private boolean hasReviews() {
        return attraction.getReviews().size() > 0;
    }

    private String getAttractionId() {
        return attractionDetailActivity.getIntent().getStringExtra(ID);
    }

    private String getAttractionName(){
        return attraction.getName();
    }

    private String getNoContentErrorMessage(){
        return attractionDetailActivity.getResources().getString(R.string.no_content_error_attraction_detail);
    }

    private String getNoReviewsErrorMessage(){
        return attractionDetailActivity.getResources().getString(R.string.no_review);
    }

}
