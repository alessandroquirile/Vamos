package com.quiriletelese.troppadvisorproject.controllers;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.adapters.ViewPagerOverViewActivityAdapter;
import com.quiriletelese.troppadvisorproject.dao_interfaces.HotelDAO;
import com.quiriletelese.troppadvisorproject.factories.DAOFactory;
import com.quiriletelese.troppadvisorproject.interfaces.Constants;
import com.quiriletelese.troppadvisorproject.model_helpers.Address;
import com.quiriletelese.troppadvisorproject.models.Hotel;
import com.quiriletelese.troppadvisorproject.utils.ConfigFileReader;
import com.quiriletelese.troppadvisorproject.views.HotelDetailActivity;
import com.quiriletelese.troppadvisorproject.views.SeeReviewsActivity;
import com.quiriletelese.troppadvisorproject.views.WriteReviewActivity;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBack;

public class HotelDetailActivityController implements View.OnClickListener, Constants {

    private HotelDetailActivity hotelDetailActivity;
    private DAOFactory daoFactory = DAOFactory.getInstance();
    private Hotel hotel;

    public HotelDetailActivityController(HotelDetailActivity hotelDetailActivity) {
        this.hotelDetailActivity = hotelDetailActivity;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.floating_action_button_hotel_write_review:
                startWriteReviewActivity();
                break;
            case R.id.button_hotel_read_reviews:
                seeReviews();
                break;
        }
    }

    public void setListenerOnViewComponents() {
        hotelDetailActivity.getFloatingActionButtonHotelWriteReview().setOnClickListener(this);
        hotelDetailActivity.getButtonHotelReadReviews().setOnClickListener(this);
    }

    private void findHotelByIdHelper(VolleyCallBack volleyCallBack, String id, Context context) {
        HotelDAO hotelDAO = daoFactory.getHotelDAO(ConfigFileReader.getProperty(HOTEL_STORAGE_TECHNOLOGY,
                hotelDetailActivity.getApplicationContext()));
        hotelDAO.findById(volleyCallBack, id, context);
    }

    public void findById() {
        findHotelByIdHelper(new VolleyCallBack() {
            @Override
            public void onSuccess(Object object) {
                hotel = (Hotel) object;
                initializeActivityFields();
                initializeViewPager();
            }

            @Override
            public void onError(String errorCode) {
                detectVolleyError(errorCode);
            }
        }, getHotelId(), hotelDetailActivity.getApplicationContext());
    }

    private void initializeActivityFields() {
        setCollapsingToolbarLayoutTitle(hotel.getName());
        setAvarageRating(hotel.getAvarageRating());
        setCertificateOfExcellence(hotel.isHasCertificateOfExcellence());
        setAddress(hotel.getAddress());
        setPhoneNunmber(hotel.getPhoneNumber());
        setHotelStars(hotel.getStars());
        setAvaragePrice(hotel.getAvaragePrice());
    }

    private void initializeViewPager() {
        ViewPagerOverViewActivityAdapter viewPagerOverViewActivityAdapter = new ViewPagerOverViewActivityAdapter(
                hotel.getImages(), hotelDetailActivity.getApplicationContext());
        hotelDetailActivity.getViewPagerOverview().setAdapter(viewPagerOverViewActivityAdapter);
    }

    private void detectVolleyError(String errorCode) {
        switch (errorCode) {
            case NO_CONTENT:
                showToastNoContentError();
                break;
        }
    }

    private void setCollapsingToolbarLayoutTitle(String title) {
        hotelDetailActivity.getCollapsingToolbarLayoutHotelDetailActivity().setTitle(title);
    }

    private void setAvarageRating(Integer avarageRating) {
        if (avarageRating.equals(0))
            hotelDetailActivity.getTextViewHotelAvarageRating().setText(R.string.no_review);
        else
            hotelDetailActivity.getTextViewHotelAvarageRating().setText(avarageRating + "/5");
    }

    private void setCertificateOfExcellence(boolean certificateOfExcellence) {
        if (!certificateOfExcellence)
            hotelDetailActivity.getTextViewHotelCertificateOfExcellence().setVisibility(View.GONE);
    }

    private void setAddress(Address address) {
        String hotelAddress = createAddressString(address);
        hotelDetailActivity.getTextViewHotelAddress().setText(hotelAddress);
    }

    private String createAddressString(Address address) {
        String hotelAddress = "";
        hotelAddress = hotelAddress.concat(address.getType() + " ");
        hotelAddress = hotelAddress.concat(address.getStreet() + ", ");
        hotelAddress = hotelAddress.concat(address.getHouseNumber() + ", ");
        hotelAddress = hotelAddress.concat(address.getCity() + ", ");
        hotelAddress = hotelAddress.concat(address.getProvince() + ", ");
        hotelAddress = hotelAddress.concat(address.getPostalCode());
        return hotelAddress;
    }

    private void setPhoneNunmber(String phoneNumber) {
        if (!phoneNumber.equals(""))
            hotelDetailActivity.getTextViewHotelPhoneNumber().setText(phoneNumber);
        else
            hotelDetailActivity.getTextViewHotelPhoneNumber().setText(hotelDetailActivity
                    .getResources().getString(R.string.no_phone_number));
    }

    private void setHotelStars(Integer stars) {
        hotelDetailActivity.getTextViewHotelStars().setText(createHotelStarsString(stars));
    }

    private String createHotelStarsString(Integer stars) {
        String hotelStars = "";
        hotelStars = hotelStars.concat(hotelDetailActivity.getResources().getString(R.string.hotel_stars) + " ");
        hotelStars = hotelStars.concat(stars.toString());
        return hotelStars;
    }

    private void setAvaragePrice(Integer price) {
        hotelDetailActivity.getTextViewHotelAvaragePrice().setText(createAvaragePriceString(price));
    }

    private String createAvaragePriceString(Integer price) {
        String avaragePrice = "";
        avaragePrice = avaragePrice.concat(hotelDetailActivity.getResources().getString(R.string.avarage_price) + " ");
        avaragePrice = avaragePrice.concat(price + " ");
        avaragePrice = avaragePrice.concat(hotelDetailActivity.getResources().getString(R.string.currency));
        return avaragePrice;
    }

    private void startWriteReviewActivity() {
        Intent writeReviewActivityIntent = new Intent(hotelDetailActivity.getApplicationContext(), WriteReviewActivity.class);
        writeReviewActivityIntent.putExtra(ID, getHotelId());
        writeReviewActivityIntent.putExtra(ACCOMODATION_TYPE, HOTEL);
        hotelDetailActivity.startActivity(writeReviewActivityIntent);

    }

    private void seeReviews() {
        if (hasReviews())
            startSeeReviewsActivity();
        else
            showToastNoReviewsError();
    }

    private void startSeeReviewsActivity() {
        hotelDetailActivity.startActivity(createSeeReviewsActivityIntent());
    }

    private Intent createSeeReviewsActivityIntent() {
        Intent seeReviewsActivityIntent = new Intent(hotelDetailActivity.getApplicationContext(), SeeReviewsActivity.class);
        seeReviewsActivityIntent.putExtra(ACCOMODATION_NAME, getHotelName());
        seeReviewsActivityIntent.putExtra(ID, getHotelId());
        return seeReviewsActivityIntent;
    }

    private void showToastNoContentError() {
        hotelDetailActivity.runOnUiThread(() -> {
            Toast.makeText(hotelDetailActivity, getNoContentErrorMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void showToastNoReviewsError(){
        hotelDetailActivity.runOnUiThread(() -> {
            Toast.makeText(hotelDetailActivity, getNoReviewsErrorMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private boolean hasReviews() {
        return hotel.getReviews().size() > 0;
    }

    private String getHotelId() {
        return hotelDetailActivity.getIntent().getStringExtra(ID);
    }

    private String getHotelName(){
        return hotel.getName();
    }

    private String getNoContentErrorMessage(){
        return hotelDetailActivity.getResources().getString(R.string.no_content_error_restaurant_detail);
    }

    private String getNoReviewsErrorMessage(){
        return hotelDetailActivity.getResources().getString(R.string.no_review);
    }

}
