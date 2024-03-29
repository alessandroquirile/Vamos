package com.quiriletelese.troppadvisorproject.controllers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.model_helpers.Constants;
import com.quiriletelese.troppadvisorproject.models.Accomodation;
import com.quiriletelese.troppadvisorproject.views.AccomodationDetailMapsActivity;
import com.quiriletelese.troppadvisorproject.views.AttractionDetailActivity;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * @author Alessandro Quirile, Mauro Telese
 */

public class AccomodationDetailMapsActivityController implements View.OnClickListener {

    private final AccomodationDetailMapsActivity accomodationDetailMapsActivity;

    public AccomodationDetailMapsActivityController(AccomodationDetailMapsActivity accomodationDetailMapsActivity) {
        this.accomodationDetailMapsActivity = accomodationDetailMapsActivity;
    }

    @Override
    public void onClick(View view) {
        onClickHelper(view);
    }

    private void onClickHelper(@NotNull View view) {
        switch (view.getId()) {
            case R.id.relative_layout_accomodation_detail:
                startDetailActivity();
                break;
            case R.id.floating_action_button_go_back_accomodation_map_detail_activity:
                onBackPressed();
                break;
        }
    }

    private void startDetailActivity() {
        startDetailActivityHelper();
    }

    private void startDetailActivityHelper() {
        switch (getAccomodationType()) {
//            case "hotel":
//                startAccomodationDetailActivity(HotelDetailActivity.class);
//                break;
//            case "restaurant":
//                startAccomodationDetailActivity(RestaurantDetailActivity.class);
//                break;
            case "attraction":
                startAccomodationDetailActivity(AttractionDetailActivity.class);
                break;
        }
    }

    private void startAccomodationDetailActivity(Class<?> intentClass) {
        Intent detailActivityIntent = new Intent(getContext(), intentClass);
        detailActivityIntent.putExtra(Constants.getId(), getAccomodationId());
        detailActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(detailActivityIntent);
    }

    private void onBackPressed() {
        accomodationDetailMapsActivity.onBackPressed();
    }

    public void setRelativeLayoutDetailsFields() {
        Accomodation accomodation = getAccomodation();
        setImage(accomodation);
        getTextViewName().setText(accomodation.getName());
        getTextViewRating().setText(createAvarageRatingString(accomodation));
        getTextViewAddress().setText(createAddressString(accomodation));
    }

    @NotNull
    private String createAddressString(@NotNull Accomodation accomodation) {
        String attractionAddress = "";
        attractionAddress = attractionAddress.concat(accomodation.getTypeOfAddress() + " ");
        attractionAddress = attractionAddress.concat(accomodation.getStreet() + ", ");
        if (!accomodation.getHouseNumber().isEmpty())
            attractionAddress = attractionAddress.concat(accomodation.getHouseNumber() + ", ");
        attractionAddress = attractionAddress.concat(accomodation.getCity() + ", ");
        if (!accomodation.getProvince().equals(accomodation.getCity()))
            attractionAddress = attractionAddress.concat(accomodation.getProvince() + ", ");
        attractionAddress = attractionAddress.concat(accomodation.getPostalCode());
        return attractionAddress;
    }

    private String createAvarageRatingString(Accomodation accomodation) {
        return !hasAvarageRating(accomodation) ? getString(R.string.no_reviews) : createAvarageRatingStringHelper(accomodation);
    }

    @NotNull
    private String createAvarageRatingStringHelper(@NotNull Accomodation accomodation) {
        if (accomodation.getTotalReviews().intValue() == 1)
            return accomodation.getAvarageRating().intValue() + "/5 (" + accomodation.getTotalReviews() + " " + getString(R.string.review) + ")";
        else
            return accomodation.getAvarageRating().intValue() + "/5 (" + accomodation.getTotalReviews() + " " + getString(R.string.reviews) + ")";
    }

    private boolean hasAvarageRating(@NotNull Accomodation accomodation) {
        return accomodation.hasAvarageRating();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setImage(Accomodation accomodation) {
        if (hasImage(accomodation))
            Picasso.with(getContext())
                    .load(accomodation.getImages().get(0))
                    .placeholder(R.drawable.app_icon_no_background)
                    .error(R.drawable.picasso_error)
                    .into(getImageViewAccomodation());
        else
            getImageViewAccomodation().setImageDrawable(getResources().getDrawable(R.drawable.picasso_error));
    }

    public void addMarker() {
        getGoogleMap().addMarker(createMarkerOptions(getAccomodation()));
        setMapZoon();
    }

    private MarkerOptions createMarkerOptions(Accomodation accomodation) {
        return new MarkerOptions()
                .position(new LatLng(accomodation.getLatitude(), accomodation.getLongitude()))
                .icon(setCustomMarker(getContext(), getAccomodationMarker()))
                .title(accomodation.getName());
    }

    private BitmapDescriptor setCustomMarker(Context context, int id) {
        Drawable background = ContextCompat.getDrawable(context, id);
        assert background != null;
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void setMapZoon() {
        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(createMarkerLatLng(), 17.0f);
        getGoogleMap().animateCamera(yourLocation);
    }

    @NotNull
    @Contract(" -> new")
    private LatLng createMarkerLatLng() {
        return new LatLng(getAccomodation().getLatitude(), getAccomodation().getLongitude());
    }

    private int getAccomodationMarker() {
        return getAccomodationMarkerHelper();
    }

    private int getAccomodationMarkerHelper() {
        switch (getAccomodationType()) {
            case "hotel":
                return R.drawable.hotel_marker;
            case "restaurant":
                return R.drawable.restaurant_marker;
            case "attraction":
                return R.drawable.attraction_marker;
            default:
                return 0;
        }
    }

    public void setListenerOnViewComponents() {
        getRelativeLayoutDetails().setOnClickListener(this);
        getFloatingActionButtonGoBack().setOnClickListener(this);
    }

    @SuppressLint("MissingPermission")
    public void setMapProperties() {
        getGoogleMap().getUiSettings().setMyLocationButtonEnabled(false);
        getGoogleMap().getUiSettings().setMapToolbarEnabled(false);
        getGoogleMap().setMyLocationEnabled(true);
    }

    private boolean hasImage(Accomodation accomodation) {
        return accomodation.hasImage();
    }

    private Context getContext() {
        return accomodationDetailMapsActivity.getApplicationContext();
    }

    private Resources getResources() {
        return accomodationDetailMapsActivity.getResources();
    }

    private String getString(int string) {
        return getResources().getString(string);
    }

    private GoogleMap getGoogleMap() {
        return accomodationDetailMapsActivity.getGoogleMap();
    }

    private RelativeLayout getRelativeLayoutDetails() {
        return accomodationDetailMapsActivity.getRelativeLayoutDetails();
    }

    private FloatingActionButton getFloatingActionButtonGoBack() {
        return accomodationDetailMapsActivity.getFloatingActionButtonGoBack();
    }

    private ImageView getImageViewAccomodation() {
        return accomodationDetailMapsActivity.getImageViewAccomodation();
    }

    private TextView getTextViewName() {
        return accomodationDetailMapsActivity.getTextViewName();
    }

    private TextView getTextViewRating() {
        return accomodationDetailMapsActivity.getTextViewRating();
    }

    private TextView getTextViewAddress() {
        return accomodationDetailMapsActivity.getTextViewAddress();
    }

    private Intent getIntent() {
        return accomodationDetailMapsActivity.getIntent();
    }

    private String getAccomodationType() {
        return getIntent().getStringExtra(Constants.getAccomodationType());
    }

    private Accomodation getAccomodation() {
        return (Accomodation) getIntent().getSerializableExtra(Constants.getAccomodation());
    }

    private String getAccomodationId() {
        return getAccomodation().getId();
    }

}
