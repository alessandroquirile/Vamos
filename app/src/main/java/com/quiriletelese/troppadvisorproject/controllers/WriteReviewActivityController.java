package com.quiriletelese.troppadvisorproject.controllers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;

import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.dao_interfaces.AccountDAO;
import com.quiriletelese.troppadvisorproject.dao_interfaces.ReviewDAO;
import com.quiriletelese.troppadvisorproject.factories.DAOFactory;
import com.quiriletelese.troppadvisorproject.model_helpers.Constants;
import com.quiriletelese.troppadvisorproject.models.Review;
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

public class WriteReviewActivityController implements RatingBar.OnRatingBarChangeListener, TextWatcher {

    private final WriteReviewActivity writeReviewActivity;
    private final DAOFactory daoFactory = DAOFactory.getInstance();
    private String title = "";
    private String description = "";
    private String accomodationId;
    private Double rating;
    private AlertDialog alertDialogWaitWhileInsertingReview, alertDialogFirstReviewReward;
    private final UserSharedPreferences userSharedPreferences;
    private ReviewDAO reviewDAO;
    private AccountDAO accountDAO;
    private Review review;
    private boolean canReview = false;

    public WriteReviewActivityController(WriteReviewActivity writeReviewActivity) {
        this.writeReviewActivity = writeReviewActivity;
        userSharedPreferences = createUserSharedPreferences();
    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        if (rating == 0) {
            getTextViewRating().setText("");
            ratingBar.setRating(1);
        } else if (rating == 1) {
            getTextViewRating().setText(writeReviewActivity.getApplicationContext().getResources().getString(R.string.pessimo));
        } else if (rating == 2) {
            getTextViewRating().setText(writeReviewActivity.getApplicationContext().getResources().getString(R.string.scarso));
        } else if (rating == 3) {
            getTextViewRating().setText(writeReviewActivity.getApplicationContext().getResources().getString(R.string.nella_media));
        } else if (rating == 4) {
            getTextViewRating().setText(writeReviewActivity.getApplicationContext().getResources().getString(R.string.molto_buono));
        } else if (rating == 5) {
            getTextViewRating().setText(writeReviewActivity.getApplicationContext().getResources().getString(R.string.eccellente));
        }
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

    private void insertAttractionReviewHelper(VolleyCallBack volleyCallBack) {
        getReviewDAO().insertAttractionReview(volleyCallBack, createReviewForInserting(), getUserEmail(), getContext());
    }

    private void insertAttractionReview() {
        insertAttractionReviewHelper(new VolleyCallBack() {
            @Override
            public void onSuccess(Object object) {
                volleyCallbackOnSuccess(object);
            }

            @Override
            public void onError(String errorCode) {
                volleyCallbackOnError(errorCode);
            }
        });
    }

    private void volleyCallbackOnSuccess(Object object) {
        Review review = (Review) object;
        dismissWaitWhileInsertingReviewDialog();
        showToastOnUiThred(R.string.review_successfully_submitted);
        if (review.getUser().getTotalReviews() == 1)
            showFirstReviewRewardDialog();
        else
            finish(Activity.RESULT_OK);
    }

    private void volleyCallbackOnError(@NotNull String errorCode) {
        dismissWaitWhileInsertingReviewDialog();
        Log.e("REVIEW ERROR CODE", errorCode);
        switch (errorCode) {
            case "500":
                handleOtherVolleyError();
                break;
        }
    }

    public void showWarningDialog() {
        new AlertDialog.Builder(writeReviewActivity)
                .setTitle(getString(R.string.pay_attention))
                .setMessage(getString(R.string.pay_attention_body_review))
                .setPositiveButton("Sì", ((dialogInterface, i) -> {
                    finish(Activity.RESULT_CANCELED);
                }))
                .setNegativeButton("No", null)
                .setCancelable(false)
                .create()
                .show();
    }

    private void showFirstReviewRewardDialog() {
        AlertDialog.Builder alertDialogBuilder = createAlertDialogBuilder();
        alertDialogBuilder.setView(getLayoutInflater().inflate(getAlertDialogFirstReviewLayout(), null));
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("OK", (dialogInterface, i) -> {
            finish(Activity.RESULT_OK);
        });
        alertDialogFirstReviewReward = alertDialogBuilder.create();
        alertDialogFirstReviewReward.show();
    }

    private void handleOtherVolleyError() {
        showToastOnUiThred(R.string.unexpected_error_while_entering_the_review);
    }

    private void showToastOnUiThred(int stringId) {
        writeReviewActivity.runOnUiThread(() ->
                Toast.makeText(writeReviewActivity, getString(stringId), Toast.LENGTH_SHORT).show());
    }

    private void detectEditText(CharSequence charSequence) {
        if (isEditTextTitleChanged(charSequence))
            editTextTitleOnTextChanged(charSequence);
        else editTextDescriptionOnTextChanged(charSequence);
    }

    private void editTextTitleOnTextChanged(@NotNull CharSequence charSequence) {
        title = charSequence.toString();
        canReview = areReviewFieldsCorrectlyFilled();
    }

    private void editTextDescriptionOnTextChanged(@NotNull CharSequence charSequence) {
        description = charSequence.toString();
        canReview = areReviewFieldsCorrectlyFilled();
    }

    public void insertReviewBasedOnAccomodationType() {
        if (canReview) {
            showWaitWhileInsertingReviewDialog();
            insertAttractionReview();
        } else
            showToastOnUiThred(R.string.fill_required_fields_error);
    }

    private void setReviewInformation() {
        title = getTextInputLayoutReviewTitleValue();
        description = getTextInputLayoutReviewDescriptionValue();
        rating = getRatingBarValue();
        accomodationId = getAccomodationId();
    }

    @NotNull
    private Review createReviewForInserting() {
        setReviewInformation();
        review = new Review();
        review.setTitle(title);
        review.setDescription(description);
        review.setRating(rating);
        review.setAccomodationId(accomodationId);
        return review;
    }

    private void showWaitWhileInsertingReviewDialog() {
        AlertDialog.Builder alertDialogBuilder = createAlertDialogBuilder();
        LayoutInflater layoutInflater = getLayoutInflater();
        View dialogView = layoutInflater.inflate(getAlertDialogLayout(), null);
        alertDialogBuilder.setView(dialogView);
        alertDialogWaitWhileInsertingReview = alertDialogBuilder.create();
        alertDialogWaitWhileInsertingReview.show();
    }

    @NotNull
    @Contract(" -> new")
    private AlertDialog.Builder createAlertDialogBuilder() {
        return new AlertDialog.Builder(writeReviewActivity);
    }

    private void dismissWaitWhileInsertingReviewDialog() {
        alertDialogWaitWhileInsertingReview.dismiss();
    }

    public void checkLogin() {
        if (!isLogged()) {
            startLoginActivity();
            finish(Activity.RESULT_CANCELED);
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

    private void finish(int result) {
        writeReviewActivity.setResult(result, new Intent());
        writeReviewActivity.finish();
    }

    public void setListenersOnViewComponents() {
        getRatingBar().setOnRatingBarChangeListener(this);
        getTextInputLayoutReviewTitleEditText().addTextChangedListener(this);
        getTextInputLayoutReviewDescriptionEditText().addTextChangedListener(this);
    }

    public void setToolbarSubtitle() {
        getSupportActionBar().setSubtitle(getAccomodationName());
    }

    private ActionBar getSupportActionBar() {
        return writeReviewActivity.getSupportActionBar();
    }

    private ReviewDAO getReviewDAO() {
        reviewDAO = daoFactory.getReviewDAO(getStorageTechnology(Constants.getRestaurantStorageTechnology()));
        return reviewDAO;
    }

    private String getStorageTechnology(String storageTechnology) {
        return ConfigFileReader.getProperty(storageTechnology, getContext());
    }

    private Intent getIntent() {
        return writeReviewActivity.getIntent();
    }

    private String getAccomodationId() {
        return getIntent().getStringExtra(Constants.getId());
    }

    private String getAccomodationName() {
        return getIntent().getStringExtra(Constants.getAccomodationName());
    }

    private String getUserEmail() {
        return userSharedPreferences.getStringSharedPreferences(Constants.getEmail());
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
    private UserSharedPreferences createUserSharedPreferences() {
        return new UserSharedPreferences(getContext());
    }

    private String getAccessToken() {
        return userSharedPreferences.getStringSharedPreferences(Constants.getAccessToken());
    }

    @NotNull
    private LayoutInflater getLayoutInflater() {
        return writeReviewActivity.getLayoutInflater();
    }

    private int getAlertDialogLayout() {
        return R.layout.dialog_wait_while_insert_review_layout;
    }

    private int getAlertDialogFirstReviewLayout() {
        return R.layout.dialog_first_review_reward_layout;
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

    private RatingBar getRatingBar() {
        return writeReviewActivity.getRatingBar();
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
        return title.trim().isEmpty();
    }

    private boolean isTitleLongerThan(int numberOfCharacters) {
        return title.trim().length() > numberOfCharacters;
    }

    private boolean isDescriptionEmpty() {
        return description.trim().isEmpty();
    }

    private boolean isDescriptionLongerThan(int numberOfCharacters) {
        return description.trim().length() > numberOfCharacters;
    }

    private boolean areReviewFieldsCorrectlyFilled() {
        return !isTitleEmpty() && !isDescriptionEmpty() && !isTitleLongerThan(100) &&
                !isDescriptionLongerThan(600);
    }

}