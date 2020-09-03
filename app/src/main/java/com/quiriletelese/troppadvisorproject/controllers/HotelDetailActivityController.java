package com.quiriletelese.troppadvisorproject.controllers;

import android.view.View;

import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.adapters.ViewPagerOverViewActivityAdapter;
import com.quiriletelese.troppadvisorproject.model_helpers.Address;
import com.quiriletelese.troppadvisorproject.models.Hotel;
import com.quiriletelese.troppadvisorproject.views.HotelDetailActivity;

public class HotelDetailActivityController {

    private HotelDetailActivity hotelDetailActivity;

    public HotelDetailActivityController(HotelDetailActivity hotelDetailActivity) {
        this.hotelDetailActivity = hotelDetailActivity;
    }

    public void initializaViewPager() {
        Hotel hotel = (Hotel) hotelDetailActivity.getIntent().getSerializableExtra("hotel");
        ViewPagerOverViewActivityAdapter viewPagerOverViewActivityAdapter = new ViewPagerOverViewActivityAdapter(hotel.getImages(), hotelDetailActivity.getApplicationContext());
        hotelDetailActivity.getViewPagerOverview().setAdapter(viewPagerOverViewActivityAdapter);
    }

    public void initializeActivityFields() {
        Hotel hotel = (Hotel) hotelDetailActivity.getIntent().getSerializableExtra("hotel");
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
            hotelDetailActivity.getTextViewAvarageRating().setText(R.string.no_review);
        else
            hotelDetailActivity.getTextViewAvarageRating().setText(avarageRating + "/5");
    }

    private void setCertificateOfExcellence(boolean certificateOfExcellence) {
        if (!certificateOfExcellence)
            hotelDetailActivity.getTextViewCertificateOfExcellence().setVisibility(View.GONE);
    }

    private void setAddress(Address address) {
        String hotelAddress = createAddressString(address);
        hotelDetailActivity.getTextViewAddress().setText(hotelAddress);
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
        hotelDetailActivity.getTextViewPhoneNumber().setText(phoneNumber);
    }

    private void setHotelStars(Integer stars) {
        hotelDetailActivity.getTextViewStars().setText(createHotelStarsString(stars));
    }

    private String createHotelStarsString(Integer stars) {
        String hotelStars = "";
        hotelStars = hotelStars.concat(hotelDetailActivity.getResources().getString(R.string.hotel_stars) + " ");
        hotelStars = hotelStars.concat(stars.toString());
        return hotelStars;
    }

    private void setAvaragePrice(Integer price) {
        hotelDetailActivity.getTextViewAvaragePrice().setText(createAvaragePriceString(price));
    }

    private String createAvaragePriceString(Integer price){
        String avaragePrice = "";
        avaragePrice = avaragePrice.concat(hotelDetailActivity.getResources().getString(R.string.avarage_price) + " ");
        avaragePrice = avaragePrice.concat(price + " ");
        avaragePrice = avaragePrice.concat(hotelDetailActivity.getResources().getString(R.string.currency));
        return avaragePrice;

    }

}
