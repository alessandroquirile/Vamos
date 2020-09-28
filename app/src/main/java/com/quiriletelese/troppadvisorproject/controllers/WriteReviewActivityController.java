package com.quiriletelese.troppadvisorproject.controllers;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RatingBar;

import androidx.appcompat.widget.SwitchCompat;

import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.dao_interfaces.ReviewDAO;
import com.quiriletelese.troppadvisorproject.factories.DAOFactory;
import com.quiriletelese.troppadvisorproject.interfaces.Constants;
import com.quiriletelese.troppadvisorproject.models.Review;
import com.quiriletelese.troppadvisorproject.utils.ConfigFileReader;
import com.quiriletelese.troppadvisorproject.utils.UserSharedPreferences;
import com.quiriletelese.troppadvisorproject.views.WriteReviewActivity;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBack;

/**
 * @author Alessandro Quirile, Mauro Telese
 */
public class WriteReviewActivityController implements View.OnClickListener, RatingBar.OnRatingBarChangeListener,
        SwitchCompat.OnCheckedChangeListener, TextWatcher, Constants {
    private DAOFactory daoFactory;
    private ReviewDAO reviewDAO;
    private WriteReviewActivity writeReviewActivity;
    private String title = "", description = "", user, accomodationId;
    private Float rating;
    private boolean isAnonymoys = false;

    public WriteReviewActivityController(WriteReviewActivity writeReviewActivity) {
        this.writeReviewActivity = writeReviewActivity;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_publish_review:
                insertReviewByAccomodationType();
                break;
        }
    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        switch ((int) rating) {
            case 0:
                writeReviewActivity.getTextViewAboutRating().setText("");
                ratingBar.setRating(1);
                break;
            case 1:
                writeReviewActivity.getTextViewAboutRating().setText(writeReviewActivity.getApplicationContext().getResources().getString(R.string.pessimo));
                break;
            case 2:
                writeReviewActivity.getTextViewAboutRating().setText(writeReviewActivity.getApplicationContext().getResources().getString(R.string.scarso));
                break;
            case 3:
                writeReviewActivity.getTextViewAboutRating().setText(writeReviewActivity.getApplicationContext().getResources().getString(R.string.nella_media));
                break;
            case 4:
                writeReviewActivity.getTextViewAboutRating().setText(writeReviewActivity.getApplicationContext().getResources().getString(R.string.molto_buono));
                break;
            case 5:
                writeReviewActivity.getTextViewAboutRating().setText(writeReviewActivity.getApplicationContext().getResources().getString(R.string.eccellente));
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

    private void detectEditText(CharSequence charSequence) {
        if (charSequence.hashCode() == getReviewTitleEditText().getText().hashCode())
            reviewEditTextTitleOnTextChanged(charSequence);
        else reviewEditTextDescriptionOnTextChanged(charSequence);
    }

    private void reviewEditTextTitleOnTextChanged(CharSequence charSequence) {
        title = charSequence.toString();
        if (reviewFieldsAreCorretclyFilled())
            enableButtonPublishReview();
        else
            disableButtonPublishReview();
    }

    private void reviewEditTextDescriptionOnTextChanged(CharSequence charSequence) {
        description = charSequence.toString();
        if (reviewFieldsAreCorretclyFilled())
            enableButtonPublishReview();
        else
            disableButtonPublishReview();
    }

    private void insertReviewByAccomodationType() {
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

    private void insertHotelReviewHelper(VolleyCallBack volleyCallBack, Review review, Context context) {
        daoFactory = DAOFactory.getInstance();
        ReviewDAO reviewDAO = daoFactory.getReviewDAO(ConfigFileReader.getProperty(REVIEW_STORAGE_TECHNOLOGY,
                writeReviewActivity.getApplicationContext()));
        reviewDAO.insertHotelReview(volleyCallBack, review, context);
    }

    private void insertHotelReview() {
        insertHotelReviewHelper(new VolleyCallBack() {
            @Override
            public void onSuccess(Object object) {

            }

            @Override
            public void onError(String errorCode) {

            }
        }, createReviewForInsert(), writeReviewActivity.getApplicationContext());
    }

    private void insertRestaurantReviewHelper(VolleyCallBack volleyCallBack, Review review, Context context) {
        daoFactory = DAOFactory.getInstance();
        ReviewDAO reviewDAO = daoFactory.getReviewDAO(ConfigFileReader.getProperty(REVIEW_STORAGE_TECHNOLOGY,
                writeReviewActivity.getApplicationContext()));
        reviewDAO.insertRestaurantReview(volleyCallBack, review, context);
    }

    private void insertRestaurantReview() {
        insertRestaurantReviewHelper(new VolleyCallBack() {
            @Override
            public void onSuccess(Object object) {

            }

            @Override
            public void onError(String errorCode) {

            }
        }, createReviewForInsert(), writeReviewActivity.getApplicationContext());
    }

    private void insertAttractionReviewHelper(VolleyCallBack volleyCallBack, Review review, Context context) {
        daoFactory = DAOFactory.getInstance();
        ReviewDAO reviewDAO = daoFactory.getReviewDAO(ConfigFileReader.getProperty(REVIEW_STORAGE_TECHNOLOGY,
                writeReviewActivity.getApplicationContext()));
        reviewDAO.insertAttractionReview(volleyCallBack, review, context);
    }

    private void insertAttractionReview() {
        insertAttractionReviewHelper(new VolleyCallBack() {
            @Override
            public void onSuccess(Object object) {

            }

            @Override
            public void onError(String errorCode) {

            }
        }, createReviewForInsert(), writeReviewActivity.getApplicationContext());
    }

    private void getReviewInformations() {
        title = writeReviewActivity.getTextInputLayoutReviewTitleValue().trim();
        description = writeReviewActivity.getTextInputLayoutReviewDescriptionValue().trim();
        rating = writeReviewActivity.getRatingBarValue();
        user = getUserInformation();
        accomodationId = getAccomodationId();
    }

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

    public void setListenersOnViewComponents() {
        writeReviewActivity.getButtonPublishReview().setOnClickListener(this);
        writeReviewActivity.getRatingBar().setOnRatingBarChangeListener(this);
        writeReviewActivity.getSwitchCompatButtonPublishAnonymously().setOnCheckedChangeListener(this);
        writeReviewActivity.getTextInputLayoutReviewTitleEditText().addTextChangedListener(this);
        writeReviewActivity.getTextInputLayoutReviewDescriptionEditText().addTextChangedListener(this);
    }

    private void enableButtonPublishReview() {
        writeReviewActivity.getButtonPublishReview().setBackgroundResource(R.drawable.background_write_review_button_enabled);
        writeReviewActivity.getButtonPublishReview().setEnabled(true);
    }

    private void disableButtonPublishReview() {
        writeReviewActivity.getButtonPublishReview().setBackgroundResource(R.drawable.background_write_review_button_disabled);
        writeReviewActivity.getButtonPublishReview().setEnabled(false);
    }

    public void setToolbarSubtitle() {
        writeReviewActivity.getSupportActionBar().setSubtitle(getAccomodationName());
    }

    private String getAccomodationId() {
        return writeReviewActivity.getIntent().getStringExtra(ID);
    }

    private String getAccomodationName() {
        return writeReviewActivity.getIntent().getStringExtra(ACCOMODATION_NAME);
    }

    private String getAccomodationType() {
        return writeReviewActivity.getIntent().getStringExtra(ACCOMODATION_TYPE);
    }

    private String getUserInformation() {
        UserSharedPreferences userSharedPreferences = new UserSharedPreferences(writeReviewActivity.getApplicationContext());
        return userSharedPreferences.getSharedPreferences().getString(USERNAME, "");
    }

    private EditText getReviewTitleEditText() {
        return writeReviewActivity.getTextInputLayoutReviewTitleEditText();
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

    private boolean reviewFieldsAreCorretclyFilled(){
        return !isTitleEmpty() && !isDescriptionEmpty() && !isTitleLongerThan50Characters() && !isDescriptionLongerThan350Characters();
    }

}