package com.quiriletelese.troppadvisorproject.controllers;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
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
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.adapters.ViewPagerOverViewActivityAdapter;
import com.quiriletelese.troppadvisorproject.dao_interfaces.AttractionDAO;
import com.quiriletelese.troppadvisorproject.dao_interfaces.UserDAO;
import com.quiriletelese.troppadvisorproject.factories.DAOFactory;
import com.quiriletelese.troppadvisorproject.model_helpers.Constants;
import com.quiriletelese.troppadvisorproject.models.Attraction;
import com.quiriletelese.troppadvisorproject.models.Review;
import com.quiriletelese.troppadvisorproject.models.User;
import com.quiriletelese.troppadvisorproject.utils.ConfigFileReader;
import com.quiriletelese.troppadvisorproject.utils.UserSharedPreferences;
import com.quiriletelese.troppadvisorproject.views.AccomodationDetailMapsActivity;
import com.quiriletelese.troppadvisorproject.views.AttractionDetailActivity;
import com.quiriletelese.troppadvisorproject.views.LoginActivity;
import com.quiriletelese.troppadvisorproject.views.SeeReviewsActivity;
import com.quiriletelese.troppadvisorproject.views.WriteReviewActivity;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBack;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * @author Alessandro Quirile, Mauro Telese
 */

public class AttractionDetailActivityController implements View.OnClickListener, GoogleMap.OnMapClickListener,
        ViewPager.OnPageChangeListener {

    private final AttractionDetailActivity attractionDetailActivity;
    private final DAOFactory daoFactory = DAOFactory.getInstance();
    public Attraction attraction;
    private AlertDialog alertDialogLoadingInProgress, alertDialogWaitShowQRCode, alertDialogVisitedAttractions;
    private String day = "";

    public AttractionDetailActivityController(AttractionDetailActivity attractionDetailActivity) {
        this.attractionDetailActivity = attractionDetailActivity;
    }

    @Override
    public void onClick(View view) {
        onClickHelper(view);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        startAccomodationDetailMapsActivity();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        setTextViewImagePositionText();
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void findByIdHelper(VolleyCallBack volleyCallBack, String id) {
        getAttractionDAO().findById(volleyCallBack, id, getContext());
    }

    private void updateWalletHelper(VolleyCallBack volleyCallBack) {
        getUserDAO().updateWallet(volleyCallBack, getEmail(), attraction.getFreeAccessPrice(), getContext());
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

    public void updateWallet() {
        updateWalletHelper(new VolleyCallBack() {
            @Override
            public void onSuccess(Object object) {
                updateUserWalletVolleyCallBackOnSuccess(object);
            }

            @Override
            public void onError(String errorCode) {
                updateUserWalletVolleyCallBackOnError(errorCode);
            }
        });
    }

    private void setTextViewImagePositionText() {
        getTextViewImagePosition().setText(crateTextViewImagePositionString());
    }

    private String crateTextViewImagePositionString() {
        return getViewPager().getCurrentItem() + 1 + "/" + getViewPager().getAdapter().getCount();
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
                startAccomodationDetailMapsActivity();
                break;
            case R.id.text_view_attraction_free_access:
                handleGetFreeAccess();
                break;
            case R.id.floating_action_button_attraction_write_review:
                startWriteReviewActivity();
                break;
            case R.id.text_view_attraction_website:
                startWebSite(attraction.getWebSite());
                break;
        }
    }

    public void setListenerOnViewComponents() {
        getTextViewPhoneNumber().setOnClickListener(this);
        getTextViewCertificateOfExcellence().setOnClickListener(this);
        getTextViewAddress().setOnClickListener(this);
        getTextViewFreeAccess().setOnClickListener(this);
        getFloatingActionButtonWriteReview().setOnClickListener(this);
        getTextViewAttractionWebsite().setOnClickListener(this);
        getViewPager().addOnPageChangeListener(this);
    }

    public void initializeActivityFields() {
        setAttractionName();
        setAvaragePrice(attraction.getPrice());
        setAvarageRating();
        setCertificateOfExcellence(isHasCertificateOfExcellence());
        setAddress();
        setFreeAccess();
        setOpeningDays(getOpeningDays());
        setPhoneNumber(getPhoneNumber());
    }

    private void startAccomodationDetailMapsActivity() {
        getContext().startActivity(createAccomodationDetailMapsIntent());
    }

    private void handleGetFreeAccess() {
        Long userWallet = getUserWallet();
        if (hasLogged()) {
            if (attraction.getFreeAccessPrice() != 0)
                if (userWallet != null && userWallet != 0) {
                    if (userWallet < attraction.getFreeAccessPrice())
                        showToastOnUiThread(R.string.not_enough_coin);
                    else {
                        showQRCode();
                        createQRCode(alertDialogWaitShowQRCode);
                    }
                }
        } else
            showLoginDialog();
        System.out.println("WALLET = " + userWallet);
    }

    public void showLoginDialog() {
        AlertDialog.Builder alertDialogBuilder = createAlertDialogBuilder();
        alertDialogBuilder.setTitle(R.string.do_login);
        alertDialogBuilder.setMessage(R.string.do_login_for_vote);
        alertDialogBuilder.setPositiveButton(R.string.do_login, (dialogInterface, i) -> {
            startLoginActivity();
        });
        alertDialogBuilder.setNegativeButton(R.string.cancel, null);
        alertDialogBuilder.setCancelable(false);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void startLoginActivity() {
        getContext().startActivity(createStartLoginActivityIntent());
    }

    private Intent createStartLoginActivityIntent() {
        Intent intentLoginActitivy = new Intent(getContext(), LoginActivity.class);
        intentLoginActitivy.addFlags(FLAG_ACTIVITY_NEW_TASK);
        return intentLoginActitivy;
    }

    private void showQRCode() {
        AlertDialog.Builder alertDialogBuilder = createAlertDialogBuilder();
        alertDialogBuilder.setView(getLayoutInflater().inflate(getAlertDialogLayout(), null));
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("Riscattato", (dialogInterface, i) -> {
            updateWallet();
        });
        alertDialogBuilder.setNegativeButton(getString(R.string.cancel), null);
        alertDialogWaitShowQRCode = alertDialogBuilder.create();
        alertDialogWaitShowQRCode.show();
    }

    private void createQRCode(AlertDialog alertDialog) {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        ImageView imageView = alertDialog.findViewById(R.id.image_view_qr_code);
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(getEmail(), BarcodeFormat.QR_CODE, 500, 500);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            imageView.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showVisitedAttractionsBadge() {
        AlertDialog.Builder alertDialogBuilder = createAlertDialogBuilder();
        alertDialogBuilder.setView(getLayoutInflater().inflate(getAlertDialogUpdateWalletLayout(), null));
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("ok", (dialogInterface, i) -> {
            alertDialogVisitedAttractions.dismiss();
        });
        alertDialogBuilder.setNegativeButton(getString(R.string.cancel), null);
        alertDialogVisitedAttractions = alertDialogBuilder.create();
        alertDialogVisitedAttractions.show();
    }

    private void setVisitedDialogField(int body) {
        TextView textView = alertDialogVisitedAttractions.findViewById(R.id.text_view_body);
        textView.setText(getString(body));
    }

    @NotNull
    @Contract(" -> new")
    private AlertDialog.Builder createAlertDialogBuilder() {
        return new AlertDialog.Builder(attractionDetailActivity);
    }

    @NotNull
    private LayoutInflater getLayoutInflater() {
        return attractionDetailActivity.getLayoutInflater();
    }

    private int getAlertDialogLayout() {
        return R.layout.dialog_get_free_access_layout;
    }

    private int getAlertDialogUpdateWalletLayout() {
        return R.layout.dialog_visited_badge_layout;
    }

    private Long getUserWallet() {
        return new UserSharedPreferences(getContext()).getLongSharedPreferences(Constants.getWallet());
    }

    private Intent createAccomodationDetailMapsIntent() {
        Intent accomodationDetailMapsIntent = new Intent(getContext(), AccomodationDetailMapsActivity.class);
        accomodationDetailMapsIntent.putExtra(Constants.getAccomodation(), attraction);
        accomodationDetailMapsIntent.putExtra(Constants.getAccomodationType(), Constants.getAttraction());
        accomodationDetailMapsIntent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        return accomodationDetailMapsIntent;
    }

    private void startWebSite(String website) {
        Intent intentWebsite = new Intent(Intent.ACTION_VIEW, Uri.parse(website));
        intentWebsite.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(intentWebsite);
    }

    private void initializeViewPager() {
        getViewPager().setAdapter(createViewPagerOverViewActivityAdapter());
    }

    private ViewPagerOverViewActivityAdapter createViewPagerOverViewActivityAdapter() {
        return new ViewPagerOverViewActivityAdapter(getImages(), getContext());
    }

    private void volleyCallBackOnSuccess(Object object) {
        attraction = (Attraction) object;
        handleOnMapReady(getMap());
        initializeActivityFields();
        initializeViewPager();
        setTextViewImagePositionText();
        dismissLoadingInProgressDialog();
        setReviewsPreview();
        if (!checkTapTargetBooleanPreferences())
            setTapTargetSequence();
    }

    private void updateUserWalletVolleyCallBackOnSuccess(Object object) {
        alertDialogWaitShowQRCode.dismiss();
        User user = (User) object;
        if (user.getTotalAttractionsVisited() == 1) {
            showVisitedAttractionsBadge();
            setVisitedDialogField(R.string.one_attractions_visited);
        } else if (user.getTotalAttractionsVisited() == 5) {
            showVisitedAttractionsBadge();
            setVisitedDialogField(R.string.five_attractions_visited);
        } else if (user.getTotalAttractionsVisited() == 10) {
            showVisitedAttractionsBadge();
            setVisitedDialogField(R.string.ten_attractions_visited);
        }
        writeUserWalletSharedPreferences();
    }

    private void volleyCallBackOnError(String errorCode) {
        detectVolleyError(errorCode);
    }

    private void updateUserWalletVolleyCallBackOnError(String errorCode) {
        Toast.makeText(attractionDetailActivity, "Errore imprevisto", Toast.LENGTH_SHORT).show();
    }

    private void detectVolleyError(String errorCode) {
        switch (errorCode) {
            case "204":
                showToastOnUiThread(R.string.no_content_error_attraction_detail);
                break;
        }
    }

    private void writeUserWalletSharedPreferences() {
        UserSharedPreferences userSharedPreferences = new UserSharedPreferences(getContext());
        Long userWallet = userSharedPreferences.getLongSharedPreferences(Constants.getWallet());
        userWallet -= attraction.getFreeAccessPrice();
        userSharedPreferences.putLongSharedPreferences(Constants.getWallet(), userWallet);

    }

    private void setAttractionName() {
        getTextViewAttractionName().setText(getName());
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
    }

    private void setAddress() {
        getTextViewAddress().setText(createAddressString());
    }

    private void setFreeAccess() {
        if (attraction.getFreeAccessPrice() != 0) {
            getTextViewFreeAccess().setText(Html.fromHtml(getString(R.string.get_free_acess).concat(" (<b>")
                    .concat(String.valueOf(attraction.getFreeAccessPrice())).concat(" gettoni</b>)")));
            getTextViewFreeAccess().setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_account_wallet_green, 0, R.drawable.icon_arrow_forward_black, 0);
        } else {
            getTextViewFreeAccess().setText(getString(R.string.free_acces_not_allowed));
            getTextViewFreeAccess().setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_not_allowed_green, 0, 0, 0);
        }
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

    private void setOpeningDays(Map<Integer, String> openingDays) {
        if (openingDays != null) {
            for (Map.Entry<Integer, String> entry : openingDays.entrySet())
                createOpeningDaysString(entry);
            day = day.substring(0, day.length() - 1);
            getTextViewOpeningTime().setText(Html.fromHtml(day));
        } else getTextViewOpeningTime().setText(getString(R.string.no_information_available));
    }

    private void createOpeningDaysString(Map.Entry<Integer, String> entry) {
        if (!entry.getValue().equals("")) {
            if (isSameDay(entry.getKey()))
                day = day.concat("<b>" + detectDay(entry.getKey()) + " " + entry.getValue() + "</b>");
            else
                day = day.concat(detectDay(entry.getKey()) + " " + entry.getValue());
        } else {
            if (isSameDay(entry.getKey()))
                day = day.concat("<b>" + detectDay(entry.getKey()) + " " + getString(R.string.closed) + "</b>");
            else
                day = day.concat(detectDay(entry.getKey()) + " " + getString(R.string.closed));
        }
        day = day.concat("<br>");
    }

    private boolean isSameDay(Integer day) {
        Calendar calendar = Calendar.getInstance();
        System.out.println("KEY = " + day + "\nDAY = " + (calendar.get(Calendar.DAY_OF_WEEK) - 1));
        if (day == 7 && (calendar.get(Calendar.DAY_OF_WEEK) - 1) == 0)
            return true;
        else
            return day == (calendar.get(Calendar.DAY_OF_WEEK) - 1);
    }

    private String detectDay(Integer day) {
        String dayOfWeek = "";
        switch (day) {
            case 1:
                dayOfWeek = getString(R.string.monday);
                break;
            case 2:
                dayOfWeek = getString(R.string.tuesday);
                break;
            case 3:
                dayOfWeek = getString(R.string.wednesday);
                break;
            case 4:
                dayOfWeek = getString(R.string.thursday);
                break;
            case 5:
                dayOfWeek = getString(R.string.friday);
                break;
            case 6:
                dayOfWeek = getString(R.string.saturday);
                break;
            case 7:
                dayOfWeek = getString(R.string.sunday);
                break;
        }
        return dayOfWeek;
    }

    private void setPhoneNumber(String phoneNumber) {
        if (!phoneNumber.equals(""))
            getTextViewPhoneNumber().setText(phoneNumber);
        else
            getTextViewPhoneNumber().setText(getString(R.string.no_phone_number));
    }

    private void setAvaragePrice(Map<String, Double> price) {
        if (price != null) {
            String priceString = createPriceString(price);
            priceString = priceString.substring(0, (priceString.length() - 1));
            getTextViewAvaragePrice().setText((Html.fromHtml(priceString)));
        } else
            getTextViewAvaragePrice().setText(getString(R.string.gratis));
    }

    private String createPriceString(Map<String, Double> price) {
        String priceString = "";
        for (Map.Entry<String, Double> entry : price.entrySet()) {
            if (entry.getValue() != 0)
                priceString = priceString.concat("- ").concat("<b>" + entry.getKey().concat(":</b>  ")
                        .concat(String.valueOf(entry.getValue())).concat(getString(R.string.currency)));
            else
                priceString = priceString.concat("- ").concat("<b>" + entry.getKey().concat(":</b>  ")
                        .concat(getString(R.string.gratis)));
            priceString = priceString.concat("<br>");
        }
        return priceString;
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
        textView.setPadding(15, 20, 0, 0);
        textView.setTextSize(16);
        textView.setOnClickListener(view -> {
            readReviews();
        });
        getLinearLayoutCompatReviewsPreview().addView(textView);
    }

    private TextView createTextView(Review review) {
        TextView textView = new TextView(getContext());
        textView.setMaxLines(4);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setText(review.getDescription());
        textView.setTextColor(getColor(R.color.black));
        textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_person_green, 0, 0, 0);
        textView.setCompoundDrawablePadding(20);
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

    public void startWriteReviewActivity() {
        if (hasLogged())
            attractionDetailActivity.startActivityForResult(createWriteReviewActivityIntent(), Constants.getLaunchWriteReviewActivity());
        else
            showLoginDialog();
    }

    public void handleOnActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d("REQ - RES", requestCode + " - " + resultCode);
        if (resultCode == RESULT_OK)
            if (requestCode == Constants.getLaunchWriteReviewActivity())
                findById();
//            else if(requestCode == Constants.getLaunchLoginActivity())
//                userWallet = getUserWallet();
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
        getMap().setOnMapClickListener(this);
        addMarker(googleMap);
        setMapZoon(googleMap);
    }

    @SuppressLint("MissingPermission")
    public void setMapProperties() {
        getMap().getUiSettings().setMyLocationButtonEnabled(false);
        getMap().getUiSettings().setMapToolbarEnabled(false);
        getMap().setMyLocationEnabled(true);
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

    private TextView getTextViewOpeningTime() {
        return attractionDetailActivity.getTextViewOpeningTime();
    }

    private TextView getTextViewPhoneNumber() {
        return attractionDetailActivity.getTextViewPhoneNumber();
    }

    private TextView getTextViewAddress() {
        return attractionDetailActivity.getTextViewAddress();
    }

    public TextView getTextViewFreeAccess() {
        return attractionDetailActivity.getTextViewFreeAccess();
    }

    private TextView getTextViewCertificateOfExcellence() {
        return attractionDetailActivity.getTextViewCertificateOfExcellence();
    }

    public RatingBar getRatingBarActivityDetail() {
        return attractionDetailActivity.getRatingBarActivityDetail();
    }

    public TextView getTextViewAttractionName() {
        return attractionDetailActivity.getTextViewAttractionName();
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

    public TextView getTextViewAttractionWebsite() {
        return attractionDetailActivity.getTextViewAttractionWebsite();
    }

    public Toolbar getToolbar() {
        return attractionDetailActivity.getToolbar();
    }

    public TextView getTextViewImagePosition() {
        return attractionDetailActivity.getTextViewImagePosition();
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

    private String getEmail() {
        return new UserSharedPreferences(getContext()).getStringSharedPreferences(Constants.getEmail());
    }

    private String getName() {
        return attraction.getName();
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

    private Map<Integer, String> getOpeningDays() {
        return attraction.getOpeningDays();
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

    private UserDAO getUserDAO() {
        return daoFactory.getUserDAO(getStorageTechnology(Constants.getUserStorageTechnology()));
    }

    private String getStorageTechnology(String storageTechnology) {
        return ConfigFileReader.getProperty(storageTechnology, getContext());
    }

    public GoogleMap getMap() {
        return attractionDetailActivity.getMap();
    }

    private boolean hasLogged() {
        String email = new UserSharedPreferences(getContext()).getStringSharedPreferences(Constants.getEmail());
        return email != null && !email.equals("");
    }

    private boolean checkTapTargetBooleanPreferences() {
        return new UserSharedPreferences(getContext()).constains(Constants.getTapTargetAttractionsDetail());
    }

    private void writeTapTargetBooleanPreferences() {
        new UserSharedPreferences(getContext()).putBooleanSharedPreferences(Constants.getTapTargetAttractionsDetail(), true);
    }

    public void setTapTargetSequence() {
        new TapTargetSequence(attractionDetailActivity).targets(
//                createTapTarget(getTextViewImagePosition(), getString(R.string.view_pager_tap_title),
//                        getString(R.string.view_pager_tap_description), true, 50),
                createTapTarget(getFloatingActionButtonWriteReview(), getString(R.string.write_review_tap_title),
                        getString(R.string.write_review_tap_description), false, 70),
                createTapTarget(getRatingBarActivityDetail(), getString(R.string.avarage_rating),
                        getString(R.string.avarage_attraction_rating_tap_description), true, 70))
//                createTapTarget(getTextViewAvarageRating(), getString(R.string.total_reviews_tap_title),
//                        getString(R.string.total_reviews_tap_description), true, 70))
                .listener(new TapTargetSequence.Listener() {
                    // This listener will tell us when interesting(tm) events happen in regards
                    // to the sequence
                    @Override
                    public void onSequenceFinish() {
                        writeTapTargetBooleanPreferences();
                    }

                    @Override
                    public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {
                        // Perform action for the current target
                    }

                    @Override
                    public void onSequenceCanceled(TapTarget lastTarget) {
                        // Boo
                    }
                }).start();

    }

    private TapTarget createTapTarget(View view, String title, String body, boolean tintTarget, int radius) {
        return TapTarget.forView(view, title, body)
                .dimColor(android.R.color.black)
                .outerCircleColor(R.color.colorPrimary)
                .textColor(android.R.color.white)
                .targetCircleColor(R.color.white)
                .drawShadow(true)
                .cancelable(false)
                .tintTarget(tintTarget)
                .targetRadius(radius);
    }

}
