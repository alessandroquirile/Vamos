package com.quiriletelese.troppadvisorproject.controllers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.SwitchCompat;

import com.amazonaws.services.cognitoidentityprovider.model.InitiateAuthResult;
import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.dao_interfaces.AccountDAO;
import com.quiriletelese.troppadvisorproject.dao_interfaces.ReviewDAO;
import com.quiriletelese.troppadvisorproject.factories.DAOFactory;
import com.quiriletelese.troppadvisorproject.models.Review;
import com.quiriletelese.troppadvisorproject.util_interfaces.Constants;
import com.quiriletelese.troppadvisorproject.utils.ConfigFileReader;
import com.quiriletelese.troppadvisorproject.utils.UserSharedPreferences;
import com.quiriletelese.troppadvisorproject.views.LoginActivity;
import com.quiriletelese.troppadvisorproject.views.WriteReviewActivity;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBack;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * @author Alessandro Quirile, Mauro Telese
 */

public class WriteReviewActivityController implements View.OnClickListener, RatingBar.OnRatingBarChangeListener,
        SwitchCompat.OnCheckedChangeListener, TextWatcher, Constants {

    private final WriteReviewActivity writeReviewActivity;
    private final DAOFactory daoFactory = DAOFactory.getInstance();
    private String title = "";
    private String description = "";
    private String user;
    private String accomodationId;
    private Double rating;
    private AlertDialog alertDialogWaitWhileInsertReview;
    private boolean isAnonymoys = false;

    public WriteReviewActivityController(WriteReviewActivity writeReviewActivity) {
        this.writeReviewActivity = writeReviewActivity;
    }

    @Override
    public void onClick(@NotNull View view) {
        onClickHelper(view);
    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        switch ((int) rating) {
            case 0:
                getTextViewRating().setText("");
                ratingBar.setRating(1);
                break;
            case 1:
                getTextViewRating().setText(writeReviewActivity.getApplicationContext().getResources().getString(R.string.pessimo));
                break;
            case 2:
                getTextViewRating().setText(writeReviewActivity.getApplicationContext().getResources().getString(R.string.scarso));
                break;
            case 3:
                getTextViewRating().setText(writeReviewActivity.getApplicationContext().getResources().getString(R.string.nella_media));
                break;
            case 4:
                getTextViewRating().setText(writeReviewActivity.getApplicationContext().getResources().getString(R.string.molto_buono));
                break;
            case 5:
                getTextViewRating().setText(writeReviewActivity.getApplicationContext().getResources().getString(R.string.eccellente));
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        isAnonymoys = isChecked;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        detectEditText(charSequence);
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    private void insertHotelReviewHelper(VolleyCallBack volleyCallBack) {
        getReviewDAO().insertHotelReview(volleyCallBack, createReviewForInsert(), getIdToken(), getContext());
    }

    private void insertRestaurantReviewHelper(VolleyCallBack volleyCallBack) {
        getReviewDAO().insertRestaurantReview(volleyCallBack, createReviewForInsert(), getIdToken(), getContext());
    }

    private void insertAttractionReviewHelper(VolleyCallBack volleyCallBack) {
        getReviewDAO().insertAttractionReview(volleyCallBack, createReviewForInsert(), getIdToken(), getContext());
    }

    private void refreshTokenHelper(VolleyCallBack volleyCallBack) {
        getAccountDAO().refreshToken(volleyCallBack, getResfreshToken(), getContext());
    }

    private void insertHotelReview() {
        insertHotelReviewHelper(new VolleyCallBack() {
            @Override
            public void onSuccess(Object object) {
                volleyCallbackOnSuccess();
            }

            @Override
            public void onError(String errorCode) {
                volleyCallbackOnError(errorCode);
            }
        });
    }

    private void insertRestaurantReview() {
        insertRestaurantReviewHelper(new VolleyCallBack() {
            @Override
            public void onSuccess(Object object) {
                volleyCallbackOnSuccess();
            }

            @Override
            public void onError(String errorCode) {
                volleyCallbackOnError(errorCode);
            }
        });
    }

    private void insertAttractionReview() {
        insertAttractionReviewHelper(new VolleyCallBack() {
            @Override
            public void onSuccess(Object object) {
                volleyCallbackOnSuccess();
            }

            @Override
            public void onError(String errorCode) {
                volleyCallbackOnError(errorCode);
            }
        });
    }

    private void refreshToken() {
        refreshTokenHelper(new VolleyCallBack() {
            @Override
            public void onSuccess(Object object) {
                volleyCallbackOnSuccessRefreshToken((InitiateAuthResult) object);
            }

            @Override
            public void onError(String errorCode) {
                volleyCallbackOnError(errorCode);
            }
        });
    }

    private void volleyCallbackOnSuccessRefreshToken(InitiateAuthResult initiateAuthResult) {
        writeSharedPreferences(initiateAuthResult);
        insertReviewByAccomodationType();
    }

    private void volleyCallbackOnSuccess() {
        dismissWaitWhileInsertReviewDialog();
        showToastOnUiThred(R.string.review_successfully_submitted);
        finish();
    }

    private void volleyCallbackOnError(@NotNull String errorCode) {
        dismissWaitWhileInsertReviewDialog();
        Log.d("REVIEW ERROR CODE", errorCode);
        switch (errorCode) {
            case UNAUTHORIZED:
                handle401VolleyError();
                break;
            case INTERNAL_ERROR_SERVER:
                handleOtherVolleyError();
                break;
        }
    }

    private void writeSharedPreferences(InitiateAuthResult initiateAuthResult) {
        UserSharedPreferences userSharedPreferences = new UserSharedPreferences(getContext());
        userSharedPreferences.putStringSharedPreferences(ACCESS_TOKEN, getAccessToken(initiateAuthResult));
        userSharedPreferences.putStringSharedPreferences(ID_TOKEN, getIdToken(initiateAuthResult));
        userSharedPreferences.putStringSharedPreferences(REFRESH_TOKEN, getRefreshToken(initiateAuthResult));
    }

    private void handle401VolleyError() {
        refreshToken();
    }

    private void handleOtherVolleyError() {
        showToastOnUiThred(R.string.unexpected_error_while_entering_the_review);
    }

    private void showToastOnUiThred(int string) {
        writeReviewActivity.runOnUiThread(() ->
                Toast.makeText(writeReviewActivity, getString(string), Toast.LENGTH_SHORT).show());
    }

    private void detectEditText(CharSequence charSequence) {
        if (isEditTextTitleChanged(charSequence))
            editTextTitleOnTextChanged(charSequence);
        else editTextDescriptionOnTextChanged(charSequence);
    }

    private void editTextTitleOnTextChanged(@NotNull CharSequence charSequence) {
        title = charSequence.toString();
        if (reviewFieldsAreCorrectlyFilled())
            enableButtonPublishReview();
        else
            disableButtonPublishReview();
    }

    private void editTextDescriptionOnTextChanged(@NotNull CharSequence charSequence) {
        description = charSequence.toString();
        if (reviewFieldsAreCorrectlyFilled())
            enableButtonPublishReview();
        else
            disableButtonPublishReview();
    }

    private void insertReviewByAccomodationType() {
        showWaitWhileInsertReviewDialog();
        switch (getAccomodationType()) {
            case HOTEL:
                insertHotelReview();
                break;
            case RESTAURANT:
                insertRestaurantReview();
                break;
            case ATTRACTION:
                insertAttractionReview();
                break;
        }
    }

    private void getReviewInformations() {
        title = getTextInputLayoutReviewTitleValue();
        description = getTextInputLayoutReviewDescriptionValue();
        rating = getRatingBarValue();
        user = getUserName();
        accomodationId = getAccomodationId();
    }

    @NotNull
    private Review createReviewForInsert() {
        getReviewInformations();
        Review review = new Review();
        review.setTitle(title);
        review.setDescription(description);
        review.setRating(rating);
        review.setUser(user);
        review.setAnonymous(isAnonymoys);
        review.setAccomodationId(accomodationId);
        return review;
    }

    private void showWaitWhileInsertReviewDialog() {
        AlertDialog.Builder alertDialogBuilder = createAlertDialogBuilder();
        LayoutInflater layoutInflater = getLayoutInflater();
        View dialogView = layoutInflater.inflate(getAlertDialogLayout(), null);
        alertDialogBuilder.setView(dialogView);
        alertDialogWaitWhileInsertReview = alertDialogBuilder.create();
        alertDialogWaitWhileInsertReview.show();
    }

    @NotNull
    @Contract(" -> new")
    private AlertDialog.Builder createAlertDialogBuilder() {
        return new AlertDialog.Builder(writeReviewActivity);
    }

    private void dismissWaitWhileInsertReviewDialog() {
        alertDialogWaitWhileInsertReview.dismiss();
    }

    public void checkLogin() {
        if (!isLogged()) {
            startLoginActivity();
            finish();
        }
    }

    private void startLoginActivity() {
        writeReviewActivity.startActivity(createLoginActivityIntent());
    }

    @NotNull
    @Contract(" -> new")
    private Intent createLoginActivityIntent() {
        Intent intentLoginActivity = new Intent(getContext(), LoginActivity.class);
        intentLoginActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return new Intent(getContext(), LoginActivity.class);
    }

    private void finish() {
        writeReviewActivity.finish();
    }

    private void onClickHelper(View view) {
        switch (view.getId()) {
            case R.id.button_publish_review:
                insertReviewByAccomodationType();
                break;
        }
    }

    public void setListenersOnViewComponents() {
        getButtonPublishReview().setOnClickListener(this);
        getRatingBar().setOnRatingBarChangeListener(this);
        getSwitchCompatButtonPublishAnonymously().setOnCheckedChangeListener(this);
        getTextInputLayoutReviewTitleEditText().addTextChangedListener(this);
        getTextInputLayoutReviewDescriptionEditText().addTextChangedListener(this);
    }

    private void enableButtonPublishReview() {
        getButtonPublishReview().setBackgroundResource(R.drawable.background_write_review_button_enabled);
        getButtonPublishReview().setEnabled(true);
    }

    private void disableButtonPublishReview() {
        getButtonPublishReview().setBackgroundResource(R.drawable.background_write_review_button_disabled);
        getButtonPublishReview().setEnabled(false);
    }

    public void setToolbarSubtitle() {
        getSupportActionBar().setSubtitle(getAccomodationName());
    }

    private ActionBar getSupportActionBar() {
        return writeReviewActivity.getSupportActionBar();
    }

    private ReviewDAO getReviewDAO() {
        return daoFactory.getReviewDAO(getStorageTechnology(REVIEW_STORAGE_TECHNOLOGY));
    }

    private AccountDAO getAccountDAO() {
        return daoFactory.getAccountDAO(getStorageTechnology(ACCOUNT_STORAGE_TECHNOLOGY));
    }

    private String getStorageTechnology(String storageTechnology) {
        return ConfigFileReader.getProperty(storageTechnology, getContext());
    }

    private Intent getIntent() {
        return writeReviewActivity.getIntent();
    }

    private String getAccomodationId() {
        return getIntent().getStringExtra(ID);
    }

    private String getAccomodationName() {
        return getIntent().getStringExtra(ACCOMODATION_NAME);
    }

    private String getAccomodationType() {
        return getIntent().getStringExtra(ACCOMODATION_TYPE);
    }

    private String getUserName() {
        return createUserSharedPreferences().getStringSharedPreferences(USERNAME);
    }

    private boolean isEditTextTitleChanged(@NotNull CharSequence charSequence) {
        return charSequence.hashCode() == getReviewTitleEditText().getText().hashCode();
    }

    private EditText getReviewTitleEditText() {
        return getTextInputLayoutReviewTitleEditText();
    }

    private boolean isLogged() {
        return !getAccessToken().equals("");
    }

    @NotNull
    @Contract(" -> new")
    private UserSharedPreferences createUserSharedPreferences() {
        return new UserSharedPreferences(getContext());
    }

    private String getAccessToken() {
        return createUserSharedPreferences().getStringSharedPreferences(ACCESS_TOKEN);
    }

    private String getAccessToken(@NotNull InitiateAuthResult initiateAuthResult) {
        return initiateAuthResult.getAuthenticationResult().getAccessToken();
    }

    private String getIdToken(@NotNull InitiateAuthResult initiateAuthResult) {
        return initiateAuthResult.getAuthenticationResult().getIdToken();
    }

    private String getIdToken() {
        return createUserSharedPreferences().getStringSharedPreferences(ID_TOKEN);
    }

    private String getRefreshToken(@NotNull InitiateAuthResult initiateAuthResult) {
        return initiateAuthResult.getAuthenticationResult().getRefreshToken();
    }

    private String getResfreshToken() {
        return createUserSharedPreferences().getStringSharedPreferences(REFRESH_TOKEN);
    }

    @NotNull
    private LayoutInflater getLayoutInflater() {
        return writeReviewActivity.getLayoutInflater();
    }

    private int getAlertDialogLayout() {
        return R.layout.dialog_wait_while_insert_review_layout;
    }

    private Context getContext() {
        return writeReviewActivity.getApplicationContext();
    }

    private Resources getResources() {
        return writeReviewActivity.getResources();
    }

    @NotNull
    private String getString(int string) {
        return getResources().getString(string);
    }

    private TextView getTextViewRating() {
        return writeReviewActivity.getTextViewRating();
    }

    private Button getButtonPublishReview() {
        return writeReviewActivity.getButtonPublishReview();
    }

    private RatingBar getRatingBar() {
        return writeReviewActivity.getRatingBar();
    }

    private SwitchCompat getSwitchCompatButtonPublishAnonymously() {
        return writeReviewActivity.getSwitchCompatButtonPublishAnonymously();
    }

    private EditText getTextInputLayoutReviewTitleEditText() {
        return writeReviewActivity.getTextInputLayoutReviewTitleEditText();
    }

    private EditText getTextInputLayoutReviewDescriptionEditText() {
        return writeReviewActivity.getTextInputLayoutReviewDescriptionEditText();
    }

    @NotNull
    private String getTextInputLayoutReviewTitleValue() {
        return writeReviewActivity.getTextInputLayoutReviewTitleValue().trim();
    }

    @NotNull
    private String getTextInputLayoutReviewDescriptionValue() {
        return writeReviewActivity.getTextInputLayoutReviewDescriptionValue().trim();
    }

    private Double getRatingBarValue() {
        return writeReviewActivity.getRatingBarValue();
    }

    private boolean isTitleEmpty() {
        return title.isEmpty();
    }

    private boolean isTitleLongerThan50Characters() {
        return title.length() > 50;
    }

    private boolean isDescriptionEmpty() {
        return description.isEmpty();
    }

    private boolean isDescriptionLongerThan350Characters() {
        return description.length() > 350;
    }

    private boolean reviewFieldsAreCorrectlyFilled() {
        return !isTitleEmpty() && !isDescriptionEmpty() && !isTitleLongerThan50Characters() && !isDescriptionLongerThan350Characters();
    }

}