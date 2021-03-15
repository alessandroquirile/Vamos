package com.quiriletelese.troppadvisorproject.controllers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.adapters.ViewPagerOverViewActivityAdapter;
import com.quiriletelese.troppadvisorproject.dao_interfaces.AttractionDAO;
import com.quiriletelese.troppadvisorproject.factories.DAOFactory;
import com.quiriletelese.troppadvisorproject.model_helpers.Constants;
import com.quiriletelese.troppadvisorproject.models.Attraction;
import com.quiriletelese.troppadvisorproject.models.Review;
import com.quiriletelese.troppadvisorproject.utils.ConfigFileReader;
import com.quiriletelese.troppadvisorproject.views.AttractionDetailActivity;
import com.quiriletelese.troppadvisorproject.views.SeeReviewsActivity;
import com.quiriletelese.troppadvisorproject.views.WriteReviewActivity;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBack;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Alessandro Quirile, Mauro Telese
 */

public class AttractionDetailActivityController implements View.OnClickListener {

    private final AttractionDetailActivity attractionDetailActivity;
    private final DAOFactory daoFactory = DAOFactory.getInstance();
    private Attraction attraction;
    private AlertDialog alertDialogLoadingInProgress;

    public AttractionDetailActivityController(AttractionDetailActivity attractionDetailActivity) {
        this.attractionDetailActivity = attractionDetailActivity;
    }

    @Override
    public void onClick(View view) {
        onClickHelper(view);
    }

    private void findByIdHelper(VolleyCallBack volleyCallBack, String id) {
        getAttractionDAO().findById(volleyCallBack, id, getContext());
    }

    public void findById() {
        findByIdHelper(new VolleyCallBack() {
            @Override
            public void onSuccess(Object object) {
                volleyCallBackOnSuccess(object);
            }

            @Override
            public void onError(String errorCode) {
                volleyCallBackOnError(errorCode);
            }
        }, getId());
    }

    private void onClickHelper(View view) {
        switch (view.getId()) {
            case R.id.text_view_attraction_phone_number:
                startCallActivity();
                break;
            case R.id.text_view_attraction_certificate_of_excellence:
                showCertificateOfExcellenceDialog();
                break;
            case R.id.text_view_attraction_address:
                startMapsActivity();
                break;
            case R.id.floating_action_button_attraction_write_review:
                startWriteReviewActivity();
                break;
        }
    }

    public void setListenerOnViewComponents() {
        getTextViewPhoneNumber().setOnClickListener(this);
        getTextViewCertificateOfExcellence().setOnClickListener(this);
        getTextViewAddress().setOnClickListener(this);
        getFloatingActionButtonWriteReview().setOnClickListener(this);
    }

    public void initializeActivityFields() {
        setCollapsingToolbarLayoutTitle(getName());
        setAvaragePrice(getAvaragePrice());
        setAvarageRating();
        setCertificateOfExcellence(isHasCertificateOfExcellence());
        setAddress();
        setOpeningTime(getOpeningTime());
        setPhoneNumber(getPhoneNumber());

    }

    private void initializeViewPager() {
        getViewPager().setAdapter(createViewPagerOverViewActivityAdapter());
    }

    private ViewPagerOverViewActivityAdapter createViewPagerOverViewActivityAdapter() {
        return new ViewPagerOverViewActivityAdapter(getImages(), getContext());
    }

    private void volleyCallBackOnSuccess(Object object) {
        attraction = (Attraction) object;
        initializeActivityFields();
        initializeViewPager();
        dismissLoadingInProgressDialog();
        setReviewsPreview();
    }

    private void volleyCallBackOnError(String errorCode) {
        detectVolleyError(errorCode);
    }

    private void detectVolleyError(String errorCode) {
        switch (errorCode) {
            case "204":
                showToastOnUiThread(R.string.no_content_error_attraction_detail);
                break;
        }
    }

    private void setCollapsingToolbarLayoutTitle(String title) {
        getCollapsingToolbarLayout().setTitle(title);
    }

    private void setAvarageRating() {
        if (!hasAvarageRating())
            getTextViewAvarageRating().setText(R.string.no_reviews);
        else
            getTextViewAvarageRating().setText(createAvarageRatingString());
    }

    private String createAvarageRatingString() {
        String avarageRating = "";
        getRatingBarActivityDetail().setRating(attraction.getAvarageRating().floatValue());
        avarageRating = avarageRating.concat(attraction.getTotalReviews() + " ");
        if (attraction.getTotalReviews().intValue() >= 2)
            avarageRating = avarageRating.concat(getString(R.string.reviews));
        else if (attraction.getTotalReviews().intValue() == 1)
            avarageRating = avarageRating.concat(getString(R.string.review));
        return avarageRating;
    }

    private void setCertificateOfExcellence(boolean certificateOfExcellence) {
        if (!certificateOfExcellence)
            getLinearLayoutCompatCertificateOfExcellence().setVisibility(View.GONE);
        //getTextViewCertificateOfExcellence().setVisibility(View.GONE);
    }

    private void setAddress() {
        getTextViewAddress().setText(createAddressString());
    }

    private String createAddressString() {
        String restaurantAddress = "";
        restaurantAddress = restaurantAddress.concat(getTypeOfAddress() + " ");
        restaurantAddress = restaurantAddress.concat(getStreet() + ", ");
        if (!getHouseNumber().isEmpty())
            restaurantAddress = restaurantAddress.concat(getHouseNumber() + ", ");
        restaurantAddress = restaurantAddress.concat(getCity() + ", ");
        if (!getProvince().equals(getCity()))
            restaurantAddress = restaurantAddress.concat(getProvince() + ", ");
        restaurantAddress = restaurantAddress.concat(getPostalCode());
        return restaurantAddress;
    }

    private void setOpeningTime(String openingTime) {
        if (!openingTime.equals(""))
            getTextViewOpeningTime().setText(openingTime);
        else
            getTextViewOpeningTime().setText(getString(R.string.no_information_available));
    }

    private void setPhoneNumber(String phoneNumber) {
        if (!phoneNumber.equals(""))
            getTextViewPhoneNumber().setText(phoneNumber);
        else
            getTextViewPhoneNumber().setText(getString(R.string.no_phone_number));
    }

    private void setAvaragePrice(Double price) {
        if (!price.equals(0d))
            getTextViewAvaragePrice().setText(createAvaragePriceString(price));
        else
            getTextViewAvaragePrice().setText(getString(R.string.gratis));
    }

    private String createAvaragePriceString(Double price) {
        String avaragePrice = "";
        avaragePrice = avaragePrice.concat(getString(R.string.avarage_price) + " ");
        avaragePrice = avaragePrice.concat(price + " ");
        avaragePrice = avaragePrice.concat(getString(R.string.currency));
        return avaragePrice;
    }

    private void setReviewsPreview() {
        if (hasReviews()) {
            if (hasAtLeast3Reviews())
                createReviewPreviews(getFirst3Reviews());
            else
                createReviewPreviews(attraction.getReviews());
            generateTextViewReadReviews();
        }
    }

    private void createReviewPreviews(List<Review> reviews) {
        for (Review review : reviews)
            generateTextViews(review);
    }

    private void generateTextViews(Review review) {
        getLinearLayoutCompatReviewsPreview().setVisibility(View.VISIBLE);
        getLinearLayoutCompatReviewsPreview().addView(createTextView(review));
    }

    private void generateTextViewReadReviews() {
        TextView textView = new TextView(getContext());
        textView.setMaxLines(1);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setText(getString(R.string.read_all_reviews));
        textView.setTextColor(getColor(R.color.black));
        textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_arrow_forward_black, 0);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setPadding(0, 10, 0, 0);
        textView.setOnClickListener(view -> {
            readReviews();
        });
    }

    private TextView createTextView(Review review) {
        TextView textView = new TextView(getContext());
        textView.setMaxLines(4);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setText(review.getDescription());
        textView.setTextColor(getColor(R.color.black));
        textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_person_green, 0, 0, 0);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setPadding(0, 5, 0, 0);
        return textView;
    }

    private List<Review> getFirst3Reviews() {
        List<Review> first3Reviews = new ArrayList<>();
        for (int i = 0; i < 3; i++)
            first3Reviews.add(attraction.getReviews().get(i));
        return first3Reviews;
    }

    private void startCallActivity() {
        if (!getTextViewPhoneNumber().getText().toString().equals(getString(R.string.no_phone_number)))
            getContext().startActivity(createCallActivityIntent());
    }

    private void startMapsActivity() {
        getContext().startActivity(createMapsActivityIntent());
    }

    private void startWriteReviewActivity() {
        getContext().startActivity(createWriteReviewActivityIntent());
    }

    private void startSeeReviewsActivity() {
        getContext().startActivity(createSeeReviewsActivityIntent());
    }

    @NotNull
    private Intent createCallActivityIntent() {
        Intent callActivityIntent = new Intent(Intent.ACTION_DIAL);
        callActivityIntent.setData(Uri.parse("tel:" + getTextViewPhoneNumber().getText().toString()));
        callActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return callActivityIntent;
    }

    @NotNull
    private Intent createMapsActivityIntent() {
        String uri = "geo:0,0?q=" + createAddressString();
        Intent mapsActivityIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        mapsActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return mapsActivityIntent;
    }

    private Intent createWriteReviewActivityIntent() {
        Intent writeReviewActivityIntent = new Intent(getContext(), WriteReviewActivity.class);
        writeReviewActivityIntent.putExtra(Constants.getId(), getId());
        writeReviewActivityIntent.putExtra(Constants.getAccomodationType(), Constants.getAttraction());
        writeReviewActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return writeReviewActivityIntent;
    }

    private Intent createSeeReviewsActivityIntent() {
        Intent seeReviewsActivityIntent = new Intent(getContext(), SeeReviewsActivity.class);
        seeReviewsActivityIntent.putExtra(Constants.getAccomodationName(), getName());
        seeReviewsActivityIntent.putExtra(Constants.getId(), getId());
        seeReviewsActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return seeReviewsActivityIntent;
    }

    private void readReviews() {
        if (hasReviews())
            startSeeReviewsActivity();
        else
            showToastOnUiThread(R.string.no_reviews);
    }

    public void showLoadingInProgressDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(attractionDetailActivity);
        LayoutInflater layoutInflater = attractionDetailActivity.getLayoutInflater();
        View dialogView = layoutInflater.inflate(R.layout.dialog_loading_in_progress, null);
        alertDialogBuilder.setView(dialogView);
        alertDialogBuilder.setCancelable(false);
        alertDialogLoadingInProgress = alertDialogBuilder.create();
        alertDialogLoadingInProgress.show();
    }

    private void showCertificateOfExcellenceDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(attractionDetailActivity);
        alertDialogBuilder.setTitle(R.string.certificate_of_excellence);
        alertDialogBuilder.setMessage(R.string.certificate_of_excellence_dialog_message);
        alertDialogBuilder.setPositiveButton("ok", null);
        AlertDialog alertDialogCertificateOfExcellence = alertDialogBuilder.create();
        alertDialogCertificateOfExcellence.show();
    }

    public void handleOnMapReady(GoogleMap googleMap) {
        addMarker(googleMap);
        setMapZoon(googleMap);
    }

    private LatLng createLatLng() {
        return new LatLng(attraction.getLatitude(), attraction.getLongitude());
    }

    private Marker addMarker(GoogleMap googleMap) {
        return googleMap.addMarker(createMarkerOptions());
    }

    private void setMapZoon(GoogleMap googleMap) {
        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(createLatLng(), 15.0f);
        googleMap.animateCamera(yourLocation);
    }

    @NotNull
    private MarkerOptions createMarkerOptions() {
        return new MarkerOptions()
                .position(createLatLng())
                .icon(setCustomMarker(getContext(), getAttractionMarker()));
    }

    @NotNull
    private BitmapDescriptor setCustomMarker(Context context, int id) {
        Drawable background = ContextCompat.getDrawable(context, id);
        Objects.requireNonNull(background).setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private int getAttractionMarker() {
        return R.drawable.attraction_marker;
    }

    private void dismissLoadingInProgressDialog() {
        alertDialogLoadingInProgress.dismiss();
    }

    private void showToastOnUiThread(int stringId) {
        attractionDetailActivity.runOnUiThread(() ->
                Toast.makeText(attractionDetailActivity, getString(stringId), Toast.LENGTH_SHORT).show());
    }

    private CollapsingToolbarLayout getCollapsingToolbarLayout() {
        return attractionDetailActivity.getCollapsingToolbarLayout();
    }

    private TextView getTextViewOpeningTime() {
        return attractionDetailActivity.getTextViewOpeningTime();
    }

    private TextView getTextViewPhoneNumber() {
        return attractionDetailActivity.getTextViewPhoneNumber();
    }

    private TextView getTextViewAddress() {
        return attractionDetailActivity.getTextViewAddress();
    }

    private TextView getTextViewCertificateOfExcellence() {
        return attractionDetailActivity.getTextViewCertificateOfExcellence();
    }

    public RatingBar getRatingBarActivityDetail() {
        return attractionDetailActivity.getRatingBarActivityDetail();
    }

    private TextView getTextViewAvarageRating() {
        return attractionDetailActivity.getTextViewAvarageRating();
    }

    public LinearLayoutCompat getLinearLayoutCompatReviewsPreview() {
        return attractionDetailActivity.getLinearLayoutCompatReviewsPreview();
    }

    public LinearLayoutCompat getLinearLayoutCompatCertificateOfExcellence() {
        return attractionDetailActivity.getLinearLayoutCompatCertificateOfExcellence();
    }

    private TextView getTextViewAvaragePrice() {
        return attractionDetailActivity.getTextViewAvaragePrice();
    }

    private FloatingActionButton getFloatingActionButtonWriteReview() {
        return attractionDetailActivity.getFloatingActionButtonWriteReview();
    }

    private boolean hasAvarageRating() {
        return attraction.hasAvarageRating();
    }

    private boolean hasReviews() {
        return attraction.hasReviews();
    }

    private boolean hasAtLeast3Reviews() {
        return attraction.getReviews().size() >= 3;
    }

    private ViewPager getViewPager() {
        return attractionDetailActivity.getViewPager();
    }

    private List<String> getImages() {
        return attraction.getImages();
    }

    private Intent getIntent() {
        return attractionDetailActivity.getIntent();
    }

    private String getId() {
        return getIntent().getStringExtra(Constants.getId());
    }

    private String getName() {
        return attraction.getName();
    }

    private Double getAvaragePrice() {
        return attraction.getAvaragePrice();
    }

    private boolean isHasCertificateOfExcellence() {
        return attraction.isCertificateOfExcellence();
    }

    private String getTypeOfAddress() {
        return attraction.getTypeOfAddress();
    }

    private String getStreet() {
        return attraction.getStreet();
    }

    private String getHouseNumber() {
        return attraction.getHouseNumber();
    }

    private String getCity() {
        return attraction.getCity();
    }

    private String getProvince() {
        return attraction.getProvince();
    }

    private String getPostalCode() {
        return attraction.getPostalCode();
    }

    private String getOpeningTime() {
        return attraction.getOpeningTime();
    }

    private String getPhoneNumber() {
        return attraction.getPhoneNumber();
    }

    private Context getContext() {
        return attractionDetailActivity.getApplicationContext();
    }

    private Resources getResources() {
        return attractionDetailActivity.getResources();
    }

    @NotNull
    private String getString(int id) {
        return getResources().getString(id);
    }

    @NotNull
    private int getColor(int id) {
        return getResources().getColor(id);
    }

    private AttractionDAO getAttractionDAO() {
        return daoFactory.getAttractionDAO(getStorageTechnology(Constants.getAttractionStorageTechnology()));
    }

    private String getStorageTechnology(String storageTechnology) {
        return ConfigFileReader.getProperty(storageTechnology, getContext());
    }

}
