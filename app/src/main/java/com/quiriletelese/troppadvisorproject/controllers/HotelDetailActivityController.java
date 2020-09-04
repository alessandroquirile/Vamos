package com.quiriletelese.troppadvisorproject.controllers;

import android.content.Intent;
import android.view.View;

import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.adapters.ViewPagerOverViewActivityAdapter;
import com.quiriletelese.troppadvisorproject.interfaces.Constants;
import com.quiriletelese.troppadvisorproject.model_helpers.Address;
import com.quiriletelese.troppadvisorproject.models.Hotel;
import com.quiriletelese.troppadvisorproject.views.HotelDetailActivity;
import com.quiriletelese.troppadvisorproject.views.WriteReviewActivity;

public class HotelDetailActivityController implements View.OnClickListener, Constants {

    private HotelDetailActivity hotelDetailActivity;

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
        }
    }

    public void initializaViewPager() {
        Hotel hotel = (Hotel) hotelDetailActivity.getIntent().getSerializableExtra(HOTEL);
        ViewPagerOverViewActivityAdapter viewPagerOverViewActivityAdapter = new ViewPagerOverViewActivityAdapter(hotel.getImages(), hotelDetailActivity.getApplicationContext());
        hotelDetailActivity.getViewPagerOverview().setAdapter(viewPagerOverViewActivityAdapter);
    }

    public void initializeActivityFields() {
        Hotel hotel = (Hotel) hotelDetailActivity.getIntent().getSerializableExtra(HOTEL);
        setCollapsingToolbarLayoutTitle(hotel.getName());
        setAvarageRating(hotel.getAvarageRating());
        setCertificateOfExcellence(hotel.isHasCertificateOfExcellence());
        setAddress(hotel.getAddress());
        setPhoneNunmber(hotel.getPhoneNumber());
        setHotelStars(hotel.getStars());
        setAvaragePrice(hotel.getAvaragePrice());
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

    private String getHotelId() {
        Hotel hotel = (Hotel) hotelDetailActivity.getIntent().getSerializableExtra(HOTEL);
        return hotel.getId();
    }

}
