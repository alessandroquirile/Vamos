package com.quiriletelese.troppadvisorproject.views;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputLayout;
import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.controllers.WriteReviewActivityController;

/**
 * @author Alessandro Quirile, Mauro Telese
 */

public class WriteReviewActivity extends AppCompatActivity {

    private WriteReviewActivityController writeReviewActivityController;
    private TextInputLayout textInputLayoutReviewTitle, textInputLayoutReviewDescription;
    private RatingBar ratingBar;
    private TextView textViewRating;
    private SwitchCompat switchCompatButtonPublishAnonymously;
    private Button buttonPublishReview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_review);

        Toolbar toolbar = findViewById(R.id.tool_bar_write_review);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initializeViewComponents();
        setRatingBarDefaultValue();
        initializeController();
        checkLogin();
        setToolbarSubtitle();
        setListenerOnViewComponents();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setToolbarSubtitle(){
        writeReviewActivityController.setToolbarSubtitle();
    }

    private void checkLogin(){
        writeReviewActivityController.checkLogin();
    }

    public void initializeViewComponents() {
        textInputLayoutReviewTitle = findViewById(R.id.text_input_layout_review_title);
        textInputLayoutReviewDescription = findViewById(R.id.text_input_layout_review_description);
        ratingBar = findViewById(R.id.rating_bar);
        textViewRating = findViewById(R.id.text_view_about_rating);
        switchCompatButtonPublishAnonymously = findViewById(R.id.switch_compat_button_publish_anonymously);
        buttonPublishReview = findViewById(R.id.button_publish_review);
    }

    private void setRatingBarDefaultValue(){
        ratingBar.setRating(3);
        textViewRating.setText(getResources().getString(R.string.nella_media));
    }

    public void initializeController() {
        writeReviewActivityController = new WriteReviewActivityController(this);
    }

    private void setListenerOnViewComponents(){
        writeReviewActivityController.setListenersOnViewComponents();
    }

    public EditText getTextInputLayoutReviewTitleEditText() {
        return textInputLayoutReviewTitle.getEditText();
    }

    public EditText getTextInputLayoutReviewDescriptionEditText() {
        return textInputLayoutReviewDescription.getEditText();
    }

    public String getTextInputLayoutReviewTitleValue(){
        return textInputLayoutReviewTitle.getEditText().getText().toString();
    }

    public String getTextInputLayoutReviewDescriptionValue(){
        return textInputLayoutReviewDescription.getEditText().getText().toString();
    }

    public RatingBar getRatingBar() {
        return ratingBar;
    }

    public Float getRatingBarValue() {
        return ratingBar.getRating();
    }

    public TextView getTextViewRating() {
        return textViewRating;
    }

    public SwitchCompat getSwitchCompatButtonPublishAnonymously() {
        return switchCompatButtonPublishAnonymously;
    }

    public Button getButtonPublishReview() {
        return buttonPublishReview;
    }
}
