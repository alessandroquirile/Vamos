package com.quiriletelese.troppadvisorproject.views;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.controllers.WriteReviewController;

public class WriteReviewActivity extends AppCompatActivity {

    private WriteReviewController writeReviewController;
    private EditText textInputEditTextTitle;
    private EditText textInputEditTextDescription;
    private RatingBar ratingBar;
    private TextView textViewAboutRating;
    private SwitchCompat switchCompatButton;
    private Button buttonPublishReview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_review);

        initViewComponents();
        initializeController();
        setListenerOnViewCOmponents();
    }

    public void initViewComponents() {
        textInputEditTextTitle = findViewById(R.id.text_input_edit_text_title_write_review_activity);
        textInputEditTextDescription = findViewById(R.id.text_input_edit_text_description_review_activity);
        ratingBar = findViewById(R.id.rating_bar_write_review_activity);
        textViewAboutRating = findViewById(R.id.text_view_about_rating_review_activity);
        switchCompatButton = findViewById(R.id.switch_button_review_activity);
        buttonPublishReview = findViewById(R.id.button_publish_review_review_activity);
    }

    public void initializeController() {
        writeReviewController = new WriteReviewController(this);
    }

    private void setListenerOnViewCOmponents(){
        writeReviewController.setListenersOnViewComponents();
    }

    public EditText getTextInputEditTextTitle() {
        return textInputEditTextTitle;
    }

    public EditText getTextInputEditTextDescription() {
        return textInputEditTextDescription;
    }

    public RatingBar getRatingBar() {
        return ratingBar;
    }

    public TextView getTextViewAboutRating() {
        return textViewAboutRating;
    }

    public SwitchCompat getSwitchCompatButton() {
        return switchCompatButton;
    }

    public Button getButtonPublishReview() {
        return buttonPublishReview;
    }
}
